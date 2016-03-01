package ru.android_studio.check_passport;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.android_studio.check_passport.model.TypicalResponse;

import java.util.List;
import java.util.Map;

public class PassportBaseAdapter extends BaseAdapter {

    private List<Map<String, String>> list;
    private Activity activity;
    private TextView passport;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_col_header, null);

            passport = (TextView) convertView.findViewById(R.id.passport);
            result = (TextView) convertView.findViewById(R.id.result);
        }

        Map<String, String> map = list.get(position);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(map.get(PassportDBHelper.COLUMN_SERIES));
        stringBuilder.append(" ");
        stringBuilder.append(map.get(PassportDBHelper.COLUMN_NUMBER));
        passport.setText(stringBuilder);

        String resultStr = map.get(PassportDBHelper.COLUMN_RESULT);
        result.setText(resultStr);
        if (position != 0) {
            result.setTextColor(TypicalResponse.findByResult(resultStr).getColor(inflater.getContext()));
        }

        return convertView;
    }

    public void clearList() {
        list.clear();
    }
}