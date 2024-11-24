import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OpeningBook {

    private HashMap<String, String[]> posMoves;
    private Random random;


    public OpeningBook(String file){
        random = new Random();
        posMoves = new HashMap<>();

        String[] entries = file.split("pos ");
        for(String entry : entries){
            if(entry == "") continue;

            String[] fenMovesSplit = entry.split("\\R");

            String[] bookMoves = new String[fenMovesSplit.length - 1];
            System.arraycopy(fenMovesSplit, 1, bookMoves, 0, bookMoves.length);

            posMoves.put(fenMovesSplit[0], bookMoves);
        }
    }

    //Looks up opening moves for given position and returns a random move from the book.
    public Move lookupPosition(Board board){
        String[] moves = posMoves.get(Helpers.boardToFen(board));
        if(moves != null){
            int index = random.nextInt(moves.length);
            String bookMove = moves[index].split(" ")[0];
            Move move = bookMoveToMove(bookMove, board);
            return move;
        }else{
            return null;
        }
    }

    //Takes a move represented as a string and turns it into the Move Class
    public Move bookMoveToMove(String bookMove, Board board){
        byte aInAscii = 97; //Ascii code for a

        byte src, dst;

        char srcColumn = bookMove.charAt(0);
        char dstColumn = bookMove.charAt(2);
        char srcFile = bookMove.charAt(1);
        char dstFile = bookMove.charAt(3);

        src = (byte)((Character.getNumericValue(srcFile) - 1) * 8 + ((byte)(srcColumn)) - aInAscii);
        dst = (byte)((Character.getNumericValue(dstFile) - 1) * 8 + ((byte)(dstColumn)) - aInAscii);

        Move placeHolderMove = new Move(src, dst);

        List<Move> moves = board.getPseudoLegalMoves();

        //Do this to get correct movetype + promotion stuff
        for(Move curMove : moves){
            if(curMove.equals(placeHolderMove)){
                return curMove;
            }
        }

        return null;
    }
}
