package musicplayer.assets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.Buffer;
import java.security.PrivateKey;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;




public class musicplayerGUI extends JFrame{
   //colour confriguration
   public static final Color FRAME_COLOR = Color.black;
   public static final Color TEXT_COLOR = Color.white;


    private  musicplayer musicplayer;


    //allow us to use file explorer in app
    private JFileChooser jFileChooser;



    //for global songtitle and artist
    private JLabel songtitle ,songartist;

    //global jpanle
    private JPanel playbackBtns;
    //global jsilder
    private JSlider playbackslider;




    public musicplayerGUI(){
        super("Music Player");


        // set the width and height
        setSize( 400, 600);
         

        // end process when app is close 
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        // launch the app at center of screen 
        setLocationRelativeTo(null);
        //prevent the app to resize
        setResizable(false);

        // set layout to null which allow us to control (x,y) coordinates
        setLayout(null);


        // color to frame colour 
        getContentPane().setBackground(FRAME_COLOR);
         musicplayer = new musicplayer(this);
        jFileChooser = new JFileChooser();
        //set a default path for file explorer
        jFileChooser.setCurrentDirectory(new File("D:\\java\\musicplayer"));
          

        //filter file chooser only to mp3
            jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));

        addGuicomponents();
    }
    private void addGuicomponents(){
        // add toolbar
        addtoolbar();
        

        //load record image
        JLabel songImage = new JLabel(loadimage("musicplayer\\assets\\drive-download-20241021T082628Z-001\\final.jpg"));
        songImage.setBounds(0,50,getWidth()-20,225);
        add(songImage);

        // show title
         songtitle = new JLabel("Song title");
        songtitle.setBounds(0,285,getWidth()-10,30);
        songtitle.setFont(new Font("Dialog",Font.BOLD,24));
        songtitle.setForeground(TEXT_COLOR);
        songtitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songtitle);

        //song artist
         songartist = new JLabel("Artist");
        songartist.setBounds(0,315,getWidth()-10,30);
        songartist.setFont(new Font("Dialog",Font.PLAIN,24));
        songartist.setForeground(TEXT_COLOR);
        songartist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songartist);

        //playback slider 
      playbackslider = new JSlider(JSlider.HORIZONTAL,0,100,0);
        playbackslider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackslider.setBackground(null);
        playbackslider.addMouseListener(new MouseAdapter() {

         @Override
         public void mousePressed(MouseEvent e) {
            // when the user is holding the tick we want to pause the song
            musicplayer.pausesong(); 
       
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            // when the user drop the tick 
            JSlider source =  (JSlider) e.getSource();
            //get the frame value from where the user  want to playback to 
            int frame = source.getValue();
            //update the current frame in the musicplayer to this frame
            musicplayer.setCurrentFrame(frame);

            //update current time in milli as well 
            musicplayer.setCurrenttimeinMilli((int)(frame / (2.08 * musicplayer.getcurrentsong().getframeRatePerMillisecond())));
            //resume song 
            musicplayer.playcurrentsong();
            //toggle on pause button and toggle off on play button 
            enablepausebuttondisableplaybutton();
         }
         
        });
        add(playbackslider);

        //playback buttom (prev ,play ,next)
        addPlaybackBtns();
    }




    private void addtoolbar(){

        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);
        //prevent from moving 
        toolBar.setFloatable(false);

        // menubar 
        JMenuBar menubar = new JMenuBar();
        toolBar.add(menubar);


        // add new song menu where we play loading song
        JMenu songMenu = new JMenu("Songs");
        menubar.add(songMenu);
             // load songs
             JMenuItem loadsong = new JMenuItem("Load songs");
           loadsong.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e)
            {
                //an interger is return to check wheather user selected song or not
               int result= jFileChooser.showOpenDialog(musicplayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();
                 //this method is to check if user pressed open button or not..
                if(result== JFileChooser.APPROVE_OPTION && selectedFile != null){

                    //create song obj based on selected file 
                    Song song = new Song(selectedFile.getPath());

                    //load song 
                    musicplayer.loadsong(song);

                    //update song title and artist 
                    updatesongtitleandartist(song);

                    //update playback slider
                    updateplaybackslider(song);

                    //toggle on pasue button and toggle off on play button 
                     enablepausebuttondisableplaybutton();
                                         
                }
             }
           });


             songMenu.add(loadsong);
             //playlist option
             JMenu playlistmMenu = new JMenu("Playlist");
             menubar.add(playlistmMenu);
             //add item to playlist 
             JMenuItem creatplaylist = new JMenuItem("Create Playlist");
             creatplaylist.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e){
                  //load music playlist dialog
                  new musicplaylistdialog(musicplayerGUI.this).setVisible(true);
               }
             });
             playlistmMenu.add(creatplaylist);
             //add item loadplaylist
             JMenuItem loadplaylist = new JMenuItem("Load Playlist");
             loadplaylist.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e){
                  JFileChooser jFileChooser = new JFileChooser();
                  jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt "));
                  jFileChooser.setCurrentDirectory(new File("D:\\java\\musicplayer"));

                  int result = jFileChooser.showOpenDialog(musicplayerGUI.this);
                  File selectedfile = jFileChooser.getSelectedFile();

                  if(result==jFileChooser.APPROVE_OPTION && selectedfile != null){
                     //stop music 
                     musicplayer.stopsong();

                     //load playlist 
                     musicplayer.loadplaylist(selectedfile);

                  }

               }
             });
             playlistmMenu.add(loadplaylist);






        add(toolBar);
    }
     
    private void addPlaybackBtns(){
         playbackBtns = new JPanel();
        playbackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playbackBtns.setBackground(null);
        add(playbackBtns);
       

         // previous button
         JButton prevButton = new JButton(loadimage("musicplayer\\assets\\drive-download-20241021T082628Z-001\\previous.png"));
         prevButton.setBorderPainted(false);
         prevButton.setBackground(null);
         prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
               // go to previous song 
               musicplayer.prevsong();
            }
         });
         playbackBtns.add(prevButton);
         

         //play button
         JButton play = new JButton(loadimage("musicplayer\\assets\\drive-download-20241021T082628Z-001\\play.png"));
         play.setBorderPainted(false);
         play.setBackground(null);
         play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //toggle off play button and toggle on pause button
                enablepausebuttondisableplaybutton();

                //play or resume song
                musicplayer.playcurrentsong();
            }
         });
         playbackBtns.add(play);


         //pause button
         JButton pause = new JButton(loadimage("musicplayer\\assets\\drive-download-20241021T082628Z-001\\pause.png"));
         pause.setBorderPainted(false);
         pause.setBackground(null);
         pause.setVisible(false);
         pause.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
             //toggle off pause button and toggle on play button 
             enableplaybuttondisablepausebutton();

             //pause the song 
             musicplayer.pausesong();
            }
         });
         playbackBtns.add(pause);


         //next button
         JButton nextButton = new JButton(loadimage("musicplayer\\assets\\drive-download-20241021T082628Z-001\\n" + //
                          "ext.png"));
         nextButton.setBorderPainted(false);
         nextButton.setBackground(null);  
         nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
               // go to next song 
               musicplayer.nextsong();
            }
         });
         playbackBtns.add(nextButton);
    }
    //this will  be used to upadte silder from music player 
    public void setPlaybacksildervalue(int fames){
      playbackslider.setValue(fames);
    }

  

     public void updatesongtitleandartist(Song song){
        songtitle.setText(song.getsongTitle());
        songartist.setText(song.getsongArtist());
     }

     public void updateplaybackslider(Song song){
      //update max count for silder 

      playbackslider.setMaximum(song.getMp3File().getFrameCount());
      //create the song length label 
      Hashtable<Integer,JLabel>labeltable = new Hashtable<>();
      //beggning 00:00
      JLabel labelbeginning= new JLabel("00:00");
      labelbeginning.setFont(new Font("Dialog",Font.BOLD,18));
      labelbeginning.setForeground(TEXT_COLOR);

      //end vary on song
      JLabel endLabel = new JLabel(song.getSonglength());
      endLabel.setFont(new Font("Dialog",Font.BOLD,18));
      endLabel.setForeground(TEXT_COLOR);

      labeltable.put(0, labelbeginning);
      labeltable.put(song.getMp3File().getFrameCount(), endLabel);

      playbackslider.setLabelTable(labeltable);
      playbackslider.setPaintLabels(true);

     }

     public void enablepausebuttondisableplaybutton(){
        //retrieve reference to play button from playbackbtns panel 
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        //turn off play button 
        playButton.setVisible(false);
        playButton.setEnabled(false);


        //turn on pause button
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);

     }
       public void enableplaybuttondisablepausebutton(){
        //retrieve reference to play button from playbackbtns panel 
        JButton playButton = (JButton) playbackBtns.getComponent(1);
        JButton pauseButton = (JButton) playbackBtns.getComponent(2);

        //turn on play button 
        playButton.setVisible(true);
        playButton.setEnabled(true);


        //turn off pause button
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
         
     }


    private ImageIcon loadimage(String imagepath){
       try{
        //read the image from given path 
        BufferedImage image = ImageIO.read(new File(imagepath));
        return new ImageIcon(image); 
       }catch (Exception e){
        e.printStackTrace(); 
       }

       // could not find resource 
       return null;
    }
}
