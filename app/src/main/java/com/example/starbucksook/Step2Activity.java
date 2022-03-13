package com.example.starbucksook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Step2Activity extends AppCompatActivity {
    TextView step2_info;
    TextView step2_textview;
    TextView step2_info2;
    TextView step2_weight;
    TextView step_coffee;
    TextView step_water;
    public Socket client;
    public DataOutputStream dataOutput;
    public DataInputStream dataInput;
    public static String SERVER_IP = "192.168.101.101";
    public static String CONNECT_MSG = "connect";
    public static String STOP_MSG = "stop";
    public String base_weight;
    public float new_weight;
    private int step2_flag = 1;
    private int step3_flag = 1;
    private int step4_flag = 1;
    private int step5_flag = 1;
    private int step6_flag = 1;
    public int timer_flag = 1;
    public int fff = 1;
    public Button btn;
    public Timer timer;
    public TimerTask timerTask;
    int hour=0, minute=0, second=5;
    int cnt=7;
    public long start, end;
    public double total_time;

    public static int BUF_SIZE = 100;
    TextView step4_weight;
    TextView countdown_text;



    /*
    타이머 추가 시작
    */
    int timerTime = 15; // 3 초를 디폴트로.
    // Timer 를 처리해주는 핸들러
    TimerHandler timer2;
    boolean isRunning = true;

    int status = 0 ; // 0:정지, 1:시작, 2:일시정지






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        start = System.currentTimeMillis();

        /*
        타이머 추가 시작
        */


        countdown_text = (TextView)findViewById(R.id.countdown_text);

        timer2 = new TimerHandler();

        step2_textview = findViewById(R.id.step2_textview);
        step2_textview.setText("Step 1: 원두 담기");
        step_coffee = findViewById(R.id.step_coffee);
        step_water = findViewById(R.id.step_water);
        step2_info = findViewById(R.id.step2_check_content);
        step2_info.setText("분쇄된 원두 커피를 드리퍼 내부에 담아줍니다.");
        step2_info2 = findViewById(R.id.step2_check_content2);
        step2_weight = findViewById(R.id.step2_weight_content);
        Connect connect = new Connect();
        connect.execute(CONNECT_MSG);
        step2_info2.setText("아래에 표시되는 원두의 무게가 20g이 초과되면 다음 단계로 넘어갑니다.");
        new_weight = 0;
        btn = (Button)findViewById(R.id.step2_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step2Activity.this, ReportActivity.class);

                startActivity(intent);

            }
        });

        countdown_text = findViewById(R.id.countdown_text);

        /* 남은 시간 */
        String initTime = "";
        initTime =  "" +  hour + ":";
        if(minute < 10) initTime += "0";
        initTime += minute +":";
        if(second < 10) initTime += "0";
        initTime += second;
