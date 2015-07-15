package me.enferas.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;

@Entity(name = "realurl")
public class RealURL implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rid")
    private Long rid;
    
    @Column(name="url", length=200)
    private String url;
    
    @OneToMany(mappedBy = "realUrl", cascade = CascadeType.REMOVE)
    private Set<ShortURL> shortUrls; 
    
    public Long getId() {
        return rid;
    }
 
    public void setId(Long id) {
        this.rid = id;
    }
 
    public String getUrl() {
        return url;
    }
 
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Set<ShortURL> getShortUrls() {
        return shortUrls;
    }
}
