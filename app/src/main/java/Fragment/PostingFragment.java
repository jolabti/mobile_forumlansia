package Fragment;

import android.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coba.els_connect.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import Adapter.DataAdapter;
import Interface.RequestInterface;
import Model.PostModel;
import Response.JsonResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import share.Api;

public class PostingFragment extends Fragment {
    private RecyclerView viewPost;
    private TextView tvName, tvPosting, tvDate;

    public ArrayList<PostModel> data;
    private DataAdapter adapter;

    public static PostingFragment newInstance() {
        PostingFragment fragment = new PostingFragment();
        // Fragment fragment = PostingFragment.newInstance();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posting, container, false);

        viewPost = rootView.findViewById(R.id.card_recycler_view);

        tvName = rootView.findViewById(R.id.tv_name);
        tvPosting = rootView.findViewById(R.id.tv_posting);
        tvDate= rootView.findViewById(R.id.tv_date);




        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPost.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        viewPost.setLayoutManager(layoutManager);


        startRequestPosts();


    }


    public void startRequestPosts(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
//                .baseUrl("https://earthquake.usgs.gov/fdsnws/event/1/query?")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JsonResponse> call = request.getPost();
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {


                JsonResponse jsonResponse = response.body();



                Log.d("jsrp", String.valueOf(jsonResponse.getPosts()));

                data = new ArrayList<>(Arrays.asList(jsonResponse.getPosts()));
                adapter = new DataAdapter(data, getActivity());
                viewPost.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }


}
