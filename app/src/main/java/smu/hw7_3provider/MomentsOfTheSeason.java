package smu.hw7_3provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
// 선택된 계절에 찍은 이미지들 보여줄 부분

public class MomentsOfTheSeason extends AppCompatActivity {
    RecyclerView recyclerView;

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

        }else if(whatSeason.equals("summer")){

        }else if(whatSeason.equals("fall")){

        }else if(whatSeason.equals("winter")){

        }

    }

    ///
    /*
    // 안드로이드 Developers 문서 링크 : https://developer.android.com/reference/android/provider/MediaStore.MediaColumns
   MediaStore.Images.Media.EXTERNAL_CONTENT_URI : uri
    MediaStore.Images.Media.MIME_TYPE : 미디어 항목의 MIME 유형
    MediaStore.Images.Media.ALBUM: 앨범 이름
    MediaStore.Images.Media._ID
MediaStore.Images.Media.DATA :디스크의 미디어 항목에 대한 절대 파일 시스템 경로
MediaStore.Images.Media.DISPLAY_NAME : 미디어 항목의 표시 이름
MediaStore.Images.Media.TITLE :MediaMetadataRetriever#METADATA_KEY_TITLE 이 미디어 항목에서 추출 된 인덱싱된 값
MediaStore.Images.Media.DATE_TAKEN
: 이 미디어 항목 의 인덱싱된 값 MediaMetadataRetriever#METADATA_KEY_DATE
또는 ExifInterface#TAG_DATETIME_ORIGINAL추출된 값

MediaStore.Images.Media.DATE_ADDED : 미디어 항목이 처음 추가된 시간
    * */
}