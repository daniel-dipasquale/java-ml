package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Generated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializableSupport {
    public static byte[] serialize(final Serializable serializable)
            throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(serializable);

            return outputStream.toByteArray();
        }
    }

    public static <T> T deserialize(final byte[] object)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(object);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (T) objectInputStream.readObject();
        }
    }
}
