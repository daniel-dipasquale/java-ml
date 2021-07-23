package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.time.DateTimeSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WaitHandleConstants {
    static final DateTimeSupport DATE_TIME_SUPPORT_NANOSECONDS = DateTimeSupport.createNanoseconds();
}
