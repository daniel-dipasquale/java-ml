package com.dipasquale.common.random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public interface RandomSupportTestSupport {
    static boolean isNextFloatBounded(final RandomSupport randomSupport, final int count, final float minimum, final float maximum) {
        for (int i = 0; i < count; i++) {
            float result = randomSupport.nextFloat();

            if (Float.compare(result, minimum) < 0 || Float.compare(result, maximum) >= 0) {
                return false;
            }
        }

        return true;
    }

    static boolean isNextIntegerEvenlyDistributed(final RandomSupport randomSupport, final int count, final int minimum, final int maximum, final List<Float> marginOfErrors) {
        Map<Integer, AtomicInteger> distribution = new HashMap<>();

        for (int i = 0; i < count; i++) {
            int result = randomSupport.nextInteger(minimum, maximum);

            distribution.computeIfAbsent(result, __ -> new AtomicInteger()).incrementAndGet();
        }

        if (distribution.size() > maximum - minimum) {
            return false;
        }

        int marginOfErrorsSize = marginOfErrors.size();

        for (int i = 0, c = maximum / 2; i < c; i++) {
            int number1 = Optional.ofNullable(distribution.get(i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            int number2 = Optional.ofNullable(distribution.get(maximum - 1 - i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            float rate = (float) number1 / (float) number2;

            float marginOfError = i >= marginOfErrorsSize
                    ? marginOfErrors.get(marginOfErrorsSize - 1)
                    : marginOfErrors.get(i);

            if (Float.compare(rate, 1f - marginOfError) < 0 || Float.compare(rate, 1f + marginOfError) > 0) {
                return false;
            }
        }

        return true;
    }
}
