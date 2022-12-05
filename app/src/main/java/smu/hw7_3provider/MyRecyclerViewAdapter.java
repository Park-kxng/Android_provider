package smu.hw7_3provider;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter {
    /*
  어댑터의 동작원리 및 순서
  1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
  2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
  3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
  4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
  */
    String TAG = "RecyclerViewAdapter";

    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<Moment> dataModels;

    Context context;

    //생성자를 통하여 데이터 리스트 context를 받음
    public MyRecyclerViewAdapter(Context context, ArrayList<Moment> dataModels) {
        this.dataModels = dataModels;
        this.context = context;
    }


    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함
        return dataModels.size();
    }


    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");

        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);


        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }


    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onBindViewHolder");

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        // 우리가 만든 예약 그거에서 가져온거임
        /*
        myViewHolder.textViewTitle.setText(dataModels.get(position).getTitle());
        // ▲ Movie에서 Title 가져와서 넣어줌
        myViewHolder.textViewRank.setText(String.valueOf(dataModels.get(position).getRank()));
        // ▲ Movie에서 rank 가져와서 넣어줌. 이때 rank가 int형이라 String.valueOf(int value)로 형변환 필요. 안하면 앱 죽음.
        myViewHolder.textViewReleaseDate.setText(dataModels.get(position).getReleaseDate());
        // ▲ 개봉일 넣어주기
        myViewHolder.starsRatingBar.setRating(dataModels.get(position).getStarScore());
        //▲ 별점 넣기
        myViewHolder.imageViewPoster.setImageResource(dataModels.get(position).image_path);
        // ▲ Movie 해당하는 poster가져와서 그려줌

        // ▼ 리사이클러 내의 아이템 클릭시 동작하는 부분
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, position+"번째 아이템 클릭", Toast.LENGTH_SHORT).show();
                // 인텐트로 넘겨줘야 하는 부분
                Intent intent = new Intent(myViewHolder.itemView.getContext(), MovieInformation.class);
                intent.putExtra("clickPosition", position); // position만 넘겨주면 어떤 영화, 스케줄인지 static 변수에 접근 가능
                ContextCompat.startActivity(myViewHolder.itemView.getContext(), intent, null);
            }
        });

         */
        myViewHolder.imageViewMoment.setImageBitmap(dataModels.get(position).getBitmapImage());
        // ▼ 리사이클러 내의 아이템 클릭시 동작하는 부분
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, position+"번째 아이템 클릭", Toast.LENGTH_SHORT).show();
                // 인텐트로 넘겨줘야 하는 부분
                Intent intent = new Intent(myViewHolder.itemView.getContext(), BeautifulMoment.class);
                intent.putExtra("clickPosition", position); // position으로 array접근해서 보여주기
                ContextCompat.startActivity(myViewHolder.itemView.getContext(), intent, null);
            }
        });
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMoment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMoment = itemView.findViewById(R.id.imageViewMoment);
        }
    }


}
