package smu.hw7_3provider;

import static smu.hw7_3provider.MomentsOfTheSeason.dataList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

// 이미지 클릭하면 보여줄 부분
public class BeautifulMoment extends AppCompatActivity {
    ImageView imageViewClickPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautiful_moment);
        Intent intent =getIntent();
        int clickPosition = intent.getIntExtra("clickPosition", 0);

        imageViewClickPosition = findViewById(R.id.imageViewClickPosition);
        imageViewClickPosition.setImageBitmap(dataList.get(clickPosition).getBitmapImage());



    }
}