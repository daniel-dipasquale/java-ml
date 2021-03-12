package com.dipasquale.data.structure.map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MapExtensions {
    private static <TKey, TValue> boolean equals(final Map<TKey, TValue> map1, final Map<TKey, TValue> map2) {
        if (map1.size() != map2.size()) {
            return false;
        }

        for (Map.Entry<TKey, TValue> entry : map1.entrySet()) {
            TKey key = entry.getKey();
            TValue value = entry.getValue();

            if (!Objects.equals(value, map2.get(key))) {
                return false;
            }
        }

        return true;
    }

    public static <TKey, TValue> boolean equals(final Map<TKey, TValue> map, final Object other) {
        if (map == other) {
            return true;
        }

        if (other instanceof Map) {
            try {
                return equals(map, (Map<TKey, TValue>) other);
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    public static <TKey, TValue> int hashCode(final Map<TKey, TValue> map) {
        int hashCode = 0;

        for (Map.Entry<TKey, TValue> entry : map.entrySet()) {
            hashCode += entry.hashCode();
        }

        return hashCode;
    }

    public static <TKey, TValue> String toString(final MapBase<TKey, TValue> map) {
        return null; // TODO: finish this
    }
}
