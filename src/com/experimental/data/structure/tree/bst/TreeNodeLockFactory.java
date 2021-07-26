package com.experimental.data.structure.tree.bst;

import com.dipasquale.common.factory.ObjectFactory;

@FunctionalInterface
interface TreeNodeLockFactory<T extends TreeNodeLock<? super T>> extends ObjectFactory<T> {
}
