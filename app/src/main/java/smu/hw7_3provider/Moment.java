package smu.hw7_3provider;

import android.graphics.Bitmap;
public class Moment {
    // 계절마다의 순간이 찍힌 사진에 대한 객체로 만들려고 함
    // 비트맵 이미지 뿐이지만 리사이클러 뷰에서 사용하기 위해 객체로 만듦
    // 이후 비트맵이미지 외에도 다양한 것을 넣어 확장 가능
    Bitmap bitmapImage;
    public Bitmap getBitmapImage() {
        return bitmapImage;
    }
    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }
}
