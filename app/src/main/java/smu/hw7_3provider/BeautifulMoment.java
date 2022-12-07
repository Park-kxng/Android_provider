package smu.hw7_3provider;

import static smu.hw7_3provider.MainActivity.fallDataList;
import static smu.hw7_3provider.MainActivity.springDataList;
import static smu.hw7_3provider.MainActivity.summerDataList;
import static smu.hw7_3provider.MainActivity.winterDataList;
import static smu.hw7_3provider.MomentsOfTheSeason.whatSeason;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

// 이미지 클릭하면 보여줄 부분
public class BeautifulMoment extends AppCompatActivity {

    ImageView imageViewClickPosition;
    ImageView imageViewAnimation;
    Animation anim_falling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beautiful_moment);

        // momentsOfTheSeason 에서 넘긴 Intent 값 받아오기 clickPosition 어떤 사진을 클릭했는지
        Intent intent =getIntent();
        int clickPosition = intent.getIntExtra("clickPosition", 0);
        // 이미지뷰 객체 만들고
        imageViewClickPosition = findViewById(R.id.imageViewClickPosition);
        imageViewAnimation = findViewById(R.id.imageViewAnimation);
        // 떨어지는 애니메이션
        anim_falling = AnimationUtils.loadAnimation(this, R.anim.falling_animation);
        
        // 선택한 이미지를 폴라로이드 안에 넣어 지금 보고 있는 계절의 아름다운 순간들 가져와서 보여주기 + 애니메이션
        if (whatSeason.equals("spring")){
            imageViewClickPosition.setImageBitmap(springDataList.get(clickPosition).getBitmapImage());
            imageViewAnimation.setImageResource(R.drawable.spring_flower2);
            imageViewAnimation.startAnimation(anim_falling);
        }else if(whatSeason.equals("summer")){
            imageViewClickPosition.setImageBitmap(summerDataList.get(clickPosition).getBitmapImage());
            imageViewAnimation.setImageResource(R.drawable.summer_item);
            imageViewAnimation.startAnimation(anim_falling);
        }else if(whatSeason.equals("fall")){
            imageViewClickPosition.setImageBitmap(fallDataList.get(clickPosition).getBitmapImage());
            imageViewAnimation.setImageResource(R.drawable.fall_falling);
            imageViewAnimation.startAnimation(anim_falling);
        }else if(whatSeason.equals("winter")){
            imageViewClickPosition.setImageBitmap(winterDataList.get(clickPosition).getBitmapImage());
            imageViewAnimation.setImageResource(R.drawable.winter_item);
            imageViewAnimation.startAnimation(anim_falling);
        }

    }

}