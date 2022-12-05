package smu.hw7_3provider;
public class Moment {
    // 뭔가 계절마다의 순간이 찍힌 사진을 객체로 만들려고 하는데 안 쓸 수 있음
    // 필요 없을 수도 있음
    // 일단 이미지 경로 저장하는 부분
    String path;
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
