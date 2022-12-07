package smu.hw7_3provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// 2022년 갤러리 연말정산 메인 화면
public class MainActivity extends AppCompatActivity {
    // 각 계절별로 순간들이 담길 것임
    public static ArrayList<Moment> springDataList;
    public static ArrayList<Moment> summerDataList;
    public static ArrayList<Moment> fallDataList;
    public static ArrayList<Moment> winterDataList;

    // 버튼 클릭하면 배경음악 나오도록
    Button buttonSelectSeason;
    MusicService mService;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MusicService.MusicServiceBinder)service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 서비스 세팅
        Intent service = new Intent(MainActivity.this, MusicService.class);
        startService(service);
        bindService(service, conn, BIND_AUTO_CREATE);
        
        // 접근 권한 요청, 권한을 부여할 권한 지정하는 부분
        // 이미지들 가져올 것이라 외부 저장소 읽는 것 권한 받기
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        checkPermissions(permissions); // 권한 허용할 것인지 물어보는 것 부분 함수
        
        // 버튼 클릭 시 음악 재생, 화면 전환
        buttonSelectSeason = findViewById(R.id.buttonSelectSeason);
        buttonSelectSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 음악 재생
                mService.play();
                // 화면 전환
                Intent intent = new Intent(getApplicationContext(), SelectSeason.class);
                startActivity(intent);
            }
        });


    }


    // 앱을 맨 처음 실행했을 때 권한 permission 허용을 요청하는 함수
    public void checkPermissions(String[] permissions) {
        // premission들을 string 배열로 가지고 있는 위험 권한 permissions를 받아옴. 외부 저장장치 읽기
        ArrayList<String> targetList = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            String curPermission = permissions[i]; // 현재 요청할 permission을 curPermission에 넣고
            int permissionCheck = ContextCompat.checkSelfPermission(this, curPermission); // 현재 앱에서 권한이 있는지를 permissionCheck에 넣음
            //ContextCompat.checkSelfPermission()를 사용하여 앱에 이미 권한을 부여 받았는지 확인
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                // 호출 결과로 PERMISSION_GRANTED or PERMISSION_DENIED 반환 받은 것을 확인
                // 초기 실행에서 권한 허용을 한 후에 다시 앱을 실행했을 때는 이미 권한이 있어서 아래와 같은 토스트 메세지를 띄워줌
                Toast.makeText(this, curPermission + " 권한 있음", Toast.LENGTH_SHORT).show();
            } else {
                // 만약 권한 설정이 허용되어 있지 않은 경우 권한 없음이 토스트 메세지로 뜨고
                Toast.makeText(this, curPermission + " 권한 없음", Toast.LENGTH_SHORT).show();
                // shouldShowRequestPermissionRationale는 사용자가 이전에 권한 요청을 거절했었을 때 true를 리턴하고
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, curPermission)) {
                    //Toast.makeText(this, curPermission + " 권한 설명 필요함.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "이번 거절시 더이상 물어보지 않습니다 -> 권한 없어 기능을 사용할 수 없음", Toast.LENGTH_SHORT).show();
                    // 거절을 2번 하면 이후는 물어보지 않음으로 안내 문구를 보여줌
                    targetList.add(curPermission);
                    // 권한 부여할 용도인 targetList에 현재 물어본 curPermission 넣음
                } else {
                    targetList.add(curPermission);
                }
            }
        }

        String[] targets = new String[targetList.size()];
        targetList.toArray(targets);

        for (int i=0; i< targets.length; i++){
            int permissionCheck = ContextCompat.checkSelfPermission(this, targets[i]); // 현재 앱에서 권한이 있는지를 permissionCheck에 넣음
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // 위험 권한이 아직 허용되지 않은 상태인데 위에서 허용해달라고 눌렀으면 이리로 오게 됨.
                // 위험 권한 허용을 요청해서 이제 기능을 쓸 수 있음
                ActivityCompat.requestPermissions(this, targets, 101);
            }
        }
    }


