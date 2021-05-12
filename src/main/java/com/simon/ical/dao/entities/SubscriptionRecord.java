package com.simon.ical.dao.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Simon [xhh52ch@gmail.com]
 */
@Entity
@Table(name = "subscription_records")
@Data
public class SubscriptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String ip;
    private Integer times;
    private Integer createdDate;
    private Integer modifiedDate;
}
