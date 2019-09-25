import java.util.*;

public class Player {
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */

    private int piece = 0;
    private final int MAX_DEPTH = 4;

    private int[][] heuristics = {
        {      1,   -10,  -100, -2001, -100000},
        {     10,     0,     0,     0,      0 },
        {    100,     0,     0,     0,      0 },
        {   1000,     0,     0,     0,      0 },
        { 200000,     0,     0,     0,      0 },
    };

    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();

        //hashset of boards and its value
        Hashtable<String, Integer> HashSet = new Hashtable<String, Integer>();

        gameState.findPossibleMoves(nextStates);
        piece = gameState.getNextPlayer();
        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        int best_possible = -10000000;
        GameState best_state = new GameState();

        for(int i=0; i<nextStates.size(); i++){
            int utility = minimax(nextStates.get(i), Constants.CELL_X, 0, -100000000, 100000000, HashSet);
            if(utility>best_possible) {
                best_possible = utility;
                best_state = nextStates.get(i);
            }
        }
        return best_state;
    }

    public int minimax(final GameState gameState, int player, int depth, int alpha, int beta, Hashtable<String, Integer>  HashSet){
        if(gameState.isEOG()) return utility(gameState);
        if(depth >= MAX_DEPTH) return utility(gameState);
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        Collections.shuffle(nextStates);
        if(player == Constants.CELL_O){
            int best_possible = -100000000;
            int utility = 0;
            for(int i=0; i<nextStates.size(); i++){
                if(HashSet.containsKey(nextStates.get(i).toMessage())) utility = HashSet.get(nextStates.get(i).toMessage());
                else {
                    utility = minimax(nextStates.get(i),Constants.CELL_X, depth+1, alpha, beta, HashSet);
                    if(depth > 0 && depth < MAX_DEPTH-1) HashSet.put(nextStates.get(i).toMessage(), utility);
                }
                if(utility>best_possible) best_possible = utility;
                if(best_possible>alpha) alpha = best_possible;
                if(alpha>=beta) break;
            }
            return best_possible;
        }

        else {
            int best_possible = 100000000;
            int utility = 0;
            for(int i=0; i<nextStates.size(); i++){
                if(HashSet.containsKey(nextStates.get(i).toMessage())) utility = HashSet.get(nextStates.get(i).toMessage());
                else {
                    utility = minimax(nextStates.get(i),Constants.CELL_O, depth+1, alpha, beta, HashSet);
                    if(depth > 0 && depth < MAX_DEPTH-1) HashSet.put(nextStates.get(i).toMessage(), utility);
                }
                if(utility<best_possible) best_possible = utility;
                if(best_possible<beta) beta = best_possible;
                if(beta<=alpha) break;
            }
            return best_possible;
        }

    }

    public int utility(GameState gameState){
        if(gameState.isXWin()){
            if(piece == Constants.CELL_X) return 1000000;
            else return 1000000;
        }
        if(gameState.isOWin()){
            if(piece == Constants.CELL_X) return -1000000;
            else return -1000000;
        }
        else{
            int home = 0;
            int away = 0;
            int total = 0;

            // checking for rows
            for(int i=0; i<GameState.BOARD_SIZE; i++){
                home = 0;
                away = 0;
                for(int j=0; j<GameState.BOARD_SIZE; j++) {
                    if (gameState.at(i,j) == piece) home++;
                    else if (gameState.at(i,j) == Constants.CELL_EMPTY) ;
                    else away++;
                }
                total += heuristics[home][away];
            }

            // checking for cols
            for(int i=0; i<GameState.BOARD_SIZE; i++){
                home = 0;
                away = 0;
                for(int j=0; j<GameState.BOARD_SIZE; j++) {
                    if (gameState.at(j,i) == piece) home++;
                    else if (gameState.at(j,i) == Constants.CELL_EMPTY) ;
                    else away++;
                }
                total += heuristics[home][away];
            }

            // checking for diags
            home = 0;
            away = 0;
            for(int i=0; i<GameState.BOARD_SIZE; i++){
                if (gameState.at(i,i) == piece) home++;
                else if (gameState.at(i,i) == Constants.CELL_EMPTY) ;
                else away++;
            }
            total += heuristics[home][away];

            // checking for diags
            home = 0;
            away = 0;
            for(int i=0; i<GameState.BOARD_SIZE; i++){
                if (gameState.at(GameState.BOARD_SIZE-i-1,i) == piece) home++;
                else if (gameState.at(GameState.BOARD_SIZE-i-1,i)== Constants.CELL_EMPTY) ;
                else away++;
            }
            total += heuristics[home][away];

            return total;
        }
    }
}

