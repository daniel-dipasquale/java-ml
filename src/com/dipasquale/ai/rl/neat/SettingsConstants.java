package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.DateTimeSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SettingsConstants {
    static final DateTimeSupport DATE_TIME_SUPPORT_MILLISECONDS = DateTimeSupport.createMilliseconds();
}
