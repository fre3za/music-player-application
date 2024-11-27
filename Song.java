package musicplayer.assets;

import java.io.File;  
import org.jaudiotagger.tag.Tag;

import com.mpatric.mp3agic.Mp3File;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
//import org.junit.platform.reporting.shadow.org.opentest4j.reporting.events.core.Tags;


//import question.fibo;

//class use to describe a song 
public class Song {
    private String songTitle;
    private String songArtist;
    private String Songlength;
    private String filepath;
    private Mp3File mp3file;
    private double frameRatePerMilliseconds;



    public Song(String filepath){
        this.filepath = filepath;
        try{ 

           mp3file = new Mp3File(filepath);
           frameRatePerMilliseconds = (double) mp3file.getFrameCount()/mp3file.getLengthInMilliseconds();
           Songlength = convertToSongLengthFormat();
                //use jaudiotagger library to create an audiofile obj to read mp3 file 
                AudioFile audioFile = AudioFileIO.read(new File(filepath));

                //read through meta data of audio file 
                Tag tag =  audioFile.getTag();
            if(tag != null){
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);
            }
                else{
                    //could not read mp3 file
                    songTitle = "N/A";
                    songArtist = "N/A";
                }

            }catch(Exception e){
                e.printStackTrace();
            }
    }
   


    private String convertToSongLengthFormat(){
      long minute = mp3file.getLengthInSeconds()/60;
      long second = mp3file.getLengthInSeconds()%60;
      String formattedtime = String.format("%02d:%02d",minute,second);
      return formattedtime;
    }
    //getters
      public String getsongTitle(){
        return songTitle;
      }
      public String getsongArtist(){
        return songArtist;
      }
       public String getSonglength(){
        return Songlength;
      } 
      public String getfilepath(){
        return filepath;
      }
      public Mp3File getMp3File(){
        return mp3file;
      }
      public double getframeRatePerMillisecond(){
        return frameRatePerMilliseconds;
      }
    
}
