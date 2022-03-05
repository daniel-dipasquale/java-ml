package com.dipasquale.simulation.tictactoe;

import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.State;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class GameState implements State<GameAction, GameState> {
    private static final int DIMENSION = 3;
    static final int BOARD_LENGTH = DIMENSION * DIMENSION;
    static final int NO_PARTICIPANT_ID = 0;
    private static final int MAXIMUM_BITS_PER_TILE = 2;
    private static final BitManipulatorSupport BIT_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(MAXIMUM_BITS_PER_TILE);
    private final Object membership;
    private final int board;
    @Getter
    @EqualsAndHashCode.Include
    private final List<Integer> actionIds;
    @Getter
    private final GameAction lastAction;
    @Getter
    private final int statusId;

    GameState() {
        this.membership = new Object();
        this.board = 0;
        this.actionIds = List.of();
        this.lastAction = new GameAction(null, MonteCarloTreeSearch.INITIAL_ACTION_ID, MonteCarloTreeSearch.INITIAL_PARTICIPANT_ID);
        this.statusId = MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    private GameState(final int board, final List<Integer> actionIds, final GameAction lastAction, final int statusId) {
        this.membership = new Object();
        this.board = board;
        this.actionIds = actionIds;
        this.lastAction = lastAction;
        this.statusId = statusId;
    }

    @Override
    public int getNextParticipantId() {
        if (actionIds.isEmpty()) {
            return 1;
        }

        return 3 - lastAction.getParticipantId();
    }

    private static int extractValueFromBoard(final int board, final int actionId) {
        return BIT_MANIPULATOR_SUPPORT.extract(board, actionId);
    }

    int getParticipantIdForAction(final int actionId) {
        return extractValueFromBoard(board, actionId);
    }

    @Override
    public boolean isValid(final int actionId) {
        return actionId >= 0 && actionId < BOARD_LENGTH && getParticipantIdForAction(actionId) == NO_PARTICIPANT_ID;
    }

    public GameAction createAction(final int id) {
        if (!isValid(id)) {
            String message = String.format("game action is not valid due to id: %d", id);

            throw new IllegalArgumentException(message);
        }

        int participantId = getNextParticipantId();

        return new GameAction(membership, id, participantId);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        int participantId = getNextParticipantId();

        return IntStream.range(0, BOARD_LENGTH)
                .filter(actionId -> getParticipantIdForAction(actionId) == NO_PARTICIPANT_ID)
                .mapToObj(actionId -> new GameAction(membership, actionId, participantId))
                ::iterator;
    }

    private static int mergeValueToBoard(final int board, final int actionId, final int value) {
        return BIT_MANIPULATOR_SUPPORT.merge(board, actionId, value);
    }

    private static List<Integer> createActionIds(final List<Integer> actionIds, final GameAction action) {
        List<Integer> actionIdsFixed = new ArrayList<>(actionIds);

        actionIdsFixed.add(action.getId());

        return Collections.unmodifiableList(actionIdsFixed);
    }

    private static int createBoard(final int board, final GameAction action) {
        return mergeValueToBoard(board, action.getId(), action.getParticipantId());
    }

    private static boolean isRowOrColumnTaken(final int board, final int participantId) {
        for (int i1 = 0; i1 < DIMENSION; i1++) {
            boolean taken1 = true;
            boolean taken2 = true;

            for (int i2 = 0; (taken1 || taken2) && i2 < DIMENSION; i2++) {
                taken1 &= extractValueFromBoard(board, i1 * DIMENSION + i2) == participantId;
                taken2 &= extractValueFromBoard(board, i2 * DIMENSION + i1) == participantId;
            }

            if (taken1 || taken2) {
                return true;
            }
        }

        return false;
    }

    private static boolean isDiagonalTaken(final int board, final int participantId) {
        boolean taken1 = true;
        boolean taken2 = true;

        for (int i = 0; (taken1 || taken2) && i < DIMENSION; i++) {
            taken1 &= extractValueFromBoard(board, i * DIMENSION + i) == participantId;
            taken2 &= extractValueFromBoard(board, i * DIMENSION + (DIMENSION - i - 1)) == participantId;
        }

        return taken1 || taken2;
    }

    private static boolean isWon(final int board, final int participantId) {
        return isRowOrColumnTaken(board, participantId) || isDiagonalTaken(board, participantId);
    }

    private static int getStatusId(final int board, final List<Integer> actionIds, final int participantId) {
        if (isWon(board, participantId)) {
            return participantId;
        }

        if (actionIds.size() == BOARD_LENGTH) {
            return MonteCarloTreeSearch.DRAWN_STATUS_ID;
        }

        return MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    @Override
    public GameState accept(final GameAction action, final boolean simulation) {
        if (action.getMembership() != membership) {
            throw new IllegalArgumentException("action does not belong to the game state");
        }

        int boardFixed = createBoard(board, action);
        List<Integer> actionIdsFixed = createActionIds(actionIds, action);
        int statusIdFixed = getStatusId(boardFixed, actionIdsFixed, action.getParticipantId());

        return new GameState(boardFixed, actionIdsFixed, action, statusIdFixed);
    }
}
