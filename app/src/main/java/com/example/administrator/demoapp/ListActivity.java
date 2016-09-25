package com.example.administrator.demoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.administrator.demoapp.model.Product;
import com.example.administrator.demoapp.model.UILConfig;
import com.kosalgeek.android.json.JsonConverter;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity implements AsyncResponse {
    final String LOG = "ListActivity";
    private ArrayList<Product> productsList;
    private ListView lvProduct;
    private FunDapter<Product> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(ListActivity.this,AddProductActivity.class);
                intent.putExtra("username",getIntent().getStringExtra("username"));
                startActivity(intent);
                finish();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lvProduct = (ListView)findViewById(R.id.lvProduct);
        ImageLoader.getInstance().init(UILConfig.config(ListActivity.this));
        HashMap data = new HashMap();
        data.put("action","ShowAll");
        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(ListActivity.this, data, this);
        taskRead.execute("http://192.168.56.1:80/customer/product.php");

        lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = adapter.getItem(position);
                Intent intent = new Intent(ListActivity.this,ProductInforActivity.class);
                intent.putExtra("product",(Serializable)product);
                startActivity(intent);

            }
        });
    }



    @Override
    public void processFinish(String s) {

            productsList =  new JsonConverter<Product>().toArrayList(s,Product.class);
        BindDictionary<Product> dict = new BindDictionary<Product>();
        dict.addStringField(R.id.tvName, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product item, int position) {
                return item.name;
            }
        });
        dict.addStringField(R.id.tvPrice, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product item, int position) {
                return "Price: "+ item.price;
            }
        });
        dict.addStringField(R.id.tvUser, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product item, int position) {
                return "UserName: " + item.username;
            }
        });
        dict.addDynamicImageField(R.id.imageView, new StringExtractor<Product>() {
            @Override
            public String getStringValue(Product item, int position) {
                return item.image_url;
            }
        }, new DynamicImageLoader() {
            @Override
            public void loadImage(String url, ImageView view) {
              // Picasso.with(ListActivity.this).load(url).placeholder(android.R.drawable.star_big_on).error(android.R.drawable.stat_sys_download).into(view);
              //  ImageLoader.getInstance().displayImage(url,view);
                Glide.with(getApplicationContext()).load(url).asBitmap().fitCenter().error(android.R.drawable.stat_notify_error).into(new BitmapImageViewTarget(view) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        view.setImageDrawable(circularBitmapDrawable);
                    }
                });

            }
        });

        adapter = new FunDapter<>(ListActivity.this,productsList,R.layout.layout_list,dict);
        lvProduct.setAdapter(adapter);

    }
}
