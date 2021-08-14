package com.dipasquale.common.time;

import com.dipasquale.common.factory.LongFactory;
import lombok.EqualsAndHashCode;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.io.Serial;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
public final class ProxyDateTimeSupport extends AbstractDateTimeSupport {
    @Serial
    private static final long serialVersionUID = 6619135767018277767L;
    private final LongFactory nowFactory;

    public ProxyDateTimeSupport(final LongFactory nowFactory, final Unit<Duration> unit) {
        super(unit);
        this.nowFactory = nowFactory;
    }

    public ProxyDateTimeSupport(final LongFactory nowFactory, final Unit<Duration> unit, final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        super(unit, dateTimeFormatter, dateTimeParser);
        this.nowFactory = nowFactory;
    }

    @Override
    public long now() {
        return nowFactory.create();
    }
}
