package com.example.starbucksook;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import static android.os.Environment.DIRECTORY_PICTURES;


public class ReportActivity extends AppCompatActivity {

    // 무게 데이터 받아옴
    public float weight_1, weight_2, weight_3, weight_4, weight_5, total_weight, total_time_float;
    public double total_time;
    TextView report_countdown_text;
    TextView report_step_water;

    TextView report_score_content;
    TextView report_score_content2;

    int cnt = 0;

    private BarChart chart;

    // 카메라
    public static final int REQUEST_IMAGE_CAPTURE = 672;
    public String imageFilePath;
    public Uri photoUri;
    Intent intent2;

    public MediaScanner mMediaScanner; // 사진 저장 시 갤러리 폴더에 바로 반영사항을 업데이트 시켜주려면 이 것이 필요하다(미디어 스캐닝)
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // 사진 저장 후 미디어 스캐닝을 돌려줘야 갤러리에 반영됨.
        mMediaScanner = MediaScanner.getInstance(getApplicationContext());

        // 권한 체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        FacebookSdk.sdkInitialize(getApplicationContext()); // 페이스북SDK 초기화
        callbackManager = CallbackManager.Factory.create(); // 콜백메소드 생성
        shareDialog = new ShareDialog(this); // 공유를 위한 다이얼로그 박스

        findViewById(R.id.camera_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent2 = new Intent(ReportActivity.this, CameraActivity.class);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) {

                    }

                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }

                intent2.putExtra("imageFilePath", imageFilePath);
            }
        });

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

        report_countdown_text = findViewById(R.id.report_countdown_text);
        report_step_water = findViewById(R.id.report_step_water);
        report_countdown_text.setText(String.valueOf(Math.round(total_time)));
        report_step_water.setText(String.valueOf(Math.round(total_weight)));


        report_score_content = findViewById(R.id.report_score_content);
        report_score_content2 = findViewById(R.id.report_score_content2);


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

            chart.setData(data);
        }

        chart.getBarData().setBarWidth(barWidth); // specify the width each bar should have
        chart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        chart.setTouchEnabled(false); // chart 터치 막기

        // x축
        XAxis xAxis = chart.getXAxis();
        xAxis.setAxisMinimum(start);
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X축 데이터 표시 위치





        String[] xAxisVals = new String[]{"추출 3", "추출 2", "추출 1", "뜸 들이기", "커피"};

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





        // Score
        int[] recipe = {55, 100, 150, 15, 20};
        int[] yourData = {(int)weight_5, (int)weight_4, (int)weight_3, (int)weight_2, (int)weight_1};
        for (int i = 0; i < 5; i++) {
            int recipeVal = recipe[i];
            int yourDataVal = yourData[i];

            if (Math.abs(recipeVal - yourDataVal) <= 10) {
                cnt++;
            }
        }

        if (cnt == 0) {
            report_score_content.setText("0 %");
            report_score_content2.setText("차근차근 \n해볼까요? 😇");
        } else if (cnt == 1) {
            report_score_content.setText("20 %");
            report_score_content2.setText("조금 더 \n천천히 ☕️");
        } else if (cnt == 2) {
            report_score_content.setText("40 %");
            report_score_content2.setText("한 번 더 \n해볼까요? ☕");
        } else if (cnt == 3) {
            report_score_content.setText("60 %");
            report_score_content2.setText("훌륭해요 😊");
        } else if (cnt == 4) {
            report_score_content.setText("80 %");
            report_score_content2.setText("잘했어요 😋");
        } else if (cnt == 5) {
            report_score_content.setText("100 %");
            report_score_content2.setText("당신은 이미 \n바리스타 👍🏻");
        }

    }


    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();

        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;

            try {
                exif = new ExifInterface(imageFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);
            } else {
                exifDegree = 0;
            }

            String result = "";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
            Date curDate = new Date(System.currentTimeMillis());
            String filename = formatter.format(curDate);

            String strFolderName = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES) + File.separator + "StarbuckSook" + File.separator;
            File file = new File(strFolderName);
            if (!file.exists())
                file.mkdirs();

            File f = new File(strFolderName + "/" + filename + ".png");
            result = f.getPath();

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result = "Save Error fOut";
            }

            // 비트맵 사진 폴더 경로에 저장
            rotate(bitmap, exifDegree).compress(Bitmap.CompressFormat.PNG, 70, fOut);

            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
                // 방금 저장된 사진을 갤러리 폴더 반영 및 최신화
                mMediaScanner.mediaScanning(strFolderName + "/" + filename + ".png");
            } catch (IOException e) {
                e.printStackTrace();
                result = "File close Error";
            }

            startActivity(intent2);


        }
    }

    public int exifOrientationToDegress(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }


    public Bitmap rotate(Bitmap bitmap, int degrees) {

        // 사진 찍고 체크(확인) 누르면 시작
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }


    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };
}
