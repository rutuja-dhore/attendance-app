package attendance.enterprise.com.enterpriseattendance;

import java.io.Serializable;
import java.sql.Time;

public class Vendor implements Serializable {
    Integer id;

    String name;

    public Vendor(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}