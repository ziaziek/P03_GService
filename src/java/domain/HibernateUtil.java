/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.LoggerFactory;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author Przemo
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    
    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static List runQuery(final String qry){
        Session s = sessionFactory.openSession();
        List r = s.createQuery(qry).list();
        s.close();
        return r;
    }
    
    public static void save(final Object obj){
        Session s = sessionFactory.openSession();
        Transaction tx = s.beginTransaction();
        try {
            s.saveOrUpdate(obj);
            tx.commit();
            LoggerFactory.getLogger("HibernateUtils").info("Saving object "+obj.getClass().getName());
        } catch(Exception ex){
            tx.rollback();
            LoggerFactory.getLogger(tx.getClass()).warn(ex.getMessage());
        }
        s.close();
    }
}
