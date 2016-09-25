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

import com.example.administrator.demoapp.helper.SQLiteHandler;
import com.example.administrator.demoapp.model.Product;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UpdateProductActivity extends AppCompatActivity {
    EditText edtUpdateName,edtUpdatePrice,edtURL;
    ImageView imgUpdate,imgChoose;
    Button btnUpdate;
    private Product product;
    GalleryPhoto galleryPhoto;
    String selectedPhoto;
    String username;
    private SQLiteHandler db;
    final  int GALLERY_REQUEST =  1111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        username = user.get("username");

        edtUpdateName = (EditText) findViewById(R.id.edtUpdateName);
        edtUpdatePrice = (EditText) findViewById(R.id.edtUpdatePrice);

        edtURL = (EditText) findViewById(R.id.edtUpdateURL);
        imgUpdate =(ImageView) findViewById(R.id.imgUpdate);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        imgChoose = (ImageView) findViewById(R.id.imgChoose);
        product = (Product) getIntent().getSerializableExtra("product");

        if(product!=null) {
            edtUpdateName.setText(product.name);
            edtURL.setText(product.image_url);
            username = product.username;
            edtUpdatePrice.setText("" + product.price);
            Picasso.with(UpdateProductActivity.this).load(product.image_url).error(android.R.drawable.stat_notify_error).placeholder(android.R.drawable.star_big_on).into(imgUpdate);
        }
        galleryPhoto = new GalleryPhoto(getApplicationContext());
        imgChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(galleryPhoto.openGalleryIntent(),GALLERY_REQUEST);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPhoto==null){
                    HashMap<String,String> postData = new HashMap<String,String>();
                    postData.put("txtPid", "" + product.pid);
                    postData.put("txtName", edtUpdateName.getText().toString());
                    postData.put("txtPrice", edtUpdatePrice.getText().toString());
                    postData.put("txtUsername", username);
                    postData.put("action","nopic");
                    postData.put("txtImageUrl",product.image_url );
                    postData.put("mobile", "android");
                    PostResponseAsyncTask insertTask = new PostResponseAsyncTask(UpdateProductActivity.this, postData, new AsyncResponse() {
                        @Override
                        public void processFinish(String s) {
                            if (s.contains("success")) {
                                Toast.makeText(UpdateProductActivity.this, "update successfully!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(UpdateProductActivity.this,UserProductActivity.class);
                                intent.putExtra("username",username);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });
                    insertTask.execute("http://192.168.56.1:80/customer/update.php");
                }
                else
                {
                    try {
                            Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(300,300).getBitmap();
                            String encodedPhoto = ImageBase64.encode(bitmap);
                            DateFormat df = new SimpleDateFormat("yyMMddHHmmssZ");
                            String date = df.format(Calendar.getInstance().getTime());
                            HashMap<String,String> postData = new HashMap<String,String>();
                            postData.put("txtPid", "" + product.pid);
                            postData.put("action","pic");
                            postData.put("txtName", edtUpdateName.getText().toString());
                            postData.put("txtPrice", edtUpdatePrice.getText().toString());
                            postData.put("txtUsername", username);
                            postData.put("txtImageUrl","http://demophp2.esy.es/upload/"+date+".jpeg" );
                            postData.put("image",encodedPhoto);
                            postData.put("imageName",date.toString());
                            postData.put("mobile", "android");
                            PostResponseAsyncTask insertTask = new PostResponseAsyncTask(UpdateProductActivity.this, postData, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    if (s.contains("success")) {
                                        Toast.makeText(UpdateProductActivity.this, "update successfully!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            });
                            insertTask.execute("http://demophp2.esy.es/update.php");
                        }catch (FileNotFoundException e) {
                            Toast.makeText(getApplicationContext(),"Something wrong while endcoding photo!",Toast.LENGTH_LONG).show();
                 }
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

                    imgUpdate.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),"Something wrong while choosing photo!",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
