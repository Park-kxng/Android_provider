package smu.hw7_3provider;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// 2022년 갤러리 연말정산 메인 화면
public class MainActivity extends AppCompatActivity {
    Button buttonSelectSeason;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSelectSeason = findViewById(R.id.buttonSelectSeason);
        buttonSelectSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectSeason.class);
                startActivity(intent);
            }
        });


    }




}