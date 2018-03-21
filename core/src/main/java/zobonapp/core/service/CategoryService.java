package zobonapp.core.service;

import java.util.Date;
import java.util.UUID;

import zobonapp.core.domain.Category;

public interface CategoryService
{
	Category save(Category category);
	Iterable<Category> findAllCategories();
	Category findByEnName(String enName);
	Category findByArName(String arName);
	Category update(UUID uuid,int rank);
	Iterable<Category> findNewCategories(Date lastUpdate);
	Iterable<Category> findUpdatedCategories(Date lastUpdate);
	Iterable<Category> findUnpublishedCategories(Date lastUpdate);
}
