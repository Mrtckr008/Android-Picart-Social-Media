package apps.picart;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Collections;

import apps.picart.Adapters.MyRecyclerViewAdapter;
import apps.picart.Objects.Posts;
import apps.picart.Objects.Users;
import apps.picart.utils.BitmapUtils;
import apps.picart.utils.GridSpacingItemDecoration;

import static apps.picart.utils.BitmapUtils.getImageUrifromBitmap;
import static maes.tech.intentanim.CustomIntent.customType;

public class SomeonesProfile extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    String first_pp;
    private RecyclerView recyclerView;
    FloatingActionButton btn_changepp;
    TextView user_name,user_countofposts;
    ImageView image_pp;
    Users targetuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_someonesprofile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);


         targetuser = (Users) getIntent().getSerializableExtra("MyClassProfile");




        image_pp = (ImageView) findViewById(R.id.user_pp);
        user_name = (TextView) findViewById(R.id.user_name);
        user_countofposts = (TextView)findViewById(R.id.user_countofposts);
        TextView mTitle = (TextView) findViewById(R.id.txt_whoisit);

        mTitle.setText(""+targetuser.name+"'s"+" Profile");
        first_pp=targetuser.profileImage;

        Typeface typeface = Typeface.createFromAsset(this.getAssets(),"Billabong.ttf");
        mTitle.setTypeface(typeface);

        try {
            renderProfile(); //profil fotosunu ve bilgilerini (isim, post sayısı vb) olusturuyor.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        initRecyclerView(); //fotoları olusturuyor


    }


    private void renderProfile() throws FileNotFoundException {


        Picasso.with(this) //Context
                .load(first_pp) //URL/FILE
                .into(image_pp);

        user_name.setText(targetuser.name);
        user_countofposts.setText(String.valueOf(targetuser.myposts.size()));

    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.mypostlist);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(4), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        Collections.reverse(targetuser.myposts);
        adapter = new MyRecyclerViewAdapter(this, targetuser.myposts);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onItemClick(View view, int position) {

        Intent intent = new Intent(this, GetPostActivity.class);
        intent.putExtra("MyClass", (Serializable) adapter.getItem(position));
        startActivity(intent);
        customType(this,"fadein-to-fadeout");

    }
}
