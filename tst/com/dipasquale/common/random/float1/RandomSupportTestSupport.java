package com.dipasquale.common.random.float1;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RandomSupportTestSupport {
    public static boolean isNextFloatBounded(final RandomSupport randomSupport, final int count, final float min, final float max) {
        for (int i = 0; i < count; i++) {
            float result = randomSupport.next();

            if (Float.compare(result, min) < 0 || Float.compare(result, max) >= 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean isNextIntegerEvenlyDistributed(final RandomSupport randomSupport, final int count, final int min, final int max, final List<Float> marginOfErrors) {
        Map<Integer, AtomicInteger> distribution = new HashMap<>();

        for (int i = 0; i < count; i++) {
            int result = randomSupport.next(min, max);

            distribution.computeIfAbsent(result, __ -> new AtomicInteger()).incrementAndGet();
        }

        if (distribution.size() > max - min) {
            return false;
        }

        int marginOfErrorsSize = marginOfErrors.size();

        for (int i = 0, c = max / 2; i < c; i++) {
            int number1 = Optional.ofNullable(distribution.get(i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            int number2 = Optional.ofNullable(distribution.get(max - 1 - i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            float rate = (float) number1 / (float) number2;

            float marginOfError = i < marginOfErrorsSize
                    ? marginOfErrors.get(i)
                    : marginOfErrors.get(marginOfErrorsSize - 1);

            if (Float.compare(rate, 1f - marginOfError) < 0 || Float.compare(rate, 1f + marginOfError) > 0) {
                return false;
            }
        }

        return true;
    }
}
