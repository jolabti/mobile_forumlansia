package Adapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coba.els_connect.DetailKomentarActivity;
import com.example.coba.els_connect.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import Model.PostModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private ArrayList<PostModel> posts;
    Context c;



    public String linkGambar = "http://forumlansia.jn-code.xyz/assets/images/uploads/f4fa8c74bf06ab70a5e27258446a60d3_21-07-18.png";
    public DataAdapter( ArrayList<PostModel> posts, Context c) {
        this.posts = posts;
        this.c=c;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posting_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, final int position) {
        holder.tv_name.setText(posts.get(position).getEmail());
        holder.tv_posting.setText(posts.get(position).getPosting());
        holder.tv_date.setText(posts.get(position).getWaktu());

        Log.d("getposid", posts.get(position).getPos_id());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoDetailKomentar = new Intent(c, DetailKomentarActivity.class);
                gotoDetailKomentar.putExtra("idposting", posts.get(position).getPos_id());
                gotoDetailKomentar.putExtra("passbyposting", posts.get(position).getPosting());
                c.startActivity(gotoDetailKomentar);
            }
        });

        //Log.d("myidmy",posts.get(position).getPos_id());



        Picasso.get().load(linkGambar).into(holder.civ);
        Picasso.get().load(linkGambar).into(holder.post_image);







    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private TextView tv_posting;
        private TextView tv_date;
        private CircleImageView civ;
        private ImageView post_image;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.main_post);
            tv_name = itemView.findViewById(R.id.post_username);
            tv_posting = itemView.findViewById(R.id.post_desc);
            tv_date = itemView.findViewById(R.id.post_date);
            civ = itemView.findViewById(R.id.post_user_image);
            post_image = itemView.findViewById(R.id.post_image);

        }
    }
}