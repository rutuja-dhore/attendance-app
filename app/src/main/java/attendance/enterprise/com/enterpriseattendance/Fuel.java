package attendance.enterprise.com.enterpriseattendance;

import java.io.Serializable;
import java.util.Date;

public class Fuel implements Serializable {

    private Integer id;

    private Date date;

    private Van van;

    private double amount;

   private String type;


    public Fuel(Integer id, Date date, Van van, double amount, String type) {
        this.id = id;
        this.date = date;
        this.van = van;
        this.amount = amount;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Van getVan() {
        return van;
    }

    public void setVan(Van van) {
        this.van = van;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
