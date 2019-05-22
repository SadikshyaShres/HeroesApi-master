package np.com.softwarica.heroesapi;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import np.com.softwarica.heroesapi.api.HeroesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddHeroActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etName,etDesc;
    private Button btnSave;
    public static final  String BASE_URL="http://10.0.2.2:3000/";
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hero);
        etName=findViewById(R.id.etName);
        etDesc=findViewById(R.id.etDesc);
        btnSave=findViewById(R.id.btnSave);
        imgProfile=findViewById(R.id.imgProfile);
        loadFromURL();

        btnSave.setOnClickListener(this);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.imgProfile) {
                    BrowseImage();
                }
            }
        });

    }
    private void BrowseImage(){
        Intent  intent=new Intent(Intent.ACTION_PICK);
        intent.setType( "image/*");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if(data ==null){
                Toast.makeText(this, "Please select an image ", Toast.LENGTH_SHORT).show();
            }
        }
        Uri uri= data.getData();
        String imagePath = getRealPathFrom(uri);
        previewImage(imagePath);
    }
    private  String getRealPathFrom( Uri uri){
        String[] projection={MediaStore.Images.Media.DATA};
        CursorLoader loader=new CursorLoader(getApplicationContext(),uri,projection, null, null,null);
        Cursor cursor=loader.loadInBackground();
        int colIndex= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(colIndex);
        cursor.close();
        return result;

    }
    private void previewImage(String imagePath){
        File imgfile =new File(imagePath);
        if (imgfile.exists()){
            Bitmap myBitmap= BitmapFactory.decodeFile(imgfile.getAbsolutePath());
            imgProfile.setImageBitmap(myBitmap);
        }
    }

    private void StrictMode(){
        android.os.StrictMode.ThreadPolicy policy=
                new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
        android.os.StrictMode.setThreadPolicy(policy);

    }
    private  void loadFromURL(){
        StrictMode();
        try{
            String imgURL="https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/avengers-endgame-thor-chris-hemsworth-1555603082.jpg?crop=0.529xw:1.00xh;0.250xw,0&resize=480:*";
            URL url=new URL(imgURL);
            imgProfile.setImageBitmap(BitmapFactory.decodeStream((InputStream)url.getContent()));

        }catch (IOException e){
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnSave)
        {
            Save();
        }

    }
    private void Save(){
        String name=etName.getText().toString();
        String desc=etDesc.getText().toString();

        Map<String,String> map=new HashMap<>();
        map.put("name",name);
        map.put("desc",desc);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HeroesAPI heroesAPI= retrofit.create(HeroesAPI.class);
//        Call<Void> heroesCall=heroesAPI.addHero(name,desc);
        Call<Void>  heroesCall=heroesAPI.addHero(map);
        heroesCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(AddHeroActivity.this,"Code" + response.code(),Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(AddHeroActivity.this,"Successfully Added",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddHeroActivity.this,"Error" + t.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }
}
