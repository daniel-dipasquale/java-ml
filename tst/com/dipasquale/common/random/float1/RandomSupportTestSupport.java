/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.random.float1;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
public final class RandomSupportTestSupport {
    public static boolean isNextFloatBounded(final RandomSupport randomSupport, final int count, final float min, final float max) {
        for (int i = 0; i < count; i++) {
            float result = randomSupport.next();

            if (Float.compare(result, min) < 0 || Float.compare(result, max) >= 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean isNextIntegerMeanDistributed(final RandomSupport randomSupport, final int count, final int min, final int max, final List<Float> marginOfErrors) {
        Map<Integer, AtomicInteger> distribution = new HashMap<>();

        for (int i = 0; i < count; i++) {
            int result = randomSupport.next(min, max);

            distribution.computeIfAbsent(result, k -> new AtomicInteger()).incrementAndGet();
        }

        if (distribution.size() > max) {
            return false;
        }

        for (int i = 0, c = max / 2; i < c; i++) { // TODO: not checking if the ratios are increase towards the mean
            float number1 = (float) Optional.ofNullable(distribution.get(i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            float number2 = (float) Optional.ofNullable(distribution.get(max - 1 - i))
                    .map(AtomicInteger::get)
                    .orElse(1);

            float ratio = number1 / number2;

            float marginOfError = i < marginOfErrors.size()
                    ? marginOfErrors.get(i)
                    : marginOfErrors.get(marginOfErrors.size() - 1);

            if (Float.compare(ratio, 1f - marginOfError) < 0 || Float.compare(ratio, 1f + marginOfError) > 0) {
                return false;
            }
        }

        return true;
    }
}
