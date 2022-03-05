package com.example.starbucksook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.*;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Step3Activity extends AppCompatActivity {
    TextView step3_weight;
    public String base_weight3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);

        step3_weight = findViewById(R.id.step3_weight);
        Button btn = (Button)findViewById(R.id.step3_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step3Activity.this, Step4Activity.class);
                startActivity(intent);
            }
        });
        // step3_weight.setText("원두 무게: ");         // TODO: 여기에 무게 측정 값 넣으면 된다.
    }


    public void onProgressUpdate(String... params) {
        step3_weight.setText(""); // Clear the chat box
        float now_weight = Float.parseFloat(params[0]);
        float weight3;


        weight3 = now_weight-Float.parseFloat(base_weight3);
        step3_weight.append("원두 무게: 과연   " + Float.toString(weight3));


        Intent intent = new Intent(Step3Activity.this, Step4Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

    }

}



//package com.example.starbucksook;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class Step3Activity extends AppCompatActivity {
//
//    TextView step3_weight;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_step3);
//
//
//        step3_weight = findViewById(R.id.step3_weight);
//        step3_weight.setText("원두 무게: ");         // TODO: 여기에 무게 측정 값 넣으면 된다.
//
//
//
//        Button btn = (Button)findViewById(R.id.step3_btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Step3Activity.this, Step4Activity.class);
//
//                startActivity(intent);
//
//            }
//        });
//    }
//
//
//
//}
