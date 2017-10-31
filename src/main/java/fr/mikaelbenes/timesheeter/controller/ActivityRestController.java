package fr.mikaelbenes.timesheeter.controller;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import fr.mikaelbenes.timesheeter.data.repository.ActivityRepository;
import fr.mikaelbenes.timesheeter.data.resource.ActivityResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
	public ActivityResource getActivity( @PathVariable Long id ) {
		Activity activity = this.activityRepository.findOne( id );

		return new ActivityResource( activity );
	}

	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<ActivityResource> startActivity( @RequestBody Activity input ) {
		input.start();
		Activity newActivity	= this.activityRepository.save( input );
		ActivityResource res	= new ActivityResource( newActivity );

		log.info( "Successfully created activity. ID : {}", newActivity.getId() );

		return new ResponseEntity<>( res, HttpStatus.CREATED );
	}

	@RequestMapping( path = "startFrom/{id}", method = RequestMethod.POST )
	public ResponseEntity<ActivityResource> startFromActivity( @PathVariable Long id ) {
		Activity fromActivity	= this.activityRepository.findOne( id );
		Activity newActivity	= new Activity( fromActivity.getTitle() );

		newActivity.setActivityType( fromActivity.getActivityType() );
		newActivity.setActivityTicket( fromActivity.getActivityTicket() );

		newActivity				= this.activityRepository.save( newActivity );
		ActivityResource res	= new ActivityResource( newActivity );
		Link forOneActivity		= res.getLink( "self" );

		log.info( "Successfully created activity from existing activity. ID : {}", newActivity.getId() );

		return new ResponseEntity<>( res, HttpStatus.CREATED );
	}

	@RequestMapping( path = "/{id}/stop", method = RequestMethod.POST )
	public ActivityResource stopActivity( @PathVariable Long id ) {
		Activity activity	= this.activityRepository.findOne( id );
		activity.stop();
		this.activityRepository.save( activity );

		log.info( "Successfully stopped activity." );

		return new ActivityResource( activity );
	}

	@RequestMapping( path = "/{id}", method = RequestMethod.DELETE )
	public ResponseEntity<?> deleteActivity( @PathVariable Long id ) {
		this.activityRepository.delete( id );

		log.info( "Successfully deleted activity." );

		return ResponseEntity.noContent().build();
	}

}
