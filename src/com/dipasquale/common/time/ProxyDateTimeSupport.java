package com.dipasquale.common.time;

import com.dipasquale.common.factory.LongFactory;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
public final class ProxyDateTimeSupport extends AbstractDateTimeSupport {
    @Serial
    private static final long serialVersionUID = 6619135767018277767L;
    private final LongFactory nowFactory;

    public ProxyDateTimeSupport(final LongFactory nowFactory, final TimeUnit timeUnit) {
        super(timeUnit);
        this.nowFactory = nowFactory;
    }

    public ProxyDateTimeSupport(final LongFactory nowFactory, final TimeUnit timeUnit, final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        super(timeUnit, dateTimeFormatter, dateTimeParser);
        this.nowFactory = nowFactory;
    }

    @Override
    public long now() {
        return nowFactory.create();
    }
}
