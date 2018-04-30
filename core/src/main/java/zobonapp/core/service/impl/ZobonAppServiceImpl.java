package zobonapp.core.service.impl;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import zobonapp.core.domain.BusinessEntity;
import zobonapp.core.domain.Category;
import zobonapp.core.domain.Offer;
import zobonapp.core.domain.Status;
import zobonapp.core.repository.CategoryRepository;
import zobonapp.core.repository.ItemRepository;
import zobonapp.core.repository.OfferRepository;
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
	@Autowired
	private OfferRepository offerRepository;
	@Override
	public BusinessEntity save(BusinessEntity item, List<String> categories)
	{
		for(String category:categories)
		{
			Iterator< Category> entities=categoryRepository.findByEnName(category).iterator();
			if(entities!=null&&entities.hasNext())
				item.getCategories().add(entities.next());
			else System.out.println("Category Not Found:"+category+ " for item :"+item.getEnName() );
		}
		// TODO Auto-generated method stub
		return itemRepository.save(item);
	}
	@Override
	public BusinessEntity save(BusinessEntity item)
	{
		// TODO Auto-generated method stub
		return itemRepository.save(item);
	}
	@Override
	public BusinessEntity find(UUID id)
	{
		return itemRepository.findOne(id);
	}
	@Override
	public BusinessEntity findByArName(String arName)
	{
		return itemRepository.findByArName(arName);
	}
	@Override
	public BusinessEntity findByEnName(String enName)
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
	public Iterable<BusinessEntity> findNewItems(Date lastUpdate)
	{
		return itemRepository.findNewItems(Status.PUBLISHED, lastUpdate);
	}
	@Override
	public Iterable<BusinessEntity> findUpdatedItems(Date lastUpdate)
	{
		return itemRepository.findUpdatedItems(Status.PUBLISHED, lastUpdate);
	}
	@Override
	public Iterable<BusinessEntity> findUnpublishedItems(Date lastUpdate)
	{
		return itemRepository.findUnpubishedItems(lastUpdate);
	}
	@Override
	public void test()
	{
		BusinessEntity item=itemRepository.findOne(UUID.fromString("98abe435-844a-4864-b20b-4994e1f76004"));
		System.out.println(item.getId());
		System.out.println(item.getCategories().size());
		
	}
	@Override
	public List<UUID> findItemsIdsforHotline(String hotline)
	{
		return itemRepository.findItemsIdsforHotline("tel:"+hotline);
	}
	@Override
	public int updateItemRank(UUID id, int rank)
	{
		return itemRepository.updateRank(id, rank,new Date());
	}
	@Override
	public Iterable<Offer> findNewOffers(Date lastUpdate)
	{
		return  offerRepository.findNewOffers(Status.PUBLISHED, lastUpdate);
	}
	@Override
	public Offer save(Offer offer, List<String> categories)
	{
		for(String category:categories)
		{
			Iterator< Category> entities=categoryRepository.findByEnName(category).iterator();
			if(entities!=null&&entities.hasNext())
				offer.getCategories().add(entities.next());
			else System.out.println("Category Not Found:"+category +" for offer:"+offer.getEnName()+" of item:"+offer.getEntity().getEnName() );
		}
		// TODO Auto-generated method stub
		return offerRepository.save(offer);
	}
	@Override
	public Offer save(Offer offer)
	{
		return offerRepository.save(offer);
	}
	@Override
	public Iterable<Offer> findUpdatedOffers(Date lastUpdate)
	{
		return offerRepository.findUpdatedOffers(Status.PUBLISHED, lastUpdate);
	}
	@Override
	public Iterable<Offer> findUnpublishedOffers(Date lastUpdate)
	{
		return offerRepository.findUnpubishedOffers(lastUpdate);
	}
	@Override
	public int offerRetrofit()
	{
		Date today=Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
		return offerRepository.retrofit(new Date(),Status.REVIEWED,Status.PUBLISHED, today);
	}
	@Override
	public void publishCategory(Category category)
	{
		
		category.setStatus(Status.PUBLISHED);
		categoryRepository.save(category);
		
		Date updated=new Date();
		Iterable<BusinessEntity> touchEntities=itemRepository.findEntitiesInCategory(category);
		for(BusinessEntity entity:touchEntities)
		{
			itemRepository.touchEntity(entity, updated);
		}
		Iterable<Offer> touchOffers=offerRepository.findOffersInCategory(category);
		for(Offer offer:touchOffers)
		{
			offerRepository.touchOffer(offer, updated);
		}
		
		System.out.println("=======");
//		itemRepository.touchPublishedCategoryEntities(category, new Date());
		
	}
	@Override
	public void publishEntity(BusinessEntity entity)
	{
		entity.setStatus(Status.PUBLISHED);
		itemRepository.save(entity);
		Date updated=new Date();
		Iterable<Offer> touchOffers=offerRepository.findOffersInEntity(entity);
		for(Offer offer:touchOffers)
		{
			offerRepository.touchOffer(offer, updated);
		}
		
		
	}
	
}
