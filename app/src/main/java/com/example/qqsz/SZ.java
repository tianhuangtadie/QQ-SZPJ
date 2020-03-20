package com.example.qqsz;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class SZ extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private int leftRight;
    private int topBottom;
    private int spanCount;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.qqsz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mRecyclerView = findViewById(R.id.RecyclerView);

        //网格布局
        spanCount = 2;  //列数
        leftRight = 0;  //左右间隔
        topBottom = 0;  //上下间隔

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false));
        WindowManager manager = getWindowManager();
        DisplayMetrics mMetricd = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(mMetricd);
        final List<String> list = FileUtils.getSZ();
        if (list.size() == 0) {
            Toast.makeText(this, "你一张都还没破解！", Toast.LENGTH_SHORT).show();
            return;
        }
        mRecyclerViewAdapter = new RecyclerViewAdapter(this, (ArrayList<String>) list, mMetricd.widthPixels, spanCount, leftRight, topBottom);
        mRecyclerView.addItemDecoration(new MyDividerItem(leftRight, topBottom));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                shareImageToQQ(getLoacalBitmap(list.get(position)));
            }
        });
    }

    /**
     * 分享图片给QQ好友
     *
     * @param bitmap
     */
    public void shareImageToQQ(Bitmap bitmap) {
        if (PlatformUtil.isInstallApp(this, PlatformUtil.PACKAGE_MOBILE_QQ)) {
            try {
                Uri uriToImage = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("image/*");
                // 遍历所有支持发送图片的应用。找到需要的应用
                ComponentName componentName = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
                shareIntent.setComponent(componentName);
                // mContext.startActivity(shareIntent);
                startActivity(Intent.createChooser(shareIntent, "Share"));
            } catch (Exception e) {
//            ContextUtil.getInstance().showToastMsg("分享图片到**失败");
            }
        }
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
