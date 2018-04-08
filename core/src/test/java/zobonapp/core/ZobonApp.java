package zobonapp.core;

import org.apache.camel.spring.Main;
import org.springframework.context.support.GenericXmlApplicationContext;

import zobonapp.core.service.ZobonAppService;
import zobonapp.core.service.impl.ZobonAppServiceImpl;
import zobonapp.core.setup.YabalashWorker;
public class ZobonApp
{

	public static void main(String[] args) throws Exception
	{
//		EntityManager em= JPASessionUtil.getEntityManager("zobonapp");
//		em.close();

//		GenericXmlApplicationContext ctx=new GenericXmlApplicationContext();
//		ctx.load("classpath:offers-workers.xml");
//		ctx.refresh();
//		YabalashWorker worker=ctx.getBean(YabalashWorker.class);
//		worker.run();
		
		
		Main main=new Main();
		main.setFileApplicationContextUri("classpath:camel.xml");
		main.run();


//		service.test();
//		=ctx.getBean("itemService",Zobon)
//		Main main=new Main();
//		main.setFileApplicationContextUri("classpath:camel.xml");
//		main.run();
	
//		ZobonApp service=ctx.getBean("itemService",ZobonAppService.class);
//		Category category=new Category();
//		category.setEnName("Restaurants")
//			.setArName("المطاعم")
//			.setRank(0)
//			.setStatus(Status.ENABLED)
//			.setType(0);
//		category=service.save(category);
//		
//
//		category.setId(null);
//		category.setArName("عربي");
//		category.setEnName("English");
//		service.save(category);
//		for(Category cat:service.findAllCategories())
//		{
////			service.update(cat.getId(), cat.getRank()+1);
//			cat.setRank(new Random().nextInt(10));
//			service.save(cat);
//		}
	}

}
