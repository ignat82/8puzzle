/* *****************************************************************************
 * Solver class for finding shortest sequence of moves, leading given board to
 * its goal state (when all the tiles are on their places
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {
    private int moves;
    private final Queue<Board> solution = new Queue<Board>();

    /**************************************************************************
     * all the job is being done at constructor. it processes simultaneously
     * the board and its twin (when the board has no solution - its twin does)
     * the way the solution is being looked for - calculate the priority based
     * on manhattan distance and moves from initial board for all the neighbors
     * of currently processed bard.
     * these neighbors are put then to priority que and the process repeats
     * on the next step with another board with lowest priority, extracted from
     * que. the sequence of boards is stored in tree structure. Win sequence is
     * reconstructed after finding the solution by travelling from current node
     * up to initial bard
     * @param initial initial bord from which we're starting the game
     *************************************************************************/
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException("Solver construcor argument is null");
        }
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>(new SearchNodeComparator());
        MinPQ<SearchNode> twinPq = new MinPQ<SearchNode>(new SearchNodeComparator());
        pq.insert(new SearchNode(initial, null));
        twinPq.insert(new SearchNode(initial.twin(), null));
        boolean gotSolution = false;
        while (!gotSolution) {
            SearchNode removedNode = pq.delMin();
            if (removedNode.board.isGoal()) {
                moves = removedNode.moves;
                do {
                    solution.enqueue(removedNode.board);
                    removedNode = removedNode.previous;
                } while (removedNode != null);
                gotSolution = true;
            }
            else {
                for (Board neighbor : removedNode.board.neighbors()) {
                    if ((removedNode.previous == null) || !neighbor
                            .equals(removedNode.previous.board)) {
                        pq.insert(new SearchNode(neighbor, removedNode));
                    }
                }
            }
            SearchNode twinRemovedNode = twinPq.delMin();
            if (twinRemovedNode.board.isGoal()) {
                moves = -1;
                gotSolution = true;
            }
            else {
                for (Board twinNeighbor : twinRemovedNode.board.neighbors()) {
                    if ((twinRemovedNode.previous == null) || !twinNeighbor
                            .equals(twinRemovedNode.previous.board)) {
                        twinPq.insert(new SearchNode(twinNeighbor, twinRemovedNode));
                    }
                }
            }
        }
    }

    /**************************************************************************
     * nested class for storing the game trees for board and its twin board
     *************************************************************************/
    private class SearchNode {
        private final Board board;
        private final int moves;
        private final int manhattan;
        private final SearchNode previous;

        /**********************************************************************
         * constructor
         * @param board - board of current step
         * @param previous - link to previous node
         *********************************************************************/
        SearchNode(Board board, SearchNode previous) {
            if (board == null) {
                throw new IllegalArgumentException("SearchNode constructor argument is null");
            }
            this.board = board;
            this.previous = previous;
            if (previous == null) {
                moves = 0;
            }
            else {
                moves = previous.moves + 1;
            }
            manhattan = board.manhattan();
        }
    }

    /**************************************************************************
     * Comparator, which helps to decide which node should be processed next
     * when finding the solution
     *************************************************************************/
    private class SearchNodeComparator implements Comparator<SearchNode> {
        public int compare(SearchNode a, SearchNode b) {
            if (a.manhattan + a.moves == b.manhattan + b.moves) {
                return a.manhattan - b.manhattan;
            }
            else {
                return a.manhattan + a.moves - b.manhattan - b.moves;
            }
        }

    }

    /*************************************************************************
     * check if the solution was found for board (moves equals or greater than zero)
     * or for its twin (when finding solution for twin setting moves to -1)
     * finding solution for twin means that board itself is unsolvable
     * @return true if solution was found and else otherwise
     *************************************************************************/
    public boolean isSolvable() {
        if (moves == -1) {
            return false;
        }
        return true;
    }

    /**************************************************************************
     * getter for number of moves to solution
     * @return number of moves to solution or -1 for unsolvable boards
     *************************************************************************/
    public int moves() {
        return moves;
    }

    /**************************************************************************
     * copying the sequence of boards from goal board to  initial board
     * to encapsulate the solution and reverse it (return from initial to goal)
     * @return Stack of boards from initial to goal
     *************************************************************************/
    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        }
        else {
            Stack<Board> copySolution = new Stack<Board>();
            for (Board board : solution) {
                copySolution.push(board);
            }
            return copySolution;
        }
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
     * @param args txt file with board
     ************************************************************************/
    public static void main(String[] args) {
        Board initial = new Board(readBordFromFile(args[0]));
        System.out.println("inital board is " + initial);
        System.out.println("with Manhattan " + initial.manhattan());
        // solve the slider puzzle
        Solver solver = new Solver(initial);
        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        }
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());

            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}

