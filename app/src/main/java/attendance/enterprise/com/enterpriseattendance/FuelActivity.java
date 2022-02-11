package attendance.enterprise.com.enterpriseattendance;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class FuelActivity extends AppCompatActivity implements View.OnClickListener {

    //listview object
    ListView listView;

    //the hero list where we will store all the hero objects after parsing json
    List<Fuel> herolist;

    private int mYear, mMonth, mDay, mHour, mMinute, mSec;

    EditText fromDate, toDate;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    ProgressBar progressBar;

    private static final String fuelDomain = "http://34.93.190.224:8080/fuel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel);
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
                        loadVans(fuelDomain + "?fromDate=" + fromDateValue + "&toDate=" + toDateValue);
                    } else if (!TextUtils.isEmpty(fromDateValue) && TextUtils.isEmpty(toDateValue)) {
                        title = "Logs of " + fromDateValue;
                        loadVans(fuelDomain + "?fromDate=" + fromDateValue);
                    }
//                http://34.93.190.224:8080/trips?fromDate=2018-04-17&toDate=2018-04-19&mobileNumber=8888888888

                    FuelActivity.this.setTitle(title);
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), AddFuelActivity.class);
                i.putExtra("user", userObject);
                startActivity(i);
                finish();
            }
        });

        if (userObject.getRole().contains("ROLE_USER")) {
            cardView.setVisibility(View.VISIBLE);
            buttonExport.setVisibility(View.INVISIBLE);
            loadVans(fuelDomain + "/" + userObject.getVanNumber());
        } else {
            fromDate.setText(formatter.format(new Date()));
            toDate.setText(formatter.format(new Date()));
            cardViewAdmin.setVisibility(View.VISIBLE);
            buttonAdd.setVisibility(View.INVISIBLE);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Fuel fuelObject = (Fuel) parent.getItemAtPosition(position);

                Intent i = new Intent(getApplicationContext(), AddFuelActivity.class);
                i.putExtra("user", userObject);
                i.putExtra("fuel", fuelObject);
                startActivity(i);
                finish();
            }
        });


        buttonExport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                progressBar.setVisibility(View.VISIBLE);

           /*     ActivityCompat.requestPermissions(AttendanceActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_GRANTED);
                ActivityCompat.requestPermissions(AttendanceActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_GRANTED);
*/
                SimpleDateFormat formatterFile = new SimpleDateFormat("yyyy-MM-dd'T'hh-mm-ss");

                String Fnamexls = "attendance_log_" + formatterFile.format(new Date()) + ".xls";

//                String appPath2 = getApplicationContext().getFilesDir().getAbsolutePath();
//                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//                String baseDir = "/data/data/attendance.enterprise.com.enterpriseattendance";

//                String appPath1 = Environment.getExternalStorageDirectory().getPath();
//                String appPath2 = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

//                String appPath2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();


                File dir = new File("/sdcard/Download/");

//                File file = new File(dir, fileName);

                File file2 = new File(dir + "/" + Fnamexls);
             /*   WorkbookSettings wbSettings = new WorkbookSettings();
                wbSettings.setLocale(new Locale("en", "EN"));

                WritableWorkbook workbook;
                try {
                    int a = 1;
                    workbook = Workbook.createWorkbook(file2, wbSettings);

                    WritableSheet sheet = workbook.createSheet("First Sheet", 0);

                    try {
                        sheet.addCell(new Label(0, 0, "FIRSTNAME"));
                        sheet.addCell(new Label(1, 0, "LOG_DATE"));
                        sheet.addCell(new Label(2, 0, "SHIFT"));
                        sheet.addCell(new Label(3, 0, "TABLE_NO"));
                        sheet.addCell(new Label(4, 0, "VAN"));
                        sheet.addCell(new Label(5, 0, "START_KM"));
                        sheet.addCell(new Label(6, 0, "END_KM"));
                        sheet.addCell(new Label(7, 0, "TOTAL_KM"));
                        sheet.addCell(new Label(8, 0, "DISEL"));

                        int index = 1;
                        for (Attendance temp : herolist) {
                            sheet.addCell(new Label(0, index, temp.getFirstName()));
                            sheet.addCell(new Label(1, index, temp.getDate().toString()));
                            sheet.addCell(new Label(2, index, temp.getShift().getName()));
                            sheet.addCell(new Label(3, index, temp.getTableNo()));
                            sheet.addCell(new Label(4, index, temp.getVan().getNumber()));
                            sheet.addCell(new Label(5, index, String.valueOf(temp.getStartKm())));
                            sheet.addCell(new Label(6, index, String.valueOf(temp.getEndKm())));
                            sheet.addCell(new Label(7, index, String.valueOf(temp.getTotalKm())));
                            sheet.addCell(new Label(8, index, String.valueOf(temp.getDisel())));

                            index = index + 1;
                        }

                        DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(DOWNLOAD_SERVICE);

                        downloadManager.addCompletedDownload(file2.getName(), file2.getName(), true, "text/plain", file2.getAbsolutePath(), file2.length(), true);
//                        addNotification(file2.getAbsolutePath());
                        progressBar.setVisibility(View.INVISIBLE);

                    } catch (RowsExceededException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                    workbook.write();

                    try {
                        workbook.close();
                    } catch (WriteException e) {
                        Toast.makeText(getApplicationContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }*/
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

                            fromDate.setText(year + "-" + month + "-" + day);
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

                            toDate.setText(year + "-" + month + "-" + day);
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

                                    Double amount = heroObject.getDouble("amount");
                                    String type = heroObject.getString("type");

                                    Vendor vendor = null;

                                    JSONObject vanObject = heroObject.getJSONObject("vanOutputDTO");
                                    Van van = new Van(vanObject.getInt("id"), vanObject.getString("number"));

                                    //creating a van object and giving them the values from json object
                                    Fuel fuel = new Fuel(id,objDate,van,amount,type);
                                    //adding the van to herolist
                                    herolist.add(fuel);
                                }

                                //creating custom adapter object
                                FuelListViewAdapter adapter = new FuelListViewAdapter(herolist, getApplicationContext());

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

        MySingleTon.getInstance(FuelActivity.this).addToRequestQue(stringRequest);

    }


}
