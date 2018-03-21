package zobonapp.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import zobonapp.core.domain.AbstractEntity;
import zobonapp.core.domain.Category;
import zobonapp.core.domain.Contact;
import zobonapp.core.domain.Item;
import zobonapp.core.service.CategoryService;
import zobonapp.core.service.ZononAppService;

@Controller
@RequestMapping("/")
public class UpdateController
{
	
	private final static int  STEP_SIZE=1000;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ZononAppService zobonAppService;

	@RequestMapping(value = "/updates/ver-{version}/{lastUpdated}", method = RequestMethod.GET)
	public void getUpdateFiles(HttpServletResponse response, @PathVariable String version,@PathVariable long lastUpdated) throws IOException
	{
		response.setContentType("zip/application;charset=UTF-8");

		response.setHeader("Content-Disposition", String.format("attachment;filename=updates%d.zip",lastUpdated));
		
		ZipOutputStream zipOutputStream=new ZipOutputStream(response.getOutputStream());

		zipOutputStream.putNextEntry(new ZipEntry("datanew.json"));
		List<Contact> contacts=new Vector<>();
		zipOutputStream.write(getNewData(contacts,lastUpdated).getBytes("UTF-8"));
		
		
		int totalsteps=contacts.size()/STEP_SIZE+1;
		for(int i=0;i<totalsteps;i++)
		{
			zipOutputStream.putNextEntry(new ZipEntry("n_step"+i+".json"));
			int start=i*STEP_SIZE;
			int end=start+STEP_SIZE;
			end=end>contacts.size()?contacts.size():end;
			String contactRecords=populateContacts(contacts.subList(start, end));
			zipOutputStream.write(String.format("{\"contacts\":\n%s}", contactRecords).getBytes("UTF-8"));
		}
		
		if(lastUpdated>0)
		{

			zipOutputStream.putNextEntry(new ZipEntry("dataupdate.json"));
			contacts=new Vector<>();
			zipOutputStream.write(getUpdatedData(contacts,lastUpdated).getBytes("UTF-8"));
			
			
			totalsteps=contacts.size()/STEP_SIZE+1;
			for(int i=0;i<totalsteps;i++)
			{
				zipOutputStream.putNextEntry(new ZipEntry("u_step"+i+".json"));
				int start=i*STEP_SIZE;
				int end=start+STEP_SIZE;
				end=end>contacts.size()?contacts.size():end;
				String contactRecords=populateContacts(contacts.subList(start, end));
				zipOutputStream.write(String.format("{\"contacts\":\n%s}", contactRecords).getBytes("UTF-8"));
			}
			
			zipOutputStream.putNextEntry(new ZipEntry("datadelete.json"));
			contacts=new Vector<>();
			zipOutputStream.write(getDeletedData(lastUpdated).getBytes("UTF-8"));
		}

		
		zipOutputStream.flush();
		zipOutputStream.close();
		
	}
	
