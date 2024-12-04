import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ConcurrentSearch implements Callable<Integer>{
    long endTime;
    private boolean exitSearch;
    private boolean foundFallbackMove; //Variable to see if we have at least one move to return, otherwise continue search even if time is out.

    private ThreadSafeTT transPosTable;

    private final static int nullMoveReduction = 2; //2 or 3

    private int maxDepth;

    private final static int maxPly = 20;
    private final AtomicReferenceArray<Move> killerMoves; //Store killer moves, modify ordering to place killers adter captures(modify moveValue).

    private Board orgBoard;

    public ConcurrentSearch(Board board, ThreadSafeTT tt, AtomicReferenceArray<Move> killers, long endTime, boolean foundFallbackMove, int depth){
        this.transPosTable = tt;
        this.killerMoves = killers;
        this.endTime = endTime;
        this.foundFallbackMove = foundFallbackMove;
        this.orgBoard = board;
        this.maxDepth = depth;
        this.exitSearch = false;
    }

    public void setDepth(int newDepth){
        this.maxDepth = newDepth;
    }
    public void setFoundFallbackMove(boolean fbm){
        this.foundFallbackMove = fbm;
    }

    public int alphaBeta(Board board, int alpha, int beta, int ply, int depth){ 
        exitSearch = isTimeToExit();
        if(exitSearch) return 0;

        if(board.isDrawByRepetitionOr50Move()) return 0;
        
        //Transposition lookup
        TransPosEntry entry = transPosTable.get(board.getZobristKey());
        Move bestTTMove = null;
        if(entry != null) bestTTMove = entry.bestMove;
        if(entry != null && entry.depth >= depth){
            int score = entry.score;
            if(score == -Constants.checkmateScore){
                score += ply;
            }else if(score == Constants.checkmateScore){
                score -= ply;
            }
            switch(entry.type){
                case Constants.exact:
                    return score;
                case Constants.upperBound:
                    if(score <= alpha){
                        return alpha;
                    }
                    break;
                case Constants.lowerBound:
                    if(score >= beta){
                        return beta;
                    }
                    break;
                default:
                    break;
            }
        }

        if(depth <= 0 || ply > maxPly){
            return quiescenceSearch(board, alpha, beta, ply+1);
        }

        boolean isInCheck = board.isCheckForColor(board.getIsWhiteToPlay());

        //Null move pruning
        int staticMaterialEval = materialEval(board); //Only fast less accurate eval needed? 
        if(!isInCheck && (depth > nullMoveReduction) && 
            (staticMaterialEval > beta) && (Helpers.endGameRatio(board) < 0.8)){ 

            board.makeNullMove();
            int nullScore = -alphaBeta(board, -beta, -alpha, ply+1, depth-1-nullMoveReduction);
            board.unMakeNullMove();

            if(nullScore >= beta){
                return beta;
            }
        }

        boolean foundAtLeastOneMove = false;

        ArrayList<Move> moves = board.getPseudoLegalMoves();
        Helpers.orderMovesWithKiller(moves, killerMoves.get(ply));
        if(bestTTMove != null) Helpers.moveToHead(moves, bestTTMove);

        int alphaOrg = alpha;
        int bestScore = Constants.lowestEval;
        Move bestMove = null;

        if(isInCheck && depth <= 1) depth++; //Check extensions
 
        for(Move move : moves){
            if(!board.tryMakeMove(move)) continue;

            foundAtLeastOneMove = true;

            int curScore = -alphaBeta(board, -beta, -alpha, ply + 1, depth-1);

            board.unmakeMove();

            //Make bot choose least move mate. Is there a better solution?
            if(curScore >= Constants.checkmateScore - maxPly){
                curScore--;
            }else if(curScore <= -Constants.checkmateScore + maxPly){
                curScore++;
            }

            if(curScore >= beta){ //Beta cut-off
                if(move.mType == MoveType.QUIET) killerMoves.set(ply, move); //Beta cutoff gives us a killer move (if quiet)
                if(!exitSearch) transPosTable.put(board.getZobristKey(), new TransPosEntry(board.getZobristKey(), depth, beta, move, Constants.lowerBound));
                return beta;
            } 
            if(curScore > bestScore){
                bestScore = curScore;
                bestMove = move;
            }
            if(curScore > alpha) alpha = curScore;
        }
        if(!foundAtLeastOneMove){
            if(isInCheck){
                return -Constants.checkmateScore;
            }else{
                return 0;
            }
        }

        
        int transPosType;
        if(alpha != alphaOrg){
            transPosType = Constants.exact;
        }else{
            transPosType = Constants.upperBound;
        }
        if(!exitSearch) transPosTable.put(board.getZobristKey(), new TransPosEntry(board.getZobristKey(), depth, alpha, bestMove, transPosType));
        
        return alpha;
    }

    private int eval(Board board){

        int wPoints = 0;
        int bPoints = 0;

        double endGameRatio = Helpers.endGameRatio(board);
        
        //Piece points
        for(int i = 0; i < 64; i++){
            PieceType piece = board.getPieceOnSquare(i);
            switch(piece){
                case BBISHOP:
                    bPoints += Constants.bishopVal + Constants.modBBi[i];
                    break;
                case BKING:
                    bPoints += Constants.kingVal + (1-endGameRatio)*Constants.modBKiMid[i] + endGameRatio*Constants.modBKiEnd[i];
                    break;
                case BKNIGHT:
                    bPoints += Constants.knightVal + Constants.modBKn[i];
                    break;
                case BPAWN:
                    bPoints += Constants.pawnVal + (1-endGameRatio)*Constants.modBPaMid[i] + endGameRatio*Constants.modBPaEnd[i];
                    break;
                case BQUEEN:
                    bPoints += Constants.queenVal + Constants.modBQu[i];
                    break;
                case BROOK:
                    bPoints += Constants.rookVal + Constants.modBRo[i];
                    break;
                case WBISHOP:
                    wPoints += Constants.bishopVal + Constants.modWBi[i];
                    break;
                case WKING:
                    wPoints += Constants.kingVal + (1-endGameRatio)*Constants.modWKiMid[i] + endGameRatio*Constants.modWKiEnd[i];
                    break;
                case WKNIGHT:
                    wPoints += Constants.knightVal + Constants.modWKn[i];
                    break;
                case WPAWN:
                    wPoints += Constants.pawnVal + (1-endGameRatio)*Constants.modWPaMid[i] + endGameRatio*Constants.modWPaEnd[i];
                    break;
                case WQUEEN:
                    wPoints += Constants.queenVal + Constants.modWQu[i];
                    break;
                case WROOK:
                    wPoints += Constants.rookVal + Constants.modWRo[i];
                    break;
                default:
                    break;
            }
        }

        long wPawns = board.getPieceType(PieceType.WPAWN);
        long wKing = board.getPieceType(PieceType.WKING);

        long bPawns = board.getPieceType(PieceType.BPAWN);
        long bKing = board.getPieceType(PieceType.BKING);

        wPoints += Helpers.passedPawnsBonus(wPawns, bPawns, true, endGameRatio);
        wPoints -= Helpers.stackedpawnsPenalty(wPawns);
        double wShieldNOpenFileMod = (1-endGameRatio)*(Helpers.pawnShieldBonus(wKing, wPawns, true) - Helpers.openFilePenalty(wKing, wPawns));
        wPoints += wShieldNOpenFileMod;

        bPoints += Helpers.passedPawnsBonus(bPawns, wPawns, false, endGameRatio);
        bPoints -= Helpers.stackedpawnsPenalty(bPawns);
        double bShieldNOpenFileMod = (1-endGameRatio)*(Helpers.pawnShieldBonus(bKing, bPawns, false) - Helpers.openFilePenalty(bKing, bPawns));
        bPoints += bShieldNOpenFileMod;

        
        int relativeScore = wPoints - bPoints;
        if(board.getIsWhiteToPlay()){
            return relativeScore;
        }else{
            return -relativeScore;
        }
    }

    //Tries to eval only quiet positions
    private int quiescenceSearch(Board board, int alpha, int beta, int ply){
        exitSearch = isTimeToExit();
        if(exitSearch) return 0;
        
        //Transposition lookup
        TransPosEntry entry = transPosTable.get(board.getZobristKey());
        Move bestTTMove = null;
        if(entry != null) bestTTMove = entry.bestMove;
        if(entry != null){
            int score = entry.score;
            if(score == -Constants.checkmateScore){
                score += ply;
            }else if(score == Constants.checkmateScore){
                score -= ply;
            }
            switch(entry.type){
                case Constants.exact:
                    return score;
                case Constants.upperBound:
                    if(score <= alpha){
                        return alpha;
                    }
                    break;
                case Constants.lowerBound:
                    if(score >= beta){
                        return beta;
                    }
                    break;
                default:
                    break;
            }
        }

        int standPat = eval(board);  //static eval

        if (standPat >= beta){
            return beta;  //Fail-hard beta cutoff
        }
        if (alpha < standPat){
            alpha = standPat;  // Update alpha with static evaluation if better
        }

        boolean foundAtLeastOneMove = false;
        int alphaOrg = alpha;
        int bestScore = Constants.lowestEval;
        Move bestMove = null;

        ArrayList<Move> moves = board.getNonQuietMoves();
        if(bestTTMove != null) Helpers.moveToHead(moves, bestTTMove);

        for(Move move : moves){
            if(!board.tryMakeMove(move)) continue;
            foundAtLeastOneMove = true;
            int eval = -quiescenceSearch(board, -beta, -alpha, ply+1);
            board.unmakeMove();
            if(eval >= beta){
                if(!exitSearch) transPosTable.put(board.getZobristKey(), new TransPosEntry(board.getZobristKey(), 0, beta, move, Constants.lowerBound));
                return beta;
            }
            if(eval > bestScore){
                bestScore = eval;
                bestMove = move;
            }
            if(eval > alpha){
                alpha = eval;
            }
        }
        if(!foundAtLeastOneMove) return standPat;

        int transPosType;
        if(alpha != alphaOrg){
            transPosType = Constants.exact;
        }else{
            transPosType = Constants.upperBound;
        }
        if(!exitSearch) transPosTable.put(board.getZobristKey(), new TransPosEntry(board.getZobristKey(), 0, alpha, bestMove, transPosType));
        return alpha;
    }

    //Evaluates only material difference for a faster eval function
    private int materialEval(Board board){
        int wPoints = 0;
        int bPoints = 0;
        
        //Piece points
        for(int i = 0; i < 64; i++){
            PieceType piece = board.getPieceOnSquare(i);
            switch(piece){
                case BBISHOP:
                    bPoints += Constants.bishopVal;
                    break;
                case BKING:
                    bPoints += Constants.kingVal;
                    break;
                case BKNIGHT:
                    bPoints += Constants.knightVal;
                    break;
                case BPAWN:
                    bPoints += Constants.pawnVal;
                    break;
                case BQUEEN:
                    bPoints += Constants.queenVal;
                    break;
                case BROOK:
                    bPoints += Constants.rookVal;
                    break;
                case WBISHOP:
                    wPoints += Constants.bishopVal;
                    break;
                case WKING:
                    wPoints += Constants.kingVal;
                    break;
                case WKNIGHT:
                    wPoints += Constants.knightVal;
                    break;
                case WPAWN:
                    wPoints += Constants.pawnVal;
                    break;
                case WQUEEN:
                    wPoints += Constants.queenVal;
                    break;
                case WROOK:
                    wPoints += Constants.rookVal;
                    break;
                default:
                    break;
            }
        }
        int relativeScore = wPoints - bPoints;
        if(board.getIsWhiteToPlay()){
            return relativeScore;
        }else{
            return -relativeScore;
        }
    }
    
    private boolean isTimeToExit(){
        if(!foundFallbackMove) return false;
        if(exitSearch) return true;
        return System.currentTimeMillis() > endTime;
    }

    @Override
    public Integer call() throws Exception {
        return -alphaBeta(orgBoard, Constants.lowestEval, Constants.highestEval, 0, maxDepth);
    }

}
