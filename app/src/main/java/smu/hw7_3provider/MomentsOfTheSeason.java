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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
// 선택된 계절에 찍은 이미지들 보여줄 부분

public class MomentsOfTheSeason extends AppCompatActivity {
    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    public static ArrayList<Moment> dataList;


    private String TAG = "저장 날짜들";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_of_the_season);

        Log.d("봄 선택됨", "0000000000000000000000000000");
        //Intent로 어떤 계절을 볼건지 가져옴
        Intent intent =getIntent();
        String whatSeason = intent.getStringExtra("whatSeason");


        // 리사이클러 뷰를 격자 모양으로 보여줌
        // https://black-jin0427.tistory.com/101 링크 참고해서 마저 구현하기
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);

        // 앞에서 어떤 버튼을 눌렀는지에 따라서 선택된 계절을 보여줌
        if (whatSeason.equals("spring")){
            // 봄을 선택했을 때
            Log.d("봄 선택됨", "0000000000000000000000000000");
           // dataList = GetSeasonMomentImage();
           // dataList = getDataList();

            dataList = readImageInMyGallery();


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

    public ArrayList<Moment> getDataList() {
        ArrayList<Moment> mdataList = new ArrayList<>();
        String[] imageSet = new String[]{

                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            return null;
        }
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                imageSet, null, null, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            Moment moment = new Moment();
            int dataAdded = cursor.getInt(0); //DATA_ADDED
            moment.setDataAdded(dataAdded);
            Log.d("이건 dataAdded", String.valueOf(dataAdded));
            String data = cursor.getString(1); //DATA
            moment.setData(data);
            Log.d("이건 data", String.valueOf(data));
            long data_id = cursor.getLong(2); // _ID
            moment.setData_id(data_id);
            Log.d("이건 data_id", String.valueOf(data_id));
            mdataList.add(moment);
        }

        cursor.close();

        return mdataList;
    }
    private ArrayList<Moment> readImageInMyGallery() {
        ArrayList<Moment> mdataList = new ArrayList<>();
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
        };

        Cursor cursor = getContentResolver().query(externalUri, projection, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("TAG", "cursor null or cursor is empty");
            return null;
        }
        int count = 0;
        do {

            String contentUrl = externalUri.toString() + "/" + cursor.getString(0);

            try {
                InputStream is = getContentResolver().openInputStream(Uri.parse(contentUrl));

                if(is != null){
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Moment moment = new Moment();
                    moment.setBitmapImage(bitmap);
                    mdataList.add(moment);
                    is.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            count+=1;
            if (count>=10){break;}
        } while (cursor.moveToNext());
        return mdataList;
    }
/*
    // 우리 갤러리에서 필요한 것들 칼럼 통해서 가져오기
    public ArrayList<Moment> GetSeasonMomentImage() {
        ArrayList<Moment> pDataList = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 우리가 원하는 데이터 셋을 project에 만들어서 넣어줌
        String[] projection = new String[]{
                // ADDED 저장된날 / TAKEN 촬영 날짜 s단위

                MediaStore.Images.Media.DATE_ADDED,
               // MediaStore.Images.Media.DATE_TAKEN,
              //  MediaStore.Images.Media.DISPLAY_NAME,
               // MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA,
              //  MediaStore.Images.Media._ID

        };
        //String selection = MediaStore.Images.Media.MIME_TYPE + "='image/jpeg'";
        // uri를 가져올거임 위의 데이터셋에 해당하는 것들에서 추가된 순서로
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED);


        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int columnUri = cursor.getColumnIndexOrThrow(String.valueOf(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        int columnDateAdded = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);

        cursor.moveToFirst(); // 처음으로 이동
        do {
            String absolutePath = cursor.getString(columnIndex);
            int imagePath = Integer.parseInt(cursor.getString(columnUri));
            Log.d(TAG, String.valueOf(columnDateAdded));
            Moment pData = new Moment();
            pData.setPath(absolutePath);
            pData.setImage_path(imagePath);
            pDataList.add(pData);
        }
        while (cursor.moveToNext());

        return pDataList;
    }

*/
}