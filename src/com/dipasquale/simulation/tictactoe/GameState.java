package com.dipasquale.simulation.tictactoe;

import com.dipasquale.common.bit.int1.BitManipulatorSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class GameState implements State<GameAction, GameState> {
    private static final int INITIAL_BOARD = 0;
    private static final String INITIAL_ACTION_IDS = "";
    private static final int FIRST_PARTICIPANT_ID = 2;
    static final boolean IS_INTENTIONAL = true;
    private static final int DIMENSION = 3;
    static final int BOARD_LENGTH = DIMENSION * DIMENSION;
    static final int NO_PARTICIPANT_ID = 0;
    private static final int MAXIMUM_BITS_PER_TILE = 2;
    private static final BitManipulatorSupport BIT_MANIPULATOR_SUPPORT = BitManipulatorSupport.create(MAXIMUM_BITS_PER_TILE);
    @Getter(AccessLevel.NONE)
    private final int board;
    @Getter(AccessLevel.NONE)
    private final String actionIds;
    private final int statusId;
    private final int participantId;

    GameState() {
        this(INITIAL_BOARD, INITIAL_ACTION_IDS, MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID, FIRST_PARTICIPANT_ID);
    }

    @Override
    public int getNextParticipantId() {
        return 3 - participantId;
    }

    @Override
    public boolean isIntentional() {
        return IS_INTENTIONAL;
    }

    @Override
    public boolean isNextIntentional() {
        return IS_INTENTIONAL;
    }

    @Override
    public GameAction createRootAction() {
        return new GameAction(MonteCarloTreeSearch.ROOT_ACTION_ID);
    }

    private static int extractValueFromBoard(final int board, final int actionId) {
        return BIT_MANIPULATOR_SUPPORT.extract(board, actionId);
    }

    public int getOwnerParticipantId(final int actionId) {
        return extractValueFromBoard(board, actionId);
    }

    private boolean isValid(final int actionId) {
        return actionId >= 0 && actionId < BOARD_LENGTH && getOwnerParticipantId(actionId) == NO_PARTICIPANT_ID;
    }

    private GameAction createNextAction(final int id) {
        return new GameAction(id);
    }

    public GameAction createAction(final int id) {
        if (isValid(id)) {
            return createNextAction(id);
        }

        String message = String.format("game action is not valid due to id: %d", id);

        throw new IllegalArgumentException(message);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        return IntStream.range(0, BOARD_LENGTH)
                .filter(actionId -> getOwnerParticipantId(actionId) == NO_PARTICIPANT_ID)
                .mapToObj(this::createNextAction)
                ::iterator;
    }

    public int[] replicateActionIds() {
        return IntStream.range(0, actionIds.length())
                .mapToObj(actionIds::charAt)
                .mapToInt(actionId -> actionId - '0')
                .toArray();
    }

    private static int mergeValueToBoard(final int board, final int actionId, final int value) {
        return BIT_MANIPULATOR_SUPPORT.merge(board, actionId, value);
    }

    private static int createBoard(final int board, final GameAction action, final int participantId) {
        return mergeValueToBoard(board, action.getId(), participantId);
    }

    private static String createActionIds(final String actionIds, final int actionId) {
        return String.format("%s%d", actionIds, actionId);
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

    private static int getStatusId(final int board, final String actionIds, final int participantId) {
        int actionCount = actionIds.length();

        if (actionCount >= 5 && isWon(board, participantId)) {
            return participantId;
        }

        if (actionCount == BOARD_LENGTH) {
            return MonteCarloTreeSearch.DRAWN_STATUS_ID;
        }

        return MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    @Override
    public GameState accept(final GameAction action) {
        int nextParticipantId = getNextParticipantId();
        int nextBoard = createBoard(board, action, nextParticipantId);
        String nextActionIds = createActionIds(actionIds, action.getId());
        int nextStatusId = getStatusId(nextBoard, nextActionIds, nextParticipantId);

        return new GameState(nextBoard, nextActionIds, nextStatusId, nextParticipantId);
    }
}
