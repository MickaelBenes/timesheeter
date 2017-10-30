package fr.mikaelbenes.timesheeter.controller;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import fr.mikaelbenes.timesheeter.data.repository.ActivityRepository;
import fr.mikaelbenes.timesheeter.data.resource.ActivityResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping( "/activities" )
public class ActivityRestController {

	private final static Logger log = LoggerFactory.getLogger( ActivityRestController.class );

	private final ActivityRepository activityRepository;

	@Autowired
	ActivityRestController( ActivityRepository activityRepository ) {
		this.activityRepository = activityRepository;
	}

	@RequestMapping( method = RequestMethod.GET )
	Resources<ActivityResource> getActivities() {
		List<ActivityResource> activities = this.activityRepository.findAll()
				.stream()
				.map( ActivityResource::new )
				.collect( Collectors.toList() );

		return new Resources<>( activities );
	}

	@RequestMapping( method = RequestMethod.GET, value = "/{id}" )
	public ActivityResource getActivity( @PathVariable Long id ) {
		Activity activity = this.activityRepository.findOne( id );

		return new ActivityResource( activity );
	}

	@RequestMapping( method = RequestMethod.POST )
	ResponseEntity<?> startActivity( @RequestBody Activity input ) {
		input.start();
		Activity newActivity	= this.activityRepository.save( input );
		Link forOneActivity		= new ActivityResource( newActivity ).getLink( "self" );

		log.info( "Successfully created activity. ID : {}", newActivity.getId() );

		return ResponseEntity.created( URI.create(forOneActivity.getHref()) ).build();
	}

	@RequestMapping( path = "startFrom/{id}", method = RequestMethod.POST )
	ResponseEntity<?> startFromActivity( @PathVariable Long id ) {
		Activity fromActivity	= this.activityRepository.findOne( id );
		Activity newActivity	= new Activity( fromActivity.getTitle() );

		newActivity.setActivityType( fromActivity.getActivityType() );
		newActivity.setActivityTicket( fromActivity.getActivityTicket() );

		newActivity			= this.activityRepository.save( newActivity );
		Link forOneActivity	= new ActivityResource( newActivity ).getLink( "self" );

		log.info( "Successfully created activity from existing activity. ID : {}", newActivity.getId() );

		return ResponseEntity.created( URI.create(forOneActivity.getHref()) ).build();
	}

	@RequestMapping( path = "/{id}/stop", method = RequestMethod.POST )
	ActivityResource stopActivity( @PathVariable Long id ) {
		Activity activity	= this.activityRepository.findOne( id );
		activity.stop();
		this.activityRepository.save( activity );

		log.info( "Successfully stopped activity." );

		return new ActivityResource( activity );
	}

}
