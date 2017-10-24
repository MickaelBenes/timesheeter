package fr.mikaelbenes.timesheeter.data.repository;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, String> {

	Collection<Activity> findByActivityTicket( String activityTicket );

}
