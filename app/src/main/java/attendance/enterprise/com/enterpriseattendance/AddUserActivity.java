package attendance.enterprise.com.enterpriseattendance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddUserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private static final String TAG = "SampleActivity";
    CardView cardAdd, cardDetails;
    User userObject;
    private static final String userDomain ="http://34.93.190.224:8080/admin/users";
    final String vendorDomain = "http://34.93.190.224:8080/admin/vendors";
    final String vanDomain = "http://34.93.190.224:8080/admin/vans";

    Spinner spinnerVendor,spinnerVan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        final EditText mEditFirstName = findViewById(R.id.editTextFirstName);
        final EditText mEditLastName = findViewById(R.id.editTextLastName);
        final EditText mEditMobileNumber = findViewById(R.id.editTextMobileNumber);
        final EditText mEditPassword = findViewById(R.id.editTextPassword);

        final EditText mEditFirstName1 = findViewById(R.id.editTextFirstName1);
        final EditText mEditLastName1 = findViewById(R.id.editTextLastName1);
        final EditText mEditMobileNumber1 = findViewById(R.id.editTextMobileNumber1);
        final EditText mEditPassword1 = findViewById(R.id.editTextPassowrd1);
        final EditText mEditVan = findViewById(R.id.editTextVan);

        spinnerVendor = (Spinner) findViewById(R.id.spinnerVendor);
        List<String> vendors = new ArrayList<String>();
        loadVendors(vendors, vendorDomain, "name");
        spinnerVendor.setOnItemSelectedListener(this);

        spinnerVan = (Spinner) findViewById(R.id.spinnerVan);
        List<String> vans = new ArrayList<String>();
        loadVans(vans, vanDomain, "number");
        spinnerVan.setOnItemSelectedListener(this);

        Button buttonSave = findViewById(R.id.buttonAdd);
        cardAdd = findViewById(R.id.cardAdd);
        cardDetails = findViewById(R.id.cardDetails);

        userObject = (User) getIntent().getSerializableExtra("user");

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String firstName = mEditFirstName.getText().toString().trim();
                String lastName = mEditLastName.getText().toString().trim();
                String mobileNumber = mEditMobileNumber.getText().toString().trim();
                String password = mEditPassword.getText().toString().trim();
                String selectedVendor = spinnerVendor.getSelectedItem().toString();

                boolean validationError = false;
                if (TextUtils.isEmpty(firstName)) {
                    mEditFirstName.setError(getString(R.string.error_field_required));
                    validationError = true;
                }
                if (TextUtils.isEmpty(lastName)) {
                    mEditLastName.setError(getString(R.string.error_field_required));
                    validationError = true;
                }
                if (TextUtils.isEmpty(mobileNumber)) {
                    mEditMobileNumber.setError(getString(R.string.error_field_required));
                    validationError = true;
                } else if (mobileNumber.length() != 10) {
                    mEditMobileNumber.setError(getString(R.string.error_invalid_mobileNumber));
                    validationError = true;
                }

                if (TextUtils.isEmpty(password)) {
                    mEditPassword.setError(getString(R.string.error_field_required));
                    validationError = true;
                }

                if (!validationError) {
                    if (userObject == null) {
                        saveUser(userDomain,
                                firstName, lastName, mobileNumber, password, selectedVendor,Request.Method.POST);
                    } else {
                        saveUser(userDomain+"?id=" + userObject.getId(),
                                firstName, lastName, mobileNumber, password,selectedVendor, Request.Method.PUT);
                    }
                }
            }
        });

        if (userObject != null) {
            cardAdd.setVisibility(View.INVISIBLE);
            cardDetails.setVisibility(View.VISIBLE);
            mEditFirstName.setText(userObject.getFirstName());
            mEditLastName.setText(userObject.getLastName());
            mEditMobileNumber.setText(userObject.getMobileNumber());
            mEditPassword.setText(userObject.getPassword());
            mEditFirstName1.setText(userObject.getFirstName());
            mEditLastName1.setText(userObject.getLastName());
            mEditMobileNumber1.setText(userObject.getMobileNumber());
            mEditPassword1.setText(userObject.getPassword());
            mEditVan.setText(userObject.getVanNumber());

            ActionBar actionBar = getSupportActionBar(); // or getActionBar();
            getSupportActionBar().setTitle("User Details"); // set the top title

        } else {
            cardAdd.setVisibility(View.VISIBLE);
            cardDetails.setVisibility(View.INVISIBLE);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (userObject != null) {
            menu.findItem(R.id.itemEdit).setVisible(true);
            menu.findItem(R.id.itemDelete).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemEdit:
                ActionBar actionBar = getSupportActionBar(); // or getActionBar();
                getSupportActionBar().setTitle("Edit User"); // set the top title
                cardAdd.setVisibility(View.VISIBLE);
                cardDetails.setVisibility(View.INVISIBLE);
                item.setVisible(false);
                return true;

            case R.id.itemDelete:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure, You want to Delete ? ");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                User vanObject = (User) getIntent().getSerializableExtra("user");
                                deleteVan(vanObject.getId(), userDomain +"?id=" + userObject.getId());
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void saveUser(String url, String firstName, String lastName, String mobileNumber, String password, String selectedVendor, int methodType) {

        try {
            JSONObject postparams = new JSONObject();
            postparams.put("firstName", firstName);
            postparams.put("lastName", lastName);
            postparams.put("mobileNumber", mobileNumber);
            postparams.put("password", password);

            JSONArray vendors = new JSONArray();
            vendors.put(selectedVendor);
            postparams.put("vendors", vendors);


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(methodType,
                    url, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "User added", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), UserActivity.class);
                            startActivity(i);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            Log.v(TAG, error.toString());
                        }
                    });

            MySingleTon.getInstance(AddUserActivity.this).addToRequestQue(jsonObjReq);

        } catch (Exception e) {
        }

    }


    private void deleteVan(Long id, String JSON_URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), UserActivity.class);
                        startActivity(i);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        MySingleTon.getInstance(AddUserActivity.this).addToRequestQue(stringRequest);
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
                                addItemsOnSpinnerVendor(vans);
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
        MySingleTon.getInstance(AddUserActivity.this).addToRequestQue(stringRequest);

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
        MySingleTon.getInstance(AddUserActivity.this).addToRequestQue(stringRequest);

    }
    public void addItemsOnSpinnerVendor(final List<String> vans) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        dataAdapter.add("Select Vendor");
        dataAdapter.addAll(vans);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVendor.setAdapter(dataAdapter);
    }

    public void addItemsOnSpinnerVan(final List<String> vans) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        dataAdapter.add("Select Van");
        dataAdapter.addAll(vans);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVan.setAdapter(dataAdapter);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (spinnerVendor.getSelectedItem() != "Select Vendor") {
            String item = parent.getItemAtPosition(position).toString();

        }
        if (spinnerVan.getSelectedItem() != "Select Van") {
            String item = parent.getItemAtPosition(position).toString();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
