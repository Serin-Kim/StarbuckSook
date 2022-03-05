package com.example.starbucksook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class Step5Activity extends AppCompatActivity {

    TextView step5_weight;
    TextView countdown_text;
    Button startBtn;

    // 값 내가 줌
    int hour=0, minute=0, second=40;
    int cnt=7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step5);


        step5_weight = findViewById(R.id.step5_weight);
        // step4_weight.setText("Set Text test");         // TODO: 여기에 무게 측정 값 넣으면 된다.

        countdown_text = findViewById(R.id.countdown_text_step5);
        /* 남은 시간 */
        String initTime = "";
        initTime =  "" +  hour + ":";
        if(minute < 10) initTime += "0";
        initTime += minute +":";
        if(second < 10) initTime += "0";
        initTime += second;
        countdown_text.setText("남은 시간 - " + initTime);

        startBtn = (Button)findViewById(R.id.startBtn);

        // 시작버튼 이벤트 1처리
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        // 반복실행할 구문

                        // 0초 이상이면
                        if(second != 0) {
                            //1초씩 감소
                            second--;

                            // 0분 이상이면
                        } else if(minute != 0) {
                            // 1분 = 60초
                            second = 60;
                            second--;
                            minute--;

                            // 0시간 이상이면
                        } else if(hour != 0) {
                            // 1시간 = 60분
                            second = 60;
                            minute = 60;
                            second--;
                            minute--;
                            hour--;
                        }

                        //시, 분, 초가 10이하(한자리수) 라면
                        // 숫자 앞에 0을 붙인다 ( 8 -> 08 )

                        String timeLeftText = "";

                        timeLeftText =  "" +  hour + ":";

                        if(minute < 10) timeLeftText += "0";
                        timeLeftText += minute +":";

                        //초가 10보다 작으면 0이 붙는다.
                        if(second < 10) timeLeftText += "0";
                        timeLeftText += second;

                        countdown_text.setText("남은 시간 - " + timeLeftText);

                        // 시분초가 다 0이라면 text를 띄우고 타이머를 종료한다..
                        if(hour == 0 && minute == 0 && second == 0) {
                            timer.cancel();//타이머 종료
                            countdown_text.setText("타이머가 종료되었습니다.");
                        }
                    }
                };

                //타이머를 실행
                timer.schedule(timerTask, 0, 1000); //Timer 실행

            }
        });


        Button btn2 = (Button)findViewById(R.id.step5_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step5Activity.this, WeightActivity.class);

                startActivity(intent);

            }
        });
    }



}
