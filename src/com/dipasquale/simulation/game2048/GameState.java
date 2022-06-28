package com.dipasquale.simulation.game2048;

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
    public static final int BOARD_DIMENSION_LENGTH = ValuedTileSupport.BOARD_DIMENSION_VECTOR_LENGTH;
    public static final int BOARD_LENGTH = ValuedTileSupport.BOARD_VECTOR_LENGTH;
    private static final int DEFAULT_VICTORY_VALUE = 11;
    static final int UNINTENTIONAL_PLAYER_ID = 1;
    static final int INTENTIONAL_PLAYER_ID = 2;
    private static final int MAXIMUM_INTENTIONAL_PLAYER_POSSIBLE_ACTION_COUNT = 4;
    private static final int MINIMUM_SPAWN_EXPONENTIAL_VALUE = 1;
    private static final int MAXIMUM_SPAWN_EXPONENTIAL_VALUE = 2;
    private static final int INITIAL_VALUED_TILE_COUNT = 2;
    static final GameAction ROOT_ACTION = GameAction.createRoot();
    private final int victoriousExponentialValue;
    private final Board board;
    @Getter
    private final int depth;
    @Getter
    private final int statusId;
    @Getter
    private final int participantId;
    @Getter(AccessLevel.PACKAGE)
    private final GameAction lastAction;

    GameState(final int victoriousExponentialValue) {
        this.victoriousExponentialValue = victoriousExponentialValue;
        this.board = new Board();
        this.depth = 0;
        this.statusId = MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
        this.participantId = INTENTIONAL_PLAYER_ID;
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
    public boolean isActionIntentional() {
        return participantId == INTENTIONAL_PLAYER_ID;
    }

    @Override
    public boolean isNextActionIntentional() {
        return participantId != INTENTIONAL_PLAYER_ID;
    }

    public int getExponentialValue(final int tileId) {
        return board.getExponentialValue(tileId);
    }

    public int getTileId(final int freedTileId) {
        return board.getTileId(freedTileId);
    }

    public int getHighestExponentialValue() {
        return board.getHighestExponentialValue();
    }

    public int getValuedTileCount() {
        return board.getValuedTileCount();
    }

    public int getScore() {
        return board.getScore();
    }

    private GameAction createInitialAction(final List<ValuedTile> valuedTiles) {
        ValuedTile valuedTile1 = valuedTiles.get(0);
        ValuedTile valuedTile2 = valuedTiles.get(1);
        int tileId1 = valuedTile1.getTileId();
        int tileId2 = valuedTile2.getTileId();
        int product = tileId1 * Board.VECTOR_LENGTH + tileId2;
        int subtraction = (tileId1 + 1) * (tileId1 + 2) / 2;
        int padding1 = 2 * valuedTile1.getExponentialValue() - MINIMUM_SPAWN_EXPONENTIAL_VALUE;
        int padding2 = valuedTile2.getExponentialValue() - MINIMUM_SPAWN_EXPONENTIAL_VALUE;
        int actionId = 4 * (product - subtraction) + padding1 + padding2 - 1;

        return new GameAction(actionId, valuedTiles);
    }

    public GameAction createInitialAction(final ValuedTile valuedTile1, final ValuedTile valuedTile2) {
        if (valuedTile1.getTileId() < valuedTile2.getTileId()) {
            List<ValuedTile> valuedTiles = List.of(valuedTile1, valuedTile2);

            return createInitialAction(valuedTiles);
        }

        List<ValuedTile> valuedTiles = List.of(valuedTile2, valuedTile1);

        return createInitialAction(valuedTiles);
    }

    private static GameAction createNextAction(final int freedTileId, final ValuedTile valuedTile) {
        int actionId = freedTileId * 2 + valuedTile.getExponentialValue() - MINIMUM_SPAWN_EXPONENTIAL_VALUE;
        List<ValuedTile> valuedTiles = List.of(valuedTile);

        return new GameAction(actionId, valuedTiles);
    }

    private GameAction createNextAction(final ValuedTileTemplate valuedTileTemplate) {
        int tileId = board.getTileId(valuedTileTemplate.freedTileId);
        ValuedTile valuedTile = new ValuedTile(tileId, valuedTileTemplate.exponentialValue);

        return createNextAction(valuedTileTemplate.freedTileId, valuedTile);
    }

    public GameAction createValuedTileAllocationAction(final ValuedTile valuedTile) {
        if (!isNextActionIntentional()) {
            int freedTileId = board.getFreedTileId(valuedTile.getTileId());

            assert freedTileId >= 0;

            return createNextAction(freedTileId, valuedTile);
        }

        String message = String.format("game action cannot be created, because next turn is not meant to add valued tile: %s", valuedTile);

        throw new UnableToCreateGameActionException(message);
    }

    private boolean isValidIntentionalId(final int actionId) {
        return actionId >= 0 && actionId < MAXIMUM_INTENTIONAL_PLAYER_POSSIBLE_ACTION_COUNT && board.isIntentionalActionIdValid(actionId);
    }

    public GameAction createAction(final ActionIdType actionIdType) {
        if (!isNextActionIntentional()) {
            String message = String.format("game action '%s' cannot be created, because the next state is not meant to be the player's turn", actionIdType);

            throw new UnableToCreateGameActionException(message);
        }

        int actionId = actionIdType.getValue();

        if (isValidIntentionalId(actionId)) {
            return GameAction.createIntentional(actionId);
        }

        String message = String.format("game action '%s' cannot be created given the current state of the game", actionIdType);

        throw new UnableToCreateGameActionException(message);
    }

    private static Stream<int[]> createInitialTileIdsCartesianProduct(final int tileId) {
        return IntStream.range(tileId + 1, Board.VECTOR_LENGTH)
                .mapToObj(nextTileId -> new int[]{tileId, nextTileId});
    }

    private static Stream<List<ValuedTile>> createInitialValuedTilesCartesianProduct(final int[] tileIds) {
        List<ValuedTile> valuedTiles = IntStream.range(0, INITIAL_VALUED_TILE_COUNT)
                .mapToObj(index -> IntStream.range(MINIMUM_SPAWN_EXPONENTIAL_VALUE, MAXIMUM_SPAWN_EXPONENTIAL_VALUE + 1)
                        .mapToObj(exponentialValue -> new ValuedTile(tileIds[index], exponentialValue)))
                .flatMap(stream -> stream)
                .toList();

        // index: 0 = tileId: 0, exponentialValue: 1
        // index: 1 = tileId: 0, exponentialValue: 2
        // index: 2 = tileId: 1, exponentialValue: 1
        // index: 3 = tileId: 1, exponentialValue: 2

        return IntStream.range(0, valuedTiles.size())
                .mapToObj(index -> {
                    ValuedTile valuedTile1 = valuedTiles.get(index / INITIAL_VALUED_TILE_COUNT);
                    ValuedTile valuedTile2 = valuedTiles.get(index % INITIAL_VALUED_TILE_COUNT + INITIAL_VALUED_TILE_COUNT);

                    return List.of(valuedTile1, valuedTile2);
                }); // NOTE: (index1, index2) => (0, 2), (0, 3), (1, 2), (1, 3)
    }

    private Stream<GameAction> createAllInitialPossibleActions() {
        return IntStream.range(0, Board.VECTOR_LENGTH - 1)
                .mapToObj(GameState::createInitialTileIdsCartesianProduct)
                .flatMap(stream -> stream)
                .flatMap(GameState::createInitialValuedTilesCartesianProduct)
                .map(this::createInitialAction);
    }

    private Stream<GameAction> createAllPossibleValuedTileAllocationActions(final int freeTileId) {
        return IntStream.range(MINIMUM_SPAWN_EXPONENTIAL_VALUE, MAXIMUM_SPAWN_EXPONENTIAL_VALUE + 1)
                .mapToObj(exponentialValue -> new ValuedTileTemplate(freeTileId, exponentialValue))
                .map(this::createNextAction);
    }

    private Stream<GameAction> createAllPossibleValuedTileAllocationActions() {
        return IntStream.range(0, Board.VECTOR_LENGTH - board.getValuedTileCount())
                .mapToObj(this::createAllPossibleValuedTileAllocationActions)
                .flatMap(stream -> stream);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        if (depth == 0) {
            return createAllInitialPossibleActions()::iterator;
        }

        if (isNextActionIntentional()) {
            return IntStream.range(0, MAXIMUM_INTENTIONAL_PLAYER_POSSIBLE_ACTION_COUNT)
                    .filter(board::isIntentionalActionIdValid)
                    .mapToObj(GameAction::createIntentional)
                    ::iterator;
        }

        return createAllPossibleValuedTileAllocationActions()::iterator;
    }

    private Board createNextBoard(final GameAction action) {
        if (isNextActionIntentional()) {
            ActionIdType actionIdType = ActionIdType.from(action.getId());

            return board.createNext(actionIdType);
        }

        List<ValuedTile> valuedTiles = action.getValuedTilesAdded();

        return board.createNext(valuedTiles);
    }

    private static int determineNextStatusId(final Board board, final int participantId, final int victoryValue) {
        if (participantId == INTENTIONAL_PLAYER_ID && board.getHighestExponentialValue() >= victoryValue) {
            return INTENTIONAL_PLAYER_ID;
        }

        if (participantId == UNINTENTIONAL_PLAYER_ID && board.getIntentionalActionIdsAllowedVector() == 0) {
            return UNINTENTIONAL_PLAYER_ID;
        }

        return MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    @Override
    public GameState accept(final GameAction action) {
        Board nextBoard = createNextBoard(action);
        int nextDepth = depth + 1;
        int nextParticipantId = getNextParticipantId();
        int nextStatusId = determineNextStatusId(nextBoard, nextParticipantId, victoriousExponentialValue);

        return new GameState(victoriousExponentialValue, nextBoard, nextDepth, nextStatusId, nextParticipantId, action);
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
    private static final class ValuedTileTemplate {
        private final int freedTileId;
        private final int exponentialValue;
    }
}
