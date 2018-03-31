package zobonapp.core.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import zobonapp.core.domain.Category;
import zobonapp.core.domain.Item;
import zobonapp.core.domain.Status;
import zobonapp.core.repository.CategoryRepository;
import zobonapp.core.repository.ItemRepository;
import zobonapp.core.service.ZobonAppService;
@Service("itemService")
@Repository
@Transactional
public class ZobonAppServiceImpl implements ZobonAppService
{
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ItemRepository itemRepository;
	@Override
	public Item save(Item item, ArrayList<String> categories)
	{
		for(String category:categories)
		{
			Category theCategory=categoryRepository.findByEnName(category);
			if(theCategory!=null)
				item.getCategories().add(theCategory);
			else System.out.println("Category Not Found:"+theCategory );
		}
		// TODO Auto-generated method stub
		return itemRepository.save(item);
	}
	@Override
	public Item save(Item item)
	{
		// TODO Auto-generated method stub
		return itemRepository.save(item);
	}
	@Override
	public Item find(UUID id)
	{
		return itemRepository.findOne(id);
	}
	@Override
	public Item findByArName(String arName)
	{
		return itemRepository.findByArName(arName);
	}
	@Override
	public Item findByEnName(String enName)
	{
		return itemRepository.findByEnName(enName);
	}
	@Override
	@Transactional(readOnly=true)
	public Timestamp latestUpdate()
	{
		// TODO Auto-generated method stub
		return itemRepository.latestUpdate();
	}
	@Override
	public Iterable<Item> findNewItems(Date lastUpdate)
	{
		return itemRepository.findNewItems(Status.PUBLISHED, lastUpdate);
	}
	@Override
	public Iterable<Item> findUpdatedItems(Date lastUpdate)
	{
		return itemRepository.findUpdatedItems(Status.PUBLISHED, lastUpdate);
	}
	@Override
	public Iterable<Item> findUnpublishedItems(Date lastUpdate)
	{
		return itemRepository.findUnpubishedItems(lastUpdate);
	}
	@Override
	public void test()
	{
		Item item=itemRepository.findOne(UUID.fromString("98abe435-844a-4864-b20b-4994e1f76004"));
		System.out.println(item.getId());
		System.out.println(item.getCategories().size());
		
	}
	@Override
	public List<UUID> findItemsIdsforHotline(String hotline)
	{
		return itemRepository.findItemsIdsforHotline("%"+hotline);
	}
	@Override
	public int updateItemRank(UUID id, int rank)
	{
		return itemRepository.updateRank(id, rank);
	}

}
