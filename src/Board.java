import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

public class Board {
    private long wPawns;
    private long bPawns;
    private long wBishops;
    private long bBishops;
    private long wKnights;
    private long bKnights;
    private long wRooks;
    private long bRooks;
    private long wQueens;
    private long bQueens;
    private long wKing;
    private long bKing;

    private boolean wCanCastleRight;
    private boolean wCanCastleLeft;
    private boolean bCanCastleRight;
    private boolean bCanCastleLeft;

    private boolean isWhiteToPlay;

    private int pawnPush; //-1 no pawnpush

    private int fiftyMoveRuleCount;
    private RepetitionBuffer repetitionBuffer;

    private long zobristKey;

    private Deque<GameState> gameStates;

    //Init the board
    public Board(){
        this.wPawns = 0x000000000000ff00L;
        this.bPawns = 0x00ff000000000000L;
        this.wKnights = 0x0000000000000042L;
        this.bKnights = 0x4200000000000000L;
        this.wBishops = 0x0000000000000024L;
        this.bBishops = 0x2400000000000000L;
        this.wRooks = 0x0000000000000081L;
        this.bRooks = 0x8100000000000000L;
        this.wQueens = 0x0000000000000008L;
        this.bQueens = 0x0800000000000000L;
        this.wKing = 0x0000000000000010L;
        this.bKing = 0x1000000000000000L;

        this.wCanCastleRight = true;
        this.wCanCastleLeft = true;
        this.bCanCastleRight = true;
        this.bCanCastleLeft = true;

        this.isWhiteToPlay = true;

        this.pawnPush = -1;

        this.fiftyMoveRuleCount = 0;
        this.repetitionBuffer = new RepetitionBuffer(70); //Use a deque instead? linked?
        

        this.zobristKey = Zobrist.generateZobristKey(this);
        this.repetitionBuffer.add(this.zobristKey);

        this.gameStates = new LinkedList<GameState>();
    }

    public Board(int test){
        this.wPawns = 0b00001000010001001010000100000000L;
        this.wBishops = 0x20004000L;
        this.wKnights = 0x00200000;
        this.wRooks = 0x22;
        this.wQueens = 0x1000000;
        this.wKing = 0x40;

        this.bPawns = (1L << 49) | (1L << 53) | (1L << 54) | (1L << 42) | (1L << 47) | (1L << 35);
        this.bBishops = (1L << 30);
        this.bKnights = (1L << 52) | (1L << 40);
        this.bRooks = (1L << 63) | (1L << 60);
        this.bQueens = (1L << 41);
        this.bKing = (1L << 56);

        this.wCanCastleRight = false;
        this.wCanCastleLeft = false;
        this.bCanCastleRight = false;
        this.bCanCastleLeft = false;

        this.isWhiteToPlay = false;

        this.pawnPush = -1;

        this.fiftyMoveRuleCount = 0;
        this.repetitionBuffer = new RepetitionBuffer(40); //Should be enough
        

        this.zobristKey = Zobrist.generateZobristKey(this);
        this.repetitionBuffer.add(this.zobristKey);

        this.gameStates = new LinkedList<GameState>();
    }

    public void reGenerateZobrist(){
        this.zobristKey = Zobrist.generateZobristKey(this);
    }

    public long getZobristKey(){
        return this.zobristKey;
    }

    public boolean getIsWhiteToPlay(){
        return this.isWhiteToPlay;
    }
    
    public long getPieceType(PieceType pT){
        switch (pT){
            case BBISHOP:
                return this.bBishops;
            case BKING:
                return this.bKing;
            case BKNIGHT:
                return this.bKnights;
            case BPAWN:
                return this.bPawns;
            case BQUEEN:
                return this.bQueens;
            case BROOK:
                return this.bRooks;
            case WBISHOP:
                return this.wBishops;
            case WKING:
                return this.wKing;
            case WKNIGHT:
                return this.wKnights;
            case WPAWN:
                return this.wPawns;
            case WQUEEN:
                return this.wQueens;
            case WROOK:
                return this. wRooks;
            default:
                return 0;

        }
    }

    public void setPieceOnSquare(char piece, int square){
        long mask = 1L << square;
        switch(piece){
            case 'b':
                this.bBishops |= mask;
                updateZobrist(square, Zobrist.bBiZob);
                break;
            case 'k':
                this.bKing |= mask;
                updateZobrist(square, Zobrist.bKiZob);
                break;
            case 'n':
                this.bKnights |= mask;
                updateZobrist(square, Zobrist.bKnZob);
                break;
            case 'p':
                this.bPawns |= mask;
                updateZobrist(square, Zobrist.bPaZob);
                break;
            case 'q':
                this.bQueens |= mask;
                updateZobrist(square, Zobrist.bQuZob);
                break;
            case 'r':
                this.bRooks |= mask;
                updateZobrist(square, Zobrist.bRoZob);
                break;
            case 'B':
                this.wBishops |= mask;
                updateZobrist(square, Zobrist.wBiZob);
                break;
            case 'K':
                this.wKing |= mask;
                updateZobrist(square, Zobrist.wKiZob);
                break;
            case 'N':
                this.wKnights |= mask;
                updateZobrist(square, Zobrist.wKnZob);
                break;
            case 'P':
                this.wPawns |= mask;
                updateZobrist(square, Zobrist.wPaZob);
                break;
            case 'Q':
                this.wQueens |= mask;
                updateZobrist(square, Zobrist.wQuZob);
                break;
            case 'R':
                this.wRooks |= mask;
                updateZobrist(square, Zobrist.wRoZob);
                break;
            default:
                break;
            
        }
    }

