import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReferenceArray;


public class ConcurrentBot {
    private long endTime;
    private boolean foundFallbackMove; //Variable to see if we have at least one move to return, otherwise continue search even if time is out.


    private OpeningBook openingBook;

    private ThreadSafeTT transPosTable;

    private final ExecutorService executor;


    private final int maxPly = 20;
    private final AtomicReferenceArray<Move> killerMoves = new AtomicReferenceArray<Move>(maxPly+1); //Store killer moves, modify ordering to place killers adter captures(modify moveValue).

    public ConcurrentBot() throws Exception{
        Path relativePath = Paths.get("resources", "Book.txt");
        this.openingBook = new OpeningBook(new String(Files.readAllBytes(relativePath.toAbsolutePath())));
        this.transPosTable = new ThreadSafeTT((int)Math.pow(2,23));
        this.executor = Executors.newFixedThreadPool(6);
    }

    public void clearTT(){
        this.transPosTable.clear();
    }

    public Move getBestMove(Board board, int timeLimit) throws Exception{

        Move bookMove = openingBook.lookupPosition(board);
        if(bookMove != null){
            return bookMove;
        }

        this.endTime = System.currentTimeMillis() + timeLimit;
        this.foundFallbackMove = false;

        ArrayList<Move> moves = board.getPseudoLegalMoves();
        Helpers.orderMoves(moves);
        
        int depth = 2; //start depth
        Move bestMove = null;
        while(System.currentTimeMillis() < endTime){
            Map<Move, Future<Integer>> moveScores = new HashMap<>();

            for(Move move : moves){
                if(!board.tryMakeMove(move)) continue;
                Callable<Integer> task = new ConcurrentSearch(new Board(board), transPosTable, killerMoves, this.endTime, foundFallbackMove, depth);
                moveScores.put(move, executor.submit(task));
                board.unmakeMove();
            }

            

            int bestScore = Constants.lowestEval;
            Move currentBestMove = null;
            for(Map.Entry<Move, Future<Integer>> entry : moveScores.entrySet()) {
                Move move = entry.getKey();
                int score = entry.getValue().get(); // Retrieve computed score

                if (score > bestScore) {
                    bestScore = score;
                    currentBestMove = move;
                }
            }

            if(foundFallbackMove && System.currentTimeMillis() >= this.endTime){
                break;
            }
            foundFallbackMove = true;
            bestMove = currentBestMove;
            
            Helpers.moveToHead(moves, bestMove); //In order to search best move first
            transPosTable.put(board.getZobristKey(), new TransPosEntry(board.getZobristKey(), depth, bestScore, bestMove, Constants.exact));
            depth++;
        }
        System.out.println(depth);
        return bestMove;
    }


    
}
