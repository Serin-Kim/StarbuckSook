package com.example.starbucksook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity extends AppCompatActivity {

    ImageView mainImageView;
    TextView title, description;

    String data1, data2;
    int myImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mainImageView = findViewById(R.id.imageView2);
        title = findViewById(R.id.textView3);
        description = findViewById(R.id.textView4);

        getData();
        setData();

        Button btn = (Button)findViewById(R.id.info_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, Step1Activity.class);

                startActivity(intent);

            }
        });
    }

    private void getData(){
        if(getIntent().hasExtra("images") &&
                getIntent().hasExtra("title") && getIntent().hasExtra("description"))
        {
            data1 = getIntent().getStringExtra("title");
            data2 = getIntent().getStringExtra("description");
            myImage = getIntent().getIntExtra("images", 1);

        } else{
            Toast.makeText(this,"No data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setData() {
        title.setText(data1);
        description.setText(data2);
        mainImageView.setImageResource(myImage);
    }

}
