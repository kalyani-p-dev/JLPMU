package com.jlp.pmu.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateUtils {
	
	public static LocalDate extractDate(LocalDateTime dateTime) {
        return dateTime.toLocalDate();
    }

    public static LocalTime extractTime(LocalDateTime dateTime) {
        return dateTime.toLocalTime();
    }

}
