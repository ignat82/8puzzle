/* *****************************************************************************
 *  class defining Board object for processing by Solver object methods
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

public class Board {
    private final int n;
    private final int[][] tiles;
    private int hamming;
    private int manhattan;

    /**************************************************************************
     * Board constructor
     * @param tiles  n-by-n array of tiles, where tiles[row][col] = tile at (row, col)
     * constructor also caches manhattan and hamming distances for the board
     *************************************************************************/
    public Board(int[][] tiles) {
        n = tiles[0].length;
        this.tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
        hamming = calculateHamming();
        manhattan = calculateManhattan();
    }

    /**************************************************************************
     * hamming distance (from solution) - number of tiles on wrong places
     *************************************************************************/
    private int calculateHamming() {
        int hammingLocal = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != (j + 1) + (i * n)) {
                    hammingLocal++;
                }
            }
        }
        hammingLocal -= 1;
        return hammingLocal;
    }

    /*************************************************************************
     * manhattan distance - sum of horizontal and vertical distances of all tiles
     * to their right places
     ************************************************************************/
    private int calculateManhattan() {
        int manhattanLocal = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0) {
                    int iOrig = (tiles[i][j] - 1) / n;
                    int jOrig = (tiles[i][j] - 1) % n;
                    manhattanLocal += Math.abs(iOrig - i) + Math.abs(jOrig - j);
                }
            }
        }
        return manhattanLocal;
    }

    /*************************************************************************
     * @return string representation of board
     *************************************************************************/
    public String toString() {
        StringBuilder outpStr = new StringBuilder(Integer.toString(n)).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                outpStr.append(" ").append(tiles[i][j]);
            }
            outpStr.append("\n");
        }
        return outpStr.toString();
    }

    /**************************************************************************
     * getter for Board object encapsulation
     * @return board dimension n
     *************************************************************************/
    public int dimension() {
        return n;
    }

    /**************************************************************************
     * getter for Board object encapsulation
     * @return number of tiles out of place
     *************************************************************************/
    public int hamming() {
        return hamming;
    }

    /*************************************************************************
     * getter for Board object encapsulation
     * @return sum of Manhattan distances between tiles and goal
     *************************************************************************/
    public int manhattan() {
        return manhattan;
    }

    /*************************************************************************
     * checking if all tiles are on right places (if the board is "goal board"_
     * @return true if the board is goal board and false otherwise
     ************************************************************************/
    public boolean isGoal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != (j + 1) + (i * n)) {
                    if (!((i == n - 1) && (j == n - 1))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**************************************************************************
     * @param y - another board
     * @return true if the boards are equal (all tiles are on the same places
     *************************************************************************/
    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }
        if (y == null) {
            return false;
        }
        if (this.getClass() != y.getClass()) {
            return false;
        }
        Board that = (Board) y;
        if (n != that.n) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != that.tiles[i][j]) {
                    if (!((i == n - 1) && (j == n - 1))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /*************************************************************************
     * neighbor boards - boards derived from given board by one move
     * @return Stack containing all boards which are neighbors to given board
     ************************************************************************/
    public Iterable<Board> neighbors() {
        int emptyI = 0;
        int emptyJ = 0;
        boolean gotEmpty = false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    emptyI = i;
                    emptyJ = j;
                    gotEmpty = true;
                    break;
                }
            }
            if (gotEmpty) {
                break;
            }
        }
        Stack<Board> neighbors = new Stack<Board>();
        int[][] increments = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int[] incr : increments) {
            if ((emptyI + incr[0] >= 0) && (emptyI + incr[0] < n)) {
                if ((emptyJ + incr[1] >= 0) && (emptyJ + incr[1] < n)) {
                    int[][] neighborTiles = new int[n][n];
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {
                            neighborTiles[i][j] = tiles[i][j];
                        }
                    }
                    neighborTiles[emptyI][emptyJ] = tiles[emptyI + incr[0]][emptyJ + incr[1]];
                    neighborTiles[emptyI + incr[0]][emptyJ + incr[1]] = 0;
                    neighbors.push(new Board(neighborTiles));
                }
            }
        }
        return neighbors;
    }

    /**************************************************************************
     * @return a board that is obtained by exchanging any pair of tiles in a
     * given board
     *************************************************************************/
    public Board twin() {
        int[][] twinTiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                twinTiles[i][j] = tiles[i][j];
            }
        }
        int tempTile = 0;
        int iTemp = 0;
        int jTemp = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (twinTiles[i][j] != 0) {
                    if (tempTile == 0) {
                        tempTile = twinTiles[i][j];
                        iTemp = i;
                        jTemp = j;
                    }
                    else {
                        twinTiles[iTemp][jTemp] = twinTiles[i][j];
                        twinTiles[i][j] = tempTile;
                        return new Board(twinTiles);
                    }
                }
            }
        }
        return null;
    }

    /*************************************************************************
     * static method to improve readability of test client
     * @param fileName name of txt file with board
     * @return Board object instance
     *************************************************************************/
    private static int[][] readBordFromFile(String fileName) {
        In boardFile = new In(fileName);
        int n = boardFile.readInt();
        int[][] boardFromFile = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boardFromFile[i][j] = boardFile.readInt();
            }
        }
        return boardFromFile;
    }

    /*************************************************************************
     * test client
     * @param args name of txt file with board
     *************************************************************************/
    public static void main(String[] args) {
        Board board = new Board(readBordFromFile(args[0]));

        System.out.println(board);
        for (Board neighbor : board.neighbors()) {
            System.out.println(neighbor);
        }

        /*
        System.out.println(board);
        System.out.println(board.twin());
        System.out.println("is goal " + board.isGoal());
        System.out.println("hamming distance = " + board.hamming());
        System.out.println("manhattan = " + board.manhattan());
        Board anotherBoard = new Board(readBordFromFile(args[1]));

        System.out.println(anotherBoard);
        System.out.println(anotherBoard.twin());
        System.out.println("is goal " + anotherBoard.isGoal());
        System.out.println("hamming distance = " + anotherBoard.hamming());
        System.out.println("manhattan = " + anotherBoard.manhattan());
        System.out.println("are equal " + board.equals(anotherBoard));
        */
    }
}
