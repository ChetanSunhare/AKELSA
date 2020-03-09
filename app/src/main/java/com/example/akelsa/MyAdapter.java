package com.example.akelsa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class MyAdapter extends ArrayAdapter {
    Context context;
    ArrayList myDataSet;


    public MyAdapter(Context context, ArrayList myDataSet) {
        super(context, R.layout.list_view,R.id.text2,myDataSet);
        this.context = context;
        this.myDataSet = myDataSet;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        LayoutInflater li= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = li.inflate(R.layout.list_view,parent,false);
        TextView tx1= convertView.findViewById(R.id.text1);
        TextView tx2= convertView.findViewById(R.id.text2);
        if(position == 0)
        {
            tx1.setText(""+myDataSet.get(position));
        }
        else
        {
            Toast.makeText(context, "tx2 called" , Toast.LENGTH_SHORT).show();
            tx2.setText(""+myDataSet.get(position));
        }

        return convertView;
    }
}
