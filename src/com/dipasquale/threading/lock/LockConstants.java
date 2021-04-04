package com.dipasquale.threading.lock;

import com.dipasquale.common.DateTimeSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class LockConstants {
    static final DateTimeSupport DATE_TIME_SUPPORT_NANOSECONDS = DateTimeSupport.createNanoseconds();
}
