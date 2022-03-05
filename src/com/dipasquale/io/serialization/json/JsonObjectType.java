package com.dipasquale.io.serialization.json;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum JsonObjectType {
    OBJECT,
    ARRAY
}
