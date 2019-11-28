package com.ccb.addressselectview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.ccb.addressselect.view.AddressSelectView;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private AddressSelectView addressSelectView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        addressSelectView = findViewById(R.id.addressSelect);
        addressSelectView.setOnSelectConfirmListEner(new AddressSelectView.OnSelectConfirmListEner() {
            @Override
            public void onConfirm(String s) {
                tv.setText(s);
            }
        });
    }
}
