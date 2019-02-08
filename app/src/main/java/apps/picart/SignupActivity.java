package apps.picart;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import apps.picart.Objects.Posts;
import apps.picart.Objects.Users;

public  class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    FirebaseDatabase db;
    Double TL_bakiye;
    TextView textView_header;
    Typeface typeface;
    String email,name,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        db= FirebaseDatabase.getInstance();
        textView_header=(TextView)findViewById(R.id.tv_picart_signup);

        typeface = Typeface.createFromAsset(this.getAssets(),"Billabong.ttf");
        textView_header.setTypeface(typeface);

        auth = FirebaseAuth.getInstance();
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputName = (EditText) findViewById(R.id.name);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 email = inputEmail.getText().toString().trim();
                 password = inputPassword.getText().toString().trim();
                 name = inputName.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Email Adresinizi Giriniz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Email Adresinizi Giriniz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Sifrenizi Giriniz!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Sifre En Az 6 Karakterden Oluşmalı!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Sifre En Az 6 Karakterden Oluşmalı!", Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())

                                setUser();

                                progressBar.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "İşlem Başarısız, Geçerli Bir Mail Adresi Giriniz!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void setUser(){

        Users newuser = new Users(name,email);

       // newuser.posts = new ArrayList<>();

        //Burda UserInfo yerine Posts yapınca postları key key kaydedicem..
        DatabaseReference dbRefUserInfo = db.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("/UserInfo");

        dbRefUserInfo.setValue(newuser);
      //  dbRefUserInfo.setValue(new Users("a","b","c","d"));

        Toast.makeText(SignupActivity.this, "Kullanıcı Kaydı Başarılı, Hoşgeldiniz.. " , Toast.LENGTH_SHORT).show();

        startActivity(new Intent(SignupActivity.this, MainActivity.class));

        finish();

    }






}