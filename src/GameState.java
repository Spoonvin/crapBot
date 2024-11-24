public class GameState {
    PieceType capturedPiece;
    Move move;
    int pawnPush;

    long zobristKey;
    
    boolean wCanCastleRight;
    boolean wCanCastleLeft;
    boolean bCanCastleRight;
    boolean bCanCastleLeft;

    int fiftyMoveRuleCount;

    //TODO: add hash

    public GameState(PieceType p, Move m, int pP, long zobristKey, int fiftyMoveRuleCount, boolean[] cR){
        this.capturedPiece = p;
        this.move = m;
        this.pawnPush = pP;

        this.zobristKey = zobristKey;

        this.fiftyMoveRuleCount = fiftyMoveRuleCount;

        if(cR == null){
            return;
        }

        this.wCanCastleRight = cR[0];
        this.wCanCastleLeft = cR[1];
        this.bCanCastleRight = cR[2];
        this.bCanCastleLeft = cR[3];
    }
}
