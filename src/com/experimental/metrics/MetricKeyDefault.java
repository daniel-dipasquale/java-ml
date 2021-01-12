package com.experimental.metrics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.measure.unit.Unit;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class MetricKeyDefault implements MetricKey {
    private final String name;
    private final Unit<?> unit;
}
