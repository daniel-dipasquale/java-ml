package com.dipasquale.simulation.game2048;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.State;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.PrintStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class GameState implements State<GameAction, GameState> {
    private static final int STARTING_TILE_ID = -1;
    private static final int MINIMUM_SPAWNING_VALUE = 1;
    private static final int MAXIMUM_SPAWNING_VALUE = 2;
    private static final ValuedTile STARTING_OCCUPIED_LOCATION = new ValuedTile(STARTING_TILE_ID, Board.EMPTY_TILE_VALUE);
    private static final int MAXIMUM_ACTIONS = 4;
    private static final int DEFAULT_VICTORY_VALUE = 11;
    static final int PARTICIPANT_ID = 1;
    static final int STARTING_VALUED_TILE_COUNT = 2;
    private final Object membership;
    private final ValuedTileSupport valuedTileSupport;
    private final int victoryValue;
    @EqualsAndHashCode.Include // TODO: incomplete, this is not unique enough
    private final Board board;
    @Getter
    private final GameAction lastAction;
    @Getter
    private final int moveCount;

    private GameAction createStartingAction() {
        return new GameAction(null, MonteCarloTreeSearch.INITIAL_ACTION_ID, MonteCarloTreeSearch.INITIAL_PARTICIPANT_ID, STARTING_OCCUPIED_LOCATION);
    }

    GameState(final ValuedTileSupport valuedTileSupport, final int victoryValue) {
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(victoryValue, Board.MAXIMUM_EXPONENT_PER_TILE, "victoryValue", String.format("the limit is %d", Board.MAXIMUM_EXPONENT_PER_TILE));
        this.membership = new Object();
        this.valuedTileSupport = valuedTileSupport;
        this.victoryValue = victoryValue;
        this.board = new Board();
        this.lastAction = createStartingAction();
        this.moveCount = 0;
    }

    GameState(final ValuedTileSupport valuedTileSupport) {
        this(valuedTileSupport, DEFAULT_VICTORY_VALUE);
    }

    private GameState(final ValuedTileSupport valuedTileSupport, final int victoryValue, final Board board, final GameAction lastAction, final int moveCount) {
        this.membership = new Object();
        this.valuedTileSupport = valuedTileSupport;
        this.victoryValue = victoryValue;
        this.board = board;
        this.lastAction = lastAction;
        this.moveCount = moveCount;
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

    void initializeDefinedTiles(final ValuedTile valuedTile1, final ValuedTile valuedTile2) {
        board.initialize(valuedTile1, valuedTile2);
    }

    void initializeRandomTiles() {
        board.initialize(valuedTileSupport);
    }

    private void ensureValid(final int id) {
        if (!isValid(id)) {
            String message = String.format("game action is not valid due to id: %d", id);

            throw new IllegalArgumentException(message);
        }
    }

    private static GameAction createAction(final Object membership, final int actionId, final ValuedTile valuedTile) {
        return new GameAction(membership, actionId, PARTICIPANT_ID, valuedTile);
    }

    private static GameAction createAction(final Object membership, final ActionIdType actionIdType, final Board nextBoard) {
        return createAction(membership, actionIdType.getValue(), nextBoard.getLastValuedTile());
    }

    public GameAction createRandomOutcomeAction(final int id) {
        ensureValid(id);

        ActionIdType actionIdType = ActionIdType.from(id);
        Board nextBoard = board.generateNext(actionIdType, valuedTileSupport);

        return createAction(membership, actionIdType, nextBoard);
    }

    public GameAction createDefinedOutcomeAction(final int id, final ValuedTile valuedTile) {
        ensureValid(id);

        ActionIdType actionIdType = ActionIdType.from(id);

        try {
            Board nextBoard = board.createNextIfTileIsFree(actionIdType, valuedTile);

            return createAction(membership, actionIdType, nextBoard);
        } catch (TileInitializedException e) {
            String message = String.format("game action is not valid due to an already initialized tile: %s", valuedTile);

            throw new IllegalArgumentException(message, e);
        }
    }

    private static Stream<GameAction> createAllPossibleActions(final Object membership, final int actionId, final int tileId) {
        return IntStream.range(MINIMUM_SPAWNING_VALUE, MAXIMUM_SPAWNING_VALUE + 1)
                .mapToObj(value -> createAction(membership, actionId, new ValuedTile(tileId, value)));
    }

    private static Stream<GameAction> createAllPossibleActionsFromTemplate(final Object membership, final ActionIdType actionIdType, final Board templateBoard) {
        int actionId = actionIdType.getValue();

        return IntStream.range(0, Board.LENGTH)
                .filter(tileId -> templateBoard.getValue(tileId) == Board.EMPTY_TILE_VALUE)
                .mapToObj(tileId -> createAllPossibleActions(membership, actionId, tileId))
                .flatMap(stream -> stream);
    }

    private Stream<GameAction> createAllPossibleActions(final int actionId) {
        ActionIdType actionIdType = ActionIdType.from(actionId);
        Board templateBoard = board.createNextTemplate(actionIdType);

        return createAllPossibleActionsFromTemplate(membership, actionIdType, templateBoard);
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
        if (action.getMembership() != membership) {
            throw new IllegalArgumentException("action does not belong to the game state");
        }

        Board nextBoard = createNextBoard(action, simulation);
        int nextMoveCount = moveCount + 1;

        return new GameState(valuedTileSupport, victoryValue, nextBoard, action, nextMoveCount);
    }

    void print(final PrintStream stream) {
        board.print(stream);
    }
}
