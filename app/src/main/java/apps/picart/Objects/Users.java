package apps.picart.Objects;

//import android.databinding.BaseObservable;
//import android.databinding.Bindable;
//import android.databinding.BindingAdapter;
//import android.databinding.ObservableField;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//import apps.picart.BR;

/**
 * Created by ilyada on 14.04.2018.
 */

public class Users implements Serializable{ // extends BaseObservable {
    public String name;
    public String email;
    public String profileImage;
    public ArrayList<Posts>myposts = new ArrayList<Posts>();
    public ArrayList<Posts>myfavposts = new ArrayList<Posts>();






    // profile meta fields are ObservableField, will update the UI
    // whenever a new value is set
//    public ObservableField<Long> numberOfFollowers = new ObservableField<>();
//    public ObservableField<Long> numberOfPosts = new ObservableField<>();
//    public ObservableField<Long> numberOfFollowing = new ObservableField<>();

    public Users() {
    }
    public Users(String namec,String emailc) {

        name=namec;
        email=emailc;
        profileImage="android.resource://apps.picart/drawable/ic_person";

    //    posts = new ArrayList<Posts>();
    }
    public Users(String namec,String emailc,String profileImagec,ArrayList<Posts> posts) {

        name=namec;
        email=emailc;
        profileImage=profileImagec;
        myposts=posts;

        //    posts = new ArrayList<Posts>();
    }



//    @Bindable
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//        notifyPropertyChanged(BR.name);
//    }
//
//    @Bindable
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//        notifyPropertyChanged(BR.email);
//    }
//
//    @BindingAdapter({"profileImage"})
//    public static void loadImage(ImageView view, String imageUrl) {
//        Glide.with(view.getContext())
//                .load(imageUrl)
//                .apply(RequestOptions.circleCropTransform())
//                .into(view);
//
//        // If you consider Picasso, follow the below
//        // Picasso.with(view.getContext()).load(imageUrl).placeholder(R.drawable.placeholder).into(view);
//    }
//
//    @Bindable
//    public String getProfileImage() {
//        return profileImage;
//    }
//
//    public void setProfileImage(String profileImage) {
//        this.profileImage = profileImage;
//        notifyPropertyChanged(BR.profileImage);
//    }
//
//    @Bindable
//    public String getAbout() {
//        return about;
//    }
//
//    public void setAbout(String about) {
//        this.about = about;
//        notifyPropertyChanged(BR.about);
//    }


//    public ObservableField<Long> getNumberOfFollowers() {
//        return numberOfFollowers;
//    }

//    public ObservableField<Long> getNumberOfPosts() {
//        return numberOfPosts;
//    }
//
//    public ObservableField<Long> getNumberOfFollowing() {
//        return numberOfFollowing;
//    }


}
//
//    int id;
//    String email;
//    String pp;
//    List<Posts> posts = new ArrayList<Posts>();
//
//    public String getPp() {
//        return pp;
//    }
//
//    public void setPp(String pp) {
//        this.pp = pp;
//    }
//
//
//    public List<Posts> getPosts() {
//        return posts;
//    }
//
//    public void setPosts(List<Posts> posts) {
//        this.posts = posts;
//    }
//
//
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//
//
//}
