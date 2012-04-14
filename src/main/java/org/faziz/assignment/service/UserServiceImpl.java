package org.faziz.assignment.service;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.faziz.assignment.domain.Address;
import org.faziz.assignment.domain.User;
import org.faziz.assignment.service.exception.UserNotFoundException;
import org.faziz.assignment.service.meta.Export;
import org.faziz.assignment.service.meta.HttpMetod;

/**
 * Service used to serve user related functionality.
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
        checkNotNull(user, "Please provide user.");
        checkNotNull(user.getAddress(), "Please provide user address.");
        
        entityManager.persist(user.getAddress());
        entityManager.persist(user);
        
        return user;
    }

    @Override
    @Export(method = HttpMetod.PUT, name = "/users/", authenticate = true)
    public User updateUser(Map<String, String[]> param, User user) {
        checkNotNull(user, "Please provide user.");
        checkNotNull(user.getAddress(), "Please provide user address.");
        
        entityManager.merge(user.getAddress());
        entityManager.merge(user);
        
        return user;
    }
    
    @Override
    @Export(method = HttpMetod.GET, name = "/users/", authenticate = false)
    public User getUser(final Map<String, String[]> param, final User user) {
        checkNotNull(user, "Please provide user.");
        
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
        checkNotNull(user, "Please provide user.");
        entityManager.remove( getUser(param, user));
    }

    @Override
    @Export(method = HttpMetod.PUT, name = "/users/*/address", authenticate = true)
    public Address updateUserProfile(Map<String, String[]> param, int userId, Address address) {
//        checkArgument( userId < 1, "User id " + userId + " not found.");
        checkNotNull(address, "Please provide address to update.");
        
        User user =  entityManager.find(User.class, userId);
        user.setAddress(address);
        
        return updateUser(param, user).getAddress();
    }
    
    @Override
    @Export(method= HttpMetod.POST, name="/users/*/address", authenticate=true)
    public Address addAddress(Map<String, String[]> param, int userId, Address address) {
        entityManager.persist(address);
        return updateUserProfile(param, userId, address);
    }

    @Override
    @Export(method= HttpMetod.GET, name="/users/*/address", authenticate=false)
    public Address getAdress(Map<String, String[]> param, int userId) {
//        checkArgument( userId < 1, "User id not found.");
        
        return entityManager.find(User.class, userId).getAddress();
    }
}
