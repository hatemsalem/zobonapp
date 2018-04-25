package zobonapp.core.setup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;

import zobonapp.core.domain.BusinessEntity;
import zobonapp.core.domain.Offer;
import zobonapp.core.domain.Status;
import zobonapp.core.repository.OfferRepository;
import zobonapp.core.service.ZobonAppService;

public abstract class OffersScraper
{
	private HashMap<String, String> configuration;
	private String offersUrl;
	private String sourceAssestsBaseUrl;
	private String jsonFolder;
	private String destAssetsPath = "C:\\zadata\\work\\resources\\offers";
	private String jsonPath="c:\\zadata\\work\\Offers\\";
	private int defaultDuration=14;
	private Status targetStatus=Status.REVIEWED;
	private boolean generateJSON=true;
	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private ZobonAppService zobonAppService;

	public OffersScraper(HashMap<String, String> configuration)
	{
		this.configuration=configuration;
		offersUrl=configuration.get("offers.url");
		sourceAssestsBaseUrl=configuration.get("assets.baseUrl");
		switch(configuration.get("status")) 
		{
		case "P":
			targetStatus=Status.PUBLISHED;
			break;
		case "R":
			targetStatus=Status.REVIEWED;
			break;
		case "D":
			targetStatus=Status.DRAFT;
		}
		generateJSON=Boolean.parseBoolean(configuration.get("json"));
		jsonFolder=configuration.get("jsonFolder");
		
	}
	public String getOffersUrl()
	{
		return offersUrl;
	}
	public String getAssetsBaseUrl()
	{
		return sourceAssestsBaseUrl;
	}
	public String getEntityEnName(String id)
	{
		return configuration.get(id+".id");
	}
	public int getDefaultDuration(String id)
	{
		int duration=defaultDuration;
		try
		{
			duration=Integer.parseInt(configuration.get(id+".duration"));
		}
		catch(NumberFormatException nfe)
		{
			//TODO: Nothing, use the default value;
		}
		return duration;
	}
	public String getCategories(String id)
	{
		return configuration.get(id+".categories");
	}
	protected void syncOffer(String src, String srcOfferId, String companyId,String arName, String enName, List<String> pages, String thumbnailUrl, Date startDate, Date endDate)
	{
		String entityEnName=getEntityEnName(companyId);
		BusinessEntity item=null;
		if(entityEnName!=null)
		{
			item=zobonAppService.findByEnName(entityEnName);
			
		}
		if(item==null)
			return;
		String categories=getCategories(companyId);
		Offer offer = offerRepository.findBySrcAndSrcId(src, srcOfferId);

		if (offer == null)
		{
			offer = new Offer();
			offer.setArName(arName);
			offer.setEnName(enName);
			offer.setSrc(src);
			offer.setSrcId(srcOfferId);
			offer.setPages(-1);
			offer.setStatus(Status.DRAFT);
			offer.setEntity(item);
			offer.setStartDate(startDate);
			offer.setEndDate(endDate);

			offer = zobonAppService.save(offer, Arrays.asList(categories.split(",")));

			File file = new File(String.format("%s\\%s", destAssetsPath, offer.getId()));
			if(!file.exists())
				file.mkdirs();
		}
		if (offer.getPages() < 0 && pages.size() > 0)
		{
			String destPath = String.format("%s\\%s\\thumbnail.jpg", destAssetsPath, offer.getId());
			String srcPath=sourceAssestsBaseUrl;
			if(thumbnailUrl==null)
				srcPath+=pages.get(0);
			else
				srcPath+=thumbnailUrl;
			if (download(srcPath, destPath))
			{
				offer.setPages(0);
				offer=zobonAppService.save(offer);

			}
			
		}
		if (offer.getStatus() == Status.DRAFT && offer.getPages() >= 0)
		{
			for (int i = offer.getPages(); i < pages.size(); i++)
			{

				String destPath = String.format("%s\\%s\\%03d.jpg", destAssetsPath, offer.getId(), i + 1);
				if (download(sourceAssestsBaseUrl+pages.get(i), destPath))
				{
					offer.setPages(i + 1);
					offer=zobonAppService.save(offer);

				} else
				{
					System.out.println("Issue in the download of:"+destPath);
					break;
				}
					
			}
		}
		if (offer.getStatus() == Status.DRAFT && pages.size() == offer.getPages())
		{
			Date today=Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
			if(offer.getEndDate().before(today))
			{
				offer.setStatus(Status.REVIEWED);
			}
			else
			{
				offer.setStatus(targetStatus);
			}
			if(generateJSON)
			{
				generateJsonFile(offer,entityEnName);
			}
		}
		zobonAppService.save(offer);
		
		
	}
	public void generateJsonFile(Offer offer,String entityName)
	{
		String folder=jsonFolder;
		if(folder==null)
			folder=offer.getSrc();
			
		File jsonFile=new File(String.format("%s\\%s\\%s-%s.json", jsonPath,folder,entityName,offer.getSrcId()));
		if(!jsonFile.getParentFile().exists())
		{
			if(!jsonFile.getParentFile().mkdirs())
			{
				System.out.println("Can't create the destination path");
				return;
			}
		}
		JsonStringEncoder jsonEncoder=JsonStringEncoder.getInstance();
		StringJoiner offerFields = new StringJoiner(",\n", "{", "}");
		offerFields.add(String.format("\"id\":\"%s\"", offer.getId()));
		offerFields.add(String.format("\"enName\":\"%s\"", new String(jsonEncoder.quoteAsString(offer.getEnName()))));
		offerFields.add(String.format("\"arName\":\"%s\"", new String(jsonEncoder.quoteAsString(offer.getArName()))));
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		offerFields.add(String.format("\"startDate\":\"%s\"", df.format(offer.getStartDate())));
		offerFields.add(String.format("\"startDate\":\"%s\"", df.format(offer.getEndDate())));
		offerFields.add(String.format("\"pages\":%d", offer.getPages()));
			
		offerFields.add(String.format("\"assetsPath\":\"%s/%s\"", destAssetsPath.replaceAll("\\\\", "/"),offer.getId()));
		try
		{
			FileWriter output = new FileWriter(jsonFile);
			output.write(offerFields.toString());
			output.flush();
			output.close();
		} catch (IOException e)
		{
			System.out.println("Issue while writing json file:"+e.getMessage());
		}
			
	}
		
	public abstract void run();
	
	public static boolean download(String src, String dest)
	{
		boolean result = false;
		try
		{
			CloseableHttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(src);

			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			int responseCode = response.getStatusLine().getStatusCode();
			

			System.out.println("Request Url: " + request.getURI());
			System.out.println("Response Code: " + responseCode);

			if(responseCode!=200)
				return false;
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
