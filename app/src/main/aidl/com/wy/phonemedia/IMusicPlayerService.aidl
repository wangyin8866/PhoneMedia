// IMusicPlayerService.aidl
package com.wy.phonemedia;

// Declare any non-default types here with import statements

interface IMusicPlayerService {
    /**
        * 根据位置打开播放器
        * @param position
        */
        void openAudio(int position);

       /**
        * 开始播放
        */
        void start();
       /**
        * 暂停播放
        */
        void pause();
       /**
        * 停止播放
        */
        void stop();

       /**
        * 得到当前的播放进度
        * @return
        */
         int getCurrentDuration();
       /**
        * 得到当总的播放时长
        * @return
        */
         int getDuration();

       /**
        * 得到歌手
        * @return
        */
        String getArtist();
       /**
        * 得到歌曲名称
        * @return
        */
        String getName();
       /**
        * 得到歌曲路径
        * @return
        */
        String getAudioPath();

       /**
        * 播放下一个
        */
        void next();
       /**
        * 播放下一个
        */
        void previous();

       /**
        * 设置播放模式
        * @param palyMode
        */
        void setPlayMode(int palyMode);
       /**
        * 得到播放模式
        */
        int getPlayMode();
       /**
         * 是否正在播放音频
         */
        boolean isPlaying();
       /**
         * 拖动进度条
         */
         void seekTo(int position);

         int getAudioSessionId();
}
