package fr.mikaelbenes.timesheeter.data.domain;

import fr.mikaelbenes.timesheeter.utils.TimerUtils;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Activity {

	@Id
	@GeneratedValue
	@Getter
	private Long id;

	@Getter @Setter
	private String title;

	@Getter @Setter
	private String activityType;

	@Getter @Setter
	private String activityTicket;

	@Getter @Setter
	private LocalDateTime startTime;

	@Getter @Setter
	private LocalDateTime stopTime;

	public Activity() {}

	public Activity( String title ) {
		this.title			= title;
		this.activityType	= null;
		this.activityTicket	= null;

		this.start();
	}

	/**
	 * Full constructor to facilitate unit testing.
	 *
	 * @param	title			activity title
	 * @param	activityType	activity type (Redmine...)
	 * @param	activityTicket	activity ticket number
	 */
	public Activity( String title, String activityType, String activityTicket ) {
		this.title			= title;
		this.activityType	= activityType;
		this.activityTicket	= activityTicket;

		this.start();
	}

	public void start() {
		this.startTime	= LocalDateTime.now();
	}

	public void stop() {
		this.stopTime = LocalDateTime.now();
	}

	public String getDuration() {
		if ( Objects.isNull(this.stopTime) ) {
			return "";
		}

		return TimerUtils.getDuration( this.startTime, this.stopTime );
	}

}
