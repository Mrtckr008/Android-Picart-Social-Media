package apps.picart;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import apps.picart.Fragments.MainpageFragment;
import apps.picart.Objects.Posts;
import apps.picart.Objects.Users;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import apps.picart.Objects.Posts;
import apps.picart.Objects.Users;
import apps.picart.utils.BitmapHelper;
import apps.picart.utils.BitmapUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apps.picart.utils.BitmapUtils.getImageUrifromBitmap;
import static maes.tech.intentanim.CustomIntent.customType;

public class PostActivity extends AppCompatActivity {

    private Uri mImageUri = null;
    private StorageReference mStorage;

    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;


    @BindView(R.id.img_getpostedphoto)
    ImageView imgposted;

    @BindView(R.id.titleField)
    EditText txt_title;

    @BindView(R.id.descField)
    EditText txt_desc;

    @BindView(R.id.submitBtn)
    Button btn_submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ButterKnife.bind(this,PostActivity.this);

setTitle("Postunu Hazırla Ve Paylaş!");
        Toolbar toolbar = findViewById(R.id.toolbarpost);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //FlipAnimation.create(FlipAnimation.UP, true, 300);


        mImageUri =getImageUrifromBitmap(this,BitmapHelper.getInstance().getBitmap());
        imgposted.setImageBitmap(BitmapHelper.getInstance().getBitmap());
        mStorage = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startposting();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {


            onBackPressed();


        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(PostActivity.this,"right-to-left");


    }

    private void startposting() {

        //mProgress.setMessage("Uploading Post..");
        //mProgress.show();



        final String title_val = txt_title.getText().toString().trim();
        final String desc_val = txt_desc.getText().toString().trim();



        if(!TextUtils.isEmpty(title_val)&& !TextUtils.isEmpty(desc_val)&& mImageUri!=null){

            StorageReference filepath = mStorage.child("Blog_images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUri = taskSnapshot.getDownloadUrl();



                    Posts newpost2 = new Posts(title_val,desc_val,downloadUri.toString(),MainActivity.currentuser.email,MainActivity.currentuser.name,MainActivity.currentuser.profileImage);



                    //Burda UserInfo yerine Posts yapınca postları key key kaydedicem..

                    DatabaseReference dbRef = MainActivity.db.getReference().
                            child("Users").
                            child(MainActivity.auth.getCurrentUser().getUid()).
                            child("MyPosts");





//ALLPOST TARAFI
                    DatabaseReference dbRef2 = MainActivity.db.getReference().child("Allposts");

                    String key2 = dbRef2.push().getKey();

                    DatabaseReference dbRefKeyli212 = MainActivity.db.getReference().child("Allposts/").child(key2);

                    dbRefKeyli212.setValue(newpost2);

                    DatabaseReference dbRefKeyli2 = MainActivity.db.getReference().child("Allposts/").child(key2).child("sendermail");

                    dbRefKeyli2.setValue(MainActivity.currentuser.email);


                    String key = dbRef.push().getKey();
                    DatabaseReference dbRefKeyli = MainActivity.db.getReference().
                            child("Users").
                            child(MainActivity.auth.getCurrentUser().getUid()).
                            child("MyPosts/")
                            .child(key);


                    dbRefKeyli.setValue(newpost2);

                    DatabaseReference dbRefKeyli232 = MainActivity.db.getReference().
                            child("Users").
                            child(MainActivity.auth.getCurrentUser().getUid()).
                            child("MyPosts/")
                            .child(key).child("sendermail");

                    dbRefKeyli232.setValue(MainActivity.currentuser.email);



//SENDERİN ALTINA KEY OLUSTURUP POSTLARI TUTUYOR.SENDERİ NULL YAPİP..
//                    DatabaseReference dbRefKeyli123 = MainActivity.db.getReference().
//                            child("Users").
//                            child(MainActivity.auth.getCurrentUser().getUid()).
//                            child("MyPosts/")
//                            .child(key);
//
//                    String key23 = dbRefKeyli123.push().getKey();
//                    Posts postwithoutsender=new Posts(title_val,desc_val,downloadUri.toString(),null);
//
//                    DatabaseReference sonref = dbRefKeyli123.child("sender/").child(key23);
//                    sonref.setValue(postwithoutsender);






//                    String key23 = dbRefKeyli.push().getKey();
//                    Posts postwithoutsender=new Posts(title_val,desc_val,downloadUri.toString(),null);
//
//                    DatabaseReference sonref = dbRefKeyli.child("Sender").child(key23);
//                    sonref.setValue(postwithoutsender);


                    MainActivity.currentuser.myposts.add(newpost2);

                    //updateUserdata();


//                    getAllPosts();
                    mProgress.dismiss();

                    startActivity(new Intent(PostActivity.this, MainActivity.class));
                    customType(PostActivity.this,"up-to-bottom"); //anim..


                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    mProgress.setMessage("Uploading " + ((int) 0) + "%...");
                    mProgress.show();
                }
            });
        }


    }


//    public void getAllPosts(){
//
////          ArrayList<Posts>allposts = new ArrayList<Posts>();
//
//        final DatabaseReference oku_Userposts = MainActivity.db.getReference()
//                .child("Allposts");
//
//        oku_Userposts.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
//                for(DataSnapshot key : keys){
//                    Posts post = key.getValue(Posts.class);
//
//                   // allposts.add(post);
//                    System.out.println("ALLPOSTS:"+post.getTitle());
//
//
//
//                    if(MainActivity.currentuser.email.equalsIgnoreCase(post.sender.email)) {
//
//
//                        DatabaseReference dbrefs= MainActivity.db.getReference()
//                                .child("Allposts")
//                                .child(key.getKey())
//                                .child("sender")
//                                .child("myposts");
//                        dbrefs.setValue(MainActivity.currentuser.myposts);
//
//
//
//
////
////                        DatabaseReference dbRefKeys = MainActivity.db.getReference().
////                                child("Allposts")
////                                .child(key.getKey());
////
////                        dbRefKeys.child("sender").child("myposts").setValue(MainActivity.currentuser.myposts);
//
//                    }
//
//
//
//
//                }
//
//
//
//
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }







}