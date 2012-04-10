package org.faziz.assignment.service;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.faziz.assignment.domain.Address;
import org.faziz.assignment.domain.User;
import org.faziz.assignment.service.exception.UserNotFoundException;
import org.faziz.assignment.service.meta.Export;
import org.faziz.assignment.service.meta.HttpMetod;

/**
 *
 * @author faisal
 */
public class UserServiceImpl extends AbstractService implements UserService{
    
    public UserServiceImpl(EntityManager entityManager){
        super(entityManager);
    }
    
    @Override
    @Export(method = HttpMetod.POST, name = "/users/", authenticate = true)
    public User addUser(Map<String, String[]> param, User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    @Export(method = HttpMetod.PUT, name = "/users/", authenticate = true)
    public User updateUser(Map<String, String[]> param, User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    @Export(method = HttpMetod.GET, name = "/users/", authenticate = false)
    public User getUser(final Map<String, String[]> param, final User user) {
        Query q = entityManager.createNamedQuery("User.findByUsername");
        q.setParameter("username", user.getUsername());
        
        User result = null;
        try{
            result = (User) q.getSingleResult();
        }catch(Exception exception){
            throw new UserNotFoundException(user.getUsername(), exception);
        }
        
        return result;
    }

    @Override
    @Export(method= HttpMetod.DELETE, name="/users/", authenticate=true)
    public void deleteUser(Map<String, String[]> param, User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Export(method= HttpMetod.POST, name="/users/*/address/*", authenticate=true)
    public Address addAddress(Map<String, String[]> param, int userId, Address address) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Export(method= HttpMetod.GET, name="/users/*/address/*", authenticate=true)
    public Address getAdress(Map<String, String[]> param, int addressId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
