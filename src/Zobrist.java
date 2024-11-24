import java.util.Random;

public class Zobrist {
    //update with castling rights and stuff later
    private static long[][] zobristKeyMap;

    public static final int wPaZob = 0;
    public static final int bPaZob = 1;
    public static final int wKnZob = 2;
    public static final int bKnZob = 3;
    public static final int wBiZob = 4;
    public static final int bBiZob = 5;
    public static final int wRoZob = 6;
    public static final int bRoZob = 7;
    public static final int wQuZob = 8;
    public static final int bQuZob = 9;
    public static final int wKiZob = 10;
    public static final int bKiZob = 11;

    public Zobrist(){
        initializeZobristKeyMap();
    }

    public static long[][] getzobristKeyMap(){
        return zobristKeyMap;
    }

    //Add keys for castling rights??
    public static void initializeZobristKeyMap(){
        Random random = new Random();
        zobristKeyMap = new long[65][12];

        for (int square = 0; square < 64; square++) {
            for (int pieceType = 0; pieceType < 12; pieceType++) {
                zobristKeyMap[square][pieceType] = random.nextLong();
            }
        }
        zobristKeyMap[64][0] = random.nextLong(); //If its whites turn
    }

    public static long generateZobristKey(Board b){
        long key = 0;
        for(byte square = 0; square < 64; square++){
            switch(b.getPieceOnSquare(square)){
                case BBISHOP:
                    key ^= zobristKeyMap[square][bBiZob];
                    break;
                case BKING:
                    key ^= zobristKeyMap[square][bKiZob];
                    break;
                case BKNIGHT:
                    key ^= zobristKeyMap[square][bKnZob];
                    break;
                case BPAWN:
                    key ^= zobristKeyMap[square][bPaZob];
                    break;
                case BQUEEN:
                    key ^= zobristKeyMap[square][bQuZob];
                    break;
                case BROOK:
                    key ^= zobristKeyMap[square][bRoZob];
                    break;
                case WBISHOP:
                    key ^= zobristKeyMap[square][wBiZob];
                    break;
                case WKING:
                    key ^= zobristKeyMap[square][wKiZob];
                    break;
                case WKNIGHT:
                    key ^= zobristKeyMap[square][wKnZob];
                    break;
                case WPAWN:
                    key ^= zobristKeyMap[square][wPaZob];
                    break;
                case WQUEEN:
                    key ^= zobristKeyMap[square][wQuZob];
                    break;
                case WROOK:
                    key ^= zobristKeyMap[square][wRoZob];
                    break;
                default:
                    break;
                
            }
            
        }

        if(b.getIsWhiteToPlay()) key ^= zobristKeyMap[64][0];
        return key;
    }
}
