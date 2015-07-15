/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.enferas.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author aboud
 */
@Entity(name = "click")
@XmlRootElement
public class Click implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cid")
    private Long cid;
    
    @Column(name="IP", length=20)
    private String IP;
    
    @Basic(optional = false)
    @Column(name = "clicktime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date clicktime;
    
    @Basic(optional = true)
    @Column(name = "deviceType", updatable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "sid")
    private ShortURL shortUrl;
    
    public Long getId() {
        return cid;
    }
 
    public void setId(Long id) {
        this.cid = id;
    }
 
    public String getIP() {
        return IP;
    }
 
    public void setIP(String IP) {
        this.IP = IP;
    }
    
    public Date getClicktime() {
        return clicktime;
    }
 
    public void setClicktime(Date clicktime) {
        this.clicktime = clicktime;
    }
    
    public DeviceType getDeviceType() {
        return deviceType;
    }
 
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public ShortURL getShortUrl() {
        return shortUrl;
    }
    
    public void setShortUrl(ShortURL shortUrl) {
        this.shortUrl = shortUrl;
    }

}
