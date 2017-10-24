package fr.mikaelbenes.timesheeter.data.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import fr.mikaelbenes.timesheeter.controller.ActivityRestController;
import fr.mikaelbenes.timesheeter.data.domain.Activity;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

public class ActivityResource extends ResourceSupport {

	@Getter
	private final Activity activity;

	public ActivityResource( Activity activity ) {
		this.activity	= activity;

		this.add( linkTo(ActivityRestController.class).withRel("activities") );
		this.add(
				linkTo(
					methodOn( ActivityRestController.class ).getActivity( this.activity.getId() )
				).withSelfRel()
		);
	}

}
