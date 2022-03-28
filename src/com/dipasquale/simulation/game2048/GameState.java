package com.dipasquale.simulation.game2048;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.State;
import com.dipasquale.search.mcts.TreeId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.PrintStream;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class GameState implements State<GameAction, GameState> {
    private static final int DEFAULT_VICTORY_VALUE = 11;
    private static final int VALUED_TILE_ADDER_PARTICIPANT_ID = 1;
    static final int PLAYER_PARTICIPANT_ID = 2;
    static final float PROBABILITY_OF_SPAWNING_2 = 0.9f;
    private static final int BOARD_SQUARE_LENGTH = Board.SQUARE_LENGTH;
    private static final int MAXIMUM_ACTIONS = 4;
    private static final int MINIMUM_SPAWNING_VALUE = 1;
    private static final int MAXIMUM_SPAWNING_VALUE = 2;
    private static final int INITIAL_VALUED_TILE_COUNT = 2;
    private final ValuedTileSupport valuedTileSupport;
    private final int victoryValue;
    private final Board board;
    @Getter
    private final int statusId;
    @Getter
    private final int participantId;
    @Getter
    private final GameAction lastAction;

    GameState(final ValuedTileSupport valuedTileSupport, final int victoryValue) {
        ArgumentValidatorSupport.ensureGreaterThanOrEqualToZero(victoryValue, "victoryValue");
        ArgumentValidatorSupport.ensureLessThanOrEqualTo(victoryValue, Board.MAXIMUM_EXPONENT_PER_TILE, "victoryValue", String.format("the limit is %d", Board.MAXIMUM_EXPONENT_PER_TILE));
        this.valuedTileSupport = valuedTileSupport;
        this.victoryValue = victoryValue;
        this.board = new Board();
        this.statusId = MonteCarloTreeSearch.IN_PROGRESS_STATUS_ID;
        this.participantId = PLAYER_PARTICIPANT_ID;
        this.lastAction = new GameAction(new TreeId(), MonteCarloTreeSearch.INITIAL_ACTION_ID, Board.NO_VALUED_TILES);
    }

    GameState(final ValuedTileSupport valuedTileSupport) {
        this(valuedTileSupport, DEFAULT_VICTORY_VALUE);
    }

    private GameState(final ValuedTileSupport valuedTileSupport, final int victoryValue, final Board board, final int statusId, final int participantId, final GameAction lastAction) {
        this.valuedTileSupport = valuedTileSupport;
        this.victoryValue = victoryValue;
        this.board = board;
        this.statusId = statusId;
        this.participantId = participantId;
        this.lastAction = lastAction;
    }

    @Override
    public int getDepth() {
        return lastAction.getTreeId().getDepth();
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

    public int getValueInTile(final int tileId) {
        return board.getValue(tileId);
    }

    public int getValuedTileCount() {
        return board.getValuedTileCount();
    }

    public int getScore() {
        return board.getScore();
    }

    private static StringJoiner createValuedTilesJoiner(final List<ValuedTile> valuedTiles) {
        StringJoiner valuedTilesJoiner = new StringJoiner(",");

        for (ValuedTile valuedTile : valuedTiles) {
            valuedTilesJoiner.add(Integer.toString(valuedTile.getId()));
            valuedTilesJoiner.add(Integer.toString(valuedTile.getValue()));
        }

        return valuedTilesJoiner;
    }

    private static String createInitialTreeIdKey(final List<ValuedTile> valuedTiles) {
        StringJoiner valuedTilesJoiner = createValuedTilesJoiner(valuedTiles);

        return String.format("[%s]", valuedTilesJoiner);
    }

    private GameAction createInitialAction(final List<ValuedTile> valuedTiles) {
        String treeIdKey = createInitialTreeIdKey(valuedTiles);
        TreeId treeId = lastAction.getTreeId().createChild(treeIdKey);

        return new GameAction(treeId, MonteCarloTreeSearch.INITIAL_ACTION_ID, valuedTiles);
    }

    public GameAction createInitialAction(final ValuedTile valuedTile1, final ValuedTile valuedTile2) {
        if (valuedTile1.getId() < valuedTile2.getId()) {
            List<ValuedTile> valuedTiles = List.of(valuedTile1, valuedTile2);

            return createInitialAction(valuedTiles);
        }

        List<ValuedTile> valuedTiles = List.of(valuedTile2, valuedTile1);

        return createInitialAction(valuedTiles);
    }

    public GameAction generateInitialAction() {
        int tileId1 = valuedTileSupport.generateId(0, BOARD_SQUARE_LENGTH);
        int tileId2 = valuedTileSupport.generateId(0, BOARD_SQUARE_LENGTH - 1);
        ValuedTile valuedTile1 = new ValuedTile(tileId1, generateValue(valuedTileSupport));
        ValuedTile valuedTile2;

        if (tileId2 >= tileId1) {
            valuedTile2 = new ValuedTile(tileId2 + 1, generateValue(valuedTileSupport));
        } else {
            valuedTile2 = new ValuedTile(tileId2, generateValue(valuedTileSupport));
        }

        return createInitialAction(valuedTile1, valuedTile2);
    }

    private static String createTreeIdKey(final int actionId, final List<ValuedTile> valuedTiles) {
        StringJoiner valuedTilesJoiner = createValuedTilesJoiner(valuedTiles);

        return String.format("%d[%s]", actionId, valuedTilesJoiner);
    }

    private GameAction createNextAction(final int actionId, List<ValuedTile> valuedTiles) {
        String treeIdKey = createTreeIdKey(actionId, valuedTiles);
        TreeId treeId = lastAction.getTreeId().createChild(treeIdKey);

        return new GameAction(treeId, actionId, valuedTiles);
    }

    private GameAction createNextAction(final ValuedTile valuedTile) {
        int actionId = lastAction.getId();
        List<ValuedTile> valuedTiles = List.of(valuedTile);

        return createNextAction(actionId, valuedTiles);
    }

    public GameAction createActionToAddValuedTile(final ValuedTile valuedTile) {
        if (isNextIntentional()) {
            String message = String.format("game action cannot be created, because next turn is not meant to add valued tile: %s", valuedTile);

            throw new IllegalArgumentException(message);
        }

        return createNextAction(valuedTile);
    }

    private static int generateValue(final ValuedTileSupport valuedTileSupport) {
        return valuedTileSupport.generateValue(PROBABILITY_OF_SPAWNING_2);
    }

    public GameAction generateActionToAddValuedTile() {
        if (isNextIntentional()) {
            String message = "game action cannot be generated, because next turn is not meant to add a random valued tiles";

            throw new IllegalArgumentException(message);
        }

        int tileIdLogical = valuedTileSupport.generateId(0, BOARD_SQUARE_LENGTH - board.getValuedTileCount());
        int tileId = -1;

        for (int i1 = 0, i2 = 0; tileId == -1; i1++) {
            if (board.getValue(i1) == Board.EMPTY_TILE_VALUE) {
                if (i2++ == tileIdLogical) {
                    tileId = i1;
                }
            }
        }

        int value = generateValue(valuedTileSupport);

        return createNextAction(new ValuedTile(tileId, value));
    }

    private boolean isValid(final int actionId) {
        return actionId >= 0 && actionId < MAXIMUM_ACTIONS && board.isActionIdValid(actionId);
    }

    public GameAction createAction(final ActionIdType actionIdType) {
        if (!isNextIntentional()) {
            String message = String.format("game action '%s' cannot be created, because the next state is not meant to be the player's turn", actionIdType);

            throw new IllegalArgumentException(message);
        }

        int actionId = actionIdType.getValue();

        if (!isValid(actionId)) {
            String message = String.format("game action '%s' cannot be created given the current state of the game", actionIdType);

            throw new IllegalArgumentException(message);
        }

        return createNextAction(actionId, Board.NO_VALUED_TILES);
    }

    private Stream<GameAction> createAllInitialPossibleActions() {
        return IntStream.range(0, BOARD_SQUARE_LENGTH - 1)
                .mapToObj(tileId1 -> IntStream.range(tileId1 + 1, BOARD_SQUARE_LENGTH)
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

                                return new InitialValuedTileSet(List.of(valuedTiles.get(fixedIndex), valuedTiles.get(fixedIndex + INITIAL_VALUED_TILE_COUNT)));
                            });
                })
                .map(valuedTileSet -> createInitialAction(valuedTileSet.valuedTiles));
    }

    private Stream<GameAction> createAllPossibleValuedTileAdditionActions(final int actionId, final Board board) {
        return IntStream.range(0, BOARD_SQUARE_LENGTH)
                .filter(tileId -> board.getValue(tileId) == Board.EMPTY_TILE_VALUE)
                .mapToObj(tileId -> IntStream.range(MINIMUM_SPAWNING_VALUE, MAXIMUM_SPAWNING_VALUE + 1)
                        .mapToObj(value -> new ValuedTile(tileId, value))
                        .map(valuedTile -> createNextAction(actionId, List.of(valuedTile))))
                .flatMap(stream -> stream);
    }

    @Override
    public Iterable<GameAction> createAllPossibleActions() {
        if (getDepth() == 0) {
            return createAllInitialPossibleActions()::iterator;
        }

        if (isNextIntentional()) {
            return IntStream.range(0, MAXIMUM_ACTIONS)
                    .filter(board::isActionIdValid)
                    .mapToObj(actionId -> createNextAction(actionId, Board.NO_VALUED_TILES))
                    ::iterator;
        }

        int actionId = lastAction.getId();

        return createAllPossibleValuedTileAdditionActions(actionId, board)::iterator;
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
        if (!action.getTreeId().isChildOf(lastAction.getTreeId())) {
            throw new IllegalArgumentException("action does not belong to the game state");
        }

        Board nextBoard = createNextBoard(action);
        int nextParticipantId = getNextParticipantId();
        int nextStatusId = getNextStatusId(nextBoard, nextParticipantId, victoryValue);

        return new GameState(valuedTileSupport, victoryValue, nextBoard, nextStatusId, nextParticipantId, action);
    }

    void print(final PrintStream stream) {
        if (lastAction.getValuedTilesAdded().isEmpty()) {
            board.print(stream, lastAction.getTreeId().getDepth(), ActionIdType.from(lastAction.getId()));
        } else {
            board.print(stream, lastAction.getTreeId().getDepth(), null);
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
