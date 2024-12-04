import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.AbstractMap;
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
        
        int depth = 4; //start depth
        Move bestMove = null;

        ArrayList<ConcurrentSearch> tasks = new ArrayList<>();
        ArrayList<Map.Entry<Move, Future<Integer>>> moveScores = new ArrayList<>();
        for(Move move : moves){
            if(!board.tryMakeMove(move)) continue;
            ConcurrentSearch task = new ConcurrentSearch(new Board(board), transPosTable, killerMoves, this.endTime, foundFallbackMove, depth);
            tasks.add(task);
            moveScores.add(new AbstractMap.SimpleEntry<>(move, null));
            board.unmakeMove();
        }
        
        do{
            

            for(int i = 0; i < tasks.size(); i++){
                ConcurrentSearch concSearch = tasks.get(i);
                concSearch.setDepth(depth);
                concSearch.setFoundFallbackMove(foundFallbackMove);
                Map.Entry<Move, Future<Integer>> entry = moveScores.get(i);
                entry.setValue(executor.submit(concSearch));
            }

            int bestScore = Constants.lowestEval;
            Move currentBestMove = null;
            for(Map.Entry<Move, Future<Integer>> entry : moveScores) {
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
            
            transPosTable.put(board.getZobristKey(), new TransPosEntry(board.getZobristKey(), depth, bestScore, bestMove, Constants.exact));
            depth++;
        }while(System.currentTimeMillis() < endTime);
        System.out.println(depth);
        return bestMove;
    }


    
}
