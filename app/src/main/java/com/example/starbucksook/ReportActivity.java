package com.example.starbucksook;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;

public class ReportActivity extends AppCompatActivity {

    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        float groupSpace = 0.2f; // 그룹 그래프간 간격
        float barSpace = 0.03f; // 각 그래프간 간격
        float barWidth = 0.2f; // 각 그래프의 너비

//        int groupCount = seekBarX.getProgress() + 1;
        int start = 0;
        int last = start + 100;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        chart = findViewById(R.id.barchart);

        ArrayList<BarEntry> values1 = new ArrayList<>();
        ArrayList<BarEntry> values2 = new ArrayList<>();

        values1.add(new BarEntry(0, (float) 15));
        values1.add(new BarEntry(1, (float) 150));
        values1.add(new BarEntry(2, (float) 100));
        values1.add(new BarEntry(3, (float) 50));

        for (int i = 0; i < 4; i++) {
//            values1.add(new BarEntry(i, (float) (Math.random() * 10)));
            values2.add(new BarEntry(i, (float) (Math.random() * 20)));
        }

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
            set1 = new BarDataSet(values1, "Original");
            set1.setColor(Color.rgb(104, 241, 175));
            set2 = new BarDataSet(values2, "Our Data");
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
        xAxis.setLabelCount(5);
        xAxis.setCenterAxisLabels(true); // 라벨 가운데 정렬
        xAxis.setTextSize(12f); // 라벨 크기
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X축 데이터 표시 위치


        String[] xAxisVals = new String[]{"1단계", "2단계", "3단계", "4단계"};

        // String setter in x-Axis
//        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisVals));
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisVals));


        xAxis.setGranularity(0.01f);

        // y축 (left)
        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setAxisMinimum(0);


        // y축 (right)
        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setAxisMinimum(0);
        yRAxis.setDrawLabels(false);


        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        chart.getXAxis().setAxisMaximum(start + chart.getBarData().getGroupWidth(groupSpace, barSpace) * 4);
        chart.groupBars(start, groupSpace, barSpace);
        chart.invalidate(); // refresh

    }
}