package zobonapp.core.repository;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import zobonapp.core.domain.Category;
import zobonapp.core.domain.Item;
import zobonapp.core.domain.Status;

public interface CategoryRepository extends CrudRepository<Category, UUID>
{
	Iterable<Category> findByArName(String arName);
	Category findByArNameAndType(String arName,int type);
	
	Iterable<Category> findByEnName(String enName);
	Category findByEnNameAndType(String enName,int type);
	
	@Query("select c from Category c where c.status=?1 and c.created>?2")
	Iterable<Category> findNewCategories(Status status,Date lastUpdate);
	
	@Query("select c from Category c where c.status=?1 and c.updated>?2 and c.created<=?2")
	Iterable<Category> findUpdatedCategories(Status status,Date lastUpdate);
	
	@Query("select c from Category c where c.status<>zobonapp.core.domain.Status.PUBLISHED and c.updated>?1 and c.created<=?1")
	Iterable<Category> findUnpubishedCategories(Date lastUpdate);
}
