package zobonapp.core.setup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.Vector;

import org.apache.camel.Exchange;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

import com.fasterxml.jackson.core.io.JsonStringEncoder;

import zobonapp.core.domain.Category;
import zobonapp.core.domain.Contact;
import zobonapp.core.domain.Offer;
import zobonapp.core.domain.BusinessEntity;
import zobonapp.core.domain.Status;
import zobonapp.core.repository.OfferRepository;
import zobonapp.core.service.CategoryService;
import zobonapp.core.service.ZobonAppService;

public class Inserter
{
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ZobonAppService zobonService;
	
	@Autowired
	private OfferRepository offerRepository;
	public void generateJson(Category category)
	{
		String fileName=String.format("C:\\zadata\\work\\categories\\json\\%s-%d.json", category.getEnName(),category.getType());
		File file=new File(fileName);
		if(!file.getParentFile().exists())
		{
			if(!file.getParentFile().mkdirs())
			{
				System.out.println("Can't create the category file for category:"+category.getEnName());
				return;
			}
				
		}
		
		JsonStringEncoder jsonEncoder=JsonStringEncoder.getInstance();
		StringJoiner offerFields = new StringJoiner(",\n", "{", "}");
		offerFields.add(String.format("\"id\":\"%s\"", category.getId()));
		offerFields.add(String.format("\"enName\":\"%s\"", new String(jsonEncoder.quoteAsString(category.getEnName()))));
		offerFields.add(String.format("\"arName\":\"%s\"", new String(jsonEncoder.quoteAsString(category.getArName()))));
		offerFields.add(String.format("\"arDesc\":\"%s\"", new String(jsonEncoder.quoteAsString(category.getArDesc()))));
		offerFields.add(String.format("\"enDesc\":\"%s\"", new String(jsonEncoder.quoteAsString(category.getEnDesc()))));
		offerFields.add(String.format("\"type\":%d", category.getType()));
		offerFields.add(String.format("\"rank\":%d", category.getRank()));
		try
		{
			FileWriter output = new FileWriter(file);
			output.write(offerFields.toString());
			output.flush();
			output.close();
		} catch (IOException e)
		{
			System.out.println("Issue while writing json file:"+e.getMessage());
		}
		
	}
	public void generateJson(BusinessEntity entity)
	{
		String fileName=String.format("C:\\zadata\\work\\entities\\json\\%s.json", entity.getEnName());
		File file=new File(fileName);
		if(!file.getParentFile().exists())
		{
			if(!file.getParentFile().mkdirs())
			{
				System.out.println("Can't create the Entity file for category:"+entity.getEnName());
				return;
			}
				
		}
		
		JsonStringEncoder jsonEncoder=JsonStringEncoder.getInstance();
		StringJoiner offerFields = new StringJoiner(",\n", "{", "}");
		offerFields.add(String.format("\"id\":\"%s\"", entity.getId()));
		offerFields.add(String.format("\"enName\":\"%s\"", new String(jsonEncoder.quoteAsString(entity.getEnName()))));
		offerFields.add(String.format("\"arName\":\"%s\"", new String(jsonEncoder.quoteAsString(entity.getArName()))));
		offerFields.add(String.format("\"rank\":%d", entity.getRank()));
		offerFields.add(String.format("\"web\":\"%s\"", entity.getWeb()));
		offerFields.add(String.format("\"fb\":\"%s\"", entity.getFacebook()));
		try
		{
			FileWriter output = new FileWriter(file);
			output.write(offerFields.toString());
			output.flush();
			output.close();
		} catch (IOException e)
		{
			System.out.println("Issue while writing json file:"+e.getMessage());
		}
		
	}
	public void publishCategory(Map<String,?> map)
	{
		Category category=categoryService.findOne(UUID.fromString(map.get("id").toString()));
		if(category==null)
		{
			System.out.println("Category Not found to be published");
			return;
		}
		category.setArName(map.get("arName").toString());
		category.setEnName(map.get("enName").toString());
		category.setType(Integer.parseInt(map.get("type").toString()));
		category.setRank(Integer.parseInt(map.get("rank").toString()));
		if(category.getStatus()==Status.PUBLISHED)
			categoryService.save(category);
		else
			zobonService.publishCategory(category);
		System.out.println(map);
	}
	public void unpublishCategory(Map<String,?> map)
	{
		Category category=categoryService.findOne(UUID.fromString(map.get("id").toString()));
		if(category==null)
		{
			System.out.println("Category Not found to be published");
			return;
		}
		
		category.setStatus(Status.REVIEWED);
		categoryService.save(category);
	}
	public void publishEntity(Map<String,?> map)
	{
		BusinessEntity entity=zobonService.find(UUID.fromString(map.get("id").toString()));
		if(entity==null)
		{
			System.out.println("Category Not found to be published");
			return;
		}
		entity.setArName(map.get("arName").toString());
		entity.setEnName(map.get("enName").toString());
		entity.setWeb(map.get("web").toString());
		entity.setFacebook(map.get("fb").toString());
		entity.setRank(Integer.parseInt(map.get("rank").toString()));
		if(entity.getStatus()==Status.PUBLISHED)
			zobonService.save(entity);
		else
		{
			zobonService.publishEntity(entity);
		}
		
		System.out.println(map);
	}
	public void unpublishEntity(Map<String,?> map)
	{
		BusinessEntity entity=zobonService.find(UUID.fromString(map.get("id").toString()));
		if(entity==null)
		{
			System.out.println("Entity Not found to be published");
			return;
		}
		
		entity.setStatus(Status.REVIEWED);
		zobonService.save(entity);
	}
	public void insertItem(Map<String,?> map)
	{
		
		Category hotlineCategory=categoryService.findByEnNameAndType("Phone",4001);
		Category addressCategory=categoryService.findByEnNameAndType("Address",4001);
		ArrayList<Map<String,?>> items=(ArrayList<Map<String,?>>)map.get("items");
		for(Map<String,?> anItem:items)
		{
			String arName=anItem.get("arName").toString();
			String enName=anItem.get("enName").toString();
			String web= anItem.get("web").toString();
			String facebook=anItem.get("facebook").toString();
			try
			{
				web=URLDecoder.decode(web, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				facebook=URLDecoder.decode(facebook, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BusinessEntity item=zobonService.findByEnName(enName);
			if(item==null)
				item=zobonService.findByArName(arName);
			if(item==null)
			{
				item=new BusinessEntity();
				item.setArName(arName);
				item.setEnName(enName);
				item.setWeb(web);
				item.setFacebook(facebook);
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
				contact.setEntity(item);
				if(mainContact==null&&(contact.getEnName().toLowerCase().contains("customer service")||contact.getEnName().toLowerCase().contains("home delivery")||contact.getEnName().toLowerCase().contains("hotline")))
				{
					mainContact=contact;
					mainContact.setUri("tel:"+anItem.get("hotline"));
					mainContact.setCategory(hotlineCategory);
					item.getContacts().add(0, mainContact);
				}
				else
				{
					String lat=aContact.get("lat").toString();
					String lng=aContact.get("lng").toString();
					if(TextUtils.isEmpty(lat)||TextUtils.isEmpty(lng))
					{
						contact.setUri("geo:");
					}
					else
					{
						contact.setUri("geo:"+lat+","+lng);
					}
							
					contact.setCategory(addressCategory);
					item.getContacts().add(contact);
				}
				
				
					
			}
			if(mainContact==null&&contacts.size()>0)
			{
				if(contacts.size()==1)
				{
					if(firstContact.getUri().startsWith("geo:")&&firstContact.getUri().length()>6)
					{
						Contact mapContact=new Contact();
						mapContact.setStatus(Status.PUBLISHED);
						mapContact.setArName(firstContact.getArName());
						mapContact.setEnName(firstContact.getEnName());
						
						mapContact.setUri(firstContact.getUri());
						mapContact.setCategory(firstContact.getCategory());
						mapContact.setEntity(firstContact.getEntity());
						item.getContacts().add(mapContact);
						firstContact.setEnName("Customer Service");
						firstContact.setArName("خدمة العملاء");
						
					}
					firstContact.setUri("tel:"+anItem.get("hotline"));
					firstContact.setCategory(hotlineCategory);
				}
				else
				{
					mainContact=new Contact();
					mainContact.setStatus(Status.PUBLISHED);
					mainContact.setArName("الخط الساخن");
					mainContact.setEnName("Hotline");
					mainContact.setUri("tel:"+anItem.get("hotline"));
					mainContact.setCategory(hotlineCategory);
					mainContact.setEntity(item);
					item.getContacts().add(0,mainContact);
				}
			}
			item.setStatus(Status.PUBLISHED);
			ArrayList<String> categories=(ArrayList<String>)anItem.get("enCategories");
			generateJson(zobonService.save(item,categories));
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
			String web=anItem.get("web").toString();
			String facebook=anItem.get("facebook").toString();
			BusinessEntity item=zobonService.findByEnName(enName);
			if(item==null)
				item=zobonService.findByArName(arName);
			if(item==null)
			{
				item=new BusinessEntity();
				
			}
			try
			{
				web=URLDecoder.decode(web, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				facebook=URLDecoder.decode(facebook, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			item.setArName(arName);
			item.setEnName(enName);
			item.setWeb(web);
			item.setFacebook(facebook);

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
				contact.setEntity(item);
				if(mainContact==null&&(contact.getEnName().toLowerCase().contains("customer service")||contact.getEnName().toLowerCase().contains("home delivery")))
				{
					mainContact=contact;
					mainContact.setUri("tel:"+anItem.get("hotline"));
					mainContact.setCategory(hotlineCategory);
					item.getContacts().add(0, mainContact);
				}
				
					
			}
			if(mainContact==null&&contacts.size()>0)
			{
				if(contacts.size()==1)
				{
					firstContact.setUri("tel:"+anItem.get("hotline"));
					firstContact.setCategory(hotlineCategory);
					item.getContacts().add(0,firstContact);
				}
				else
				{
					mainContact=new Contact();
					mainContact.setStatus(Status.PUBLISHED);
					mainContact.setArName("الخط الساخن");
					mainContact.setEnName("Hotline");
					mainContact.setUri("tel:"+anItem.get("hotline"));
					mainContact.setCategory(hotlineCategory);
					mainContact.setEntity(item);
					item.getContacts().add(0,mainContact);
				}
			}
			item.setStatus(Status.PUBLISHED);
			ArrayList<String> categories=(ArrayList<String>)anItem.get("enCategories");
			generateJson(zobonService.save(item,categories));
//			itemService.save(item);
//			System.out.println(item);
//			System.out.println("=======");
		}
		
	}
	public void publishOffer(Map<String,?> map)
	{
		Offer offer=offerRepository.findOne(UUID.fromString(map.get("id").toString()));
		if(offer==null)
		{
			System.out.println("Offer Not found to be published");
			return;
		}
		offer.setArName(map.get("arName").toString());
		offer.setEnName(map.get("enName").toString());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			Date date=sdf.parse(map.get("startDate").toString());
			offer.setStartDate(date);
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			Date date=sdf.parse(map.get("endDate").toString());
			offer.setEndDate(date);
		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date today=Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
		if(offer.getEndDate().before(today))
		{
			offer.setStatus(Status.REVIEWED);
		}
		else
		{
			offer.setStatus(Status.PUBLISHED);
		}
		offer.setRank(Integer.parseInt(map.get("rank").toString()));
		offerRepository.save(offer);
		System.out.println(map);
	}
	public void unpublishOffer(Map<String,?> map)
	{
		Offer offer=offerRepository.findOne(UUID.fromString(map.get("id").toString()));
		if(offer==null)
		{
			System.out.println("Offer Not found to be unpublished");
			return;
		}
		
		offer.setStatus(Status.REVIEWED);
		offerRepository.save(offer);
		System.out.println(map);
	}
	public void insertCategories(List<String> record)
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
			generateJson(categoryService.save(category));
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
		for(UUID id:zobonService.findItemsIdsforHotline(hotline))
		{
			String uri=String.format("file://c://zadata/work/resources/%s/?fileName=%s.webp",resolution, id);
			recipients.add(uri);
//			zobonService.updateItemRank(id, 1);
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
			String uri=String.format("file://c://zadata/work/resources/%s/?fileName=%s.webp",resolution, category.getId());
			recipients.add(uri);
//			itemService.updateItemRank(id, 1);
		} 
		exchange.getIn().getHeaders().put("za.recipients", recipients);
	}
	public void retrofit()
	{
		System.out.println("Retrofit offers:"+zobonService.offerRetrofit());
	}
}
