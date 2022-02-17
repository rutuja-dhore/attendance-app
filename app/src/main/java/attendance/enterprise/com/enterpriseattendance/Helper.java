package attendance.enterprise.com.enterpriseattendance;

import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class Helper {

    public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static Date getFirstDateOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    public static User getUserObject(SharedPreferences sp) {
        return new User(sp.getLong("id", 0),
                sp.getString("firstName", ""),
                sp.getString("lastName", ""),
                sp.getString("password", ""),
                sp.getString("mobileNumber", ""),
                sp.getStringSet("roles", new HashSet<String>()),
                sp.getString("vanNumber", ""),
                sp.getString("vendor", ""));
    }

}
