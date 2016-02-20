package com.joinlang.yury.checkpassportbyfms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPassportRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPassportRequest = (Button) findViewById(R.id.btnCheckPassport);
        btnPassportRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCheckPassport:
                startActivity(new Intent(MainActivity.this, PassportActivity.class));
                break;
        }
    }
}