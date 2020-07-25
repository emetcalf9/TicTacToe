package tictactoe;

import java.util.*;

public class Main {
    static Scanner s = new Scanner(System.in);
    static Random r = new Random();

    public static void main(String[] args) {
        //Define and initialize 2 players objects. These will be overwritten later with the player types
        Player player1 = new Player();
        Player player2 = new Player();

        while (true) {
            boolean startGame = false;
            System.out.print("Input command (start, exit, help):"); // Main menu
            String command = s.nextLine();
            try {
                String[] commandPieces = command.split(" ");
                if (commandPieces[0].equals("exit")) {
                    // Close program
                    break;
                } else if (commandPieces[0].equals("help")) {
                    // Print instructions
                    System.out.println("Instructions:");
                    System.out.println("start [player1] [player2]: Starts a new game");
                    System.out.println("Player Options: user easy medium hard");
                    System.out.println("exit: Exits the program");
                } else if (commandPieces.length != 3) {
                    // "start" command requires "start" and 2 player choices, so command length must be 3
                    System.out.println("Bad parameters!");
                    continue;
                } else if (commandPieces[0].equals("start")) {
                    switch (commandPieces[1]) {
                        // Player 1 choice
                        case "user":
                            player1 = new Player();
                            break;
                        case "easy":
                            player1 = new EasyCPU();
                            break;
                        case "medium":
                            player1 = new MediumCPU();
                            break;
                        case "hard":
                            player1 = new HardCPU();
                            break;
                        default:
                            System.out.println("Bad parameters!");
                            continue;
                    }
                    switch (commandPieces[2]) {
                        // Player 2 choice
                        case "user":
                            player2 = new Player();
                            break;
                        case "easy":
                            player2 = new EasyCPU();
                            break;
                        case "medium":
                            player2 = new MediumCPU();
                            break;
                        case "hard":
                            player2 = new HardCPU();
                            break;
                        default:
                            System.out.println("Bad parameters!");
                            continue;
                    }
                    startGame = true;
                }
            } catch (Exception e) {
                System.out.println("Bad parameters!");
                continue;
            }
            if (startGame) {
                char winner = 'N';
                int xCount = 0; // Track the number of X's to determine when a win is possible and when there is a draw
                Board board = new Board();
                board.printBoard();
                while (winner == 'N') {
                    board = player1.move(board);
                    board.printBoard();
                    xCount++;
                    if (xCount > 2) { // Game cannot be won until there are at least 3 X's
                        winner = board.checkWin();
                        if (winner == 'X') {
                            continue;
                        }
                    }
                    if (xCount == 5) { // After the 5th X, if X did not win the game is a draw
                        winner = 'D';
                        continue;
                    }
                    board = player2.move(board);
                    board.printBoard();
                    if (xCount > 2) {
                        winner = board.checkWin();
                    }
                }
                switch (winner) {
                    case 'X':
                        System.out.println("X wins");
                        break;
                    case 'O':
                        System.out.println("O wins");
                        break;
                    case 'D':
                        System.out.println("Draw");
                }
            }
        }
    }

    static class Player {
        public Board move(Board board) { // move method takes in a board, updates it and returns a new board
            boolean validMove = false;
            // Initialize move as 0 0, which is not valid
            int column = 0;
            int row = 0;
            while (!validMove) {
                System.out.print("Enter the coordinates (column row):");
                String move = s.nextLine();
                String[] moveCoordinates = move.split(" ");
                try {
                    // Set column and row to user input
                    column = Integer.parseInt(moveCoordinates[0]);
                    row = Integer.parseInt(moveCoordinates[1]);
                } catch (Exception e) {
                    // Exception will be thrown if the user does not enter 2 numbers
                    System.out.println("You should enter numbers!");
                    continue;
                }
                if (0 < row && row < 4 && 0 < column && column < 4) {
                    if (board.moves[column][row] == ' ') { // Check if cell is still blank
                        validMove = true;
                    } else {
                        System.out.println("This cell is occupied! Choose another one!");
                    }
                } else {
                    System.out.println("Coordinates should be from 1 to 3!");
                }
            }
            board.moves[column][row] = board.getNextMove(); // Check to see if X or O should be played
            board.switchMove(); // Switch the next move
            return board;
        }
    }

