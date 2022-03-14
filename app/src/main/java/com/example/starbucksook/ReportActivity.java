package com.example.starbucksook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
     public float weight_1, weight_2, weight_3, weight_4, weight_5, total_weight;
     public String total_time;
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
        total_time = intent.getStringExtra("total_time");


        report_countdown_text = findViewById(R.id.report_countdown_text);
        report_step_water = findViewById(R.id.report_step_water);
        report_countdown_text.setText(total_time);
//        report_step_water.setText(Float.toString(total_weight));


        float groupSpace = 0.24f; // 그룹 그래프간 간격
        float barSpace = 0.08f; // 각 그래프간 간격
        float barWidth = 0.25f; // 각 그래프의 너비

//        int groupCount = seekBarX.getProgress() + 1;
        int start = 0;
        int last = start + 100;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        chart = findViewById(R.id.barchart);

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();




        values1.add(new BarEntry(0, (float) 120));  // 총 소요 시간
        values1.add(new BarEntry(1, (float) 320));  // 총 추출 양
        values1.add(new BarEntry(2, (float) 55));   // 추출 3
        values1.add(new BarEntry(3, (float) 100));  // 추출 2
        values1.add(new BarEntry(4, (float) 150));  // 추출 1
        values1.add(new BarEntry(5, (float) 15));   // 뜸 들이기
        values1.add(new BarEntry(6, (float) 20));   // 커피 무게



//        for (int i = 0; i < 7; i++) {
////            values1.add(new BarEntry(i, (float) (Math.random() * 10)));
//            values2.add(new BarEntry(i, (float) (Math.random() * 20)));
//        }
        values2.add(new BarEntry(0, Float.parseFloat(total_time)));
        values2.add(new BarEntry(1, total_weight));
        values2.add(new BarEntry(2, weight_5));
        values2.add(new BarEntry(3, weight_4));
        values2.add(new BarEntry(4, weight_3));
        values2.add(new BarEntry(5, weight_2));
        values2.add(new BarEntry(6, weight_1));

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

//        ArrayList<String> xAxisVals = new ArrayList<>();
//        xAxisVals.add("1");
//        xAxisVals.add("2");
//        xAxisVals.add("3");
//        xAxisVals.add("4");
//        xAxisVals.add("5");
//        xAxisVals.add("6");





        String[] xAxisVals = new String[]{"총 소요 시간", "총 추출량", "추출 3", "추출 2","추출 1", "뜸 들이기", "커피", "test1", "test2"};

        // String setter in x-Axis
//        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisVals));

//        xAxis.setLabelCount(5, true);
//        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisVals));





        // y축 (left)
        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setAxisMinimum(0);


        // y축 (right)
        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setAxisMinimum(0);
//        yRAxis.setDrawLabels(false);


        // chart가 그려질 때 애니메이션
        chart.animateXY(0,800);


        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
//        chart.getXAxis().setAxisMaximum(start + chart.getBarData().getGroupWidth(groupSpace, barSpace) * 7);
        chart.groupBars(start, groupSpace, barSpace);
        chart.invalidate(); // refresh

    }


}