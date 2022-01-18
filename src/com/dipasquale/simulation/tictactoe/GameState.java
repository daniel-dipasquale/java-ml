package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.core.State;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class GameState implements State<GameAction, GameState> {
    private static final int MAXIMUM_MOVES = 9;
    private static final int NO_PARTICIPANT_ID = -1;
    private static final int NO_LOCATION = -1;
    private static final int NOT_PLAYED_VALUE = 0;
    private final Object membership;
    @Getter(AccessLevel.PACKAGE)
    private final int[] board;
    @Getter(AccessLevel.PACKAGE)
    @EqualsAndHashCode.Include
    private final List<Integer> moves;
    @Getter
    private final GameAction lastAction;
    @Getter
    private final int statusId;

    GameState() {
        this.membership = new Object();
        this.lastAction = new GameAction(null, NO_PARTICIPANT_ID, NO_LOCATION);
        this.board = new int[MAXIMUM_MOVES];
        this.moves = List.of();
        this.statusId = MonteCarloTreeSearch.IN_PROGRESS;
    }

    private GameState(final GameAction lastAction, final int[] board, final List<Integer> moves, final int statusId) {
        this.membership = new Object();
        this.lastAction = lastAction;
        this.board = board;
        this.moves = moves;
        this.statusId = statusId;
    }

    @Override
    public int getNextParticipantId() {
        if (moves.isEmpty()) {
            return 1;
        }

        return 3 - lastAction.getParticipantId();
    }

    public GameAction createValidAction(final int location) {
        if (location < 0 || location >= MAXIMUM_MOVES || board[location] != NOT_PLAYED_VALUE) {
            String message = String.format("game action is not valid due to location: %d", location);

            throw new IllegalArgumentException(message);
        }

        int participantId = getNextParticipantId();

        return new GameAction(membership, participantId, location);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        int participantId = getNextParticipantId();

        return IntStream.range(0, board.length)
                .filter(index -> board[index] == NOT_PLAYED_VALUE)
                .mapToObj(index -> new GameAction(membership, participantId, index))
                ::iterator;
    }

    private static int[] createBoard(final int[] board, final GameAction action) {
        int[] boardFixed = Arrays.copyOf(board, 9);

        boardFixed[action.getLocation()] = action.getParticipantId();

        return boardFixed;
    }

    private static List<Integer> createMoves(final List<Integer> moves, final GameAction action) {
        List<Integer> movesFixed = new ArrayList<>(moves);

        movesFixed.add(action.getLocation());

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
    public GameState accept(final GameAction action) {
        if (action.getMembership() != membership) {
            throw new IllegalArgumentException("action does not belong to the game state");
        }

        int[] boardFixed = createBoard(board, action);
        List<Integer> movesFixed = createMoves(moves, action);
        int statusIdFixed = getStatusId(boardFixed, movesFixed, action.getParticipantId());

        return new GameState(action, boardFixed, movesFixed, statusIdFixed);
    }
}
