package smu.hw7_3provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    MyRecyclerViewAdapter adapter;
    public static ArrayList<Moment> dataList;

    int PERMISSION_ALL = 1;
    private String TAG = "저장 날짜들";
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


        // 리사이클러 뷰를 격자 모양으로 보여줌
        // https://black-jin0427.tistory.com/101 링크 참고해서 마저 구현하기
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);

        // 앞에서 어떤 버튼을 눌렀는지에 따라서 선택된 계절을 보여줌
        if (whatSeason.equals("spring")){
            // 봄을 선택했을 때

            dataList = GetSeasonMomentImage();


        }else if(whatSeason.equals("summer")){

        }else if(whatSeason.equals("fall")){

        }else if(whatSeason.equals("winter")){

        }
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new MyRecyclerViewAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

    }
    ///아놔
    /*
     MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
    * */






    // 우리 갤러리에서 필요한 것들 칼럼 통해서 가져오기
    public ArrayList<Moment> GetSeasonMomentImage() {
        ArrayList<Moment> pDataList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                // ADDED 저장된날 / TAKEN 촬영 날짜 s단위

                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                String.valueOf(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        };
        String selection = MediaStore.Images.Media.MIME_TYPE + "='image/jpeg'";
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, selection, null, MediaStore.Images.Media.DATE_ADDED);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int columnUri = cursor.getColumnIndexOrThrow(String.valueOf(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        int columnDateAdded = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

        while (cursor.moveToNext()) {
            String absolutePath = cursor.getString(columnIndex);
            String imagePath = cursor.getString(columnUri);
            Log.d(TAG, String.valueOf(columnDateAdded));
            Moment pData = new Moment();
            pData.setPath(absolutePath);
            pData.setImage_path(imagePath);
            pDataList.add(pData);

        }
        return pDataList;
    }


}