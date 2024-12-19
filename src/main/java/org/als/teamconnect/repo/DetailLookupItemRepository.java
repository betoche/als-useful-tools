package org.als.teamconnect.repo;

import org.als.teamconnect.entity.DetailLookupItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface DetailLookupItemRepository extends CrudRepository<DetailLookupItem, BigInteger> {
    Optional<DetailLookupItem> findByParentIdAndNameAndTableId(BigInteger parentId, String name, Integer tableId);
}
