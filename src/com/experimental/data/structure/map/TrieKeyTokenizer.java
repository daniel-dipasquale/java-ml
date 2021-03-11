package com.experimental.data.structure.map;

@FunctionalInterface
public interface TrieKeyTokenizer<T> {
    Iterable<Object> tokenize(T key);
}
