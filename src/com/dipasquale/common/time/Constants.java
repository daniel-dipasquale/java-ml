package com.dipasquale.common.time;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

interface Constants {
    ZoneId UTC = ZoneId.of("UTC");
    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZone(UTC);
    DateTimeFormatter DATE_TIME_PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd[[ ]['T']HH:mm[:ss[.SSS][z][Z]]]").withZone(UTC);
}
