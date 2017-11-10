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
	public Activity getActivity( @PathVariable Long id ) {
		return this.activityRepository.findOne( id );
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
		Activity fromActivity	= this.activityRepository.findOne( id );
		Activity newActivity	= new Activity( fromActivity.getTitle() );

		newActivity.setActivityType( fromActivity.getActivityType() );
		newActivity.setActivityTicket( fromActivity.getActivityTicket() );

		newActivity	= this.activityRepository.save( newActivity );

		log.info( "Successfully created activity from existing activity. ID : {}", newActivity.getId() );

		return new ResponseEntity<>( newActivity, HttpStatus.CREATED );
	}

	@RequestMapping( path = "/{id}/stop", method = RequestMethod.POST )
	public Activity stopActivity( @PathVariable Long id ) {
		Activity activity	= this.activityRepository.findOne( id );
		activity.stop();

		log.info( "Successfully stopped activity." );

		return this.activityRepository.save( activity );
	}

	@RequestMapping( path = "/{id}", method = RequestMethod.DELETE )
	public ResponseEntity<?> deleteActivity( @PathVariable Long id ) {
		this.activityRepository.delete( id );

		log.info( "Successfully deleted activity." );

		return ResponseEntity.noContent().build();
	}

}
