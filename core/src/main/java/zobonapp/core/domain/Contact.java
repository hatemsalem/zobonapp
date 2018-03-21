package zobonapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Contact extends AbstractEntity<Contact>
{
	private String arName;
	private String enName;
	@ManyToOne(optional=false)
	private Item item;
	@NotNull
	private String uri;
	private int rank;
	
	private String profileUri;
	
	@ManyToOne(optional=false)
	private Category category;

	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private Status status;

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

	public Item getItem()
	{
		return item;
	}

	public void setItem(Item item)
	{
		this.item = item;
	}

	public String getUri()
	{
		return uri;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	
	public String getProfileUri()
	{
		return profileUri;
	}

	public void setProfileUri(String profileUri)
	{
		this.profileUri = profileUri;
	}

	public Category getCategory()
	{
		return category;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	@Override
	public String toString()
	{
		return "Contact [arName=" + arName + ", enName=" + enName + ", item=" + item.getId() + ", uri=" + uri + ", rank=" + rank + ", category=" + category
				+ ", status=" + status + "]";
	}
	

}