	@RequestMapping(value = "/updates/ver-{version}", method = RequestMethod.GET)
	public void initialFiles(HttpServletResponse response, @PathVariable String version) throws IOException
	{
		getUpdateFiles(response,version,-1);
		
	}
	private String getNewData(List<Contact> detailContacts,long lastUpdate)
	{
		Date timePoint=new Date(lastUpdate);
		StringJoiner sjFile = new StringJoiner(",\n", "{", "}");

		
		List<Contact> basicContacts=new Vector<>();
		String itemRecords=populateItems(zobonAppService.findNewItems(timePoint), detailContacts,  basicContacts);
		int steps=(detailContacts==null)?0:(detailContacts.size()/STEP_SIZE+1);
		sjFile.add(String.format("\"steps\":%d\n", steps));
		sjFile.add(String.format("\"latestUpdate\":%d\n", zobonAppService.latestUpdate().getTime()));
		sjFile.add(String.format("\"categories\":\n%s", populateCategories(categoryService.findNewCategories(timePoint))));
		sjFile.add(String.format("\"entities\":\n%s", itemRecords));
		sjFile.add(String.format("\"contacts\":\n%s", populateContacts(basicContacts)));

		return sjFile.toString();
	}
	private String getUpdatedData(List<Contact> detailContacts,long lastUpdate)
	{
		Date timePoint=new Date(lastUpdate);
		StringJoiner sjFile = new StringJoiner(",\n", "{", "}");

		
		List<Contact> basicContacts=new Vector<>();
		String itemRecords=populateItems(zobonAppService.findUpdatedItems(timePoint), detailContacts,  basicContacts);
		int steps=(detailContacts==null)?0:(detailContacts.size()/STEP_SIZE+1);
		sjFile.add(String.format("\"steps\":%d\n", steps));
		sjFile.add(String.format("\"latestUpdate\":%d\n", zobonAppService.latestUpdate().getTime()));
		sjFile.add(String.format("\"categories\":\n%s", populateCategories(categoryService.findUpdatedCategories(timePoint))));
		sjFile.add(String.format("\"entities\":\n%s", itemRecords));
		sjFile.add(String.format("\"contacts\":\n%s", populateContacts(basicContacts)));
		return sjFile.toString();
	}
	private String getDeletedData(long lastUpdate)
	{
		Date timePoint=new Date(lastUpdate);
		StringJoiner sjFile = new StringJoiner(",\n", "{", "}");

		
		sjFile.add(String.format("\"latestUpdate\":%d\n", zobonAppService.latestUpdate().getTime()));
		sjFile.add(String.format("\"deletedCategories\":\n%s", unpublishElements(categoryService.findUnpublishedCategories(timePoint))));
		sjFile.add(String.format("\"deletedEntities\":\n%s", unpublishElements(zobonAppService.findUnpublishedItems(timePoint))));
		return sjFile.toString();
	}
	private String populateContacts(Iterable<Contact> contacts)
	{
		StringJoiner contactRecords = new StringJoiner(",\n", "[", "]");
		for(Contact contact:contacts)
		{
			StringJoiner contactFields = new StringJoiner(",\n", "{", "}");
			contactFields.add(String.format("\"id\":\"%s\"", contact.getId()));
			contactFields.add(String.format("\"itemId\":\"%s\"", contact.getItem().getId()));
			contactFields.add(String.format("\"uri\":\"%s\"", contact.getUri()));
			contactFields.add(String.format("\"arName\":\"%s\"", contact.getArName()));
			contactFields.add(String.format("\"enName\":\"%s\"", contact.getEnName()));
			contactFields.add(String.format("\"category\":\"%s\"", contact.getCategory().getId()));
			contactRecords.add(contactFields.toString());
			
		}
		return contactRecords.toString();
	}
	private String unpublishElements(Iterable<? extends AbstractEntity> elements)
	{
		StringJoiner elementRecords = new StringJoiner(",\n", "[", "]");
		for(AbstractEntity element:elements)
		{
			elementRecords.add("\""+element.getId().toString()+"\"");
		}
		return elementRecords.toString();
	}
	private String populateItems(Iterable<Item> items, List<Contact> detailedContacts,  List<Contact> basicContacts)
	{
		System.out.println("No. Of items:"+items.spliterator().getExactSizeIfKnown());
		StringJoiner itemRecords = new StringJoiner(",\n", "[", "]");
		for (Item item : items)
		{

			if (item.getContacts().size() > 0)
			{
				StringJoiner itemFields = new StringJoiner(",\n", "{", "}");
				itemFields.add(String.format("\"id\":\"%s\"", item.getId()));
				itemFields.add(String.format("\"enName\":\"%s\"", item.getEnName()));
				itemFields.add(String.format("\"arName\":\"%s\"", item.getArName()));
				itemFields.add(String.format("\"contactId\":\"%s\"", item.getContacts().get(0).getId()));
				StringJoiner icRecord = new StringJoiner(",", "[", "]");
				for (Category category : item.getCategories())
				{
					icRecord.add(String.format("\"%s\"", category.getId()));

				}
				itemFields.add(String.format("\"categories\":%s", icRecord.toString()));
				itemRecords.add(itemFields.toString());

				if(basicContacts!=null)
				{
					basicContacts.add(item.getContacts().get(0));
				}
				
				if(detailedContacts!=null)
					detailedContacts.addAll(item.getContacts().subList(1, item.getContacts().size()));

				
			}

		}
		return itemRecords.toString();
	}

	private String populateCategories(Iterable<Category> categories)
	{
		StringJoiner categoryRecords = new StringJoiner(",\n", "[", "]");
		for (Category category : categories)
		{
			StringJoiner categoryFields = new StringJoiner(",\n", "{", "}");

			categoryFields.add(String.format("\"id\":\"%s\"", category.getId()));
			categoryFields.add(String.format("\"enName\":\"%s\"", category.getEnName()));
			categoryFields.add(String.format("\"arName\":\"%s\"", category.getArName()));
			categoryFields.add(String.format("\"type\":%d", category.getType()));
			categoryFields.add(String.format("\"keywords\":\"%s\"", category.getKeywords()));
			categoryFields.add(String.format("\"status\":\"%s\"", category.getStatus()));
			categoryRecords.add(categoryFields.toString());
		}
		return categoryRecords.toString();
	}
	

	
}
