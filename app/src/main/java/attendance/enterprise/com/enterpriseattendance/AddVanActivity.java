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

import com.android.volley.DefaultRetryPolicy;
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

public class AddVanActivity extends AppCompatActivity {

    EditText mEdit, mDetail;
    Button buttonSave;
    CardView cardAdd, cardDetails;
    Van vanObject;

    private static final String vanDomain ="http://34.93.190.224:8080/admin/vans";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_van);
        mEdit = findViewById(R.id.editVanNumber);
        mDetail = findViewById(R.id.viewVanNumber);

        buttonSave = findViewById(R.id.buttonAdd);
        cardAdd = findViewById(R.id.cardAdd);
        cardDetails = findViewById(R.id.cardDetails);
         vanObject = (Van) getIntent().getSerializableExtra("van");

        buttonSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String vanNumber = mEdit.getText().toString().trim();
                if (TextUtils.isEmpty(vanNumber)) {
                    mEdit.setError(getString(R.string.error_field_required));
                } else {
                    if (vanObject == null) {
                        saveVan(vanNumber);
                    } else {
                        editVan(vanObject, vanNumber);
                    }
                }
            }
        });

        if (vanObject != null) {
            cardAdd.setVisibility(View.INVISIBLE);
            cardDetails.setVisibility(View.VISIBLE);
            mEdit.setText(vanObject.getNumber());
            mDetail.setText(vanObject.getNumber());
            ActionBar actionBar = getSupportActionBar(); // or getActionBar();
            getSupportActionBar().setTitle("Van Details"); // set the top title
        } else {
            cardAdd.setVisibility(View.VISIBLE);
            cardDetails.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if (vanObject != null) {
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
                getSupportActionBar().setTitle("Edit Van"); // set the top title
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
                                Van vanObject = (Van) getIntent().getSerializableExtra("van");
                                deleteVan(vanObject.getId(), vanDomain +"?id=" + vanObject.getId());
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


   private void editVan(Van van, String updatedVanNumber) {

       String url = vanDomain + "?id=" + van.getId();

        try {
            JSONObject postparams = new JSONObject();
            postparams.put("number", updatedVanNumber);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                    url, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Van Edited", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), VanActivity.class);
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
            MySingleTon.getInstance(AddVanActivity.this).addToRequestQue(jsonObjReq);

        } catch (Exception e) {
        }
    }

    private void saveVan(String number) {

        try {
            JSONObject postparams = new JSONObject();
            postparams.put("number", number);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    vanDomain, postparams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Van added", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), VanActivity.class);
                            startActivity(i);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(AddVanActivity.this);

// 2. Chain together various setter methods to set the dialog characteristics
                            builder.setMessage("Van Already Added")
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
            MySingleTon.getInstance(AddVanActivity.this).addToRequestQue(jsonObjReq);

        } catch (Exception e) {
        }

    }

    private void deleteVan(Integer id, String JSON_URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), VanActivity.class);
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

        MySingleTon.getInstance(AddVanActivity.this).addToRequestQue(stringRequest);
    }
}
