package com.example.sxguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager vp;
    private CircleIndicator dot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vp=findViewById(R.id.vp);
        dot=findViewById(R.id.dot);

        List<ImageView> views=new ArrayList<>();
        ImageView iv1=new ImageView(this);
        iv1.setImageResource(R.drawable.iv);
        views.add(iv1);
        ImageView iv2=new ImageView(this);
        iv2.setImageResource(R.drawable.ic_launcher_foreground);
        views.add(iv2);
        ImageView iv3=new ImageView(this);
        iv3.setImageResource(R.drawable.iv);
        views.add(iv3);
        vp.setAdapter(new VPagerAdapter(views));
        dot.setViewPager(vp);
    }
}
