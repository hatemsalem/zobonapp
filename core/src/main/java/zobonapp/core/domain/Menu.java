package zobonapp.core.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
public class Menu extends AbstractEntity<Menu>
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
	
	@ManyToMany
	List<Category> categories;
	
	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private Status status;

	

}
