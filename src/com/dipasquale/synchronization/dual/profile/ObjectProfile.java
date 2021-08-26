package com.dipasquale.synchronization.dual.profile;

import com.dipasquale.common.Pair;

import java.util.Map;

public interface ObjectProfile<T> {
    boolean switchProfile(boolean concurrent);

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

    static <T> T getObject(final Pair<T> objectPair, final boolean concurrent) {
        if (concurrent) {
            return objectPair.getLeft();
        }

        return objectPair.getRight();
    }

    static <T> ObjectProfile<T> switchProfile(final ObjectProfile<T> objectProfile, final boolean concurrent) {
        objectProfile.switchProfile(concurrent);

        return objectProfile;
    }

    static <T extends Iterable<? extends ObjectProfile<?>>> T switchProfiles(final T objectProfiles, final boolean concurrent) {
        for (ObjectProfile<?> objectProfile : objectProfiles) {
            switchProfile(objectProfile, concurrent);
        }

        return objectProfiles;
    }

    static <T extends Map<?, ? extends ObjectProfile<?>>> T switchProfileMap(final T objectProfiles, final boolean concurrent) {
        switchProfiles(objectProfiles.values(), concurrent);

        return objectProfiles;
    }
}
