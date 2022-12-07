package smu.hw7_3provider;


import static smu.hw7_3provider.MainActivity.fallDataList;
import static smu.hw7_3provider.MainActivity.springDataList;
import static smu.hw7_3provider.MainActivity.summerDataList;
import static smu.hw7_3provider.MainActivity.winterDataList;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

// 계절 선택해서 해당하는 화면으로 이동
public class SelectSeason extends AppCompatActivity {
    // 위의 데이터를 가져오는 시간이 길어 progressDialog로 로딩중 보여줌
    ProgressDialog progressDialog;
    private Handler handler = new Handler();
    final private int PROGRESS_DIALOG = 0;
    Button buttonSpring, buttonSummer, buttonFall, buttonWinter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_season);

        // 데이터를 앱 실행 초기부터 가져옴 (처음에는 로딩이 걸리더라도 이후에는 바로바로 사용할 수 있게 하기 위함)
        // 데이터를 가져올 때 OnCreate에서 그대로 가져오면 앱이 중단되기도 함 그래서 스레드로 진행해줌
        Thread thread = new Thread(null, getGalleryData); //스레드 생성후 스레드에서 작업할 함수 지정해줌
        thread.start(); // 스레드 시작시키고
        showDialog(PROGRESS_DIALOG); //다이얼로그 팝업을 띄워 로딩중인걸 알림

        buttonSpring = findViewById(R.id.buttonSpring);
        buttonSummer = findViewById(R.id.buttonSummer);
        buttonFall = findViewById(R.id.buttonFall);
        buttonWinter = findViewById(R.id.buttonWinter);

        buttonSpring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MomentsOfTheSeason.class);
                // 봄이라는 것을 넘겨주기
                intent.putExtra("whatSeason", "spring");
                startActivity(intent);
            }
        });
        buttonSummer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MomentsOfTheSeason.class);
                // 여름이라는 것을 넘겨주기
                intent.putExtra("whatSeason", "summer");
                startActivity(intent);
            }
        });
        buttonFall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MomentsOfTheSeason.class);
                // 가을이라는 것을 넘겨주기
                intent.putExtra("whatSeason", "fall");
                startActivity(intent);
            }
        });
        buttonWinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MomentsOfTheSeason.class);
                // 겨울이라는 것을 넘겨주기
                intent.putExtra("whatSeason", "winter");
                startActivity(intent);
            }
        });
    }


    // 자료 처리 : 계절별 순간들을 앱 처음 실행할 때부터 미리 다 세팅해둬서 앱 실행 도중에는 멈추는 일이 없도록 함
    private Runnable getGalleryData= new Runnable() {
        public void run() {
            try {
                try {
                    // 계절: 봄1, 여름2, 가을3, 겨울4로 번호를 whatSeason으로 넘김
                    springDataList = readImageInMyGallery_final(1);
                    summerDataList = readImageInMyGallery_final(2);
                    fallDataList = readImageInMyGallery_final(3);
                    winterDataList = readImageInMyGallery_final(4);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 처리 완료후 Handler의 Post를 사용해서 이벤트 던짐
                handler.post(updateResults);
            } catch (Exception e) {
                Log.e("getDATA", e.toString());
            }
        }
    };

    private Runnable updateResults = new Runnable() {
        public void run () {
            // 만약에 다 끝났으면 로딩중 다이얼로그를 지워줌
            progressDialog.dismiss();
            removeDialog(PROGRESS_DIALOG);
        }
    };

    @Override
    protected Dialog onCreateDialog (int id) {
        switch (id) {
            case (PROGRESS_DIALOG):
                progressDialog = new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("(로딩중)계절의 순간을 담아오는 중입니다 잠시만 기다려주세요!");
                return progressDialog;
        }
        return null;
    }
    // 외부 저장소에 접근하여 데이터 가져옴. cursor, query, contents provider 핵심 부분
    private ArrayList<Moment> readImageInMyGallery_final(int whatSeason) throws ParseException {
        ArrayList<Moment> mdataList = new ArrayList<>();
        boolean externalFlag = false;
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // 외부 저장소
        Uri internalUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI; //sd카드 같은 외부 저장소 없는 경우를 대비함

        // 각 계절이 2022년 몇월에 해당하는지 담겨져 있는 R.array.계절 배열에서 문자열을 가져와 넣음
        String title_month[] = new String[3];
        Resources resources = getResources();
        // whatSeason 봄1 여름2 가을3 겨울4
        switch (whatSeason){
            case 1:
                title_month = resources.getStringArray(R.array.spring);
                break;
            case 2:
                title_month = resources.getStringArray(R.array.summer);
                break;
            case 3:
                title_month = resources.getStringArray(R.array.fall);
                break;
            case 4:
                title_month = resources.getStringArray(R.array.winter);
                break;
        }

        // 우리가 가져올 칼럼들
        // 이미지 가져올 때 필요한 레코드의 id인 _ID, 촬영날짜 s단위 DATE_TAKEN,
        // 데이터 스트림, 파일 경로 DATA, 제목 TITLE 제목이 핵심
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE
        };

        // 촬영일을 기준으로 내림차순 정렬하여 가장 최근 것부터 줄 세움. 2022년에서만 가져올 것이라 이게 더 효율적 접근
        final String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

        // 위의 projection으로 칼럼들 빼오고 orderBy로 DATE_TAKEN 찍은 날 기준 DESC 내림차순으로 정렬을 해주었다.
        Cursor cursor = getContentResolver().query(internalUri, projection, null, null, orderBy);

        cursor.moveToFirst();
        if (cursor==null|| !cursor.moveToFirst()){
            // 인터널이 널이면 외부로 바꿔주기
            cursor = getContentResolver().query(externalUri, projection, null, null, orderBy);
            externalFlag = true;
        }


        cursor.moveToFirst();
        if (cursor == null || !cursor.moveToFirst()) {
            // 이랬는데도 없으면 기본 이미지 띄워주기
            Log.e("TAG", "cursor null or cursor is empty -- NO IMAGE 이미지가 없어요!");
            Toast noImageToast = Toast.makeText(this.getApplicationContext(),"이미지가 없어요!", Toast.LENGTH_SHORT);
            noImageToast.show();
            // 내장, 외장 메모리에 이미지 없으면 기본 이미지 띄워줌
            // 비트맵 byte 사이즈 줄이기 - 너무 커서 오류 나는 경우 있음
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.fall, options);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 440, 320, true);
            Moment moment = new Moment();
            moment.setBitmapImage(resizedBitmap);
            mdataList.add(moment);
            return mdataList;
        }else{
            int count = 0;
            //int cursor_count = cursor.getCount();
            //Log.d("개수 ----------", String.valueOf(cursor_count));
            do {
                // 내가 어느 저장소에 접근했는지에 따라서 contentUrl 설정
                String contentUrl;
                if (externalFlag){
                    contentUrl = externalUri.toString() + "/" + cursor.getString(0);
                    // o에 해당하는 것 ID
                    //Log.d("어느 저장소?: ", "외부 저장소다!!!!!!!!");
                    //Log.d("externalUri.toString()-----: ", externalUri.toString());
                    //Log.d("cursor.getString(0)-----: ", cursor.getString(0));
                   // Log.d("URI어떻게?: ", contentUrl);
                }else{
                    contentUrl = internalUri.toString() + "/" + cursor.getString(0);
                   // Log.d("어느 저장소?: ", "내부 저장소다!!!!!!!!");
                    //Log.d("externalUri.toString()-----: ", externalUri.toString());
                   // Log.d("cursor.getString(0)-----: ", cursor.getString(0));
                   // Log.d("URI어떻게?: ", contentUrl);
                }

                // MediaStore.Images.Media.DISPLAY_NAME은 20220905_095513.jpg 이렇게 표시됨
                String title = String.valueOf(cursor.getString(3));  // 타이틀 20220905_095513 이렇게 표시됨
                //Log.d("(MediaStore.Images.Media.TITLE ? : ", title);//20220905_095513

                // 사진에 관련된 칼럼들의 데이터를 찍어보면서 사진으로 촬영한 것은 보통 날짜로 저장된다는 것을 확인할 수 있었음
                // 그래서 해당 칼럼을 찾았고, DISPLAY_NAME과 TITLE이 이에 해당됨. DISPLAY_NAME은 확장자까지 나와서 TITLE을 사용하기로 함
                // TITLE에 해당 문자열이 있는지를 확인하여 언제 찍힌 것인지를 알 수 있고 Screenshot 문자열을 제외시켜 화면 캡쳐들은 넘길 수 있음
                // title_month[]에는 strings.xml에서 가져온 각 계절별 날짜 문자열이 들어 있어서 그 문자열이 들어있는지 확인하는 부분임 ▼
                if ((title.contains(title_month[0])||title.contains(title_month[1])||title.contains(title_month[2]))&& !title.contains("Screenshot")){
                    // Screenshot_20220529-000511_YouTube.jpg 이런건 패스하도록 함
                    //Log.d("hello 디버깅중", "-------------------------------" );
                    try {
                        InputStream is = getContentResolver().openInputStream(Uri.parse(contentUrl));
                        // bitmap을 만들어서 Moment 클래스로 만든 객체에 넣어줌. 리사이클러뷰에서 활용하기 위해 객체에 넣어주는 것.
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
                    Log.d("count : " ,String.valueOf(count));
                }
                else{
                    Log.d("해당 안되는 것: ", "해당 안되면 처리 안합니다.--------");
                }
                // 10개의 이미지만 가져오고 중단
                if (count>=10){break;}
                // 다음으로 이동
            } while (cursor.moveToNext());
        }
        // 계절 이미지를 담은 것을 반환
        return mdataList;
    }
}