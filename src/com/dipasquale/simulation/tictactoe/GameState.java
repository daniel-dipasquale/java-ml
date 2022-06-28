package com.dipasquale.simulation.tictactoe;

import com.dipasquale.common.bit.VectorManipulatorSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameState implements State<GameAction, GameState> {
    private static final int EMPTY_BOARD_VECTOR = 0;
    private static final List<Integer> EMPTY_LOCATION_IDS = List.of();
    private static final int FIRST_PARTICIPANT_ID = 2;
    private static final boolean IS_INTENTIONAL = true;
    private static final int VECTOR_DIMENSION_SIZE = 3;
    public static final int BOARD_VECTOR_SIZE = VECTOR_DIMENSION_SIZE * VECTOR_DIMENSION_SIZE;
    public static final int NO_PARTICIPANT_ID = 0;
    private static final int MAXIMUM_BITS_PER_TILE = 2;
    private static final VectorManipulatorSupport BOARD_VECTOR_MANIPULATOR_SUPPORT = VectorManipulatorSupport.create(MAXIMUM_BITS_PER_TILE);
    private final int boardVector;
    @Getter
    private final List<Integer> locationIds;
    @Getter
    private final int statusId;
    @Getter
    private final int participantId;

    GameState() {
        this(EMPTY_BOARD_VECTOR, EMPTY_LOCATION_IDS, MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID, FIRST_PARTICIPANT_ID);
    }

    @Override
    public int getNextParticipantId() {
        return 3 - participantId;
    }

    @Override
    public boolean isActionIntentional() {
        return IS_INTENTIONAL;
    }

    @Override
    public boolean isNextActionIntentional() {
        return IS_INTENTIONAL;
    }

    private static int extractParticipantIdFromBoardVector(final int boardVector, final int locationId) {
        return BOARD_VECTOR_MANIPULATOR_SUPPORT.extract(boardVector, locationId);
    }

    public int getParticipantId(final int locationId) {
        return extractParticipantIdFromBoardVector(boardVector, locationId);
    }

    private boolean isValid(final int locationId) {
        return locationId >= 0 && locationId < BOARD_VECTOR_SIZE && getParticipantId(locationId) == NO_PARTICIPANT_ID;
    }

    private GameAction createNextAction(final int locationId) {
        return new GameAction(locationId);
    }

    public GameAction createAction(final int locationId) {
        if (isValid(locationId)) {
            return createNextAction(locationId);
        }

        String message = String.format("invalid locationId: %d", locationId);

        throw new IllegalArgumentException(message);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        return IntStream.range(0, BOARD_VECTOR_SIZE)
                .filter(locationId -> getParticipantId(locationId) == NO_PARTICIPANT_ID)
                .mapToObj(this::createNextAction)
                ::iterator;
    }

    private static int mergeParticipantIdIntoBoardVector(final int boardVector, final int locationId, final int participantId) {
        return BOARD_VECTOR_MANIPULATOR_SUPPORT.merge(boardVector, locationId, participantId);
    }

    private static int mergeNextBoardVector(final int boardVector, final GameAction action, final int participantId) {
        return mergeParticipantIdIntoBoardVector(boardVector, action.getLocationId(), participantId);
    }

    private static List<Integer> createNextLocationIds(final List<Integer> locationIds, final int locationId) {
        List<Integer> nextLocationIds = new ArrayList<>(locationIds);

        nextLocationIds.add(locationId);

        return Collections.unmodifiableList(nextLocationIds);
    }

    private static boolean isRowOrColumnTaken(final int boardVector, final int participantId) {
        for (int i1 = 0; i1 < VECTOR_DIMENSION_SIZE; i1++) {
            boolean taken1 = true;
            boolean taken2 = true;

            for (int i2 = 0; (taken1 || taken2) && i2 < VECTOR_DIMENSION_SIZE; i2++) {
                taken1 &= extractParticipantIdFromBoardVector(boardVector, i1 * VECTOR_DIMENSION_SIZE + i2) == participantId;
                taken2 &= extractParticipantIdFromBoardVector(boardVector, i2 * VECTOR_DIMENSION_SIZE + i1) == participantId;
            }

            if (taken1 || taken2) {
                return true;
            }
        }

        return false;
    }

    private static boolean isDiagonalTaken(final int boardVector, final int participantId) {
        boolean taken1 = true;
        boolean taken2 = true;

        for (int i = 0; (taken1 || taken2) && i < VECTOR_DIMENSION_SIZE; i++) {
            taken1 &= extractParticipantIdFromBoardVector(boardVector, i * VECTOR_DIMENSION_SIZE + i) == participantId;
            taken2 &= extractParticipantIdFromBoardVector(boardVector, i * VECTOR_DIMENSION_SIZE + (VECTOR_DIMENSION_SIZE - i - 1)) == participantId;
        }

        return taken1 || taken2;
    }

    private static boolean isWon(final int boardVector, final int participantId) {
        return isRowOrColumnTaken(boardVector, participantId) || isDiagonalTaken(boardVector, participantId);
    }

    private static int determineNextStatusId(final int boardVector, final List<Integer> locationIds, final int participantId) {
        int size = locationIds.size();

        if (size >= 5 && isWon(boardVector, participantId)) {
            return participantId;
        }

        if (size == BOARD_VECTOR_SIZE) {
            return MonteCarloTreeSearch.DRAWN_STATUS_ID;
        }

        return MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    @Override
    public GameState accept(final GameAction action) {
        int nextParticipantId = getNextParticipantId();
        int nextBoardVector = mergeNextBoardVector(boardVector, action, nextParticipantId);
        List<Integer> nextLocationIds = createNextLocationIds(locationIds, action.getLocationId());
        int nextStatusId = determineNextStatusId(nextBoardVector, nextLocationIds, nextParticipantId);

        return new GameState(nextBoardVector, nextLocationIds, nextStatusId, nextParticipantId);
    }
}
