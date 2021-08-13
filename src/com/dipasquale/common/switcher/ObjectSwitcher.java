/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.switcher;

import com.dipasquale.common.Pair;

public interface ObjectSwitcher<T> {
    boolean switchObject(boolean on);

    T getObject();

    static <T> Pair<T> deconstruct(final ObjectSwitcher<T> objectSwitcher) {
        if (objectSwitcher == null) {
            return null;
        }

        T object = objectSwitcher.getObject();

        if (objectSwitcher.switchObject(true)) {
            try {
                objectSwitcher.switchObject(false);

                return new Pair<>(object, objectSwitcher.getObject());
            } finally {
                objectSwitcher.switchObject(true);
            }
        }

        try {
            return new Pair<>(objectSwitcher.getObject(), object);
        } finally {
            objectSwitcher.switchObject(false);
        }
    }

    static <T> T getObject(final Pair<T> objectSwitcher, final boolean on) {
        if (on) {
            return objectSwitcher.getLeft();
        }

        return objectSwitcher.getRight();
    }

    static <T> ObjectSwitcher<T> switchObject(final ObjectSwitcher<T> objectSwitcher, final boolean on) {
        objectSwitcher.switchObject(on);

        return objectSwitcher;
    }
}
