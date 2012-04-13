package org.faziz.assignment.service;

import java.util.Map;
import org.faziz.assignment.domain.Address;
import org.faziz.assignment.domain.User;

/**
 *
 * @author faisal
 */
public interface UserService {

    public User addUser(Map<String, String[]> param, User user);

    public User updateUser(Map<String, String[]> param, User user);

    public User getUser(Map<String, String[]> param, User user);

    public void deleteUser(Map<String, String[]> param, User user);

    public Address updateUserProfile(Map<String, String[]> param, int userId, Address address);
    
    public Address  addAddress(Map<String, String[]> param, int userId, Address address);
    
    public Address  getAdress(Map<String, String[]> param, int userId);
}
