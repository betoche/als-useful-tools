package org.als.random.repository;

import org.als.random.entity.GlobalTextSearch;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalTextSearchRepository extends CrudRepository<GlobalTextSearch, Long> {
}
