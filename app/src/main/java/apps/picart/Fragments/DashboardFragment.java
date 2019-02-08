package apps.picart.Fragments;


import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.squareup.picasso.Picasso;

import apps.picart.MainActivity;
import apps.picart.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener{

    TextView tv_header,tv_name,tv_mail;
    ImageView image_pp;
    Typeface typeface;
    Button btn_setname,btn_setpsw;
    LinearLayout ll_setter;
    private ProgressBar progressBar;

    public DashboardFragment() {
        // Required empty public constructor
    }
    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
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
        View v= inflater.inflate(R.layout.fragment_dashboard, container, false);


        tv_header = (TextView) v.findViewById(R.id.tv_headersettings);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "Billabong.ttf");
        tv_mail = (TextView) v.findViewById(R.id.tv_usermail);
        tv_name = (TextView) v.findViewById(R.id.tv_username);
        image_pp = (ImageView) v.findViewById(R.id.img_userpp);
//        btn_setname = (Button) v.findViewById(R.id.btn_setusername);
        btn_setpsw = (Button) v.findViewById(R.id.btn_setuserpsw);
//        ll_setter=(LinearLayout) v.findViewById(R.id.ll_setter);
//        btn_setname.setOnClickListener(this);
        btn_setpsw.setOnClickListener(this);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        tv_header.setTypeface(typeface);


        setprofile();





        return v;

    }

    private void setprofile() {
        tv_name.setText(MainActivity.currentuser.name);
        tv_mail.setText(MainActivity.currentuser.email);
        Picasso.with(getContext())
                .load(MainActivity.currentuser.profileImage)
                .into(image_pp);


    }

    @Override
    public void onClick(View v) {

//        if(v.getId() == btn_setname.getId()){
//
//        }
        if(v.getId() == btn_setpsw.getId()){
            progressBar.setVisibility(View.VISIBLE);

                MainActivity.auth.sendPasswordResetEmail(MainActivity.currentuser.email.trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    MainActivity.auth.sendPasswordResetEmail(MainActivity.currentuser.email);
                                    Toast.makeText(getActivity(), "Sifre Sıfırlama Linki Gönderildi, Lütfen Mailinizi Kontrol Ediniz..", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(getActivity(), "Sifre Sıfırlama İşlemi Başarısız", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });

        }

        }







//    protected void anim(View v){
//        if(v instanceof EditText) {
//            YoYo.with(Techniques.RollIn)
//                    .duration(2000)
//                    .playOn(v);
//        }
//        else {
//            YoYo.with(Techniques.BounceIn)
//                    .duration(2000)
//                    .playOn(v);
//        }
//        ll_setter.setVisibility(View.VISIBLE);
//
//    }
}
