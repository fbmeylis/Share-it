package com.fbmeylis.shareit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FeedRecycleAdapter extends RecyclerView.Adapter<FeedRecycleAdapter.PostHolder> {

    private ArrayList<String> usermailList;
    private ArrayList<String> commentList;
    private ArrayList<String> urllist;

    public FeedRecycleAdapter() {
        this.usermailList = new ArrayList<>();
        this.commentList = new ArrayList<>();
        this.urllist = new ArrayList<>();

    }

    public void addItems(String comment, String downloadurl, String usermail) {
        this.commentList.add(comment);
        this.urllist.add(downloadurl);
        this.usermailList.add(usermail);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycle_row,parent,false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FeedRecycleAdapter.PostHolder holder, int position) {

        holder.usermail.setText(usermailList.get(position));
        holder.commenttext.setText(commentList.get(position));
        Picasso.get().load(urllist.get(position)).into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        return usermailList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView commenttext;
        TextView usermail;

        public PostHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recycle_row_image);
            commenttext = itemView.findViewById(R.id.recycle_row_comment);
            usermail = itemView.findViewById(R.id.recycle_row_usermail);

        }
    }

}
