package com.example.starbucksook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Step1Activity extends AppCompatActivity {

    TextView step1_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);


        step1_weight = findViewById(R.id.step1_weight);
        step1_weight.setText("Set Text test");         // TODO: 여기에 무게 측정 값 넣으면 된다.



        Button btn = (Button)findViewById(R.id.step1_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step1Activity.this, WeightActivity.class);

                startActivity(intent);

            }
        });
    }



}
