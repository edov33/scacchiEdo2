package res;

public class King extends Piece{

    public King(int color, int col, int row) {
        super(color, col, row);

        type = Type.KING;
        
        if(color == GamePanel.WHITE){
            immagine = getImage("/fotoPiece/WhiteKing");
        }else{
            immagine = getImage("/fotoPiece/BlackKing");
        }
    }
    
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow)){

            if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 || 
              (Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow)) == 1){//abs indica absolute number
                                                                                   //con questa condizione copriamo i moviemnti del re
                if(isValidSquare(targetCol, targetRow)){
                    return true;  
                }
            }

            //arrocco
           if(moved == false){

            //arrcco a destra
            if(targetCol == preCol+2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false){
                for(Piece p : GamePanel.simPieces){
                    if(p.col == preCol+3 && p.row == preRow && p.moved == false){
                        GamePanel.arroccoPiece = p;
                        return true;
                    }
                }
            }

            //arrocco a sinistra
            if(targetCol == preCol-2 && targetRow == preRow && pieceIsOnStraightLine(targetCol, targetRow) == false){
                Piece p[] = new Piece[2];
                for(Piece piece : GamePanel.simPieces){
                    if(piece.col == preCol-3 && piece.row == targetRow){
                        p[0] = piece;
                    }
                    if(piece.col == preCol-4 && piece.row == targetRow){
                        p[1] = piece;
                    }

                    if(p[0] == null && p[1] != null && p[1].moved == false){
                        GamePanel.arroccoPiece = p[1];
                        return true;
                    }

                }
            }
           } 
        }
        return false;
    }
}
