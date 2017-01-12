package com.wy.phonemedia.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wy.phonemedia.R;
import com.wy.phonemedia.entity.MediaItem;
import com.wy.phonemedia.utils.Utils;

import java.util.List;

/**
 * Created by Wyman on 2016/9/19.
 * WeChat: wy391920778
 * Effect: netVideoPager的适配器
 */
public class NetVideoAdapter extends RecyclerView.Adapter<NetVideoAdapter.MyHolder> {
    private Context mContext;
    private List<MediaItem> mediaItems;
    private LayoutInflater inflater;
    private Utils utils;


    public NetVideoAdapter(Context context, List<MediaItem> mediaItems) {
        this.mContext = context;
        this.mediaItems = mediaItems;
        inflater = LayoutInflater.from(mContext);
        utils = new Utils();
    }

    /**
     * recyclerView item点击事件的回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_net_video_pager, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        final MediaItem mediaItem = mediaItems.get(position);
        holder.tv_title.setText(mediaItem.getName());
        holder.tv_desc.setText(mediaItem.getDescribe());
        Picasso.with(mContext).load(mediaItem.getImageUrl()).placeholder(R.drawable.video_default).into(holder.iv_icon);

        //设置点击事件

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener!=null) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView,position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener!=null) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, position);
                }
                return false;
            }
        });

    }

    static class MyHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_desc;

        public MyHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
        }
    }
}

