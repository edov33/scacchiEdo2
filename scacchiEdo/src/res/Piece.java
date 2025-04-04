package res;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Piece {//la super-class di tutti i pezzi 
    public Type type; // con la classe enum abbaimo queste costanti per indicare il tipo della pedina

    public BufferedImage immagine;
    public int x,y;
    public int col, row, preCol, preRow;
    public int color; //int perchè rappresenta il colore bianco e nero (0 e 1)
    public Piece hittingP;

    public boolean moved; //proprietà legata principalmente per la classe pedone(per la sua proprietà di muoversi di due quadraati al primo spostamento)

    //costruttore
    //x, y li otteniamo attraverso due metodi
    public Piece(int color, int col, int row){

        this.color = color;
        this.col = col;
        this.row = row;
        x=getX(col);
        y=getY(row);
        preCol=col;
        preRow=row;
    }


    public int getX(int col){
        return col * Table.SQUARE_SIZE;
    }
    public int getY(int row){
        return row * Table.SQUARE_SIZE;
    }

    //metodo simile ai getX,Y ma per funzionalità del mouse
    public int getCol(int x){
        return (x + Table.HALF_SQUARE_SIZE)/Table.SQUARE_SIZE;
    }
    public int getRow(int y){
        return (y+ Table.HALF_SQUARE_SIZE)/Table.SQUARE_SIZE;
    }
    //HAlF_SQUARE_SIZE nei calcoli permette di dare una posizione più precisa al posizionamento della pedina
    //Di norma ci calcolerebbe la posizione con l'angolo in alto a sinistra del quadrato dell'immagine della pedina(100x100pixel)
    //Dunque rischiamo di non posizionare in modo preciso le pedine, rischiamo di metterle in altre caselle senza volerlo
    //Con HAlF_SQUARE_SIZE nei calcoli possiamo calcolare la posizione con il centro dell'immagine della pedina


    public int getIndex(){
        for(int index = 0; index < GamePanel.simPieces.size(); index++){
            if(GamePanel.simPieces.get(index) == this){
                return index;
            }
        }
        return 0;
    }

    public void updatePosition(){

        x = getX(col);//questi get premetto di aggiustare il posizionamento delle pedine quando rilasciamo l'hold
        y = getY(row);
        preCol = getCol(x);//questi get invece permetto di dare le nuove coordinate del posizionamento nuovo della pedina
        preRow = getRow(y);
        moved = true;
    }
    public void resetPosition(){
        col = preCol;//preCol e preRow indicano la posizione del pedone prima dello spostamento
        row = preRow;//così da rimettere in posizione la pedina quando la proviamo a spostare dove non si può
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow){//metodo che implementeremo in tutte le clssi delle pedine per poter limitare i movimenti legali 
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow){
        if(targetCol >= 0 && targetCol <=7 && targetRow >=0 && targetRow <=7){
            return true;
        }
        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow){
        if(targetCol == preCol && targetRow == preRow){
            return true;
        }
        return false;
    }

    public Piece getHittingP(int targetCol, int targetRow){//metodo che controlla il posizionamento delle pedine e vede se stiamo toccando un quadrato già occupato
        for(Piece p : GamePanel.simPieces){
            if(p.col == targetCol && p.row == targetRow && p != this){
                return p;
            }
        }
        return null;
    }
    public boolean isValidSquare(int targetCol, int targetRow){
        hittingP = getHittingP(targetCol, targetRow);
        if(hittingP == null){
            return true;
        }else{
            if(hittingP.color != this.color){
                
                return true;
            }else{
                hittingP = null;
            }
        }

        return false;
    }

    public BufferedImage getImage(String imagePercorso){

        BufferedImage image = null;//avere l'immagine come oggetto BufferedImage è ottimo per la sua gestione(lo gestisce come un insieme di pixel)

        try{
            image = ImageIO.read(getClass().getResourceAsStream(imagePercorso + ".png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        return image;

    }
    
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow){
        //quando la pedina si muove a sinistra
        for(int c = preCol-1; c > targetCol; c--){//preCol-1 equivale al quadrato di sinistra, parte il controllo dal quadrato a sinistra di quello della pedina
            for(Piece p : GamePanel.simPieces){
                if(p.col == c && p.row == targetRow){
                    hittingP = p;
                    return true;
                }
            }
        }

        //quando la pedina si muove a destra
        for(int c = preCol+1; c < targetCol; c++){//in pratica l'opposto al for scritto sopra
            for(Piece p : GamePanel.simPieces){
                if(p.col == c && p.row == targetRow){
                    hittingP = p;
                    return true;
                }
            }
        }

        //quando la pedina si muove verso l'alto
        for(int r = preRow-1; r > targetRow; r--){
            for(Piece p : GamePanel.simPieces){
                if(p.col == targetCol && p.row == r){
                    hittingP = p;
                    return true;
                }
            }
        }

        //quando la pedina si muove verso il basso
        for(int r = preRow+1; r < targetRow; r++){
            for(Piece p : GamePanel.simPieces){
                if(p.col == targetCol && p.row == r){
                    hittingP = p;
                    return true;
                }
            }
        }

        return false;
   }

   public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow){
        
        //dividiamo le direzioni in alto e basso
        if(targetRow < preRow){
            //Up left
            for(int c = preCol-1; c > targetCol; c--){
                int diff = Math.abs(c - preCol);
                for(Piece p : GamePanel.simPieces){
                    if(p.col == c && p.row == preRow - diff){
                        hittingP = p;
                        return true;
                    }
                }
            }

            //Up right
            for(int c = preCol+1; c < targetCol; c++){
                int diff = Math.abs(c - preCol);
                for(Piece p : GamePanel.simPieces){
                    if(p.col == c && p.row == preRow - diff){
                        hittingP = p;
                        return true;
                    }
                }
            }
        }
    
        if(targetRow > preRow){
            //Down left
            for(int c = preCol-1; c > targetCol; c--){
                int diff = Math.abs(c - preCol);
                for(Piece p : GamePanel.simPieces){
                    if(p.col == c && p.row == preRow + diff){
                        hittingP = p;
                        return true;
                    }
                }
            }

            //Down right
            for(int c = preCol+1; c < targetCol; c++){
                int diff = Math.abs(c - preCol);
                for(Piece p : GamePanel.simPieces){
                    if(p.col == c && p.row == preRow + diff){
                        hittingP = p;
                        return true;
                    }
                }
            }
        }
    
    
        return false;

   }

    public void draw(Graphics2D g2){
        g2.drawImage(immagine, x, y, Table.SQUARE_SIZE, Table.SQUARE_SIZE, null);
    }

}
