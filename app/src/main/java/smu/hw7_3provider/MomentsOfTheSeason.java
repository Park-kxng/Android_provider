package smu.hw7_3provider;

import static smu.hw7_3provider.MainActivity.fallDataList;
import static smu.hw7_3provider.MainActivity.springDataList;
import static smu.hw7_3provider.MainActivity.summerDataList;
import static smu.hw7_3provider.MainActivity.winterDataList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
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

}