package apps.picart;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.desarrollodroide.libraryfragmenttransactionextended.FragmentTransactionExtended;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import apps.picart.Fragments.DashboardFragment;
import apps.picart.Fragments.FavPostFragment;
import apps.picart.Fragments.HomeFragment;
import apps.picart.Fragments.MainpageFragment;
import apps.picart.Objects.Posts;
import apps.picart.Objects.Users;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Size;
import nl.dionsegijn.konfetti.models.Shape;

import static maes.tech.intentanim.CustomIntent.customType;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ImageView imageview;
    public static FirebaseAuth auth;
    public static String userid;
    public static FirebaseUser user;
    public static FirebaseDatabase db;
    public static Users currentuser;
    boolean ilkacilis = true;
    Boolean ApphasRun;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment homeFragment = new HomeFragment();
            Fragment mainpageFragment = new MainpageFragment();
            Fragment dashboardFragment = new DashboardFragment();
            Fragment favpostsFragment = new FavPostFragment();


            switch (item.getItemId()) {
                case R.id.navigation_home:
                        homeFragment.onCreateAnimation(1,true,25);

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, homeFragment).commit();
                        return true;

                case R.id.navigation_dashboard:

                        dashboardFragment.onCreateAnimation(1,true,25);

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, dashboardFragment).commit();
                    return true;

                case R.id.navigation_instagram:
                    mainpageFragment.onCreateAnimation(1,true,25);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_content, mainpageFragment).commit();
                    return true;

                case R.id.navigation_effects:
                    homeFragment.onCreateAnimation(1,true,25);

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_content, favpostsFragment).commit();

                    return true;

                case R.id.navigation_addimages:

                    Intent intent = new Intent(getApplicationContext(), FiltersActivity.class);
                    startActivity(intent);
                    customType(MainActivity.this,"bottom-to-up");



            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        userid = user.getUid();

        getCurrentUserMain();




        //ilk fragment bu olsun diye..!
        Fragment selectedFragment =null;
        selectedFragment = MainpageFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, selectedFragment).commit();


        mTextMessage = (TextView) findViewById(R.id.message);
//        imageview = (ImageView) findViewById;
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_instagram);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Image Filters");

        KonfettiView viewKonfetti= (KonfettiView) findViewById(R.id.viewKonfetti);


        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.RED, Color.CYAN)
                .setDirection(10000.0, viewKonfetti.getWidth())
                .setSpeed(5f, 8f)
                .setFadeOutEnabled(true)
                .setTimeToLive(4000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(8,5f))
                .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, viewKonfetti.getHeight() + 50f)
                .stream(150, 2000L);


    }



    private void getCurrentUserMain(){

        DatabaseReference oku_userinfo = db.getReference().child("/Users").child(MainActivity.userid).child("/UserInfo");
        oku_userinfo.addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(DataSnapshot dataSnapshot) {

                                                       currentuser = dataSnapshot.getValue(Users.class);
                                                       //currentuser.myposts.add(new Posts("1TITLE","2","3"));
                                                     //   System.out.println("YOOOO:"+currentuser.myposts.get(0).getTitle());

                                                        getMyPosts();
                                                        getMyFavPosts();
                                               }

                                               @Override
                                               public void onCancelled(DatabaseError databaseError) {

                                               }
                                           });


        }

        public void getMyPosts(){

            DatabaseReference oku_Userposts = db.getReference()
                    .child("/Users")
                    .child(MainActivity.userid)
                    .child("/MyPosts");
            oku_Userposts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                    for(DataSnapshot key : keys){
                        Posts post = key.getValue(Posts.class);

                        currentuser.myposts.add(post);
                        System.out.println("POSTLARRRRRRRRRRRRRR:"+post.getTitle());

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        public void getMyFavPosts(){

            DatabaseReference oku_Userposts = db.getReference()
                    .child("/Users")
                    .child(MainActivity.userid)
                    .child("/MyFavPosts");
            oku_Userposts.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Iterable<DataSnapshot> keys = dataSnapshot.getChildren();
                    for(DataSnapshot key : keys){
                        Posts post = key.getValue(Posts.class);

                        currentuser.myfavposts.add(post);
                        System.out.println("Favori-POSTLARRRRRRRRRRRRRR:"+post.getTitle());

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

}