    static class EasyCPU extends Player {
        // The EasyCPU moves randomly regardless of the board state
        public Board move(Board board) {
            boolean validMove = false;
            int row = 0;
            int column = 0;
            while (!validMove) {
                row = r.nextInt(3) + 1;
                column = r.nextInt(3) + 1;
                if (board.moves[column][row] == ' ') {
                    validMove = true;
                }
            }
            System.out.println("Making move level \"easy\"");
            board.moves[column][row] = board.getNextMove();
            board.switchMove();
            return board;
        }
    }

    static class MediumCPU extends Player {
        // The MediumCPU checks to see if it can win, and takes the win if possible
        // If it cannot win on this turn, it checks to see if the opponent can win and blocks them
        // If no one is about to win, it moves randomly
        public Board move(Board board) {
            boolean validMove = false;
            int row = 0;
            int column = 0;
            int[] nextMove = findMove(board);
            if (nextMove[0] != 0) {
                System.out.println("Making move level \"medium\"");
                board.moves[nextMove[0]][nextMove[1]] = board.getNextMove();
                board.switchMove();
                return board;
            }
            while (!validMove) {
                row = r.nextInt(3) + 1;
                column = r.nextInt(3) + 1;
                if (board.moves[column][row] == ' ') {
                    validMove = true;
                }
            }
            System.out.println("Making move level \"medium\"");
            board.moves[column][row] = board.getNextMove();
            board.switchMove();
            return board;
        }

        public int[] findMove(Board board) {
            int[] nextMove = new int[2];
            char currentPlayer = board.getNextMove();
            char nextPlayer;
            if (currentPlayer == 'X') {
                nextPlayer = 'O';
            } else {
                nextPlayer = 'X';
            }
            // Loop through each cell to check if it is empty
            // If it is empty, see if playing there will win the game, and return this cell if it does
            for (int i = 1; i < 4; i++) {
                for (int j = 1; j < 4; j++) {
                    if (board.moves[i][j] == ' ') {
                        board.moves[i][j] = currentPlayer;
                        if (board.checkWin() == currentPlayer) {
                            board.moves[i][j] = ' ';
                            nextMove[0] = i;
                            nextMove[1] = j;
                            return nextMove;
                        }
                        board.moves[i][j] = ' ';
                    }
                }
            }
            // Loop through each cell to check if it is empty
            // If it is empty, see if the opponent will win the game, and return this cell if it will
            for (int i = 1; i < 4; i++) {
                for (int j = 1; j < 4; j++) {
                    if (board.moves[i][j] == ' ') {
                        board.moves[i][j] = nextPlayer;
                        if (board.checkWin() == nextPlayer) {
                            board.moves[i][j] = ' ';
                            nextMove[0] = i;
                            nextMove[1] = j;
                            return nextMove;
                        }
                        board.moves[i][j] = ' ';
                    }
                }
            }
            // If neither player can win, return 0 0 which is an invalid move
            nextMove[0] = 0;
            nextMove[1] = 0;
            return nextMove;
        }
    }

    static class HardCPU extends Player {
        // The HardCPU implements the minimax algorithm to always make the ideal move
        // The HardCPU can never lose, only win or draw
        public Board move(Board board) {
            int[] nextMove = findMove(board);
            int row = nextMove[1];
            int column = nextMove[0];
            System.out.println("Making move level \"hard\"");
            board.moves[column][row] = board.getNextMove();
            board.switchMove();
            return board;
        }

