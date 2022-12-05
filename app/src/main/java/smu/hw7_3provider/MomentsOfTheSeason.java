package smu.hw7_3provider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
    // 내부저장소 vs 외부 저장소
    // https://hellose7.tistory.com/96
    // 정신건강에 좋은 파일 공유하기
    //https://crystalcube.co.kr/category/Android
    // 외부 저장소에서 mp3 가져와 재생
    // https://developer88.tistory.com/192
    // 컨텐트 프로바이더
    // https://50billion-dollars.tistory.com/entry/Android-%EC%BD%98%ED%85%90%ED%8A%B8-%ED%94%84%EB%A1%9C%EB%B0%94%EC%9D%B4%EB%8D%94

    // https://choidev-1.tistory.com/74 참고 주소
    private ArrayList<Moment> readImageInMyGallery() {
        ArrayList<Moment> mdataList = new ArrayList<>();
        boolean externalFlag = false;
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; //sd카드 있는 사람은 이거 외부 저장소 가능
        Uri internalUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI; // sd카드 없는 근영이를 위한 internal

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
        };
        Cursor cursor = getContentResolver().query(internalUri, projection, null, null, null);
        //Cursor
        if (cursor==null|| !cursor.moveToFirst()){
            // 인터널이 널이면 외부로 바꿔주기
            cursor = getContentResolver().query(externalUri, projection, null, null, null);
            externalFlag = true;
        }


        if (cursor == null || !cursor.moveToFirst()) {
            // 이랬는데도 없으면 기본 이미지
            Log.e("TAG", "cursor null or cursor is empty");
            // 내장, 외부 메모리에 이미지 없으면 기본 이미지 띄워줌
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.fall);
            Moment moment = new Moment();
            moment.setBitmapImage(bitmap);
            mdataList.add(moment);
            return mdataList; // 여기에 비어있을 때 표시하는거 넣기
        }else{
            int count = 0;
            do {
                String contentUrl;
                if (externalFlag){
                    contentUrl = externalUri.toString() + "/" + cursor.getString(0);
                }else{
                    contentUrl = internalUri.toString() + "/" + cursor.getString(0);
                }
                //String contentUrl = externalUri.toString() + "/" + cursor.getString(0);
                //String contentUrl = internalUri.toString() + "/" + cursor.getString(0);
                try {
                    InputStream is = getContentResolver().openInputStream(Uri.parse(contentUrl));
                    // bitmap 만드는 법 https://developer88.tistory.com/499
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
        }

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