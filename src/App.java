import java.util.Scanner;

public class App {


    public static void main(String[] args) throws Exception {

        Zobrist.initializeZobristKeyMap(); //Init the random hash values
        Board b = new Board();

        ChessAi bot = new ChessAi();
        ChessAiOld botOld = new ChessAiOld();
        

        Scanner scan = new Scanner(System.in);

        System.out.println("0: for player vs bot");
        System.out.println("1: for bot vs bot");
        System.out.println("2: to test depth performance");
        int gameType = scan.nextInt();
        scan.close();

        ChessGUI gui = new ChessGUI(b, bot, botOld);
        gui.setVisible(true);

        switch(gameType){
            case 0:
                break;
            case 1:
                botVsBot(b, bot, botOld, gui);
                break;
            case 2:
                Board testBoard = new Board(1);
                gui = new ChessGUI(testBoard, bot, botOld);
                testBoard.makeMove(botOld.getBestMove(testBoard, 1000));
                gui.drawBoard();
                break;
            default:
                break;
        }
        
        
    }

    private static void botVsBot(Board b, ChessAi bot, ChessAiOld botOld, ChessGUI gui){
        int oldScore = 0;
            int newScore = 0;
            boolean oldBotWhite = true;
            boolean newBotWhite = false;

            while(true){
                while(true){
                    if(b.getIsWhiteToPlay() == oldBotWhite){
                        Move move = botOld.getBestMove(b, 1000);
                        if(move == null){
                            if(b.isCheckForColor(oldBotWhite)){
                                System.out.println("New bot wins!");
                                newScore++;
                                break;
                            }else{
                                System.out.println("Old bot got stalemated!");
                                break;
                            }
                        }
                        if(b.isDrawByRepetitionOr50Move()){
                            System.out.println("Draw by repetition or 50 move rule!");
                            break;
                        } 
                        b.makeMove(move);
                        gui.drawBoard();
                    }else{
                        Move move = bot.getBestMove(b, 1000);
                        if(move == null){
                            if(b.isCheckForColor(newBotWhite)){
                                System.out.println("Old bot wins!");
                                oldScore++;
                                break;
                            }else{
                                System.out.println("New bot got stalemated!");
                                break;
                            }
                        }
                        if(b.isDrawByRepetitionOr50Move()){
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
                b = new Board();
                gui.newMatch(b);
            }
    }
}
