package com.kukuhpr.aplikasiaxamandiri.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kukuhpr.aplikasiaxamandiri.Model.Post;
import com.kukuhpr.aplikasiaxamandiri.R;


import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> implements Filterable {

    ArrayList<Post> postList;
    ArrayList<Post> postListFull;
    Context mContext;

    public PostAdapter(Context mContext, ArrayList<Post> posts){
        this.mContext = mContext;
        postListFull = posts;
        this.postList = new ArrayList<>(postListFull);

    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.jph_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  PostViewHolder holder, int position) {


        Post posts = postList.get(position);
        holder.tv_Id.setText("Id: "+posts.getId());
        holder.tv_userId.setText("User Id: "+posts.getUserId());
        holder.tv_body.setText("Body: "+posts.getBody());
        holder.tv_title.setText("Title: "+posts.getTitle());

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    @Override
    public Filter getFilter() {
        return postFilter;
    }

    private final Filter postFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Post> filteredPostList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){

                filteredPostList.addAll(postListFull);

            } else {

                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Post postList : postListFull){

                    if (postList.getTitle().toLowerCase().contains(filterPattern))
                        filteredPostList.add(postList);


                    if (postList.getId().toLowerCase().contains(filterPattern))
                        filteredPostList.add(postList);

                    if (postList.getUserId().toLowerCase().contains(filterPattern))
                        filteredPostList.add(postList);

                    if (postList.getBody().toLowerCase().contains(filterPattern))
                        filteredPostList.add(postList);
                }

            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredPostList;
            filterResults.count = filteredPostList.size();
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {


            postList.clear();
            postList.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();

        }
    };


    public class PostViewHolder extends RecyclerView.ViewHolder{

        TextView tv_userId, tv_Id, tv_body, tv_title;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_userId = itemView.findViewById(R.id.userId_TV);
            tv_Id = itemView.findViewById(R.id.id_TV);
            tv_body = itemView.findViewById(R.id.body_TV);
            tv_title = itemView.findViewById(R.id.title_TV);

        }
    }

}