//        countdown_text.setText("남은 시간 - " + initTime);
        countdown_text.setText("남은 시간 - " + initTime);

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 반복실행할 구문

                // 0초 이상이면
                if(second != 0) {
                    //1초씩 감소
                    timer_flag = 1;
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
//                    timerTask.cancel();//타이머 종료
                    countdown_text.setText("타이머가 종료되었습니다.");
                    timer_flag = 0;
                }
            }
        };
    }



    /*
    타이머 추가 시작
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer2 != null)
            timer2.removeMessages(0);
        isRunning = false;
    }

    // 타이머 핸들러 클래스.
    class TimerHandler extends Handler{

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 0: // 시작 하기
                    if (timerTime == 0) {
//                        countdown_text.setText("Timer : " + timerTime);
                        countdown_text.setText("Timer가 종료되었습니다.");
                        removeMessages(0);
                        fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음

                        break;
                    }
                    // Main Thread 가 쉴때 Main Thread 가 실행하기에
                    // 오래걸리는 작업은 하면 안된다. (무한 루프 문 etc.. 은 쓰레드로)
                    countdown_text.setText("남은 시간 : 00:00:" + timerTime--);
                    sendEmptyMessageDelayed(0, 1000);
                    Log.d("test", "msg.what:0 time = " + timerTime);

                    break;

                case 1: // 일시 정지
                    removeMessages(0); // 타이머 메시지 삭제
                    countdown_text.setText("남은 시간 : 00:00:" + timerTime); // 현재 시간을 표시.
                    Log.d("test" , "msg.what:1 time = " + timerTime);

                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음

                    break;

                case 2: // 정지 후 타이머 초기화
                    removeMessages(0); // 타이머 메시지 삭제
                    timerTime = 10; // 타이머 초기화
                    countdown_text.setText("남은 시간 : 00:00:" + timerTime);
                    // button.setText("시작");
//                    Log.d("test" , "msg.what:2 time = " + timerTime);

                    status = 0;

                    // 타이머 다시 바로 재생
                    // 타이머 시작!
                    if(status == 0) // 정지 상태라면, 재 시작.
                    {
                        status = 1;
                        timer2.sendEmptyMessage(0);
                        //    button.setText("일시정지");

                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기

                        status = 0;
                        // 1번 메시지를 던진다.
                        timer2.sendEmptyMessage(1);
                        // button.setText("시작");
                    }

//                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음 (너가 문제였어 이놈아)

                    break;


                case 3: // 정지 후 타이머 초기화
                    removeMessages(0); // 타이머 메시지 삭제
                    timerTime = 10; // 타이머 초기화
                    countdown_text.setText("남은 시간 : 00:00:" + timerTime);
                    // button.setText("시작");
//                    Log.d("test" , "msg.what:3 time = " + timerTime);


                    status = 0;

                    // 타이머 다시 바로 재생
                    // 타이머 시작!
                    if(status == 0) // 정지 상태라면, 재 시작.
                    {
                        status = 1;
                        timer2.sendEmptyMessage(0);
                        // button.setText("일시정지");

                    }else if(status == 1){ // 타이머 동작 중이라면, 일시 정지 시키기

                        status = 0;
                        // 1번 메시지를 던진다.
                        timer2.sendEmptyMessage(1);
                        // button.setText("시작");
                    }

                    break;
                case 4: // 정지 후 타이머 초기화
                    removeMessages(0); // 타이머 메시지 삭제
                    timerTime = 6; // 타이머 초기화
                    countdown_text.setText("남은 시간 : 00:00:" + timerTime);
                    // button.setText("시작");
//                    Log.d("test" , "msg.what:2 time = " + timerTime);

                    status = 0;

                    // 타이머 다시 바로 재생
                    // 타이머 시작!
                    if(status == 0) // 정지 상태라면, 재 시작.
                    {
                        status = 1;
                        timer2.sendEmptyMessage(0);
                        //    button.setText("일시정지");

                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기

                        status = 0;
                        // 1번 메시지를 던진다.
                        timer2.sendEmptyMessage(1);
                        // button.setText("시작");
                    }

//                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음 (너가 문제였어 이놈아)

                    break;
                case 5: // 정지 후 타이머 초기화
                    removeMessages(0); // 타이머 메시지 삭제
                    timerTime = 4; // 타이머 초기화
                    countdown_text.setText("남은 시간 : 00:00:" + timerTime);
                    // button.setText("시작");
//                    Log.d("test" , "msg.what:2 time = " + timerTime);

                    status = 0;

                    // 타이머 다시 바로 재생
                    // 타이머 시작!
                    if(status == 0) // 정지 상태라면, 재 시작.
                    {
                        status = 1;
                        timer2.sendEmptyMessage(0);
                        //    button.setText("일시정지");

                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기

                        status = 0;
                        // 1번 메시지를 던진다.
                        timer2.sendEmptyMessage(1);
                        // button.setText("시작");
                    }

//                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음 (너가 문제였어 이놈아)

                    break;
            }
        }
    }

    /*
    타이머 추가 종료
     */




    public class Connect extends AsyncTask< String , String,Void > {
        public String output_message;
        public String input_message;

        @Override
        public Void doInBackground(String... strings) {
            try {
                client = new Socket(SERVER_IP, 8080);
                dataOutput = new DataOutputStream(client.getOutputStream());
                dataInput = new DataInputStream(client.getInputStream());
                output_message = strings[0];
                dataOutput.writeUTF(output_message);

            } catch (UnknownHostException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 1");
            } catch (IOException e) {
                String str = e.getMessage().toString();
                Log.w("discnt", str + " 2");
            }
            try {
                byte[] buf1 = new byte[BUF_SIZE];
                int read_Byte1 = dataInput.read(buf1);
                base_weight = new String(buf1, 0, read_Byte1);
            }catch (IOException e){
                e.printStackTrace();
            }

            while (true) {
                try {
                    byte[] buf = new byte[BUF_SIZE];
                    int read_Byte = dataInput.read(buf);
                    input_message = new String(buf, 0, read_Byte);
                    if (!input_message.equals(STOP_MSG)) {
                        publishProgress(input_message);
                    } else {
                        break;
                    }
                    Thread.sleep(2);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onProgressUpdate(String... params) {
//            Log.d("test", "Step1 Start!!");

            step2_weight.setText(""); // Clear the chat box
            float now_weight = Float.parseFloat(params[0]);
            float weight;
            float total_weight = now_weight - Float.parseFloat(base_weight);
            weight = now_weight - Float.parseFloat(base_weight) - new_weight;
            step2_weight.setText("현재 단계 무게: " + Float.toString(weight) + "\ntotal_weight: " + Float.toString(total_weight));
            //step2_weight.append("base weight " + base_weight);
            //step2_weight.append("now_weight: " + Float.toString(now_weight));

            if (step2_flag == 1 && total_weight > 20 && total_weight < 35) {
                Log.d("test", "Step2 Start!!");
                new_weight += 20;
                timer_flag = 1;
                step2_flag = 0;
                step2_textview.setText("Step 2: 뜸들이기");
                step_coffee.setText("20");
                step_water.setText("15");
                step2_info.setText("물 15ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다." +
                        "이 작업은 가루 전체에 물이 고르게 퍼지며 추출이 원활하게 합니다. " +
                        "또한 커피에 함유된 탄산가스와 공기를 빼주는 역할도 합니다.");
                step2_info2.setText("아래에 표시된 물의 양이 15ml가 되면 타이머가 시작됩니다. 40초가 지나면 다음 단계로 넘어갑니다. ");
                Log.d("-----", "Step2 : " + total_weight);
            }

            if (step2_flag == 0 && total_weight >= 35) {
                Log.d("-----", "Step2: Over weight: " + total_weight);
//                    timer.schedule(timerTask, 0, 1000);
                timer2.sendEmptyMessage(0);
                timer_flag = 0;
                step2_flag = 1;


            }
//            timer_flag = 0;



            if (step3_flag == 1 && total_weight > 35 && total_weight < 185 && timer_flag==0 && fff==0) {
                Log.d("test", "Step3 Start!!");
                fff = 1;
                new_weight += 15;
                timer_flag = 1;
                step3_flag = 0;
                step2_textview.setText("Step 3: 첫 번째 추출");
                step_coffee.setText("0");
                step_water.setText("150");
                step2_info.setText("물 150ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
                step2_info2.setText("아래에 표시된 물의 양이 150ml가 되면 타이머가 시작됩니다. 시간이 다 되면 다음 단계로 넘어갑니다. ");
                Log.d("-----", "Step3 : " + total_weight);
            }

            if (step3_flag == 0 && total_weight >= 185){
                Log.d("-----", "Step3: Over weight: " + total_weight);
//                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
//                    timer.schedule(timerTask, 0, 1000);
                timer2.sendEmptyMessage(2);
                timer_flag = 0;
                step3_flag = 1;
            }
//            timer_flag = 0;



            if (step4_flag == 1 && total_weight > 185 && total_weight < 285 && timer_flag==0 && fff==0) {
                Log.d("test", "Step4 Start!!");
                fff = 1;
                new_weight += 150;
                timer_flag = 1;
                step4_flag = 0;
                step2_textview.setText("Step 4: 두 번째 추출");
                step_coffee.setText("0");
                step_water.setText("100");
                step2_info.setText("물 100ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
                step2_info2.setText("아래에 표시된 물의 양이 100ml가 되면 다음 단계로 넘어갑니다. ");
                Log.d("-----", "Step4 : " + total_weight);
            }

            if (step4_flag == 0 && total_weight >= 285){
                Log.d("-----", "Step4: Over weight: " + total_weight);
//                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
//                    timer.schedule(timerTask, 0, 1000);
                timer2.sendEmptyMessage(4);
                timer_flag = 0;
                step4_flag = 1;
            }
//            timer_flag = 0;


            if (step5_flag == 1 && total_weight >= 285 && total_weight < 340 && timer_flag==0 && fff==0) {
                Log.d("test", "Step5 Start!!");
                fff = 1;
                new_weight += 100;
                timer_flag = 1;
                step5_flag = 0;
                step2_textview.setText("Step 5: 마지막 추출");
                step_coffee.setText("0");
                step_water.setText("55");
                step2_info.setText("물 55ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
                step2_info2.setText("아래에 표시된 물의 양이 55ml가 되면 드립 과정이 마무리됩니다. ");
                Log.d("-----", "Step5 : " + total_weight);
            }

            if (step5_flag == 0 && total_weight >= 340){
                Log.d("-----", "Step5: Over weight" + total_weight);
//                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
//                    timer.schedule(timerTask, 0, 1000);
                timer2.sendEmptyMessage(5);
                timer_flag = 0;
                step5_flag = 1;
            }

//            timer_flag = 0;



            else if (step6_flag == 1 && total_weight >= 340 && timer_flag==0 && fff==0) {
                end = System.currentTimeMillis();
                total_time = (end - start)/1000;
                btn.setVisibility(View.VISIBLE);
                btn.setEnabled(true);
                step2_weight.setVisibility(View.INVISIBLE);
                step6_flag = 0;
                step2_textview.setText("드립 완료! 총 소요시간: " + Double.toString(total_time));
                step2_info.setText("핸드 드립 커피가 완성되었습니다!");
                step2_info2.setText("향긋한 커피를 마시며 저희가 분석한 결과를 확인해보세요.");


            }
        }
    }
}




//package com.example.starbucksook;
//
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.os.Handler;
//import android.os.Message;
//import android.os.SystemClock;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class Step2Activity extends AppCompatActivity {
//    TextView step2_info;
//    TextView step2_textview;
//    TextView step2_info2;
//    TextView step2_weight;
//    TextView step_coffee;
//    TextView step_water;
//    public Socket client;
//    public DataOutputStream dataOutput;
//    public DataInputStream dataInput;
//    public static String SERVER_IP = "192.168.101.101";
//    public static String CONNECT_MSG = "connect";
//    public static String STOP_MSG = "stop";
//    public String base_weight;
//    public float new_weight;
//    private int step2_flag = 1;
//    private int step3_flag = 1;
//    private int step4_flag = 1;
//    private int step5_flag = 1;
//    private int step6_flag = 1;
//    public int timer_flag = 1;
//    public int fff = 1;
//    public Button btn;
//    public Timer timer;
//    public TimerTask timerTask;
//    int hour=0, minute=0, second=5;
//    int cnt=7;
//    public long start, end;
//    public double total_time;
//
//    public static int BUF_SIZE = 100;
//    TextView step4_weight;
//    TextView countdown_text;
//
//
//
//    /*
//    타이머 추가 시작
//    */
//    int timerTime = 3; // 3 초를 디폴트로.
//    // Timer 를 처리해주는 핸들러
//    TimerHandler timer2;
//    boolean isRunning = true;
//
//    int status = 0 ; // 0:정지, 1:시작, 2:일시정지
//
//
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_step2);
//        start = System.currentTimeMillis();
//
//        /*
//        타이머 추가 시작
//        */
//
//
//        countdown_text = (TextView)findViewById(R.id.countdown_text);
//
//        timer2 = new TimerHandler();
//
//        step2_textview = findViewById(R.id.step2_textview);
//        step2_textview.setText("Step 1: 원두 담기");
//        step_coffee = findViewById(R.id.step_coffee);
//        step_water = findViewById(R.id.step_water);
//        step2_info = findViewById(R.id.step2_check_content);
//        step2_info.setText("분쇄된 원두 커피를 드리퍼 내부에 담아줍니다.");
//        step2_info2 = findViewById(R.id.step2_check_content2);
//        step2_weight = findViewById(R.id.step2_weight_content);
//        Connect connect = new Connect();
//        connect.execute(CONNECT_MSG);
//        step2_info2.setText("아래에 표시되는 원두의 무게가 20g이 초과되면 다음 단계로 넘어갑니다.");
//        new_weight = 0;
//        btn = (Button)findViewById(R.id.step2_btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Step2Activity.this, ReportActivity.class);
//
//                startActivity(intent);
//
//            }
//        });
//
//        countdown_text = findViewById(R.id.countdown_text);
//
//        /* 남은 시간 */
//        String initTime = "";
//        initTime =  "" +  hour + ":";
//        if(minute < 10) initTime += "0";
//        initTime += minute +":";
//        if(second < 10) initTime += "0";
//        initTime += second;
////        countdown_text.setText("남은 시간 - " + initTime);
//        countdown_text.setText("남은 시간 - " + initTime);
//
//        timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                // 반복실행할 구문
//
//                // 0초 이상이면
//                if(second != 0) {
//                    //1초씩 감소
//                    timer_flag = 1;
//                    second--;
//
//                    // 0분 이상이면
//                } else if(minute != 0) {
//                    // 1분 = 60초
//                    second = 60;
//                    second--;
//                    minute--;
//
//                    // 0시간 이상이면
//                } else if(hour != 0) {
//                    // 1시간 = 60분
//                    second = 60;
//                    minute = 60;
//                    second--;
//                    minute--;
//                    hour--;
//                }
//
//                //시, 분, 초가 10이하(한자리수) 라면
//                // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
//
//                String timeLeftText = "";
//
//                timeLeftText =  "" +  hour + ":";
//
//                if(minute < 10) timeLeftText += "0";
//                timeLeftText += minute +":";
//
//                //초가 10보다 작으면 0이 붙는다.
//                if(second < 10) timeLeftText += "0";
//                timeLeftText += second;
//
//                countdown_text.setText("남은 시간 - " + timeLeftText);
//
//                // 시분초가 다 0이라면 text를 띄우고 타이머를 종료한다..
//                if(hour == 0 && minute == 0 && second == 0) {
////                    timerTask.cancel();//타이머 종료
//                    countdown_text.setText("타이머가 종료되었습니다.");
//                    timer_flag = 0;
//                }
//            }
//        };
//    }
//
//
//
//    /*
//    타이머 추가 시작
//     */
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(timer2 != null)
//            timer2.removeMessages(0);
//        isRunning = false;
//    }
//
//    // 타이머 핸들러 클래스.
//    class TimerHandler extends Handler{
//
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//
//            switch (msg.what){
//                case 0: // 시작 하기
//                    if (timerTime == 0) {
////                        countdown_text.setText("Timer : " + timerTime);
//                        countdown_text.setText("Timer가 종료되었습니다.");
//                        removeMessages(0);
//                        fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음
//
//                        break;
//                    }
//                    // Main Thread 가 쉴때 Main Thread 가 실행하기에
//                    // 오래걸리는 작업은 하면 안된다. (무한 루프 문 etc.. 은 쓰레드로)
//                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime--);
//                    sendEmptyMessageDelayed(0, 1000);
//                    Log.d("test", "msg.what:0 time = " + timerTime);
//
//                    break;
//
//                case 1: // 일시 정지
//                    removeMessages(0); // 타이머 메시지 삭제
//                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime); // 현재 시간을 표시.
//                    Log.d("test" , "msg.what:1 time = " + timerTime);
//
//                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음
//
//                    break;
//
//                case 2: // 정지 후 타이머 초기화
//                    removeMessages(0); // 타이머 메시지 삭제
//                    timerTime = 3; // 타이머 초기화
//                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime);
//                    // button.setText("시작");
////                    Log.d("test" , "msg.what:2 time = " + timerTime);
//
//                    status = 0;
//
//                    // 타이머 다시 바로 재생
//                    // 타이머 시작!
//                    if(status == 0) // 정지 상태라면, 재 시작.
//                    {
//                        status = 1;
//                        timer2.sendEmptyMessage(0);
//                        //    button.setText("일시정지");
//
//                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기
//
//                        status = 0;
//                        // 1번 메시지를 던진다.
//                        timer2.sendEmptyMessage(1);
//                        // button.setText("시작");
//                    }
//
////                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음 (너가 문제였어 이놈아)
//
//                    break;
//
//
//                case 3: // 정지 후 타이머 초기화
//                    removeMessages(0); // 타이머 메시지 삭제
//                    timerTime = 10; // 타이머 초기화
//                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime);
//                    // button.setText("시작");
////                    Log.d("test" , "msg.what:3 time = " + timerTime);
//
//
//                    status = 0;
//
//                    // 타이머 다시 바로 재생
//                    // 타이머 시작!
//                    if(status == 0) // 정지 상태라면, 재 시작.
//                    {
//                        status = 1;
//                        timer2.sendEmptyMessage(0);
//                        // button.setText("일시정지");
//
//                    }else if(status == 1){ // 타이머 동작 중이라면, 일시 정지 시키기
//
//                        status = 0;
//                        // 1번 메시지를 던진다.
//                        timer2.sendEmptyMessage(1);
//                        // button.setText("시작");
//                    }
//
//                    break;
//                case 4: // 정지 후 타이머 초기화
//                    removeMessages(0); // 타이머 메시지 삭제
//                    timerTime = 6; // 타이머 초기화
//                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime);
//                    // button.setText("시작");
////                    Log.d("test" , "msg.what:2 time = " + timerTime);
//
//                    status = 0;
//
//                    // 타이머 다시 바로 재생
//                    // 타이머 시작!
//                    if(status == 0) // 정지 상태라면, 재 시작.
//                    {
//                        status = 1;
//                        timer2.sendEmptyMessage(0);
//                        //    button.setText("일시정지");
//
//                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기
//
//                        status = 0;
//                        // 1번 메시지를 던진다.
//                        timer2.sendEmptyMessage(1);
//                        // button.setText("시작");
//                    }
//
////                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음 (너가 문제였어 이놈아)
//
//                    break;
//                case 5: // 정지 후 타이머 초기화
//                    removeMessages(0); // 타이머 메시지 삭제
//                    timerTime = 4; // 타이머 초기화
//                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime);
//                    // button.setText("시작");
////                    Log.d("test" , "msg.what:2 time = " + timerTime);
//
//                    status = 0;
//
//                    // 타이머 다시 바로 재생
//                    // 타이머 시작!
//                    if(status == 0) // 정지 상태라면, 재 시작.
//                    {
//                        status = 1;
//                        timer2.sendEmptyMessage(0);
//                        //    button.setText("일시정지");
//
//                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기
//
//                        status = 0;
//                        // 1번 메시지를 던진다.
//                        timer2.sendEmptyMessage(1);
//                        // button.setText("시작");
//                    }
//
////                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음 (너가 문제였어 이놈아)
//
//                    break;
//            }
//        }
//    }
//
//    /*
//    타이머 추가 종료
//     */
//
//
//
//
//    public class Connect extends AsyncTask< String , String,Void > {
//        public String output_message;
//        public String input_message;
//
//        @Override
//        public Void doInBackground(String... strings) {
//            try {
//                client = new Socket(SERVER_IP, 8080);
//                dataOutput = new DataOutputStream(client.getOutputStream());
//                dataInput = new DataInputStream(client.getInputStream());
//                output_message = strings[0];
//                dataOutput.writeUTF(output_message);
//
//            } catch (UnknownHostException e) {
//                String str = e.getMessage().toString();
//                Log.w("discnt", str + " 1");
//            } catch (IOException e) {
//                String str = e.getMessage().toString();
//                Log.w("discnt", str + " 2");
//            }
//            try {
//                byte[] buf1 = new byte[BUF_SIZE];
//                int read_Byte1 = dataInput.read(buf1);
//                base_weight = new String(buf1, 0, read_Byte1);
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//            while (true) {
//                try {
//                    byte[] buf = new byte[BUF_SIZE];
//                    int read_Byte = dataInput.read(buf);
//                    input_message = new String(buf, 0, read_Byte);
//                    if (!input_message.equals(STOP_MSG)) {
//                        publishProgress(input_message);
//                    } else {
//                        break;
//                    }
//                    Thread.sleep(2);
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        @Override
//        public void onProgressUpdate(String... params) {
////            Log.d("test", "Step1 Start!!");
//
//            step2_weight.setText(""); // Clear the chat box
//            float now_weight = Float.parseFloat(params[0]);
//            float weight;
//            float total_weight = now_weight - Float.parseFloat(base_weight);
//            weight = now_weight - Float.parseFloat(base_weight) - new_weight;
//            step2_weight.setText("현재 단계 무게: " + Float.toString(weight) + "\ntotal_weight: " + Float.toString(total_weight));
//            //step2_weight.append("base weight " + base_weight);
//            //step2_weight.append("now_weight: " + Float.toString(now_weight));
//
//            if (step2_flag == 1 && total_weight > 20 && total_weight < 35) {
//                Log.d("test", "Step2 Start!!");
//                new_weight += 20;
//                timer_flag = 1;
//                step2_flag = 0;
//                step2_textview.setText("Step 2: 뜸들이기");
//                step_coffee.setText("20");
//                step_water.setText("15");
//                step2_info.setText("물 15ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다." +
//                        "이 작업은 가루 전체에 물이 고르게 퍼지며 추출이 원활하게 합니다. " +
//                        "또한 커피에 함유된 탄산가스와 공기를 빼주는 역할도 합니다.");
//                step2_info2.setText("아래에 표시된 물의 양이 15ml가 되면 타이머가 시작됩니다. 40초가 지나면 다음 단계로 넘어갑니다. ");
//                Log.d("-----", "Step2 : " + total_weight);
//            }
//
//            if (step2_flag == 0 && total_weight >= 35) {
//                Log.d("-----", "Step2: Over weight: " + total_weight);
////                    timer.schedule(timerTask, 0, 1000);
//                timer2.sendEmptyMessage(0);
//                timer_flag = 0;
//                step2_flag = 1;
//
//
//            }
////            timer_flag = 0;
//
//
//
//            if (step3_flag == 1 && total_weight > 35 && total_weight < 185 && timer_flag==0 && fff==0) {
//                Log.d("test", "Step3 Start!!");
//                fff = 1;
//                new_weight += 15;
//                timer_flag = 1;
//                step3_flag = 0;
//                step2_textview.setText("Step 3: 첫 번째 추출");
//                step_coffee.setText("0");
//                step_water.setText("150");
//                step2_info.setText("물 150ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
//                step2_info2.setText("아래에 표시된 물의 양이 150ml가 되면 타이머가 시작됩니다. 시간이 다 되면 다음 단계로 넘어갑니다. ");
//                Log.d("-----", "Step3 : " + total_weight);
//            }
//
//            if (step3_flag == 0 && total_weight >= 185){
//                Log.d("-----", "Step3: Over weight: " + total_weight);
////                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
////                    timer.schedule(timerTask, 0, 1000);
//                timer2.sendEmptyMessage(2);
//                timer_flag = 0;
//                step3_flag = 1;
//            }
////            timer_flag = 0;
//
//
//
//            if (step4_flag == 1 && total_weight > 185 && total_weight < 285 && timer_flag==0 && fff==0) {
//                Log.d("test", "Step4 Start!!");
//                fff = 1;
//                new_weight += 150;
//                timer_flag = 1;
//                step4_flag = 0;
//                step2_textview.setText("Step 4: 두 번째 추출");
//                step_coffee.setText("0");
//                step_water.setText("100");
//                step2_info.setText("물 100ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
//                step2_info2.setText("아래에 표시된 물의 양이 100ml가 되면 다음 단계로 넘어갑니다. ");
//                Log.d("-----", "Step4 : " + total_weight);
//            }
//
//            if (step4_flag == 0 && total_weight >= 285){
//                Log.d("-----", "Step4: Over weight: " + total_weight);
////                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
////                    timer.schedule(timerTask, 0, 1000);
//                timer2.sendEmptyMessage(4);
//                timer_flag = 0;
//                step4_flag = 1;
//            }
////            timer_flag = 0;
//
//
//            if (step5_flag == 1 && total_weight >= 285 && total_weight < 340 && timer_flag==0 && fff==0) {
//                Log.d("test", "Step5 Start!!");
//                fff = 1;
//                new_weight += 100;
//                timer_flag = 1;
//                step5_flag = 0;
//                step2_textview.setText("Step 5: 마지막 추출");
//                step_coffee.setText("0");
//                step_water.setText("55");
//                step2_info.setText("물 55ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
//                step2_info2.setText("아래에 표시된 물의 양이 55ml가 되면 드립 과정이 마무리됩니다. ");
//                Log.d("-----", "Step5 : " + total_weight);
//            }
//
//            if (step5_flag == 0 && total_weight >= 340){
//                Log.d("-----", "Step5: Over weight" + total_weight);
////                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
////                    timer.schedule(timerTask, 0, 1000);
//                timer2.sendEmptyMessage(5);
//                timer_flag = 0;
//                step5_flag = 1;
//            }
//
////            timer_flag = 0;
//
//
//
//            else if (step6_flag == 1 && total_weight >= 340 && timer_flag==0 && fff==0) {
//                end = System.currentTimeMillis();
//                total_time = (end - start)/1000;
//                btn.setVisibility(View.VISIBLE);
//                btn.setEnabled(true);
//                step2_weight.setVisibility(View.INVISIBLE);
//                step6_flag = 0;
//                step2_textview.setText("드립 완료! 총 소요시간: " + Double.toString(total_time));
//                step2_info.setText("핸드 드립 커피가 완성되었습니다!");
//                step2_info2.setText("향긋한 커피를 마시며 저희가 분석한 결과를 확인해보세요.");
//
//
//            }
//        }
//    }
//}
//
//
//
//
//
//
//
//
//
//
////package com.example.starbucksook;
////import android.content.Intent;
////import android.os.AsyncTask;
////import android.os.Bundle;
////import android.util.Log;
////import android.view.View;
////import android.widget.Button;
////import android.widget.TextView;
////import android.os.Handler;
////import android.os.Message;
////
////import androidx.annotation.NonNull;
////import androidx.appcompat.app.AppCompatActivity;
////import androidx.constraintlayout.widget.ConstraintLayout;
////
////import java.io.DataInputStream;
////import java.io.DataOutputStream;
////import java.io.IOException;
////import java.net.Socket;
////import java.net.UnknownHostException;
////import java.util.Timer;
////import java.util.TimerTask;
////
////public class Step2Activity extends AppCompatActivity {
////    TextView step2_check_content;
////    TextView step2_check_title;
////    TextView step2_check_title2;
////    TextView step2_textview;
////    TextView step2_check_content2;
////    TextView step2_weight;
////    ConstraintLayout step2_weight_box;
////    TextView step_coffee;
////    TextView step_water;
////    public Socket client;
////    public DataOutputStream dataOutput;
////    public DataInputStream dataInput;
////    public static String SERVER_IP = "192.168.101.101";
//////    public static String SERVER_IP = "172.20.10.12";
////    public static String CONNECT_MSG = "connect";
////    public static String STOP_MSG = "stop";
////    public String base_weight;
////    public float new_weight;
////    private int step2_flag = 1;
////    private int step3_flag = 1;
////    private int step4_flag = 1;
////    private int step5_flag = 1;
////    private int step6_flag = 1;
////    public int timer_flag = 1;
////    public int fff = 1;
////    public Button btn;
////    public Timer timer;
////    public TimerTask timerTask;
////    int hour=0, minute=0, second=5;
////    int cnt=7;
////
////    public static int BUF_SIZE = 100;
////    TextView step4_weight;
////    TextView countdown_text;
////
////
////
////    /*
////    타이머 추가 시작
////    */
////    int timerTime = 3; // 30 초를 디폴트로.
////    // Timer 를 처리해주는 핸들러
////    TimerHandler timer2;
////    // 시작, 중지 버튼
////    Button button, button2;
////    // 시간을 표시
//////    TextView countdown_text;
////    boolean isRunning = true;
////
////    int status = 0 ; // 0:정지, 1:시작, 2:일시정지
////
////    public void onStartPauseButton(View view){
////        // 타이머 시작!
////        if(status == 0) // 정지 상태 라면, 재 시작.
////        {
////            status = 1;
////            timer2.sendEmptyMessage(0);
//////            button.setText("일시정지");
////
////        }else if(status == 1){ // 타이머 동작 중이라면, 일시 정지 시키기
////
////            status = 0;
////            // 1번 메시지를 던진다.
////            timer2.sendEmptyMessage(1);
//////            button.setText("시작");
////        }
////    }
////
////    public void onStop(View view){
////        status = 0;
//////        status = 1; // 정지 버튼 누르면 바로 재시작
////        timer2.sendEmptyMessage(2);
////    }
////
////    /*
////    타이머 추가 종료
////    */
////
////
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_step2);
////
////        /*
////        타이머 추가 시작
////        */
////
//////        button = (Button)findViewById(R.id.button);
//////        button2 = (Button)findViewById(R.id.button2);
////        countdown_text = (TextView)findViewById(R.id.countdown_text);
////
////        timer2 = new TimerHandler();
////
//////        // 타이머 바로 재생
//////        // 타이머 시작!
//////        if(status == 0) // 정지 상태 라면, 재 시작.
//////        {
//////            status = 1;
//////            timer2.sendEmptyMessage(0);
//////            button.setText("일시정지");
//////
//////        }else if(status == 1){ // 타이머 동작 중이라면, 일시 정지 시키기
//////
//////            status = 0;
//////            // 1번 메시지를 던진다.
//////            timer2.sendEmptyMessage(1);
//////            button.setText("시작");
//////        }
////
////        /*
////        타이머 추가 종료
////        */
////
////
////
////        step2_textview = findViewById(R.id.step2_textview);
////        step2_textview.setText("Step 1: 원두 담기");
////        step_coffee = findViewById(R.id.step_coffee);
////        step_water = findViewById(R.id.step_water);
////        step2_check_content = findViewById(R.id.step2_check_content);
////        step2_check_content.setText("분쇄된 원두 커피를 드리퍼 내부에 담아줍니다.");
////        step2_check_content2 = findViewById(R.id.step2_check_content2);
////        step2_weight = findViewById(R.id.step2_weight_content);
////
////        step2_check_title = findViewById(R.id.step2_check_title);
////        step2_check_title.setText("확인하기");
////        step2_check_title2 = findViewById(R.id.step2_check_title2);
////        step2_check_title2.setText("다음으로");
////
////        step2_weight_box = findViewById(R.id.step_check3);
////
////
////        Connect connect = new Connect();
////        connect.execute(CONNECT_MSG);
////        step2_check_content2.setText("아래에 표시되는 원두의 무게가 20g이 초과되면 다음 단계로 넘어갑니다.");
////        new_weight = 0;
////        btn = (Button)findViewById(R.id.step2_btn);
////        btn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Intent intent = new Intent(Step2Activity.this, ReportActivity.class);
////
////                startActivity(intent);
////
////            }
////        });
////
////        countdown_text = findViewById(R.id.countdown_text);
////
////        /* 남은 시간 */
////        String initTime = "";
////        initTime =  "" +  hour + ":";
////        if(minute < 10) initTime += "0";
////        initTime += minute +":";
////        if(second < 10) initTime += "0";
////        initTime += second;
//////        countdown_text.setText("남은 시간 - " + initTime);
////        countdown_text.setText("남은 시간 - " + initTime);
////
////        timer = new Timer();
////        timerTask = new TimerTask() {
////            @Override
////            public void run() {
////                // 반복실행할 구문
////
////                // 0초 이상이면
////                if(second != 0) {
////                    //1초씩 감소
////                    timer_flag = 1;
////                    second--;
////
////                    // 0분 이상이면
////                } else if(minute != 0) {
////                    // 1분 = 60초
////                    second = 60;
////                    second--;
////                    minute--;
////
////                    // 0시간 이상이면
////                } else if(hour != 0) {
////                    // 1시간 = 60분
////                    second = 60;
////                    minute = 60;
////                    second--;
////                    minute--;
////                    hour--;
////                }
////
////                //시, 분, 초가 10이하(한자리수) 라면
////                // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
////
////                String timeLeftText = "";
////
////                timeLeftText =  "" +  hour + ":";
////
////                if(minute < 10) timeLeftText += "0";
////                timeLeftText += minute +":";
////
////                //초가 10보다 작으면 0이 붙는다.
////                if(second < 10) timeLeftText += "0";
////                timeLeftText += second;
////
////                countdown_text.setText("남은 시간 - " + timeLeftText);
////
////                // 시분초가 다 0이라면 text를 띄우고 타이머를 종료한다..
////                if(hour == 0 && minute == 0 && second == 0) {
//////                    timerTask.cancel();//타이머 종료
////                    countdown_text.setText("타이머가 종료되었습니다.");
////                    timer_flag = 0;
////                }
////            }
////        };
////    }
////
////
////
////    /*
////    타이머 추가 시작
////     */
////
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        if(timer2 != null)
////            timer2.removeMessages(0);
////        isRunning = false;
////    }
////
////    // 타이머 핸들러 클래스.
////    class TimerHandler extends Handler{
////
////        @Override
////        public void handleMessage(@NonNull Message msg) {
////            super.handleMessage(msg);
////
////            switch (msg.what){
////                case 0: // 시작 하기
////                    if (timerTime == 0) {
//////                        countdown_text.setText("Timer : " + timerTime);
////                        countdown_text.setText("Timer가 종료되었습니다.");
////                        removeMessages(0);
////                        fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음
////
////                        break;
////                    }
////                    // Main Thread 가 쉴때 Main Thread 가 실행하기에
////                    // 오래걸리는 작업은 하면 안된다. (무한 루프 문 etc.. 은 쓰레드로)
////                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime--);
////                    sendEmptyMessageDelayed(0, 1000);
////                    Log.d("test", "msg.what:0 time = " + timerTime);
////
////                    break;
////
////                case 1: // 일시 정지
////                    removeMessages(0); // 타이머 메시지 삭제
////                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime); // 현재 시간을 표시.
////                    Log.d("test" , "msg.what:1 time = " + timerTime);
////
////                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음
////
////                    break;
////
////                case 2: // 정지 후 타이머 초기화
////                    removeMessages(0); // 타이머 메시지 삭제
////                    timerTime = 3; // 타이머 초기화
////                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime);
//////                    button.setText("시작");
//////                    Log.d("test" , "msg.what:2 time = " + timerTime);
////
////                    status = 0;
////
////                    // 타이머 다시 바로 재생
////                    // 타이머 시작!
////                    if(status == 0) // 정지 상태라면, 재 시작.
////                    {
////                        status = 1;
////                        timer2.sendEmptyMessage(0);
//////                        button.setText("일시정지");
////
////                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기
////
////                        status = 0;
////                        // 1번 메시지를 던진다.
////                        timer2.sendEmptyMessage(1);
//////                        button.setText("시작");
////                    }
////
//////                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음
////
////                    break;
////
////                case 3: // 정지 후 타이머 초기화
////                    removeMessages(0); // 타이머 메시지 삭제
////                    timerTime = 4; // 타이머 초기화
////                    countdown_text.setText("남은 시간 : 00:00:0" + timerTime);
//////                    button.setText("시작");
//////                    Log.d("test" , "msg.what:3 time = " + timerTime);
////
////
////                    status = 0;
////
////                    // 타이머 다시 바로 재생
////                    // 타이머 시작!
////                    if(status == 0) // 정지 상태라면, 재 시작.
////                    {
////                        status = 1;
////                        timer2.sendEmptyMessage(0);
//////                        button.setText("일시정지");
////
////                    }else if(status == 1){ // 타이머 동작 중이라면, 일시 정지 시키기
////
////                        status = 0;
////                        // 1번 메시지를 던진다.
////                        timer2.sendEmptyMessage(1);
//////                        button.setText("시작");
////
////                    }
////
////                    break;
////            }
////        }
////    }
////
////    /*
////    타이머 추가 종료
////     */
////
////
////
////
////    public class Connect extends AsyncTask< String , String,Void > {
////        public String output_message;
////        public String input_message;
////
////        @Override
////        public Void doInBackground(String... strings) {
////            try {
////                client = new Socket(SERVER_IP, 8080);
////                dataOutput = new DataOutputStream(client.getOutputStream());
////                dataInput = new DataInputStream(client.getInputStream());
////                output_message = strings[0];
////                dataOutput.writeUTF(output_message);
////
////            } catch (UnknownHostException e) {
////                String str = e.getMessage().toString();
////                Log.w("discnt", str + " 1");
////            } catch (IOException e) {
////                String str = e.getMessage().toString();
////                Log.w("discnt", str + " 2");
////            }
////            try {
////                byte[] buf1 = new byte[BUF_SIZE];
////                int read_Byte1 = dataInput.read(buf1);
////                base_weight = new String(buf1, 0, read_Byte1);
////            }catch (IOException e){
////                e.printStackTrace();
////            }
////
////            while (true) {
////                try {
////                    byte[] buf = new byte[BUF_SIZE];
////                    int read_Byte = dataInput.read(buf);
////                    input_message = new String(buf, 0, read_Byte);
////                    if (!input_message.equals(STOP_MSG)) {
////                        publishProgress(input_message);
////                    } else {
////                        break;
////                    }
////                    Thread.sleep(2);
////                } catch (IOException | InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
////            return null;
////        }
////
////        @Override
////        public void onProgressUpdate(String... params) {
//////            Log.d("test", "Step1 Start!!");
////
////            step2_weight.setText(""); // Clear the chat box
////            float now_weight = Float.parseFloat(params[0]);
////            float weight;
////
////            float total_weight = now_weight - Float.parseFloat(base_weight);
////            weight = now_weight - Float.parseFloat(base_weight) - new_weight;
////            step2_weight.setText("현재 단계 무게: " + Float.toString(weight) + "\ntotal_weight: " + Float.toString(total_weight));
////            //step2_weight.append("base weight " + base_weight);
////            //step2_weight.append("now_weight: " + Float.toString(now_weight));
////
////            if (step2_flag == 1 && total_weight > 20 && total_weight < 35) {
////                Log.d("test", "Step2 Start!!");
////                new_weight += 20;
////                timer_flag = 1;
////                step2_flag = 0;
////                step2_textview.setText("Step 2: 뜸들이기");
////                step_coffee.setText("20");
////                step_water.setText("15");
////                step2_check_content.setText("물 15ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다." +
////                        "이 작업은 가루 전체에 물이 고르게 퍼지며 추출이 원활하게 합니다. " +
////                        "또한 커피에 함유된 탄산가스와 공기를 빼주는 역할도 합니다.");
////                step2_check_content2.setText("아래에 표시된 물의 양이 15ml가 되면 타이머가 시작됩니다. 40초가 지나면 다음 단계로 넘어갑니다. ");
////                Log.d("-----", "Step2 : " + total_weight);
////            }
////
////            if (step2_flag == 0 && total_weight >= 35) {
////                Log.d("-----", "Step2: Over weight: " + total_weight);
//////                    timer.schedule(timerTask, 0, 1000);
////                timer2.sendEmptyMessage(0);
////                timer_flag = 0;
////                step2_flag = 1;
////
////
////            }
//////            timer_flag = 0;
////
////
////
////            if (step3_flag == 1 && total_weight > 35 && total_weight < 185 && timer_flag==0 && fff==0) {
////                Log.d("test", "Step3 Start!!");
////                fff = 1;
////                new_weight += 15;
////                timer_flag = 1;
////                step3_flag = 0;
////                step2_textview.setText("Step 3: 첫 번째 추출");
////                step_coffee.setText("0");
////                step_water.setText("150");
////                step2_check_content.setText("물 150ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
////                step2_check_content2.setText("아래에 표시된 물의 양이 150ml가 되면 타이머가 시작됩니다. 시간이 다 되면 다음 단계로 넘어갑니다. ");
////                Log.d("-----", "Step3 : " + total_weight);
////            }
////
////            if (step3_flag == 0 && total_weight >= 185){
////                Log.d("-----", "Step3: Over weight: " + total_weight);
//////                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
//////                    timer.schedule(timerTask, 0, 1000);
////                timer2.sendEmptyMessage(2);
////                timer_flag = 0;
////                step3_flag = 1;
////            }
//////            timer_flag = 0;
////
////
////
////            if (step4_flag == 1 && total_weight > 185 && total_weight < 285 && timer_flag==0 && fff==0) {
////                Log.d("test", "Step4 Start!!");
////                fff = 1;
////                new_weight += 150;
////                timer_flag = 1;
////                step4_flag = 0;
////                step2_textview.setText("Step 4: 두 번째 추출");
////                step_coffee.setText("0");
////                step_water.setText("100");
////                step2_check_content.setText("물 100ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
////                step2_check_content2.setText("아래에 표시된 물의 양이 100ml가 되면 다음 단계로 넘어갑니다. ");
////                Log.d("-----", "Step4 : " + total_weight);
////            }
////
////            if (step4_flag == 0 && total_weight >= 285){
////                Log.d("-----", "Step4: Over weight: " + total_weight);
//////                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
//////                    timer.schedule(timerTask, 0, 1000);
////                timer2.sendEmptyMessage(2);
////                timer_flag = 0;
////                step4_flag = 1;
////            }
//////            timer_flag = 0;
////
////
////            if (step5_flag == 1 && total_weight >= 285 && total_weight < 340 && timer_flag==0 && fff==0) {
////                Log.d("test", "Step5 Start!!");
////                fff = 1;
////                new_weight += 100;
////                timer_flag = 1;
////                step5_flag = 0;
////                step2_textview.setText("Step 5: 마지막 추출");
////                step_coffee.setText("0");
////                step_water.setText("55");
////                step2_check_content.setText("물 55ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
////                step2_check_content2.setText("아래에 표시된 물의 양이 55ml가 되면 드립 과정이 마무리됩니다. ");
////                Log.d("-----", "Step5 : " + total_weight);
////            }
////
////            if (step5_flag == 0 && total_weight >= 340){
////                Log.d("-----", "Step5: Over weight" + total_weight);
//////                countdown_text.setText("남은 시간 : " + timerTime); // 현재 시간을 표시.
//////                    timer.schedule(timerTask, 0, 1000);
////                timer2.sendEmptyMessage(2);
////                timer_flag = 0;
////                step5_flag = 1;
////            }
////
//////            timer_flag = 0;
////
////
////
////            else if (step6_flag == 1 && total_weight >= 340 && timer_flag==0 && fff==0) {
////                btn.setVisibility(View.VISIBLE);
////                btn.setEnabled(true);
////                step2_weight_box.setVisibility(View.INVISIBLE);
////                step6_flag = 0;
////                step2_textview.setText("드립 완료!");
////                step2_check_content.setText("핸드 드립 커피가 완성되었습니다!");
////                step2_check_content2.setText("향긋한 커피를 마시며 저희가 분석한 결과를 확인해보세요.");
////
////            }
////        }
////    }
////}
//
//
//
//
