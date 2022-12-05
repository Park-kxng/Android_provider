package smu.hw7_3provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
// 선택된 계절에 찍은 이미지들 보여줄 부분

public class MomentsOfTheSeason extends AppCompatActivity {
    RecyclerView recyclerView;
    public static ArrayList<Moment> dataList;

    int PERMISSION_ALL = 1;
    private String TAG = "OurTAG";
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_moments_of_the_season);

        //Intent로 어떤 계절을 볼건지 가져옴
        Intent intent =getIntent();
        String whatSeason = intent.getStringExtra("whatSeason");
        recyclerView = findViewById(R.id.recyclerView);

        // 리사이클러 뷰를 격자 모양으로 보여줌
        // https://black-jin0427.tistory.com/101 링크 참고해서 마저 구현하기
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);

        // 앞에서 어떤 버튼을 눌렀는지에 따라서 선택된 계절을 보여줌
        if (whatSeason.equals("spring")){
            // 봄을 선택했을 때
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }

            //백스레드 실행시켜 이미지 로드
            BackThread backThread = new BackThread(handler);
            backThread.start();
            try {
                backThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(whatSeason.equals("summer")){

        }else if(whatSeason.equals("fall")){

        }else if(whatSeason.equals("winter")){

        }

    }
    ///아놔
    /*
     MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
    * */





    private void getPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        }, 10);
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 11) {
                System.out.println("Thread 전달");
                //메인스레드에서 데이터 크기 확인. 전달 성공했는지 확인하기 위함.
                System.out.println("Size of photo data list in MainThread: " + dataList.size());
            }
            return true;
        }
    });



    // 저장소 권한 확인 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(), "앱 권한 설정이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 잘 되고 있나 찍어보기
            Log.d(TAG, "Permission: " + permissions[0] + "was" + grantResults[0]);
        } else {
            Log.d(TAG, "Permission denied");
        }
    }

    //갤러리 동기화
    public ArrayList<Moment> ImageReady() {
        ArrayList<Moment> pDataList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };
        String selection = MediaStore.Images.Media.MIME_TYPE + "='image/jpeg'";
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, selection, null, MediaStore.Images.Media.DATE_ADDED);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        while (cursor.moveToNext()) {
            String absolutePath = cursor.getString(columnIndex);
            Moment pData = new Moment();
            pData.setPath(absolutePath);
            pDataList.add(pData);

        }
        return pDataList;
    }

    //백스레드에서 갤러리 동기화 실행
    class BackThread extends Thread {
        private Handler handler;

        BackThread(Handler handler) {
            this.handler = handler;
        }

        ArrayList<Moment> pDataBackList = new ArrayList<>();

        @Override
        public void run() {
            pDataBackList = ImageReady();

            //백스레드에서 데이터의 크기 확인
            System.out.println("Size of photo data list in BackThread: " + pDataBackList.size());

            MomentsOfTheSeason.dataList = pDataBackList;
            handler.sendEmptyMessage(11);
        }
    }
}