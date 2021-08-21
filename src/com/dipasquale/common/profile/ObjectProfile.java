package com.dipasquale.common.profile;

import com.dipasquale.common.Pair;

public interface ObjectProfile<T> {
    boolean switchProfile(boolean on);

    T getObject();

    static <T> Pair<T> deconstruct(final ObjectProfile<T> objectProfile) {
        if (objectProfile == null) {
            return null;
        }

        T object = objectProfile.getObject();

        if (objectProfile.switchProfile(true)) {
            try {
                objectProfile.switchProfile(false);

                return new Pair<>(object, objectProfile.getObject());
            } finally {
                objectProfile.switchProfile(true);
            }
        }

        try {
            return new Pair<>(objectProfile.getObject(), object);
        } finally {
            objectProfile.switchProfile(false);
        }
    }

    static <T> T getObject(final Pair<T> objectProfile, final boolean on) {
        if (on) {
            return objectProfile.getLeft();
        }

        return objectProfile.getRight();
    }

    static <T> ObjectProfile<T> switchProfile(final ObjectProfile<T> objectProfile, final boolean on) {
        objectProfile.switchProfile(on);

        return objectProfile;
    }
}
