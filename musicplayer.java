package musicplayer.assets;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.Buffer;
import java.util.ArrayList;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
//import practice.primitive;

public class musicplayer extends PlaybackListener {
        //this will use to update ispause more synchronously 
        private static final Object playSignal = new Object();   

        //need refference so that can update the gui in this class
        private musicplayerGUI musicplayerGUI;
        //to store our song details
        private Song currentsong;
        public Song getcurrentsong(){
                return currentsong;
        }
        private ArrayList<Song>playlist;

        // we need to keep track the index we are int the playlist
        private int currentplaylistindex;

       //use Jlayer library to  create an advancedplayer obj which will handel playing the music
       private AdvancedPlayer advancedPlayer;
        
       //pause boolean flag used to indicate wheatehr the player has been paused
       private boolean ispaused;
       //boolen falg used to tell when th song has been finished
       private boolean songfinished;
         
       private boolean pressnext, pressprev;

       //this store the last fames when the playback is finished(use for pausing or resuming)
       private int currentframes;
       public void setCurrentFrame(int frames){
        currentframes = frames;  
       }


       //track how many millisecond has passed since playing the song (use to update silder)\
       private int currenttimeinmilli;
       public void setCurrenttimeinMilli(int timeinMilli){
        currenttimeinmilli = timeinMilli;
       }
        //constructor
        public musicplayer(musicplayerGUI musicplayerGUI){
                this.musicplayerGUI = musicplayerGUI;

        }


