package zobonapp.core.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import zobonapp.core.domain.Item;
import zobonapp.core.domain.Offer;

public interface ZobonAppService
{
	Item save(Item item, List<String> categories);
	Offer save(Offer offer,List<String> categories);
	Item save(Item item);
	Offer save(Offer offer);
	Item find(UUID id);
	Item findByArName(String arName);
	Item findByEnName(String enName);
	Iterable<Item> findNewItems(Date lastUpdate);
	Iterable<Item> findUpdatedItems(Date lastUpdate);
	Iterable<Item> findUnpublishedItems(Date lastUpdate);
	Iterable<Offer> findNewOffers(Date lastUpdate);
	Timestamp latestUpdate();
	List<UUID> findItemsIdsforHotline(String hotline);
	int updateItemRank(UUID id,int rank);
	void test();

}
