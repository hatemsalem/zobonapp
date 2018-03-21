package zobonapp.core.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(name="item.categories",attributeNodes= {@NamedAttributeNode("categories"),@NamedAttributeNode("contacts")})
})

public class Item extends AbstractEntity<Item>
{
	@Column(unique=true)
	@NotNull
	private String arName;
	@Column(unique=true)
	@NotNull
	private String enName;
	private String arDesc;
	private String enDesc;
	private boolean favorite;
	private String keywords;
	private int rank;
	
	@OneToOne(fetch=FetchType.EAGER)
	private Contact mainContact;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true,mappedBy="item")
	@OrderColumn(name="rank",nullable=false)
	private List<Contact> contacts=new ArrayList<>();
	
	
	@ManyToMany
	Set<Category> categories=new HashSet<>();
	
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

	public String getArDesc()
	{
		return arDesc;
	}

	public void setArDesc(String arDesc)
	{
		this.arDesc = arDesc;
	}

	public String getEnDesc()
	{
		return enDesc;
	}

	public void setEnDesc(String enDesc)
	{
		this.enDesc = enDesc;
	}

	public boolean isFavorite()
	{
		return favorite;
	}

	public void setFavorite(boolean favorite)
	{
		this.favorite = favorite;
	}

	public String getKeywords()
	{
		return keywords;
	}

	public void setKeywords(String keywords)
	{
		this.keywords = keywords;
	}

	public int getRank()
	{
		return rank;
	}

	public void setRank(int rank)
	{
		this.rank = rank;
	}

	public Contact getMainContact()
	{
		return mainContact;
	}

	public void setMainContact(Contact mainContact)
	{
		this.mainContact = mainContact;
	}

	public List<Contact> getContacts()
	{
		return contacts;
	}

	public void setContacts(List<Contact> contacts)
	{
		this.contacts = contacts;
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

	@Override
	public String toString()
	{
		return "Item [arName=" + arName + ", enName=" + enName + ", arDesc=" + arDesc + ", enDesc=" + enDesc + ", favorite=" + favorite + ", keywords="
				+ keywords + ", rank=" + rank + ", mainContact=" + mainContact + ", contacts=" + contacts + ", categories=" + categories + ", status=" + status
				+ "]";
	}


	
}
