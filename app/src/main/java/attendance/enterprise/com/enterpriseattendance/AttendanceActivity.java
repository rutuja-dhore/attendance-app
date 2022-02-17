package attendance.enterprise.com.enterpriseattendance;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class AttendanceActivity extends AppCompatActivity implements View.OnClickListener {

    //listview object
    ListView listView;

    //the hero list where we will store all the hero objects after parsing json
    List<Attendance> herolist;

    private int mYear, mMonth, mDay, mHour, mMinute, mSec;

    EditText fromDate, toDate;

    ProgressBar progressBar;

    private static final String attendanceDomain = "http://34.93.190.224:8080/trips";

    private static final String exportDomain = "http://34.93.190.224:8080/trips/downloadTemplate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        listView = (ListView) findViewById(R.id.listView);
        final User userObject = (User) getIntent().getSerializableExtra("user");

        final CardView cardView = (CardView) findViewById(R.id.card_view);
        final CardView cardViewAdmin = (CardView) findViewById(R.id.card_view_admin);

        FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.buttonAdd);
        FloatingActionButton buttonExport = (FloatingActionButton) findViewById(R.id.buttonExport);
        Button buttonSearch = (Button) findViewById(R.id.buttonSearch);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fromDate = (EditText) findViewById(R.id.fromDate);
        toDate = (EditText) findViewById(R.id.toDate);

        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);

        final LinearLayout emailForm = (LinearLayout) findViewById(R.id.email_login_form);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (inputValidated()) {
                    cardView.setVisibility(View.VISIBLE);
                    cardViewAdmin.setVisibility(View.INVISIBLE);


                    String fromDateValue = fromDate.getText().toString();
                    String toDateValue = toDate.getText().toString();

                    String title = "All Logs";
                    if (!TextUtils.isEmpty(fromDateValue) && !TextUtils.isEmpty(toDateValue)) {
                        title = "Logs of " + fromDateValue + " to " + toDateValue;
                        loadVans(attendanceDomain + "?fromDate=" + fromDateValue + "&toDate=" + toDateValue);
                    } else if (!TextUtils.isEmpty(fromDateValue) && TextUtils.isEmpty(toDateValue)) {
                        title = "Logs of " + fromDateValue;
                        loadVans(attendanceDomain + "?fromDate=" + fromDateValue);
                    }
//                http://34.93.190.224:8080/trips?fromDate=2018-04-17&toDate=2018-04-19&mobileNumber=8888888888

                    AttendanceActivity.this.setTitle(title);
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), AddAttendance.class);
                i.putExtra("user", userObject);
                startActivity(i);
                finish();
            }
        });

        if (userObject.getRole().contains("ROLE_USER")) {
            cardView.setVisibility(View.VISIBLE);
            buttonExport.setVisibility(View.INVISIBLE);
            loadVans(attendanceDomain + "/" + userObject.getMobileNumber());
        } else {
            fromDate.setText(Helper.formatter.format(Helper.getFirstDateOfMonth(new Date())));
            toDate.setText(Helper.formatter.format(new Date()));
            cardViewAdmin.setVisibility(View.VISIBLE);
            buttonAdd.setVisibility(View.INVISIBLE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Attendance attendanceObject = (Attendance) parent.getItemAtPosition(position);

                Intent i = new Intent(getApplicationContext(), AddAttendance.class);
                i.putExtra("user", userObject);
                i.putExtra("attendance", attendanceObject);
                startActivity(i);
                finish();
            }
        });


        buttonExport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                progressBar.setVisibility(View.VISIBLE);

   export(exportDomain);
            }
        });

    }

    private void addNotification(String location) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_file_download_black_24dp)
                        .setContentTitle("Download Complete")
                        .setContentText("Location : " + location);

        Intent notificationIntent = new Intent(this, AttendanceActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }



    private boolean inputValidated() {

        if (TextUtils.isEmpty(fromDate.getText())) {
            fromDate.setError(getString(R.string.error_field_required));
            fromDate.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(toDate.getText())) {
            toDate.setError(getString(R.string.error_field_required));
            toDate.requestFocus();
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {

        if (v == fromDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            String month = String.valueOf((monthOfYear + 1)), day = String.valueOf(dayOfMonth);

                            if (String.valueOf(monthOfYear).length() == 1) {
                                month = "0" + month;

                            }
                            if (String.valueOf(dayOfMonth).length() == 1) {
                                day = "0" + day;
                            }

                            fromDate.setText( day + "-" + month+ "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        } else if (v == toDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            String month = String.valueOf((monthOfYear + 1)), day = String.valueOf(dayOfMonth);

                            if (String.valueOf(monthOfYear).length() == 1) {
                                month = "0" + month;

                            }
                            if (String.valueOf(dayOfMonth).length() == 1) {
                                day = "0" + day;
                            }

                            toDate.setText(day + "-" + month+ "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

    }

    private void loadVans(String url) {

        herolist = new ArrayList<>();

        //getting the progressbar

        //making the progressbar visible
        progressBar.setVisibility(View.VISIBLE);

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        progressBar.setVisibility(View.INVISIBLE);

                        try {
                            JSONArray heroArray = new JSONArray(response);


                            if (heroArray.length() > 0) {
                                //now looping through all the elements of the json array
                                for (int i =  heroArray.length()-1; i >= 0; i--) {
                                    //getting the json object of the particular index inside the array
                                    JSONObject heroObject = heroArray.getJSONObject(i);

                                    Integer id = null;
                                    if (!heroObject.isNull("id")) {
                                        id = heroObject.getInt("id");
                                    }

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                                    Date objDate = dateFormat.parse(heroObject.getString("logDate"));

                                    Double startKm = heroObject.getDouble("startKm");
                                    Double endKm = heroObject.getDouble("endKm");
                                    Double totalKm = heroObject.getDouble("totalKm");

                                    Vendor vendor = null;

                                    if( heroObject.has("vendorOutputDTO")){
                                        JSONObject shiftObject = heroObject.getJSONObject("vendorOutputDTO");
                                        vendor = new Vendor(shiftObject.getInt("id"), shiftObject.getString("name"));
                                    }

                                    JSONObject vanObject = heroObject.getJSONObject("vanOutputDTO");
                                    Van van = new Van(vanObject.getInt("id"), vanObject.getString("number"));

                                    JSONObject userObject = heroObject.getJSONObject("userOutupDTO");
                                    String firstName = userObject.getString("firstName") + " " + userObject.getString("lastName");
                                    String comment = heroObject.getString("comment");

                                    //creating a van object and giving them the values from json object
                                    Attendance attendance = new Attendance(id, objDate, vendor, van, startKm, endKm, totalKm, firstName,comment);

                                    //adding the van to herolist
                                    herolist.add(attendance);


                                }

                                //creating custom adapter object
                                AttendanceListViewAdapter adapter = new AttendanceListViewAdapter(herolist, getApplicationContext());

                                //adding the adapter to listview
                                listView.setAdapter(adapter);
                            }
                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        MySingleTon.getInstance(AttendanceActivity.this).addToRequestQue(stringRequest);

    }


    private void export(String url) {

        herolist = new ArrayList<>();

        //getting the progressbar

        //making the progressbar visible
        progressBar.setVisibility(View.VISIBLE);

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        progressBar.setVisibility(View.INVISIBLE);

                       try  {
                           Toast.makeText(getApplicationContext(), "Downloaded", Toast.LENGTH_SHORT).show();


                       } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        MySingleTon.getInstance(AttendanceActivity.this).addToRequestQue(stringRequest);

    }

}
