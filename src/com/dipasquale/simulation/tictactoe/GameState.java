package com.dipasquale.simulation.tictactoe;

import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.IntStream;

@Getter
public final class GameState implements State<GameAction, GameState> {
    private static final String INITIAL_CACHE_ID = "";
    private static final int DIMENSION = 3;
    static final int BOARD_LENGTH = DIMENSION * DIMENSION;
    static final int NO_PARTICIPANT_ID = 0;
    private static final int MAXIMUM_BITS_PER_TILE = 2;
    private static final BitManipulatorSupport BIT_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(MAXIMUM_BITS_PER_TILE);
    @Getter(AccessLevel.NONE)
    private final int board;
    private final int depth;
    private final GameAction lastAction;
    private final int statusId;

    GameState() {
        this.board = 0;
        this.depth = 0;
        this.lastAction = new GameAction(null, INITIAL_CACHE_ID, MonteCarloTreeSearch.INITIAL_ACTION_ID, MonteCarloTreeSearch.INITIAL_PARTICIPANT_ID);
        this.statusId = MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    private GameState(final int board, final GameAction lastAction, final int statusId) {
        this.board = board;
        this.depth = lastAction.getCacheId().length();
        this.lastAction = lastAction;
        this.statusId = statusId;
    }

    @Override
    public int getNextParticipantId() {
        if (lastAction.getCacheId().isEmpty()) {
            return 1;
        }

        return 3 - lastAction.getParticipantId();
    }

    private static int extractValueFromBoard(final int board, final int actionId) {
        return BIT_MANIPULATOR_SUPPORT.extract(board, actionId);
    }

    public int getActionOwnerParticipantId(final int actionId) {
        return extractValueFromBoard(board, actionId);
    }

    @Override
    public boolean isValid(final int actionId) {
        return actionId >= 0 && actionId < BOARD_LENGTH && getActionOwnerParticipantId(actionId) == NO_PARTICIPANT_ID;
    }

    private String getParentCacheId() {
        return lastAction.getCacheId();
    }

    private String createNextCacheId(final int actionId) {
        return String.format("%s%d", getParentCacheId(), actionId);
    }

    public GameAction createAction(final int id) {
        if (!isValid(id)) {
            String message = String.format("game action is not valid due to id: %d", id);

            throw new IllegalArgumentException(message);
        }

        String parentCacheId = getParentCacheId();
        String cacheId = createNextCacheId(id);
        int participantId = getNextParticipantId();

        return new GameAction(parentCacheId, cacheId, id, participantId);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        String parentCacheId = getParentCacheId();
        int participantId = getNextParticipantId();

        return IntStream.range(0, BOARD_LENGTH)
                .filter(actionId -> getActionOwnerParticipantId(actionId) == NO_PARTICIPANT_ID)
                .mapToObj(actionId -> new GameAction(parentCacheId, createNextCacheId(actionId), actionId, participantId))
                ::iterator;
    }

    public int[] replicateActionIds() {
        String cacheId = lastAction.getCacheId();

        return IntStream.range(0, cacheId.length())
                .mapToObj(cacheId::charAt)
                .mapToInt(actionId -> actionId - '0')
                .toArray();
    }

    private static int mergeValueToBoard(final int board, final int actionId, final int value) {
        return BIT_MANIPULATOR_SUPPORT.merge(board, actionId, value);
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

    private static int getStatusId(final int board, final GameAction action) {
        int participantId = action.getParticipantId();
        int actions = action.getCacheId().length();

        if (actions >= 5 && isWon(board, participantId)) {
            return participantId;
        }

        if (actions == BOARD_LENGTH) {
            return MonteCarloTreeSearch.DRAWN_STATUS_ID;
        }

        return MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    @Override
    public GameState accept(final GameAction action, final boolean simulation) {
        if (!Objects.equals(getParentCacheId(), action.getParentCacheId())) {
            throw new IllegalArgumentException("action does not belong to the game state");
        }

        int nextBoard = createBoard(board, action);
        int nextStatusId = getStatusId(nextBoard, action);

        return new GameState(nextBoard, action, nextStatusId);
    }
}
