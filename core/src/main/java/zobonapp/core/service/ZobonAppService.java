package zobonapp.core.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import zobonapp.core.domain.BusinessEntity;
import zobonapp.core.domain.Offer;

public interface ZobonAppService
{
	BusinessEntity save(BusinessEntity item, List<String> categories);
	Offer save(Offer offer,List<String> categories);
	BusinessEntity save(BusinessEntity item);
	Offer save(Offer offer);
	BusinessEntity find(UUID id);
	BusinessEntity findByArName(String arName);
	BusinessEntity findByEnName(String enName);
	Iterable<BusinessEntity> findNewItems(Date lastUpdate);
	Iterable<BusinessEntity> findUpdatedItems(Date lastUpdate);
	Iterable<BusinessEntity> findUnpublishedItems(Date lastUpdate);
	Iterable<Offer> findNewOffers(Date lastUpdate);
	Iterable<Offer> findUpdatedOffers(Date lastUpdate);
	Iterable<Offer> findUnpublishedOffers(Date lastUpdate);
	Timestamp latestUpdate();
	List<UUID> findItemsIdsforHotline(String hotline);
	int updateItemRank(UUID id,int rank);
	void test();

}
