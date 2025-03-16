package res;

public class CKnight extends Piece{

    public CKnight(int color, int col, int row) {
        super(color, col, row);

        type = Type.KNIGHT;
        
        if(color == GamePanel.WHITE){
            immagine = getImage("/fotoPiece/WhiteKnight");
        }else{
            immagine = getImage("/fotoPiece/BlackKnight");
        }

    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow)){
            //il ccavallo può muoversi se il suo movimento ratio di Col e Row è 1:2 o 2:1
            if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2){
                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }
        
        }
        return false;
    }
    
}
