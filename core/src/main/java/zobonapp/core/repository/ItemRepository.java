package zobonapp.core.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import zobonapp.core.domain.Item;
import zobonapp.core.domain.Status;

public interface ItemRepository extends CrudRepository<Item, UUID>
{
	String latestQuery="select max(updated) from" + 
			"(" + 
			"select max(updated) updated from item union " + 
			"select max(updated) updated from category union " + 
			"select max(updated) updated from contact " + 
			"    ) results" + 
			"\r\n" + 
			"";
	
	@EntityGraph(value="item.categories" ,type=EntityGraphType.LOAD)
	@Query("select distinct i from Item i where i.status=?1 and i.created>?2")
	Iterable<Item> findNewItems(Status status,Date lastUpdate);
	
	@EntityGraph(value="item.categories" ,type=EntityGraphType.LOAD)
	@Query("select distinct i from Item i where i.status=?1 and i.updated>?2 and i.created<=?2")
	Iterable<Item> findUpdatedItems(Status status,Date lastUpdate);
	
	
	@Query("select distinct i from Item i where i.status<>zobonapp.core.domain.Status.PUBLISHED and i.updated>?1 and i.created<=?1")
	Iterable<Item> findUnpubishedItems(Date lastUpdate);
	
	
	
	@EntityGraph(value="item.categories" ,type=EntityGraphType.LOAD)
	Item findByArName(String arName);
	@EntityGraph(value="item.categories" ,type=EntityGraphType.LOAD)
	Item findByEnName(String enName);
	
	
	@Query(value=latestQuery,nativeQuery=true)
	Timestamp latestUpdate();
}
