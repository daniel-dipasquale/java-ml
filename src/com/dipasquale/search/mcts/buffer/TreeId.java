package com.dipasquale.search.mcts.buffer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class TreeId {
    private static final String INITIAL_KEY = "";
    private final WeakReference<TreeId> parent;
    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    private final int depth;
    @EqualsAndHashCode.Include
    @ToString.Include
    private final String key;

    public TreeId() {
        this.parent = null;
        this.key = INITIAL_KEY;
        this.depth = 0;
    }

    TreeId getParent() {
        if (parent == null) {
            return null;
        }

        return parent.get();
    }

    public TreeId createChild(final String key) {
        WeakReference<TreeId> nextParent = new WeakReference<>(this);
        int nextDepth = depth + 1;

        return new TreeId(nextParent, nextDepth, key);
    }

    private static boolean equals(final TreeId treeId1, final TreeId treeId2) {
        return Objects.equals(treeId1, treeId2);
    }

    public boolean isChildOf(final TreeId treeId) {
        return parent != null && equals(parent.get(), treeId);
    }

    Iterable<TreeId> tokenizeFrom(final TreeId rootTreeId) {
        Stack<TreeId> treeIds = new Stack<>();

        if (rootTreeId.depth <= depth) {
            treeIds.add(this);

            for (WeakReference<TreeId> currentParent = parent; currentParent != null; ) {
                TreeId fixedCurrentParent = currentParent.get();

                if (fixedCurrentParent != null && rootTreeId.depth <= fixedCurrentParent.depth) {
                    treeIds.add(fixedCurrentParent);
                    currentParent = fixedCurrentParent.parent;
                } else {
                    currentParent = null;
                }
            }
        }

        if (treeIds.isEmpty()) {
            return null;
        }

        if (!equals(treeIds.pop(), rootTreeId)) {
            return List.of();
        }

        return () -> new StackIterator(treeIds);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class StackIterator implements Iterator<TreeId> {
        private final Stack<TreeId> treeIds;

        @Override
        public boolean hasNext() {
            return !treeIds.isEmpty();
        }

        @Override
        public TreeId next() {
            return treeIds.pop();
        }
    }
}
