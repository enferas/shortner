package me.enferas.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;

@Entity(name = "shorturl")
public class ShortURL implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sid")
    private Long sid;
    
    @Column(name="url", length=50)
    private String url;
    
    
    @Basic(optional = false)
    @Column(name = "creationtime", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationtime;

    
    @ManyToOne(optional = false)
    @JoinColumn(name = "rid")
    private RealURL realUrl;
    
    @OneToMany(mappedBy = "shortUrl", cascade = CascadeType.REMOVE)
    private Set<Click> clicks;
    
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
    
    public Date getCreationtime() {
        return creationtime;
    }
    
    public void setCreationtime(Date timestamp) {
        creationtime = timestamp;
    }
    
    public RealURL getRealUrl() {
        return realUrl;
    }
    
    public void setRealUrl(RealURL realUrl) {
        this.realUrl = realUrl;
    }
    
    public Set<Click> getClicks() {
        return clicks;
    }
}
