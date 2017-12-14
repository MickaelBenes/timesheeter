package fr.mikaelbenes.timesheeter.data.repository;

import fr.mikaelbenes.timesheeter.data.domain.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ActivityRepository extends BaseRepository<Activity, Long> {

	List<Activity> findByActivityTicket(String activityTicket);

	@Query(
		"SELECT a FROM Activity a " +
		"WHERE LOWER(a.title) LIKE LOWER(CONCAT('%',:searchTerms, '%')) " +
		"OR a.activityTicket = :searchTerms"
	)
	List<Activity> findBySearchTerms(@Param("searchTerms") String searchTerms);

	@Query("SELECT a FROM Activity a WHERE a.id IN :ids")
	List<Activity> findAllByIds(@Param("ids") Set<Long> ids);

}
