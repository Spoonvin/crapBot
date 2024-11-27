import java.util.ArrayList;

public class Helpers {

    //Prints a bitboard to the console
    public static void printBBoard(long b){
        for(int row = 7; row >= 0; row--){
            for(int col = 0; col < 8; col++){
                int square = row*8 + col;
                if((b & (1L << square)) != 0){
                    System.out.print(1);
                }else{
                    System.out.print(0);
                }
                if(square % 8 == 7) System.out.println();
            }
        }
    }

    // Helper function to check if the knight's move stays within bounds
    public static boolean isValidKnightMove(int square, int targetSquare, int delta) {
        if (targetSquare < 0 || targetSquare >= 64) return false;  // Check if out of bounds

        int currentRank = square / 8;
        int currentFile = square % 8;
        int targetRank = targetSquare / 8;
        int targetFile = targetSquare % 8;

        // Knights can only move to a square that's no more than 2 ranks and 2 files away
        int rankDiff = Math.abs(currentRank - targetRank);
        int fileDiff = Math.abs(currentFile - targetFile);

        return rankDiff <= 2 && fileDiff <= 2;
    }

    // Helper function to calculate moves in a single direction
    public static long shift(int square, int dx, int dy) {
        long mask = 0L;
        int rank = square / 8;  // Calculate rank (row)
        int file = square % 8;  // Calculate file (column)

        // Keep shifting in the given direction until we go off the board
        for (int i = 0; i < 7; i++) {
            rank += dy;
            file += dx;

            // Check if the move stays within the board bounds
            if (rank >= 0 && rank < 8 && file >= 0 && file < 8) {
                mask |= (1L << (rank * 8 + file));
            } else {
                break;
            }
        }
        return mask;
    }

    // Helper function to calculate moves in a single direction (with blocking pieces)
    public static long shift(int square, int dx, int dy, long occupied) {
        long mask = 0L;
        int rank = square / 8;  // Calculate rank (row)
        int file = square % 8;  // Calculate file (column)

        // Keep shifting in the given direction until we go off the board
        for (int i = 0; i < 7; i++) {
            rank += dy;
            file += dx;

            // Check if the move stays within the board bounds
            if (rank >= 0 && rank < 8 && file >= 0 && file < 8) {
                mask |= (1L << (rank * 8 + file));
            } else {
                break;
            }

            //Break when encountering a piece
            if(isOccupied(occupied, rank * 8 + file)) break;
        }
        return mask;
    }

    public static boolean isOccupied(long occupied, int pos){
        return (occupied & (0b1L << pos)) != 0;
    }
    public static boolean isOccupiedMask(long occupied, long posMask){
        return (occupied & posMask) != 0;
    }

