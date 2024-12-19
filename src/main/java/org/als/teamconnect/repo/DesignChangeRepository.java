package org.als.teamconnect.repo;

import org.als.teamconnect.entity.DesignChange;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface DesignChangeRepository extends CrudRepository<DesignChange, BigInteger> {
}
