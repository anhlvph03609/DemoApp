package com.example.administrator.demoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {
    private EditText edtName,edtPrice;
    private ImageView imgUpload,imgProduct;
    private Button btnAdd;
    GalleryPhoto galleryPhoto;
    String selectedPhoto;
    final  int GALLERY_REQUEST =  1111;
    String username ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        edtName = (EditText) findViewById(R.id.edtAddName);
        edtPrice = (EditText) findViewById(R.id.edtAddPrice);
        username = getIntent().getStringExtra("username");

        btnAdd = (Button) findViewById(R.id.btnAdd);
        imgProduct =(ImageView) findViewById(R.id.Addimg);
        imgUpload = (ImageView) findViewById(R.id.ivGallery);
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivityForResult(galleryPhoto.openGalleryIntent(),GALLERY_REQUEST);
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(300,300).getBitmap();
                    String encodedPhoto = ImageBase64.encode(bitmap);
                    DateFormat df = new SimpleDateFormat("yyMMddHHmmssZ");
                    String date = df.format(Calendar.getInstance().getTime());
                    HashMap <String,String> postData = new HashMap<String,String>();
                    postData.put("txtName",edtName.getText().toString());
                    postData.put("txtPrice",edtPrice.getText().toString());
                    postData.put("txtUsername",username);
                    postData.put("txtImageUrl","http://demophp2.esy.es/upload/"+date+".jpeg");
                    postData.put("image",encodedPhoto);
                    postData.put("imageName",date.toString());
                    postData.put("mobile","android");



                    PostResponseAsyncTask insertTask = new PostResponseAsyncTask(AddProductActivity.this, postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if(s.contains("success")){
                                finish();
                                Toast.makeText(AddProductActivity.this,"Insert Successfully!",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(AddProductActivity.this,username,Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    insertTask.execute("http://demophp2.esy.es/insert.php");
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),"Something wrong while endcoding photo!",Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQUEST){
                Uri uri =data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;

                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512,512).getBitmap();

                    imgProduct.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),"Something wrong while choosing photo!",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
