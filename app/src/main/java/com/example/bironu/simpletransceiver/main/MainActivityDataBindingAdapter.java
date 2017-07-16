package com.example.bironu.simpletransceiver.main;

import android.databinding.BindingAdapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Bindableアノテーションだけではまかないきれない各種バインドメソッドをまとめるクラス。
 */
public class MainActivityDataBindingAdapter {

    /**
     * 送信先IPアドレスリストが更新された時に呼ばれるメソッド。
     * @param listView 送信先IPアドレスリストを表示しているListView
     * @param forwardIpList 更新された送信先IPアドレスリスト
     */
    @BindingAdapter("items")
    public static void setForwardIpList(ListView listView, List<String> forwardIpList) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
        if(adapter != null) {
            adapter.clear();
            adapter.addAll(forwardIpList);
            adapter.notifyDataSetChanged();
        }
        else {
            adapter = new ArrayAdapter<>(listView.getContext(), android.R.layout.simple_list_item_1, forwardIpList);
            listView.setAdapter(adapter);
        }
    }
}
