package com.experimental.rate.limit.simple;

import lombok.Builder;
import lombok.Generated;
import lombok.Getter;

@Generated
@Builder
@Getter
public final class RateLimitSlidingWindow {
    private final long milliseconds;
    private final int buckets;
}

