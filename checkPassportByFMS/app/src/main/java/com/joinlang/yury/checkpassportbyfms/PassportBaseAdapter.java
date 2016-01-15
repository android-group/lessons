package com.joinlang.yury.checkpassportbyfms;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class PassportBaseAdapter extends BaseAdapter {

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView series;
    TextView number;
    TextView result;

    public PassportBaseAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_col_row, null);

            series = (TextView) convertView.findViewById(R.id.series);
            number = (TextView) convertView.findViewById(R.id.number);
            result = (TextView) convertView.findViewById(R.id.result);
        }

        HashMap<String, String> map = list.get(position);
        series.setText(map.get(PassportDBHelper.COLUMN_SERIES));
        number.setText(map.get(PassportDBHelper.COLUMN_NUMBER));
        result.setText(map.get(PassportDBHelper.COLUMN_RESULT));

        return convertView;
    }
}