package fr.mikaelbenes.timesheeter.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimerUtils {

	/**
	 * Private constructor to hide the public one.<br>
	 * SonarLint said...
	 */
	private TimerUtils() {}

	/**
	 * Gives the time duration from two given dates of the same day.
	 *
	 * @param	startDateTime starting date time
	 * @param	stopDateTime  stopping date time
	 * @return	the duration
	 */
	public static String getDuration( LocalDateTime startDateTime, LocalDateTime stopDateTime ) {
		if ( !startDateTime.toLocalDate().isEqual(stopDateTime.toLocalDate()) ) {
			return "";
		}

		long startTime	= convertToTimestamp(startDateTime);
		long stopTime	= convertToTimestamp(stopDateTime);
		long duration	= Math.round((float) (stopTime - startTime) / 1000);

		return LocalTime.ofSecondOfDay( duration ).toString();
	}

	public static long convertToTimestamp( LocalDateTime localDateTime ) {
		return Timestamp.valueOf( localDateTime ).getTime();
	}

}
