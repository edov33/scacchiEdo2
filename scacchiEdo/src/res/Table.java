package res;

import java.awt.Color;
import java.awt.Graphics2D;

public class Table {


    //La tabella che creaiamo è composta da righe e colonne, nella tabella noi comprendiamo anche le coordinate 0x, 0y
    //Dunque anche (2x, 4y) o (5x, 0y) o (0x, 1y)
    
    final int MAX_COL = 8;
    final int MAX_ROW = 8;
    public static final int SQUARE_SIZE = 100; //ogni quadrato è formato da 100x100 pixel, dunque otteniamo una tabbella da 800x800 pixel
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;

    public void draw(Graphics2D g2){// l'oggetto Graphics2D ci permetterà di disegnare la tavola di gioco

        int c=0;

        for(int row =0; row < MAX_ROW; row++){
            
            for(int col = 0; col<MAX_COL; col++){//ogni riga accediamo a ogni quadrato di ogni colonna

                if(c == 0){
                    g2.setColor(new Color(210,145,125));// i numeri rappresentano gli r.g.b. number(l'oggetto contiente il colore che verrà creato dal mix dei numeri rgb)
                    c = 1;
                }else{//entro se c=1
                    g2.setColor(new Color(175,115,70));
                    c = 0;
                }
                g2.fillRect(col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE); 
                //col*SQUARE_SIZE e row*SQUARE_SIZE indicano le coordinate del quadrato 
                //fillRect disegna il contorno del quadrato specificato (dell'oggetto g2)
            }

            if(c == 0){//qui analiziamo c dopo l'utilizzo dell'ultimo quadrato della riga(l'ordine di colori nell'if è opposto a quello dell'if precedente)
                c = 1;//nel caso l'ultimo quadrato l'avesse colorato con il secondo colore
            }else{
                c = 0;//nel caso l'ultimo quadrato l'avesse colorato con il primo colore
            }
        }
        
    }

}
