package zobonapp.core.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(name="offer.categories",attributeNodes= {@NamedAttributeNode("categories")})
})

public class Offer extends AbstractEntity<Offer>
{
	@ManyToOne
	private BusinessEntity entity;
	
	private String arName;
	private String enName;
	private String keywords;
	@Temporal(TemporalType.DATE)
	private Date startDate;
	@Temporal(TemporalType.DATE)
	private Date endDate;
	private int pages;
	private int rank;
	private String src;
	private String srcId;
	@ManyToMany
	Set<Category> categories=new HashSet<>();
	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private Status status;
	public BusinessEntity getEntity()
	{
		return entity;
	}
	public void setEntity(BusinessEntity entity)
	{
		this.entity = entity;
	}
	public String getArName()
	{
		return arName;
	}
	public void setArName(String arName)
	{
		this.arName = arName;
	}
	public String getEnName()
	{
		return enName;
	}
	public void setEnName(String enName)
	{
		this.enName = enName;
	}
	public String getKeywords()
	{
		return keywords;
	}
	public void setKeywords(String keywords)
	{
		this.keywords = keywords;
	}
	public Date getStartDate()
	{
		return startDate;
	}
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}
	public Date getEndDate()
	{
		return endDate;
	}
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}
	public int getPages()
	{
		return pages;
	}
	public void setPages(int pages)
	{
		this.pages = pages;
	}
	public int getRank()
	{
		return rank;
	}
	public void setRank(int rank)
	{
		this.rank = rank;
	}
	public String getSrc()
	{
		return src;
	}
	public void setSrc(String src)
	{
		this.src = src;
	}
	public String getSrcId()
	{
		return srcId;
	}
	public void setSrcId(String srcId)
	{
		this.srcId = srcId;
	}
	public Set<Category> getCategories()
	{
		return categories;
	}
	public void setCategories(Set<Category> categories)
	{
		this.categories = categories;
	}
	public Status getStatus()
	{
		return status;
	}
	public void setStatus(Status status)
	{
		this.status = status;
	}

	

}
