package com.dipasquale.io;

import lombok.Getter;

import java.io.IOException;
import java.io.Reader;
import java.util.Deque;
import java.util.LinkedList;

public final class CharacterBufferedReader {
    private final Reader reader;
    private final char[] buffer;
    private final Deque<Character> bufferRead;
    private int index;
    private int length;
    private int read;
    @Getter
    private char current;

    public CharacterBufferedReader(final Reader reader, final int size) {
        this.reader = reader;
        this.buffer = new char[size];
        this.bufferRead = new LinkedList<>();
        this.read = 0;
        this.index = size;
        this.length = size;
        this.current = '\0';
    }

    public int getIndex() {
        return read;
    }

    public String extractText() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Character character : bufferRead) {
            stringBuilder.append((char) character);
        }

        return stringBuilder.toString();
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
        current = buffer[index++];
        bufferRead.addLast(current);

        if (bufferRead.size() > buffer.length) {
            bufferRead.removeFirst();
        }

        return current;
    }
}
