package apps.picart.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import apps.picart.Fragments.MainpageFragment;
import apps.picart.Objects.Posts;
import apps.picart.R;

/**
 * Created by ilyada on 13.05.2018.
 */

public class MyRecyclerViewAdapterMain extends RecyclerView.Adapter<MyRecyclerViewAdapterMain.ViewHolder> {

    private List<Posts> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapterMain(Context context, List<Posts> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.blog_row, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Posts posts = mData.get(position);
//        holder.myTextView.setText(animal);


        Picasso.with(this.mInflater.getContext()) //Context
                .load(posts.getImageUrl()) //URL/FILE
                .into(holder.post_picture);

        Picasso.with(this.mInflater.getContext()) //Context
                .load(posts.senderpp) //URL/FILE
                .into(holder.sender_pp);

        holder.sender_name.setText(" "+posts.sendername);
        holder.sender_mail.setText("  "+posts.sendermail);
        holder.post_desc.setText(posts.title);
        holder.post_title.setText(posts.desc);


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sender_name,sender_mail,post_title,post_desc;
        ImageView sender_pp,post_picture;
        ViewHolder(View itemView) {
            super(itemView);
            sender_pp = (ImageView) itemView.findViewById(R.id.sender_pp);
            post_picture = (ImageView) itemView.findViewById(R.id.post_image);
            sender_name = (TextView) itemView.findViewById(R.id.sender_name);
            sender_mail = (TextView) itemView.findViewById(R.id.sender_mail);
            post_desc = (TextView) itemView.findViewById(R.id.post_desc);
            post_title = (TextView) itemView.findViewById(R.id.post_title);



            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Posts getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}