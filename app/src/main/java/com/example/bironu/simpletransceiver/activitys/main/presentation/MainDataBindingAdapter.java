package com.example.bironu.simpletransceiver.activitys.main.presentation;

import android.content.Context;
import android.database.Cursor;
import android.databinding.BindingAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bironu.simpletransceiver.data.db.SendTargetTable;

/**
 * Bindableアノテーションだけではまかないきれない各種バインドメソッドをまとめるクラス。
 */
public class MainDataBindingAdapter
{

    private static class ForwardIpListCursorAdapter extends CursorAdapter
    {

        public ForwardIpListCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((TextView) view.findViewById(android.R.id.text1)).setText(cursor.getString(cursor.getColumnIndex(SendTargetTable.COLUMN_ADDRESS)));
        }
    }

    /**
     * 送信先IPアドレスリストが更新された時に呼ばれるメソッド。
     *
     * @param listView 送信先IPアドレスリストを表示しているListView
     * @param cursor   更新された送信先IPアドレスリスト
     */
    @BindingAdapter("items")
    public static void setForwardIpList(ListView listView, Cursor cursor) {
        ListAdapter adapter = listView.getAdapter();
        if (!(adapter instanceof CursorAdapter)) {
            adapter = new ForwardIpListCursorAdapter(listView.getContext(), cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            listView.setAdapter(adapter);
        }
        else {
            ((CursorAdapter) adapter).swapCursor(cursor);
        }
    }
}
