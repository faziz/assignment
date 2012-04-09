package org.faziz.assignment.domain;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserList {
    
    private List<User> users = new ArrayList<User>();

    /**
     * @return the users
     */
    @XmlElement
    public List<User> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<User> users) {
        this.users = users;
    }
}
