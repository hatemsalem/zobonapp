package zobonapp.core.setup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import zobonapp.core.domain.BusinessEntity;
import zobonapp.core.domain.Offer;
import zobonapp.core.domain.Status;
import zobonapp.core.repository.OfferRepository;
import zobonapp.core.service.ZobonAppService;

public class OrodShopWorker
{
	private HashMap<String, String> entitiesMapper;

	private static final String SRC = OrodShopWorker.class.getSimpleName();
	
	private String assetsPath="C:\\zadata\\resources\\offers";

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private ZobonAppService zobonAppService;
	
	public HashMap<String, String> getEntitiesMapper()
	{
		return entitiesMapper;
	}

	public void setEntitiesMapper(HashMap<String, String> entitiesMapper)
	{
		this.entitiesMapper = entitiesMapper;
	}
	
	private void syncOffer(String src,String srcId, String arName,String enName, Vector<String> pages,Date startDate,Date endDate,BusinessEntity item,String categories)
	{
		Offer offer = offerRepository.findBySrcAndSrcId(src, srcId);
		
		if (offer == null)
		{
			offer = new Offer();
			offer.setArName(arName);
			offer.setEnName(enName);
			offer.setSrc(src);
			offer.setSrcId(srcId);
			offer.setPages(-1);
			offer.setStatus(Status.DRAFT);
			offer.setEntity(item);
			offer.setStartDate(startDate);
			offer.setEndDate(endDate);
			
			offer=zobonAppService.save(offer, Arrays.asList(categories.split(",")));
			
			File file = new File(String.format("%s\\%s", assetsPath,offer.getId()));
			file.mkdirs();
		}
		if(offer.getPages()<0&&pages.size()>0)
		{
			String destPath=String.format("%s\\%s\\thumbnail.jpg", assetsPath,offer.getId());
			if(download(pages.get(0), destPath))
			{
				offer.setPages(0);
				
			}
		}
		if (offer.getStatus() == Status.DRAFT&&offer.getPages()>=0)
		{
			for (int i = offer.getPages(); i < pages.size(); i++)
			{
				
				String destPath=String.format("%s\\%s\\%03d.jpg", assetsPath,offer.getId(),i+1);
				if(download(pages.get(i), destPath))
				{
					offer.setPages(i+1);
					
				}
				else
					break;
			}
		}
		if (offer.getStatus()!=Status.PUBLISHED&&pages.size() == offer.getPages())
		{
			offer.setStatus(Status.PUBLISHED);
		}
		zobonAppService.save(offer);
	}

	public void run()
	{
		String offersUrl = "http://3orodshop.com/Apps/OrodShop/OrodShopEgypt/index.php/rest/items/search/city_id/0";
		SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(offersUrl);
		String content = null;
		try
		{
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
			// Read the contents of an entity and return it as a String.
			// System.out.println(content);
			int responseCode = response.getStatusLine().getStatusCode();
			if(responseCode!=200)
				throw new IOException("Invalid resposne code");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Content:"+content);
		Gson gson = new Gson();
		JsonObject dataRoot= gson.fromJson(content, JsonObject.class);
		JsonArray offersArray= dataRoot.getAsJsonArray("data");
		System.out.println("No. of offers:"+offersArray.size());
		System.out.println("Offers");
		for(int i=0;i<offersArray.size();i++)
		{
			JsonObject offerObject=offersArray.get(i).getAsJsonObject();
			String srcId=offerObject.get("id").getAsString();
			String companyId=offerObject.get("city_id").getAsString();
			companyId=companyId+offerObject.get("cat_id").getAsString();
			companyId=companyId+offerObject.get("sub_cat_id").getAsString();
			String arName=offerObject.get("name").getAsString();
			Date startDate=new Date();
			Date endDate=null;
			try
			{
				startDate=sdf2.parse(offerObject.get("added").getAsString().substring(0, 10));
				endDate=sdf1.parse(offerObject.get("phone").getAsString());
				
			} catch (ParseException e)
			{
				if(endDate==null)
				{
					endDate=Date.from(
							LocalDate.from(startDate.toInstant().atZone(ZoneId.systemDefault())).
							plusDays(Integer.valueOf(entitiesMapper.get(companyId+".duration"))).
							atStartOfDay(ZoneId.systemDefault()).toInstant());
				}
			}
//			LocalDate startDate=LocalDate.parse(fields[6], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//			LocalDate endDate=startDate.plusDays(Integer.valueOf(entitiesMapper.get(companyId+".duration")));					
			
			
			String enName=arName;
			Vector<String> pages=new Vector();
			JsonArray imagesArray=offerObject.getAsJsonArray("images");
			for(int j=0;j<imagesArray.size();j++)
			{
				pages.add("http://3orodshop.com/Apps/OrodShop/OrodShopEgypt/uploads/"+imagesArray.get(j).getAsJsonObject().get("path").getAsString());
			}
			System.out.println("Id:"+srcId+", For :"+companyId);
			String entityEnName=entitiesMapper.get(companyId+".id");
			
			String categories=entitiesMapper.get(companyId+".categories");
			if(entityEnName!=null)
			{
				BusinessEntity item=zobonAppService.findByEnName(entityEnName);
				if(item!=null)
				{
					syncOffer(SRC, srcId, arName, enName, pages, startDate, endDate, item,categories);

				}
			}

			
		}
//		if (content != null && content.charAt(1) == '#')
//		{
//			content = content.substring(2);
//			// System.out.println(content);
//			Pattern recordPattern = Pattern.compile("([^#]*#){8}");
//			Matcher matcher = recordPattern.matcher(content);
//			while (matcher.find())
//			{
//				String record = content.substring(matcher.start(), matcher.end());
//				System.out.println("Record:" + record);
//				String fields[] = record.split("#");
//				String srcId = fields[0];
//				String arName = fields[5];
//				String companyId=fields[1];
//				int pages = Integer.valueOf(fields[3]);
//				String entityEnName=entitiesMapper.get(companyId+".id");
//				
//				
//				if(entityEnName!=null)
//				{
//					Item item=zobonAppService.findByEnName(entityEnName);
//					if(item!=null)
//					{
//						LocalDate startDate=LocalDate.parse(fields[6], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//						LocalDate endDate=startDate.plusDays(Integer.valueOf(entitiesMapper.get(companyId+".duration")));					
//						syncOffer(SRC, srcId, arName,arName, pages,
//								Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
//								Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),item);
//					}
//				}
//				
//				
//			}
//		}

	}

	
	public static boolean download(String src, String dest)
	{
		boolean result=false;
		try
		{
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(src);

			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			int responseCode = response.getStatusLine().getStatusCode();

			System.out.println("Request Url: " + request.getURI());
			System.out.println("Response Code: " + responseCode);

			InputStream is = entity.getContent();

			FileOutputStream fos = new FileOutputStream(new File(dest));

			int inByte;
			while ((inByte = is.read()) != -1)
			{
				fos.write(inByte);
			}

			is.close();
			fos.close();

			client.close();
			System.out.println("File Download Completed!!!");
			return true;
		} catch (ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (UnsupportedOperationException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return result;
	}

}
