package com.experimental.concurrent;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Generated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class UnsafeProvider { // NOTE: based on: https://github.com/boundary/high-scale-lib
    @Getter
    private static final UnsafeProvider instance = new UnsafeProvider();

    public Unsafe getUnsafe() {
        if (UnsafeProvider.class.getClassLoader() == null) {
            return Unsafe.getUnsafe();
        }

        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");

            field.setAccessible(true);

            return (Unsafe) field.get(UnsafeProvider.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not obtain access to sun.misc.Unsafe", e);
        }
    }
}
