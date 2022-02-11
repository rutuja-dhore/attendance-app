package attendance.enterprise.com.enterpriseattendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONObject;

public class AddShiftActivity extends AppCompatActivity {
    CardView cardAdd, cardDetails;
    Vendor vendorObject;

    private static final String vendorDomain = "http://34.93.190.224:8080/admin/vendors";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shift);
        final EditText mEdit = findViewById(R.id.editTextName);
        EditText viewShiftName = findViewById(R.id.viewShiftName);
        Button buttonSave = findViewById(R.id.buttonAdd);
        vendorObject = (Vendor) getIntent().getSerializableExtra("shift");
        cardAdd = findViewById(R.id.cardAdd);
        cardDetails = findViewById(R.id.cardDetails);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String shiftName = mEdit.getText().toString().trim();

                if (TextUtils.isEmpty(shiftName)) {
                    mEdit.setError(getString(R.string.error_field_required));
                } else {
                    if (vendorObject == null) {
                        saveShift(shiftName);
                    } else {
                        editShift(vendorObject.getId(), shiftName);
                    }
                }
            }
        });

        if (vendorObject != null) {
            cardAdd.setVisibility(View.INVISIBLE);
            cardDetails.setVisibility(View.VISIBLE);
            mEdit.setText(vendorObject.getName());
            viewShiftName.setText(vendorObject.getName());
            ActionBar actionBar = getSupportActionBar(); // or getActionBar();
            getSupportActionBar().setTitle("Vendor Details"); // set the top title
        } else {
            cardAdd.setVisibility(View.VISIBLE);
            cardDetails.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (vendorObject != null) {
            menu.findItem(R.id.itemEdit).setVisible(true);
            menu.findItem(R.id.itemDelete).setVisible(true);
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
                getSupportActionBar().setTitle("Edit Vendor"); // set the top title
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
                                Vendor vendorObject = (Vendor) getIntent().getSerializableExtra("shift");
                                deleteVan(vendorObject.getId(), vendorDomain + "?id=" + vendorObject.getId());
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

    private void saveShift(String name) {
        try {
            JSONObject postparams = new JSONObject();
            postparams.put("name", name);


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    vendorDomain, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Vendor added", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), ShiftActivity.class);
                            startActivity(i);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(AddShiftActivity.this);

// 2. Chain together various setter methods to set the dialog characteristics
                            builder.setMessage("Vendor Already Added")
                                    .setTitle("Error");

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    dialog.cancel();
                                }
                            });

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
                            AlertDialog dialog = builder.create();

                            dialog.show();
                        }
                    });

            MySingleTon.getInstance(AddShiftActivity.this).addToRequestQue(jsonObjReq);


        } catch (Exception e) {
        }

    }

    private void editShift(Integer id, String name) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = vendorDomain + "?id=" + id;

        try {
            JSONObject postparams = new JSONObject();
            postparams.put("name", name);


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                    url, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Shift added", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), ShiftActivity.class);
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

// Adding the request to the queue along with a unique string tag
            queue.add(jsonObjReq);

        } catch (Exception e) {
        }

    }

    private void deleteVan(Integer id, String JSON_URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), ShiftActivity.class);
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

        MySingleTon.getInstance(AddShiftActivity.this).addToRequestQue(stringRequest);
    }
}
