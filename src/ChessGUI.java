import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class ChessGUI extends JFrame {
    
    private final int TILE_SIZE = 80; // Tile size for each square
    private JButton[][] buttons = new JButton[8][8]; // 8x8 grid of buttons for the chessboard
    
    private Board board; // Assuming you have a Board class
    private int from = -1; // Store the first clicked square (no square is selected initially)

    private ChessAi bot;
    private ChessAiOld botOld;

    public ChessGUI(Board b, ChessAi bot, ChessAiOld botOld) {
        this.board = b;
        this.bot = bot;
        this.botOld = botOld;

        setTitle("Chess Game");
        setSize(8 * TILE_SIZE, 8 * TILE_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 8)); // 8x8 chessboard grid
        
        initializeBoard();
        drawBoard();
    }

    public void newMatch(Board b){
        this.board = b;
    }

    // Initialize the board with buttons
    private void initializeBoard() {
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                button.setBackground((row + col) % 2 == 0 ? Color.WHITE : Color.GRAY); // Alternate colors for the board
                
                int position = row * 8 + col; // Calculate board index (0 to 63)
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        handleSquareClick(position); // Handle the click event
                    }
                });

                buttons[7-row][col] = button;
                add(button); // Add button to the layout
            }
        }
    }

    // Redraw the board with the updated piece positions
    public void drawBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int pos = row * 8 + col;
                JButton button = buttons[7-row][col];
                
                ImageIcon icon = getPieceIcon(pos);
                button.setIcon(icon);
            }
        }
    }

    // Handle square click event
    private void handleSquareClick(int squareIndex) {
        if (from == -1) {
            // First click: select the square to move from
            from = squareIndex;
        } else {
            // Second click: try to make the move
            ArrayList<Move> moves = board.getPseudoLegalMoves();
            if(moves.contains(new Move(from, squareIndex))){
                int idx = moves.indexOf(new Move(from, squareIndex)); //Ugly , but need to have correct moveType
                boolean moveSuccessful = board.tryMakeMove(moves.get(idx)); // Assuming this is your method
                if (moveSuccessful) {
                    drawBoard(); // Redraw the board after a successful move
                    
                    
                    System.out.print("Old bot nodes: ");
                    botOld.getBestMoveDepth(board, 4);
                    
                    //Make bot move
                    System.out.print("New bot nodes: ");
                    board.makeMove(bot.getBestMoveDepth(board, 4));
                    drawBoard();
                }else{
                    System.out.println("Illegal move");
                }
            }else{
                System.out.println("Not a pseudo legal move!");
            }
            from = -1; // Reset for the next move
        }
    }

    // Get the icon for a given piece (assuming you have an enum for PieceType and Color)
    private ImageIcon getPieceIcon(int pos) {
        PieceType piece = board.getPieceOnSquare(pos);

        if(piece == PieceType.NONE) return null;

        String pieceName = piece.toString() + ".png";

        try {
            // Load the image from the resources folder
            BufferedImage img = ImageIO.read(new File("pieceIcons/" + pieceName));
            return new ImageIcon(img.getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle missing image case
        }
    }
}
