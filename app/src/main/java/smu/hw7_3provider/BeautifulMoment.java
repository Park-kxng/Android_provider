package smu.hw7_3provider;

import static smu.hw7_3provider.MomentsOfTheSeason.dataList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

// 이미지 클릭하면 보여줄 부분
public class BeautifulMoment extends AppCompatActivity {

    ImageView imageViewClickPosition;
    Button btn_musicStop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautiful_moment);

        // momentsOfTheSeason 에서 넘긴 Intent 값 받아오기
        Intent intent =getIntent();
        int clickPosition = intent.getIntExtra("clickPosition", 0);
        // 선택한 이미지를 폴라로이드 안에 넣기
        imageViewClickPosition = findViewById(R.id.imageViewClickPosition);
        imageViewClickPosition.setImageBitmap(dataList.get(clickPosition).getBitmapImage());}

}