import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ChessAiOld {
    private long startTime;
    private long timeLim;
    private boolean exitSearch;
    private final int timeCheckInterval = 512; //Check time only every nth node. Preferably n power of 2.
    private boolean foundFallbackMove; //Variable to see if we have at least one move to return, otherwise continue search even if time is out.

    private int nodeCount;

    private OpeningBook openingBook;

    private TranspositionTable transPosTable;

    private final int nullMoveReduction = 2; //2 or 3

    private final int maxPly = 20;
    private final Move[] killerMoves = new Move[maxPly+1]; //Store killer moves, modify ordering to place killers adter captures(modify moveValue).

    public ChessAiOld() throws Exception{
        Path relativePath = Paths.get("resources", "Book.txt");
        this.openingBook = new OpeningBook(new String(Files.readAllBytes(relativePath.toAbsolutePath())));
        this.transPosTable = new TranspositionTable((int)Math.pow(2,23));
    }

    public Move getBestMove(Board board, int timeLimit){

        Move bookMove = openingBook.lookupPosition(board);
        if(bookMove != null){
            return bookMove;
        }

        this.startTime = System.currentTimeMillis();
        this.timeLim = timeLimit; //Timelim in millis
        this.exitSearch = false;
        this.foundFallbackMove = false;

        //this.transPosTable.clear();

        this.nodeCount = 0;

        ArrayList<Move> moves = board.getPseudoLegalMoves();
        Helpers.orderMoves(moves);
        
        int depth = 0; //start depth
        Move bestMove = null;
        while(!exitSearch && (System.currentTimeMillis() - startTime) < timeLim){
            int alpha = Constants.lowestEval +1;
            int beta = Constants.highestEval -1;
            int score = Constants.lowestEval +1;

            Move currentBestMove = null;

            for(Move move : moves){
                if(!board.tryMakeMove(move)) continue;
                int curScore = -alphaBeta(board, -beta, -alpha, 1, depth);
                board.unmakeMove();

                if(exitSearch) break;
                if(curScore > score){
                    score = curScore;
                    alpha = curScore;
                    currentBestMove = move;
                }
            }
            foundFallbackMove = true;
            if(exitSearch){
                break;
            }
            bestMove = currentBestMove;
            Helpers.moveToHead(moves, bestMove); //In order to search best move first
            depth++;
        }

        //System.out.println(nodeCount);

        return bestMove;
    }

    

    public int alphaBeta(Board board, int alpha, int beta, int ply, int depth){ 
        exitSearch = isTimeToExit(nodeCount);
        if(exitSearch) return 0;

        /* 
        //Transposition lookup
        TransPosEntry entry = transPosTable.get(board.getZobristKey());
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
            */

        this.nodeCount++;

        if(board.isDrawByRepetitionOr50Move()) return 0;

        if(depth <= 0 || ply > maxPly){
            return quiescenceSearch(board, alpha, beta);
        }

        //Null move pruning
        int staticEval = eval(board);
        if(!board.isCheckForColor(board.getIsWhiteToPlay()) && (depth > nullMoveReduction) && 
            (staticEval > beta) && (Helpers.endGameRatio(board) < 0.7)){ 

            board.makeNullMove();
            int nullScore = -alphaBeta(board, -beta, -alpha, ply+1, depth-1-nullMoveReduction);
            board.unMakeNullMove();

            if(nullScore >= beta){
                return beta;
            }
        }

        boolean foundAtLeastOneMove = false;

        ArrayList<Move> moves = board.getPseudoLegalMoves();
        Helpers.orderMovesWithKiller(moves, killerMoves[ply]);

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
                if(move.mType == MoveType.QUIET) killerMoves[ply] = move; //Beta cutoff gives us a killer move (if quiet)
                return beta;
            } 
            if(curScore > alpha) alpha = curScore;
        }
        if(!foundAtLeastOneMove){
            if(board.isCheckForColor(board.getIsWhiteToPlay())){
                return -Constants.checkmateScore;
            }else{
                return 0;
            }
        }

        /* 
        int transPosType;
        if(alpha <= alphaOrg){
            transPosType = Constants.upperBound;
        }else{
            transPosType = Constants.exact;
        }
        transPosTable.put(board.getZobristKey(), new TransPosEntry(board.getZobristKey(), depth, alpha, transPosType));
        */
        return alpha;
    }

    private int eval(Board board){

        //TODO: Calculate endgame ratio

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

        wPoints += Helpers.passedPawnsBonus(wPawns, bPawns, true);
        wPoints -= Helpers.stackedpawnsPenalty(wPawns);
        wPoints += (1-endGameRatio)*Helpers.pawnShieldModifier(wKing, wPawns, true);

        bPoints += Helpers.passedPawnsBonus(bPawns, wPawns, false);
        bPoints -= Helpers.stackedpawnsPenalty(bPawns);
        bPoints += (1-endGameRatio)*Helpers.pawnShieldModifier(bKing, bPawns, false);
        
        int relativeScore = wPoints - bPoints;
        if(board.getIsWhiteToPlay()){
            return relativeScore;
        }else{
            return -relativeScore;
        }
    }

    //Tries to eval only quiet positions
    //TODO: add timecheck
    private int quiescenceSearch(Board board, int alpha, int beta){
        exitSearch = isTimeToExit(nodeCount);
        if(exitSearch) return 0;

        this.nodeCount++;

        int standPat = eval(board);  //static eval

        if (standPat >= beta){
            return beta;  //Fail-hard beta cutoff
        }
        if (alpha < standPat){
            alpha = standPat;  // Update alpha with static evaluation if better
        }

        boolean foundAtLeastOneMove = false;

        for(Move move : board.getNonQuietMoves()){
            if(!board.tryMakeMove(move)) continue;
            foundAtLeastOneMove = true;
            int eval = -quiescenceSearch(board, -beta, -alpha);
            board.unmakeMove();
            if(eval >= beta){
                return beta;
            }
            if(eval > alpha){
                alpha = eval;
            }
        }
        if(!foundAtLeastOneMove) return standPat; //TODO: Check if in check
        return alpha;
    }

    
    public Move getBestMoveDepth(Board board, int depth){
        this.nodeCount = 0;
        this.foundFallbackMove = false; //Make search not depend on time

        ArrayList<Move> moves = board.getPseudoLegalMoves();
        Helpers.orderMoves(moves);

        Move bestMove = null;
        int alpha = Constants.lowestEval +1;
        int beta = Constants.highestEval -1;
        int score = Constants.lowestEval +1;
        for(Move move : moves){
            if(!board.tryMakeMove(move)) continue;
            int curScore = -alphaBeta(board, -beta, -alpha, 1, depth); //Modify search first to test
            board.unmakeMove();

            if(curScore > score){
                score = curScore;
                bestMove = move;
            }
        }
        System.out.print("Node count: ");
        System.out.println(this.nodeCount);
        return bestMove;
    }
    

    //Input node count => check time every nth node
    private boolean isTimeToExit(int checkNumber){
        if(!foundFallbackMove) return false;
        if(exitSearch) return true;
        if(checkNumber % timeCheckInterval != 0) return false;
        return (System.currentTimeMillis() - startTime) > timeLim;
    }
}
