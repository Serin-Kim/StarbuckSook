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

    TextView step2_weight;
    public Socket client;
    public DataOutputStream dataOutput;
    public DataInputStream dataInput;
    public static String SERVER_IP = "192.168.101.101";
//    public static String SERVER_IP = "192.168.0.7";
    public static String CONNECT_MSG = "connect";
    public static String STOP_MSG = "stop";
    public String base_weight;
    public static int BUF_SIZE = 100;

    private TextView check1;
    private TextView check2;
    private TextView check3;

    private TextView step2_textview;
    private int step2_flag = 1;
    private int step3_flag = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);

        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        step2_textview = findViewById(R.id.step2_textview);


        step2_weight = findViewById(R.id.step2_weight);
        Connect connect = new Connect();
        connect.execute(CONNECT_MSG);
        step2_weight.setText("예열된 드립 서버의 물을 비웁니다. 현재 무게: ");         // TODO: 여기에 무게 측정 값 넣으면 된다.

        Button btn = (Button)findViewById(R.id.step2_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step2Activity.this, Step3Activity.class);

                startActivity(intent);

            }
        });


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

                // TODO
                base_weight = new String(buf1, 0, read_Byte1);
//                check2.setText("base_weight = " + base_weight);


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

            weight = now_weight-Float.parseFloat(base_weight);
            step2_weight.append("예열된 드립 서버의 물을 비웁니다. 물의 무게: " + Float.toString(weight));

            if (step2_flag == 1 && weight > 300) {
                step2_flag = 0;
                step2_textview.setText("over 300g ");
            }

            if (step3_flag == 1 && weight > 800) {
                step3_flag = 0;
                step2_textview.setText("over 400g ");
            }

            check1.setText("now_weight = " + Float.toString(now_weight));
            check2.setText("base_weight = " + base_weight);
            check3.setText("weight = " + Float.toString(weight));


        }



    }

}
