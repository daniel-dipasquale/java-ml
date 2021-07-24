package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.common.time.NanosecondsDateTimeSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WaitHandleConstants {
    static final DateTimeSupport DATE_TIME_SUPPORT_NANOSECONDS = new NanosecondsDateTimeSupport();
}
