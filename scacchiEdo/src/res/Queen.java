package res;

public class Queen extends Piece {

    public Queen(int color, int col, int row) {
        super(color, col, row);

        type = Type.QUEEN;
        
        if(color == GamePanel.WHITE){
            immagine = getImage("/fotoPiece/WhiteQueen");
        }else{
            immagine = getImage("/fotoPiece/BlackQueen");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){

            //una fusione tra la torre e Bishop
            //Verticale e orizzonatale
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow) == false){
                    return true;
                }
            }

            //Diagonale
            if(Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLine(targetCol, targetRow) == false){
                    return true;
                }
            }
        }
        return false;
    }
    
}
