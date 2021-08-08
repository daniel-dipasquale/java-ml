package com.dipasquale.threading.event.loop;

import com.dipasquale.common.random.float1.DefaultRandomSupport;
import com.dipasquale.common.random.float1.RandomSupport;
import com.dipasquale.common.random.float1.ThreadLocalRandomSupport;
import lombok.Generated;

final class Constants {
    @Generated
    private Constants() {
    }

    static final RandomSupport RANDOM_SUPPORT_UNIFORM = new DefaultRandomSupport();
    static final RandomSupport RANDOM_SUPPORT_UNIFORM_CONCURRENT = new ThreadLocalRandomSupport();
}
