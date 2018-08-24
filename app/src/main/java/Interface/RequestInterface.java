package Interface;

import Response.JsonResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import share.Api;

public interface RequestInterface {
    @POST("api/api_get_posting")
    Call<JsonResponse> getPost();
}