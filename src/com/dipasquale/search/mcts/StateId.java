package com.dipasquale.search.mcts;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class StateId {
    private final WeakReference<StateId> parent;
    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    private final int depth;
    @EqualsAndHashCode.Include
    @ToString.Include
    private final int actionId;

    public StateId() {
        this.parent = null;
        this.depth = 0;
        this.actionId = MonteCarloTreeSearch.ROOT_ACTION_ID;
    }

    public StateId getParent() {
        if (parent == null) {
            return null;
        }

        return parent.get();
    }

    public StateId createChild(final int actionId) {
        WeakReference<StateId> current = new WeakReference<>(this);
        int childDepth = depth + 1;

        return new StateId(current, childDepth, actionId);
    }

    private static boolean equals(final StateId stateId1, final StateId stateId2) {
        return Objects.equals(stateId1, stateId2);
    }

    public boolean isChildOf(final StateId stateId) {
        return parent != null && equals(parent.get(), stateId);
    }

    public Iterable<StateId> tokenizeUntil(final StateId ancestorStateId) {
        Stack<StateId> stateIds = new Stack<>();

        if (ancestorStateId.depth <= depth) {
            stateIds.add(this);

            for (WeakReference<StateId> currentParent = parent; currentParent != null; ) {
                StateId fixedCurrentParent = currentParent.get();

                if (fixedCurrentParent != null && ancestorStateId.depth <= fixedCurrentParent.depth) {
                    stateIds.add(fixedCurrentParent);
                    currentParent = fixedCurrentParent.parent;
                } else {
                    currentParent = null;
                }
            }
        }

        if (stateIds.isEmpty()) {
            return null;
        }

        if (!equals(stateIds.pop(), ancestorStateId)) {
            return List.of();
        }

        return () -> new StackIterator(stateIds);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class StackIterator implements Iterator<StateId> {
        private final Stack<StateId> stateIds;

        @Override
        public boolean hasNext() {
            return !stateIds.isEmpty();
        }

        @Override
        public StateId next() {
            return stateIds.pop();
        }
    }
}
