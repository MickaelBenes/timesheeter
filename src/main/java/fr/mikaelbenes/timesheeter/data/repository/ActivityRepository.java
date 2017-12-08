package fr.mikaelbenes.timesheeter.data.repository;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ActivityRepository extends BaseRepository<Activity, Long> {

	Collection<Activity> findByActivityTicket( String activityTicket );

	@Query("SELECT a FROM Activity a WHERE " +
			"LOWER(a.title) LIKE LOWER(CONCAT('%',:searchTerms, '%')) " +
			"OR a.activityTicket LIKE CONCAT('%',:searchTerms, '%')")
	List<Activity> findBySearchTerms(@Param("searchTerms") String searchTerms);

}
