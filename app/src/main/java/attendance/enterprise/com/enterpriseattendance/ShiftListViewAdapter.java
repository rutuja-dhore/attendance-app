package attendance.enterprise.com.enterpriseattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Belal on 9/5/2017.
 */

public class ShiftListViewAdapter extends ArrayAdapter<Vendor> {

    //the hero list that will be displayed
    private List<Vendor> heroList;

    //the context object
    private Context mCtx;

    //here we are getting the herolist and context
    //so while creating the object of this adapter class we need to give herolist and context
    public ShiftListViewAdapter(List<Vendor> heroList, Context mCtx) {
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
        Vendor vendor = heroList.get(position);

        //setting hero values to textviews
        textViewName.setText(vendor.getName());

        //returning the listitem
        return listViewItem;
    }
}
