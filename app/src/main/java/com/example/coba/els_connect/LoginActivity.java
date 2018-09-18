package com.example.coba.els_connect;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;

import java.io.IOException;

import GSON.GSONLogin;
import Utils.SessionManager;
import Utils.Tools;
import base.BaseOkHttpClient;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import share.Api;

public class LoginActivity extends AppCompatActivity {

    public EditText edEmail,edPassword;
    public Button btnLogin,btnReg;
    String TAG_LOGIN = "thislogin";

    String myEmail,myPassport, varMessage;

    SessionManager sessionManager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(getApplicationContext());

        if(sessionManager.isLoggedIn()){

            Intent gotoMain = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(gotoMain);
        }


        edEmail = findViewById(R.id.login_email);
        edPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login_btn);
        btnReg = findViewById(R.id.login_reg_btn);

//

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myEmail= edEmail.getText().toString();
                myPassport= edPassword.getText().toString();

                if(!Tools.validateEmail(myEmail)){

                    edEmail.setError("Formal email tidak sesuai");
                }
                else{

                    if(myEmail.equals("") || myPassport.equals("")){

                         Toast.makeText(getApplicationContext(),"Email atau Password Kosong", Toast.LENGTH_SHORT).show();
                    }
                    else{

                         //Log.d("testlogin", "Berhasil");
                         startRequestApiLogin(myEmail,myPassport);
                         //Log.d("gsonGlob", gson.message);
                    }
                }



            }
        });


        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoRegist = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(gotoRegist);
            }
        });

    }



    public void startRequestApiLogin(String paramEmail, String paramPassword){

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.JSON_DO_LOGIN + "/" + paramEmail + "/"+paramPassword)
                .tag(TAG_LOGIN)
//                .addHeader("Content-Type","application/x-www-form-urlencoded")
//                .addHeader("Accept","*/*")
//                .addHeader("Connection","Keep-Alive")
//                .addHeader("Expect", "100-continue")
//                .addHeader("Accept-Encoding", "")

                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest(TAG_LOGIN);

        BaseOkHttpClient.getInstance(getApplicationContext()).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("cek_koneksi", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                        if(response==null){

                            Log.d("cek_koneksi", "Null Response");
                        }
                        else {

//                            Log.d("cek_koneksi", response.toString());
//                            Log.d("cek_koneksi_bod", response.body().string());

                            GSONLogin gson= new Gson().fromJson(response.body().string(), GSONLogin.class);
                            Log.d("cek_gson", gson.message);
                            varMessage=gson.message;

                            if(gson.message.equals("Ok")){
                                Log.d("getH", "Ok");
                                Log.d("trace_iduser", gson.id_resp);
                                sessionManager.createLoginSession("sess_email",gson.email_resp,gson.id_resp);
                                Intent gotoHome = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(gotoHome);
                            }
                            else{
                                backgroundThreadShortToast(getApplicationContext(),gson.message);
                            }
                    }
              }
        });

}



    public static void backgroundThreadShortToast(final Context context,
                                                  final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
