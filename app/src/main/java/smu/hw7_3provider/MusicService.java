package smu.hw7_3provider;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

// 배경음악 재생을 위한 MusicService class
public class MusicService extends Service {

    IBinder binder = new MusicServiceBinder();
    public class MusicServiceBinder extends Binder {
        public MusicService getService(){
            return MusicService.this;
        }
    }

    private MediaPlayer mp;

    @Override
    public IBinder onBind (Intent intent){
        return binder;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        mp = MediaPlayer.create(this, R.raw.younha);
        mp.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(getApplicationContext(), "서비스 연결", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(getApplicationContext(), "서비스연결해제", Toast.LENGTH_SHORT).show();
        mp.stop();
        super.onDestroy();
    }

    public void play(){
        Toast.makeText(getApplicationContext(), "사운드 재생", Toast.LENGTH_SHORT).show();
        mp.start();
    }

    public void pause(){
        Toast.makeText(getApplicationContext(), "사운드 일시정지지", Toast.LENGTH_SHORT).show();
        mp.pause();
    }

}