    public void setIsWhiteToPlay(boolean isWhiteToPLay){
        this.isWhiteToPlay = isWhiteToPLay;
    }

    public void setPawnPush(int square){
        this.pawnPush = square;
    }

    public void setMovesSinceLastCap(int moves){
        this.fiftyMoveRuleCount = moves;
    }

    public void setCastlingRights(boolean[] cR){
        this.wCanCastleRight = cR[0];
        this.wCanCastleLeft = cR[1];
        this.bCanCastleRight = cR[2];
        this.bCanCastleLeft = cR[3];
    }

    public PieceType getPieceOnSquare(int pos){
        long posMask = 0b1L << pos;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WPAWN), posMask)) return PieceType.WPAWN;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BPAWN), posMask)) return PieceType.BPAWN;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WBISHOP), posMask)) return PieceType.WBISHOP;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BBISHOP), posMask)) return PieceType.BBISHOP;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WKNIGHT), posMask)) return PieceType.WKNIGHT;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BKNIGHT), posMask)) return PieceType.BKNIGHT;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WROOK), posMask)) return PieceType.WROOK;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BROOK), posMask)) return PieceType.BROOK;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WQUEEN), posMask)) return PieceType.WQUEEN;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BQUEEN), posMask)) return PieceType.BQUEEN;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WKING), posMask)) return PieceType.WKING;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BKING), posMask)) return PieceType.BKING; 
        return PieceType.NONE;
    }

    public PieceType getPieceValueOnSquare(int pos){
        long posMask = 0b1L << pos;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WPAWN), posMask)) return PieceType.WPAWN;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BPAWN), posMask)) return PieceType.BPAWN;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WBISHOP), posMask)) return PieceType.WBISHOP;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BBISHOP), posMask)) return PieceType.BBISHOP;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WKNIGHT), posMask)) return PieceType.WKNIGHT;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BKNIGHT), posMask)) return PieceType.BKNIGHT;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WROOK), posMask)) return PieceType.WROOK;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BROOK), posMask)) return PieceType.BROOK;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WQUEEN), posMask)) return PieceType.WQUEEN;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BQUEEN), posMask)) return PieceType.BQUEEN;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.WKING), posMask)) return PieceType.WKING;
        if(Helpers.isOccupiedMask(getPieceType(PieceType.BKING), posMask)) return PieceType.BKING; 
        return PieceType.NONE;
    }

    public int getPawnPush(){
        return this.pawnPush;
    }

    //Returns pseudo legal moves. Need to check castling and king safety
    public ArrayList<Move> getPseudoLegalMoves(){
        ArrayList<Move> moves = new ArrayList<Move>(50); //What is avg num of pseudo moves?
        long enemyPieces = getPiecesByColor(!this.isWhiteToPlay);
        long friendlyPieces = getPiecesByColor(this.isWhiteToPlay);
        long allPieces = getAllPieces();

        for(int square = 0; square < 64; square++){
            PieceType piece = getPieceOnSquare(square);
            //If there is no piece or the piece is an enemy, we can continue the loop
            if(piece == PieceType.NONE || !Helpers.isOccupied(friendlyPieces, square)) continue;

            if(piece == PieceType.WKNIGHT || piece == PieceType.BKNIGHT){
                long knMoves = Constants.knightMoves[square] & ~(friendlyPieces);
                for(int to = 0; to < 64; to++){
                    if(!Helpers.isOccupied(knMoves, to)) continue;
                    if(Helpers.isOccupied(enemyPieces, to)){
                        int mVal = getValueOnSquare(to) - Constants.knightVal;
                        moves.add(new Move(square, to, MoveType.CAP, mVal));
                    }else{
                        moves.add(new Move(square, to, MoveType.QUIET));
                    }
                }
            }else if(piece == PieceType.WKING || piece == PieceType.BKING){
                long kiMoves = Constants.kingMoves[square] & ~(friendlyPieces);
                for(int to = 0; to < 64; to++){
                    if(!Helpers.isOccupied(kiMoves, to)) continue;
                    if(Helpers.isOccupied(enemyPieces, to)){
                        int mVal = getValueOnSquare(to) - Constants.kingVal;
                        moves.add(new Move(square, to, MoveType.CAP, mVal));
                    }else{
                        moves.add(new Move(square, to, MoveType.QUIET));
                    }
                }
                if(this.isWhiteToPlay){
                    if(this.wCanCastleLeft && !Helpers.isOccupiedMask(allPieces, Constants.wCastleLeftPath)){
                        moves.add(new Move(square, square-2, MoveType.LCASTLE));
                    }
                    if(this.wCanCastleRight && !Helpers.isOccupiedMask(allPieces, Constants.wCastleRightPath)){
                        moves.add(new Move(square, square+2, MoveType.RCASTLE));
                    }
                }else{
                    if(this.bCanCastleLeft && !Helpers.isOccupiedMask(allPieces, Constants.bCastleLeftPath)){
                        moves.add(new Move(square, square-2, MoveType.LCASTLE));
                    }
                    if(this.bCanCastleRight && !Helpers.isOccupiedMask(allPieces, Constants.bCastleRightPath)){
                        moves.add(new Move(square, square+2, MoveType.RCASTLE));
                    }
                }
            }else if(piece == PieceType.WPAWN){
                Helpers.addWPawnMovesToList(this, moves, allPieces, enemyPieces, square);
                //En Passant check
                if(this.pawnPush == -1) continue;
                if(square % 8 != 0){
                    int mVal = getValueOnSquare(square+7) - Constants.pawnVal;
                    if(square-1 == this.pawnPush) moves.add(new Move(square, square+7, MoveType.ENPAS, mVal));
                }
                if(square % 8 != 7){
                    int mVal = getValueOnSquare(square+9) - Constants.pawnVal;
                    if(square+1 == this.pawnPush) moves.add(new Move(square, square+9, MoveType.ENPAS, mVal));
                }
            }else if(piece == PieceType.BPAWN){
                Helpers.addBPawnMovesToList(this, moves, allPieces, enemyPieces, square);
                //En Passant check
                if(this.pawnPush == -1) continue;
                if(square % 8 != 0){
                    int mVal = getValueOnSquare(square-9) - Constants.pawnVal;
                    if(square-1 == this.pawnPush) moves.add(new Move(square, square-9, MoveType.ENPAS, mVal));
                }
                if(square % 8 != 7){
                    int mVal = getValueOnSquare(square-7) - Constants.pawnVal;
                    if(square+1 == this.pawnPush) moves.add(new Move(square, square-7, MoveType.ENPAS, mVal));
                }
            }else if(piece == PieceType.WBISHOP || piece == PieceType.BBISHOP){
                long biMoves = Helpers.getBishopMovesMagic(allPieces, square);
                biMoves &= ~friendlyPieces; //Cant capture friendly
                for(int i = 0; i < 64; i++){
                    if(!Helpers.isOccupied(biMoves, i)) continue;
                    if(Helpers.isOccupied(enemyPieces, i)){
                        int mVal = getValueOnSquare(i) - Constants.bishopVal;
                        moves.add(new Move(square, i, MoveType.CAP, mVal));
                    }else{
                        moves.add(new Move(square, i, MoveType.QUIET));
                    }
                }
            }else if(piece == PieceType.WROOK || piece == PieceType.BROOK){
                long roMoves = Helpers.getRookMovesMagic(allPieces, square);
                roMoves &= ~friendlyPieces; //Cant capture friendly
                for(int i = 0; i < 64; i++){
                    if(!Helpers.isOccupied(roMoves, i)) continue;
                    if(Helpers.isOccupied(enemyPieces, i)){
                        int mVal = getValueOnSquare(i) - Constants.rookVal;
                        moves.add(new Move(square, i, MoveType.CAP, mVal));
                    }else{
                        moves.add(new Move(square, i, MoveType.QUIET));
                    }
                }
            }else if(piece == PieceType.WQUEEN || piece == PieceType.BQUEEN){
                long quMoves = Helpers.getQueenMovesMagic(allPieces, square);
                quMoves &= ~friendlyPieces; //Cant capture friendly
                for(int i = 0; i < 64; i++){
                    if(!Helpers.isOccupied(quMoves, i)) continue;
                    if(Helpers.isOccupied(enemyPieces, i)){
                        int mVal = getValueOnSquare(i) - Constants.queenVal;
                        moves.add(new Move(square, i, MoveType.CAP,mVal));
                    }else{
                        moves.add(new Move(square, i, MoveType.QUIET));
                    }
                }
            }
        }
        return moves;
    }

    public ArrayList<Move> getNonQuietMoves(){
        ArrayList<Move> moves = new ArrayList<Move>(12); //What is avg num of non quiet moves?
        long enemyPieces = getPiecesByColor(!this.isWhiteToPlay);
        long friendlyPieces = getPiecesByColor(this.isWhiteToPlay);
        long allPieces = getAllPieces();

        for(int square = 0; square < 64; square++){
            PieceType piece = getPieceOnSquare(square);
            //If there is no piece or the piece is an enemy, we can continue the loop
            if(piece == PieceType.NONE || !Helpers.isOccupied(friendlyPieces, square)) continue;

            if(piece == PieceType.WKNIGHT || piece == PieceType.BKNIGHT){
                long knMoves = Constants.knightMoves[square] & ~(friendlyPieces);
                knMoves &= enemyPieces; //Get only captures
                for(int to = 0; to < 64; to++){
                    if(!Helpers.isOccupied(knMoves, to)) continue;
                    moves.add(new Move(square, to, MoveType.CAP));
                }
            }else if(piece == PieceType.WKING || piece == PieceType.BKING){
                long kiMoves = Constants.kingMoves[square] & ~(friendlyPieces);
                kiMoves &= enemyPieces;
                for(int to = 0; to < 64; to++){
                    if(!Helpers.isOccupied(kiMoves, to)) continue;
                    moves.add(new Move(square, to, MoveType.CAP));
                }
            }else if(piece == PieceType.WPAWN){
                Helpers.addWPawnNonQuietMovesToList(moves, allPieces, enemyPieces, square);
                //En Passant check
                if(this.pawnPush == -1) continue;
                if(square % 8 != 0){
                    if(square-1 == this.pawnPush) moves.add(new Move(square, square+7, MoveType.ENPAS));
                }
                if(square % 8 != 7){
                    if(square+1 == this.pawnPush) moves.add(new Move(square, square+9, MoveType.ENPAS));
                }
            }else if(piece == PieceType.BPAWN){
                Helpers.addBPawnNonQuietMovesToList(moves, allPieces, enemyPieces, square);
                //En Passant check
                if(this.pawnPush == -1) continue;
                if(square % 8 != 0){
                    if(square-1 == this.pawnPush) moves.add(new Move(square, square-9, MoveType.ENPAS));
                }
                if(square % 8 != 7){
                    if(square+1 == this.pawnPush) moves.add(new Move(square, square-7, MoveType.ENPAS));
                }
            }else if(piece == PieceType.WBISHOP || piece == PieceType.BBISHOP){
                long biMoves = Helpers.getBishopMovesMagic(allPieces, square);
                biMoves &= ~friendlyPieces; //Cant capture friendly
                biMoves &= enemyPieces; //Only get captures 
                for(int i = 0; i < 64; i++){
                    if(!Helpers.isOccupied(biMoves, i)) continue;
                    moves.add(new Move(square, i, MoveType.CAP));
                }
            }else if(piece == PieceType.WROOK || piece == PieceType.BROOK){
                long roMoves = Helpers.getRookMovesMagic(allPieces, square);
                roMoves &= ~friendlyPieces; //Cant capture friendly
                roMoves &= enemyPieces; //Only get captures
                for(int i = 0; i < 64; i++){
                    if(!Helpers.isOccupied(roMoves, i)) continue;
                    moves.add(new Move(square, i, MoveType.CAP));
                }
            }else if(piece == PieceType.WQUEEN || piece == PieceType.BQUEEN){
                long quMoves = Helpers.getQueenMovesMagic(allPieces, square);
                quMoves &= ~friendlyPieces; //Cant capture friendly
                quMoves &= enemyPieces; //Only get captures
                for(int i = 0; i < 64; i++){
                    if(!Helpers.isOccupied(quMoves, i)) continue;
                    moves.add(new Move(square, i, MoveType.CAP));
                }
            }
        }
        return moves;
    }

    private long getAllAttacksByColor(boolean white){
        long occupancy = getAllPieces();
        long attacks = 0;
        for(int square = 0; square < 64; square++){
            switch(getPieceOnSquare(square)){
                case BBISHOP:
                    if(white) break;
                    attacks |= Helpers.getBishopMovesMagic(occupancy, square);
                    break;
                case BKING:
                    if(white) break;
                    attacks |= Constants.kingMoves[square];
                    break;
                case BKNIGHT:
                    if(white) break;
                    attacks |= Constants.knightMoves[square];
                    break;
                case BPAWN:
                    if(white) break;
                    attacks |= Constants.bPawnAttacks[square];
                    break;
                case BQUEEN:
                    if(white) break;
                    attacks |= Helpers.getQueenMovesMagic(occupancy, square);
                    break;
                case BROOK:
                    if(white) break;
                    attacks |= Helpers.getRookMovesMagic(occupancy, square);
                    break;
                case WBISHOP:
                    if(!white) break;
                    attacks |= Helpers.getBishopMovesMagic(occupancy, square);
                    break;
                case WKING:
                    if(!white) break;
                    attacks |= Constants.kingMoves[square];
                    break;
                case WKNIGHT:
                    if(!white) break;
                    attacks |= Constants.knightMoves[square];
                    break;
                case WPAWN:
                    if(!white) break;
                    attacks |= Constants.wPawnAttacks[square];
                    break;
                case WQUEEN:
                    if(!white) break;
                    attacks |= Helpers.getQueenMovesMagic(occupancy, square);
                    break;
                case WROOK:
                    if(!white) break;
                    attacks |= Helpers.getRookMovesMagic(occupancy, square);
                    break;
                default:
                    break;  
            }
        }
        return attacks;
    }

    //Check if legal move then makeMove
    public boolean tryMakeMove(Move move){
        makeMove(move);
        long attacks = getAllAttacksByColor(this.isWhiteToPlay); //We have switched sides and want to get other colors attacks

        long kingMask;
        if(this.isWhiteToPlay){ //Black just moved
            kingMask = this.bKing;
        }else{ //White just moved
            kingMask = this.wKing;
        }

        if(Helpers.isOccupiedMask(attacks, kingMask)){
            unmakeMove();
            return false;
        } 

        if(move.mType == MoveType.RCASTLE){ //TODO: check check for castling
            long rCastleMask;
            if(this.isWhiteToPlay){ //Black just moved
                rCastleMask = Constants.bCastleRightPath | (1L << Constants.bKingStartSquare);
            }else{ //White just moved
                rCastleMask = Constants.wCastleRightPath | (1L << Constants.wKingStartSquare);
            }

            //Is path attacked?
            if(Helpers.isOccupiedMask(attacks, rCastleMask)){
                this.unmakeMove();
                return false;
            }
        }

        if(move.mType == MoveType.LCASTLE){
            long lCastleMask;
            if(this.isWhiteToPlay){ //Black just moved
                lCastleMask = Constants.bCastleLeftPath | (1L << Constants.bKingStartSquare);
            }else{ //White just moved
                lCastleMask = Constants.wCastleLeftPath | (1L << Constants.wKingStartSquare);
            }

            //Is path attacked?
            if(Helpers.isOccupiedMask(attacks, lCastleMask)){
                this.unmakeMove();
                return false;
            }
        }
        return true;
    }

    public void makeNullMove(){
        gameStates.push(new GameState(PieceType.NONE, null, this.pawnPush, this.zobristKey, this.fiftyMoveRuleCount, null));
        this.isWhiteToPlay = !this.isWhiteToPlay;
        updateZobristWithKey(Zobrist.turnKey); //Update zobrist by side to play

        this.fiftyMoveRuleCount++;
        if(this.pawnPush != -1){
            updateZobrist(this.pawnPush, Zobrist.pawnPush);
        }
        this.pawnPush = -1;
    }

    public void unMakeNullMove(){
        GameState gS = gameStates.pop();
        long zobrist = gS.zobristKey;
        int pawnPush = gS.pawnPush;
        
        this.isWhiteToPlay = !this.isWhiteToPlay;
        this.zobristKey = zobrist;
        this.pawnPush = pawnPush;
        this.fiftyMoveRuleCount--;
    }

    //Just make the move
    public void makeMove(Move move){
        //TODO: Captures
        int from = move.from;
        int to = move.to;

        boolean[] castlingRights = {this.wCanCastleRight, this.wCanCastleLeft, this.bCanCastleRight, this.bCanCastleLeft};
        gameStates.push(new GameState(getPieceOnSquare(to), move, this.pawnPush, this.zobristKey, this.fiftyMoveRuleCount, castlingRights));

        long fromMask = 0b1L << from;
        long toMask = 0b1L << to;

        long mask = fromMask | toMask;

        this.fiftyMoveRuleCount++;

        if(this.pawnPush != -1){ //Pawn push square always change when not -1. Update zobrist to go back to default state (-1).
            updateZobrist(this.pawnPush, Zobrist.pawnPush);
        }
        if(move.mType != MoveType.PAWNPUSH) this.pawnPush = -1;

        switch(move.mType){
            case CAP:
                captureSquare(to);
                this.fiftyMoveRuleCount = 0;
                break;
            case ENPAS:
                if(this.isWhiteToPlay){
                    captureSquare(to-8);
                }else{
                    captureSquare(to+8);
                }
                this.fiftyMoveRuleCount = 0;
                break;
            case LCASTLE: //Update castling rights + castle zobrists later
                long castleMaskL = (1L << (to-2)) | 1L << (to+1);
                if(this.isWhiteToPlay){
                    this.wRooks ^= castleMaskL;
                    updateZobrist(to-2, Zobrist.wRoZob);
                    updateZobrist(to+1, Zobrist.wRoZob);
                }else{
                    this.bRooks ^= castleMaskL;
                    updateZobrist(to-2, Zobrist.bRoZob);
                    updateZobrist(to+1, Zobrist.bRoZob);
                }
                break;
            case PAWNPUSH:
                this.pawnPush = to;
                updateZobrist(this.pawnPush, Zobrist.pawnPush);
                break;
            case QUIET:
                break;
            case RCASTLE: //Update castling rights + castle zobrists later
                long castleMaskR = (1L << (to+1)) | 1L << (to-1);
                if(this.isWhiteToPlay){
                    this.wRooks ^= castleMaskR;
                    updateZobrist(to-1, Zobrist.wRoZob);
                    updateZobrist(to+1, Zobrist.wRoZob);
                }else{
                    this.bRooks ^= castleMaskR;
                    updateZobrist(to-1, Zobrist.bRoZob);
                    updateZobrist(to+1, Zobrist.bRoZob);
                }
                break;
            default:
                break;
        }
        

        PieceType piece = getPieceOnSquare(from);
        switch (piece){
            case BBISHOP:
                this.bBishops ^= mask;
                updateZobrist(to, Zobrist.bBiZob);
                updateZobrist(from, Zobrist.bBiZob);
                break;
            case BKING:
                this.bKing ^= mask;
                updateZobrist(to, Zobrist.bKiZob);
                updateZobrist(from, Zobrist.bKiZob);
                if(this.bCanCastleRight){
                    this.bCanCastleRight = false;
                    updateZobristWithKey(Zobrist.bCastleRKey);
                }
                if(this.bCanCastleLeft){
                    this.bCanCastleLeft = false;
                    updateZobristWithKey(Zobrist.bCastleLKey);
                }
                break;
            case BKNIGHT:
                this.bKnights ^= mask;
                updateZobrist(to, Zobrist.bKnZob);
                updateZobrist(from, Zobrist.bKnZob);
                break;
            case BPAWN:
                this.fiftyMoveRuleCount = 0; //change name to fiftyMoveRule?
                //Promotion check
                if(to < 8){
                    this.bPawns &= ~(1L << from);
                    promoteSquareTo(to, move.promoteTo);
                    updateZobrist(from, Zobrist.bPaZob);
                }else{
                    this.bPawns ^= mask;
                    updateZobrist(to, Zobrist.bPaZob);
                    updateZobrist(from, Zobrist.bPaZob);
                }
                break;
            case BQUEEN:
                this.bQueens ^= mask;
                updateZobrist(to, Zobrist.bQuZob);
                updateZobrist(from, Zobrist.bQuZob);
                break;
            case BROOK:
                this.bRooks ^= mask;
                updateZobrist(to, Zobrist.bRoZob);
                updateZobrist(from, Zobrist.bRoZob);
                if(from == 63 && this.bCanCastleRight){
                    this.bCanCastleRight = false;
                    updateZobristWithKey(Zobrist.bCastleRKey);
                } 
                if(from == 56 && this.bCanCastleLeft){
                    this.bCanCastleLeft = false;
                    updateZobristWithKey(Zobrist.bCastleLKey);
                }
                break;
            case WBISHOP:
                this.wBishops ^= mask;
                updateZobrist(to, Zobrist.wBiZob);
                updateZobrist(from, Zobrist.wBiZob);
                break;
            case WKING:
                this.wKing ^= mask;
                updateZobrist(to, Zobrist.wKiZob);
                updateZobrist(from, Zobrist.wKiZob);
                if(this.wCanCastleRight){
                    this.wCanCastleRight = false;
                    updateZobristWithKey(Zobrist.wCastleRKey);
                }
                if(this.wCanCastleLeft){
                    this.wCanCastleLeft = false;
                    updateZobristWithKey(Zobrist.wCastleLKey);
                }
                break;
            case WKNIGHT:
                this.wKnights ^= mask;
                updateZobrist(to, Zobrist.wKnZob);
                updateZobrist(from, Zobrist.wKnZob);
                break;
            case WPAWN:
                this.fiftyMoveRuleCount = 0; //change name to fiftyMoveRule?
                //Promotion Check
                if(to >= 56){
                    this.wPawns &= ~(1L << from);
                    promoteSquareTo(to, move.promoteTo);
                    updateZobrist(from, Zobrist.wPaZob);
                }else{
                    this.wPawns ^= mask;
                    updateZobrist(to, Zobrist.wPaZob);
                    updateZobrist(from, Zobrist.wPaZob);
                }
                break;
            case WQUEEN:
                this.wQueens ^= mask;
                updateZobrist(to, Zobrist.wQuZob);
                updateZobrist(from, Zobrist.wQuZob);
                break;
            case WROOK:
                this.wRooks ^= mask;
                updateZobrist(to, Zobrist.wRoZob);
                updateZobrist(from, Zobrist.wRoZob);
                if(from == 7 && this.wCanCastleRight){
                    this.wCanCastleRight = false;
                    updateZobristWithKey(Zobrist.wCastleRKey);
                } 
                if(from == 0 && this.wCanCastleLeft){
                    this.wCanCastleLeft = false;
                    updateZobristWithKey(Zobrist.wCastleLKey);
                }
                break;
            default:
                //No piece to move
                break;
        }
        
        this.isWhiteToPlay = !this.isWhiteToPlay; //Switch side
        updateZobristWithKey(Zobrist.turnKey); //Update for side to play
        this.repetitionBuffer.add(this.zobristKey);

    }

    public void unmakeMove(){
        GameState gameState = this.gameStates.pop();
        this.pawnPush = gameState.pawnPush;
        Move move = gameState.move;
        PieceType capturedPiece = gameState.capturedPiece;

        this.wCanCastleRight = gameState.wCanCastleRight;
        this.wCanCastleLeft = gameState.wCanCastleLeft;
        this.bCanCastleRight = gameState.bCanCastleRight;
        this.bCanCastleLeft = gameState.bCanCastleLeft;

        this.fiftyMoveRuleCount = gameState.fiftyMoveRuleCount;

        this.isWhiteToPlay = !this.isWhiteToPlay;

        this.repetitionBuffer.goBack();

        int from = move.from;
        int to = move.to;
        long moveMask = (1L << from) | (1L << to);

        if(move.promotion){
            captureSquare(to);
            if(this.isWhiteToPlay){
                setPieceOnSquare('P', from);
            }else{
                setPieceOnSquare('p', from);
            }
        }else{
            switch(getPieceOnSquare(to)){
                case BBISHOP:
                    this.bBishops ^= moveMask;
                    break;
                case BKING:
                    this.bKing ^= moveMask;
                    //Castle check
                    if(move.mType == MoveType.RCASTLE){
                        this.bRooks ^= (1L << 63) | (1L << 61);
                    }else if(move.mType == MoveType.LCASTLE){
                        this.bRooks ^= (1L << 56) | (1L << 59);
                    }
                    break;
                case BKNIGHT:
                    this.bKnights ^= moveMask;
                    break;
                case BPAWN:
                    this.bPawns ^= moveMask;
                    //EnPassant check
                    if(move.mType == MoveType.ENPAS){
                        this.wPawns |= 1L << (to+8);
                    }
                    break;
                case BQUEEN:
                    this.bQueens ^= moveMask;
                    break;
                case BROOK:
                    this.bRooks ^= moveMask;
                    break;
                case WBISHOP:
                    this.wBishops ^= moveMask;
                    break;
                case WKING:
                    this.wKing ^= moveMask;
                    //Castle check
                    if(move.mType == MoveType.RCASTLE){
                        this.wRooks ^= (1L << 7) | (1L << 5);
                    }else if(move.mType == MoveType.LCASTLE){
                        this.wRooks ^= (1L << 0) | (1L << 3);
                    }
                    break;
                case WKNIGHT:
                    this.wKnights ^= moveMask;
                    break;
                case WPAWN:
                    this.wPawns ^= moveMask;
                    //EnPassant check
                    if(move.mType == MoveType.ENPAS){
                        this.bPawns |= 1L << (to-8);
                    }
                    break;
                case WQUEEN:
                    this.wQueens ^= moveMask;
                    break;
                case WROOK:
                    this.wRooks ^= moveMask;
                    break;
                default:
                    break;
            }
        }

        //Captures
        if(move.mType == MoveType.CAP){
            long toMask = 1L << to;
            switch(capturedPiece){
                case BBISHOP:
                    this.bBishops |= toMask;
                    break;
                case BKING:
                    this.bKing |= toMask;
                    break;
                case BKNIGHT:
                    this.bKnights |= toMask;
                    break;
                case BPAWN:
                    this.bPawns |= toMask;
                    break;
                case BQUEEN:
                    this.bQueens |= toMask;
                    break;
                case BROOK:
                    this.bRooks |= toMask;
                    break;
                case WBISHOP:
                    this.wBishops |= toMask;
                    break;
                case WKING:
                    this.wKing |= toMask;
                    break;
                case WKNIGHT:
                    this.wKnights |= toMask;
                    break;
                case WPAWN:
                    this.wPawns |= toMask;
                    break;
                case WQUEEN:
                    this.wQueens |= toMask;
                    break;
                case WROOK:
                    this.wRooks |= toMask;
                    break;
                default:
                    break;
                
            }
        }
        

        //Zobrist is saved in gamestate to make unmakemove logic more simple. But some functions in unmakemove modify zobrist, so update zobrist last?
        this.zobristKey = gameState.zobristKey; 
    }

    //Remove piece from given square and updates zobrist key
    private void captureSquare(int pos){
        PieceType piece = getPieceOnSquare(pos);
        long mask = 0b1L << pos;
        switch (piece){
            case BBISHOP:
                this.bBishops &= ~mask;
                updateZobrist(pos, Zobrist.bBiZob);
                break;
            case BKING:
                this.bKing &= ~mask;
                updateZobrist(pos, Zobrist.bKiZob);
                break;
            case BKNIGHT:
                this.bKnights &= ~mask;
                updateZobrist(pos, Zobrist.bKnZob);
                break;
            case BPAWN:
                this.bPawns &= ~mask;
                updateZobrist(pos, Zobrist.bPaZob);
                break;
            case BQUEEN:
                this.bQueens &= ~mask;
                updateZobrist(pos, Zobrist.bQuZob);
                break;
            case BROOK:
                this.bRooks &= ~mask;
                updateZobrist(pos, Zobrist.bRoZob);
                if(pos == 63 && this.bCanCastleRight){  //Prevents castling after your rook got captured :)
                    this.bCanCastleRight = false;
                    updateZobristWithKey(Zobrist.bCastleRKey);
                }else if(pos == 56 && this.bCanCastleLeft){
                    this.bCanCastleLeft = false;
                    updateZobristWithKey(Zobrist.bCastleLKey);
                }
                break;
            case WBISHOP:
                this.wBishops &= ~mask;
                updateZobrist(pos, Zobrist.wBiZob);
                break;
            case WKING:
                this.wKing &= ~mask;
                updateZobrist(pos, Zobrist.wKiZob);
                break;
            case WKNIGHT:
                this.wKnights &= ~mask;
                updateZobrist(pos, Zobrist.wKnZob);
                break;
            case WPAWN:
                this.wPawns &= ~mask;
                updateZobrist(pos, Zobrist.wPaZob);
                break;
            case WQUEEN:
                this.wQueens &= ~mask;
                updateZobrist(pos, Zobrist.wQuZob);
                break;
            case WROOK:
                this.wRooks &= ~mask;
                updateZobrist(pos, Zobrist.wRoZob);
                if(pos == 7 && this.wCanCastleRight){  //Prevents castling after your rook got captured :)
                    this.wCanCastleRight = false;
                    updateZobristWithKey(Zobrist.wCastleRKey);
                }else if(pos == 0 && this.wCanCastleLeft){
                    this.wCanCastleLeft = false;
                    updateZobristWithKey(Zobrist.wCastleLKey);
                }
                break;
            default:
                break;
            
        }
    }

    //Returns mask for all pieces
    public long getAllPieces(){
        return      wPawns | bPawns | 
                    wBishops | bBishops | 
                    wKnights | bKnights | 
                    wRooks | bRooks | 
                    wQueens | bQueens | 
                    wKing | bKing;
    }

    //Returns mask for enemy pieces
    private long getPiecesByColor(boolean white){
        if(!white){
            return bPawns | 
                    bBishops | 
                    bKnights | 
                    bRooks | 
                    bQueens | 
                    bKing;
        }else{
            return wPawns |
                    wBishops | 
                    wKnights |
                    wRooks |
                    wQueens | 
                    wKing;
        }
    }

    public boolean isCheckForColor(boolean white){
        long attacks = getAllAttacksByColor(!white);
        if(white){
            return Helpers.isOccupiedMask(attacks, this.wKing);
        }else{
            return Helpers.isOccupiedMask(attacks, this.bKing);
        }
        
    }

    public boolean[] getCastlingRights() {
        boolean[] cR = {this.wCanCastleRight, this.wCanCastleLeft, this.bCanCastleRight, this.bCanCastleLeft};
        return cR;
    }

    private void updateZobrist(int square, int piece){
        this.zobristKey ^= Zobrist.getzobristKeyMap()[square][piece];
    }
    private void updateZobristWithKey(long key){
        this.zobristKey ^= key;
    }

    public boolean isDrawByRepetitionOr50Move(){
        return this.fiftyMoveRuleCount >= 100 || this.repetitionBuffer.checkRepetition(this.zobristKey);
    }

    public int getValueOnSquare(int square){
        PieceType piece = getPieceOnSquare(square);
        switch (piece) {
            case BBISHOP:
                return Constants.bishopVal; 
            case BKNIGHT:
                return Constants.knightVal;
            case BPAWN:
                return Constants.pawnVal;
            case BQUEEN:
                return Constants.queenVal;
            case BROOK:
                return Constants.rookVal;
            case WBISHOP:
                return Constants.bishopVal;
            case WKNIGHT:
                return Constants.knightVal;
            case WPAWN:
                return Constants.pawnVal;
            case WQUEEN:
                return Constants.queenVal;
            case WROOK:
                return Constants.rookVal;
            default:
                return 0;
        }
    }

    public void empty() {
        this.wPawns = 0;
        this.bPawns = 0;
        this.wKnights = 0;
        this.bKnights = 0;
        this.wBishops = 0;
        this.bBishops = 0;
        this.wRooks = 0;
        this.bRooks = 0;
        this.wQueens = 0;
        this.bQueens = 0;
        this.wKing = 0;
        this.bKing = 0;

        this.wCanCastleRight = false;
        this.wCanCastleLeft = false;
        this.bCanCastleRight = false;
        this.bCanCastleLeft = false;

        this.isWhiteToPlay = true;

        this.pawnPush = -1;

        this.fiftyMoveRuleCount = 0;
        this.repetitionBuffer = new RepetitionBuffer(40); //Use a deque instead? linked?
        
        this.gameStates = new LinkedList<GameState>();

        reGenerateZobrist();
    }

    private void promoteSquareTo(int square, char piece){
        if(this.isWhiteToPlay){
            setPieceOnSquare(Character.toUpperCase(piece), square);
        }else{
            setPieceOnSquare(Character.toLowerCase(piece), square);
        }
    }

}
