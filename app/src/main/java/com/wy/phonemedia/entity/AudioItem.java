package com.wy.phonemedia.entity;

import java.util.List;

/**
 * Created by Wyman on 2016/10/18.
 * WeChat: wy391920778
 * Effect:
 */

public class AudioItem {

    /**
     * count : 4009
     * np : 1.476765302E9
     */

    private InfoBean info;
    /**
     * status : 4
     * comment : 104
     * top_comments : [{"voicetime":0,"status":0,"cmt_type":"text","precid":0,"content":"妓女都能当大学老师了？","like_count":35,"u":{"header":["http://wimg.spriteapp.cn/profile/large/2016/09/10/57d3e22bec9fe_mini.jpg","http://dimg.spriteapp.cn/profile/large/2016/09/10/57d3e22bec9fe_mini.jpg"],"sex":"m","uid":"12083469","name":"过千城访万江孑影未成双"},"preuid":0,"passtime":"2016-10-17 21:58:57","voiceuri":"","id":66509894}]
     * tags : [{"id":1,"name":"搞笑"},{"id":60,"name":"吐槽"},{"id":61,"name":"恶搞"},{"id":62,"name":"内涵"},{"id":63,"name":"笑话"}]
     * bookmark : 35
     * text : 别人家的老湿！制服诱惑！韩国“最美女教师”私照！！
     * image : {"medium":[],"big":["http://wimg.spriteapp.cn/ugc/2016/10/17/58048688d81c6_1.jpg","http://dimg.spriteapp.cn/ugc/2016/10/17/58048688d81c6_1.jpg"],"download_url":["http://wimg.spriteapp.cn/ugc/2016/10/17/58048688d81c6_d.jpg","http://dimg.spriteapp.cn/ugc/2016/10/17/58048688d81c6_d.jpg","http://wimg.spriteapp.cn/ugc/2016/10/17/58048688d81c6.jpg","http://dimg.spriteapp.cn/ugc/2016/10/17/58048688d81c6.jpg"],"height":7269,"width":460,"small":[],"thumbnail_small":["http://wimg.spriteapp.cn/crop/150x150/ugc/2016/10/17/58048688d81c6.jpg","http://dimg.spriteapp.cn/crop/150x150/ugc/2016/10/17/58048688d81c6.jpg"]}
     * up : 138
     * share_url : http://a.f.budejie.com/share/21432422.html?wx.qq.com
     * down : 55
     * forward : 10
     * u : {"header":["http://wimg.spriteapp.cn/profile/large/2016/05/01/572555a248804_mini.jpg","http://dimg.spriteapp.cn/profile/large/2016/05/01/572555a248804_mini.jpg"],"is_v":false,"uid":"18192453","is_vip":false,"name":"菊花姐夫"}
     * passtime : 2016-10-18 16:02:16
     * type : image
     * id : 21432422
     */

