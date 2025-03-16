import javax.swing.JFrame;

import res.GamePanel;

public class App {
    public static void main(String[] args) throws Exception {
    
        JFrame finestra = new JFrame("Scacchi Romanisti");
        finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// questo ci permette di spegnere il programma quando chiusiamo la finestra di gioco
        //EXIT_ON_CLOSE è un must per programmare un gioco
        finestra.setResizable(false);
        finestra.setLocationRelativeTo(null);
        //con questi ultimi due comandi impostiamo con false il fatto che non possiamo ridimensionare la finestra di gioco
        //il valore null nel metodo setLocation metterà la finestra di gioco al centro
        //di default lo inserisce in alto a sinistra(se non usassimo il meto setLocation)
        
        finestra.setVisible(true);//per poter vedere la finestra di gioco
        
        //Aggiungiamo GamePanel all'oggetto finestra
        GamePanel gp = new GamePanel();
        finestra.add(gp);
        finestra.pack();
        

        gp.launchGame();//dopo aver avviato e aver creato la finestra di gioco, il metodo launchGame creerà l'oggetto Thread e chiamerà il metodo run 
        
    }
}
