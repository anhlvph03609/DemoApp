package com.example.administrator.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
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

public class UserProductActivity extends AppCompatActivity implements AsyncResponse {
    private ArrayList<Product> productsList;
    private ListView lvProduct;
    private FunDapter<Product> adapter;
    TextView tvErr;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        username = getIntent().getStringExtra("username");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        tvErr = (TextView) findViewById(R.id.TvNoProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProductActivity.this,AddProductActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
                finish();
            }
        });
        lvProduct = (ListView)findViewById(R.id.lvProduct);
        ImageLoader.getInstance().init(UILConfig.config(UserProductActivity.this));
        HashMap data = new HashMap();
        data.put("action","getUserProduct");
        data.put("username",getIntent().getStringExtra("username").toString());
        PostResponseAsyncTask taskRead = new PostResponseAsyncTask(UserProductActivity.this, data, this);
        taskRead.execute("http://demophp2.esy.es/product.php");

        registerForContextMenu(lvProduct);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Product  selectedProduct = adapter.getItem(info.position);
        if(item.getItemId() == R.id.menuUpdate){
            Intent intent = new Intent(UserProductActivity.this,UpdateProductActivity.class);
            intent.putExtra("product", (Serializable) selectedProduct);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.menuRemove){
            productsList.remove(selectedProduct);
            adapter.notifyDataSetChanged();

            HashMap postData = new HashMap();
            postData.put("pid",""+selectedProduct.pid);
            postData.put("mobile","android");
            PostResponseAsyncTask taskRemove = new PostResponseAsyncTask(UserProductActivity.this, postData, new AsyncResponse() {
                @Override
                public void processFinish(String s) {
                    if(s.contains("success")){
                        Toast.makeText(UserProductActivity.this,"Delete Successfully!",Toast.LENGTH_LONG).show();
                    }
                }
            });
            taskRemove.execute("http://demophp2.esy.es/remove.php");
        }
        return super.onContextItemSelected(item);

    }

    @Override
    public void processFinish(String s) {
        if(s.equals("error")){
           tvErr.setVisibility(View.VISIBLE);
        }
        else{
            tvErr.setVisibility(View.GONE);
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
                return "Username: " + item.username;
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
                Picasso.with(UserProductActivity.this).load(url).placeholder(android.R.drawable.star_big_on).error(android.R.drawable.stat_sys_download).into(view);
                //  ImageLoader.getInstance().displayImage(url,view);
            }
        });

        adapter = new FunDapter<>(UserProductActivity.this,productsList,R.layout.layout_list,dict);
        lvProduct.setAdapter(adapter);}


        }


}
