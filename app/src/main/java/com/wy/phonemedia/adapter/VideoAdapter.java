package com.wy.phonemedia.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wy.phonemedia.R;
import com.wy.phonemedia.entity.MediaItem;
import com.wy.phonemedia.utils.Utils;

import java.util.List;

/**
 * Created by Wyman on 2016/9/19.
 * WeChat: wy391920778
 * Effect: videoPager的适配器
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyHolder> {
    private Context mContext;
    private List<MediaItem> mediaItems;
    private LayoutInflater inflater;
    private Utils utils;
    private boolean isVideo;
    public VideoAdapter(Context context, List<MediaItem> mediaItems,boolean isVideo) {
        this.mContext = context;
        this.mediaItems = mediaItems;
        inflater = LayoutInflater.from(mContext);
        utils = new Utils();
        this.isVideo = isVideo;
    }

    /**
     * recyclerView item点击事件的回调接口
     */
    public interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        Log.e("setOnItemClickListener", "setOnItemClickListener");
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.item_video_pager, parent, false);
        View view = View.inflate(mContext, R.layout.item_video_pager, null);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        final MediaItem mediaItem = mediaItems.get(position);
        holder.tv_video_name.setText(mediaItem.getName());
        holder.tv_video_size.setText(Formatter.formatFileSize(mContext, mediaItem.getSize()));
        holder.tv_video_time.setText(utils.stringForTime((int) mediaItem.getDuration()));
        if (!isVideo) {
            //音频
            holder.iv_video.setImageResource(R.drawable.music_default_bg);
        }

        //设置点击事件

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onClick", "onClick:" + "onClick");
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
        ImageView iv_video;
        TextView tv_video_name;
        TextView tv_video_time;
        TextView tv_video_size;

        public MyHolder(View itemView) {
            super(itemView);
            iv_video = (ImageView) itemView.findViewById(R.id.iv_video);
            tv_video_name = (TextView) itemView.findViewById(R.id.tv_video_name);
            tv_video_time = (TextView) itemView.findViewById(R.id.tv_video_time);
            tv_video_size = (TextView) itemView.findViewById(R.id.tv_video_size);
        }
    }
}

