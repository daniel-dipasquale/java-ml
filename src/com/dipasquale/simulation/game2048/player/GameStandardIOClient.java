package com.dipasquale.simulation.game2048.player;

import com.dipasquale.io.ConnectionClosedException;
import com.dipasquale.io.FailedToConnectException;
import com.dipasquale.io.IORuntimeException;
import com.dipasquale.io.InvalidResponseException;
import com.dipasquale.io.RequestMethod;
import com.dipasquale.io.ServerErrorException;
import com.dipasquale.io.StandardIOClient;
import com.dipasquale.io.StandardIORequest;
import com.dipasquale.io.StandardIOResponse;
import com.dipasquale.io.UnsupportedStatusCodeException;
import com.dipasquale.simulation.game2048.ActionIdType;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.ValuedTile;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public final class GameStandardIOClient implements AutoCloseable {
    private static final String BEGINNING_RESOURCE = "/beginning";
    private static final String MOVEMENT_RESOURCE = "/movement";
    private static final Map<String, String> HEADERS = Map.of("content-type", "text");
    private static final Pattern VALUED_TILE_PATTERN = Pattern.compile("\\[(?<value>\\d+),(?<column>\\d+),(?<row>\\d+)\\]");
    private final StandardIOClient client;

    private static List<ValuedTile> parseValuedTiles(final String valuedTileText) {
        List<ValuedTile> valuedTiles = new ArrayList<>();
        Matcher valuedTileTextMatcher = VALUED_TILE_PATTERN.matcher(valuedTileText);

        while (valuedTileTextMatcher.find()) {
            int row = Integer.parseInt(valuedTileTextMatcher.group("row"));
            int column = Integer.parseInt(valuedTileTextMatcher.group("column"));
            int tileId = Game.BOARD_ONE_DIMENSION_LENGTH * (row - 1) + (column - 1);
            int displayValue = Integer.parseInt(valuedTileTextMatcher.group("value"));
            int exponentialValue = Game.toExponentialValue(displayValue);

            valuedTiles.add(new ValuedTile(tileId, exponentialValue));
        }

        return valuedTiles;
    }

    public List<ValuedTile> start() {
        StandardIORequest request = StandardIORequest.builder()
                .method(RequestMethod.POST)
                .resource(BEGINNING_RESOURCE)
                .headers(HEADERS)
                .build();

        try {
            StandardIOResponse response = client.invoke(request);

            return parseValuedTiles(response.getBody());
        } catch (FailedToConnectException | ConnectionClosedException | ServerErrorException |
                 InvalidResponseException | UnsupportedStatusCodeException e) {
            throw new IORuntimeException(e.getMessage(), e);
        }
    }

    public ValuedTile move(final ActionIdType actionIdType) {
        StandardIORequest request = StandardIORequest.builder()
                .method(RequestMethod.POST)
                .resource(MOVEMENT_RESOURCE)
                .headers(HEADERS)
                .body(actionIdType.name())
                .build();

        try {
            StandardIOResponse response = client.invoke(request);

            return parseValuedTiles(response.getBody()).get(0);
        } catch (FailedToConnectException | ConnectionClosedException | ServerErrorException |
                 InvalidResponseException | UnsupportedStatusCodeException e) {
            throw new IORuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void close()
            throws Exception {
        client.close();
    }
}
