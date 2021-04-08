package com.dipasquale.common;

import java.io.Serial;
import java.io.Serializable;

@FunctionalInterface
public interface ExpirySupport extends Serializable {
    ExpiryRecord next();

    private static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset, final double slider) {
        ArgumentValidatorUtils.ensureNotNull(dateTimeSupport, "dateTimeSupport");
        ArgumentValidatorUtils.ensureGreaterThanZero(expiryTime, "expiryTime");
        ArgumentValidatorUtils.ensureGreaterThanOrEqualToZero(offset, "offset");
        ArgumentValidatorUtils.ensureGreaterThan(expiryTime, offset, "expiryTime");

        double expiryTimeDouble = (double) expiryTime;

        return new ExpirySupport() {
            @Serial
            private static final long serialVersionUID = 7891950777397882031L;

            @Override
            public ExpiryRecord next() {
                long currentDateTime = dateTimeSupport.now();
                long expiryDateTimePrevious = dateTimeSupport.getTimeFrameFor(currentDateTime, expiryTime, offset);
                long expiryDateTime = expiryDateTimePrevious + expiryTime * Math.round((expiryTimeDouble * slider + (double) (expiryTime + currentDateTime - offset) % expiryTime) / expiryTimeDouble);

                return new ExpiryRecord(currentDateTime, expiryDateTime, dateTimeSupport.unit());
            }
        };
    }

    static ExpirySupport createSlider(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset) {
        return create(dateTimeSupport, expiryTime, offset, 1D);
    }

    static ExpirySupport createSlider(final DateTimeSupport dateTimeSupport, final long expiryTime) {
        return createSlider(dateTimeSupport, expiryTime, 0L);
    }

    static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime, final long offset) {
        return create(dateTimeSupport, expiryTime, offset, 0.5D);
    }

    static ExpirySupport create(final DateTimeSupport dateTimeSupport, final long expiryTime) {
        return create(dateTimeSupport, expiryTime, 0L);
    }

    static Factory createSliderFactory(final DateTimeSupport dateTimeSupport) {
        return new Factory() {
            @Serial
            private static final long serialVersionUID = 6563396837209486475L;

            @Override
            public ExpirySupport create(final long expiryTime, final long offset) {
                return createSlider(dateTimeSupport, expiryTime, offset);
            }
        };
    }

    static Factory createFactory(final DateTimeSupport dateTimeSupport) {
        return new Factory() {
            @Serial
            private static final long serialVersionUID = 6563396837209486475L;

            @Override
            public ExpirySupport create(final long expiryTime, final long offset) {
                return ExpirySupport.create(dateTimeSupport, expiryTime, offset);
            }
        };
    }

    @FunctionalInterface
    interface Factory extends Serializable {
        ExpirySupport create(long expiryTime, long offset);

        default ExpirySupport create(final long expiryTime) {
            return create(expiryTime, 0L);
        }
    }
}
