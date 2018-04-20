package zobonapp.core.setup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import zobonapp.core.domain.BusinessEntity;
import zobonapp.core.domain.Offer;
import zobonapp.core.domain.Status;
import zobonapp.core.repository.OfferRepository;
import zobonapp.core.service.ZobonAppService;

public class YabalashWorker extends OffersScraper
{

	

	private static final String SRC = YabalashWorker.class.getSimpleName();
	
	public YabalashWorker(HashMap<String, String> configuration)
	{
		super(configuration);
	}
	
//	private void syncOffer(String src,String srcId, String arName,String enName, int pages,Date startDate,Date endDate,BusinessEntity item)
//	{
//		Offer offer = offerRepository.findBySrcAndSrcId(src, srcId);
//		if (offer == null)
//		{
//			offer = new Offer();
//			offer.setArName(arName);
//			offer.setEnName(enName);
//			offer.setSrc(src);
//			offer.setSrcId(srcId);
//			offer.setPages(-1);
//			offer.setStatus(Status.DRAFT);
//			offer.setEntity(item);
//			offer.setStartDate(startDate);
//			offer.setEndDate(endDate);
//			offer=offerRepository.save(offer);
//			
//			File file = new File(String.format("%s\\%s", assetsPath,offer.getId()));
//			file.mkdirs();
//		}
//		if(offer.getPages()<0)
//		{
//			String srcPath=String.format("http://mobappsbaker.com/catalogs/thwr/thumbs/%s.jpg", srcId);
//			String destPath=String.format("%s\\%s\\thumbnail.jpg", assetsPath,offer.getId());
//			if(download(srcPath, destPath))
//			{
//				offer.setPages(0);
//				
//			}
//		}
//		if (offer.getStatus() == Status.DRAFT&&offer.getPages()>=0)
//		{
//			for (int i = offer.getPages(); i < pages; i++)
//			{
//				
//				String srcPath=String.format("http://mobappsbaker.com/catalogs/thwr/offers/%s/%d.jpg", srcId,i+1);
//				String destPath=String.format("%s\\%s\\%03d.jpg", assetsPath,offer.getId(),i+1);
//				if(download(srcPath, destPath))
//				{
//					offer.setPages(i+1);
//					
//				}
//				else
//					break;
//			}
//		}
//		if (offer.getStatus()!=Status.PUBLISHED&&pages == offer.getPages())
//		{
//			offer.setStatus(Status.PUBLISHED);
//		}
//		offerRepository.save(offer);
//	}

	public void run()
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(getOffersUrl());
		String content = null;
		try
		{
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
			// Read the contents of an entity and return it as a String.
			// System.out.println(content);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (content != null && content.charAt(1) == '#')
		{
			content = content.substring(2);
			// System.out.println(content);
			Pattern recordPattern = Pattern.compile("([^#]*#){8}");
			Matcher matcher = recordPattern.matcher(content);
			while (matcher.find())
			{
				String record = content.substring(matcher.start(), matcher.end());
				System.out.println("Record:" + record);
				String fields[] = record.split("#");
				String srcOfferId = fields[0];
				String arName = fields[5];
				String companyId=fields[1];
				List<String> pages=new Vector<>();
				for(int i=0;i<Integer.valueOf(fields[3]);i++)
				{
					pages.add(String.format("offers/%s/%d.jpg",srcOfferId,i+1));
				}
				String entityEnName=getEntityEnName(companyId);
				LocalDate startDate=LocalDate.now();
				try
				{
						LocalDate.parse(fields[6], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				}
				catch(DateTimeParseException dtpe)
				{
					//TODO: no thing
				}
				LocalDate endDate=startDate.plusDays(Integer.valueOf(getDefaultDuration(companyId)));
				String thumUri=String.format("thumbs/%s.jpg", srcOfferId);
				syncOffer(SRC, srcOfferId, companyId, arName, arName, pages,thumUri,
						Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()), 
						Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
				
				
			}
		}

	}

	
	
}
