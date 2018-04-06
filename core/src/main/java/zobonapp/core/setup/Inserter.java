package zobonapp.core.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import zobonapp.core.domain.Category;
import zobonapp.core.domain.Contact;
import zobonapp.core.domain.Item;
import zobonapp.core.domain.Status;
import zobonapp.core.service.CategoryService;
import zobonapp.core.service.ZobonAppService;

public class Inserter
{
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ZobonAppService itemService;
	public void insertItem(Map<String,?> map)
	{
		
		Category hotlineCategory=categoryService.findByEnNameAndType("Phone",4001);
		Category addressCategory=categoryService.findByEnNameAndType("Address",4001);
		ArrayList<Map<String,?>> items=(ArrayList<Map<String,?>>)map.get("items");
		for(Map<String,?> anItem:items)
		{
			String arName=anItem.get("arName").toString();
			String enName=anItem.get("enName").toString();
			Item item=itemService.findByEnName(enName);
			if(item==null)
				item=itemService.findByArName(arName);
			if(item==null)
			{
				item=new Item();
				item.setArName(arName);
				item.setEnName(enName);
			}

			Contact mainContact=null;
			Contact firstContact=null;
//			contact.setStatus(Status.PUBLISHED);
//			contact.setArName("الخط الساخن");
//			contact.setEnName("Hotline");
//			contact.setUri("tel:"+anItem.get("hotlone"));
//			contact.setCategory(hotlineCategory);
//			contact.setItem(item);
//			item.getContacts().add(contact);
			
			ArrayList<Map<String,?>> contacts=(ArrayList<Map<String,?>>)anItem.get("contacts");
			for(Map<String,?> aContact:contacts)
			{
				Contact contact=new Contact();
				if(firstContact==null)
					firstContact=contact;
				contact.setStatus(Status.PUBLISHED);
				contact.setArName(aContact.get("arName").toString());
				contact.setEnName(aContact.get("enName").toString());
				contact.setProfileUri(item.getEnName()+"/"+aContact.get("profileId"));
				contact.setItem(item);
				if(mainContact==null&&(contact.getEnName().toLowerCase().contains("customer service")||contact.getEnName().toLowerCase().contains("home delivery")))
				{
					mainContact=contact;
					mainContact.setUri("tel:"+anItem.get("hotlone"));
					mainContact.setCategory(hotlineCategory);
					item.getContacts().add(0, mainContact);
				}
				else
				{
					contact.setUri("geo:NA");
					contact.setCategory(addressCategory);
					item.getContacts().add(contact);
				}
				
				
					
			}
			if(mainContact==null&&contacts.size()>0)
			{
				if(contacts.size()==1)
				{
					firstContact.setUri("tel:"+anItem.get("hotlone"));
					firstContact.setCategory(hotlineCategory);
				}
				else
				{
					mainContact=new Contact();
					mainContact.setStatus(Status.PUBLISHED);
					mainContact.setArName("الخط الساخن");
					mainContact.setEnName("Hotline");
					mainContact.setUri("tel:"+anItem.get("hotlone"));
					mainContact.setCategory(hotlineCategory);
					mainContact.setItem(item);
					item.getContacts().add(0,mainContact);
				}
			}
			item.setStatus(Status.PUBLISHED);
			ArrayList<String> categories=(ArrayList<String>)anItem.get("enCategories");
			itemService.save(item,categories);
//			itemService.save(item);
//			System.out.println(item);
//			System.out.println("=======");
		}
		
	}
	public void insertItemWithIssues(Map<String,?> map)
	{
		
		Category hotlineCategory=categoryService.findByEnNameAndType("Phone",4001);
		Category addressCategory=categoryService.findByEnNameAndType("Address",4001);
		ArrayList<Map<String,?>> items=(ArrayList<Map<String,?>>)map.get("items");
		for(Map<String,?> anItem:items)
		{
			String arName=anItem.get("arName").toString();
			String enName=anItem.get("enName").toString();
			Item item=itemService.findByEnName(enName);
			if(item==null)
				item=itemService.findByArName(arName);
			if(item==null)
			{
				item=new Item();
				
			}
			item.setArName(arName);
			item.setEnName(enName);

			Contact mainContact=null;
			Contact firstContact=null;
//			contact.setStatus(Status.PUBLISHED);
//			contact.setArName("الخط الساخن");
//			contact.setEnName("Hotline");
//			contact.setUri("tel:"+anItem.get("hotlone"));
//			contact.setCategory(hotlineCategory);
//			contact.setItem(item);
//			item.getContacts().add(contact);
			
			ArrayList<Map<String,?>> contacts=(ArrayList<Map<String,?>>)anItem.get("contacts");
			for(Map<String,?> aContact:contacts)
			{
				Contact contact=new Contact();
				if(firstContact==null)
					firstContact=contact;
				contact.setStatus(Status.PUBLISHED);
				contact.setArName(aContact.get("arName").toString());
				contact.setEnName(aContact.get("enName").toString());
				contact.setProfileUri(item.getEnName()+"/"+aContact.get("profileId"));
				contact.setItem(item);
				if(mainContact==null&&(contact.getEnName().toLowerCase().contains("customer service")||contact.getEnName().toLowerCase().contains("home delivery")))
				{
					mainContact=contact;
					mainContact.setUri("tel:"+anItem.get("hotlone"));
					mainContact.setCategory(hotlineCategory);
					item.getContacts().add(0, mainContact);
				}
				
					
			}
			if(mainContact==null&&contacts.size()>0)
			{
				if(contacts.size()==1)
				{
					firstContact.setUri("tel:"+anItem.get("hotlone"));
					firstContact.setCategory(hotlineCategory);
					item.getContacts().add(0,firstContact);
				}
				else
				{
					mainContact=new Contact();
					mainContact.setStatus(Status.PUBLISHED);
					mainContact.setArName("الخط الساخن");
					mainContact.setEnName("Hotline");
					mainContact.setUri("tel:"+anItem.get("hotlone"));
					mainContact.setCategory(hotlineCategory);
					mainContact.setItem(item);
					item.getContacts().add(0,mainContact);
				}
			}
			item.setStatus(Status.PUBLISHED);
			ArrayList<String> categories=(ArrayList<String>)anItem.get("enCategories");
			itemService.save(item,categories);
//			itemService.save(item);
//			System.out.println(item);
//			System.out.println("=======");
		}
		
	}
	public void insertGroups(List<String> record)
	{
		System.out.println(record);
		Category category=new Category();
		category.setArName(record.get(1));
		category.setEnName(record.get(2));
		category.setArDesc(record.get(3));
		category.setEnDesc(record.get(4));
		category.setKeywords(record.get(5));
		category.setRank(Integer.parseInt(record.get(6)));
		category.setStatus(Status.valueOf(record.get(7)));
		category.setType(Integer.parseInt(record.get(8)));
		System.out.println(category);
		try
		{
			categoryService.save(category);
		} catch (DataAccessException dae)
		{
			Category updatedCategory=categoryService.findByEnNameAndType(category.getEnName(),category.getType());
			if(updatedCategory==null)
				updatedCategory=categoryService.findByArNameAndType(category.getArName(),category.getType());
			if(updatedCategory!=null)
			{
				category.setId(updatedCategory.getId());
				categoryService.save(category);
			}
			else 
				throw dae;
		}
		
	}
//	public Exchange renameLogoFile(Exchange exchange)
//	{
//		return exchange;
//	}
	public void renameLogoFile(Exchange exchange)
	{
		String fileNameParent=exchange.getIn().getHeader("CamelFileParent").toString();
		String hotline=exchange.getIn().getHeader("CamelFileNameOnly").toString().replaceAll("\\.\\w+", "");
		String resolution=fileNameParent.substring(fileNameParent.lastIndexOf("\\")+1);
		List<String> recipients=new Vector<>();
		for(UUID id:itemService.findItemsIdsforHotline(hotline))
		{
			String uri=String.format("file://c://zadata/resources/%s/?fileName=%s.webp",resolution, id);
			recipients.add(uri);
			itemService.updateItemRank(id, 1);
		} 
		exchange.getIn().getHeaders().put("za.recipients", recipients);
	}
	
	public void renameCatLogoFile(Exchange exchange)
	{
		String fileNameParent=exchange.getIn().getHeader("CamelFileParent").toString();
		String enGroupName=exchange.getIn().getHeader("CamelFileNameOnly").toString().replaceAll("\\.\\w+", "");
		String resolution=fileNameParent.substring(fileNameParent.lastIndexOf("\\")+1);
		List<String> recipients=new Vector<>(); 
		for(Category category:categoryService.findCategoryByEnName(enGroupName))
		{
			String uri=String.format("file://c://zadata/resources/%s/?fileName=%s.webp",resolution, category.getId());
			recipients.add(uri);
//			itemService.updateItemRank(id, 1);
		} 
		exchange.getIn().getHeaders().put("za.recipients", recipients);
	}


}
