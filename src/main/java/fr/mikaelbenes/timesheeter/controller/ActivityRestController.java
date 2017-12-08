package fr.mikaelbenes.timesheeter.controller;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import fr.mikaelbenes.timesheeter.data.repository.ActivityRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping( "/activities" )
public class ActivityRestController {

	private static final Logger logger = LoggerFactory.getLogger( ActivityRestController.class );

	private final ActivityRepository activityRepository;

	@Autowired
	public ActivityRestController( ActivityRepository activityRepository ) {
		this.activityRepository = activityRepository;
	}

	@RequestMapping( method = RequestMethod.GET )
	public ResponseEntity<List<Activity>> getActivities() {
		List<Activity> activities = this.activityRepository.findAll();

		return ResponseEntity.ok(activities);
	}

	@RequestMapping( method = RequestMethod.GET, value = "/{id}" )
	public ResponseEntity<Activity> getActivity( @PathVariable Long id ) {
		Optional<Activity> actOptional = this.activityRepository.findOne( id );

		if ( actOptional.isPresent() ) {
			return ResponseEntity.ok(actOptional.get());
		}

		return ResponseEntity.noContent().build();
	}

	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Activity> createActivity( @RequestBody Activity input ) {
		input.start();
		Activity newActivity	= this.activityRepository.save( input );

		logger.info( "Successfully created activity. ID : {}", newActivity.getId() );

		return new ResponseEntity<>( newActivity, HttpStatus.CREATED );
	}

	@RequestMapping( path = "/{id}/duplicate", method = RequestMethod.POST )
	public ResponseEntity<Activity> duplicateActivity( @PathVariable Long id ) {
		Optional<Activity> actOptional = this.activityRepository.findOne( id );

		if ( !actOptional.isPresent() ) {
			return ResponseEntity.noContent().build();
		}

		Activity fromActivity	= actOptional.get();
		Activity newActivity	= new Activity( fromActivity.getTitle() );

		newActivity.setActivityType( fromActivity.getActivityType() );
		newActivity.setActivityTicket( fromActivity.getActivityTicket() );

		newActivity	= this.activityRepository.save( newActivity );

		logger.info( "Successfully duplicated activity #{}. ID : {}", id, newActivity.getId() );

		return new ResponseEntity<>( newActivity, HttpStatus.CREATED );
	}

	@RequestMapping( path = "/{id}/stop", method = RequestMethod.POST )
	public ResponseEntity<Activity> stopActivity( @PathVariable Long id ) {
		Optional<Activity> actOptional = this.activityRepository.findOne( id );

		if ( !actOptional.isPresent() ) {
			return ResponseEntity.noContent().build();
		}

		Activity activity = actOptional.get();
		activity.stop();
		activity = this.activityRepository.save( activity );

		logger.info( "Successfully stopped activity." );

		return ResponseEntity.ok( activity );
	}

	@RequestMapping( path = "/{id}", method = RequestMethod.PATCH )
	public ResponseEntity<Activity>  updateActivity( @PathVariable Long id, @RequestBody Activity input ) {
		Optional<Activity> actOptional = this.activityRepository.findOne( id );

		if ( !actOptional.isPresent() ) {
			return ResponseEntity.noContent().build();
		}

		Activity activity = actOptional.get();
		activity.setTitle( input.getTitle() );
		activity.setActivityType( input.getActivityType() );
		activity.setActivityTicket( input.getActivityTicket() );

		activity = this.activityRepository.save( activity );

		logger.info( "Successfully updated activity #{}.", activity.getId() );

		return ResponseEntity.ok( activity );
	}

	@RequestMapping( path = "/{id}", method = RequestMethod.DELETE )
	public ResponseEntity deleteActivity( @PathVariable Long id ) {
		this.activityRepository.delete( id );

		logger.info( "Successfully deleted activity #{}.", id );

		return ResponseEntity.noContent().build();
	}

	@RequestMapping( path = "/totalTime", method = RequestMethod.GET, produces = "application/json; charset=UTF-8" )
	public ResponseEntity<String> getTotalTimeAsString() {
		long totalTime	= this.activityRepository.findAll()
				.stream()
				.filter( activity -> !Objects.isNull(activity.getStopTime()) )
				.mapToLong( Activity::getDurationTimestamp )
				.sum();

		if ( totalTime == 0 ) {
			return ResponseEntity.noContent().build();
		}

		long seconds	= Math.round( (float) totalTime / 1000 );
		long minutes	= seconds / 60;
		long hours		= minutes / 60;

		String secondsStr	= Long.toString(seconds % 60);
		String minutesStr	= Long.toString(minutes % 60);
		String hoursStr		= Long.toString(hours % 60);
		String totalTimeStr	= StringUtils.leftPad(hoursStr, 2, '0') + ":"
				+ StringUtils.leftPad(minutesStr, 2, '0') + ":"
				+ StringUtils.leftPad(secondsStr, 2, '0');

		String jsonResponse = "{" +
				"\"totalTime\": \"" + totalTimeStr + "\"" +
			"}";

		return ResponseEntity.ok( jsonResponse );
	}

	@RequestMapping(path = "/search/{searchTerms}", method = RequestMethod.GET)
	public ResponseEntity<List<Activity>> search(@PathVariable String searchTerms) {
		List<Activity> activities = this.activityRepository.findBySearchTerms(searchTerms);

		return ResponseEntity.ok(activities);
	}

}
