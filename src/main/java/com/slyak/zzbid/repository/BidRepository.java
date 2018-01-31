package com.slyak.zzbid.repository;

import com.slyak.zzbid.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * .
 *
 * @author stormning 2017/12/19
 * @since 1.3.0
 */
public interface BidRepository extends JpaRepository<Bid, String> {
}
