package com.example.ruralroadworks;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
    
    private Activity activity;
    private String[] Values;
    private static LayoutInflater inflater=null;
    
    public MyAdapter(Activity a, int spinnerValue, String[] code) 
    {
        activity = a;
        Values = code;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return Values.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.spinner_value, null);

        TextView text=(TextView)vi.findViewById(R.id.textView1);;
        text.setText(Values[position]);
               
        return vi;
    }
}