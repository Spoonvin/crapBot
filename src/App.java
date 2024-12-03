import java.util.Scanner;

public class App {


    public static void main(String[] args) throws Exception {

        Zobrist.initializeZobristKeyMap(); //Init the random hash values
        Board b = new Board();

        ConcurrentBot cBot = new ConcurrentBot();

        ChessAi bot = new ChessAi();
        ChessAiOld botOld = new ChessAiOld();
        

        Scanner scan = new Scanner(System.in);

        System.out.println("0: for player vs bot");
        System.out.println("1: for bot vs bot");
        System.out.println("2: to test depth performance");
        int gameType = scan.nextInt();
        scan.close();

        ChessGUI gui = new ChessGUI(b, bot, botOld, cBot);
        gui.setVisible(true);

        switch(gameType){
            case 0:
                break;
            case 1:
                botVsBot(b, bot, cBot, botOld, gui);
                break;
            case 2:
                Board testBoard = new Board(1);
                gui = new ChessGUI(testBoard, bot, botOld, cBot);
                testBoard.makeMove(botOld.getBestMove(testBoard, 1000));
                gui.drawBoard();
                break;
            default:
                break;
        }
        
        
    }

    private static void botVsBot(Board b, ChessAi bot, ConcurrentBot cBot, ChessAiOld botOld, ChessGUI gui) throws Exception {
        int oldScore = 0;
        int newScore = 0;
        boolean oldBotWhite = true;
        boolean newBotWhite = false;

        while (true) {
            while (true) {
                if (b.getIsWhiteToPlay() == oldBotWhite) {
                    Move move = cBot.getBestMove(b, 1000);
                    if (move == null) {
                        if (b.isCheckForColor(oldBotWhite)) {
                            System.out.println("New bot wins!");
                            newScore++;
                            break;
                        } else {
                            System.out.println("Old bot got stalemated!");
                            break;
                        }
                    }
                    if (b.isDrawByRepetitionOr50Move()) {
                        System.out.println("Draw by repetition or 50 move rule!");
                        break;
                    }
                    b.makeMove(move);
                    gui.drawBoard();
                } else {
                    Move move = bot.getBestMove(b, 1000);
                    if (move == null) {
                        if (b.isCheckForColor(newBotWhite)) {
                            System.out.println("Old bot wins!");
                            oldScore++;
                            break;
                        } else {
                            System.out.println("New bot got stalemated!");
                            break;
                        }
                    }
                    if (b.isDrawByRepetitionOr50Move()) {
                        System.out.println("Draw by repetition or 50 move rule!");
                        break;
                    }
                    b.makeMove(move);
                    gui.drawBoard();
                }
            }
            System.out.print("New ai score: ");
            System.out.println(newScore);
            System.out.print("Old ai score: ");
            System.out.println(oldScore);
            oldBotWhite = !oldBotWhite;
            newBotWhite = !newBotWhite;
            bot.clearTT();
            botOld.clearTT();
            b = new Board();
            gui.newMatch(b);
        }
    }
}
