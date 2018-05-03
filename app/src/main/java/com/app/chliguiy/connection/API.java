package com.app.chliguiy.connection;

import com.app.chliguiy.connection.callbacks.CallbackCategory;
import com.app.chliguiy.connection.callbacks.CallbackClient;
import com.app.chliguiy.connection.callbacks.CallbackDevice;
import com.app.chliguiy.connection.callbacks.CallbackFeaturedNews;
import com.app.chliguiy.connection.callbacks.CallbackFrais;
import com.app.chliguiy.connection.callbacks.CallbackInfo;
import com.app.chliguiy.connection.callbacks.CallbackNewsInfo;
import com.app.chliguiy.connection.callbacks.CallbackNewsInfoDetails;
import com.app.chliguiy.connection.callbacks.CallbackOrder;
import com.app.chliguiy.connection.callbacks.CallbackPlan;
import com.app.chliguiy.connection.callbacks.CallbackPrevision;
import com.app.chliguiy.connection.callbacks.CallbackProduct;
import com.app.chliguiy.connection.callbacks.CallbackProductDetails;
import com.app.chliguiy.connection.callbacks.CallbackRapport;
import com.app.chliguiy.connection.callbacks.CallbackStock;
import com.app.chliguiy.connection.callbacks.CallbackUser;
import com.app.chliguiy.data.Constant;
import com.app.chliguiy.model.Checkout;
import com.app.chliguiy.model.DeviceInfo;
import com.app.chliguiy.model.Fraisequipe;
import com.app.chliguiy.model.PlanAction;
import com.app.chliguiy.model.PrivisionDeplacement;
import com.app.chliguiy.model.RapportSubmit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "User-Agent: Markeet";
    String SECURITY = "Security: " + Constant.SECURITY_CODE;

    /* Recipe API transaction ------------------------------- */

    @Headers({CACHE, AGENT})
    @GET("services/info")
    Call<CallbackInfo> getInfo(
            @Query("version") int version
    );

    /* Fcm API ----------------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/insertOneFcm")
    Call<CallbackDevice> registerDevice(
            @Body DeviceInfo deviceInfo
    );
    /* User API ----------------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/loginmobile")
    Call<CallbackUser> Login(

            @Query("username") String username,

            @Query("password") String password
    );



    /* News Info API ---------------------------------------------------- */

    @Headers({CACHE, AGENT})
    @GET("services/listFeaturedNews")
    Call<CallbackFeaturedNews> getFeaturedNews();

    @Headers({CACHE, AGENT})
    @GET("services/listNews")
    Call<CallbackNewsInfo> getListNewsInfo(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query
    );

    @Headers({CACHE, AGENT})
    @GET("services/getNewsDetails")
    Call<CallbackNewsInfoDetails> getNewsDetails(
            @Query("id") long id
    );

    /* Category API ---------------------------------------------------  */
    @Headers({CACHE, AGENT})
    @GET("services/listCategory")
    Call<CallbackCategory> getListCategory();

    /* Category API ---------------------------------------------------  */
    @Headers({CACHE, AGENT})
    @GET("services/listClient")
    Call<CallbackClient> getListClient(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query,
            @Query("id") String id
    );

    /* Category API ---------------------------------------------------  */
    @Headers({CACHE, AGENT})
    @GET("services/listrapport")
    Call<CallbackRapport> getListrapport(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query,
            @Query("id") String id
    );
    /* Category API ---------------------------------------------------  */
    @Headers({CACHE, AGENT})
    @GET("services/listprivision")
    Call<CallbackPrevision> getListPrivision(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query,
            @Query("id") String id
    );
    /* Category API ---------------------------------------------------  */
    @Headers({CACHE, AGENT})
    @GET("services/listplan")
    Call<CallbackPlan> getlistPlan(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query,
            @Query("id") String id
    );
    /* Category API ---------------------------------------------------  */
    @Headers({CACHE, AGENT})
    @GET("services/listfrais")
    Call<CallbackFrais> getListFrais(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query,
            @Query("id") String id
    );
    /* Stock API ---------------------------------------------------  */
    @Headers({CACHE, AGENT})
    @GET("services/listStock")
    Call<CallbackStock> getlistStock(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query,
            @Query("id") String id
    );



    /* Product API ---------------------------------------------------- */

    @Headers({CACHE, AGENT})
    @GET("services/listProduct")
    Call<CallbackProduct> getListProduct(
            @Query("page") int page,
            @Query("count") int count,
            @Query("q") String query,
            @Query("category_id") long category_id
    );

    @Headers({CACHE, AGENT})
    @GET("services/getProductDetails")
    Call<CallbackProductDetails> getProductDetails(
            @Query("id") long id
    );

    /* Checkout API ---------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/submitProductOrder")
    Call<CallbackOrder> submitProductOrder(
            @Body Checkout checkout
    );
    /* Checkout API ---------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/submitrapport")
    Call<CallbackRapport> submitRapport(
            @Body RapportSubmit rapportSubmit
            );
    /* Checkout API ---------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/submitfrais")
    Call<CallbackRapport> submitfrais(
            @Body Fraisequipe fraisequipe
    );
    /* Checkout API ---------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/submitplan")
    Call<CallbackRapport> submitplan(
            @Body PlanAction planAction
    );
    /* Checkout API ---------------------------------------------------- */
    @Headers({CACHE, AGENT, SECURITY})
    @POST("services/submitprevision")
    Call<CallbackRapport> submitprevision(
            @Body PrivisionDeplacement privisionDeplacement
    );
}
