package fr.mikaelbenes.timesheeter.controller;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import fr.mikaelbenes.timesheeter.data.repository.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping( "/activities" )
public class ActivityRestController {

	private final static Logger log = LoggerFactory.getLogger( ActivityRestController.class );

	private final ActivityRepository activityRepository;

	@Autowired
	public ActivityRestController( ActivityRepository activityRepository ) {
		this.activityRepository = activityRepository;
	}

	@RequestMapping( method = RequestMethod.GET )
	public List<Activity> getActivities() {
		return this.activityRepository.findAll();
	}

	@RequestMapping( method = RequestMethod.GET, value = "/{id}" )
	public ResponseEntity<Activity> getActivity( @PathVariable Long id ) {
		Optional<Activity> actOptional = this.activityRepository.findOne( id );

		if ( actOptional.isPresent() ) {
			return new ResponseEntity<>( actOptional.get(), HttpStatus.FOUND );
		}

		return ResponseEntity.noContent().build();
	}

	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Activity> createActivity( @RequestBody Activity input ) {
		input.start();
		Activity newActivity	= this.activityRepository.save( input );

		log.info( "Successfully created activity. ID : {}", newActivity.getId() );

		return new ResponseEntity<>( newActivity, HttpStatus.CREATED );
	}

	@RequestMapping( path = "startFrom/{id}", method = RequestMethod.POST )
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

		log.info( "Successfully created activity from existing activity. ID : {}", newActivity.getId() );

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

		log.info( "Successfully stopped activity." );

		return new ResponseEntity<>( activity, HttpStatus.OK );
	}

	@RequestMapping( path = "/{id}", method = RequestMethod.PATCH )
	public ResponseEntity<Activity>  updateActivity( @PathVariable Long id, @RequestBody Activity input ) {
		Optional<Activity> actOptional = this.activityRepository.findOne( id );

		if ( !actOptional.isPresent() ) {
			return ResponseEntity.noContent().build();
		}

		Activity activity = actOptional.get();

		if ( !activity.getTitle().equals(input.getTitle()) ) {
			activity.setTitle( input.getTitle() );
		}

		if ( !activity.getActivityType().equals(input.getActivityType()) ) {
			activity.setActivityType( input.getActivityType() );
		}

		if ( !activity.getActivityTicket().equals(input.getActivityTicket()) ) {
			activity.setActivityTicket( input.getActivityTicket() );
		}

		activity = this.activityRepository.save( activity );

		return new ResponseEntity<>( activity, HttpStatus.OK );
	}

	@RequestMapping( path = "/{id}", method = RequestMethod.DELETE )
	public ResponseEntity<?> deleteActivity( @PathVariable Long id ) {
		this.activityRepository.delete( id );

		log.info( "Successfully deleted activity." );

		return ResponseEntity.noContent().build();
	}

}
