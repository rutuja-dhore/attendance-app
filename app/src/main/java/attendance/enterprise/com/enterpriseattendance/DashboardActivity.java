package attendance.enterprise.com.enterpriseattendance;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sp;
    Button buttonAttendance, buttonFuel;

    User userObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        setContentView(R.layout.activity_dashboard);
        User userObject = (User) getIntent().getSerializableExtra("user");

        if (userObject == null) {
            userObject = Helper.getUserObject(sp);
        }
        Button buttonVan = (Button) findViewById(R.id.buttonVan);

        buttonVan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), VanActivity.class);
                startActivity(i);

            }
        });

        Button buttonShift = (Button) findViewById(R.id.buttonVendor);

        buttonShift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), ShiftActivity.class);
                startActivity(i);
            }
        });


        Button buttonUsers = (Button) findViewById(R.id.buttonUsers);

        buttonUsers.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(i);

            }
        });

        buttonAttendance = (Button) findViewById(R.id.buttonAttendance);
        buttonAttendance.setOnClickListener(this);

        if (userObject != null && userObject.getRole().contains("ROLE_USER")) {
            buttonVan.setVisibility(View.INVISIBLE);
            buttonShift.setVisibility(View.INVISIBLE);
            buttonUsers.setVisibility(View.INVISIBLE);
        }

        buttonFuel = (Button) findViewById(R.id.buttonFuel);
        buttonFuel.setOnClickListener(this);

        if (userObject != null && userObject.getRole().contains("ROLE_USER")) {
            buttonVan.setVisibility(View.INVISIBLE);
            buttonShift.setVisibility(View.INVISIBLE);
            buttonUsers.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == buttonAttendance) {
            Intent i = new Intent(getApplicationContext(), AttendanceActivity.class);
            userObject = Helper.getUserObject(sp);

            i.putExtra("user", userObject);
            startActivity(i);
        }
        else  if (v == buttonFuel) {
            Intent i = new Intent(getApplicationContext(), FuelActivity.class);
            userObject = Helper.getUserObject(sp);

            i.putExtra("user", userObject);
            startActivity(i);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.itemLogout).setVisible(true);
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
            case R.id.itemLogout:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Are you sure, You want to Logout ? ");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();                            }
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

}
