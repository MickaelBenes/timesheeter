package fr.mikaelbenes.timesheeter.data.repository;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

	Collection<Activity> findByActivityTicket( String activityTicket );

}
