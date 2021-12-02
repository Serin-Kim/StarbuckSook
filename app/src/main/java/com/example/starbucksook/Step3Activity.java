package com.example.starbucksook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Step3Activity extends AppCompatActivity {

    TextView step3_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);


        step3_weight = findViewById(R.id.step3_weight);
        step3_weight.setText("원두 무게: ");         // TODO: 여기에 무게 측정 값 넣으면 된다.



        Button btn = (Button)findViewById(R.id.step3_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step3Activity.this, Step4Activity.class);

                startActivity(intent);

            }
        });
    }



}
