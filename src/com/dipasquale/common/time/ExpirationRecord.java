/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.common.time;

import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.io.Serial;
import java.io.Serializable;

@Generated
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class ExpirationRecord implements Serializable {
    @Serial
    private static final long serialVersionUID = -7606800053983722156L;
    private final long currentDateTime;
    private final long expirationDateTime;
    private final Unit<Duration> unit;
}
