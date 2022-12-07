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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    public static ArrayList<Moment> springDataList;
    public static ArrayList<Moment> summerDataList;
    public static ArrayList<Moment> fallDataList;
    public static ArrayList<Moment> winterDataList;
    ProgressDialog progressDialog;

    private Handler handler = new Handler();

    final private int PROGRESS_DIALOG = 0;



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

        Intent service = new Intent(MainActivity.this, MusicService.class);
        startService(service);
        bindService(service, conn, BIND_AUTO_CREATE);


        // 위험 권한 요청
        // checkDangerousPermisstions();
        // 접근 권한 요청, 위험 권한을 부여할 권한 지정하는 부분
        String[] permissions = {
                // Manifest.permission.CALL_PHONE,
                //Manifest.permission.DIAL_PHONE, // 위험권한 아님
                //Manifest.permission.SEND_SMS,
                //Manifest.permission.INTERNET,
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
        Thread thread = new Thread(null, getGalleryData); //스레드 생성후 스레드에서 작업할 함수 지정(getDATA)
        thread.start();
        showDialog(PROGRESS_DIALOG); //다이얼로그 팝업
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    springDataList = readImageInMyGallery(1); // 이건 커서 맨 앞에서 시작
                    summerDataList = readImageInMyGallery(2); // 이건 커서 맨 앞에서 시작
                    fallDataList = readImageInMyGallery(3); // 이건 커서 맨 앞에서 시작
                    winterDataList = readImageInMyGallery(4); // 이건 커서 맨 앞에서 시작

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
*/





    }

//////////
    //자료 처리
    private Runnable getGalleryData= new Runnable() {
        public void run() {
            try {
                //원하는 자료 처리(데이터 로딩등)
                try {
                    springDataList = readImageInMyGallery(1); // 이건 커서 맨 앞에서 시작
                    summerDataList = readImageInMyGallery(2); // 이건 커서 맨 앞에서 시작
                    fallDataList = readImageInMyGallery(3); // 이건 커서 맨 앞에서 시작
                    winterDataList = readImageInMyGallery(4); // 이건 커서 맨 앞에서 시작

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.post(updateResults); //처리 완료후 Handler의 Post를 사용해서 이벤트 던짐
            } catch (Exception e) {
                Log.e("getDATA", e.toString());
            }
        }
    };
    private Runnable updateResults = new Runnable() {

        public void run () {
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
                progressDialog.setMessage("Now loading..");
                return progressDialog;

        }
        return null;
    }
/////////////
    // 앱을 맨 처음 실행했을 때 위험 권한 permission 허용을 요청하는 함수
    public void checkPermissions(String[] permissions) {
        // premission들을 string 배열로 가지고 있는 위험 권한 permissions를 받아옴. 전화 바로 걸기와 문자 보내기.
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
        ///////////////////////////
        String[] targets = new String[targetList.size()];
        targetList.toArray(targets);
        ///
        for (int i=0; i< targets.length; i++){
            int permissionCheck = ContextCompat.checkSelfPermission(this, targets[i]); // 현재 앱에서 권한이 있는지를 permissionCheck에 넣음
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // 위험 권한이 아직 허용되지 않은 상태인데 위에서 허용해달라고 눌렀으면 이리로 오게 됨.
                // 위험 권한 허용을 요청해서 이제 기능을 쓸 수 있음
                ActivityCompat.requestPermissions(this, targets, 101);
            }
        }
    }


    /////////////

    private ArrayList<Moment> readImageInMyGallery(int whatSeason) throws ParseException {

        ArrayList<Moment> mdataList = new ArrayList<>();
        boolean externalFlag = false;
        Uri externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; //sd카드 있는 사람은 이거 외부 저장소 가능
        Uri internalUri = MediaStore.Images.Media.INTERNAL_CONTENT_URI; // sd카드 없는 근영이를 위한 internal --NOPE..
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
                MediaStore.Images.Media.MIME_TYPE
        };




        // String selection = MediaStore.Images.Media.DATE_ADDED + ">=5108213426";
        //// 여기에서 select문 넣어서 애초에 처음부터 minMonth ~ maxMonth 사이의 월에 해당하는 것만 가져오기
        // 이걸 했는데도 데이터 처리에 너무 많은 시간이 쓰여서 죽는다면? => 뭔가 이미지 표시도 하고 이미지에 대한 정보도
        // 위의 칼럼에서 가져온 것들 싹다 표시해줘야 할 것 같은....
        // 계절은 못하는...
        //final String orderBy = MediaStore.Images.Media.DATE_ADDED +" DESC";
        // https://hello-bryan.tistory.com/219
        // modified로 최종 수정날짜 알 수 있음
        final String orderBy = MediaStore.Images.Media.DATE_MODIFIED +" DESC";

        Cursor cursor = getContentResolver().query(internalUri, projection, null, null, orderBy);
        //Cursor
        cursor.moveToFirst();
        if (cursor==null|| !cursor.moveToFirst()){
            // 인터널이 널이면 외부로 바꿔주기
            cursor = getContentResolver().query(externalUri, projection, null, null, orderBy);
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
                }else{
                    contentUrl = internalUri.toString() + "/" + cursor.getString(0);
                }
                //String contentUrl = externalUri.toString() + "/" + cursor.getString(0);
                //String contentUrl = internalUri.toString() + "/" + cursor.getString(0);

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




                if (count>=10){break;}

            } while (cursor.moveToNext());
        }

        return mdataList;
    }
}