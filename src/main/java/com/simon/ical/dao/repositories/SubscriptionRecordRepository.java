package com.simon.ical.dao.repositories;

import com.simon.ical.dao.entities.SubscriptionRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Repository
public interface SubscriptionRecordRepository extends JpaRepository<SubscriptionRecord, Integer> {

    SubscriptionRecord findByIp(String ip);

    @Query(nativeQuery = true, value = "select count(id) from subscription_records")
    Integer countAll();
}
