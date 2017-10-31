package fr.mikaelbenes.timesheeter.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonIgnore
	@Getter @Setter
	private LocalDateTime stopTime;

	public Activity() {}

	public Activity( String title ) {
		this.title	= title;

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
