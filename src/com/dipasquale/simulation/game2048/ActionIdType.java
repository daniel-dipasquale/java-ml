package com.dipasquale.simulation.game2048;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ActionIdType {
    LEFT(0),
    UP(1),
    RIGHT(2),
    DOWN(3);

    private static final ActionIdType[] VALUES = createActionIds();
    private final int value;

    private static ActionIdType[] createActionIds() {
        ActionIdType[] actionIdTypes = ActionIdType.values();
        ActionIdType[] fixedActionIdTypes = new ActionIdType[actionIdTypes.length];

        for (int i = 0; i < actionIdTypes.length; i++) {
            ActionIdType actionIdType = actionIdTypes[i];

            fixedActionIdTypes[actionIdType.value] = actionIdType;
        }

        return fixedActionIdTypes;
    }

    public static ActionIdType from(final int id) {
        return VALUES[id];
    }
}
