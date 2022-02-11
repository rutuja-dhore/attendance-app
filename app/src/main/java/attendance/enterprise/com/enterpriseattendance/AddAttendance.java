package attendance.enterprise.com.enterpriseattendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddAttendance extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {


    Spinner spinnerVan, spinnerVendor;
    EditText txtDate, txtStartKm, txtEndKm,txtComment, updateEndKm;
    private int mYear, mMonth, mDay, mHour, mMinute, mSec;
    String selectedVan, selectedShift;
    final String vanDomain = "http://34.93.190.224:8080/admin/vans";
    final String vendorDomain = "http://34.93.190.224:8080/admin/vendors";
    final String attendanceDomain = "http://34.93.190.224:8080/trips";
    CardView cardAdd, cardDetails, cardEdit;
    Attendance attendanceObject;

    Button buttonSave, buttonUpdate;
    EditText txtFirstName1, txtDate1, txtStartKm1, txtEndKm1, txtDisel1, txtComment1, txtVan1, txtShift1;
    User userObject = null;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userObject = (User) getIntent().getSerializableExtra("user");
        attendanceObject = (Attendance) getIntent().getSerializableExtra("attendance");

        setContentView(R.layout.activity_add_attendance);
        spinnerVan = (Spinner) findViewById(R.id.spinnerVan);
        spinnerVendor = (Spinner) findViewById(R.id.spinnerShift);

        List<String> vans = new ArrayList<String>();
        loadVans(vans, vanDomain, "number");
        spinnerVan.setOnItemSelectedListener(this);

        List<String> shifts = new ArrayList<String>();
        loadVendors(shifts, vendorDomain, "name");
        spinnerVendor.setOnItemSelectedListener(this);

        txtDate = (EditText) findViewById(R.id.in_date);
        txtStartKm = (EditText) findViewById(R.id.editTextStartKm);
        txtEndKm = (EditText) findViewById(R.id.editTextEndKm);
        txtComment = (EditText) findViewById(R.id.editTextComment);

        txtDate.setOnClickListener(this);
        cardAdd = findViewById(R.id.cardAdd);
        cardDetails = findViewById(R.id.cardDetails);
        cardEdit = findViewById(R.id.cardEdit);

        updateEndKm = (EditText) findViewById(R.id.updateEndKm);

        buttonSave = findViewById(R.id.buttonAdd);
        buttonUpdate = findViewById(R.id.buttonUpdate);


        txtDate1 = (EditText) findViewById(R.id.in_date1);
        txtStartKm1 = (EditText) findViewById(R.id.editTextStartKm1);
        txtEndKm1 = (EditText) findViewById(R.id.editTextEndKm1);

        txtDisel1 = (EditText) findViewById(R.id.editTextDisel1);
        txtFirstName1 = (EditText) findViewById(R.id.editTextFirstName1);
        txtComment1 = (EditText) findViewById(R.id.editTextComment1);
        txtVan1 = (EditText) findViewById(R.id.spinnerVan1);
        txtShift1 = (EditText) findViewById(R.id.spinnerShift1);

        buttonSave.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);

        if (attendanceObject != null) {
            cardAdd.setVisibility(View.INVISIBLE);
            cardDetails.setVisibility(View.VISIBLE);
            cardEdit.setVisibility(View.INVISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            txtDate1.setText(formatter.format(attendanceObject.getDate()));
            txtStartKm1.setText(String.valueOf(attendanceObject.getStartKm()));
            txtEndKm1.setText(String.valueOf(attendanceObject.getEndKm()));
            txtDisel1.setText(String.valueOf(attendanceObject.getDisel()));
            txtVan1.setText(attendanceObject.getVan().getNumber());
            txtShift1.setText(attendanceObject.getVendor().getName());
            txtComment1.setText(attendanceObject.getComment());
            txtFirstName1.setText(attendanceObject.getFirstName());

            txtDate.setText(formatter.format(attendanceObject.getDate()));
            txtStartKm.setText(String.valueOf(attendanceObject.getStartKm()));
            txtEndKm.setText(String.valueOf(attendanceObject.getEndKm()));

//            ArrayAdapter<String> array_spinner=(ArrayAdapter<String>)spinnerVan.getAdapter();
//            spinnerVan.setSelection(array_spinner.getPosition(attendanceObject.getVan().getNumber()));


            ActionBar actionBar = getSupportActionBar(); // or getActionBar();
            getSupportActionBar().setTitle("Trip Details"); // set the top title
        } else {
            cardAdd.setVisibility(View.VISIBLE);
            cardDetails.setVisibility(View.INVISIBLE);
            cardEdit.setVisibility(View.INVISIBLE);
            txtDate.setText(formatter.format(new Date()));

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (attendanceObject != null) {
            menu.findItem(R.id.itemEdit).setVisible(true);
            if (userObject != null && userObject.getRole().contains("ROLE_ADMIN")) {
                menu.findItem(R.id.itemDelete).setVisible(true);
            }
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemEdit:
                ActionBar actionBar = getSupportActionBar(); // or getActionBar();
                getSupportActionBar().setTitle("Edit Trip"); // set the top title
                cardAdd.setVisibility(View.INVISIBLE);
                cardDetails.setVisibility(View.INVISIBLE);
                updateEndKm.setText(txtEndKm1.getText());
                cardEdit.setVisibility(View.VISIBLE);

                item.setVisible(false);
                return true;

//            case R.id.itemDelete:
//                Toast.makeText(getApplicationContext(), "delete Menu", Toast.LENGTH_SHORT).show();
//                item.setVisible(false);
//                Van vanObject = (Van) getIntent().getSerializableExtra("van");
//                deleteVan(vanObject.getId());
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private boolean inputValidated() {

        if (TextUtils.isEmpty(txtDate.getText())) {
            txtDate.setError(getString(R.string.error_field_required));
            txtDate.requestFocus();
            return false;
        }
        if (spinnerVendor.getSelectedItem() == "Select Vendor") {
            TextView errorText = (TextView) spinnerVendor.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select Vendor");//changes the selected item text to this
            spinnerVendor.requestFocus();
            return false;
        }


        if (spinnerVan.getSelectedItem() == "Select Van") {
            TextView errorText = (TextView) spinnerVan.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error
            errorText.setText("Select Van");//changes the selected item text to this
            spinnerVan.requestFocus();
            return false;
        }

        if (!TextUtils.isEmpty(txtEndKm.getText()) && !TextUtils.isEmpty(txtStartKm.getText())) {
            if (Double.parseDouble(txtStartKm.getText().toString()) >= Double.parseDouble(txtEndKm.getText().toString())) {
                txtStartKm.setError("Start Km cannot be greater than End Km!");
                txtStartKm.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void saveVan(JSONObject postparams, final User userObject, String url, int methodType) {

        try {

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(methodType,
                    url, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Trip added", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), AttendanceActivity.class);
                            i.putExtra("user", userObject);
                            startActivity(i);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

            MySingleTon.getInstance(AddAttendance.this).addToRequestQue(jsonObjReq);

        } catch (Exception e) {
        }

    }

    @Override
    public void onClick(View v) {

        if (v == txtDate) {

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

                            txtDate.setText(year + "-" + month + "-" + day);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == buttonSave) {
            if (inputValidated()) {
                JSONObject postparams = new JSONObject();
                try {
                    postparams.put("comment", txtComment.getText());
                    postparams.put("endKm", txtEndKm.getText());
                    postparams.put("startKm", txtStartKm.getText());
                    postparams.put("vendorName", spinnerVendor.getSelectedItem().toString());
                    postparams.put("vanNumber", spinnerVan.getSelectedItem().toString());
                    postparams.put("logDate", txtDate.getText());
                    postparams.put("mobileNumber", userObject.getMobileNumber());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (attendanceObject == null) {
                    saveVan(postparams, userObject, attendanceDomain, Request.Method.POST);
                }
            }
        }
        if (v == buttonUpdate) {
            JSONObject postparams = new JSONObject();
            try {

                postparams.put("endKm", updateEndKm.getText());
                postparams.put("startKm", attendanceObject.getStartKm());
                postparams.put("shiftName", attendanceObject.getVendor().getName());
                postparams.put("vanNumber", attendanceObject.getVan().getNumber());

                Date date = new Date(String.valueOf(attendanceObject.getDate()));

                postparams.put("logDate", formatter.format(date));
                postparams.put("mobileNumber", userObject.getMobileNumber());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (attendanceObject != null) {

                if (updateValidation()) {
                    saveVan(postparams, userObject, attendanceDomain + "?id=" + attendanceObject.getId(), Request.Method.PUT);
                }
            }

        }
    }

    public boolean updateValidation() {
        if (TextUtils.isEmpty(updateEndKm.getText())) {
            updateEndKm.setError(getString(R.string.error_field_required));
            updateEndKm.requestFocus();
            return false;
        }
        if (!TextUtils.isEmpty(updateEndKm.getText())) {
            if (Double.parseDouble(String.valueOf(attendanceObject.getStartKm())) >= Double.parseDouble(updateEndKm.getText().toString())) {
                updateEndKm.setError("Start Km cannot be greater than End Km!");
                updateEndKm.requestFocus();
                return false;
            }
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (spinnerVan.getSelectedItem() != "Select Van") {
            String item = parent.getItemAtPosition(position).toString();

        }
        if (spinnerVendor.getSelectedItem() != "Select Vendor") {
            String item = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void addItemsOnSpinnerVan(final List<String> vans) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        dataAdapter.add("Select Van");
        dataAdapter.addAll(vans);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVan.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinnerShift(final List<String> vans) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        dataAdapter.add("Select Vendor");
        dataAdapter.addAll(vans);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVendor.setAdapter(dataAdapter);
    }

    private void loadVans(final List<String> vans, String JSON_URL, final String paramName) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray heroArray = new JSONArray(response);
                            if (heroArray.length() > 0) {
                                for (int i = 0; i < heroArray.length(); i++) {
                                    JSONObject heroObject = heroArray.getJSONObject(i);
                                    vans.add(heroObject.getString(paramName));
                                }
                                addItemsOnSpinnerVan(vans);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleTon.getInstance(AddAttendance.this).addToRequestQue(stringRequest);

    }

    private void loadVendors(final List<String> vans, String JSON_URL, final String paramName) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray heroArray = new JSONArray(response);
                            if (heroArray.length() > 0) {
                                for (int i = 0; i < heroArray.length(); i++) {
                                    JSONObject heroObject = heroArray.getJSONObject(i);
                                    vans.add(heroObject.getString(paramName));
                                }
                                addItemsOnSpinnerShift(vans);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleTon.getInstance(AddAttendance.this).addToRequestQue(stringRequest);

    }
}
