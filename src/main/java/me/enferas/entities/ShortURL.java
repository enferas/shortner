package me.enferas.entities;

import java.io.Serializable;
import javax.persistence.*;

@Entity(name = "shorturl")
public class ShortURL implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sid")
    private Long sid;
    
    @Column(name="url", length=50)
    private String url;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "rid")
    private RealURL realUrl;
    
    public Long getId() {
        return sid;
    }
 
    public void setId(Long id) {
        this.sid = id;
    }
 
    public String getUrl() {
        return url;
    }
 
    public void setUrl(String url) {
        this.url = url;
    }
    
    public RealURL getRealUrl() {
        return realUrl;
    }
    
    public void setRealUrl(RealURL realUrl) {
        this.realUrl = realUrl;
    }
}
