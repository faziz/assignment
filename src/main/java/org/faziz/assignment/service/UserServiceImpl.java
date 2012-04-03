package org.faziz.assignment.service;

import java.util.Map;
import org.faziz.assignment.domain.Address;
import org.faziz.assignment.domain.User;
import org.faziz.assignment.service.meta.Export;
import org.faziz.assignment.service.meta.HttpMetod;

/**
 *
 * @author faisal
 */
public class UserServiceImpl implements UserService{

    private static UserService userService = new UserServiceImpl();
    
    private UserServiceImpl(){}
    
    public static UserService getInstance(){
        return userService;
    }
    
    @Override
    @Export(method = HttpMetod.POST, name = "/users/", authenticate = true)
    public User addUser(Map<String, String> param, User user) {
        
        return user;
    }

    @Override
    @Export(method = HttpMetod.PUT, name = "/users/*/address", authenticate = true)
    public User updateUser(Map<String, String> param, User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Export(method = HttpMetod.GET, name = "/users/*", authenticate = false)
    public User getUser(Map<String, String> param, int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Export(method= HttpMetod.DELETE, name="/users/*", authenticate=true)
    public void deleteUser(Map<String, String> param, int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Export(method= HttpMetod.POST, name="/users/*/address", authenticate=true)
    public Address addAddress(Map<String, String> param, int userId, Address address) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Export(method= HttpMetod.GET, name="/users/*/address/*", authenticate=true)
    public Address getAdress(Map<String, String> param, int addressId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
