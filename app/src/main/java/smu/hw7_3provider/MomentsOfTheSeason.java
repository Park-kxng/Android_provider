package smu.hw7_3provider;

import static smu.hw7_3provider.MainActivity.fallDataList;
import static smu.hw7_3provider.MainActivity.springDataList;
import static smu.hw7_3provider.MainActivity.summerDataList;
import static smu.hw7_3provider.MainActivity.winterDataList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
// 선택된 계절에 찍은 이미지들 보여줄 부분
public class MomentsOfTheSeason extends AppCompatActivity {
    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    public static ArrayList<Moment> dataList;
    TextView title;
    ImageView icon;
    LinearLayout layout;

    public static  String whatSeason = "spring";
    private String TAG = "저장 날짜들";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moments_of_the_season);
        title = findViewById(R.id.momentsTitle);
        icon = findViewById(R.id.seasonIcon);
        layout = findViewById(R.id.layout);
        recyclerView = findViewById(R.id.recyclerView);

        //Intent로 어떤 계절을 볼건지 가져옴
        Intent intent =getIntent();
        whatSeason = intent.getStringExtra("whatSeason");

        // 앞에서 어떤 버튼을 눌렀는지에 따라서 선택된 계절을 보여줌
        // 화면 스타일 변경
        if (whatSeason.equals("spring")){
            // 봄을 선택했을 때
            title.setText("봄날의 순간들");
            icon.setImageResource(R.drawable.resize_spring);
            layout.setBackgroundColor(Color.rgb(71, 183, 73));
            //Log.d("봄 선택됨", "0000000000000000000000000000");
            recyclerView.setBackgroundColor(Color.rgb(207, 255, 209));
            adapter = new MyRecyclerViewAdapter(this, springDataList);
        }else if(whatSeason.equals("summer")){
            title.setText("여름날의 순간들");
            icon.setImageResource(R.drawable.resize_summer);
            layout.setBackgroundColor(Color.rgb(255, 210, 5));
            recyclerView.setBackgroundColor(Color.rgb(255, 232, 162));
            adapter = new MyRecyclerViewAdapter(this, summerDataList);
        }else if(whatSeason.equals("fall")){
            title.setText("가을의 순간들");
            icon.setImageResource(R.drawable.resize_fall);
            layout.setBackgroundColor(Color.rgb(241, 116, 34));
            recyclerView.setBackgroundColor(Color.rgb(248, 191, 174));
            adapter = new MyRecyclerViewAdapter(this, fallDataList);
        }else if(whatSeason.equals("winter")){
            title.setText("겨울의 순간들");
            icon.setImageResource(R.drawable.resize_winter);
            layout.setBackgroundColor(Color.rgb(45, 170, 226));
            recyclerView.setBackgroundColor(Color.rgb(184, 230, 255));
            adapter = new MyRecyclerViewAdapter(this, winterDataList);

        }
        recyclerView.setAdapter(adapter);
        //  리사이클러 뷰 불규칙 레이아웃인데 2개씩 넣어줌
        StaggeredGridLayoutManager staggeredGridLayoutManager
                = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //레이아웃 매니저 연결
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

    }

    // 리사이클러 뷰를 격자 모양으로 보여줌
    // https://black-jin0427.tistory.com/101 링크 참고해서 마저 구현하기
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

}