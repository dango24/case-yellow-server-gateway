package com.caseyellow.server.central.persistence.website.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SpeedTestWebSiteCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String identifier;
    private int count;
    private boolean active;

    public SpeedTestWebSiteCounter(String identifier) {
        this.identifier = identifier;
        this.count = 0;
        this.active = true;
    }
}