/////////////////////////아까워서 일단 놔둔 것
    // 외부 저장소에 접근하여 데이터 가져옴. cursor, query, contents provider 핵심 부분
    private ArrayList<Moment> readImageInMyGallery_final_backup(int whatSeason) throws ParseException {
        ArrayList<Moment> mdataList = new ArrayList<>();
        boolean externalFlag = false;
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; // 외부 저장소
        Uri internalUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI; //sd카드 없는 경우를 대비함
        //externalUri.toString()-----:: content://media/external/images/media 이런식으로 나옴

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
        // 이미지 가져올 때 필요한 레코드의 id인 _ID, 촬영날짜 s단위 DATE_TAKEN, 데이터 스트림, 파일 경로 DATA, 제목 TITLE 제목이 핵심
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

        if (cursor == null || !cursor.moveToFirst()) {
            // 이랬는데도 없으면 기본 이미지 띄워주기
            Log.e("TAG", "cursor null or cursor is empty -- NO IMAGE 이미지가 없어요!");
            Toast noImageToast = Toast.makeText(this.getApplicationContext(),"이미지가 없어요!", Toast.LENGTH_SHORT);
            noImageToast.show();
            // 내장, 외장 메모리에 이미지 없으면 기본 이미지 띄워줌
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
            return mdataList;
        }else{
            int count = 0;
            int cursor_count = cursor.getCount();
            Log.d("개수 ----------", String.valueOf(cursor_count));
            do {
                // 내가 어느 저장소에 접근했는지에 따라서 contentUrl 설정
                String contentUrl;
                if (externalFlag){
                    contentUrl = externalUri.toString() + "/" + cursor.getString(0);
                    // o에 해당하는 것 ID
                    Log.d("어느 저장소?: ", "외부 저장소다!!!!!!!!");
                    Log.d("externalUri.toString()-----: ", externalUri.toString());
                    Log.d("cursor.getString(0)-----: ", cursor.getString(0));
                    Log.d("URI어떻게?: ", contentUrl);
                }else{
                    contentUrl = internalUri.toString() + "/" + cursor.getString(0);
                    Log.d("어느 저장소?: ", "내부 저장소다!!!!!!!!");
                    Log.d("externalUri.toString()-----: ", externalUri.toString());
                    Log.d("cursor.getString(0)-----: ", cursor.getString(0));
                    Log.d("URI어떻게?: ", contentUrl);
                }
                //
                //String name = String.valueOf(cursor.getString(3)); // 3 index // 디스플레이 되는 이름 20220905_095513.jpg 이렇게 표시됨
                String title = String.valueOf(cursor.getString(3));  // 타이틀 20220905_095513 이렇게 표시됨
                //Log.d("MediaStore.Images.Media.DISPLAY_NAME#######: ", name); //20220905_095513.jpg
                Log.d("(MediaStore.Images.Media.TITLE ? : ", title);//20220905_095513

                // 처음에는 이미지가 저장된 때를 가져와서 계절별로 보여주려고 했음.
                // 이렇게 하게 되면 스크린 샷도 함께 나오게 됨 ▼
                /*
                // 이미지 저장된 때를 가져옴 우리가 원하는 날짜를 뽑아 오기 위해서는 인덱스 3을 넣어야 함.
                // * DATA ADDED (ms) 값
                 Long value_3 = cursor.getLong(3);
                 Log.d("ms시간 - modified: ", String.valueOf(value_3));
                // 시간 결정 가능
                //Log.d("저장된 날짜 ms : ", String.valueOf(value_3)); // ms 단위임
                // ms 단위 변환
                Calendar calendar = Calendar.getInstance(); //캘린더 클래스 인스턴스 만들고
                calendar.setTimeInMillis(value_3); // ms단위의 저장된 날짜를 세팅하고
                Date date = calendar.getTime(); // 포매팅 하기 위해서
                // 포매팅을 년-월을 가져옴
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                Date test = new Date(value_3);
                String yearNmonth = simpleDateFormat.format(date);
                Date date_yearNmonth = simpleDateFormat.parse(yearNmonth);
                // 원래는 계절별 월을 넣었음 봄 3~6월이면 minMonth가 3월 maxMonth가 6월
                Date date_standard = simpleDateFormat.parse(minMonth);
                Date date_standard2 = simpleDateFormat.parse(maxMonth);
                Log.d("저장된 년+월 : ",yearNmonth); // 년+월
                Log.d("기준 날짜 : ", String.valueOf(date_standard)); // 년+월
                Log.d("test 날짜 : ", String.valueOf(test)); // 년+월
                */
                // 위를 해주고서 겨울인 경우는 2023년 1월 before하면 조금 잘 안되길래 겨울인 경우만 after 하나로 판별했었음
                // 겨울인 경우
               /*
                if (whatSeason == 4){
                    if (test.after(date_standard)){
                        Log.d("hello", "-------------------------------" );
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
                        Log.d("count : " ,String.valueOf(count));

                    }
                    else{Log.d("아니지!", " 빠르면 처리 안합니다.--------"); }

                }
                else{
                    //겨울이 아닌 경우
                    if (test.after(date_standard) && test.before(date_standard2)){
                        Log.d("hello", "-------------------------------" );
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
                        Log.d("count : " ,String.valueOf(count));

                    }
                    else{Log.d("아니지!", " 빠르면 처리 안합니다.--------"); }
                }
                */

                // 그러나 사진에 관련된 칼럼들의 데이터를 찍어보면서 사진으로 촬영한 것은 보통 날짜로 저장된다는 것을 확인할 수 있었음
                // 그래서 해당 칼럼을 찾았고, DISPLAY_NAME과 TITLE이 이에 해당됨. DISPLAY_NAME은 확장자까지 나와서 TITLE을 사용하기로 함
                // TITLE에 해당 문자열이 있는지를 확인하여 언제 찍힌 것인지를 알 수 있고 Screenshot 문자열을 제외시켜 화면 캡쳐들은 넘길 수 있음
                // title_month[]에는 strings.xml에서 가져온 각 계절별 날짜 문자열이 들어 있어서 그 문자열이 들어있는지 확인하는 부분임 ▼
                if ((title.contains(title_month[0])||title.contains(title_month[1])||title.contains(title_month[2]))&& !title.contains("Screenshot")){
                    // Screenshot_20220529-000511_YouTube.jpg 이런건 패스하도록 함
                    Log.d("hello 디버깅중", "-------------------------------" );
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
                    Log.d("count : " ,String.valueOf(count));

                }
                else{
                    Log.d("아니지!", "해당 안되면 처리 안합니다.--------");
                }


                if (count>=10){break;}

            } while (cursor.moveToNext());
        }

        return mdataList;
    }

    private ArrayList<Moment> readImageInMyGallery(int whatSeason) throws ParseException {

        ArrayList<Moment> mdataList = new ArrayList<>();
        boolean externalFlag = false;
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; //sd카드 있는 사람은 이거 외부 저장소 가능
        Uri internalUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI; // sd카드 없을 경우를 위한 internal
        //externalUri.toString()-----:: content://media/external/images/media

        String minMonth="", maxMonth="";

        switch (whatSeason){
            case 1:
                minMonth = "2022-03";
                maxMonth = "2022-06";
                break;
            case 2:
                minMonth = "2022-06";
                maxMonth = "2022-09";
                break;

            case 3:
                minMonth = "2022-09";
                maxMonth = "2022-12";
                break;

            case 4:
                //minMonth = "2022-12";
                minMonth = "2022-01";
                //maxMonth = "2023-01";
                maxMonth = "2023-03";
                break;
        }

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE
        };

        // String selection = MediaStore.Images.Media.DATE_ADDED + ">=5108213426";
        //// 여기에서 select문 넣어서 애초에 처음부터 minMonth ~ maxMonth 사이의 월에 해당하는 것만 가져오기
        // 이걸 했는데도 데이터 처리에 너무 많은 시간이 쓰여서 죽는다면? => 뭔가 이미지 표시도 하고 이미지에 대한 정보도
        // 위의 칼럼에서 가져온 것들 싹다 표시해줘야 할 것 같은....
        // 계절은 못하는...
        //final String orderBy = MediaStore.Images.Media.DATE_ADDED +" DESC";
        // https://hello-bryan.tistory.com/219
        // modified로 최종 수정날짜 알 수 있음
        //final String orderBy = MediaStore.Images.Media.DATE_MODIFIED +" DESC";
        final String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";