        public void loadsong(Song song){
                currentsong = song;
                playlist = null;
        // stop the song if possible
        if(!songfinished){
                stopsong();
        }

                //play the  current song if not null 
                if(currentsong != null){
                        //reset the frame
                        currentframes =0;
                        // reset current time in milli 
                        currenttimeinmilli=0;
                        //update gui 
                        musicplayerGUI.setPlaybacksildervalue(0);
                        playcurrentsong();
                }

        }
        public void loadplaylist(File playlistfile){
            playlist = new ArrayList<>();
             //store the path from the text file into playlist array list 
             try{
                FileReader fileReader = new FileReader(playlistfile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
     
                //read each line from the text file  and store the text into th songpath variable 

                String songpath;
                while((songpath = bufferedReader.readLine())!= null){
            //create song object based  on song path 
            Song song = new Song(songpath);

            // add playlist array 
            playlist.add(song);
                }
               

             }catch(Exception e){
                e.printStackTrace();
             }

             if(playlist.size()>0){
                //reset playback slider 
                musicplayerGUI.setPlaybacksildervalue(0);
                 currenttimeinmilli =0;
                //update current song to the first song in the playlist 

                currentsong = playlist.get(0);

                //start from beginning of frame 
                currentframes =0;

                //update gui 
                musicplayerGUI.enablepausebuttondisableplaybutton();
                musicplayerGUI.updatesongtitleandartist(currentsong);
                musicplayerGUI.updateplaybackslider(currentsong);

                //start song 
                playcurrentsong();

             }
        }

        public void pausesong(){
          if(advancedPlayer != null){
                //update ispaused flag
                ispaused = true;
                //then to stop player 
                  stopsong();
          }
        }
        public void stopsong(){
                if(advancedPlayer!= null){
                        advancedPlayer.stop();
                        advancedPlayer.close();
                        advancedPlayer = null;
                }
        }
        public void nextsong(){
                // no need to go to next song if there is no playlist
                if(playlist == null){
                        return;
                }
                // check to see if we have reached the end of the playlist if so then dont do anything 
                if(currentplaylistindex +1 >playlist.size()-1){
                        return;
                }
                pressnext = true;
                // stop the song if possible 
                if(!songfinished)
                stopsong();
                // increase current playlist index
                currentplaylistindex++;
                //update current song
                currentsong = playlist.get(currentplaylistindex);
                //reset frames
                currentframes =0;
               // reset current time in milli 
                  currenttimeinmilli=0;
                  // update gui
                  musicplayerGUI.enablepausebuttondisableplaybutton();
                  musicplayerGUI.updatesongtitleandartist(currentsong);
                  musicplayerGUI.updateplaybackslider(currentsong);

                  //play song 
                  playcurrentsong();
        }
        public void prevsong(){
                 // no need to go to next song if there is no playlist
                 if(playlist == null){
                        return;
                } 
                // check to see if we have reached the end of the playlist if so then dont do anything 
                if(currentplaylistindex -1 <0 ){
                        return;
                }
                pressprev = true;

                // stop the song if possible 
                if(!songfinished)
                stopsong();
                // decrease current playlist index
                currentplaylistindex--;
                //update current song
                currentsong = playlist.get(currentplaylistindex);
                //reset frames
                currentframes =0;
               // reset current time in milli 
                  currenttimeinmilli=0;
                  // update gui
                  musicplayerGUI.enablepausebuttondisableplaybutton();
                  musicplayerGUI.updatesongtitleandartist(currentsong);
                  musicplayerGUI.updateplaybackslider(currentsong);

                  //play song 
                  playcurrentsong();

        }


        public void playcurrentsong(){
                if(currentsong == null){
                        return;
                }
                 try{
                        //read mp3 audio 
                  FileInputStream fileInputStream = new FileInputStream(currentsong.getfilepath());
                  BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);


                  //create a new advance player 
                   advancedPlayer = new AdvancedPlayer(bufferedInputStream);
                   advancedPlayer.setPlayBackListener(this);


                   //start 
                   startmusicthread();

                   //start playback slider thread 
                   startplaybacksilderthread();
                 }catch(Exception e){
                        e.printStackTrace();

                 }
        }
        //create a thread that handel music playing 
        private void startmusicthread(){
       new Thread(new Runnable() {
           public void run(){
                try{

                        if(ispaused){
                                synchronized(playSignal){
                                        //update flag
                                        ispaused = false;
                                        //notify the other thread to continue (to make sure ispaused is updated false properly)
                                        playSignal.notify();
                                }
                         //resume song from last frame
                         advancedPlayer.play(currentframes,Integer.MAX_VALUE);
 

                        }else{
                                //play music 
                           advancedPlayer.play();
                        }

                }catch(Exception e){
                  e.printStackTrace();
                }
           }
       }).start();
        }
        //create a thread will update the silder 
        private void startplaybacksilderthread(){
                new Thread(new Runnable() {
                        public void run(){

                                if(ispaused){
                                        try{
                                                // wait till it gets notified by other thread to continue
                                                //make sure ispaused boolean is false
                                                synchronized(playSignal){
                                                        playSignal.wait();
                                                }
                                        }catch(Exception e){
                                                e.printStackTrace(); 
                                        }
                                }
                         while(!ispaused && !songfinished && !pressnext && !pressprev){
                        
                         try{
                                 //increment  current  time  milli 
                                currenttimeinmilli++;

                                //calculate into frame value 
                                int calculateframe = (int)((double)currenttimeinmilli*2.08*currentsong.getframeRatePerMillisecond());
      
                                //update gui
      
                                musicplayerGUI.setPlaybacksildervalue(calculateframe);
      
                                //mimic 1 millisecond using thred.sleep
                                Thread.sleep(1);
                         }catch(Exception e){
                                e.printStackTrace();
                         }
                         }
                        }
                }).start();
        }
        @Override
        public void playbackStarted(PlaybackEvent evt) {
                // this method get called in the begging of song 
                System.out.println("Playback  Started");
                songfinished= false;
                pressnext=false;
                pressprev = false;
        }
        
@Override
        public void playbackFinished(PlaybackEvent evt) {
                // this method get called when finishing of song or player get close 
                
                System.out.println("Playback Finished");
        if(ispaused){
                currentframes += (int)((double)evt.getFrame()*currentsong.getframeRatePerMillisecond());
         
        }else{
                //  if the user pressed next or prev we dont need to execute the rest of code
                if(pressnext || pressprev ){
                        return;
                }
                //when the song end 
               songfinished =true;
                if(playlist == null ){
                        //update gui 
                        musicplayerGUI.enableplaybuttondisablepausebutton();
                }else{

                        // last song in th playlist 
                        if(currentplaylistindex == playlist.size()-1){
                                // update gui 
                                musicplayerGUI.enableplaybuttondisablepausebutton();
                        
                        }else{
                                // go to next song in playlist 
                                nextsong();
                        }

                }
        }


}



      
}
