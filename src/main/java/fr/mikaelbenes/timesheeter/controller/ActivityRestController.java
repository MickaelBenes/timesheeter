package fr.mikaelbenes.timesheeter.controller;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import fr.mikaelbenes.timesheeter.data.repository.ActivityRepository;
import fr.mikaelbenes.timesheeter.data.resource.ActivityResource;
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
	public ActivityResource getActivity( @PathVariable String id ) {
		Activity activity = this.activityRepository.findOne( id );

		return new ActivityResource( activity );
	}

	@RequestMapping( method = RequestMethod.POST )
	ResponseEntity<?> addActivity( @RequestBody Activity activity ) {
		Activity newActivity	= this.activityRepository.save( new Activity(activity.getTitle()) );
		Link forOneActivity		= new ActivityResource( newActivity ).getLink( "self" );

		return ResponseEntity.created( URI.create(forOneActivity.getHref()) ).build();
	}

}
