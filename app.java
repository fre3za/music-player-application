package musicplayer.assets;

import javax.swing.SwingUtilities;

public class app {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
              new musicplayerGUI().setVisible(true);


            //    Song song = new Song("D:\\java\\musicplayer\\Pehle Bhi Main - Animal 320 Kbps.mp3");
            //   System.out.println(song.getsongTitle());
            //   System.out.println(song.getsongArtist());
            }
        });
    }
    
}
