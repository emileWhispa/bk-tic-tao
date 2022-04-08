package com.example.tictao.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    public ResponseEntity<String> run(String board) {
        if (board.length() != 9) {
            return ResponseEntity.badRequest().body("Invalid tic-tac-toe board length (9 characters required !)");
        }


        boolean allMatch = board.chars().allMatch(e -> e == ' ' || e == 'x' || e == 'o');

        if (!allMatch) {
            return ResponseEntity.badRequest().body("Invalid tic-tac-toe board characters (x,o,space) allowed");
        }


        int[] turns = board.chars().toArray();


        if (checkWinner(turns, 'x')) {
            return ResponseEntity.ok("Congratulations you win a game !!");
        }

        if (checkWinner(turns, 'o')) {
            return ResponseEntity.ok("Computer win a game !!");
        }

        return ResponseEntity.ok(buildNewBoard(board));
    }


    public String buildNewBoard(String board) {

        int[] array = board.chars().toArray();

        int index = checkPossibleRivalWin(array);

        //If index greater than -1 means that we found line that contains 2 rival positions we need to replace remaining space with o
        if (index > -1) {
            StringBuilder builder = new StringBuilder(board);
            builder.setCharAt(index, 'o');
            return builder.toString();
        }

        int index1 = checkPossibleNextMoveIndex(array);


        //If index greater than -1 mean that we have free line to play and win the game
        // We do this after checking our rival potential move which can lead to their win
        if(index1>-1){
            StringBuilder builder = new StringBuilder(board);
            builder.setCharAt(index1, 'o');
            return builder.toString();
        }


        //Else we check if there is any remaining place to play
        int position = getPlayerEmptyPosition(array);

        //If position greater than one means that we found empty position
        if(position>-1){
            StringBuilder builder = new StringBuilder(board);
            builder.setCharAt(position, 'o');
            return builder.toString();
        }

        //Finally, if none of above conditions succeeded we return the normal board
        return board;
    }



    // This function is used to check if user can win a game with his next move and computer decide where to play
    public int checkPossibleRivalWin(int[] turns) {

        List<int[]> moveWinIndex = getPossibleNextMoveWinIndex();

        List<int[]> combinationIndex = getPossibleCombinationIndex();

        for (int[] players : moveWinIndex) {
            for (int[] indexes : combinationIndex) {
                boolean validRow = checkValidRow(turns, indexes, players);
                int emptyPosition = getPlayerEmptyPosition(players);
                if (validRow && emptyPosition >= 0) {
                    return indexes[emptyPosition];
                }
            }
        }

        return -1;
    }

    // This function is used to check if computer have free line where he can complete and win a game
    public int checkPossibleNextMoveIndex(int[] turns) {

        List<int[]> playIndex = getPossibleToPlayIndex();

        List<int[]> possibleCombinationIndex = getPossibleCombinationIndex();

        for (int[] players : playIndex) {
            for (int[] indexes : possibleCombinationIndex) {
                boolean validRow = checkValidRow(turns, indexes, players);
                if (validRow) {
                    int position = getPlayerEmptyPosition(players);
                    if (position >= 0) {
                        return indexes[position];
                    }
                }
            }
        }

        return -1;
    }

    // Get index position on row to be replaced with an empty space with o
    public int getPlayerEmptyPosition(int[] players) {
        int index = 0;
        for (int c : players) {
            if (c == ' ') {
                return index;
            }
            index++;
        }

        return -1;
    }


    /*
        This function receive board characters array and indexes and play characters (x,o,space) to check their position and
        existence on row
     */
    public boolean checkValidRow(int[] turns, int[] indexes, int[] plays) {
        return turns[indexes[0]] == plays[0] && turns[indexes[1]] == plays[1] && turns[indexes[2]] == plays[2];
    }

    //Get all possibilities where rival can can win a game
    public List<int[]> getPossibleNextMoveWinIndex() {
        return List.of(
                new int[]{'x', 'x', ' '},
                new int[]{' ', 'x', 'x'},
                new int[]{'x', ' ', 'x'}
        );
    }

    //Get all possibilities where you have two empty spaces to play
    public List<int[]> getPossibleToPlayIndex() {
        return List.of(
                new int[]{'o', ' ', ' '},
                new int[]{' ', 'o', ' '},
                new int[]{' ', ' ', 'o'}
        );
    }

    public List<int[]> getPossibleCombinationIndex() {
        return List.of(
                new int[]{0, 1, 2}, //Player wins on the first horizontal line of tic-tac-toe board
                new int[]{3, 4, 5}, //Player wins on the second horizontal line of tic-tac-toe board
                new int[]{6, 7, 8}, //Player wins on the third horizontal line of tic-tac-toe board
                new int[]{0, 3, 6}, //Player wins on the first vertical line of tic-tac-toe board
                new int[]{1, 4, 7}, //Player wins on the second vertical line of tic-tac-toe board
                new int[]{2, 5, 8}, //Player wins on the third vertical line of tic-tac-toe board
                new int[]{0, 4, 8}, //Player wins on the diagonal line from top-left to bottom-right of tic-tac-toe board
                new int[]{2, 4, 6});//Player wins on the diagonal line from top-right to bottom-left of tic-tac-toe board
    }

    boolean checkWinner(int[] turns, char player) {

        int[] players = {player, player, player};


        List<int[]> combinationIndex = getPossibleCombinationIndex();


        return combinationIndex.stream().anyMatch(e -> checkValidRow(turns, e, players));
    }
}
