package com.dipasquale.io.serialization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SerializableSupport {
    public static byte[] serializeObject(final Serializable object)
            throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(object);

            return outputStream.toByteArray();
        }
    }

    public static byte[] serializeStateGroup(final SerializableStateGroup stateGroup)
            throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            stateGroup.writeTo(objectOutputStream);

            return outputStream.toByteArray();
        }
    }

    public static <T> T deserializeObject(final byte[] object)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(object);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            return (T) objectInputStream.readObject();
        }
    }

    public static SerializableStateGroup deserializeStateGroup(final byte[] object)
            throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(object);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            SerializableStateGroup stateGroup = new SerializableStateGroup();

            stateGroup.readFrom(objectInputStream);

            return stateGroup;
        }
    }
}
