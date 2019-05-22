package np.com.softwarica.heroesapi.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface HeroesAPI {
    @FormUrlEncoded
    @POST("heroes")
    Call<Void> addHero(@Field("name") String name , @Field("desc") String desc);

@FormUrlEncoded
@POST("heroes")
Call<Void> addHero(@FieldMap Map<String,String> map);}