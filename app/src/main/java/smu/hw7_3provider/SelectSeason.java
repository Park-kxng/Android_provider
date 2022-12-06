package smu.hw7_3provider;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// 계절 선택해서 해당하는 화면으로 이동
public class SelectSeason extends AppCompatActivity {
    Button buttonSpring, buttonSummer, buttonFall, buttonWinter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_season);

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
}