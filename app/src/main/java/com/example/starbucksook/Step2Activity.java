package com.example.starbucksook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Step2Activity extends AppCompatActivity {

    TextView step2_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);


        step2_weight = findViewById(R.id.step2_weight);
        step2_weight.setText("예열된 드립 서버의 물을 비웁니다. 현재 무게: ");         // TODO: 여기에 무게 측정 값 넣으면 된다.



        Button btn = (Button)findViewById(R.id.step2_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step2Activity.this, Step3Activity.class);

                startActivity(intent);

            }
        });
    }



}
