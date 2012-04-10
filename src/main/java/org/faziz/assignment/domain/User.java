package org.faziz.assignment.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @author faisal
 */
@Entity
@Table(name = "user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.isAuthentic", query = "SELECT u FROM User u WHERE "
        + "u.username = :username AND u.password = :password AND u.apiToken = :apiToken")
    })
public class User implements Serializable {
    private static final long serialVersionUID = 12314234L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull(message="Username can not be empty.")
    @Size(min = 1, max = 20)
    @Column(name = "USERNAME")
    private String username;
    @Basic(optional = false)
    @NotNull(message="Password can not be empty.")
    @Size(min = 1, max = 20)
    @Column(name = "PASSWORD")
    private String password;
    @Basic(optional = false)
    @NotNull(message="First name can not be empty.")
    @Size(min = 1, max = 20)
    @Column(name = "FIRSTNAME")
    private String firstName;
    @Basic(optional = false)
    @NotNull(message="Last name can not be empty.")
    @Size(min = 1, max = 20)
    @Column(name = "LASTNAME")
    private String lastName;
    @Size(max = 20)
    @Column(name = "MIDDLENAME")
    private String middleName;
    @Basic(optional = false)
    @NotNull(message="API token can not be empty.")
    @Size(min = 1, max = 20)
    @Column(name = "APITOKEN")
    private String apiToken;
    @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ID")
    @OneToOne
    private Address address;

    public User() {
    }

    public User(Integer id) {
        this.id = id;
    }

    public User(Integer id, String username, String password, String firstname, String lastname, String apiToken) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstname;
        this.lastName = lastname;
        this.apiToken = apiToken;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstName;
    }

    public void setFirstname(String firstname) {
        this.firstName = firstname;
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastname) {
        this.lastName = lastname;
    }

    public String getMiddlename() {
        return middleName;
    }

    public void setMiddlename(String middlename) {
        this.middleName = middlename;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    @XmlElement
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(3, 11).
                append(this.address).
                append(this.apiToken).
                append(this.firstName).
                append(this.lastName).
                append(this.middleName).
                append(this.password).
                append(this.username).
                toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        
        if( this == object){
            return true;
        }
        
        User other = (User) object;
        return new EqualsBuilder().append(this.address, other.address).
                append(this.apiToken, other.apiToken).
                append(this.firstName, other.firstName).
                append(this.lastName, other.lastName).
                append(this.middleName, other.middleName).
                append(this.password, other.password).
                append(this.username, other.username).
                isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("address", address).
                append("apiToken", apiToken).
                append("firstName", firstName).
                append("lastName", lastName).
                append("middleName", middleName).
                append("password", password).
                append("username", username).
                toString();
    }    
}
