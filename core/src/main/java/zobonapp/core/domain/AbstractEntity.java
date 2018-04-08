package zobonapp.core.domain;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class AbstractEntity<T extends AbstractEntity<?>>
{
	@Id
	@GenericGenerator(name="UUID",strategy="org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator="UUID")
	private UUID id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date updated;
	@Version
	private int version;
	
	@PrePersist
	protected void onCreate()
	{
		updated=created=new Date();
	}
	
	@PreUpdate
	protected void onUpdate()
	{
		updated=new Date();
	}
	
	
	public UUID getId()
	{
		return id;
	}
	public T setId(UUID id)
	{
		this.id = id;
		return (T)this;
	}
	
	
	
	public Date getCreated()
	{
		return created;
	}
	public void setCreated(Date creationTimestamp)
	{
		this.created = creationTimestamp;
	}
	public Date getUpdated()
	{
		return updated;
	}
	public void setUpdated(Date lastupdateTimestamp)
	{
		this.updated = lastupdateTimestamp;
	}
	public int getVersion()
	{
		return version;
	}
	public void setVersion(int version)
	{
		this.version = version;
	}
}