/////////////////////
        //3번째 시도
        /*
        File cameraFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        String cameraFilePath = cameraFile.getPath();
        Uri fileUri = Uri.parse(cameraFilePath);
        String filePath = fileUri.getPath();
        */

        Cursor cursor = getContentResolver().query(internalUri, projection, null, null, orderBy);
        //Cursor cursor = getContentResolver().query(internalUri, projection,  MediaStore.Images.Media.DATA + " like ? ", new String[] {"%Camera%"}, orderBy);
        ///3//Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection, "_data = '" + filePath + "'", null,null);
        //Cursor
        cursor.moveToFirst();
        if (cursor==null|| !cursor.moveToFirst()){
            // 인터널이 널이면 외부로 바꿔주기
            cursor = getContentResolver().query(externalUri, projection, null, null, orderBy);
            //cursor = getContentResolver().query(externalUri, projection,  MediaStore.Images.Media.DATA + " like ? ", new String[] {"%Camera%"}, orderBy);
            ///3//cursor = cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection, "_data = '" + filePath + "'", null,null);
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
            int cursor_count = cursor.getCount();
            Log.d("개수 ----------", String.valueOf(cursor_count));
            do {

                String contentUrl;
                if (externalFlag){
                    contentUrl = externalUri.toString() + "/" + cursor.getString(0);
                    Log.d("externalUri.toString()-----: ", externalUri.toString());
                    Log.d("cursor.getString(0)-----: ", cursor.getString(0));
                    Log.d("URI어떻게?: ", contentUrl);
                }else{
                    contentUrl = internalUri.toString() + "/" + cursor.getString(0);
                    Log.d("externalUri.toString()-----: ", externalUri.toString());
                    Log.d("cursor.getString(0)-----: ", cursor.getString(0));
                    Log.d("URI어떻게?: ", contentUrl);
                }
                //String contentUrl = externalUri.toString() + "/" + cursor.getString(0);
                //String contentUrl = internalUri.toString() + "/" + cursor.getString(0);
                ///마지막 시도
                String name = String.valueOf(cursor.getString(3)); //3 index // 디스플레이 되는 이름
                String title = String.valueOf(cursor.getString(6)); //6 index // 타이틀
                Log.d("MediaStore.Images.Media.DISPLAY_NAME#######: ", name); //20220905_095513.jpg
                Log.d("(MediaStore.Images.Media.TITLE%%%%%?: ", title);//20220905_095513
                ////
                // 이미지 저장된 때를 가져옴
                // 우리가 원하는 날짜를 뽑아 오기 위해서는 인덱스 3을 넣어야 함.
                // * DATA ADDED (ms) 값
                Long value_3 = cursor.getLong(3);
                Log.d("ms시간 - modified: ", String.valueOf(value_3));
                // 시간 결정 가능

                //Log.d("저장된 날짜 ms : ", String.valueOf(value_3)); // ms 단위임
                // ms 단위 변환
                Calendar calendar = Calendar.getInstance(); //캘린더 클래스 인스턴스 만들고
                calendar.setTimeInMillis(value_3); // ms단위의 저장된 날짜를 세팅하고
                Date date = calendar.getTime(); // 포매팅 하기 위해서

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                Date test = new Date(value_3);

                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");

                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                // <--millisecond는 "sss"가 아니라 "SSS"로 해야 정확하게 보존된다.
                //Date timeInDate = new Date(dateAdded);

                String yearNmonth = simpleDateFormat.format(date);
                Date date_yearNmonth = simpleDateFormat.parse(yearNmonth);

                Date date_standard = simpleDateFormat.parse(minMonth);
                Date date_standard2 = simpleDateFormat.parse(maxMonth);

                //String yearS = year.format(date);
                //String monthS = month.format(date);

                Log.d("저장된 년+월 : ",yearNmonth); // 년+월
                Log.d("기준 날짜 : ", String.valueOf(date_standard)); // 년+월
                Log.d("test 날짜 : ", String.valueOf(test)); // 년+월

                // Log.d("저장된 년 : ",yearS); // 년
                //Log.d("저장된 월 ",monthS); // 월
                // 겨울인 경우
               /*
                if (whatSeason == 4){
                    if (test.after(date_standard)){
                        Log.d("hello", "-------------------------------" );
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
                        Log.d("count : " ,String.valueOf(count));

                    }
                    else{Log.d("아니지!", " 빠르면 처리 안합니다.--------"); }

                }
                else{
                    //겨울이 아닌 경우
                    if (test.after(date_standard) && test.before(date_standard2)){
                        Log.d("hello", "-------------------------------" );
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
                        Log.d("count : " ,String.valueOf(count));

                    }
                    else{Log.d("아니지!", " 빠르면 처리 안합니다.--------"); }
                }
                */

                if ((title.contains("202203")||title.contains("202204")||title.contains("202205"))&& !title.contains("Screenshot")){
                    //Screenshot_20220529-000511_YouTube.jpg 이런건 패스하게
                    Log.d("hello", "-------------------------------" );
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
                    Log.d("count : " ,String.valueOf(count));

                }
                else{Log.d("아니지!", " 빠르면 처리 안합니다.--------"); }


                if (count>=10){break;}

            } while (cursor.moveToNext());
        }

        return mdataList;
    }

}