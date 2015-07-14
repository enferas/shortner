/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.enferas.entities;

import me.enferas.util.persistence.PersistenceUtil;
import java.io.File;
import javax.persistence.EntityManagerFactory;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

/**
 *
 * @author cosette
 */
public class ShortnerEntities {

    /**
     * @param args the command line arguments
     */
    
    public static void build_DB(){
        File libraryFile = new File(System.getProperty("user.home") + File.separator + "shortnerdb" + File.separator);
        boolean setup = false;
        if (!libraryFile.exists()) {
            setup = true;
        }
        Ejb3Configuration cfg = createConfiguration(libraryFile);
        new SchemaUpdate(cfg.getHibernateConfiguration()).execute(true, true);
        EntityManagerFactory factory = cfg.createEntityManagerFactory();
        PersistenceUtil persistenceUtil = new PersistenceUtil(factory);
        ShortnerUtil.initDefault(persistenceUtil);
    }
    
    public static void main(String[] args) {
        build_DB();
        RealURL real = new RealURL();
        real.setUrl("www.google.com");
        
        real = ShortnerUtil.getDefault().addRealUrl(real);
        
        ShortURL shor = new ShortURL();
        shor.setUrl("gogl");
        shor.setRealUrl(real);
        
        ShortnerUtil.getDefault().addShortUrl(shor);
    }

    private static Ejb3Configuration createConfiguration(File libFile) {
        Ejb3Configuration cfg = new Ejb3Configuration();
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        cfg.setProperty("hibernate.connection.url", "jdbc:h2:"
                + libFile.toURI().getPath() + "/db/sayegh_library");
        cfg.setProperty("hibernate.connection.username", "sa");
        cfg.setProperty("hibernate.connection.password", "");

        cfg.setProperty("connection.pool_size", "1");
        cfg.setProperty("current_session_context_class", "thread");
        cfg.setProperty("hbm2ddl.auto", "update");
        cfg.setProperty("show_sql", "true");
        cfg.setProperty("default-lazy", "false");
        cfg.setProperty("hibernate.connection.autocommit", "true");

        cfg.addAnnotatedClass(ShortURL.class);
        cfg.addAnnotatedClass(RealURL.class);
        cfg.addAnnotatedClass(Click.class);

        return cfg;
    }
}
