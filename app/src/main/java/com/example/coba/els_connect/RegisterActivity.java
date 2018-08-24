package com.example.coba.els_connect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;


import GSON.GsonRegister;
import Utils.Tools;
import base.BaseOkHttpClient;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import share.Api;

public class RegisterActivity extends AppCompatActivity {


    //    Deklarasi
    public EditText regEmail,regPassword,regrepassword;
    public Button btnCreate,btnLogin;

    String TAG_REGIST = "SilahkanLogin";
    String addEmail, addPassword, addRepassword;
    String varKota ="Depok";
    LoginActivity lga = new LoginActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regrepassword = findViewById(R.id.reg_repassword);
        btnCreate = findViewById(R.id.create_btn);
        btnLogin = findViewById(R.id.already_btn);


        btnCreate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongViewCast")
            @Override
            public void onClick(View v) {

                addEmail = regEmail.getText().toString();
                addPassword = regPassword.getText().toString();
                addRepassword = regrepassword.getText().toString();


                if (!Tools.validateEmail(addEmail)) {

                    regEmail.setError("Format Email tidak sesuai");

                } else {
                    if (addEmail.equals("") || addPassword.equals("") || addRepassword.equals("")) {

                        Toast.makeText(getApplicationContext(), "Email atau Password Kosong", Toast.LENGTH_LONG).show();
                    }
//
//                    else if(addPassword.trim()!=addRepassword.trim()){
//                        Log.d("1pass", addPassword);
//                        Log.d("2pass", addRepassword);
//
//
//                        Toast.makeText(getApplicationContext(), "Password tidak sama dengan repassword", Toast.LENGTH_LONG).show();
//
//                    }

                    else{

                        Log.d("testRegister", "Berhasil");

                        String fooString1 = new String(addPassword);
                        String fooString2 = new String(addRepassword);







                        //startRequestApiRegist(addEmail,addPassword,addRepassword);
                        if(!fooString1.equals(fooString2)){

                                regPassword.setError("Password tidak sama dengan re-trype password");
                                regrepassword.setError("Password tidak sama dengan password");

                        }else{

                            startRequestApiRegist(addEmail,addPassword);
                            clearInput();
                        }

                    }


                }


            }
        });


//  Fungsion Btn Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gotologin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(gotologin);
            }
        });

    }

    //        Deklar Api
    public void startRequestApiRegist(String paramEmail, String paramPassword){

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.JSON_NEW_USER + "/" + paramEmail + "/" + paramPassword + "/" + varKota)
                .tag(TAG_REGIST)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest(TAG_REGIST);

        BaseOkHttpClient.getInstance(getApplicationContext()). newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("cek_kon_register", e.getLocalizedMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response == null){

                    Log.d("cek_response", "Null Respone");

                } else {
//                    Log.d("cek_koneksi", response.body().toString());
                      GsonRegister gson = new Gson().fromJson(response.body().string(),GsonRegister.class);
//                    Log.d("cek_gson", gson.message);
//
                    if (gson.message.equals("Ok")){
//                        Link To Login
                        Toast.makeText(getApplicationContext(),gson.message.toString(), Toast.LENGTH_SHORT).show();
                        Intent gotologin = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(gotologin);
                    }

                    else{

                        lga.backgroundThreadShortToast(getApplicationContext(),gson.message);
                    }

                }

            }
        });

    }


    public void clearInput(){

        regEmail.setText("");
        regPassword.setText("");
        regrepassword.setText("");

    }




}