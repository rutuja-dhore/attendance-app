package attendance.enterprise.com.enterpriseattendance;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class User implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private String password;

    private String mobileNumber;

    private Set<String> role;

    private String vanNumber;

    private String vendor;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVanNumber() {
        return vanNumber;
    }

    public void setVanNumber(String vanNumber) {
        this.vanNumber = vanNumber;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public User(Long id, String firstName, String lastName, String password, String mobileNumber, Set<String> role, String vanNumber, String vendor) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.role = role;
        this.vanNumber = vanNumber;
        this.vendor = vendor;
    }
}