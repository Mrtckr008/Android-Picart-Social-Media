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

import apps.picart.Objects.Comments;
import apps.picart.Objects.Posts;
import apps.picart.R;

/**
 * Created by ilyada on 13.05.2018.
 */

public class MyRecyclerViewAdapterComments extends RecyclerView.Adapter<MyRecyclerViewAdapterComments.ViewHolder> {

    private List<Comments> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapterComments(Context context, List<Comments> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.comments_row, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Comments comment = mData.get(position);
//        holder.myTextView.setText(animal);


//        Picasso.with(this.mInflater.getContext()) //Context
//                .load(posts.getImageUrl()) //URL/FILE
//                .into(holder.post_picture);



        holder.commentername.setText(" "+comment.commenter_name);
        holder.commenteremail.setText("  "+comment.commenter_mail);
        holder.commentdesc.setText(comment.comment_desc);


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView commentername,commenteremail,commentdesc;
        ViewHolder(View itemView) {
            super(itemView);
            commentername = (TextView) itemView.findViewById(R.id.sender_name);
            commenteremail = (TextView) itemView.findViewById(R.id.sender_mail);
            commentdesc = (TextView) itemView.findViewById(R.id.comment_desc);



            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Comments getItem(int id) {
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