package smu.hw7_3provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
// 선택된 계절에 찍은 이미지들 보여줄 부분

public class MomentsOfTheSeason extends AppCompatActivity {
    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    public static ArrayList<Moment> dataList;
    TextView title;
    ImageView icon;
    LinearLayout layout;


    private String TAG = "저장 날짜들";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_of_the_season);
        title = findViewById(R.id.momentsTitle);
        icon = findViewById(R.id.seasonIcon);
        layout = findViewById(R.id.layout);

        Log.d("봄 선택됨", "0000000000000000000000000000");
        //Intent로 어떤 계절을 볼건지 가져옴
        Intent intent =getIntent();
        String whatSeason = intent.getStringExtra("whatSeason");




        // 앞에서 어떤 버튼을 눌렀는지에 따라서 선택된 계절을 보여줌
        if (whatSeason.equals("spring")){
            // 봄을 선택했을 때
            // 화면 스타일 변경
            title.setText("봄날의 순간들");
            icon.setImageResource(R.drawable.resize_spring);
            layout.setBackgroundColor(R.color.spring);

            Log.d("봄 선택됨", "0000000000000000000000000000");
           // dataList = GetSeasonMomentImage();
           // dataList = getDataList();

            dataList = readImageInMyGallery(3, 5);


        }else if(whatSeason.equals("summer")){
            // 화면 스타일 변경
            title.setText("여름날의 순간들");
            icon.setImageResource(R.drawable.resize_summer);
            layout.setBackgroundColor(R.color.summer);

        }else if(whatSeason.equals("fall")){
            // 화면 스타일 변경
            title.setText("가을의 순간들");
            icon.setImageResource(R.drawable.resize_fall);
            layout.setBackgroundColor(R.color.fall);

        }else if(whatSeason.equals("winter")){
            // 화면 스타일 변경
            title.setText("겨울의 순간들");
            icon.setImageResource(R.drawable.resize_winter);
            layout.setBackgroundColor(R.color.winter);

        }
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new MyRecyclerViewAdapter(this, dataList);
        recyclerView.setAdapter(adapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        // 리사이클러 뷰를 격자 모양으로 보여줌
        // https://black-jin0427.tistory.com/101 링크 참고해서 마저 구현하기
        //불규칙 레이아웃
        StaggeredGridLayoutManager staggeredGridLayoutManager
                = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //레이아웃 매니저 연결
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

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
        final String orderBy = MediaStore.Images.Media.DATE_ADDED;
        Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                imageSet, null, null, orderBy +"ASC");
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

    //mp3
    // https://ddolcat.tistory.com/622
    // 미디어 db table column정보
    // https://aroundck.tistory.com/190
    // https://choidev-1.tistory.com/74 참고 주소
    private ArrayList<Moment> readImageInMyGallery(int minMonth, int maxMonth) {
        ArrayList<Moment> mdataList = new ArrayList<>();
        boolean externalFlag = false;
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; //sd카드 있는 사람은 이거 외부 저장소 가능
        Uri internalUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI; // sd카드 없는 근영이를 위한 internal --NOPE..

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE
        };



       // String selection = MediaStore.Images.Media.DATE_ADDED + ">=5108213426";
        //// 여기에서 select문 넣어서 애초에 처음부터 minMonth ~ maxMonth 사이의 월에 해당하는 것만 가져오기
        // 이걸 했는데도 데이터 처리에 너무 많은 시간이 쓰여서 죽는다면? => 뭔가 이미지 표시도 하고 이미지에 대한 정보도
        // 위의 칼럼에서 가져온 것들 싹다 표시해줘야 할 것 같은....
        // 계절은 못하는...

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

            // 비트맵 byte 사이즈 줄이기 방법 : https://it77.tistory.com/99
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.fall, options);
            // 화면에 맞게 resized하는 것도 있으나 일단 /4로 조절
            // http://egloos.zum.com/pavecho/v/7210478
            //Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/4, bitmap.getHeight()/4, true);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 440, 320, true);
            Moment moment = new Moment();
            moment.setBitmapImage(resizedBitmap);
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

                // 이미지 저장된 때를 가져옴
                // 우리가 원하는 날짜를 뽑아 오기 위해서는 인덱스 3을 넣어야 함.
                // * DATA ADDED (ms) 값
                Long value_3 = cursor.getLong(3);




                Log.d("저장된 날짜 ms : ", String.valueOf(value_3)); // ms 단위임

                Calendar calendar = Calendar.getInstance(); //캘린더 클래스 인스턴스 만들고
                calendar.setTimeInMillis(value_3); // ms단위의 저장된 날짜를 세팅하고
                Date date = calendar.getTime(); // 포매팅 하기 위해서
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");

                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");

                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                // <--millisecond는 "sss"가 아니라 "SSS"로 해야 정확하게 보존된다.
                //Date timeInDate = new Date(dateAdded);
                String yearNmonth = simpleDateFormat.format(date);
                Log.d("저장된 월 : ", yearNmonth); // 월






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
                if (count>=30){break;}

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