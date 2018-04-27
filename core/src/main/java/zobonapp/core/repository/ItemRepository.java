package zobonapp.core.repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import zobonapp.core.domain.BusinessEntity;
import zobonapp.core.domain.Category;
import zobonapp.core.domain.Status;

public interface ItemRepository extends CrudRepository<BusinessEntity, UUID>
{
	String latestQuery="select max(updated) from" + 
			"(" + 
			"select max(updated) updated from businessentity union " + 
			"select max(updated) updated from category union " +
			"select max(updated) updated from offer union " +
			"select max(updated) updated from menu union " + 
			"select max(updated) updated from contact " + 
			"    ) results" + 
			"\r\n" + 
			"";
	
	@EntityGraph(value="item.categories" ,type=EntityGraphType.LOAD)
	@Query("select distinct i from BusinessEntity i where i.status=?1 and i.created>?2")
	Iterable<BusinessEntity> findNewItems(Status status,Date lastUpdate);
	
	@EntityGraph(value="item.categories" ,type=EntityGraphType.LOAD)
	@Query("select distinct i from BusinessEntity i where i.status=?1 and i.updated>?2 and i.created<=?2")
	Iterable<BusinessEntity> findUpdatedItems(Status status,Date lastUpdate);
	
	
	@Query("select distinct i from BusinessEntity i where i.status<>zobonapp.core.domain.Status.PUBLISHED and i.updated>?1 and i.created<=?1")
	Iterable<BusinessEntity> findUnpubishedItems(Date lastUpdate);
	
	
	
	@EntityGraph(value="item.categories" ,type=EntityGraphType.LOAD)
	BusinessEntity findByArName(String arName);
	@EntityGraph(value="item.categories" ,type=EntityGraphType.LOAD)
	BusinessEntity findByEnName(String enName);
	
	
	@Query(value=latestQuery,nativeQuery=true)
	Timestamp latestUpdate();
	
	@Query(value="select c.entity.id from Contact c where c.uri like ?1")
	List<UUID> findItemsIdsforHotline(String hotline);
	
	@Modifying
	@Query("update BusinessEntity i set i.rank=?2,version=version+1,updated=?3 where i.id=?1")
	int updateRank(UUID id,int newRank,Date updated);
	
	@Modifying
	@Query("update BusinessEntity i set i.updated=?2 where i=?1  ")
	int touchEntity(BusinessEntity entity, Date updated);
	
	@Query("select distinct i from  BusinessEntity i join fetch i.categories c  where  c=?1")
	Iterable<BusinessEntity> findEntitiesInCategory(Category  category);
	
	
//	@Query("select i from BusinessEntity i join fetch i.categories c where c.id=?1")
//	Iterable<BusinessEntity> findUpdatedItems(Status status,Date lastUpdate);
}
