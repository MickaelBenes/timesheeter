package fr.mikaelbenes.timesheeter.data.repository;

import fr.mikaelbenes.timesheeter.data.domain.Activity;

import java.util.Collection;

public interface ActivityRepository extends BaseRepository<Activity, Long> {

	Collection<Activity> findByActivityTicket( String activityTicket );

}
