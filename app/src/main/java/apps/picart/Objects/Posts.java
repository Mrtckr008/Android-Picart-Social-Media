package apps.picart.Objects;

import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//import android.databinding.BindingAdapter;

/**
 * Created by ilyada on 14.04.2018.
 */

public class Posts implements Serializable{
    public String title;
    public String desc;
    public List<Comments> comments = new ArrayList<Comments>();
    public String imageUrl;
    public String sendermail;
    public String sendername;
    public String senderpp;


    public Posts(){



    }

    public Posts(String title,String desc, String imageUrl,String sendermailc,String sendernamec,String senderppc){
        this.title=title;
        this.desc= desc;
        this.imageUrl =imageUrl;
        this.sendermail =sendermailc;
        this.sendername=sendernamec;
        this.senderpp=senderppc;


    }

//    @BindingAdapter("imageUrl")
//    public static void loadImage(ImageView view, String imageUrl) {
//        Glide.with(view.getContext())
//                .load(imageUrl)
//                .into(view);
//    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getTitle() {
        return title;
    }


   /*public void setImage(String downloadUri) {
        this.image = downloadUri;
    } */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String content) {
        this.desc = content;
    }

//    public List<Comments> getComments() {
//        return comments;
//    }

//    public void setComments(List<Comments> comments) {
//        this.comments = comments;
//    }


}
