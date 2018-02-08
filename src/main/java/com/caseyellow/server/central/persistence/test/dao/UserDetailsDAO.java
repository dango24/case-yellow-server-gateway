package com.caseyellow.server.central.persistence.test.dao;

import javax.persistence.*;

@Entity
@Table(name = "user_details")
public class UserDetailsDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String userName;

    private int speed;
    private String isp;
    private String infrastructure;

    public UserDetailsDAO() {
    }

    public UserDetailsDAO(String userName, String isp, String infrastructure, int speed) {
        this.userName = userName;
        this.infrastructure = infrastructure;
        this.isp = isp;
        this.speed = speed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(String infrastructure) {
        this.infrastructure = infrastructure;
    }
}
