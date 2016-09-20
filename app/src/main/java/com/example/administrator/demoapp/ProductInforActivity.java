package com.example.administrator.demoapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.demoapp.model.Product;
import com.squareup.picasso.Picasso;


public class ProductInforActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbarLayout = null;
    TextView tvName,tvRelease,tvPrice,tvInfor;
    ImageView imgProduct;
    String infor = "4.7-inch screen\n" +
            "1,334 x 750 pixel display (326ppi pixel density)\n" +
            "Dimensions: 158.2 x 67.1 x 7.1mm\n" +
            "Weight: 138g\n" +
            "A10 Fusion (64-bit, quad-core CPU, six-core GPU)\n" +
            "32/128/256GB storage\n" +
            "12-megapixel main camera (OIS, f/1.8)\n" +
            "7-megapixel secondary camera\n" +
            "Stereo speakers\n" +
            "Fingerprint scanner\n" +
            "IP67 water-resistant\n" +
            "iOS 10 software\n" +
            "Colours: Jet Black, Black, Silver, Gold, Rose Gold\n";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_infor);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton)(findViewById(R.id.faButton));
        tvName = (TextView) findViewById(R.id.tvName);
        tvRelease =(TextView) findViewById(R.id.quote);
        tvPrice = (TextView)findViewById(R.id.quote2);
        tvInfor = (TextView) findViewById(R.id.tvInfor);
        imgProduct = (ImageView) findViewById(R.id.profile_id);
        Product product =(Product) getIntent().getSerializableExtra("product");
        tvRelease.setText(product.username);
        tvName.setText(product.name);
        tvPrice.setText("Price: "+product.price);
        tvInfor.setText(infor);
        Picasso.with(getApplicationContext()).load(product.image_url).error(android.R.drawable.stat_notify_error).placeholder( R.drawable.progress_animation ).into(imgProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductInforActivity.this,"click button",Toast.LENGTH_LONG).show();
            }
        });
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(product.name);
        collapsingToolbarLayout.setBackgroundColor(Color.parseColor("#AA000000"));
        dynamicToolbarColor();

        toolbarTextAppernce();

    }


    private void dynamicToolbarColor() {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.canada);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(R.attr.colorPrimary));
                collapsingToolbarLayout.setStatusBarScrimColor(palette.getMutedColor(R.attr.colorPrimaryDark));
            }
        });
    }


    private void toolbarTextAppernce() {
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_context_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