    public static long hypQuiRookAttacks(long occupied, int s)
    {
        long binaryS=1L<<s;
        long possibilitiesHorizontal = (occupied - 2 * binaryS) ^ Long.reverse(Long.reverse(occupied) - 2 * Long.reverse(binaryS));
        long possibilitiesVertical = ((occupied&Constants.fileMasks8[s % 8]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(occupied&Constants.fileMasks8[s % 8]) - (2 * Long.reverse(binaryS)));
        return (possibilitiesHorizontal&Constants.rankMasks8[s / 8]) | (possibilitiesVertical&Constants.fileMasks8[s % 8]);
    }

    public static long hypQuiBishopAttacks(long occupied, int s)
    {
        long binaryS=1L<<s;
        long possibilitiesDiagonal = ((occupied&Constants.diagonalMasks8[(s / 8) + (s % 8)]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(occupied&Constants.diagonalMasks8[(s / 8) + (s % 8)]) - (2 * Long.reverse(binaryS)));

        long possibilitiesAntiDiagonal = ((occupied&Constants.antiDiagonalMasks8[(s / 8) + 7 - (s % 8)]) - (2 * binaryS)) ^ Long.reverse(Long.reverse(occupied&Constants.antiDiagonalMasks8[(s / 8) + 7 - (s % 8)]) - (2 * Long.reverse(binaryS)));

        return (possibilitiesDiagonal&Constants.diagonalMasks8[(s / 8) + (s % 8)]) | (possibilitiesAntiDiagonal&Constants.antiDiagonalMasks8[(s / 8) + 7 - (s % 8)]);
    }

    public static long[] genBiPossibleBlockSquares(){
        long[] bishopAttacks = new long[64];
        for(int square = 0; square < 64; square++){
            bishopAttacks[square] = Constants.bishopMoves[square];
            bishopAttacks[square] &= ~(0xFF | 0xFF00000000000000L | Constants.aFile | Constants.hFile); 
        }
        return bishopAttacks;
    }

    public static long[] genRoPossibleBlockSquares(){
        long[] rookAttacks = new long[64];

        
        for(int square = 0; square < 64; square++){
            long mask = 0L;
            rookAttacks[square] = Constants.rookMoves[square];

            if(square / 8 != 0){
            mask |= 0xFF; //Rank 0
            }
            if(square / 8 != 7){
                mask |= 0xFF00000000000000L; //Rank 7
            }
            if(square % 8 != 0){
                mask |= Constants.aFile;
            }
            if(square % 8 != 7){
                mask |= Constants.hFile;
            }

            rookAttacks[square] &= ~mask;
        }

        return rookAttacks;
    }

    public static long[] generateKingMoves(){
        long allMoves[] = new long[64];
        int[] kingDeltas = {
            8, -8,   // Up and Down
            1, -1,   // Right and Left
            9, 7,   // Top-right and Top-left
            -9, -7  // Bottom-right and Bottom-left
        }; // The king's possible move offsets (1 square in any direction)
        for(int square = 0; square < 64; square++){
            long cMoves = 0L;
            for (int delta : kingDeltas) {
                int targetSquare = square + delta;
                if (square % 8 == 0) {
                    if(delta == -1 || delta == 7 || delta == -9) continue;
                }
                if (square % 8 == 7) {
                    if(delta == 1 || delta == 9 || delta == -7) continue;
                }
                if(targetSquare >= 64 || targetSquare < 0) continue;
                cMoves |= (1L << targetSquare);
            }
            allMoves[square] = cMoves;
        }
        
        return allMoves;
    }

    public static long[] generateWPawnMoves(){
        long[] allMoves = new long[64];
        for(int square = 8; square < 56; square++){
            long cMoves = 0;
            
            cMoves |= (0b1L << (square+8));
            if(square % 8 != 0){
                cMoves |= (0b1L << (square+7));
            }
            if(square % 8 != 7){
                cMoves |= (0b1L << (square+9));
            }
            if(square < 16){
                cMoves |= (0b1L << (square+16));
            }

            allMoves[square] = cMoves;
        }
        return allMoves;
    }

    public static long[] generateBPawnMoves(){
        long[] allMoves = new long[64];
        for(int square = 8; square < 56; square++){
            long cMoves = 0;
            
            cMoves |= (0b1L << (square-8));
            if(square % 8 != 0){
                cMoves |= (0b1L << (square-9));
            }
            if(square % 8 != 7){
                cMoves |= (0b1L << (square-7));
            }
            if(square >= 48){
                cMoves |= (0b1L << (square-16));
            }

            allMoves[square] = cMoves;
        }
        return allMoves;
    }

    public static long[] generateWPawnAttacks() {
        long[] allMoves = new long[64];
        for(int square = 8; square < 56; square++){
            long cMoves = 0;

            if(square % 8 != 0){
                cMoves |= (0b1L << (square+7));
            }
            if(square % 8 != 7){
                cMoves |= (0b1L << (square+9));
            }
            if(square < 16){
                cMoves |= (0b1L << (square+16));
            }

            allMoves[square] = cMoves;
        }
        return allMoves;
    }

    public static long[] generateBPawnAttacks() {
        long[] allMoves = new long[64];
        for(int square = 8; square < 56; square++){
            long cMoves = 0;
            
            if(square % 8 != 0){
                cMoves |= (0b1L << (square-9));
            }
            if(square % 8 != 7){
                cMoves |= (0b1L << (square-7));
            }
            if(square >= 48){
                cMoves |= (0b1L << (square-16));
            }

            allMoves[square] = cMoves;
        }
        return allMoves;
    }

    public static long[] generateBishopMoves(){
        long[] allMoves = new long[64];
        for(int square = 0; square < 64; square ++){
            allMoves[square] = Helpers.shift(square, 1, 1) | Helpers.shift(square, 1, -1) | 
                Helpers.shift(square, -1, 1) | Helpers.shift(square, -1, -1);
        }
        return allMoves;
    }

    public static long[] generateRookMoves(){
        long[] allMoves = new long[64];
        for(int square = 0; square < 64; square ++){
            allMoves[square] = Helpers.shift(square, 1, 0) | Helpers.shift(square,-1, 0) | 
                Helpers.shift(square, 0, 1) | Helpers.shift(square, 0, -1);
        }
        return allMoves;
    }

    public static long[] generateQueenMoves(){
        long[] bMoves = generateBishopMoves();
        long[] rMoves = generateRookMoves();
        long[] allMoves = new long[64];
        for(int square = 0; square < 64; square++){
            allMoves[square] = bMoves[square] | rMoves[square];
        }
        return allMoves;
    }

    public static long[] generateKnightMoves(){
        long[] allMoves = new long[64];
        int[] knightDeltas = {
            17, 15, 10, 6, -17, -15, -10, -6
        }; // The knight's possible move offsets (accounting for the "L" shape)
        for(int square = 0; square < 64; square++){
            long cMoves = 0L;
    
            for (int delta : knightDeltas) {
                int targetSquare = square + delta;
                if (Helpers.isValidKnightMove(square, targetSquare, delta)) {
                    cMoves |= (1L << targetSquare);
                }
            }
            allMoves[square] = cMoves;
        }
        return allMoves;
    }

    //Given a piece move mask, return an array with all different combinations of blocking pieces on its path
    public static long[] generateAllBlockingCombos(long mask){
        int bitCount = Long.bitCount(mask);
        int[] onePositions = getBitPositions(mask, bitCount);
        int length = (int)Math.pow(2, bitCount);
        long[] allCombos = new long[length];
        //Use number of ones to calculate amount of variations
        for(long comb = 0; comb < length; comb++){
            long blocking = 0;
            for(int i = 0; i < 64; i++){
                if(isOccupied(comb, i)){
                    blocking |= 1L << onePositions[i];
                }
            }
            allCombos[(int)comb] = blocking;
        }
        return allCombos;
    }
    //Helper for generateAllBlockingCombos. Gives array with positions of one the one bits in mask.
    private static int[] getBitPositions(long mask, int ones){
        int[] bitPos = new int[Long.bitCount(mask)];
        int idxCount = 0;
        for(int i = 0; i < 64; i++){
            if(isOccupied(mask, i)){
                bitPos[idxCount++] = i;
            }
        }
        return bitPos;
    }

    //Init magic lookup table
    public static long[][] initRookMagicLookup(){
        long[][] lookup = new long[64][4096];
        for(int square = 0; square < 64; square++){
            long[] blockCombos = generateAllBlockingCombos(Constants.rookPossibleBlockSquares[square]);
            long magicNum = Constants.rookMagics[square];
            for(long combo : blockCombos){
                int mIndex = (int)((combo*magicNum) >>> (64-Constants.rookRellevantBits[square]));
                lookup[square][mIndex] = hypQuiRookAttacks(combo, square);
            }
        }
        return lookup;
    }
    //Init magic lookup table
    public static long[][] initBishopMagicLookup(){
        long[][] lookup = new long[64][512];
        for(int square = 0; square < 64; square++){
            long[] blockCombos = generateAllBlockingCombos(Constants.bishopPossibleBlockSquares[square]);
            long magicNum = Constants.bishopMagics[square];
            for(long combo : blockCombos){
                int mIndex = (int)((magicNum*combo) >>> (64-Constants.bishopRellevantBits[square]));
                lookup[square][mIndex] = hypQuiBishopAttacks(combo, square);
            }
        }
        return lookup;
    }

    //MoveGen using magic numbers
    public static long getRookMovesMagic(long blockers, int square){
        blockers &= Constants.rookPossibleBlockSquares[square];
        int idx = (int)((Constants.rookMagics[square]*blockers) >>> (64-Constants.rookRellevantBits[square]));
        return Constants.rookMagicLookup[square][idx];
    }

    //MoveGen using magic numbers
    public static long getBishopMovesMagic(long blockers, int square){
        blockers &= Constants.bishopPossibleBlockSquares[square];
        int idx = (int)((Constants.bishopMagics[square]*blockers) >>> (64-Constants.bishopRellevantBits[square]));
        return Constants.bishopMagicLookup[square][idx];
    }

    //MoveGen using magic numbers
    public static long getQueenMovesMagic(long blockers, int square){
        return getRookMovesMagic(blockers, square) | getBishopMovesMagic(blockers, square);
    }

    //Add white pawn moves to move list, except en passant
    public static void addWPawnMovesToList(Board board, ArrayList<Move> moves, long occupancy, long enemy, int square){
        if(square >= 56) System.out.println("Something is wrong! Pawn on last row.");

        boolean promotion = false;
        if(square >= 48) promotion = true;

        if(!isOccupied(occupancy, square+8)){
            moves.add(new Move(square, square+8, MoveType.QUIET, promotion));
        }
        if(square % 8 != 0 && isOccupied(enemy, square+7)){
            int moveVal = board.getValueOnSquare(square+7) - Constants.pawnVal;
            moves.add(new Move(square, square+7, MoveType.CAP, promotion, moveVal));
        }
        if(square % 8 != 7 && isOccupied(enemy, square+9)){
            int moveVal = board.getValueOnSquare(square+9) - Constants.pawnVal;
            moves.add(new Move(square, square+9, MoveType.CAP, promotion, moveVal));
        }
        if(square < 16 && !isOccupied(occupancy, square+8) && !isOccupied(occupancy, square+16)){
            moves.add(new Move(square, square+16, MoveType.PAWNPUSH, promotion));
        }
    }

    //Add white pawn attacks and promotion to move list
    public static void addWPawnNonQuietMovesToList(ArrayList<Move> moves, long occupancy, long enemy, int square){
        if(square >= 56) System.out.println("Something is wrong! Pawn on last row.");

        boolean promotion = false;
        if(square >= 48) promotion = true;

        if(!isOccupied(occupancy, square+8) && promotion){
            moves.add(new Move(square, square+8, MoveType.QUIET, promotion));
        }
        if(square % 8 != 0 && isOccupied(enemy, square+7)){
            moves.add(new Move(square, square+7, MoveType.CAP, promotion));
        }
        if(square % 8 != 7 && isOccupied(enemy, square+9)){
            moves.add(new Move(square, square+9, MoveType.CAP, promotion));
        }
    }

    //Add black pawn moves to move list, except en passant
    public static void addBPawnMovesToList(Board board, ArrayList<Move> moves, long occupancy, long enemy, int square){
        if(square < 8) System.out.println("Something is wrong! Pawn on last row.");

        boolean promotion = false;
        if(square < 16) promotion = true;

        if(!isOccupied(occupancy, square-8)){
            moves.add(new Move(square, square-8, MoveType.QUIET, promotion));
        }
        if(square % 8 != 0 && isOccupied(enemy, square-9)){
            int moveVal = board.getValueOnSquare(square-9) - Constants.pawnVal;
            moves.add(new Move(square, square-9, MoveType.CAP, promotion, moveVal));
        }
        if(square % 8 != 7 && isOccupied(enemy, square-7)){
            int moveVal = board.getValueOnSquare(square-7) - Constants.pawnVal;
            moves.add(new Move(square, square-7, MoveType.CAP, promotion, moveVal));
        }
        if(square >= 48 && !isOccupied(occupancy, square-8) && !isOccupied(occupancy, square-16)){
            moves.add(new Move(square, square-16, MoveType.PAWNPUSH, promotion));
        }
    }

    //Add black pawn caps and promotion to move list, except en passant
    public static void addBPawnNonQuietMovesToList(ArrayList<Move> moves, long occupancy, long enemy, int square){
        if(square < 8) System.out.println("Something is wrong! Pawn on last row.");

        boolean promotion = false;
        if(square < 16) promotion = true;

        if(!isOccupied(occupancy, square-8) && promotion){
            moves.add(new Move(square, square-8, MoveType.QUIET, promotion));
        }
        if(square % 8 != 0 && isOccupied(enemy, square-9)){
            moves.add(new Move(square, square-9, MoveType.CAP, promotion));
        }
        if(square % 8 != 7 && isOccupied(enemy, square-7)){
            moves.add(new Move(square, square-7, MoveType.CAP, promotion));
        }
    }

    //Mirrors a 64 element byte array
    public static byte[] mirror(byte[] arr){
        byte[] aMir = new byte[64];
        for(int i = 0; i < 64; i++){
            if(i < 8){
                aMir[63-(7-i)] = arr[i];
            } else if(i < 16){
                aMir[55 -(7-(i%8))] = arr[i];
            } else if(i < 24){
                aMir[47 -(7-(i%8))] = arr[i];
            } else if(i < 32){
                aMir[39 -(7-(i%8))] = arr[i];
            } else if(i < 40){
                aMir[31 -(7-(i%8))] = arr[i];
            } else if(i < 48){
                aMir[23 -(7-(i%8))] = arr[i];
            } else if(i < 56){
                aMir[15 -(7-(i%8))] = arr[i];
            } else if(i < 64){
                aMir[7 -(7-(i%8))] = arr[i];
            }
        }
        return aMir;
    }

    public static String boardToFen(Board board){
        StringBuilder fen = new StringBuilder(25);

        long wPawns = board.getPieceType(PieceType.WPAWN);
        long bPawns = board.getPieceType(PieceType.BPAWN);
        long wKnigths = board.getPieceType(PieceType.WKNIGHT);
        long bKnigths = board.getPieceType(PieceType.BKNIGHT);
        long wBishops = board.getPieceType(PieceType.WBISHOP);
        long bBishops = board.getPieceType(PieceType.BBISHOP);
        long wRooks = board.getPieceType(PieceType.WROOK);
        long bRooks = board.getPieceType(PieceType.BROOK);
        long wQueens = board.getPieceType(PieceType.WQUEEN);
        long bQueens = board.getPieceType(PieceType.BQUEEN);
        long wKing = board.getPieceType(PieceType.WKING);
        long bKing = board.getPieceType(PieceType.BKING);

        for(int row = 7; row >= 0; row--){
            int emptySquares = 0;
            for(int col = 0; col < 8; col++){
                byte square = (byte)(row*8 + col);

                if(!isOccupied(board.getAllPieces(), square)){
                    emptySquares++;
                    continue;
                }

                if(emptySquares != 0){
                    fen.append(emptySquares);
                    emptySquares = 0;
                }

                if(isOccupied(wPawns, square)){
                    fen.append("P");
                } else if(isOccupied(bPawns, square)){
                    fen.append("p");
                } else if(isOccupied(wKnigths, square)){
                    fen.append("N");
                } else if(isOccupied(bKnigths, square)){
                    fen.append("n");
                } else if(isOccupied(wBishops, square)){
                    fen.append("B");
                } else if(isOccupied(bBishops, square)){
                    fen.append("b");
                } else if(isOccupied(wRooks, square)){
                    fen.append("R");
                } else if(isOccupied(bRooks, square)){
                    fen.append("r");
                } else if(isOccupied(wQueens, square)){
                    fen.append("Q");
                } else if(isOccupied(bQueens, square)){
                    fen.append("q");
                } else if(isOccupied(wKing, square)){
                    fen.append("K");
                } else if(isOccupied(bKing, square)){
                    fen.append("k");
                }
            }
            if(emptySquares != 0){
                fen.append(emptySquares);
            }
            if(row != 0){
                fen.append("/");
            }
            
        }

        if(board.getIsWhiteToPlay()){
            fen.append(" w ");
        }else{
            fen.append(" b ");
        }

        boolean[] castlingRights = board.getCastlingRights();

        if(castlingRights[0]){
            fen.append("K");
        }
        if(castlingRights[1]){
            fen.append("Q");
        }
        if(castlingRights[2]){
            fen.append("k");
        }
        if(castlingRights[3]){
            fen.append("q");
        }

        fen.append(" -");

        return fen.toString();
    }

    public static void setBoardByFen(Board board, String fen){
        board.empty();
        String[] fenSections = fen.split(" ");
        int square = 0;
        for(char c : fenSections[0].toCharArray()){
            if(Character.isDigit(c)){
                square += ((int)c)-48;
            }else{
                board.setPieceOnSquare(c, square);
                square++;
            }   
        }
        board.setIsWhiteToPlay(fenSections[1] == "w");

        boolean[] castlingRights = {false, false, false, false}; //KQkq
        for(char c : fenSections[2].toCharArray()){
            switch(c){
                case 'K':
                    castlingRights[0] = true;
                    break;
                case 'Q':
                    castlingRights[1] = true;
                    break;
                case 'k':
                    castlingRights[2] = true;
                    break;
                case 'q':
                    castlingRights[3] = true;
                    break;
                default:
                    break;
            }
        }
        board.setCastlingRights(castlingRights);

        String enPassantTargetSquare = fenSections[3];
        if(enPassantTargetSquare != "-"){
            int targetSquare = stringCoordToSquare(enPassantTargetSquare);
            if(targetSquare/8 == 2){
                board.setPawnPush(targetSquare+8);
            }else{
                board.setPawnPush(targetSquare-8);
            }
        }

        int fiftyMoveRule = Integer.parseInt(fenSections[4]);
        board.setMovesSinceLastCap(fiftyMoveRule);

        board.reGenerateZobrist();
    }

    //Sorts moves by move value. m1 on right in comparator because i want largest number first.
    public static void orderMoves(ArrayList<Move> moves){
        moves.sort((m1, m2) -> Integer.compare(m2.moveValue, m1.moveValue));
    }

    public static void orderMovesWithKiller(ArrayList<Move> moves, Move killer){
        if(killer != null){
            int kIdx = moves.indexOf(killer);
            if(kIdx != -1){
                moves.get(kIdx).moveValue = -1; //Killer will be placed after good captures (target - self >= 0) and before quiet moves
            }
        }
        moves.sort((m1, m2) -> Integer.compare(m2.moveValue, m1.moveValue));
    }

    public static void moveToHead(ArrayList<Move> moves, Move head){
        if(moves.remove(head)) moves.add(0, head);
    }

    public static int totalPointOnBoard(Board board){
        int sum = 0;
        sum += Long.bitCount(board.getPieceType(PieceType.WPAWN))*Constants.pawnVal;
        sum += Long.bitCount(board.getPieceType(PieceType.BPAWN))*Constants.pawnVal;
        sum += Long.bitCount(board.getPieceType(PieceType.WKNIGHT))*Constants.knightVal;
        sum += Long.bitCount(board.getPieceType(PieceType.BKNIGHT))*Constants.knightVal;
        sum += Long.bitCount(board.getPieceType(PieceType.WBISHOP))*Constants.bishopVal;
        sum += Long.bitCount(board.getPieceType(PieceType.BBISHOP))*Constants.bishopVal;
        sum += Long.bitCount(board.getPieceType(PieceType.WROOK))*Constants.rookVal;
        sum += Long.bitCount(board.getPieceType(PieceType.BROOK))*Constants.rookVal;
        sum += Long.bitCount(board.getPieceType(PieceType.WQUEEN))*Constants.queenVal;
        sum += Long.bitCount(board.getPieceType(PieceType.BQUEEN))*Constants.queenVal;
        return sum;
    }
    
    //Calculate how far into the game it is. Values from 0 to 1.
    public static double endGameRatio(Board board){
        //Starting points is aprox. 7880
        //Count endgame if around half of that
        int totPoints = totalPointOnBoard(board);
        if(totPoints <= 4200) return 1;
        if(totPoints >= 7880) return 0;
        return (double)(7880 - totPoints)/4200;
    }

    public static int passedPawnsBonus(long friendPawns, long enemyPawns, boolean isWhite){
        int totalBonus = 0;
        for(byte i = 0; i < 64; i++){
            if(isOccupied(friendPawns, i)){
                if(!isPassedPawn(enemyPawns, i, isWhite)) continue;
                int rank = i/8;
                if(isWhite){
                    totalBonus += Constants.passedPawnRankMod[rank];
                }else{
                    totalBonus += Constants.passedPawnRankMod[7-rank];
                }
            }
        }
        return totalBonus;
    }
    private static boolean isPassedPawn(long enemyPawns, int square, boolean isWhite){
        long mask = 0xFFFFFFFFFFFFFFFFL;
        int file = square % 8;
        int rank = square / 8;

        if(isWhite){
            mask = mask << ((rank+1)*8);
        }else{
            mask = mask >>> ((7 - rank + 1)*8);
        }

        long fileMask = Constants.files[file];
        if(file+1 < 8){
            fileMask |= Constants.files[file+1];
        }
        if(file-1 >= 0){
            fileMask |= Constants.files[file-1];
        }

        mask &= fileMask;

        return (enemyPawns & mask) == 0;
    }
    public static int stackedpawnsPenalty(long pawns){
        int count = 0;
        for(int i = 0; i < 8; i++){
            long copy = pawns;
            copy &= Constants.fileMasks8[i];
            int sPwns = Long.bitCount(copy) - 1; 
            count += sPwns;
        }
        return count*Constants.stackedPawnPenalty;
    }
    
    //Only use in opening and mid game
    public static int pawnShieldModifier(long king, long pawns, boolean isWhite){
        //Todo: use king move map ored with movemap shifted up. Remember to weigh based on time in game
        int kingCoord = Long.numberOfTrailingZeros(king);
        int kingRank = kingCoord/8;
        long shieldMap = Constants.kingMoves[kingCoord];
        int[] shieldModifiers = Constants.pawnShieldModifiers;

        if(isWhite){
            shieldMap |= (shieldMap << 8);
        }else{
            shieldMap |= (shieldMap >>> 8);
        }
        
        long noPassedRanksMask = 0xFFFFFFFFFFFFFFFFL;
        if(isWhite){
            noPassedRanksMask = noPassedRanksMask << (kingRank*8);
        }else{
            noPassedRanksMask = noPassedRanksMask >>> ((7-kingRank)*8);
        }
        
        shieldMap &= noPassedRanksMask; //Removes passed ranks
        long closePawns = shieldMap & pawns;
        int numPawnsInShield = Long.bitCount(closePawns);

        if(numPawnsInShield < shieldModifiers.length){
            return shieldModifiers[numPawnsInShield];
        }else{
            return shieldModifiers[shieldModifiers.length-1];
        }
    }

    public static boolean isValidCoordinate(int coord){
        return coord >= 0 && coord < 64;
    }

    public static int stringCoordToSquare(String coord){
        int squareFile = (int)coord.charAt(0)-97;
        int squareRank = (int)coord.charAt(1)-49;
        return squareRank*8+squareFile;
    }

    public static Move parseFromMoveNotation(String sMove, Board board){
        int from = stringCoordToSquare(sMove.substring(0, 2));
        int to = stringCoordToSquare(sMove.substring(2, 4));

        char promoteTo = 'x';
        boolean promotion = false;
        if(sMove.length() > 4){
            promoteTo = sMove.charAt(4);
            promotion = true;
        }

        PieceType movingPiece = board.getPieceOnSquare(from);
        PieceType capturedPiece = board.getPieceOnSquare(to);

        MoveType moveType;
        int moveDiff = to-from;
        if(capturedPiece != PieceType.NONE){
            moveType = MoveType.CAP;
        }else if(movingPiece == PieceType.WKING || movingPiece == PieceType.BKING){
            if((moveDiff) == 2){
                moveType = MoveType.RCASTLE;
            }else if((moveDiff) == -2){
                moveType = MoveType.LCASTLE;
            }else{
                moveType = MoveType.QUIET;
            }
        }else if(movingPiece == PieceType.WPAWN || movingPiece == PieceType.BPAWN){
            if(Math.abs(moveDiff) == 16){
                moveType = MoveType.PAWNPUSH;
            }else if((capturedPiece == PieceType.NONE) && (Math.abs(moveDiff) == 7 || (Math.abs(moveDiff) == 9))){
                moveType = MoveType.ENPAS;
            }else{
                moveType = MoveType.QUIET;
            }
        }else{
            moveType = MoveType.QUIET;
        }
        
        Move move = new Move(from, to, moveType, promotion);
        move.promoteTo = promoteTo;
        return move;
    }

    public static String parseToMoveNotation(Move move){
        char fromFile = (char)((move.from % 8) + 97);
        char toFile = (char)((move.to % 8) + 97);
        char fromRank = (char)((move.from / 8) + 49);
        char toRank = (char)((move.to / 8) + 49);

        String sMove = "" + fromFile + fromRank + toFile + toRank;
        if(move.promotion){
            sMove = sMove + move.promoteTo;
        }

        return sMove;
    }

    public static int calculateBotThinkTime(int time, int timeInc){
        if(time < 60000){
            return 800 + timeInc;
        }else if(time < 120000){
            return 1500 + timeInc;
        }else if(time < 300000){ //5 min
            return 4000 + timeInc;
        }else if(time < 600000){ //10 min
            return 11000 + timeInc;
        }else{
            return 15000 + timeInc;
        }
    }
}
