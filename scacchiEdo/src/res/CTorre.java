package res;

public class CTorre extends Piece {

    public CTorre(int color, int col, int row) {
        super(color, col, row);     
        
        type = Type.ROOK;
        if(color == GamePanel.WHITE){
            immagine = getImage("/fotoPiece/WhiteRook");
        }else{
            immagine = getImage("/fotoPiece/BlackRook");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            // la torre può andare all'infinito e andare lunga la direzione orizzontale o verticaledei quadrati
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    return true;
                }
            }
        }
        return false;
    }
    
}
