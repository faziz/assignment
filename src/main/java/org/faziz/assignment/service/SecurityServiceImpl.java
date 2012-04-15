package org.faziz.assignment.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;

/**
 * Provides authentication related services.
 * @author faziz
 */
//@Stateless(name = "securityService")
public class SecurityServiceImpl implements SecurityService {

    @PersistenceUnit(name = "assignment")
    private EntityManagerFactory entityManagerFactory;
    
    @Override
    public boolean isAuthentic(String userName, String password, String apiToken) {
        boolean isAuth = false;
        EntityManager em = null;
        
        try {
            em = entityManagerFactory.createEntityManager();
            
            Query q = em.createNamedQuery("User.isAuthentic");
            q.setParameter("username", userName);
            q.setParameter("password", password);
            q.setParameter("apiToken", apiToken);
            
            q.getSingleResult();
            
            isAuth = true;
        } catch (NoResultException ex) {
        } catch (Exception ex) {
            Logger.getLogger(SecurityServiceImpl.class.getName()).log(Level.SEVERE, "Exception occurred.", ex);
            throw new IllegalArgumentException("Exception occurred.", ex);
        }finally{
            em.close();
        }
        
        return isAuth;
    }
}
