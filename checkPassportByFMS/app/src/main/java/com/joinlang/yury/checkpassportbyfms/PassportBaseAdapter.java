package com.joinlang.yury.checkpassportbyfms;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joinlang.yury.checkpassportbyfms.model.TypicalResponse;

import java.util.List;
import java.util.Map;

public class PassportBaseAdapter extends BaseAdapter {

    private List<Map<String, String>> list;
    private Activity activity;
    private TextView series;
    private TextView number;
    private TextView result;

    public PassportBaseAdapter(Activity activity, List<Map<String, String>> list) {
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

    /* @TODO
        Card View v.4 support
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_col_row, null);

            series = (TextView) convertView.findViewById(R.id.series);
            number = (TextView) convertView.findViewById(R.id.number);
            result = (TextView) convertView.findViewById(R.id.result);
        }

        Map<String, String> map = list.get(position);
        series.setText(map.get(PassportDBHelper.COLUMN_SERIES));
        number.setText(map.get(PassportDBHelper.COLUMN_NUMBER));

        String resultStr = map.get(PassportDBHelper.COLUMN_RESULT);
        result.setText(resultStr);
        
        if(TypicalResponse.findByResult(resultStr).isValid()) {
            result.setTextColor(Color.GREEN);
        } else {
            result.setTextColor(Color.RED);
        }


        return convertView;
    }

    public void clearList() {
        list.clear();
    }
}