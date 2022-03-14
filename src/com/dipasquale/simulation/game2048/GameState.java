package com.dipasquale.simulation.game2048;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.State;
import lombok.Getter;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class GameState implements State<GameAction, GameState> {
    private static final String INITIAL_CACHE_ID = "";
    private static final int STARTING_TILE_ID = -1;
    private static final int MINIMUM_SPAWNING_VALUE = 1;
    private static final int MAXIMUM_SPAWNING_VALUE = 2;
    private static final ValuedTile STARTING_OCCUPIED_LOCATION = new ValuedTile(STARTING_TILE_ID, Board.EMPTY_TILE_VALUE);
    private static final int MAXIMUM_ACTIONS = 4;
    private static final int DEFAULT_VICTORY_VALUE = 11;
    static final int PARTICIPANT_ID = 1;
    static final int STARTING_VALUED_TILE_COUNT = 2;
    private final ValuedTileSupport valuedTileSupport;
    private final int victoryValue;
    private final Board board;
    @Getter
    private final int depth;
    @Getter
    private final GameAction lastAction;

    GameState(final ValuedTileSupport valuedTileSupport, final int victoryValue) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(victoryValue, "victoryValue");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(victoryValue, Board.MAXIMUM_EXPONENT_PER_TILE, "victoryValue", String.format("the limit is %d", Board.MAXIMUM_EXPONENT_PER_TILE));
        this.valuedTileSupport = valuedTileSupport;
        this.victoryValue = victoryValue;
        this.board = new Board();
        this.depth = 0;
        this.lastAction = new GameAction(null, INITIAL_CACHE_ID, MonteCarloTreeSearch.INITIAL_ACTION_ID, MonteCarloTreeSearch.INITIAL_PARTICIPANT_ID, STARTING_OCCUPIED_LOCATION);
    }

    GameState(final ValuedTileSupport valuedTileSupport) {
        this(valuedTileSupport, DEFAULT_VICTORY_VALUE);
    }

    private GameState(final ValuedTileSupport valuedTileSupport, final int victoryValue, final Board board, final int depth, final GameAction lastAction) {
        this.valuedTileSupport = valuedTileSupport;
        this.victoryValue = victoryValue;
        this.board = board;
        this.depth = depth;
        this.lastAction = lastAction;
    }

    @Override
    public int getStatusId() {
        if (board.getMaximumValue() >= victoryValue) {
            return PARTICIPANT_ID;
        }

        if (board.getActionIdsAllowed() == 0) {
            return PARTICIPANT_ID + 1; // NOTE: since statusId = PARTICIPANT_ID indicates the status of the specific winner, in a single player game, statusId > PARTICIPANT_ID means losing
        }

        return MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
    }

    @Override
    public int getNextParticipantId() {
        return PARTICIPANT_ID;
    }

    public int getValueFromTile(final int tileId) {
        return board.getValue(tileId);
    }

    public int getValuedTileCount() {
        return board.getValuedTileCount();
    }

    public int getScore() {
        return board.getScore();
    }

    @Override
    public boolean isValid(final int actionId) {
        return board.isActionIdValid(actionId);
    }

    private static String createCacheId(final ValuedTile valuedTile1, final ValuedTile valuedTile2) {
        if (valuedTile1.getId() < valuedTile2.getId()) {
            return String.format("[%d,%d,%d,%d]", valuedTile1.getId(), valuedTile1.getValue(), valuedTile2.getId(), valuedTile2.getValue());
        }

        return String.format("[%d,%d,%d,%d]", valuedTile2.getId(), valuedTile2.getValue(), valuedTile1.getId(), valuedTile1.getValue());
    }

    private static String createCacheId(final int depth, final int actionId, final ValuedTile valuedTile) {
        return String.format("%d,%d[%d,%d]", depth, actionId, valuedTile.getId(), valuedTile.getValue());
    }

    void initializeDefinedTiles(final ValuedTile valuedTile1, final ValuedTile valuedTile2) {
        board.initialize(valuedTile1, valuedTile2);
        lastAction.setCacheId(createCacheId(valuedTile1, valuedTile2));
        lastAction.setValuedTileAdded(valuedTile2);
    }

    void initializeRandomTiles() {
        ValuedTile firstValuedTile = board.initialize(valuedTileSupport);
        ValuedTile lastValuedTile = board.getLastValuedTile();
        String cacheId = createCacheId(firstValuedTile, lastValuedTile);

        lastAction.setCacheId(cacheId);
        lastAction.setValuedTileAdded(lastValuedTile);
    }

    private void ensureValid(final int id) {
        if (!isValid(id)) {
            String message = String.format("game action is not valid due to id: %d", id);

            throw new IllegalArgumentException(message);
        }
    }

    private static GameAction createAction(final String parentCacheId, final int depth, final int actionId, final ValuedTile valuedTile) {
        String cacheId = createCacheId(depth, actionId, valuedTile);

        return new GameAction(parentCacheId, cacheId, actionId, PARTICIPANT_ID, valuedTile);
    }

    private static GameAction createAction(final String parentCacheId, final int depth, final ActionIdType actionIdType, final Board nextBoard) {
        return createAction(parentCacheId, depth, actionIdType.getValue(), nextBoard.getLastValuedTile());
    }

    private GameAction createNextAction(final ActionIdType actionIdType, final Board nextBoard) {
        String parentCacheId = lastAction.getCacheId();
        int nextDepth = depth + 1;

        return createAction(parentCacheId, nextDepth, actionIdType, nextBoard);
    }

    public GameAction createRandomOutcomeAction(final int id) {
        ensureValid(id);

        ActionIdType actionIdType = ActionIdType.from(id);
        Board nextBoard = board.generateNext(actionIdType, valuedTileSupport);

        return createNextAction(actionIdType, nextBoard);
    }

    public GameAction createDefinedOutcomeAction(final int id, final ValuedTile valuedTile) {
        ensureValid(id);

        ActionIdType actionIdType = ActionIdType.from(id);

        try {
            Board nextBoard = board.createNextIfTileIsFree(actionIdType, valuedTile);

            return createNextAction(actionIdType, nextBoard);
        } catch (TileInitializedException e) {
            String message = String.format("game action is not valid due to an already initialized tile: %s", valuedTile);

            throw new IllegalArgumentException(message, e);
        }
    }

    private static Stream<GameAction> createAllPossibleActions(final String parentCacheId, final int depth, final int actionId, final int tileId) {
        return IntStream.range(MINIMUM_SPAWNING_VALUE, MAXIMUM_SPAWNING_VALUE + 1)
                .mapToObj(value -> createAction(parentCacheId, depth, actionId, new ValuedTile(tileId, value)));
    }

    private static Stream<GameAction> createAllPossibleActionsFromTemplate(final String parentCacheId, final int depth, final ActionIdType actionIdType, final Board templateBoard) {
        int actionId = actionIdType.getValue();

        return IntStream.range(0, Board.LENGTH)
                .filter(tileId -> templateBoard.getValue(tileId) == Board.EMPTY_TILE_VALUE)
                .mapToObj(tileId -> createAllPossibleActions(parentCacheId, depth, actionId, tileId))
                .flatMap(stream -> stream);
    }

    private Stream<GameAction> createAllPossibleActions(final int actionId) {
        String parentCacheId = lastAction.getCacheId();
        int nextDepth = depth + 1;
        ActionIdType actionIdType = ActionIdType.from(actionId);
        Board templateBoard = board.createNextTemplate(actionIdType);

        return createAllPossibleActionsFromTemplate(parentCacheId, nextDepth, actionIdType, templateBoard);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        return IntStream.range(0, MAXIMUM_ACTIONS)
                .mapToObj(this::createAllPossibleActions)
                .flatMap(stream -> stream)
                ::iterator;
    }

    private Board createNextBoard(final GameAction action, final boolean simulation) {
        ActionIdType actionIdType = ActionIdType.from(action.getId());

        if (simulation) {
            ValuedTile valuedTile = action.getValuedTileAdded();

            return board.createNext(actionIdType, valuedTile);
        }

        return board.generateNext(actionIdType, valuedTileSupport);
    }

    @Override
    public GameState accept(final GameAction action, final boolean simulation) {
        if (!Objects.equals(lastAction.getCacheId(), action.getParentCacheId())) {
            throw new IllegalArgumentException("action does not belong to the game state");
        }

        Board nextBoard = createNextBoard(action, simulation);
        int nextDepth = depth + 1;

        return new GameState(valuedTileSupport, victoryValue, nextBoard, nextDepth, action);
    }

    void print(final PrintStream stream) {
        board.print(stream);
    }
}
