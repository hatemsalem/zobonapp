package zobonapp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import zobonapp.core.domain.Category;
import zobonapp.core.domain.Contact;
import zobonapp.core.domain.Item;
import zobonapp.core.domain.Status;
import zobonapp.core.service.CategoryService;
import zobonapp.core.service.ZononAppService;

public class Inserter
{
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ZononAppService itemService;
	public void insertItem(Map<String,?> map)
	{
		
		Category hotlineCategory=categoryService.findByEnName("Phone");
		Category addressCategory=categoryService.findByEnName("Address");
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
		categoryService.save(category);
	}
}
