package zobonapp.core.service.impl;

import java.util.Date;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import zobonapp.core.domain.Category;
import zobonapp.core.domain.Status;
import zobonapp.core.repository.CategoryRepository;
import zobonapp.core.service.CategoryService;
@Service("categoryService")
@Repository
@Transactional
public class CategoryServiceImpl implements CategoryService
{

	@Autowired
	private CategoryRepository categoryRepository;
	@Override
	public Category save(Category category)
	{
		
		return categoryRepository.save(category);
	}

	@Override
	public Iterable<Category> findAllCategories()
	{
		return categoryRepository.findAll();
	}

	@Override
	public Category update(UUID uuid, int rank)
	{
		Category category=categoryRepository.findOne(uuid);
		category.setRank(rank);
		return categoryRepository.save(category);
	}

	@Override
	public Category findByEnNameAndType(String enName,int type)
	{
		return categoryRepository.findByEnNameAndType(enName,type);
	}

	@Override
	public Category findByArNameAndType(String arName,int type)
	{
		return categoryRepository.findByArNameAndType(arName,type);
	}

	@Override
	public Iterable<Category> findNewCategories(Date lastUpdate)
	{
		return categoryRepository.findNewCategories(Status.PUBLISHED, lastUpdate);
	}

	@Override
	public Iterable<Category> findUpdatedCategories(Date lastUpdate)
	{
		return categoryRepository.findUpdatedCategories(Status.PUBLISHED, lastUpdate);
	}

	@Override
	public Iterable<Category> findUnpublishedCategories(Date lastUpdate)
	{
		return categoryRepository.findUnpubishedCategories(lastUpdate);
	}

	@Override
	public Iterable<Category> findCategoryByEnName(String enName)
	{
		return categoryRepository.findByEnName(enName);
	}


}
