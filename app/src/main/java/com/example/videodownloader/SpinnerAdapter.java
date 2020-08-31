package com.example.videodownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    private ArrayList<String> titlelist;
    private Context context;

    public SpinnerAdapter(Context c,ArrayList<String> list){
        context=c;
        titlelist=list;
    }

    @Override
    public int getCount() {
        return titlelist.size();
    }

    @Override
    public Object getItem(int i) {
        return titlelist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        view=layoutInflater.inflate(R.layout.spinner_item, null);
        if(view!=null){
            TextView item=view.findViewById(R.id.textView);
            item.setText(titlelist.get(i));
        }
        return view;
    }
}
