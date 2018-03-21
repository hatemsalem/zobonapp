package zobonapp.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Entity
public class Category extends AbstractEntity<Category>
{
	
	@Column(unique=true)
	@NotNull
	private String arName;
	@Column(unique=true)
	@NotNull
	private String enName;
	private String arDesc;
	private String enDesc;
	private String keywords;
	private int type;
	private int rank;
	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private Status status;
	
	
	
	public String getArName()
	{
		return arName;
		
	}
	public Category setArName(String arName)
	{
		this.arName = arName;
		return this;
	}
	public String getEnName()
	{
		return enName;
	}
	public Category setEnName(String enName)
	{
		this.enName = enName;
		return this;
	}
	public String getArDesc()
	{
		return arDesc;
	}
	public Category setArDesc(String arDesc)
	{
		this.arDesc = arDesc;
		return this;
	}
	public String getEnDesc()
	{
		return enDesc;
	}
	public Category setEnDesc(String enDesc)
	{
		this.enDesc = enDesc;
		return this;
	}
	public String getKeywords()
	{
		return keywords;
	}
	public Category setKeywords(String keywords)
	{
		this.keywords = keywords;
		return this;
	}
	public int getType()
	{
		return type;
	}
	public Category setType(int type)
	{
		this.type = type;
		return this;
	}
	public int getRank()
	{
		return rank;
	}
	public Category setRank(int rank)
	{
		this.rank = rank;
		return this;
	}
	public Status getStatus()
	{
		return status;
	}
	public Category setStatus(Status status)
	{
		this.status = status;
		return this;
	}
	
	@Override
	public String toString()
	{
		return "Category [id=" + getId() + ", arName=" + arName + ", enName=" + enName + ", arDesc=" + arDesc + ", enDesc="
				+ enDesc + ", keywords=" + keywords + ", type=" + type + ", rank=" + rank + ", status=" + status
				+ ", creationTimestamp=" + getCreated() + ", lastupdateTimestamp=" + getUpdated()
				+ ", version=" + getVersion() + "]";
	}

}
