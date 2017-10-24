package fr.mikaelbenes.timesheeter.data.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;

@Entity
public class Activity {

	public Activity() {}

	public Activity( String title ) {
		this.title = title;
	}

	@Id
	@GeneratedValue
	@Getter
	private String id;

	@Getter
	private String title;

	@Getter
	private String activityType;

	@Getter
	private String activityTicket;

	@Getter
	private LocalTime startTime;

	@Getter
	private LocalTime endTime;

}
