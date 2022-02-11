package attendance.enterprise.com.enterpriseattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Belal on 9/5/2017.
 */

public class AttendanceListViewAdapter extends ArrayAdapter<Attendance> {

    //the hero list that will be displayed
    private List<Attendance> heroList;

    //the context object
    private Context mCtx;

    //here we are getting the herolist and context
    //so while creating the object of this adapter class we need to give herolist and context
    public AttendanceListViewAdapter(List<Attendance> heroList, Context mCtx) {
        super(mCtx, R.layout.activity_list_view, heroList);
        this.heroList = heroList;
        this.mCtx = mCtx;
    }

    //this method will return the list item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //getting the layoutinflater
        LayoutInflater inflater = LayoutInflater.from(mCtx);

        //creating a view with our xml layout
        View listViewItem = inflater.inflate(R.layout.activity_list_view, null, true);

        //getting text views
        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewImageUrl = listViewItem.findViewById(R.id.textViewImageUrl);

        //Getting the hero for the specified position
        Attendance hero = heroList.get(position);

        //setting hero values to textviews

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        textViewName.setText(formatter.format(hero.getDate()));
        textViewImageUrl.setText("User : " + hero.getFirstName());
        //returning the listitem
        return listViewItem;
    }
}
