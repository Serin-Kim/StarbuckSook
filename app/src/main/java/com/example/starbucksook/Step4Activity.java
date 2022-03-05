package com.example.starbucksook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class Step4Activity extends AppCompatActivity {

    TextView step4_weight;
    TextView countdown_text;
    Button startBtn;

    // 값 내가 줌
    int hour=0, minute=0, second=40;
    int cnt=7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step4);

        countdown_text = findViewById(R.id.countdown_text_step4);

        /* 남은 시간 */
        String initTime = "";
        initTime =  "" +  hour + ":";
        if(minute < 10) initTime += "0";
        initTime += minute +":";
        if(second < 10) initTime += "0";
        initTime += second;
        countdown_text.setText("남은 시간 - " + initTime);

        startBtn = (Button)findViewById(R.id.startBtn);


        step4_weight = findViewById(R.id.step4_weight);
        // step4_weight.setText("Set Text test");         // TODO: 여기에 무게 측정 값 넣으면 된다.



        Button btn = (Button)findViewById(R.id.step4_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step4Activity.this, Step5Activity.class);

                startActivity(intent);

            }
        });

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

        Button btn2 = (Button)findViewById(R.id.step4_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step4Activity.this, Step5Activity.class);
                startActivity(intent);
            }
        });
    }





}


//package com.example.starbucksook;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class Step4Activity extends AppCompatActivity {
//
//    TextView countdown_text;
//    Button startBtn;
//    int hour, minute, second;
//    int count;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_step4);
//
//        countdown_text = findViewById(R.id.countdown_text);
//
//
//        startBtn = (Button)findViewById(R.id.startBtn);
//
//        hour = 0;
//        minute = 0;
//        second = 5;
//        count = second;
//
//        // 시작버튼 이벤트 1처리
//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                // 값 내가 줌
//
//
//
//                Timer timer = new Timer();
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        // 반복실행할 구문
//
//                        // 0초 이상이면
//                        if(second != 0) {
//                            //1초씩 감소
//                            second--;
//
//                            // 0분 이상이면
//                        } else if(minute != 0) {
//                            // 1분 = 60초
//                            second = 60;
//                            second--;
//                            minute--;
//
//                            // 0시간 이상이면
//                        } else if(hour != 0) {
//                            // 1시간 = 60분
//                            second = 60;
//                            minute = 60;
//                            second--;
//                            minute--;
//                            hour--;
//                        }
//
//
//
//                        //시, 분, 초가 10이하(한자리수) 라면
//                        // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
//
//                        String timeLeftText = "";
//
//                        timeLeftText =  "" +  hour + ":";
//
//                        if(minute < 10) timeLeftText += "0";
//                        timeLeftText += minute +":";
//
//                        //초가 10보다 작으면 0이 붙는다.
//                        if(second < 10) timeLeftText += "0";
//                        timeLeftText += second;
//
//                        countdown_text.setText("남은 시간 - " + timeLeftText);
//
//
//                        // 시분초가 다 0이라면 toast를 띄우고 타이머를 종료한다..
//                        if(hour == 0 && minute == 0 && second == 0) {
//                            timer.cancel();//타이머 종료
//                            countdown_text.setText("타이머가 종료되었습니다.");
//                        }
//                    }
//                };
//
//                //타이머를 실행
//                timer.schedule(timerTask, 0, 1000); //Timer 실행
//
//            }
//        });
//
//
//        new Handler().postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                Intent intent = new Intent(Step4Activity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        }, count * 1000);// 5초 정도 딜레이를 준 후 시작; 1초가 1000
//
//
//
//
//
//        Button btn = (Button)findViewById(R.id.step4_btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Step4Activity.this, WeightActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//
//
//}
