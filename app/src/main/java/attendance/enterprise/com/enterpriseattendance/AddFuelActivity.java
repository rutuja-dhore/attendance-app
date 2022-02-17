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

public class AddFuelActivity extends AppCompatActivity implements View.OnClickListener {


    EditText txtDate, txtAmount, txtType,txtVan;
    private int mYear, mMonth, mDay, mHour, mMinute, mSec;
    String selectedVan;
    final String vanDomain = "http://34.93.190.224:8080/admin/vans";
    final String fuelDomain = "http://34.93.190.224:8080/fuel";
    CardView cardAdd, cardDetails;
    Fuel fuelObject;

    Button buttonSave;
    EditText txtDate1, txtAmount1, txtType1,txtVan1;
    User userObject = null;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userObject = (User) getIntent().getSerializableExtra("user");
        fuelObject = (Fuel) getIntent().getSerializableExtra("fuel");

        setContentView(R.layout.activity_add_fuel);

        txtDate = (EditText) findViewById(R.id.in_date);
        txtAmount = (EditText) findViewById(R.id.editTextAmount);
        txtType = (EditText) findViewById(R.id.editTextType);
        txtVan = (EditText) findViewById(R.id.editTextVan);
        txtDate.setOnClickListener(this);
        cardAdd = findViewById(R.id.cardAdd);
        cardDetails = findViewById(R.id.cardDetails);

        buttonSave = findViewById(R.id.buttonAdd);

        txtDate1 = (EditText) findViewById(R.id.in_date1);
        txtAmount1 = (EditText) findViewById(R.id.editTextAmount1);
        txtType1 = (EditText) findViewById(R.id.editTextType1);

        txtVan1 = (EditText) findViewById(R.id.spinnerVan1);

        buttonSave.setOnClickListener(this);

        if (fuelObject != null) {
            cardAdd.setVisibility(View.INVISIBLE);
            cardDetails.setVisibility(View.VISIBLE);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            txtDate1.setText(formatter.format(fuelObject.getDate()));
            txtAmount1.setText(String.valueOf(fuelObject.getAmount()));
            txtType1.setText(String.valueOf(fuelObject.getType()));
            txtVan1.setText(fuelObject.getVan().getNumber());

            txtDate.setText(formatter.format(fuelObject.getDate()));
            txtAmount.setText(String.valueOf(fuelObject.getAmount()));
            txtType.setText(String.valueOf(fuelObject.getType()));

            ActionBar actionBar = getSupportActionBar(); // or getActionBar();
            getSupportActionBar().setTitle("Fuel Details"); // set the top title
        } else {
            cardAdd.setVisibility(View.VISIBLE);
            cardDetails.setVisibility(View.INVISIBLE);
            txtDate.setText(formatter.format(new Date()));
            txtVan.setText(userObject.getVanNumber());
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (fuelObject != null) {
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
       return false;
    }

    private boolean inputValidated() {

        if (TextUtils.isEmpty(txtDate.getText())) {
            txtDate.setError(getString(R.string.error_field_required));
            txtDate.requestFocus();
            return false;
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
                            Toast.makeText(getApplicationContext(), "Fuel added", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), FuelActivity.class);
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

            MySingleTon.getInstance(AddFuelActivity.this).addToRequestQue(jsonObjReq);

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
                    postparams.put("type", txtType.getText());
                    postparams.put("amount", txtAmount.getText());
                    postparams.put("vanNumber", txtVan.getText());
                    postparams.put("logDate", txtDate.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (fuelObject == null) {
                    saveVan(postparams, userObject, fuelDomain, Request.Method.POST);
                }
            }
        }
    }
}
