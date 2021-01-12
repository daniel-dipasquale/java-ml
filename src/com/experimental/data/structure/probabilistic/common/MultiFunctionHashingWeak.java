package com.experimental.data.structure.probabilistic.common;

import com.dipasquale.data.structure.probabilistic.MultiFunctionHashing;
import lombok.Getter;

final class MultiFunctionHashingWeak implements MultiFunctionHashing {
    private static final long ORIGIN = 0x5_44B_2FB_ACA_AF1_684L;
    private static final long OFFSET = 0xB_B40_E64_DA2_05B_064L;
    private static final long FACTOR = 7_664_345_821_815_920_749L;
    private static final int SIZE = 255;
    private final long[] hashCodes; // NOTE: based on: https://www.javamex.com/tutorials/collections/bloom_filter_java.shtml
    @Getter
    private final int maximumHashFunctions;
    private final long offset;
    private final long factor;

    MultiFunctionHashingWeak(final int maximumHashFunctions, final long origin, final long offset, final long factor) {
        this.hashCodes = createHashCodes(maximumHashFunctions, origin);
        this.maximumHashFunctions = maximumHashFunctions;
        this.offset = offset;
        this.factor = factor;
    }

    MultiFunctionHashingWeak(final int maximumHashFunctions) {
        this(maximumHashFunctions, ORIGIN, OFFSET, FACTOR);
    }

    private static long[] createHashCodes(final int maximumHashFunctions, final long origin) {
        long[] table = new long[(SIZE + 1) * maximumHashFunctions];
        long hashCode = origin;

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < 31; j++) {
                hashCode = (hashCode >>> 7) ^ hashCode;
                hashCode = (hashCode << 11) ^ hashCode;
                hashCode = (hashCode >>> 10) ^ hashCode;
            }

            table[i] = hashCode;
        }

        return table;
    }

    @Override
    public long hashCode(final int hashCode, final int hashingFunction) {
        long output = offset;
        int offset = (SIZE + 1) * hashingFunction;

        output = (output * factor) ^ hashCodes[offset + (hashCode & SIZE)];
        output = (output * factor) ^ hashCodes[offset + ((hashCode >>> 8) & SIZE)];

        return output;
    }
}
