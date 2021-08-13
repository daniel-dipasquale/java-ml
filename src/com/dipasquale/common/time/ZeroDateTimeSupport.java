/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.time;

import lombok.EqualsAndHashCode;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.io.Serial;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
public final class ZeroDateTimeSupport extends AbstractDateTimeSupport {
    @Serial
    private static final long serialVersionUID = 4537808299655330771L;

    public ZeroDateTimeSupport(final Unit<Duration> unit) {
        super(unit);
    }

    public ZeroDateTimeSupport(final Unit<Duration> unit, final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        super(unit, dateTimeFormatter, dateTimeParser);
    }

    @Override
    public long now() {
        return 0L;
    }
}
