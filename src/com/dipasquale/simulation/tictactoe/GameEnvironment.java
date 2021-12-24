package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.Environment;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.IntStream;

public final class GameEnvironment implements Environment<GameState> {
    private static final int MAXIMUM_PLAYS = 9;
    private static final int NO_PARTICIPANT_ID = -1;
    private static final int NO_LOCATION = -1;
    private static final int UNPLAYED_STATE = 0;
    private final Object membership;
    private final int[] board;
    private final int plays;
    @Getter
    private final GameState currentState;
    @Getter
    private final int statusId;

    GameEnvironment() {
        this.membership = new Object();
        this.board = new int[MAXIMUM_PLAYS];
        this.plays = 0;
        this.currentState = new GameState(null, NO_PARTICIPANT_ID, NO_LOCATION);
        this.statusId = MonteCarloTreeSearch.IN_PROGRESS;
    }

    private GameEnvironment(final int[] board, final int plays, final GameState currentState, final int statusId) {
        this.membership = new Object();
        this.board = board;
        this.plays = plays;
        this.currentState = currentState;
        this.statusId = statusId;
    }

    private int getNextParticipantId() {
        if (plays == 0) {
            return 1;
        }

        return 3 - currentState.getParticipantId();
    }

    public GameState createPossibleState(final int location) {
        if (location < 0 || location >= MAXIMUM_PLAYS || board[location] != UNPLAYED_STATE) {
            String message = String.format("game state is not possible due to location: %d", location);

            throw new IllegalArgumentException(message);
        }

        int participantId = getNextParticipantId();

        return new GameState(membership, participantId, location);
    }

    @Override
    public Iterable<GameState> createAllPossibleStates() {
        int participantId = getNextParticipantId();

        return IntStream.range(0, board.length)
                .filter(i -> board[i] == UNPLAYED_STATE)
                .mapToObj(i -> new GameState(membership, participantId, i))
                ::iterator;
    }

    private static int[] createBoard(final int[] board, final GameState state) {
        int[] boardCopied = Arrays.copyOf(board, 9);

        boardCopied[state.getLocation()] = state.getParticipantId();

        return boardCopied;
    }

    private static boolean isRowTaken(final int[] board, final int participantId) {
        for (int i1 = 0; i1 < 9; i1 += 3) {
            boolean taken = true;

            for (int i2 = 0; taken && i2 < 3; i2++) {
                taken = board[i2 + i1] == participantId;
            }

            if (taken) {
                return true;
            }
        }

        return false;
    }

    private static boolean isColumnTaken(final int[] board, final int participantId) {
        for (int i1 = 0; i1 < 3; i1++) {
            boolean taken = true;

            for (int i2 = 0; taken && i2 < 9; i2 += 3) {
                taken = board[i2 + i1] == participantId;
            }

            if (taken) {
                return true;
            }
        }

        return false;
    }

    private static boolean isDiagonal1Taken(final int[] board, final int participantId) {
        boolean taken = true;

        for (int i = 0; taken && i < 9; i += 4) {
            taken = board[i] == participantId;
        }

        return taken;
    }

    private static boolean isDiagonal2Taken(final int[] board, final int participantId) {
        boolean taken = true;

        for (int i = 2; taken && i < 7; i += 2) {
            taken = board[i] == participantId;
        }

        return taken;
    }

    private static boolean isWon(final int[] board, final int participantId) {
        return isRowTaken(board, participantId) || isColumnTaken(board, participantId) || isDiagonal1Taken(board, participantId) || isDiagonal2Taken(board, participantId);
    }

    private static int getStatusId(final int[] board, final int plays, final int participantId) {
        if (isWon(board, participantId)) {
            return participantId;
        }

        if (plays == MAXIMUM_PLAYS) {
            return MonteCarloTreeSearch.DRAWN;
        }

        return MonteCarloTreeSearch.IN_PROGRESS;
    }

    @Override
    public GameEnvironment accept(final GameState state) {
        if (state.getMembership() != membership) {
            throw new IllegalArgumentException("game state does not belong to the environment");
        }

        int[] boardCopied = createBoard(board, state);
        int playsFixed = plays + 1;
        int newStatusId = getStatusId(boardCopied, playsFixed, state.getParticipantId());

        return new GameEnvironment(boardCopied, playsFixed, state, newStatusId);
    }
}
