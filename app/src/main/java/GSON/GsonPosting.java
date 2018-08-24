package GSON;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import Model.PostAll;

public class GsonPosting {

    @SerializedName("response")
    public String response;

    @SerializedName("posts")
    public ArrayList<PostAll> postse;



}
