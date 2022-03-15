package com.example.starbucksook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    // 무게 데이터 받아옴
     public float weight_1, weight_2, weight_3, weight_4, weight_5, total_weight, total_time_float;
     public double total_time;
     TextView report_countdown_text;
     TextView report_step_water;

    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        weight_1 = intent.getFloatExtra("weight_1", 0);
        weight_2 = intent.getFloatExtra("weight_2", 0);
        weight_3 = intent.getFloatExtra("weight_3", 0);
        weight_4 = intent.getFloatExtra("weight_4", 0);
        weight_5 = intent.getFloatExtra("weight_5", 0);
        total_weight = intent.getFloatExtra("total_weight", 0);
        total_time = intent.getDoubleExtra("total_time", 0);
        total_time_float = (float) total_time;


        float groupSpace = 0.24f; // 그룹 그래프간 간격
        float barSpace = 0.08f; // 각 그래프간 간격
        float barWidth = 0.25f; // 각 그래프의 너비

        int start = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);


        report_countdown_text = findViewById(R.id.report_countdown_text);
        report_step_water = findViewById(R.id.report_step_water);
        report_countdown_text.setText(String.valueOf(Math.round(total_time)));
        report_step_water.setText(String.valueOf(Math.round(total_weight)));



        chart = findViewById(R.id.barchart);

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();


        values1.add(new BarEntry(0, (float) 55));   // 추출 3
        values1.add(new BarEntry(1, (float) 100));  // 추출 2
        values1.add(new BarEntry(2, (float) 150));  // 추출 1
        values1.add(new BarEntry(3, (float) 15));   // 뜸 들이기
        values1.add(new BarEntry(4, (float) 20));   // 커피 무게

        values2.add(new BarEntry(0, weight_5));
        values2.add(new BarEntry(1, weight_4));
        values2.add(new BarEntry(2, weight_3));
        values2.add(new BarEntry(3, weight_2));
        values2.add(new BarEntry(4, weight_1));


        BarDataSet set1, set2;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) chart.getData().getDataSetByIndex(1);
            set1.setValues(values1);
            set2.setValues(values2);

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            // create 2 DataSets
            set1 = new BarDataSet(values1, "Recipe");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(values2, "Your Data");
            set2.setColor(Color.rgb(164, 228, 251));

            BarData data = new BarData(set1, set2);
            data.setValueFormatter(new LargeValueFormatter());
//            data.setValueTypeface(tfLight);

            chart.setData(data);
        }

        chart.getBarData().setBarWidth(barWidth); // specify the width each bar should have
        chart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        chart.setTouchEnabled(false); // chart 터치 막기

        // x축
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(start);
        xAxis.setDrawAxisLine(false);
//        xAxis.setCenterAxisLabels(true); // 라벨 가운데 정렬
//        xAxis.setTextSize(10f); // 라벨 크기
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X축 데이터 표시 위치





        String[] xAxisVals = new String[]{"추출 3", "추출 2","추출 1", "뜸 들이기", "커피"};

        // String setter in x-Axis
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisVals));

        // y축 (left)
        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setAxisMinimum(0);

        // y축 (right)
        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setAxisMinimum(0);

        // chart가 그려질 때 애니메이션
        chart.animateXY(0,800);


        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart.groupBars(start, groupSpace, barSpace);
        chart.invalidate(); // refresh
    }
}