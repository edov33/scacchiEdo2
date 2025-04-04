package res;



import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

//In questa classe gestiamo il gioco visivo, abbiamo JFrame che rappresenta la finestra di gioco
//i vari metodi gestiscono funzioni per la visualizzazione del gioco, ogni metodo ricava dati e gestisce i dati per il gioco


public class GamePanel extends JPanel implements Runnable{
    
    //useremo questa classe per personalizzare Jpanel

    //grandezza x y della finestra
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread; // oggetto della classe Thread che permette di avviare un game loop

    Table table = new Table(); //oggetto per diegnare la grafica della tabella di gioco
    Mouse mouse = new Mouse();

    //PEZZI
    public static ArrayList<Piece> pieces = new ArrayList<>();//usiamo pieces come una backupList
    public static ArrayList<Piece> simPieces = new ArrayList<>();//useremo più questo ArrayList
    //questi due metodi rappresentano la stessa cosa, contengono la posizione corrente dei pezzi nel tabellone
    Piece activeP;
    public static Piece arroccoPiece;
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece checkingP; 

    //COLORI DEI PEZZI
    public static final int WHITE = 0;
    public static final int Black = 1;
    int currentColor = WHITE;//Questa variabile indicherà quale colore comincerà il gioco


    //BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;


    public GamePanel(){
        setPreferredSize((new Dimension(WIDTH, HEIGHT)));
        setBackground((Color.black));
        addMouseMotionListener(mouse);//grazie a questi due metodi per il mouse, il programma potrà rilevare il mouse dell'utente
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void setPieces(){
        //squadra bianca
        pieces.add(new CPedone(WHITE,0,6));
        pieces.add(new CPedone(WHITE,1,6));
        pieces.add(new CPedone(WHITE,2,6));
        pieces.add(new CPedone(WHITE,3,6));
        pieces.add(new CPedone(WHITE,4,6));
        pieces.add(new CPedone(WHITE,5,6));
        pieces.add(new CPedone(WHITE,6,6));
        pieces.add(new CPedone(WHITE,7,6));
        pieces.add(new CTorre(WHITE,0,7));
        pieces.add(new CTorre(WHITE,7,7));
        pieces.add(new CKnight(WHITE,1,5));
        pieces.add(new CKnight(WHITE,6,5));
        pieces.add(new Bishop(WHITE,2,5));
        pieces.add(new Bishop(WHITE,5,5));
        pieces.add(new Queen(WHITE,3,5));
        pieces.add(new King(WHITE,4,7));

         //squadra nera
         pieces.add(new CPedone(Black,0,1));
         pieces.add(new CPedone(Black,1,1));
         pieces.add(new CPedone(Black,2,1));
         pieces.add(new CPedone(Black,3,1));
         pieces.add(new CPedone(Black,4,1));
         pieces.add(new CPedone(Black,5,1));
         pieces.add(new CPedone(Black,6,1));
         pieces.add(new CPedone(Black,7,1));
         pieces.add(new CTorre(Black,0,0));
         pieces.add(new CTorre(Black,7,0));
         pieces.add(new CKnight(Black,1,0));
         pieces.add(new CKnight(Black,6,0));
         pieces.add(new Bishop(Black,2,0));
         pieces.add(new Bishop(Black,5,0));
         pieces.add(new Queen(Black,3,0));
         pieces.add(new King(Black,4,0));
    }  

    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }

    //metodo legato all'oggetto gameThread
    public void launchGame(){
        System.out.println("Avvio del gioco...");
        gameThread = new Thread(this);
        gameThread.start();//il metodo start chiama il metodo sotto(il run method implementato)
        System.out.println("Avvio del gioco...");
    }

