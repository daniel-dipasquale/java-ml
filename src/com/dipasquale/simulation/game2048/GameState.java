package com.dipasquale.simulation.game2048;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameState implements State<GameAction, GameState> {
    private static final int DEFAULT_VICTORY_VALUE = 11;
    private static final int VALUED_TILE_ADDER_PARTICIPANT_ID = 1;
    public static final int PLAYER_PARTICIPANT_ID = 2;
    public static final float PROBABILITY_OF_SPAWNING_2 = 0.9f;
    private static final int MAXIMUM_ACTIONS = 4;
    private static final int MINIMUM_SPAWNING_VALUE = 1;
    private static final int MAXIMUM_SPAWNING_VALUE = 2;
    private static final int INITIAL_VALUED_TILE_COUNT = 2;
    private static final GameAction ROOT_ACTION = constructRootAction();
    private final int victoryValue;
    private final Board board;
    @Getter
    private final int depth;
    @Getter
    private final int statusId;
    @Getter
    private final int participantId;
    private final GameAction lastAction;

    GameState(final int victoryValue) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(victoryValue, "victoryValue");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(victoryValue, Board.MAXIMUM_EXPONENT_PER_TILE, "victoryValue", String.format("the limit is %d", Board.MAXIMUM_EXPONENT_PER_TILE));
        this.victoryValue = victoryValue;
        this.board = new Board();
        this.depth = 0;
        this.statusId = MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
        this.participantId = PLAYER_PARTICIPANT_ID;
        this.lastAction = ROOT_ACTION;
    }

    GameState() {
        this(DEFAULT_VICTORY_VALUE);
    }

    @Override
    public int getNextParticipantId() {
        return 3 - participantId;
    }

    @Override
    public boolean isIntentional() {
        return participantId == PLAYER_PARTICIPANT_ID;
    }

    @Override
    public boolean isNextIntentional() {
        return getNextParticipantId() == PLAYER_PARTICIPANT_ID;
    }

    private static GameAction constructRootAction() {
        return new GameAction(MonteCarloTreeSearch.ROOT_ACTION_ID, Board.NO_VALUED_TILES);
    }

    @Override
    public GameAction createRootAction() {
        return constructRootAction();
    }

    public int getValueInTile(final int tileId) {
        return board.getValue(tileId);
    }

    public int getHighestValue() {
        return board.getHighestValue();
    }

    public int getValuedTileCount() {
        return board.getValuedTileCount();
    }

    public int getScore() {
        return board.getScore();
    }

    private GameAction createInitialAction(final List<ValuedTile> valuedTiles) {
        return new GameAction(MonteCarloTreeSearch.ROOT_ACTION_ID, valuedTiles);
    }

    public GameAction createInitialAction(final ValuedTile valuedTile1, final ValuedTile valuedTile2) {
        if (depth != 0) {
            throw new UnableToCreateGameActionException("game action cannot be created, because the game state is beyond the initial state");
        }

        if (valuedTile1.getId() < valuedTile2.getId()) {
            List<ValuedTile> valuedTiles = List.of(valuedTile1, valuedTile2);

            return createInitialAction(valuedTiles);
        }

        List<ValuedTile> valuedTiles = List.of(valuedTile2, valuedTile1);

        return createInitialAction(valuedTiles);
    }

    private static int calculateActionId(final ValuedTile valuedTile) {
        return Board.SQUARE_LENGTH * (valuedTile.getValue() - 1) + valuedTile.getId();
    }

    private static int calculateActionId(final List<ValuedTile> valuedTiles) {
        int actionId = 0;
        int size = valuedTiles.size();

        for (int i = 0; i < size; i++) {
            ValuedTile valuedTile = valuedTiles.get(i);

            actionId += calculateActionId(valuedTile) + (int) Math.pow(10D, size - i - 1);
        }

        return actionId;
    }

    private static GameAction createNextAction(final List<ValuedTile> valuedTiles) {
        int actionId = calculateActionId(valuedTiles);

        return new GameAction(actionId, valuedTiles);
    }

    private GameAction createNextAction(final ValuedTile valuedTile) {
        List<ValuedTile> valuedTiles = List.of(valuedTile);

        return createNextAction(valuedTiles);
    }

    public GameAction createActionToAddValuedTile(final ValuedTile valuedTile) {
        if (!isNextIntentional()) {
            return createNextAction(valuedTile);
        }

        String message = String.format("game action cannot be created, because next turn is not meant to add valued tile: %s", valuedTile);

        throw new UnableToCreateGameActionException(message);
    }

    private boolean isValid(final int actionId) {
        return actionId >= 0 && actionId < MAXIMUM_ACTIONS && board.isActionIdValid(actionId);
    }

    public GameAction createAction(final ActionIdType actionIdType) {
        if (!isNextIntentional()) {
            String message = String.format("game action '%s' cannot be created, because the next state is not meant to be the player's turn", actionIdType);

            throw new UnableToCreateGameActionException(message);
        }

        int actionId = actionIdType.getValue();

        if (isValid(actionId)) {
            return new GameAction(actionId, Board.NO_VALUED_TILES);
        }

        String message = String.format("game action '%s' cannot be created given the current state of the game", actionIdType);

        throw new UnableToCreateGameActionException(message);
    }

    private Stream<GameAction> createAllInitialPossibleActions() {
        return IntStream.range(0, Board.SQUARE_LENGTH - 1)
                .mapToObj(tileId1 -> IntStream.range(tileId1 + 1, Board.SQUARE_LENGTH)
                        .mapToObj(tileId2 -> new InitialTileIdSet(new int[]{tileId1, tileId2})))
                .flatMap(stream -> stream)
                .flatMap(initialTileIdSet -> {
                    List<ValuedTile> valuedTiles = IntStream.range(0, INITIAL_VALUED_TILE_COUNT)
                            .mapToObj(index -> IntStream.range(MINIMUM_SPAWNING_VALUE, MAXIMUM_SPAWNING_VALUE + 1)
                                    .mapToObj(value -> new ValuedTile(initialTileIdSet.tileIds[index], value)))
                            .flatMap(stream -> stream)
                            .toList();

                    return IntStream.range(0, valuedTiles.size())
                            .mapToObj(index -> {
                                int fixedIndex = index / INITIAL_VALUED_TILE_COUNT;
                                ValuedTile valuedTile1 = valuedTiles.get(fixedIndex);
                                ValuedTile valuedTile2 = valuedTiles.get(fixedIndex + INITIAL_VALUED_TILE_COUNT);
                                List<ValuedTile> fixedValuedTiles = List.of(valuedTile1, valuedTile2);

                                return new InitialValuedTileSet(fixedValuedTiles);
                            });
                })
                .map(valuedTileSet -> createInitialAction(valuedTileSet.valuedTiles));
    }

    private Stream<GameAction> createAllPossibleValuedTileAdditionActions(final Board board) {
        return IntStream.range(0, Board.SQUARE_LENGTH)
                .filter(tileId -> board.getValue(tileId) == Board.EMPTY_TILE_VALUE)
                .mapToObj(tileId -> IntStream.range(MINIMUM_SPAWNING_VALUE, MAXIMUM_SPAWNING_VALUE + 1)
                        .mapToObj(value -> new ValuedTile(tileId, value))
                        .map(this::createNextAction))
                .flatMap(stream -> stream);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        if (depth == 0) {
            return createAllInitialPossibleActions()::iterator;
        }

        if (isNextIntentional()) {
            return IntStream.range(0, MAXIMUM_ACTIONS)
                    .filter(board::isActionIdValid)
                    .mapToObj(actionId -> new GameAction(actionId, Board.NO_VALUED_TILES))
                    ::iterator;
        }

        return createAllPossibleValuedTileAdditionActions(board)::iterator;
    }

    private Board createNextBoard(final GameAction action) {
        List<ValuedTile> valuedTiles = action.getValuedTilesAdded();

        if (!valuedTiles.isEmpty()) {
            return board.createNext(valuedTiles);
        }

        ActionIdType actionIdType = ActionIdType.from(action.getId());

        return board.createNextPartiallyInitialized(actionIdType);
    }

    private static int getNextStatusId(final Board board, final int participantId, final int victoryValue) {
        if (participantId == PLAYER_PARTICIPANT_ID && board.getHighestValue() >= victoryValue) {
            return PLAYER_PARTICIPANT_ID;
        }

        if (participantId == VALUED_TILE_ADDER_PARTICIPANT_ID && board.getActionIdsAllowed() == 0) {
            return VALUED_TILE_ADDER_PARTICIPANT_ID;
        }

        return MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    @Override
    public GameState accept(final GameAction action) {
        Board nextBoard = createNextBoard(action);
        int nextDepth = depth + 1;
        int nextParticipantId = getNextParticipantId();
        int nextStatusId = getNextStatusId(nextBoard, nextParticipantId, victoryValue);

        return new GameState(victoryValue, nextBoard, nextDepth, nextStatusId, nextParticipantId, action);
    }

    public void print(final PrintStream stream) {
        if (lastAction.getValuedTilesAdded().isEmpty()) {
            int actionId = lastAction.getId();

            if (actionId >= 0) {
                ActionIdType actionIdType = ActionIdType.from(actionId);

                board.print(stream, depth, actionIdType);
            } else {
                board.print(stream, depth, null);
            }
        } else {
            board.print(stream, depth, null);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InitialTileIdSet {
        private final int[] tileIds;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InitialValuedTileSet {
        private final List<ValuedTile> valuedTiles;
    }
}
