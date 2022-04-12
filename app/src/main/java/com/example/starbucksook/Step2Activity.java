package com.example.starbucksook;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
    //    public static String SERVER_IP = "192.168.101.101";
    public static String SERVER_IP = "192.168.219.111";
    public static String CONNECT_MSG = "connect";
    public static String STOP_MSG = "stop";
    public String base_weight;
    public float new_weight;
    public float new_weight2;
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
    public long start, end;
    public double total_time;
    public float total_weight;
    public float weight_1, weight_2, weight_3, weight_4, weight_5;
    public float now1, now2, now3, now4, now5;

    public static int BUF_SIZE = 100;
    TextView countdown_text;

    // 타이머 실행 시 알림
    Toast toastMessage;
    MediaPlayer ringtone;


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


        // 타이머 실행 시 알림
        toastMessage = Toast.makeText(this, "타이머를 시작합니다", Toast.LENGTH_LONG);
        ringtone = MediaPlayer.create(this, R.raw.timer_sound);

        countdown_text = (TextView)findViewById(R.id.countdown_text);

        countdown_text.setVisibility(View.INVISIBLE);

        timer2 = new TimerHandler();

        step2_textview = findViewById(R.id.step2_textview);
        step2_textview.setText("Step 1. 원두 담기");
        step_coffee = findViewById(R.id.step_coffee);
        step_water = findViewById(R.id.step_water);
        step2_info = findViewById(R.id.step2_check_content);
        step2_info.setText("분쇄된 원두 커피를 드리퍼 내부에 담아줍니다.");
        step2_info2 = findViewById(R.id.step2_check_content2);

        step2_weight = findViewById(R.id.step2_weight_content);

        Connect connect = new Connect();
        connect.execute(CONNECT_MSG);
        step2_info2.setText("원두의 무게가 15g이 넘으면 다음 단계로 넘어갑니다.");
        new_weight = 0;
        btn = (Button)findViewById(R.id.step2_btn);
        btn = (Button)findViewById(R.id.step2_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step2Activity.this, ReportActivity.class);
                intent.putExtra("weight_1", weight_1);
                intent.putExtra("weight_2", weight_2-weight_1);
                intent.putExtra("weight_3", weight_3-weight_2);
                intent.putExtra("weight_4", weight_4-weight_3);
                intent.putExtra("weight_5", weight_5-weight_4);
                intent.putExtra("total_time", total_time);
                Log.d("test", String.valueOf(total_time));

                intent.putExtra("total_weight", weight_5-weight_1);
                startActivity(intent);

            }
        });


        countdown_text = findViewById(R.id.countdown_text);

        countdown_text.setText("---");

    }



    /*
    타이머 시작
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
                    if (timerTime == -1) {
                        countdown_text.setText(" 0"); // 타이머 끝나면 0 나오게 하려고

                        if(total_weight >= 30 && total_weight < 180){
                            weight_2 = total_weight;
                        }
                        if(total_weight >= 180 && total_weight < 280){
                            weight_3 = total_weight;
                        }
                        if(total_weight >= 280 && total_weight < 335){
                            weight_4 = total_weight;
                        }
                        if(total_weight >= 335){
                            weight_5 = total_weight;
                        }

                        fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음

                        break;
                    }

                    // Main Thread 가 쉴때 Main Thread 가 실행하기에
                    // 오래걸리는 작업은 하면 안된다. (무한 루프 문 etc.. 은 쓰레드로)
                    countdown_text.setText(" " + timerTime--);
                    sendEmptyMessageDelayed(0, 1000);
                    Log.d("test", "msg.what:0 time = " + timerTime);

                    break;

                case 1: // 일시 정지
                    removeMessages(0); // 타이머 메시지 삭제
                    countdown_text.setText(" " +timerTime); // 현재 시간을 표시.
                    Log.d("test" , "msg.what:1 time = " + timerTime);

                    fff = 0; // 타이머까지 끝나야 다음 단계로 넘어갈 수 있음

                    break;

                case 2: // 정지 후 타이머 초기화
                    timerTime = 15; // 타이머 초기화
                    countdown_text.setText(" " + timerTime);

                    status = 0;

                    // 타이머 다시 바로 재생
                    // 타이머 시작!
                    if(status == 0) // 정지 상태라면, 재 시작.
                    {
                        status = 1;
                        timer2.sendEmptyMessage(0);

                    }else if(status == 1){ // 타이머 동작 중이라면, 일시 정지 시키기

                        status = 0;
                        // 1번 메시지를 던진다.
                        timer2.sendEmptyMessage(1);
                    }

                    break;


                case 3: // 정지 후 타이머 초기화
                    timerTime = 15; // 타이머 초기화
                    countdown_text.setText(" " +timerTime);

                    status = 0;

                    // 타이머 다시 바로 재생
                    // 타이머 시작!
                    if(status == 0) // 정지 상태라면, 재 시작.
                    {
                        status = 1;
                        timer2.sendEmptyMessage(0);

                    }else if(status == 1){ // 타이머 동작 중이라면, 일시 정지 시키기

                        status = 0;
                        // 1번 메시지를 던진다.
                        timer2.sendEmptyMessage(1);
                    }

                    break;
                case 4: // 정지 후 타이머 초기화
                    timerTime = 10; // 타이머 초기화
                    countdown_text.setText(" " +timerTime);

                    status = 0;

                    // 타이머 다시 바로 재생
                    // 타이머 시작!
                    if(status == 0) // 정지 상태라면, 재 시작.
                    {
                        status = 1;
                        timer2.sendEmptyMessage(0);

                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기

                        status = 0;
                        // 1번 메시지를 던진다.
                        timer2.sendEmptyMessage(1);
                    }

                    break;
                case 5: // 정지 후 타이머 초기화
//                    removeMessages(0); // 타이머 메시지 삭제
                    timerTime = 10; // 타이머 초기화
                    countdown_text.setText(" " + timerTime);
                    countdown_text.setText(" " + timerTime);

                    status = 0;

                    // 타이머 다시 바로 재생
                    // 타이머 시작!
                    if(status == 0) // 정지 상태라면, 재 시작.
                    {
                        status = 1;
                        timer2.sendEmptyMessage(0);

                    }else if(status == 1){ // 타이머 동중이라면, 일시 정지 시키기

                        status = 0;
                        // 1번 메시지를 던진다.
                        timer2.sendEmptyMessage(1);
                    }

                    break;
            }
        }
    }

    /*
    타이머 종료
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

            step2_weight.setText(""); // Clear the chat box
            float now_weight = Float.parseFloat(params[0]);
            float weight;
            total_weight = now_weight - Float.parseFloat(base_weight);
            weight = now_weight - Float.parseFloat(base_weight) - new_weight2;
//            step2_weight.setText("현재 단계 무게: " + Float.toString(weight) + "\ntotal_weight: " + Float.toString(total_weight));
            step2_weight.setText(String.format("%.0f", weight) + " g");


            if (step2_flag == 1 && total_weight > 15 && total_weight < 30) {
//            if (step2_flag == 1 && weight >= 15) {
                Log.d("test", "Step2 Start!!");

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        weight_1 = total_weight;
                    }
                }, 2000);// 2초 정도 딜레이를 준 후 시작

                new_weight2 += weight;
                now2 = Float.parseFloat(params[0]); // Step 2 현재 단계 무게

                timer_flag = 1;
                step2_flag = 0;
                step2_textview.setText("Step 2. 뜸들이기");
                step_coffee.setText("15");
                step_water.setText("15");


                step_coffee.setVisibility(View.INVISIBLE);
                countdown_text.setVisibility(View.VISIBLE);
                step2_info.setText("물 15g을 가운데부터 나선형으로 천천히 부어줍니다.\n" +
                        "이 과정은 원두를 골고루 적셔 추출이 원활하게 도와주며 " +
                        "커피에 함유된 탄산가스와 공기를 빼줍니다.");
                step2_info2.setText("15g이 되면 타이머가 시작됩니다.");
                countdown_text.setText(" 15");
                Log.d("-----", "Step2 : " + total_weight);
            }

            if (step2_flag == 0 && total_weight >= 30 && weight >= 15) {
                Log.d("-----", "Step2: Over weight: " + total_weight);

                toastMessage.show();    // 토스트 메시지
                ringtone.start();
                timer2.sendEmptyMessage(0);
                timer_flag = 0;
                step2_flag = 1;
            }


            if (step3_flag == 1 && total_weight > 30 && total_weight < 180 && timer_flag==0 && fff==0) {
                Log.d("test", "Step3 Start!!");
                fff = 1;
                new_weight2 += weight;

                timer_flag = 1;
                step3_flag = 0;
                step2_textview.setText("Step 3. 첫 번째 추출");
                step_coffee.setText("0");
                step_water.setText("150");
                step2_info.setText("물 150g를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
                step2_info2.setText("150g가 되면 타이머가 시작됩니다. \n40초 기다려주세요.");
                countdown_text.setText(" 15");
                Log.d("-----", "Step3 : " + total_weight);
            }

            if (step3_flag == 0 && total_weight >= 180 && weight >= 150){
                Log.d("-----", "Step3: Over weight: " + total_weight);
                toastMessage.show();    // 토스트 메시지
                ringtone.start();
                timer2.sendEmptyMessage(3);
                timer_flag = 0;
                step3_flag = 1;
            }

            if (step4_flag == 1 && total_weight >= 180 && total_weight < 280 && timer_flag==0 && fff==0) {
                Log.d("test", "Step4 Start!!");
                fff = 1;
                new_weight2 += weight;

                timer_flag = 1;
                step4_flag = 0;
                step2_textview.setText("Step 4. 두 번째 추출");
                step_coffee.setText("0");
                step_water.setText("100");
                step2_info.setText("물 100g를 가운데부터 나선형으로 천천히 부어줍니다.");
                step2_info2.setText("100g가 되면 타이머가 시작됩니다. \n30초 기다려주세요.");
                countdown_text.setText(" 10");
                Log.d("-----", "Step4 : " + total_weight);
            }

            if (step4_flag == 0 && total_weight >= 280 && weight >= 100){
                Log.d("-----", "Step4: Over weight: " + total_weight);
                toastMessage.show();    // 토스트 메시지
                ringtone.start();
                timer2.sendEmptyMessage(4);
                timer_flag = 0;
                step4_flag = 1;
            }


            if (step5_flag == 1 && total_weight >= 280 && total_weight < 335 && timer_flag==0 && fff==0) {
                Log.d("test", "Step5 Start!!");
                fff = 1;
                new_weight2 += weight;

                timer_flag = 1;
                step5_flag = 0;
                step2_textview.setText("Step 5. 마지막 추출");
                step_coffee.setText("0");
                step_water.setText("55");
                step2_info.setText("물 55g를 가운데부터 나선형으로 천천히 부어줍니다.");
                step2_info2.setText("55g가 되면 타이머가 시작됩니다. \n시간이 다 되면 드립이 완료됩니다.");
                countdown_text.setText(" 10");
                Log.d("-----", "Step5 : " + total_weight);
            }

            if (step5_flag == 0 && total_weight >= 335 && weight >= 55){
                Log.d("-----", "Step5: Over weight" + total_weight);
                toastMessage.show();    // 토스트 메시지
                ringtone.start();
                timer2.sendEmptyMessage(5);
                timer_flag = 0;
                step5_flag = 1;
            }

            else if (step6_flag == 1 && total_weight >= 335 && timer_flag==0 && fff==0) {
                end = System.currentTimeMillis();
                total_time = (end - start)/1000;
                btn.setVisibility(View.VISIBLE);
                btn.setEnabled(true);
                step2_weight.setVisibility(View.INVISIBLE);
                step6_flag = 0;
                countdown_text.setText(" 0");
                step_water.setText("0");
                step2_textview.setText("드립 완료!");
                step2_info.setText("핸드 드립 커피가 완성되었습니다!");
                step2_info2.setText("향긋한 커피를 마시며 저희가 분석한 결과를 확인해보세요.");
            }
        }
    }
}

