package com.howie.grouping.example.module1.business;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.howie.grouping.core.Grouping;
import com.howie.grouping.example.module1.R;
import com.howie.grouping.example.module2.api.service.M2RouterService;

public class Module1MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m1_activity_main);

        initView();
    }

    private void initView() {
        findViewById(R.id.btnGoM2).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Grouping.getInstance().getService(M2RouterService.class).launchM2MainActivity(Module1MainActivity.this);
            }
        });
    }
}