    private List<ListBean> list;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "AudioItem{" +
                "info=" + info +
                ", list=" + list +
                '}';
    }

    public static class InfoBean {
        private int count;
        private double np;

        @Override
        public String toString() {
            return "InfoBean{" +
                    "count=" + count +
                    ", np=" + np +
                    '}';
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getNp() {
            return np;
        }

        public void setNp(double np) {
            this.np = np;
        }
    }

    public static class ListBean {
        private int status;
        private String comment;
        private String bookmark;
        private String text;
        private VideoEntity video;
        private GifEntity gif;
        private ImageBean image;
        private String up;
        private String share_url;
        private int down;
        private int forward;
        private UBean u;
        private String passtime;
        private String type;
        private String id;

        private List<TopCommentsBean> top_comments;


        private List<TagsBean> tags;

        @Override
        public String toString() {
            return "ListBean{" +
                    "status=" + status +
                    ", comment='" + comment + '\'' +
                    ", bookmark='" + bookmark + '\'' +
                    ", text='" + text + '\'' +
                    ", image=" + image +
                    ", up='" + up + '\'' +
                    ", share_url='" + share_url + '\'' +
                    ", down=" + down +
                    ", forward=" + forward +
                    ", u=" + u +
                    ", passtime='" + passtime + '\'' +
                    ", type='" + type + '\'' +
                    ", id='" + id + '\'' +
                    ", top_comments=" + top_comments +
                    ", tags=" + tags +
                    '}';
        }

        public VideoEntity getVideo() {
            return video;
        }

        public void setVideo(VideoEntity video) {
            this.video = video;
        }

        public GifEntity getGif() {
            return gif;
        }

        public void setGif(GifEntity gif) {
            this.gif = gif;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getBookmark() {
            return bookmark;
        }

        public void setBookmark(String bookmark) {
            this.bookmark = bookmark;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public ImageBean getImage() {
            return image;
        }

        public void setImage(ImageBean image) {
            this.image = image;
        }

        public String getUp() {
            return up;
        }

        public void setUp(String up) {
            this.up = up;
        }

        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public int getDown() {
            return down;
        }

        public void setDown(int down) {
            this.down = down;
        }

        public int getForward() {
            return forward;
        }

        public void setForward(int forward) {
            this.forward = forward;
        }

        public UBean getU() {
            return u;
        }

        public void setU(UBean u) {
            this.u = u;
        }

        public String getPasstime() {
            return passtime;
        }

        public void setPasstime(String passtime) {
            this.passtime = passtime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<TopCommentsBean> getTop_comments() {
            return top_comments;
        }

        public void setTop_comments(List<TopCommentsBean> top_comments) {
            this.top_comments = top_comments;
        }

        public List<TagsBean> getTags() {
            return tags;
        }

        public void setTags(List<TagsBean> tags) {
            this.tags = tags;
        }

        public static class GifEntity{

            /**
             * images : ["http://ww4.sinaimg.cn/large/005OPWbujw1f5y7y68fyyg307s055npd.gif","http://wimg.spriteapp.cn/ugc/2016/07/18/578c76fab0bab.gif","http://dimg.spriteapp.cn/ugc/2016/07/18/578c76fab0bab.gif"]
             * width : 280
             * gif_thumbnail : ["http://wimg.spriteapp.cn/ugc/2016/07/18/578c76fab0bab_a_1.jpg","http://dimg.spriteapp.cn/ugc/2016/07/18/578c76fab0bab_a_1.jpg"]
             * download_url : ["http://wimg.spriteapp.cn/ugc/2016/07/18/578c76fab0bab_d.jpg","http://dimg.spriteapp.cn/ugc/2016/07/18/578c76fab0bab_d.jpg","http://wimg.spriteapp.cn/ugc/2016/07/18/578c76fab0bab_a_1.jpg","http://dimg.spriteapp.cn/ugc/2016/07/18/578c76fab0bab_a_1.jpg"]
             * height : 185
             */

            private int width;
            private int height;
            private List<String> images;
            private List<String> gif_thumbnail;
            private List<String> download_url;

            public void setWidth(int width) {
                this.width = width;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public void setImages(List<String> images) {
                this.images = images;
            }

            public void setGif_thumbnail(List<String> gif_thumbnail) {
                this.gif_thumbnail = gif_thumbnail;
            }

            public void setDownload_url(List<String> download_url) {
                this.download_url = download_url;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }

            public List<String> getImages() {
                return images;
            }

            public List<String> getGif_thumbnail() {
                return gif_thumbnail;
            }

            public List<String> getDownload_url() {
                return download_url;
            }
        }
        public static class VideoEntity {
            private int playfcount;
            private int height;
            private int width;
            private int duration;
            private int playcount;
            private List<String> video;
            private List<String> thumbnail;
            private List<String> download;

            public void setPlayfcount(int playfcount) {
                this.playfcount = playfcount;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public void setPlaycount(int playcount) {
                this.playcount = playcount;
            }

            public void setVideo(List<String> video) {
                this.video = video;
            }

            public void setThumbnail(List<String> thumbnail) {
                this.thumbnail = thumbnail;
            }

            public void setDownload(List<String> download) {
                this.download = download;
            }

            public int getPlayfcount() {
                return playfcount;
            }

            public int getHeight() {
                return height;
            }

            public int getWidth() {
                return width;
            }

            public int getDuration() {
                return duration;
            }

            public int getPlaycount() {
                return playcount;
            }

            public List<String> getVideo() {
                return video;
            }

            public List<String> getThumbnail() {
                return thumbnail;
            }

            public List<String> getDownload() {
                return download;
            }
        }
        public static class ImageBean {
            private int height;
            private int width;
            private List<?> medium;
            private List<String> big;
            private List<String> download_url;
            private List<?> small;
            private List<String> thumbnail_small;

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public List<?> getMedium() {
                return medium;
            }

            public void setMedium(List<?> medium) {
                this.medium = medium;
            }

            public List<String> getBig() {
                return big;
            }

            public void setBig(List<String> big) {
                this.big = big;
            }

            public List<String> getDownload_url() {
                return download_url;
            }

            public void setDownload_url(List<String> download_url) {
                this.download_url = download_url;
            }

            public List<?> getSmall() {
                return small;
            }

            public void setSmall(List<?> small) {
                this.small = small;
            }

            public List<String> getThumbnail_small() {
                return thumbnail_small;
            }

            public void setThumbnail_small(List<String> thumbnail_small) {
                this.thumbnail_small = thumbnail_small;
            }

            @Override
            public String toString() {
                return "ImageBean{" +
                        "height=" + height +
                        ", width=" + width +
                        ", medium=" + medium +
                        ", big=" + big +
                        ", download_url=" + download_url +
                        ", small=" + small +
                        ", thumbnail_small=" + thumbnail_small +
                        '}';
            }
        }

        public static class UBean {
            private boolean is_v;
            private String uid;
            private boolean is_vip;
            private String name;
            private List<String> header;

            public boolean isIs_v() {
                return is_v;
            }

            public void setIs_v(boolean is_v) {
                this.is_v = is_v;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public boolean isIs_vip() {
                return is_vip;
            }

            public void setIs_vip(boolean is_vip) {
                this.is_vip = is_vip;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<String> getHeader() {
                return header;
            }

            public void setHeader(List<String> header) {
                this.header = header;
            }
        }


        public static class TopCommentsBean {
            private int voicetime;
            private int status;
            private String cmt_type;
            private int precid;
            private String content;
            private int like_count;
            /**
             * header : ["http://wimg.spriteapp.cn/profile/large/2016/09/10/57d3e22bec9fe_mini.jpg","http://dimg.spriteapp.cn/profile/large/2016/09/10/57d3e22bec9fe_mini.jpg"]
             * sex : m
             * uid : 12083469
             * name : 过千城访万江孑影未成双
             */

            private UBean u;
            private int preuid;
            private String passtime;
            private String voiceuri;
            private int id;

            public int getVoicetime() {
                return voicetime;
            }

            public void setVoicetime(int voicetime) {
                this.voicetime = voicetime;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getCmt_type() {
                return cmt_type;
            }

            public void setCmt_type(String cmt_type) {
                this.cmt_type = cmt_type;
            }

            public int getPrecid() {
                return precid;
            }

            public void setPrecid(int precid) {
                this.precid = precid;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getLike_count() {
                return like_count;
            }

            public void setLike_count(int like_count) {
                this.like_count = like_count;
            }

            public UBean getU() {
                return u;
            }

            public void setU(UBean u) {
                this.u = u;
            }

            public int getPreuid() {
                return preuid;
            }

            public void setPreuid(int preuid) {
                this.preuid = preuid;
            }

            public String getPasstime() {
                return passtime;
            }

            public void setPasstime(String passtime) {
                this.passtime = passtime;
            }

            public String getVoiceuri() {
                return voiceuri;
            }

            public void setVoiceuri(String voiceuri) {
                this.voiceuri = voiceuri;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public static class UBean {
                private String sex;
                private String uid;
                private String name;
                private List<String> header;

                public String getSex() {
                    return sex;
                }

                public void setSex(String sex) {
                    this.sex = sex;
                }

                public String getUid() {
                    return uid;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<String> getHeader() {
                    return header;
                }

                public void setHeader(List<String> header) {
                    this.header = header;
                }

                @Override
                public String toString() {
                    return "UBean{" +
                            "sex='" + sex + '\'' +
                            ", uid='" + uid + '\'' +
                            ", name='" + name + '\'' +
                            ", header=" + header +
                            '}';
                }
            }
        }

        public static class TagsBean {
            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            @Override
            public String toString() {
                return "TagsBean{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        '}';
            }
        }
    }
}
