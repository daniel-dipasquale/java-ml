/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.time;

import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
public final class MillisecondsDateTimeSupport extends AbstractDateTimeSupport {
    @Serial
    private static final long serialVersionUID = 7552571945900875884L;

    public MillisecondsDateTimeSupport() {
        super(Constants.MILLISECONDS_UNIT);
    }

    public MillisecondsDateTimeSupport(final DateTimeFormatter dateTimeFormatter, final DateTimeFormatter dateTimeParser) {
        super(Constants.MILLISECONDS_UNIT, dateTimeFormatter, dateTimeParser);
    }

    @Override
    public long now() {
        return System.currentTimeMillis();
    }
}
