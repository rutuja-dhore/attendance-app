package attendance.enterprise.com.enterpriseattendance;

import java.io.Serializable;
import java.util.Date;

public class Attendance implements Serializable {

    private Integer id;

    private Date date;

    private Vendor vendor;

    private String tableNo;

    private Van van;

    private double startKm;

    private double endKm;

    private double totalKm;

    private double disel;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    private String comment;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public Van getVan() {
        return van;
    }

    public void setVan(Van van) {
        this.van = van;
    }

    public double getStartKm() {
        return startKm;
    }

    public void setStartKm(double startKm) {
        this.startKm = startKm;
    }

    public double getEndKm() {
        return endKm;
    }

    public void setEndKm(double endKm) {
        this.endKm = endKm;
    }

    public double getDisel() {
        return disel;
    }

    public void setDisel(double disel) {
        this.disel = disel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public double getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(double totalKm) {
        this.totalKm = totalKm;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Attendance(Integer id, Date date, Vendor vendor, Van van, double startKm, double endKm, Double totalKm, String firstName, String comment) {
        this.id = id;
        this.date = date;
        this.vendor = vendor;
        this.van = van;
        this.startKm = startKm;
        this.endKm = endKm;
        this.totalKm= endKm - startKm;
        this.firstName = firstName;
        this.comment = comment;
    }
}
