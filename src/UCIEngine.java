import java.util.Scanner;

public class UCIEngine {
    private static ChessAi bot;  // Your bot class
    private static Board board;  // Your board class

    public static void main(String[] args) throws Exception {
        Zobrist.initializeZobristKeyMap(); //Init the random hash values
        bot = new ChessAi();
        board = new Board();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            if (input.equals("uci")) {
                handleUCI();
            } else if (input.equals("isready")) {
                handleIsReady();
            } else if (input.equals("ucinewgame")) {
                handleNewGame();
            } else if (input.startsWith("position")) {
                handlePosition(input);
            } else if (input.startsWith("go")) {
                handleGo(input);
            } else if (input.equals("quit")) {
                scanner.close();
                break;
            }
        }
    }

    private static void handleUCI() {
        System.out.println("id name crapBot");
        System.out.println("id author Spoonvin");
        System.out.println("uciok");
    }

    private static void handleIsReady() {
        System.out.println("readyok");
    }

    private static void handleNewGame() {
        board = new Board();
        bot.clearTT();
    }

    private static void handlePosition(String input) {
        if (input.contains("startpos")) {
            board = new Board();  // Start from the default position
            if (input.contains("moves")) {
                String[] parts = input.split("moves");
                if (parts.length > 1) {
                    String[] moves = parts[1].trim().split(" ");
                    for (String move : moves) {
                        board.makeMove(Helpers.parseFromMoveNotation(move, board));  // Apply each move to your board
                    }
                }
            }
        } else if (input.contains("fen")) {
            String[] parts = input.split("fen");
            String fen = parts[1].trim().split("moves")[0].trim();
            Helpers.setBoardByFen(board, fen);  // Set the board using the FEN string
            if (input.contains("moves")) {
                String[] moves = input.split("moves")[1].trim().split(" ");
                for (String move : moves) {
                    board.makeMove(Helpers.parseFromMoveNotation(move, board));
                }
            }
        }
    }

    private static void handleGo(String input) {

        String regex;
        int timeLeft = 300000; //Standard 5 min
        int timeInc = 0;

        if(board.getIsWhiteToPlay()){
            regex = "wtime";
        }else{
            regex = "btime";
        }
            // Parse the input for time control parameters
        if (input.contains(regex)) {
            // Extract time left from input
            String[] parts = input.split(regex);
            String timeString = (parts[1].trim()).split(" ")[0];
            timeLeft = Integer.parseInt(timeString); // Time left in milliseconds
        }

        if(board.getIsWhiteToPlay()){
            regex = "winc";
        }else{
            regex = "binc";
        }
        if (input.contains(regex)) {
            // Extract increment time from input
            String[] parts = input.split(regex);
            String incrementString = (parts[1].trim()).split(" ")[0];
            timeInc = Integer.parseInt(incrementString); // Time increment in milliseconds
        }

        System.out.println(timeLeft);System.out.println(timeInc);


        int thinkTime = Helpers.calculateBotThinkTime(timeLeft, timeInc);
        Move bestMove = bot.getBestMove(board, thinkTime);  // Use your bot to calculate the move
        String sBestMove = Helpers.parseToMoveNotation(bestMove);
        System.out.print("Engine pos: ");
        System.out.println(Helpers.boardToFen(board));
        System.out.println("bestmove " + sBestMove);
    }
}
