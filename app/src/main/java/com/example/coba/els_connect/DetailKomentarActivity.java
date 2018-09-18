package com.example.coba.els_connect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import Adapter.KomentaritemAdapter;
import Pojo.DC_POJO;
import Utils.SessionManager;
import base.BaseOkHttpClient;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import share.Api;

public class DetailKomentarActivity extends AppCompatActivity {
    String tampungID, passByPostingVariabel;
    DC_POJO dcpojo;
    SessionManager sessionManager;

    TextView tv_pass_by_posting;

    ListView lvElement;

    ArrayList<DC_POJO> dcall = new ArrayList<>();

    KomentaritemAdapter kmtAdapter=null;

    Button btn_balas;
    EditText ed_balas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_komentar);

        sessionManager = new SessionManager(getApplicationContext());
        tv_pass_by_posting = findViewById(R.id.tv_passby_posting);
        lvElement= findViewById(R.id.lv_detail_komentar);
        ed_balas = findViewById(R.id.textBalas);
        btn_balas =  findViewById(R.id.button_balas);


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            tampungID = null;
        } else {
            tampungID = extras.getString("idposting");
            passByPostingVariabel = extras.getString("passbyposting");

            Log.d("cek_tampungID", tampungID);

            startRequestDetailKomentar(tampungID,passByPostingVariabel);

        }

        btn_balas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRequestReply();
            }
        });


    }


    public void startRequestDetailKomentar(String idPosting, String passByPosting) {

        tv_pass_by_posting.setText(passByPosting);

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.JSON_SHOW_DETAIL_KOMENTAR + "/" + idPosting)
                .tag("DETAILKOMENTAR")
                .addHeader("application/json", "charset=utf-8")
                //.addHeader("Content-Type","application/x-www-form-urlencoded")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest("SERVICEHARGA");
        BaseOkHttpClient.getInstance(getApplicationContext()).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if(e.getMessage()!=""){
                        sessionManager.logoutUser();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                if (response.isSuccessful()) {

                    //Log.d("okeresponse", response.body().string());

                    try {


                        JSONObject jobRoot = new JSONObject(response.body().string());

                        JSONArray jarPost = jobRoot.getJSONArray("posts");


                        Log.d("jarPost", String.valueOf(jarPost.length()));
                        dcall.clear();
                        for (int i = 0; i < jarPost.length(); i++) {
                            dcpojo = new DC_POJO();
                            Log.d("jarpostid", jarPost.getJSONObject(i).toString());

                            dcpojo.setPost_id(jarPost.getJSONObject(i).getString("post_id"));
                            dcpojo.setPosting(jarPost.getJSONObject(i).getString("posting"));
                            dcpojo.setWaktu(jarPost.getJSONObject(i).getString("waktu"));
                            dcpojo.setUser_id(jarPost.getJSONObject(i).getString("user_id"));
                            dcpojo.setUrlgambar(jarPost.getJSONObject(i).getString("urlgambar"));
                            dcpojo.setKomentar(jarPost.getJSONObject(i).getString("komentar"));
                            dcpojo.setUsermail(jarPost.getJSONObject(i).getString("email"));

                            dcall.add(dcpojo);


                        }


                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                kmtAdapter = new KomentaritemAdapter(getApplicationContext(),dcall);
                                lvElement.setAdapter(kmtAdapter);
                                kmtAdapter.notifyDataSetChanged();

                            }
                        });


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (!response.isSuccessful()) {

                    Log.d("okeresponse", "Gagal");
                }

            }
        });
    }


   public void startRequestReply(){


        Log.d("trace_tampungID",tampungID);
        Log.d("trace_rpy_userid",sessionManager.getUserDetails().get("post_btnpost_btn"));
        final RequestBody requestBody = new FormBody.Builder()
                .add("komentar", ed_balas.getText().toString())
                .build();


        final okhttp3.Request request = new okhttp3.Request.Builder()


                .url(Api.JSON_DO_REPLY_KOMENTAR+"/"+ sessionManager.getUserDetails().get("post_btnpost_btn")+"/"+tampungID)
                .tag("REPLYKOMENTAR")
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(requestBody)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest("REPLYKOMENTAR");

        BaseOkHttpClient.getInstance(getApplicationContext()).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) {

                if(response.isSuccessful()){


                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getApplicationContext(),"Komentar berhasil diupload", Toast.LENGTH_SHORT).show();

                            startRequestDetailKomentar(tampungID,passByPostingVariabel);

                            ed_balas.setText("");

                        }
                    });


                }

            }
        });


    }


}
