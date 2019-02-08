package apps.picart.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//import apps.picart.Adapters.PostsAdapter;
import apps.picart.Adapters.MyRecyclerViewAdapter;
import apps.picart.FiltersActivity;
import apps.picart.GetPostActivity;
import apps.picart.MainActivity;
import apps.picart.Objects.Posts;
import apps.picart.Objects.Users;
import apps.picart.PostActivity;
import apps.picart.R;
import apps.picart.utils.BitmapHelper;
import apps.picart.utils.BitmapUtils;
import apps.picart.utils.GridSpacingItemDecoration;

import static android.app.Activity.RESULT_OK;
import static apps.picart.utils.BitmapUtils.getImageUrifromBitmap;
import static maes.tech.intentanim.CustomIntent.customType;

//import android.databinding.DataBindingUtil;
//import apps.picart.databinding.FragmentHomeBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener { //implements PostsAdapter.PostsAdapterListener{

    MyRecyclerViewAdapter adapter;
    String first_pp;
    private RecyclerView recyclerView;
    FloatingActionButton btn_changepp;
    TextView user_name,user_countofposts;
    ImageView image_pp;
    public ArrayList<Posts> allPosts = new ArrayList<Posts>();
    private Uri mImageUri = null;
    public static int gallery=1;
    public  static int camera = 2;
    private ProgressDialog mProgress;

    private StorageReference mStorage;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return CubeAnimation.create(CubeAnimation.LEFT, enter, 500);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //  binding = DataBindingUtil.setContentView(this.getActivity(), R.layout.fragment_home);
//            binding= DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false);
//        View view = binding.getRoot();
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        btn_changepp= (FloatingActionButton) view.findViewById(R.id.user_changepp);
        image_pp = (ImageView) view.findViewById(R.id.user_pp);
        user_name = (TextView) view.findViewById(R.id.user_name);
        user_countofposts = (TextView) view.findViewById(R.id.user_countofposts);
        TextView mTitle = (TextView) view.findViewById(R.id.toolbar_title_profil);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(),"Billabong.ttf");
        first_pp=MainActivity.currentuser.profileImage;
        mTitle.setTypeface(typeface);
        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage("Profiliniz Ayarlanıyor...");

        btn_changepp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogforresource();
            }
        });

//        handlers = new MyClickHandlers(getContext());
        try {
            renderProfile(); //profil fotosunu ve bilgilerini (isim, post sayısı vb) olusturuyor.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        initRecyclerView(view); //fotoları olusturuyor

        return view;


    }

    private void renderProfile() throws FileNotFoundException {

        if( (!MainActivity.currentuser.profileImage.equalsIgnoreCase(first_pp) ) ){

            Bitmap selectedImage=BitmapUtils.stringToBitmap(MainActivity.currentuser.profileImage);
          //  image_pp.setImageBitmap(selectedImage);
            mStorage = FirebaseStorage.getInstance().getReference();
            mImageUri =getImageUrifromBitmap(getContext(), selectedImage);
            StorageReference filepath = mStorage.child("users_pp").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    MainActivity.currentuser.profileImage = downloadUri.toString();
                    for(Posts pst:MainActivity.currentuser.myposts){
                        pst.senderpp=downloadUri.toString();
                    }

//                    mProgress.dismiss();
                    Picasso.with(getContext()) //Context
                            .load(MainActivity.currentuser.profileImage) //URL/FILE
                            .into(image_pp);

                    setUserppFromFirebase();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    @SuppressWarnings("VisibleForTests") double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
//                    mProgress.setMessage("Uploading " + ((int) progress) + "%...");
//                    mProgress.show();
                }
            });
        }
        else {
            Picasso.with(getContext()) //Context
                    .load(MainActivity.currentuser.profileImage) //URL/FILE
                    .into(image_pp);

        }

        user_name.setText(MainActivity.currentuser.name);
        user_countofposts.setText(String.valueOf(MainActivity.currentuser.myposts.size()));

        mProgress.dismiss();

    }


    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.mypostlist);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(4), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        Collections.reverse(MainActivity.currentuser.myposts);
        adapter = new MyRecyclerViewAdapter(getContext(), MainActivity.currentuser.myposts);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


    }
    private void showDialogforresource(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Uploading Images");
        builder.setMessage("Upload Your Image Resource From:");
        builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {

                //İptal butonuna basılınca yapılacaklar.Sadece kapanması isteniyorsa boş bırakılacak
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , gallery);

            }
        });

        builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //CAMERA butonuna basılınca yapılacaklar
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, camera);
            }
        });
        builder.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == gallery) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(getContext(), data.getData(), 800, 800);
            MainActivity.currentuser.profileImage = BitmapUtils.bitmapToString(bitmap);


        }
        if (resultCode == RESULT_OK && requestCode == camera) {
//            Bitmap bitmap =   onCaptureImageResult(data);
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(getContext(), data.getData(), 1000, 1000);
            MainActivity.currentuser.profileImage = BitmapUtils.bitmapToString(bitmap);


        }
        try {

            mProgress.show();

            renderProfile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void setUserppFromFirebase(){

        DatabaseReference dbRefUserInfo = MainActivity.db.getReference().child("Users").child(MainActivity.userid).child("/UserInfo").child("/profileImage");

        dbRefUserInfo.setValue(MainActivity.currentuser.profileImage);

        DatabaseReference dbRefUserInfo2 = MainActivity.db.getReference()
                .child("Users")
                .child(MainActivity.userid)
                .child("/MyPosts");

        dbRefUserInfo2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {
                    DatabaseReference dbRefUserInfo3 = MainActivity.db.getReference()
                            .child("Users")
                            .child(MainActivity.userid)
                            .child("/MyPosts")
                            .child(key.getKey())
                            .child("senderpp");

                    dbRefUserInfo3.setValue(MainActivity.currentuser.profileImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference dbRefUserInfoall = MainActivity.db.getReference()
                .child("Allposts");

        dbRefUserInfoall.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {



                    if(key.getValue(Posts.class).sendermail.equalsIgnoreCase(MainActivity.currentuser.email)){

                        DatabaseReference dbRefUserInfo5 = MainActivity.db.getReference()
                                .child("Allposts")
                                .child(key.getKey())
                                .child("senderpp");
                        dbRefUserInfo5.setValue(MainActivity.currentuser.profileImage);

                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onItemClick(View view, int position) {



        Intent intent = new Intent(getContext(), GetPostActivity.class);
        intent.putExtra("MyClass", (Serializable) adapter.getItem(position));
        startActivity(intent);
        customType(getActivity(),"fadein-to-fadeout");

       // Toast.makeText(getContext(), "You clicked " + adapter.getItem(position).getTitle() + " on row number " + position, Toast.LENGTH_SHORT).show();

    }




//    private void getAllPosts() {
//
//        allPosts.clear();
//        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
//                for (DataSnapshot key : keys) {
//                    Posts newpost = key.getValue(Posts.class);
//
//                    allPosts.add(newpost);
//                    System.out.println("AAAAAAAA:" + newpost.getTitle());
//
//                }
//                System.out.println("AAAAAAAA:" + allPosts.size());
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
