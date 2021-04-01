package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ThreadingConstants {
    public static final DateTimeSupport DATE_TIME_SUPPORT_NANOSECONDS = DateTimeSupport.createNanoseconds();
}
