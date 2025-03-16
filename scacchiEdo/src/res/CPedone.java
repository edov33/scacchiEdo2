package res;

public class CPedone extends Piece{
    

    public CPedone(int color, int col, int row) {
        super(color, col, row);

        type = Type.PAWN;
        
        if(color == GamePanel.WHITE){
            immagine = getImage("/fotoPiece/WhitePawn");
        }else{
            immagine = getImage("/fotoPiece/BlackPawn");
        } 
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){
            //Definisco moveValue basandomi sul colore
            int moveValue;
            if(color == GamePanel.WHITE){
                moveValue = -1;
            }else{
                moveValue = 1;
            }
            
            //Controllo il Hitting piece
            hittingP = getHittingP(targetCol, targetRow);
            //non potremo usare i metodi di verifica della torre o regina perchè il pedone quando incontra una pedina davanti si blocca ( non la mangia)

            //movimento di un quadrato
            if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null){
                return true;
            }

            //movimento di due quadrati
            if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && moved == false &&
                pieceIsOnStraightLine(targetCol, targetRow) == false){

                    return true;
            }

            //Moviemti diagonali & cattura 
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null &&
                hittingP.color != color){
                    return true;
                }
        }

        return false;
    }
    
}
