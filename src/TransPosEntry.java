public class TransPosEntry {
    int depth;
    int score;

    //0: exact. 1: upperBound. 2: lowerBound.
    int type;

    long zobKey;

    Move bestMove;

    public TransPosEntry(long zobKey, int depth, int score, Move bestMove, int type){
        this.zobKey = zobKey;
        this.depth = depth;
        this.score = score;
        this.bestMove = bestMove;
        this.type = type;
    }
}