        public int[] findMove(Board board) {
            int[] nextMove = new int[2];
            nextMove[0] = 0;
            nextMove[1] = 0;
            char player = board.getNextMove();
            int bestVal = -1000;
            // Loop through each field to see it if is empty, and run the minimax function on each empty cell
            for (int i = 1; i < 4; i++) {
                for (int j = 1; j < 4; j++) {
                    if(board.moves[i][j] == ' ') {
                        board.moves[i][j] = player;
                        int moveVal = minimax(board, player, false);
                        board.moves[i][j] = ' ';
                        // If this move is better than the current bestVal, set it as the next move and update bestVal
                        if (moveVal > bestVal) {
                            nextMove[0] = i;
                            nextMove[1] = j;
                            bestVal = moveVal;
                        }
                    }
                }
            }
            return nextMove; // Return the move that was determined to be the best
        }
        boolean isMovesLeft(Board board) {
            // Loop through each cell on the board to find empty cells
            // This function will be used in the minimax function to determine when there is a draw
            for (int i = 1; i < 4; i++) {
                for (int j = 1; j < 4; j++) {
                    if(board.moves[i][j] == ' ') {
                        return true;
                    }
                }
            }
            return false;
        }
        int minimax(Board board, char player, boolean isMax) {
            int score = 0;
            int best;
            char opponent;
            if (player == 'X') {
                opponent = 'O';
            } else {
                opponent = 'X';
            }
            if (board.checkWin() == player) {
                // If the current player wins, return 10 as the score for this move
                score = 10;
                return score;
            } else if (board.checkWin() == opponent) {
                // If the opponent wins, return -10 as the score for this move
                score = -10;
                return score;
            }
            if (isMovesLeft(board) == false) {
                // If there is a draw, return 0
                return score;
            }
            if (isMax) {
                best = -1000; // When attempting to maximize the score, start with a low value
                for (int i = 1; i < 4; i++) {
                    for (int j = 1; j < 4; j++) {
                        if(board.moves[i][j] == ' ') {
                            board.moves[i][j] = player; // Make the move, then run minimax again for the other player
                            best = Math.max(best, minimax(board, player, !isMax));
                            board.moves[i][j] = ' '; // Undo the move because we are only testing cells in this function
                        }
                    }
                }
            } else {
                best = 1000; // When attempting to minimize the score, start with a high value
                for (int i = 1; i < 4; i++) {
                    for (int j = 1; j < 4; j++) {
                        if(board.moves[i][j] == ' ') {
                            board.moves[i][j] = opponent;
                            best = Math.min(best, minimax(board, player, !isMax));
                            board.moves[i][j] = ' ';
                        }
                    }
                }
            }
            return best;
        }
    }
}
    class Board {
        char[][] moves = {{'0', '1', '2', '3'}, {'1', ' ', ' ', ' '}, {'2', ' ', ' ', ' '}, {'3', ' ', ' ', ' '}};
        char nextMove;

        Board() {
            nextMove = 'X'; // First player is always X
        }

        public char getNextMove() {
            return nextMove;
        }

        public void switchMove() {
            if (nextMove == 'X') {
                nextMove = 'O';
            } else {
                nextMove = 'X';
            }
        }

        public void printBoard() {
            System.out.println("    " + moves[1][0] + " " + moves[2][0] + " " + moves[3][0]);
            System.out.println("   --------");
            System.out.println(moves[0][3] + " | " + moves[1][3] + " " + moves[2][3] + " " + moves[3][3] + " |");
            System.out.println(moves[0][2] + " | " + moves[1][2] + " " + moves[2][2] + " " + moves[3][2] + " |");
            System.out.println(moves[0][1] + " | " + moves[1][1] + " " + moves[2][1] + " " + moves[3][1] + " |");
            System.out.println("   --------");
        }

        public char checkWin() {
            // Loop through each column and see if either player won
            for (int i = 1; i < 4; i++) {
                if (moves[i][1] == moves[i][2] && moves[i][2] == moves[i][3]) {
                    if (moves[i][1] == 'X') {
                        return 'X';
                    } else if (moves[i][1] == 'O') {
                        return 'O';
                    }
                }
            }
            // Loop through each row
            for (int j = 1; j < 4; j++) {
                if (moves[1][j] == moves[2][j] && moves[2][j] == moves[3][j]) {
                    if (moves[1][j] == 'X') {
                        return 'X';
                    } else if (moves[1][j] == 'O') {
                        return 'O';
                    }
                }
            }
            // Check diagonals
            if (moves[1][1] == moves[2][2] && moves[2][2] == moves[3][3] || moves[3][1] == moves[2][2] && moves[2][2] == moves[1][3]) {
                if (moves[2][2] == 'X') {
                    return 'X';
                } else if (moves[2][2] == 'O') {
                    return 'O';
                }
            }
            // If neither player has won, return N
            return 'N';
        }
    }
