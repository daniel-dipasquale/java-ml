package com.experimental.data.structure.tree.bst;

@FunctionalInterface
interface TreeNodeLockFactory<T extends TreeNodeLock<? super T>> {
    T create();
}
