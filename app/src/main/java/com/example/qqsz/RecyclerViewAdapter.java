package com.example.qqsz;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MXY on 2018/3/18.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private Context mContext;
    private List<String> mData;
    private RecyclerViewAdapter.OnItemClickListener mOnItemClickListener;
    private int leftRight;
    private int topBottom;
    private int spanCount;
    private int LayoutWidth;


    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        ImageView imageView;
        TextView zt;

        //初始化viewHolder，此处绑定后在onBindViewHolder中可以直接使用
        public ViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            imageView = itemView.findViewById(R.id.im_gd);
        }
    }

    public RecyclerViewAdapter(Context mContext, ArrayList<String> data, int LayoutWidth, int spanCount, int leftRight, int topBottom) {
        this.mContext = mContext;
        this.mData = data;
        this.LayoutWidth = LayoutWidth;
        this.spanCount = spanCount;
        this.leftRight = leftRight;
        this.topBottom = topBottom;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View views = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder holder = new ViewHolder(views);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (!FileUtils.isImageFile(mData.get(position))) {
            return;
        }
        //获取屏幕宽度
        int screenWidth = LayoutWidth;
        //获取单张图片宽度
        int itemImgWidth = (screenWidth - leftRight * (spanCount + 1)) / spanCount;
        //设置图片宽高
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.linearLayout.getLayoutParams();
        params.width = itemImgWidth;
        params.height = itemImgWidth;
        holder.linearLayout.setLayoutParams(params);
        holder.linearLayout.setBackground(mContext.getResources().getDrawable(R.drawable.item_bg_shape));
        holder.imageView.setImageBitmap(getLoacalBitmap(mData.get(position)));
        if (mOnItemClickListener != null) {
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(position);
                }
            });
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    FileUtils.deleteFiles(mData.get(position));
                    notifyDataSetChanged();
                    return true;
                }
            });
        }
//        Log.e(TAG, "onBindViewHolder: =============" + FileUtils.isImageFile(mData.get(position)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
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
