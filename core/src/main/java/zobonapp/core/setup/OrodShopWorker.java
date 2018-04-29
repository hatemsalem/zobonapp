package zobonapp.core.setup;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class OrodShopWorker extends OffersScraper
{

	private static final String SRC = OrodShopWorker.class.getSimpleName();

	public OrodShopWorker(HashMap<String, String> configuration)
	{
		super(configuration);
	}
	
	

	public void run()
	{
		SimpleDateFormat sdf1=new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(getOffersUrl());
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

		Gson gson = new Gson();
		JsonObject dataRoot= gson.fromJson(content, JsonObject.class);
		JsonArray offersArray= dataRoot.getAsJsonArray("data");
		System.out.println("No. of offers:"+offersArray.size());
		System.out.println("Offers");
		for(int i=0;i<offersArray.size();i++)
		{
			JsonObject offerObject=offersArray.get(i).getAsJsonObject();
			String srcOfferId=offerObject.get("id").getAsString();
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
							plusDays(getDefaultDuration(companyId)).
							atStartOfDay(ZoneId.systemDefault()).toInstant());
				}
			}
			
			
			String enName=arName;
			List<String> pages=new Vector<>();
			JsonArray imagesArray=offerObject.getAsJsonArray("images");
			for(int j=0;j<imagesArray.size();j++)
			{
				pages.add(imagesArray.get(j).getAsJsonObject().get("path").getAsString());
			}
			System.out.println("Id:"+srcOfferId+", For :"+companyId);
			if(pages.size()>0)
				syncOffer(SRC, srcOfferId, companyId,arName, enName, pages,pages.get(0), startDate, endDate);
			
		}

	}

	
	

}
