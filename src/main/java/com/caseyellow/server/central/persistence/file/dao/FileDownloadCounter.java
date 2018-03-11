package com.caseyellow.server.central.persistence.file.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String identifier;
    private int count;
    private boolean active;

    public FileDownloadCounter(String identifier) {
        this.identifier = identifier;
        this.count = 0;
        this.active = true;
    }
}
