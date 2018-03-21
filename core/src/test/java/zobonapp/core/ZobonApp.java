package zobonapp.core;

import org.apache.camel.spring.Main;

public class ZobonApp
{

	public static void main(String[] args) throws Exception
	{
//		EntityManager em= JPASessionUtil.getEntityManager("zobonapp");
//		em.close();

//		GenericXmlApplicationContext ctx=new GenericXmlApplicationContext();
//		ctx.load("classpath:camel.xml");
		Main main=new Main();
		main.setFileApplicationContextUri("classpath:camel.xml");
//		ctx.refresh();
		main.run();
//		CategoryService service=ctx.getBean("categoryService",CategoryService.class);
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
