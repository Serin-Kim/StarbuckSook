package com.example.starbucksook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MenuActivity extends AppCompatActivity {

        RecyclerView recyclerView;

        String s1[], s2[];
        int images[] = {R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3, R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3, R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3, R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3};
//        int images[] = {R.drawable.coffee1, R.drawable.coffee2, R.drawable.coffee3, R.drawable.black_cat, R.drawable.cow, R.drawable.deer, R.drawable.dog, R.drawable.eagle, R.drawable.elephant,  R.drawable.fish_in_glass, R.drawable.hunter, R.drawable.lion};

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_menu);

            recyclerView = findViewById(R.id.recyclerView);

//            s1 = getResources().getStringArray(R.array.menu_name);
//            s2 = getResources().getStringArray(R.array.menu_description);
            s1 = getResources().getStringArray(R.array.animal_name);
            s2 = getResources().getStringArray(R.array.description);

            MyAdapter myAdapter = new MyAdapter(this, s1, s2, images);
            recyclerView.setAdapter(myAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }



}
