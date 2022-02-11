package attendance.enterprise.com.enterpriseattendance;

import java.io.Serializable;

public class Van implements Serializable {
    Integer id;
    String number;

    public Van(int id, String name) {
        this.id =id;
        this.number = name;
    }

    public String getNumber() {
        return number;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}