package musicplayer.assets;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.junit.jupiter.params.shadow.com.univocity.parsers.common.fields.FieldSelector;

public class musicplaylistdialog  extends JDialog{
  private musicplayerGUI musicplayerGUI;
//store all of the path to be written in text file (when we load playlist)
  private ArrayList<String>songpath;
    public musicplaylistdialog(musicplayerGUI musicplayerGUI){
        this.musicplayerGUI = musicplayerGUI;
        songpath = new ArrayList<>();
        //configure dialog
        setTitle("Create Playlist");
        setSize(400,400);
        setResizable(false);
        getContentPane().setBackground(musicplayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true); //this property make it  so that the dialog has to be closed to give focus
        setLocationRelativeTo(musicplayerGUI);


        addDialogcomponents();

    }
    private void addDialogcomponents(){
        //container to hold each song path 
        JPanel songcontainer = new JPanel();
        songcontainer.setLayout(new BoxLayout(songcontainer, BoxLayout.Y_AXIS));
        songcontainer.setBounds((int)(getWidth()*0.025),10,(int)(getWidth()*0.90),(int)(getHeight()*0.75));
        add(songcontainer);

        //add song button
        JButton  addsongbutton = new JButton("Add");
        addsongbutton.setBounds(60,(int)(getHeight()*0.80),100,25);
        addsongbutton.setFont(new Font("Dialog",Font.BOLD,14));
        addsongbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //open file explorer
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
                jFileChooser.setCurrentDirectory(new File("D:\\java\\musicplayer"));
                int result = jFileChooser.showOpenDialog(musicplaylistdialog.this);


                File selectedfile = jFileChooser.getSelectedFile();
                if(result == jFileChooser.APPROVE_OPTION && selectedfile != null){
                    JLabel filepathlLabel = new JLabel(selectedfile.getPath());
                    filepathlLabel.setFont(new Font("Dialog",Font.BOLD,12));
                    filepathlLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    

                    //add to the list 
                   songpath.add(filepathlLabel.getText());

                   //add to container 
                   songcontainer.add(filepathlLabel);

                   //refreshes dialog to show newly added Jlabel
                   songcontainer.revalidate();
                }
            }
        });
        add(addsongbutton);

        //save playlist button
        JButton saveplaylistbutton = new JButton("Save");
        saveplaylistbutton.setBounds(215 , (int)(getHeight()*0.80),100,25);
        saveplaylistbutton.setFont(new Font("Dialog",Font.BOLD,14));
        saveplaylistbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
           try{
            JFileChooser jFileChooser = new JFileChooser();
           jFileChooser.setCurrentDirectory(new File("D:\\java\\musicplayer"));
           int result = jFileChooser.showSaveDialog(musicplaylistdialog.this);


           if(result == jFileChooser.APPROVE_OPTION ){
            //used getselectedfile() to get reference to the file that we are about to save
        
            File selectedFile = jFileChooser.getSelectedFile();
             
            //convert to. text file if not done so already 
            //this will check to see if the file does not have .txt file extension
            if(!selectedFile.getName().substring(selectedFile.getName().length()-4).equalsIgnoreCase(".txt")){
                selectedFile = new File(selectedFile.getAbsoluteFile()+".txt");
            }
           //create new file at the destination directory 
           selectedFile.createNewFile();

           FileWriter fileWriter = new FileWriter(selectedFile);
           BufferedWriter  bufferedWriter = new BufferedWriter(fileWriter);

           //iterate through our song path list and write each string into the file 
           // each song will be written in their own row
           for (String songpaths : songpath) {
            bufferedWriter.write(songpaths+"\n");
           }
         bufferedWriter.close();

         //display success dialog
         JOptionPane.showMessageDialog(musicplaylistdialog.this, "Sucessfully Created Playlist! ");
         //close this dialog
         musicplaylistdialog.this.dispose();
           }
           }catch(Exception exception){
            exception.printStackTrace();
           }
            }
        });
        add(saveplaylistbutton);
    }
    
}
