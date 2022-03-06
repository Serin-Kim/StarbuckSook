package com.example.starbucksook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

        private TextView userName;

        ConstraintLayout layout;

//        RecyclerView recyclerView;


//        String s1[], s2[];
//        int images[] = {R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3, R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3, R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3, R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3};

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_menu);

//          파이어베이스에서 로그인한 사용자 가져오기
            userName = findViewById(R.id.menu_userId);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();

                userName.setText(name + "님, 안녕하세요!");
            }

            layout = findViewById(R.id.menuBox);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MenuActivity.this, Step1Activity.class);

                    startActivity(intent);

                }
            });


//            recyclerView
//            recyclerView = findViewById(R.id.recyclerView);
//
//            s1 = getResources().getStringArray(R.array.animal_name);
//            s2 = getResources().getStringArray(R.array.description);
//
//            MyAdapter myAdapter = new MyAdapter(this, s1, s2, images);
//            recyclerView.setAdapter(myAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }



}
