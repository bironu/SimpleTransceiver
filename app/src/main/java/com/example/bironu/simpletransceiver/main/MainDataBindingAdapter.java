package com.example.bironu.simpletransceiver.main;

import android.databinding.BindingAdapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 *
 */
public class MainDataBindingAdapter {
    @BindingAdapter("items")
    public static void setForwardIpList(ListView listView, List<String> forwardIpList) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        if(adapter == null) {
            adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, forwardIpList);
            listView.setAdapter(adapter);
        }
        else {
            adapter.clear();
            adapter.addAll(forwardIpList);
            adapter.notifyDataSetChanged();
        }
    }
}
