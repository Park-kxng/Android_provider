package smu.hw7_3provider;

import android.graphics.Bitmap;
import android.net.Uri;

public class Moment {
    // 뭔가 계절마다의 순간이 찍힌 사진을 객체로 만들려고 하는데 안 쓸 수 있음
    // 필요 없을 수도 있음
    // 일단 이미지 경로 저장하는 부분
    String path;
    int dataAdded;
    long data_id;
    String data;
    Bitmap bitmapImage;

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public long getData_id() {
        return data_id;
    }

    public void setData_id(long data_id) {
        this.data_id = data_id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDataAdded() {
        return dataAdded;
    }

    public void setDataAdded(int dataAdded) {
        this.dataAdded = dataAdded;
    }

    int image_path;

    public int getImage_path() {
        return this.image_path;
    }

    public void setImage_path(int image_path) {
        this.image_path = image_path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
