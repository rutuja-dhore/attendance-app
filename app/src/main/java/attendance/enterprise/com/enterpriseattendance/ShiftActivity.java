package attendance.enterprise.com.enterpriseattendance;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShiftActivity extends AppCompatActivity {


    //the URL having the json data
    private static final String vendorDomain = "http://34.93.190.224:8080/admin/vendors";

    //listview object
    ListView listView;

    //the hero list where we will store all the hero objects after parsing json
    List<Vendor> herolist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);
        final TextView mTextView = (TextView) findViewById(R.id.text);
        //initializing listview and hero list
        listView = (ListView) findViewById(R.id.listView);
        herolist = new ArrayList<>();

        FloatingActionButton buttonAdd = (FloatingActionButton) findViewById(R.id.buttonAdd);

        buttonAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), AddShiftActivity.class);
                startActivity(i);
                finish();
            }
        });
        //this method will fetch and parse the data
        loadHeroList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Vendor vendorObject = (Vendor) parent.getItemAtPosition(position);

                Intent i = new Intent(getApplicationContext(),AddShiftActivity.class);
                i.putExtra("shift", vendorObject);
                startActivity(i);
                finish();
            }
        });
    }

    private void loadHeroList() {
        //getting the progressbar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //making the progressbar visible
        progressBar.setVisibility(View.VISIBLE);

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, vendorDomain,
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

                                    //creating a van object and giving them the values from json object
                                    Vendor van = new Vendor(heroObject.getInt("id"), heroObject.getString("name"));

                                    //adding the van to herolist
                                    herolist.add(van);
                                }

                                //creating custom adapter object
                                ShiftListViewAdapter adapter = new ShiftListViewAdapter(herolist, getApplicationContext());

                                //adding the adapter to listview
                                listView.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
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

        MySingleTon.getInstance(ShiftActivity.this).addToRequestQue(stringRequest);

    }
}

