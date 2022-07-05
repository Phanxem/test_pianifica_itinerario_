package com.example.test_Pianifica_Itinerario.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.test_Pianifica_Itinerario.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void clickTest(View view) {
        Log.d("TAS","textView");
    }

    public void clickTest0(View view) {
        Log.d("TAS","relativeLayout");
    }
}