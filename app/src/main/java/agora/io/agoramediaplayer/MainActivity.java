/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package agora.io.agoramediaplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfoCallback;
import tv.danmaku.ijk.media.player.MediaPlayerKit;
import tv.danmaku.ijk.media.player.MediaPlayerKitEventHandler;
import tv.danmaku.ijk.media.player.mode.AudioTrackInfo;
import tv.danmaku.ijk.media.player.mode.VideoTrackInfo;

/**
 * Main activity -- entry point from Launcher.
 */
public class MainActivity extends Activity {
    private SurfaceView surfaceView1;
    private SurfaceView surfaceView2;
    private Button start_bofang;
    private Button stop_bofang;

    private Button allVideo;

    private Button audio1;
    private Button audio2;
    private Button audio3;
    private Button audio4;
    private Button audio5;
    private Button audio6;
    private Button audio7;

    private MediaPlayerKit ijkMediaPlayer1;
    private MediaPlayerKit ijkMediaPlayer2;
    private String JUstCan1 = "/sdcard/2222.mp4";
    private String JUstCan2 = "/sdcard/3333.mp4";


    private String[] audioUrls = new String[]{
            "http://114.236.93.153:8080/download/audio/BeyondLightYear.mp3",
            "http://114.236.93.153:8080/download/audio/P501_mos_pure.wav",
            "http://114.236.93.153:8080/download/audio/3650_sound.3gp",
            "http://114.236.93.153:8080/download/audio/04.%20David%20Bowie%20-%20Moonage%20Daydream.m4a",
            "http://114.236.93.153:8080/download/audio/MrBean.3gp",
            "http://114.236.93.153:8080/download/audio/Friday.aac",
            "http://114.236.93.153:8080/download/audio/DJ%E9%9F%B3%E6%95%88.ogg"


    };
    private MediaPlayerKit[] ijkMediaAudioPlayers = new MediaPlayerKit[audioUrls.length];

    private ArrayList<MyAudioThread> myAudioThreads= new ArrayList<MyAudioThread>();
    private VideoThread videoThread;
    private VideoThread2 videoThread2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_bofang = (Button)findViewById(R.id.start_view);
        stop_bofang = (Button)findViewById(R.id.stop_view);
        surfaceView1 =(SurfaceView)findViewById(R.id.video_test1);
        surfaceView2 =(SurfaceView)findViewById(R.id.video_test2);
        surfaceView1.getHolder().addCallback(callback);
        surfaceView2.getHolder().addCallback(callback);


        audio1= (Button)findViewById(R.id.start_audio_1);
        audio2= (Button)findViewById(R.id.start_audio_2);
        audio3= (Button)findViewById(R.id.start_audio_3);
        audio4= (Button)findViewById(R.id.start_audio_4);
        audio5= (Button)findViewById(R.id.start_audio_5);
        audio6= (Button)findViewById(R.id.start_audio_6);
        audio7= (Button)findViewById(R.id.start_audio_7);
        allVideo = (Button)findViewById(R.id.start_all_video);
        InitIjk();
        start_bofang.setOnClickListener(onClickListener);
        stop_bofang.setOnClickListener(onClickListener);

