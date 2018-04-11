package com.slyak.zzbid.repository;

import com.slyak.zzbid.model.Bid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * .
 *
 * @author stormning 2017/12/19
 * @since 1.3.0
 */
public interface BidRepository extends JpaRepository<Bid, String> {
    @Query(value = "select b from Bid b where b.bidTime=0")
    @Cacheable(cacheNames = "bidList", key = "'ubids'")
    List<Bid> findUnbidBids();

    @Override
    @CacheEvict(cacheNames = "bidList", key = "'ubids'")
    Bid save(Bid entity);

    @Override
    @CacheEvict(cacheNames = "bidList", key = "'ubids'")
    void deleteAllInBatch();
}
