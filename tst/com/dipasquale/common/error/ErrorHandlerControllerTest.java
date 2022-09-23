package com.dipasquale.common.error;

import com.dipasquale.data.structure.collection.ListSupport;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

public class ErrorHandlerControllerTest {
    @Test
    public void TEST_1() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<HandlerMock> elements = ListSupport.<HandlerMock>builder()
                .add(new HandlerMock(data, 0, 3))
                .add(new HandlerMock(data, 1, 2))
                .add(new HandlerMock(data, 2, 1))
                .build();

        ErrorHandlerController<HandlerMock> test = new ErrorHandlerController<>(elements, HandlerMock::handle);

        test.handleAll(() -> new RuntimeException("unit test failure"));
        Assertions.assertEquals(3, data.get(0));
        Assertions.assertEquals(2, data.get(1));
        Assertions.assertEquals(1, data.get(2));
    }

    @Test
    public void TEST_2() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<HandlerMock> elements = ListSupport.<HandlerMock>builder()
                .add(new HandlerMock(data, 0, 3))
                .add(new HandlerMock(data, -1, 2))
                .add(new HandlerMock(data, 2, 1))
                .build();

        ErrorHandlerController<HandlerMock> test = new ErrorHandlerController<>(elements, HandlerMock::handle);

        try {
            test.handleAll(() -> new RuntimeException("unit test failure"));
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(RuntimeException.class)
                    .message("unit test failure")
                    .suppressed(List.of(
                            ErrorSnapshot.builder()
                                    .type(ArrayIndexOutOfBoundsException.class)
                                    .message("Index -1 out of bounds for length 3")
                                    .build()
                    ))
                    .build(), ErrorSnapshot.create(e));
        }

        Assertions.assertEquals(3, data.get(0));
        Assertions.assertEquals(0, data.get(1));
        Assertions.assertEquals(1, data.get(2));
    }

    @Test
    public void TEST_3() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<HandlerMock> elements = ListSupport.<HandlerMock>builder()
                .add(new HandlerMock(data, 0, 3))
                .add(new HandlerMock(data, 1, 2))
                .add(new HandlerMock(data, 2, 1))
                .build();

        ErrorHandlerController<HandlerMock> test = new ErrorHandlerController<>(elements, HandlerMock::handle);

        test.handleAll("unit test failure");
        Assertions.assertEquals(3, data.get(0));
        Assertions.assertEquals(2, data.get(1));
        Assertions.assertEquals(1, data.get(2));
    }

    @Test
    public void TEST_4() {
        AtomicLongArray data = new AtomicLongArray(3);

        List<HandlerMock> elements = ListSupport.<HandlerMock>builder()
                .add(new HandlerMock(data, 0, 3))
                .add(new HandlerMock(data, -1, 2))
                .add(new HandlerMock(data, 2, 1))
                .build();

        ErrorHandlerController<HandlerMock> test = new ErrorHandlerController<>(elements, HandlerMock::handle);

        try {
            test.handleAll("unit test failure");
        } catch (Throwable e) {
            Assertions.assertEquals(ErrorSnapshot.builder()
                    .type(RuntimeException.class)
                    .message("unit test failure")
                    .suppressed(List.of(
                            ErrorSnapshot.builder()
                                    .type(ArrayIndexOutOfBoundsException.class)
                                    .message("Index -1 out of bounds for length 3")
                                    .build()
                    ))
                    .build(), ErrorSnapshot.create(e));
        }

        Assertions.assertEquals(3, data.get(0));
        Assertions.assertEquals(0, data.get(1));
        Assertions.assertEquals(1, data.get(2));
    }

    @RequiredArgsConstructor
    private static final class HandlerMock {
        private final AtomicLongArray data;
        private final int index;
        private final long value;

        public void handle() {
            data.set(index, value);
        }
    }
}
