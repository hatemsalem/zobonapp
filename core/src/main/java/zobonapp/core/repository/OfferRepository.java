package zobonapp.core.repository;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;

import zobonapp.core.domain.Offer;
import zobonapp.core.domain.Status;

public interface OfferRepository extends CrudRepository<Offer, UUID>
{
	Offer findBySrcAndSrcId(String src,String SrcId);
	
	@EntityGraph(value="offer.categories" ,type=EntityGraphType.LOAD)
	@Query("select o from Offer o where o.status=?1 and o.created>?2")
	Iterable<Offer> findNewOffers(Status status,Date lastUpdate);

	@EntityGraph(value="offer.categories" ,type=EntityGraphType.LOAD)
	@Query("select distinct o from Offer o where o.status=?1 and o.updated>?2 and o.created<=?2")
	Iterable<Offer> findUpdatedOffers(Status published, Date lastUpdate);

	@Query("select distinct o from Offer o where o.status<>zobonapp.core.domain.Status.PUBLISHED and o.updated>?1 and o.created<=?1")
	Iterable<Offer> findUnpubishedOffers(Date lastUpdate);
}
