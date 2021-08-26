package com.dipasquale.synchronization.dual.mode;

@FunctionalInterface
public interface DualModeObject {
    void switchMode(boolean concurrent);

    static <T extends DualModeObject> T switchMode(final T dualObject, final boolean concurrent) {
        dualObject.switchMode(concurrent);

        return dualObject;
    }

    static <TObject extends DualModeObject, TIterable extends Iterable<? extends TObject>> TIterable switchModes(final TIterable dualObjects, final boolean concurrent) {
        for (TObject dualObject : dualObjects) {
            switchMode(dualObject, concurrent);
        }

        return dualObjects;
    }
}
