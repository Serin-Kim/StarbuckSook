package com.example.starbucksook;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Step2Activity extends AppCompatActivity {
    TextView step2_info;
    TextView step2_textview;
    TextView step2_info2;
    TextView step2_weight;
    public Socket client;
    public DataOutputStream dataOutput;
    public DataInputStream dataInput;
    public static String SERVER_IP = "192.168.101.101";
    public static String CONNECT_MSG = "connect";
    public static String STOP_MSG = "stop";
    public String base_weight;
    public float new_weight;
    public float total_weight;
    private int step2_flag = 1;
    private int step3_flag = 1;
    private int step4_flag = 1;
    private int step5_flag = 1;
    private int step6_flag = 1;
    public Button btn;


    public static int BUF_SIZE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        step2_textview = findViewById(R.id.step2_textview);
        step2_textview.setText("Step 1: 원두 담기");
        step2_info = findViewById(R.id.step2_info);
        step2_info.setText("분쇄된 원두 커피를 드리퍼 내부에 담아줍니다.");
        step2_info2 = findViewById(R.id.step2_info2);
        step2_weight = findViewById(R.id.step2_weight);
        Connect connect = new Connect();
        connect.execute(CONNECT_MSG);
        step2_info2.setText("아래에 표시되는 원두의 무게가 20g이 초과되면 다음 단계로 넘어갑니다.");
        new_weight = 0;
        btn = (Button)findViewById(R.id.step2_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step2Activity.this, MenuActivity.class);

                startActivity(intent);

            }
        });


    }
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

            total_weight = now_weight-Float.parseFloat(base_weight);
            weight = now_weight-Float.parseFloat(base_weight)-new_weight;
            step2_weight.setText("무게: " + Float.toString(weight) + "total_weight" + Float.toString(total_weight));
            //step2_weight.append("base weight " + base_weight);
            //step2_weight.append("now_weight: " + Float.toString(now_weight));

            if (step2_flag == 1 && total_weight > 20) {
                new_weight += 20;
                step2_flag = 0;
                step2_textview.setText("Step 2: 뜸들이기");
                step2_info.setText("물 15ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다." +
                        "이 작업은 가루 전체에 물이 고르게 퍼지며 추출이 원활하게 합니다. " +
                        "또한 커피에 함유된 탄산가스와 공기를 빼주는 역할도 합니다.");
                step2_info2.setText("아래에 표시된 물의 양이 15ml가 되면 타이머가 시작됩니다. 40초가 지나면 다음 단계로 넘어갑니다. ");
            }


            if (step3_flag == 1 && total_weight > 35) {
                new_weight += 15;
                step3_flag = 0;
                step2_textview.setText("Step 3: 첫번째 추출");
                step2_info.setText("물 150ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
                step2_info2.setText("아래에 표시된 물의 양이 150ml가 되면 다음 단계로 넘어갑니다. ");
            }

            if (step4_flag == 1 && total_weight > 185) {
                new_weight += 150;
                step4_flag = 0;
                step2_textview.setText("Step 4: 두번째 추출");
                step2_info.setText("물 100ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
                step2_info2.setText("아래에 표시된 물의 양이 100ml가 되면 다음 단계로 넘어갑니다. ");
            }

            if (step5_flag == 1 && total_weight > 285) {
                new_weight += 100;
                step5_flag = 0;
                step2_textview.setText("Step 5: 마지막 추출");
                step2_info.setText("물 55ml를 가운데부터 나선형으로 가장자리까지 천천히 부어줍니다.");
                step2_info2.setText("아래에 표시된 물의 양이 55ml가 되면 드립 과정이 마무리됩니다. ");
            }


            if (step6_flag == 1 && total_weight > 340) {
                btn.setVisibility(View.VISIBLE);
                btn.setEnabled(true);
                step2_weight.setVisibility(View.INVISIBLE);
                step6_flag = 0;
                step2_textview.setText("드립 완료!");
                step2_info.setText("핸드 드립 커피가 완성되었습니다!");
                step2_info2.setText("향긋한 커피를 마시며 저희가 분석한 결과를 확인해보세요.");

            }

        }

    }

}



// 세린 기존 코드 (혹시 몰라서 백업)
//package com.example.starbucksook;
//
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.Socket;
//import java.net.UnknownHostException;
//
//public class Step2Activity extends AppCompatActivity {
//
//    TextView step2_weight;
//    public Socket client;
//    public DataOutputStream dataOutput;
//    public DataInputStream dataInput;
//    public static String SERVER_IP = "192.168.101.101";
//    public static String CONNECT_MSG = "connect";
//    public static String STOP_MSG = "stop";
//    public String base_weight;
//    public static int BUF_SIZE = 100;
//
//    private TextView check1;
//    private TextView check2;
//    private TextView check3;
//
//    private TextView step2_textview;
//    private int step2_flag = 1;
//    private int step3_flag = 1;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_step2);
//
//        check1 = findViewById(R.id.check1);
//        check2 = findViewById(R.id.check2);
//        check3 = findViewById(R.id.check3);
//        step2_textview = findViewById(R.id.step2_textview);
//
//
//        step2_weight = findViewById(R.id.step2_weight);
//        Connect connect = new Connect();
//        connect.execute(CONNECT_MSG);
//        step2_weight.setText("예열된 드립 서버의 물을 비웁니다. 현재 무게: ");         // TODO: 여기에 무게 측정 값 넣으면 된다.
//
//        Button btn = (Button)findViewById(R.id.step2_btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Step2Activity.this, Step3Activity.class);
//
//                startActivity(intent);
//
//            }
//        });
//
//    }
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
//
//                // TODO
//                base_weight = new String(buf1, 0, read_Byte1);
//
//
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
//            step2_weight.setText(""); // Clear the chat box
//            float now_weight = Float.parseFloat(params[0]);
//            float weight;
//
//            weight = now_weight-Float.parseFloat(base_weight);
//            step2_weight.append("예열된 드립 서버의 물을 비웁니다. 물의 무게: " + Float.toString(weight));
//
//            if (step2_flag == 1 && weight > 300) {
//                step2_flag = 0;
//                step2_textview.setText("over 300g ");
//            }
//
//            if (step3_flag == 1 && weight > 800) {
//                step3_flag = 0;
//                step2_textview.setText("over 400g ");
//            }
//
//            check1.setText("now_weight = " + Float.toString(now_weight));
//            check2.setText("base_weight = " + base_weight);
//            check3.setText("weight = " + Float.toString(weight));
//        }
//    }
//
//}