    @Override //metodo sovrascritto dalla classe Runnable
    //in questo metodo creiamo il game loop
    public void run() {
        //un GAME LOOP  è una sequenza di processi che girano in continuo finchè il gioco sta "running"
        
        //GAME LOOP 
        double drawInterval = 100000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();

            delta+= (currentTime-lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta>=1){
                update();
                repaint();//i metodi update e repaint li usiamo ogni 1/60 di un secondo
                delta--;
            }
        }
        //usiamo System.nanoTime() per misurare il "elapsed"(trascorso del tempo)
        //di base esistono diversi GAME LOOP, per questo programma possiamo scrivere diciamo tutti i GAME LOOT che esistono
    }

    private void update(){

        if(promotion){
            promoting();
        }else if(gameover == false && stalemate == false){
            if(mouse.pressed){
                if(activeP == null){//uguale a null vuol dire che il mouse non sta holdando un pedina di scacchi

                    for(Piece piece : simPieces){
                        if(piece.color == currentColor &&
                            piece.col == mouse.x/Table.SQUARE_SIZE &&    //questa condizione vede se quando clichiamo il mouse, se la sua posizione coincide 
                            piece.row == mouse.y/Table.SQUARE_SIZE){     //coincide con la posizione (x,y) di una pedina da gioco

                                activeP = piece;
                        }
                    }
                }else{//questa condizione indica che l'utente sta gia holdando una pedina
                    simulate(); // se stiamo holdando una pedina, il metodo simula il movimento 
                }//non creiamo qui il metodo di spostamento effettivo della pedina, perchè qua stiamo ancora nella "Thinking phase" dell'utente(tipico dei giochi strategici)
            }
            //condizione se il mouse rilascia la pedina
            if(mouse.pressed == false){
                if(activeP !=null){
                
                    if(validSquare){

                        //Update la lista di pedina nel caso fosse catturata una pedina, della thinking phase 
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition();//quando rilasciamo la pedina questo metodo verrà chiamto 
                        if(arroccoPiece != null){
                            arroccoPiece.updatePosition();
                        }

                        if(isKingInCheck() && isCheckMate()){
                            gameover = true;
                        }else if(isStalemate() && isKingInCheck() == false){
                            stalemate = true;
                        }else{//se entriamo nell'else il gioco sta ancora girando 
                            if(canPromote()){
                                promotion = true;
                            }else{
                                changePlayer();//dopo aver cambiato la posizione della pedina, passeremo il turno all'avversario
                            }
                        }
                    }else{
                        //la mossa non è valida quindi  ressetto tutto
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;//dopo aver aggiornato le posizioni, impostiamo la pedina da interazione a null
                    }
                }
            }
        }

        
    }
    private void simulate(){

        canMove = false;
        validSquare = false;
        copyPieces(pieces, simPieces);

        //resetto  la posizione dell'arrocco piece 
        if(arroccoPiece != null){
            arroccoPiece.col = arroccoPiece.preCol;
            arroccoPiece.x = arroccoPiece.getX(arroccoPiece.col);
            arroccoPiece = null;
        }
        
        //Se la pedina è holdata, aggiorna la sua posizione
        activeP.x = mouse.x-Table.HALF_SQUARE_SIZE;
        activeP.y = mouse.y-Table.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        //controllo di movimento
        if(activeP.canMove(activeP.col, activeP.row)){//se il metodo canMove restituisce true allora aggiorno le proprietà
            canMove = true;

            //se hittingP è diverso da null allora rimuovo ia pedina hittingP
            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkArrocco();

            if(isIllegal(activeP) == false && opponentCanCaptureKing() == false){
                validSquare = true;
            }
        }

    }
    //---------------------------------------------------------------

    private boolean isIllegal(Piece king){//metodo che associamo solo alla pedina del re 
        if(king.type == Type.KING){
            for(Piece p : simPieces){
                if(p != king && p.color != king.color && p.canMove(king.col, king.row)){// vedo se  ci stanno pedine diverse dal re e dal colore del mio re
                    return true;                                                        // e vedo se una delle pedine ancora in gioco possa muoversi nelle coordinate del re e catturarlo
                }
            }
        }
        return false;
    }//----------------------------------------------------------------
    private boolean opponentCanCaptureKing(){//la condizione è simili al metodo di sopra

        //metodo per controllare l'illegal move di poter muovere una pedina quando abbiamo il re in check
        Piece king = getKing(false);
        for(Piece p : simPieces){
            if(p.color != king.color && p.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
    }//----------------------------------------------------------------
    private boolean isKingInCheck(){

        Piece king = getKing(true);
 
        if(activeP.canMove(king.col, king.row)){
            checkingP = activeP;
            return true;
        }else{
            checkingP = null;
        }
        
        return false;
    }//---------------------------------------------------------------
    private Piece getKing(boolean opponent){

        Piece king = null;

        for(Piece p : simPieces){
            if(opponent){
                if(p.type == Type.KING && p.color != currentColor){
                    king = p;
                }
            }else{
                if(p.type == Type.KING && p.color == currentColor){
                    king = p;
                }
            }
        }
        return king;
    }
    
    //---------------------------------------------------------------------
    public void checkArrocco(){
        if(arroccoPiece != null){
            if(arroccoPiece.col == 0){// controllo la torre di sinisrta
                arroccoPiece.col += 3;
            }else if(arroccoPiece.col == 7){// controllo la torre di destra
                arroccoPiece.col -= 2;
            }
            arroccoPiece.x = arroccoPiece.getX(arroccoPiece.col);
        }
    }

    private void changePlayer(){
        if(currentColor == WHITE){
            currentColor = Black;
        }else{
            currentColor = WHITE;
        }
        activeP = null;
    }
    private boolean canPromote(){
        
        if(activeP.type == Type.PAWN){
            if(currentColor == WHITE && activeP.row == 0 || currentColor == Black && activeP.row == 7){//indichiamo le riche opposte ai pedoni dei due colori
                promoPieces.clear();
                promoPieces.add(new CTorre(currentColor, 9, 2));//queste pedine verranno mostrate a destra 
                promoPieces.add(new CKnight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        return false;
    }

    private void promoting(){
        if(mouse.pressed){
            for(Piece p: promoPieces){
                if(p.col == mouse.x/Table.SQUARE_SIZE && p.row == mouse.y/Table.SQUARE_SIZE){//per prumovere indichiamo con il mouse (vedendo se coincidono x,y)
                    switch(p.type){
                        case ROOK : simPieces.add(new CTorre(currentColor, activeP.col, activeP.row)); break;
                        case KNIGHT : simPieces.add(new CKnight(currentColor, activeP.col, activeP.row)); break;
                        case BISHOP : simPieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
                        case QUEEN : simPieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
                        default: break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    private boolean isStalemate(){
        int count = 0;

        //conto il numero di pedine
        for(Piece p : simPieces){
            if(p.color != currentColor){
                count++;
            }
        }

        //Se il numero di pedine del colore opposto è 1(cioè c'è solo il re)
        if(count==1){
            if(kingCanMove(getKing(true)) == false){
                return true;
            }
        }
        
        return false;

    }

    //-----------------------------------------------------------

    //METODI PER IL CHECKMATE
    //esistono tre condizioni per determinare il checkmate, e le tratteremo con questi tre metodi
    private boolean isCheckMate(){

        Piece king = getKing(true);

        if(kingCanMove(king)){
            return false;
        }else{
            //Pure se il re non si può muovere, vediamo se possiamo usare qualche pedina nostra per difenderci
            
            //controllo la posizione del pezzo che ha dato lo scacco e il pezzo del re in scacco
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);
            
            if(colDiff == 0){//Controllo se è un attacco verticale
                
                if(checkingP.row < king.row){//da sopra
                    for(int row = checkingP.row ; row < king.row; row++){//controlliamo dall'alto verso il basso se ci stanno pezzi che posso bloccare il check
                        for(Piece p : simPieces){
                            if(p != king && p.color != currentColor && p.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){//da sotto
                    for(int row = checkingP.row ; row > king.row; row--){
                        for(Piece p : simPieces){
                            if(p != king && p.color != currentColor && p.canMove(checkingP.col, row)){
                                return false;
                            }
                        }
                    }
                }

            }else if(rowDiff == 0){//controllo se è un attacco orizzontale

                if(checkingP.col < king.col){//da sinistra
                    for(int col = checkingP.col ; col < king.col; col++){
                        for(Piece p : simPieces){
                            if(p != king && p.color != currentColor && p.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }
                if(checkingP.col > king.col){//da destra
                    for(int col = checkingP.col ; col > king.col; col--){
                        for(Piece p : simPieces){
                            if(p != king && p.color != currentColor && p.canMove(col, checkingP.row)){
                                return false;
                            }
                        }
                    }
                }

            }else if(colDiff == rowDiff){//se l'attacco è diagonale
                
                if(checkingP.row < king.row){//diagonale ma dall'alto

                    if(checkingP.col < king.col){//da in alto a sinistra
                        for(int col = checkingP.col, row = checkingP.row ; col < king.col; col++, row++ ){
                            for(Piece p : simPieces){
                                if(p != king && p.color != currentColor && p.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){//da in alto a destra
                        for(int col = checkingP.col, row = checkingP.row ; col > king.col; col--, row++){
                            for(Piece p : simPieces){
                                if(p != king && p.color != currentColor && p.canMove(col, row)){
                                    return false;
                                }
                            }
                        }
                    }
                }
                if(checkingP.row > king.row){//diagonale ma dal basso

                    if(checkingP.col < king.col){//da in basso a sinistra
                        for(int col = checkingP.col, row = checkingP.row ; col < king.col; col++, row--){
                            for(Piece p : simPieces){
                                if(p != king && p.color != currentColor && p.canMove(col, row)){
                                    return true;
                                }
                            }
                        }
                    }
                    if(checkingP.col > king.col){//da in basso a destra
                        for(int col = checkingP.col, row = checkingP.row ; col > king.col; col--, row--){
                            for(Piece p : simPieces){
                                if(p != king && p.color != currentColor && p.canMove(col, row)){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }else{//se il pezzo che mette in check è un cavallo 
                    //questo attacco non può essere fermato essendo che fa un salto
            }
        }


        return true;
    }
    private boolean kingCanMove(Piece king){
        
        //Metodo per vedere se il re ha quadrati in cui andare per scappare dal check
        if(isValidMove(king, -1, -1)){return true;}
        if(isValidMove(king, 0, -1)){return true;}
        if(isValidMove(king, 1, -1)){return true;}
        if(isValidMove(king, -1, 0)){return true;}//Allla fine sono 8 i possibili quadrati in cui può andare la pedina del re
        if(isValidMove(king, 1, 0)){return true;}
        if(isValidMove(king, -1, 1)){return true;}
        if(isValidMove(king, 0, 1)){return true;}
        if(isValidMove(king, 1, 1)){return true;}
        
        return false;//se nessuno degli if ritorna true allora il re non si può muovere e ritorno false
    }
    private boolean isValidMove(Piece king, int colPlus, int rowPlus){
        
        boolean isValidMove = false;

        //Aggiorniamo al volo la posizione del re
        king.col += colPlus;
        king.row += rowPlus;

        if(king.canMove(king.col,  king.row)){ //negli if vediamo se il nuovo posto sia safe, e che non sia illegale
            if(king.hittingP != null){
                simPieces.remove(king.hittingP.getIndex());
            }
            if(isIllegal(king) == false){
                isValidMove = true;
            }
        }

        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }//--------------------------------------------------------------
    
    public void paintComponent(Graphics g){//un metodo di JComponent->e ereditata a Jpanel
        super.paintComponent(g);//il metodo paintComponent permette di disegnare oggetti nella finestra di gioco
        
        Graphics2D g2 = (Graphics2D)g;

        //TABLE
        table.draw(g2);//metodo che disegna e colora la grafica della tabella

        //PIECES
        for(Piece p : simPieces){
            p.draw(g2);
        }

        if(activeP != null){
            if(canMove){//con questo if coloriamo di bianco solo i quadrati che possiamo raggiungere con la pedina
                if(isIllegal(activeP) || opponentCanCaptureKing() ){
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Table.SQUARE_SIZE, activeP.row*Table.SQUARE_SIZE,
                                Table.SQUARE_SIZE, Table.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }else{
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Table.SQUARE_SIZE, activeP.row*Table.SQUARE_SIZE,
                                Table.SQUARE_SIZE, Table.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            }
            

            //ora disegnamo di nuovo la pedina che stiamo holdando perchè così non verrà coperta dal nuovo quadrato colorato
            activeP.draw(g2); // creare il metodo sempre dopo i metodi scritti sopra sempre dentro l'if

        }

        //messaggi per stato di gioco
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if(promotion){
            g2.drawString("Promote to:", 840, 150);
            for(Piece p : promoPieces){
                g2.drawImage(p.immagine, p.getX(p.col), p.getY(p.row),Table.SQUARE_SIZE, Table.SQUARE_SIZE, null);
            }
        }else{
            if(currentColor == WHITE){//display il messaggio
                g2.drawString("White's turn", 840, 550);
                if(checkingP != null && checkingP.color == Black){//condizione nel caso il re fosse in check
                    g2.setColor(Color.red);
                    g2.drawString("Il Re ha er ", 840, 650);
                    g2.drawString("ferro puntato", 840, 700);
                }
            }else{
                g2.drawString("Monkey's turn", 840, 250);
                if(checkingP != null && checkingP.color == WHITE){//condizione nel caso il re fosse in check
                    g2.setColor(Color.red);
                    g2.drawString("Il Re ha er", 840, 100);
                    g2.drawString("ferro puntato", 840, 150);
                }
            }
        }

        if(gameover){
            String s = "";
            if(currentColor == WHITE){
                s = "Colonizzatori Wins";
            }else{
                s = "Monkey Wins";
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.green);
            g2.drawString(s, 200, 420);
        }

        if(stalemate){
            g2.setFont(new Font("Arial", Font.PLAIN, 90));
            g2.setColor(Color.lightGray);
            g2.drawString("Stalemate", 200, 420);
        }


    }
    

    


}
