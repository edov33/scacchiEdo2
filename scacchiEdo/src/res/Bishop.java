package res;

public class Bishop extends Piece {

    public Bishop(int color, int col, int row) {
        super(color, col, row);

        type = Type.BISHOP;
        
        if(color == GamePanel.WHITE){
            immagine = getImage("/fotoPiece/WhiteBishop");
        }else{
            immagine = getImage("/fotoPiece/BlackBishop");
        }
        
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)){ // il ratio deve essere sempre 1, si muove sull'ipotenusa di un triangolo
                if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false){
                    return true;
                }
            }
        }
        return false;
    }
    
}
