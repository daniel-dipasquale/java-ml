package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.Environment;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public final class GameEnvironment implements Environment<GameState, GameEnvironment> {
    private static final int MAXIMUM_MOVES = 9;
    private static final int NO_PARTICIPANT_ID = -1;
    private static final int NO_LOCATION = -1;
    private static final int UNPLAYED_STATE = 0;
    private final Object membership;
    @Getter(AccessLevel.PACKAGE)
    private final int[] board;
    @Getter(AccessLevel.PACKAGE)
    private final List<Integer> moves;
    @Getter
    private final GameState currentState;
    @Getter
    private final int statusId;

    GameEnvironment() {
        this.membership = new Object();
        this.board = new int[MAXIMUM_MOVES];
        this.moves = List.of();
        this.currentState = new GameState(null, NO_PARTICIPANT_ID, NO_LOCATION);
        this.statusId = MonteCarloTreeSearch.IN_PROGRESS;
    }

    private GameEnvironment(final int[] board, final List<Integer> moves, final GameState currentState, final int statusId) {
        this.membership = new Object();
        this.board = board;
        this.moves = moves;
        this.currentState = currentState;
        this.statusId = statusId;
    }

    @Override
    public int getNextParticipantId() {
        if (moves.isEmpty()) {
            return 1;
        }

        return 3 - currentState.getParticipantId();
    }

    public GameState createPossibleState(final int location) {
        if (location < 0 || location >= MAXIMUM_MOVES || board[location] != UNPLAYED_STATE) {
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
                .filter(index -> board[index] == UNPLAYED_STATE)
                .mapToObj(index -> new GameState(membership, participantId, index))
                ::iterator;
    }

    private static int[] createBoard(final int[] board, final GameState state) {
        int[] boardFixed = Arrays.copyOf(board, 9);

        boardFixed[state.getLocation()] = state.getParticipantId();

        return boardFixed;
    }

    private static List<Integer> createMoves(final List<Integer> moves, final GameState state) {
        List<Integer> movesFixed = new ArrayList<>(moves);

        movesFixed.add(state.getLocation());

        return movesFixed;
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

    private static int getStatusId(final int[] board, final List<Integer> moves, final int participantId) {
        if (isWon(board, participantId)) {
            return participantId;
        }

        if (moves.size() == MAXIMUM_MOVES) {
            return MonteCarloTreeSearch.DRAWN;
        }

        return MonteCarloTreeSearch.IN_PROGRESS;
    }

    @Override
    public GameEnvironment accept(final GameState state) {
        if (state.getMembership() != membership) {
            throw new IllegalArgumentException("game state does not belong to the environment");
        }

        int[] boardFixed = createBoard(board, state);
        List<Integer> movesFixed = createMoves(moves, state);
        int statusIdFixed = getStatusId(boardFixed, movesFixed, state.getParticipantId());

        return new GameEnvironment(boardFixed, movesFixed, state, statusIdFixed);
    }
}
