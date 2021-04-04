package com.dipasquale.threading.event.loop;

import com.dipasquale.common.RandomSupportFloat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class EventLoopConstants {
    static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM = RandomSupportFloat.create();
    static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM_CONCURRENT = RandomSupportFloat.createConcurrent();
}
