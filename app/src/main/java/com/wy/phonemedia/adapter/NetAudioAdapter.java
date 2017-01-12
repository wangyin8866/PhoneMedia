package com.wy.phonemedia.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wy.phonemedia.R;
import com.wy.phonemedia.activity.ImageActivity;
import com.wy.phonemedia.entity.AudioItem;
import com.wy.phonemedia.utils.Utils;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Wyman on 2016/9/19.
 * WeChat: wy391920778
 * Effect: netVideoPager的适配器(分类型的listView)
 */
public class NetAudioAdapter extends BaseAdapter {
    /**
     * 视频
     */
    private static final int TYPE_VIDEO = 0;

    /**
     * 图片
     */
    private static final int TYPE_IMAGE = 1;

    /**
     * 文字
     */
    private static final int TYPE_TEXT = 2;

    /**
     * GIF图片
     */
    private static final int TYPE_GIF = 3;


    /**
     * 软件推广
     */
    private static final int TYPE_AD = 4;

    private Context mContext;
    private List<AudioItem.ListBean> mData;
    private LayoutInflater inflater;
    private Utils utils;

    public NetAudioAdapter(List<AudioItem.ListBean> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        utils = new Utils();
    }

    /**
     * 得到类型的总数
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 5;
    }

    /**
     * 根据位置得到对应的类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        AudioItem.ListBean listBean = mData.get(position);
        String type = listBean.getType();
        int itemType;
        if ("video".equals(type)) {
            itemType = TYPE_VIDEO;
        } else if ("gif".equals(type)) {
            itemType = TYPE_GIF;
        } else if ("image".equals(type)) {
            itemType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemType = TYPE_TEXT;
        } else {
            itemType = TYPE_AD;//广告
        }
        return itemType;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemType = getItemViewType(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            //初始化控件
            convertView = initView(convertView, itemType, viewHolder);
            //初始化公共部分
            initCommonView(convertView, itemType, viewHolder);
            //设置tag
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //绑定数据
        //根据位置得到数据,绑定数据
        AudioItem.ListBean mediaItem = mData.get(position);
        bindData(itemType, viewHolder, mediaItem);
        return convertView;
    }

    /**
     * 初始化所有数据
     * @param itemType
     * @param viewHolder
     * @param mediaItem
     */
    private void bindData(int itemType, ViewHolder viewHolder, final AudioItem.ListBean mediaItem) {
        switch (itemType) {
            case TYPE_VIDEO://视频
                bindCommonData(viewHolder, mediaItem);
                //第一个参数是视频播放地址，第二个参数是封面图像，第三参数是标题
                viewHolder.jcv_videoplayer.setUp(mediaItem.getVideo().getVideo().get(0), mediaItem.getVideo().getThumbnail().get(0), null);
                viewHolder.tv_play_nums.setText(mediaItem.getVideo().getPlaycount() + "次播放");
                viewHolder.tv_video_duration.setText(utils.stringForTime(mediaItem.getVideo().getDuration() * 1000));

                break;
            case TYPE_IMAGE://图片
                bindCommonData(viewHolder, mediaItem);
                Glide.with(mContext).load(mediaItem.getImage().getThumbnail_small().get(0)).placeholder(R.drawable.bg_item).error(R.drawable.ic_launcher).diskCacheStrategy(DiskCacheStrategy.RESULT).into(viewHolder.iv_image_icon);
                viewHolder.iv_image_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//显示大图
                        Intent intent = new Intent(mContext, ImageActivity.class);
                        intent.putExtra("url", mediaItem.getImage().getBig().get(0));
                        mContext.startActivity(intent);
                    }
                });
                break;
            case TYPE_TEXT://文字
                bindCommonData(viewHolder, mediaItem);
                break;
            case TYPE_GIF://gif
                bindCommonData(viewHolder, mediaItem);
                System.out.println("mediaItem.getGif().getImages().get(0)" + mediaItem.getGif().getImages().get(0));
                Glide.with(mContext).load(mediaItem.getGif().getImages().get(0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.iv_image_gif);

                break;
            case TYPE_AD://软件广告
                break;
        }
    }

    private void initCommonView(View convertView, int itemType, ViewHolder viewHolder) {
        switch (itemType) {
            case TYPE_VIDEO://视频
            case TYPE_IMAGE://图片
            case TYPE_TEXT://文字
            case TYPE_GIF://gif
                //加载除开广告部分的公共部分视图
                //user info
                viewHolder.iv_headpic = (ImageView) convertView.findViewById(R.id.iv_headpic);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_time_refresh = (TextView) convertView.findViewById(R.id.tv_time_refresh);
                viewHolder.iv_right_more = (ImageView) convertView.findViewById(R.id.iv_right_more);
                //bottom
                viewHolder.iv_video_kind = (ImageView) convertView.findViewById(R.id.iv_video_kind);
                viewHolder.tv_video_kind_text = (TextView) convertView.findViewById(R.id.tv_video_kind_text);
                viewHolder.tv_shenhe_ding_number = (TextView) convertView.findViewById(R.id.tv_shenhe_ding_number);
                viewHolder.tv_shenhe_cai_number = (TextView) convertView.findViewById(R.id.tv_shenhe_cai_number);
                viewHolder.tv_posts_number = (TextView) convertView.findViewById(R.id.tv_posts_number);
                viewHolder.ll_download = (LinearLayout) convertView.findViewById(R.id.ll_download);
                break;
        }
        //中间公共部分 -所有的都有
        viewHolder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
    }

    private View initView(View convertView, int itemType, ViewHolder viewHolder) {
        switch (itemType) {
            case TYPE_VIDEO://视频
                convertView = View.inflate(mContext, R.layout.all_video_item, null);
                //在这里实例化特有的
                viewHolder.tv_play_nums = (TextView) convertView.findViewById(R.id.tv_play_nums);
                viewHolder.tv_video_duration = (TextView) convertView.findViewById(R.id.tv_video_duration);

                viewHolder.jcv_videoplayer = (JCVideoPlayer) convertView.findViewById(R.id.jcv_videoplayer);
                break;
            case TYPE_IMAGE://图片
                convertView = View.inflate(mContext, R.layout.all_image_item, null);
                viewHolder.iv_image_icon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
                break;
            case TYPE_TEXT://文字
                convertView = View.inflate(mContext, R.layout.all_text_item, null);
                break;
            case TYPE_GIF://gif
                convertView = View.inflate(mContext, R.layout.all_gif_item, null);
                viewHolder.iv_image_gif = (GifImageView) convertView.findViewById(R.id.iv_image_gif);
                break;
            case TYPE_AD://软件广告
                convertView = View.inflate(mContext, R.layout.all_ad_item, null);
                viewHolder.btn_install = (Button) convertView.findViewById(R.id.btn_install);
                viewHolder.iv_image_icon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
                break;
        }
        return convertView;
    }

    /**
     * 舒适化公共部分数据
     * @param viewHolder
     * @param mediaItem
     */
    private void bindCommonData(ViewHolder viewHolder, AudioItem.ListBean mediaItem) {
        if (mediaItem.getU() != null && mediaItem.getU().getHeader() != null && mediaItem.getU().getHeader().get(0) != null) {
//                x.image().bind(viewHolder.iv_headpic, mediaItem.getU().getHeader().get(0));
            Glide.with(mContext).load(mediaItem.getU().getHeader().get(0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.iv_headpic);

        }
        if (mediaItem.getU() != null && mediaItem.getU().getName() != null) {
            viewHolder.tv_name.setText(mediaItem.getU().getName());
        }
        viewHolder.tv_context.setText(mediaItem.getText());
        viewHolder.tv_time_refresh.setText(mediaItem.getPasstime());

        //设置标签
        List<AudioItem.ListBean.TagsBean> tagsEntities = mediaItem.getTags();
        if (tagsEntities != null && tagsEntities.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tagsEntities.size(); i++) {
                buffer.append(tagsEntities.get(i).getName() + " ");
            }
            viewHolder.tv_video_kind_text.setText(buffer.toString());
        }

        //设置点赞，踩,转发
        viewHolder.tv_shenhe_ding_number.setText(mediaItem.getUp());
        viewHolder.tv_shenhe_cai_number.setText(mediaItem.getDown() + "");
        viewHolder.tv_posts_number.setText(mediaItem.getForward() + "");

    }

    static class ViewHolder {
        //user_info
        ImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        ImageView iv_right_more;
        //bottom
        ImageView iv_video_kind;
        TextView tv_video_kind_text;
        TextView tv_shenhe_ding_number;
        TextView tv_shenhe_cai_number;
        TextView tv_posts_number;
        LinearLayout ll_download;

        //中间公共部分 -所有的都有
        TextView tv_context;


        //Video
//        TextView tv_context;
        TextView tv_play_nums;
        TextView tv_video_duration;

        JCVideoPlayer jcv_videoplayer;

        //Image
        ImageView iv_image_icon;
//        TextView tv_context;

        //Text
//        TextView tv_context;

        //Gif
        GifImageView iv_image_gif;
//        TextView tv_context;

        //软件推广
        Button btn_install;
//        TextView iv_image_icon;
        //TextView tv_context;

    }
}
