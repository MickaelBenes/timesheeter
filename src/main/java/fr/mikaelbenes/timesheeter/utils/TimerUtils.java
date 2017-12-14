package fr.mikaelbenes.timesheeter.utils;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

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

	/**
	 * Calculates the sum of all working times in a given list of activity.
	 *
	 * @param	activities	the activities list
	 * @return	a timestamp of the total working time
	 */
	public static long calculateWorkingTime(List<Activity> activities) {
		return activities.stream()
				.filter(activity -> !Objects.isNull(activity.getStopTime()))
				.mapToLong(Activity::getDurationTimestamp)
				.sum();
	}

	/**
	 * Converts a timestamp into a human readable time string.
	 *
	 * @param	timestamp	the timestamp to convert
	 * @return	a human readable time string
	 */
	public static String humanizeTimestamp(long timestamp) {
		long seconds	= Math.round( (float) timestamp / 1000 );
		long minutes	= seconds / 60;
		long hours		= minutes / 60;

		String secondsStr	= Long.toString(seconds % 60);
		String minutesStr	= Long.toString(minutes % 60);
		String hoursStr		= Long.toString(hours % 60);

		return StringUtils.leftPad(hoursStr, 2, '0') + ":"
				+ StringUtils.leftPad(minutesStr, 2, '0') + ":"
				+ StringUtils.leftPad(secondsStr, 2, '0');
	}

}
