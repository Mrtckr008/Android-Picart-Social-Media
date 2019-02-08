package apps.picart.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.labo.kaji.fragmentanimations.CubeAnimation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import apps.picart.Adapters.MyRecyclerViewAdapterMain;
import apps.picart.MainActivity;
import apps.picart.Objects.Posts;
import apps.picart.Objects.Users;
import apps.picart.R;
import apps.picart.SomeonesProfile;

import static maes.tech.intentanim.CustomIntent.customType;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavPostFragment extends Fragment  implements MyRecyclerViewAdapterMain.ItemClickListener {

    //    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseallUsers;
    private ProgressDialog mProgress;



    Typeface typeface;
    TextView textView_header;
    public ArrayList<Posts> allPosts = new ArrayList<Posts>();
    public ArrayList<Users> allUsers = new ArrayList<Users>();


    RecyclerView recyclerView;
    MyRecyclerViewAdapterMain adapter;
    Context thiscontext;


    public FavPostFragment() {
        // Required empty public constructor
    }

    public static FavPostFragment newInstance() {
        FavPostFragment fragment = new FavPostFragment();
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
        View v = inflater.inflate(R.layout.fragment_favposts, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Allposts");
//        mBlogList = (RecyclerView) v.findViewById(R.id.image_list);
//        mBlogList.setHasFixedSize(true);
//        mBlogList.setLayoutManager(new LinearLayoutManager(getActivity()));

        typeface = Typeface.createFromAsset(getContext().getAssets(), "Billabong.ttf");

        thiscontext = getContext();
        textView_header = (TextView) v.findViewById(R.id.tv_headermain);
        recyclerView = v.findViewById(R.id.image_list);

        mProgress = new ProgressDialog(getContext());


        textView_header.setTypeface(typeface);

        mProgress.setMessage("Hazırlanıyor Lütfen Bekleyin..");
        mProgress.show();
        getAllPosts();
        getAllUsers();
        //postları olusturuyor


        return v;
    }

    private void getAllPosts() {

        allPosts.clear();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {
                    Posts newpost = key.getValue(Posts.class);

                    allPosts.add(newpost);
                    System.out.println("AAAAAAAA:" + newpost.getTitle());

                }
                System.out.println("AAAAAAAA:" + allPosts.size());

                initRecyclerView();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void getAllUsers(){

        mDatabaseallUsers=FirebaseDatabase.getInstance().getReference().child("Users");


        allUsers.clear();
        mDatabaseallUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 DatabaseReference mDatabaseallUsersmail;
                 DatabaseReference mDatabaseallUsersname;
                 DatabaseReference mDatabaseallUserspp;


                Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                for (DataSnapshot key : keys) {


                    final Users user=new Users();





                    mDatabaseallUsersmail = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(key.getKey())
                            .child("UserInfo")
                            .child("email");
                    mDatabaseallUsersname = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(key.getKey())
                            .child("UserInfo")
                            .child("name");

                    mDatabaseallUserspp = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(key.getKey())
                            .child("UserInfo")
                            .child("profileImage");

                    mDatabaseallUsersname.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                             user.name =  dataSnapshot.getValue(String.class );


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mDatabaseallUsersmail.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user.email =  dataSnapshot.getValue(String.class);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mDatabaseallUserspp.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            user.profileImage =  dataSnapshot.getValue(String.class);


                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                    DatabaseReference mDatabaseallUsersgetPosts;
                    mDatabaseallUsersgetPosts = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(key.getKey())
                            .child("MyPosts");

                    mDatabaseallUsersgetPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

//                            userpost.clear();

                            Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                            for (DataSnapshot key : keys) {

                                user.myposts.add (key.getValue(Posts.class));
                            }



                            allUsers.add(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



//                    System.out.println("AAAAAAAA:" + user.name);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProgress.dismiss();


    }




    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(thiscontext));
        Collections.reverse(MainActivity.currentuser.myfavposts);
        adapter = new MyRecyclerViewAdapterMain(thiscontext, MainActivity.currentuser.myfavposts);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);



    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onItemClick(View view, int position) {

        mProgress.show();
        Log.d("Userlar:",String.valueOf(allUsers.size()));


        for(Users user:allUsers){
            if( adapter.getItem(position).sendermail.equalsIgnoreCase( user.email )){
                Intent intent = new Intent(getContext(), SomeonesProfile.class);
                intent.putExtra("MyClassProfile", (Serializable) user);
                startActivity(intent);
                mProgress.dismiss();
                customType(getActivity(), "fadein-to-fadeout");
            }

        }




    }
}