package fr.mikaelbenes.timesheeter.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimerUtils {

	private static final ZoneId ZONE_ID_PARIS = ZoneId.of( ZoneId.SHORT_IDS.get("ECT") );

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

		ZonedDateTime startZdt	= startDateTime.atZone( ZONE_ID_PARIS );
		ZonedDateTime stopZdt	= stopDateTime.atZone( ZONE_ID_PARIS );

		long startTime	= startZdt.toInstant().toEpochMilli();
		long stopTime	= stopZdt.toInstant().toEpochMilli();
		long duration	= ( stopTime - startTime ) / 1000;

		return LocalTime.ofSecondOfDay( duration ).toString();
	}

}
