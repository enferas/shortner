/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.enferas.entities;

import me.enferas.util.persistence.PersistenceException;
import me.enferas.util.persistence.PersistenceUtil;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cosette
 */
public class ShortnerUtil {

    private static ShortnerUtil defaultInstance;
    private PersistenceUtil persistence;


    public static ShortnerUtil getDefault() {
        return defaultInstance;
    }

    public static ShortnerUtil initDefault(PersistenceUtil persistenceUtil) {

        if (defaultInstance == null) {
            defaultInstance = new ShortnerUtil(persistenceUtil);
        }

        return defaultInstance;
    }

    public ShortnerUtil(PersistenceUtil persistenceUtil) {
        this.persistence = persistenceUtil;

    }

    
    public RealURL addRealUrl(RealURL realurl) {

        try {
            return this.persistence.merge(realurl);
        } catch (PersistenceException ex) {
            Logger.getLogger(ShortnerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public ShortURL addShortUrl(ShortURL shorturl) {

        try {
            return this.persistence.merge(shorturl);
        } catch (PersistenceException ex) {
            Logger.getLogger(ShortnerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public ShortURL getShortUrl(String url) {
        
        List<ShortURL> ls =  this.persistence.find(ShortURL.class, "url", url);
        if(ls==null || ls.size()==0)
            return null;
        else
            return ls.get(0);
    }
    
    public Click addClick(Click click) {
        try {
            return this.persistence.merge(click);
        } catch (PersistenceException ex) {
            Logger.getLogger(ShortnerUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
