package com.dipasquale.io;

import lombok.Getter;

import java.io.IOException;
import java.io.Reader;

public final class CharacterBufferedReader {
    private final Reader reader;
    private final char[] buffer;
    private int index;
    private int length;
    private int read;
    @Getter
    private char current;

    public CharacterBufferedReader(final Reader reader, final int size) {
        this.reader = reader;
        this.buffer = new char[size];
        this.read = 0;
        this.index = size;
        this.length = size;
        this.current = '\0';
    }

    public int getIndex() {
        return read;
    }

    private boolean isEmpty() {
        return index >= length;
    }

    public boolean isDone() {
        return length == -1;
    }

    private void replace()
            throws IOException {
        if (isDone()) {
            throw new ReachedEndOfReaderException();
        }

        index = 0;
        length = reader.read(buffer, 0, buffer.length);
    }

    public char readNext()
            throws IOException {
        if (isEmpty()) {
            replace();
        }

        read++;

        return current = buffer[index++];
    }
}
