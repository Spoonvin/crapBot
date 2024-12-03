public class Constants {
    public final static int pawnVal = 100;
    public final static int knightVal = 300;
    public final static int bishopVal = 320;
    public final static int rookVal = 500;
    public final static int queenVal = 900;
    public final static int kingVal = 1000;

    public final static int wKingStartSquare = 4;
    public final static int bKingStartSquare = 60;

    public final static long[] wPawnMoves = Helpers.generateWPawnMoves();
    public final static long[] bPawnMoves = Helpers.generateBPawnMoves();

    public final static long[] wPawnAttacks = Helpers.generateWPawnAttacks();
    public final static long[] bPawnAttacks = Helpers.generateBPawnAttacks();

    public final static long[] bishopMoves = Helpers.generateBishopMoves();
    public final static long[] knightMoves = Helpers.generateKnightMoves();
    public final static long[] rookMoves = Helpers.generateRookMoves();
    public final static long[] queenMoves = Helpers.generateQueenMoves();
    public final static long[] kingMoves = Helpers.generateKingMoves();

    public final static long wCastleLeftPath = 0b1110;
    public final static long wCastleRightPath = 0b1100000;
    public final static long bCastleLeftPath = 0xE00000000000000L;
    public final static long bCastleRightPath = 0x6000000000000000L;

    public final static long aFile = 0x0101010101010101L;
    public final static long bFile = aFile << 1;
    public final static long cFile = aFile << 2;
    public final static long dFile = aFile << 3;
    public final static long eFile = aFile << 4;
    public final static long fFile = aFile << 5;
    public final static long gFile = aFile << 6;
    public final static long hFile = aFile << 7;
    
    public static final long[] rookPossibleBlockSquares = //Used for magicLookup generation
        Helpers.genRoPossibleBlockSquares();

    public static final long[] bishopPossibleBlockSquares = //Used for magicLookup generation
        Helpers.genBiPossibleBlockSquares();
    


    public final static long rankMasks8[] =/*from rank1 to rank8*/
    {
        0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L, 0xFF000000000000L, 0xFF00000000000000L
    };

    public final static long fileMasks8[] =/*from fileA to FileH*/
    {
        0x101010101010101L, 0x202020202020202L, 0x404040404040404L, 0x808080808080808L,
        0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L
    };

    public final static long diagonalMasks8[] =/*from top left to bottom right*/
    {
	0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L, 0x1020408102040L,
	0x102040810204080L, 0x204081020408000L, 0x408102040800000L, 0x810204080000000L,
	0x1020408000000000L, 0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L
    };

    public final static long antiDiagonalMasks8[] =/*from top right to bottom left*/
    {
	0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L, 0x80402010080402L,
	0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L,
	0x804020100000000L, 0x402010000000000L, 0x201000000000000L, 0x100000000000000L
    };

    public final static long[] files = {aFile, bFile, cFile, dFile, eFile, fFile, gFile, hFile};

    public final static  long[] bishopMagics = {
        0x40040844404084L,
        0x2004208a004208L,
        0x10190041080202L,
        0x108060845042010L,
        0x581104180800210L,
        0x2112080446200010L,
        0x1080820820060210L,
        0x3c0808410220200L,
        0x4050404440404L,
        0x21001420088L,
        0x24d0080801082102L,
        0x1020a0a020400L,
        0x40308200402L,
        0x4011002100800L,
        0x401484104104005L,
        0x801010402020200L,
        0x400210c3880100L,
        0x404022024108200L,
        0x810018200204102L,
        0x4002801a02003L,
        0x85040820080400L,
        0x810102c808880400L,
        0xe900410884800L,
        0x8002020480840102L,
        0x220200865090201L,
        0x2010100a02021202L,
        0x152048408022401L,
        0x20080002081110L,
        0x4001001021004000L,
        0x800040400a011002L,
        0xe4004081011002L,
        0x1c004001012080L,
        0x8004200962a00220L,
        0x8422100208500202L,
        0x2000402200300c08L,
        0x8646020080080080L,
        0x80020a0200100808L,
        0x2010004880111000L,
        0x623000a080011400L,
        0x42008c0340209202L,
        0x209188240001000L,
        0x400408a884001800L,
        0x110400a6080400L,
        0x1840060a44020800L,
        0x90080104000041L,
        0x201011000808101L,
        0x1a2208080504f080L,
        0x8012020600211212L,
        0x500861011240000L,
        0x180806108200800L,
        0x4000020e01040044L,
        0x300000261044000aL,
        0x802241102020002L,
        0x20906061210001L,
        0x5a84841004010310L,
        0x4010801011c04L,
        0xa010109502200L,
        0x4a02012000L,
        0x500201010098b028L,
        0x8040002811040900L,
        0x28000010020204L,
        0x6000020202d0240L,
        0x8918844842082200L,
        0x4010011029020020L
    };

    public final static long[] rookMagics = {
        0x8a80104000800020L,
        0x140002000100040L,
        0x2801880a0017001L,
        0x100081001000420L,
        0x200020010080420L,
        0x3001c0002010008L,
        0x8480008002000100L,
        0x2080088004402900L,
        0x800098204000L,
        0x2024401000200040L,
        0x100802000801000L,
        0x120800800801000L,
        0x208808088000400L,
        0x2802200800400L,
        0x2200800100020080L,
        0x801000060821100L,
        0x80044006422000L,
        0x100808020004000L,
        0x12108a0010204200L,
        0x140848010000802L,
        0x481828014002800L,
        0x8094004002004100L,
        0x4010040010010802L,
        0x20008806104L,
        0x100400080208000L,
        0x2040002120081000L,
        0x21200680100081L,
        0x20100080080080L,
        0x2000a00200410L,
        0x20080800400L,
        0x80088400100102L,
        0x80004600042881L,
        0x4040008040800020L,
        0x440003000200801L,
        0x4200011004500L,
        0x188020010100100L,
        0x14800401802800L,
        0x2080040080800200L,
        0x124080204001001L,
        0x200046502000484L,
        0x480400080088020L,
        0x1000422010034000L,
        0x30200100110040L,
        0x100021010009L,
        0x2002080100110004L,
        0x202008004008002L,
        0x20020004010100L,
        0x2048440040820001L,
        0x101002200408200L,
        0x40802000401080L,
        0x4008142004410100L,
        0x2060820c0120200L,
        0x1001004080100L,
        0x20c020080040080L,
        0x2935610830022400L,
        0x44440041009200L,
        0x280001040802101L,
        0x2100190040002085L,
        0x80c0084100102001L,
        0x4024081001000421L,
        0x20030a0244872L,
        0x12001008414402L,
        0x2006104900a0804L,
        0x1004081002402L
    };

    public final static int[] rookRellevantBits = {
        12, 11, 11, 11, 11, 11, 11, 12,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        11, 10, 10, 10, 10, 10, 10, 11,
        12, 11, 11, 11, 11, 11, 11, 12
    };

    public final static int[] bishopRellevantBits = {
        6, 5, 5, 5, 5, 5, 5, 6,
        5, 5, 5, 5, 5, 5, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 9, 9, 7, 5, 5,
        5, 5, 7, 7, 7, 7, 5, 5,
        5, 5, 5, 5, 5, 5, 5, 5,
        6, 5, 5, 5, 5, 5, 5, 6
    };
    
    public static long[][] bishopMagicLookup = Helpers.initBishopMagicLookup();
    public static long[][] rookMagicLookup = Helpers.initRookMagicLookup();

    //************************** Ai constants **********************************//

    public final static int checkmateScore = 999999;

    //Add / sub 1 because overflow when negating in the bot :/
    public final static int lowestEval = Integer.MIN_VALUE + 1;
    public final static int highestEval = Integer.MAX_VALUE - 1;

    //Transposition entry flags
    public final static int upperBound = 1;
    public final static int lowerBound = 2;
    public final static int exact = 0;

    public static int maxPly = 12;

    public final static int[] passedPawnRankMod = {0, 15, 20, 50, 70, 95, 130, 0};
    public final static int stackedPawnPenalty = 30;
    public final static int[] pawnShieldModifiers = {-70, -20, 40, 50};
    public final static int openKingFilesPenalty = 35;
    public final static int attackNextToKingPenalty = 15;
    public final static int movementBonus = 4; //Multiply num of attacks by this

    public final static byte[] modBPaMid =   {
        0,  0,  0,  0,  0,  0,  0,  0,
       50, 100,60, 80, 70, 100,20,  0,
       10, 10, 20, 30, 30, 20, 10,  0,
        5,  5, 10, 25, 25, 10,  5,  0,
        0,  0,  0, 20, 20,  0,  0,  0,
        5, -5,  0,  0,  0,  0, 10,  5,
        5,  0, 10,-20,-20, 10, 10,  0,
        0,  0,  0,  0,  0,  0,  0,  0};
   public final static byte[] modWPaMid = Helpers.mirror(modBPaMid);

   public final static byte[] modBPaEnd =   {
       0,   0,   0,   0,   0,   0,   0,   0,
       120, 120, 100, 100, 100, 100, 120, 120,
       70,  70,  65,  65,  65,  65,  65,  65,
       30,  30,  25,  25,  25,  25,  30,  30,
       15,  10,  10,  10,  10,  10,  10,  15,
       0,   0,   0,   0,   0,   0,   0,   0,
       5,   5,   5,   5,   5,   5,   5,   5,
       0,   0,   0,   0,   0,   0,   0,   0};
  public final static byte[] modWPaEnd = Helpers.mirror(modBPaEnd);
    
    public final static byte[] modBKn =   {
        -50,-40,-30,-30,-30,-30,-40,-50,
        -40,-20,  0,  0,  0,  0,-20,-40,
        -30,  0, 10, 15, 15, 10,  0,-30,
        -30,  5, 15, 20, 20, 15,  5,-30,
        -30,  0, 15, 20, 20, 15,  0,-30,
        -30,  5, 10, 15, 15, 10,  5,-30,
        -40,-20,  0,  5,  5,  0,-20,-40,
        -50,-40,-30,-30,-30,-30,-40,-50};
    public final static byte[] modWKn = Helpers.mirror(modBKn);

    public final static byte[] modBBi =   {
        -20,-10,-10,-10,-10,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5, 10, 10,  5,  0,-10,
        -10,  5,  5, 10, 10,  5,  5,-10,
        -10,  0, 10, 10, 10, 10,  0,-10,
        -10, 10, 10, 10, 10, 10, 10,-10,
        -10,  5,  0,  0,  0,  0,  5,-10,
        -20,-10,-10,-10,-10,-10,-10,-20};
    public final static byte[] modWBi = Helpers.mirror(modBBi);

    public final static byte[] modBRo =   {
         0,  0,  0,  0,  0,  0,  0,  0,
         5, 10, 10, 10, 10, 10, 10,  5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
         0,  0,  0,  5,  5,  0,  0,  0};
    public final static byte[] modWRo = Helpers.mirror(modBRo);

    public final static byte[] modBQu =   {
        -20,-10,-10, -5, -5,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5,  5,  5,  5,  0,-10,
         -5,  0,  5,  5,  5,  5,  0, -5,
          0,  0,  5,  5,  5,  5,  0, -5,
        -10,  5,  5,  5,  5,  5,  0,-10,
        -10,  0,  5,  0,  0,  0,  0,-10,
        -20,-10,-10, -5, -5,-10,-10,-20};
    public final static byte[] modWQu = Helpers.mirror(modBQu);

    //Mid game
    public final static byte[] modBKiMid =   {
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -20,-30,-30,-40,-40,-30,-30,-20,
        -10,-20,-20,-20,-20,-20,-20,-10,
         5 ,  5,  0,-25,-25,  0,  5,  5,
         0, 30, 10, -15, 0, -15, 30, 20};
    public final static byte[] modWKiMid = Helpers.mirror(modBKiMid);

    //End game
    public final static byte[] modBKiEnd =   {
        -50,-40,-15,-15,-15,-15,-40,-50,
        -30,-10,-10,  0,  0,-10,-10,-30,
        -10,-10, 15, 20, 20, 15,-10,-10,
         -0,-10, 20, 20, 20, 20,-10,  0,
         -0,-10, 20, 20, 20, 20,-10,  0,
        -10,-10, 15, 20, 20, 15,-10,-10,
        -30,-15,  0,  0,  0,  0,-15,-30,
        -50,-30,-15,-15,-15,-15,-30,-50};
    public final static byte[] modWKiEnd = Helpers.mirror(modBKiEnd);
}