        audio1.setOnClickListener(onClickListener);
        audio2.setOnClickListener(onClickListener);
        audio3.setOnClickListener(onClickListener);
        audio4.setOnClickListener(onClickListener);
        audio5.setOnClickListener(onClickListener);
        audio6.setOnClickListener(onClickListener);
        audio7.setOnClickListener(onClickListener);
        allVideo.setOnClickListener(onClickListener);
    }

    private  View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.start_view:
                    new Thread(videoThread).start();
                    new Thread(videoThread2).start();
                    for(int i=0;i<audioUrls.length;i++){
                        new Thread(myAudioThreads.get(i)).start();
                    }
                    break;
                case R.id.stop_view:
                    ijkMediaPlayer1.stop();
                    ijkMediaPlayer2.stop();
                    ijkMediaPlayer1.unload();
                    ijkMediaPlayer2.unload();
                    for(int i=0;i<audioUrls.length;i++){
                        ijkMediaAudioPlayers[i].stop();
                        ijkMediaAudioPlayers[i].unload();
                        myAudioThreads.get(i).isStop = true;
                    }
                    videoThread.isStop = true;
                    videoThread2.isStop = true;
                    break;
                case R.id.start_all_video:
                    if(allVideo.getTag()==null||(boolean)allVideo.getTag()==false){
                        new Thread(videoThread).start();
                        new Thread(videoThread2).start();
                        allVideo.setTag(true);
                        allVideo.setText("停止所有视频");
                    }else{
                        ijkMediaPlayer1.stop();
                        ijkMediaPlayer2.stop();
                        //ijkMediaPlayer1.unload();
                        //ijkMediaPlayer2.unload();
                        videoThread.isStop = true;
                        videoThread2.isStop = true;
                        allVideo.setTag(false);
                        allVideo.setText("开始所有视频");
                    }
                    break;
                case R.id.start_audio_1:
                    if(audio1.getTag()==null||(boolean)audio1.getTag()==false){
                        new Thread(myAudioThreads.get(0)).start();
                        audio1.setTag(true);
                        audio1.setText("停止音频1");
                    }else{
                        ijkMediaAudioPlayers[0].stop();
                        ijkMediaAudioPlayers[0].unload();
                        myAudioThreads.get(0).isStop = true;
                        audio1.setTag(false);
                        audio1.setText("开始音频1");
                    }
                    break;
                case R.id.start_audio_2:
                    if(audio2.getTag()==null||(boolean)(audio2.getTag())==false){
                        new Thread(myAudioThreads.get(1)).start();
                        audio2.setTag(true);
                        audio2.setText("停止音频2");
                    }else{
                        ijkMediaAudioPlayers[1].stop();
                        ijkMediaAudioPlayers[1].unload();
                        myAudioThreads.get(1).isStop = true;
                        audio2.setTag(false);
                        audio2.setText("开始音频2");
                    }
                    break;
                case R.id.start_audio_3:
                    if(audio3.getTag()==null||(boolean)audio3.getTag()==false){
                        new Thread(myAudioThreads.get(2)).start();
                        audio3.setTag(true);
                        audio3.setText("停止音频3");
                    }else{
                        ijkMediaAudioPlayers[2].stop();
                        ijkMediaAudioPlayers[2].unload();
                        myAudioThreads.get(2).isStop = true;
                        audio3.setTag(false);
                        audio3.setText("开始音频3");
                    }
                    break;
                case R.id.start_audio_4:
                    if(audio4.getTag()==null||(boolean)audio4.getTag()==false){
                        new Thread(myAudioThreads.get(3)).start();
                        audio4.setTag(true);
                        audio4.setText("停止音频4");
                    }else{
                        ijkMediaAudioPlayers[3].stop();
                        ijkMediaAudioPlayers[3].unload();
                        myAudioThreads.get(3).isStop = true;
                        audio4.setTag(false);
                        audio4.setText("开始音频4");
                    }
                    break;
                case R.id.start_audio_5:
                    if(audio5.getTag()==null||(boolean)audio5.getTag()==false){
                        new Thread(myAudioThreads.get(4)).start();
                        audio5.setTag(true);
                        audio5.setText("停止音频5");
                    }else{
                        ijkMediaAudioPlayers[4].stop();
                        ijkMediaAudioPlayers[4].unload();
                        myAudioThreads.get(4).isStop = true;
                        audio5.setTag(false);
                        audio5.setText("开始音频5");
                    }
                    break;
                case R.id.start_audio_6:
                    if(audio6.getTag()==null||(boolean)audio6.getTag()==false){
                        new Thread(myAudioThreads.get(5)).start();
                        audio6.setTag(true);
                        audio6.setText("停止音频6");
                    }else{
                        ijkMediaAudioPlayers[5].stop();
                        ijkMediaAudioPlayers[5].unload();
                        myAudioThreads.get(5).isStop = true;
                        audio6.setTag(false);
                        audio6.setText("开始音频6");
                    }
                    break;
                case R.id.start_audio_7:
                    if(audio7.getTag()==null||(boolean)audio7.getTag()==false){
                        new Thread(myAudioThreads.get(6)).start();
                        audio7.setTag(true);
                        audio7.setText("停止音频7");
                    }else{
                        ijkMediaAudioPlayers[6].stop();
                        ijkMediaAudioPlayers[6].unload();
                        myAudioThreads.get(6).isStop = true;
                        audio7.setTag(false);
                        audio7.setText("开始音频7");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void InitIjk(){
        Log.i("TJY","IjkMediaPlayer init");
        IjkMediaPlayer.loadLibrariesOnce(null);
        //IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        //IjkMediaPlayer.native_profileBegin("libijkffmpeg.so");
       // IjkMediaPlayer.native_profileBegin("libijksdl.so");

        ijkMediaPlayer1 = MediaPlayerKit.createMediaPlayerKit();
        ijkMediaPlayer1.setEventHandler(new MediaPlayerKitEventHandler() {
            @Override
            public void onKitError(int errorCode, String errMsg) {
                Log.i("TJY","onKitError:"+errorCode+" "+errMsg);
            }

            @Override
            public void onPlayerStateChanged(int state) {
                Log.i("TJY","onPlayerStateChanged:"+state);
            }
        });

        ijkMediaPlayer1.setMediaInfoCallback(new MediaInfoCallback() {
            @Override
            public void onAudioTrackInfoCallBack(AudioTrackInfo[] audioTrackInfos) {
                Log.i("TJY","onAudioTrackInfoCallBack "+audioTrackInfos.length);
            }

            @Override
            public void onVideoTrackInfoCallBack(VideoTrackInfo[] videoTrackInfos) {
                Log.i("TJY","IjkMediaPlayer init "+videoTrackInfos.length);
            }
        });

        ijkMediaPlayer2 = MediaPlayerKit.createMediaPlayerKit();
        for(int i=0;i<audioUrls.length;i++){
            ijkMediaAudioPlayers[i] = MediaPlayerKit.createMediaPlayerKit();
        }
        for(int i=0;i<audioUrls.length;i++){
            myAudioThreads.add(new MyAudioThread(ijkMediaAudioPlayers[i],audioUrls[i]));
        }
        videoThread = new VideoThread();
        videoThread2 = new VideoThread2();
        Log.i("TJY","IjkMediaPlayer over");
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.i("TJY","surfaceCreated "+surfaceHolder);
            //ijkMediaPlayer.setDisplay(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };


    private class VideoThread implements Runnable{
        public boolean isStop = true;
        public VideoThread(){
        }
        @Override
        public void run() {
            isStop = false;
            ijkMediaPlayer1.load(JUstCan1,true);
            ijkMediaPlayer1.setVideoView(surfaceView1.getHolder().getSurface());  //将视频画面输出到surface上
            ijkMediaPlayer1.play();                                //开始播放
        }
    }

    private class VideoThread2 implements Runnable{
        public boolean isStop = true;
        public VideoThread2(){
        }
        @Override
        public void run() {
            isStop = false;
            ijkMediaPlayer2.load(JUstCan2,true);
            ijkMediaPlayer2.setVideoView(surfaceView2.getHolder().getSurface());  //将视频画面输出到surface上
            ijkMediaPlayer2.play();                                //开始播放

        }
    }

    private class MyAudioThread implements Runnable{
        private MediaPlayerKit mediaPlayerKit = null;
        private String path = null;
        public boolean isStop = true;
        public MyAudioThread(MediaPlayerKit mediaPlayerKit, String path){
            this.mediaPlayerKit = mediaPlayerKit;
            this.path = path;
        }

        @Override
        public void run() {
            Log.i("TJY","MyAudioThread: path"+path);
            isStop = false;
            this.mediaPlayerKit.load(path,true);
            this.mediaPlayerKit.play();                                //开始播放
        }
    }
}

