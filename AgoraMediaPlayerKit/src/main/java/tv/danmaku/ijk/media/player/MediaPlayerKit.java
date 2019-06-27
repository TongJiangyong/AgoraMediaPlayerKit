package tv.danmaku.ijk.media.player;

import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;
import tv.danmaku.ijk.media.player.mode.AudioTrackInfo;
import tv.danmaku.ijk.media.player.mode.VideoTrackInfo;

import static tv.danmaku.ijk.media.player.Constants.*;

/**
 * Created by yong on 2019/6/26.
 */

public class MediaPlayerKit implements IMediaPlayerKit {
    private static final String TAG = MediaPlayerKit.class.getName();
    private IMediaPlayer mediaPlayer = null;
    private final ConcurrentHashMap<MediaInfoCallback, Integer> mMediaInfoCallbackHander = new ConcurrentHashMap();
    private final ConcurrentHashMap<MediaPlayerKitEventHandler, Integer> mMediaPlayerKitEventHandler = new ConcurrentHashMap();
    private AgoraMediaPlayerState mCurrentState = null;
    private static boolean DEBUG_FLAG = true;
    private String instanceName = null;

    static {
        IjkMediaPlayer.loadLibrariesOnce(null);
    }

    private MediaPlayerKit() {
        mediaPlayer = new IjkMediaPlayer();
        instanceName = mediaPlayer.toString();
        mediaPlayer.setOnPreparedListener(mPreparedListener);
        mediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
        mediaPlayer.setOnCompletionListener(mCompletionListener);
        mediaPlayer.setOnErrorListener(mErrorListener);
        mediaPlayer.setOnInfoListener(mInfoListener);
        mediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
        mediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
        mediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
        mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_UNINITIALIZED;
    }


    public static synchronized MediaPlayerKit createMediaPlayerKit() {
        return new MediaPlayerKit();
    }

    public static void enableDebugLog(boolean enable) {
        DEBUG_FLAG = enable;
    }


    public void setMediaInfoCallback(MediaInfoCallback callback) {
        mMediaInfoCallbackHander.put(callback, 0);
    }


    public void setEventHandler(MediaPlayerKitEventHandler handler) {
        mMediaPlayerKitEventHandler.put(handler, 0);
    }


    @Override
    public void setVideoView(Surface surface) {
        mediaPlayer.setSurface(surface);
    }

    @Override
    public void load(String source, boolean auto_play) {
        try {
            try {
                mediaPlayer.setDataSource(source);
            } catch (IOException e) {
                Log.i(TAG, instanceName + " MediaPlayerKit load error " + e);
                onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_ERROR, AgoraKitErrorType.AG_PROCESS_ERROR, "load process error");
            }
            mediaPlayer.prepareAsync();
            if (auto_play) {
                mediaPlayer.setVolume(DEFAULT_VOLUME, DEFAULT_VOLUME);
            } else {
                mediaPlayer.setVolume(0F, 0F);
            }
            mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_BUFFERING;
        } catch (Exception e) {
            Log.e(TAG, instanceName + " MediaPlayerKit prepareAsync error " + e);
            onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_ERROR, AgoraKitErrorType.AG_PROCESS_ERROR, "prepare process error");
        }
    }

    @Override
    public long getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

    }

    @Override
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_PAUSE;
        }
    }

    @Override
    public void resume() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_PLAY;
        }
    }

    @Override
    public long getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
        mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_BUFFERING;
    }

    @Override
    public void adjustPlaybackSignalVolume(int volume) {
        float tempVolume = (Float.valueOf(volume) / VOLUME_LEVEL);
        if (tempVolume > 1.0F) {
            tempVolume = 1.0F;
        }
        mediaPlayer.setVolume(tempVolume, tempVolume);
    }

    @Override
    public void stop() {
        if (DEBUG_FLAG) {
            Log.i(TAG, instanceName + " MediaPlayerKit stop " + android.os.Process.myTid());
        }
        mediaPlayer.stop();
        mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_STOP;
    }


    @Override
    public void unload() {
        mediaPlayer.reset();
    }

    @Override
    public void destroy() {
        mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_UNINITIALIZED;
        mediaPlayer.setOnPreparedListener(null);
        mediaPlayer.setOnVideoSizeChangedListener(null);
        mediaPlayer.setOnCompletionListener(null);
        mediaPlayer.setOnErrorListener(null);
        mediaPlayer.setOnInfoListener(null);
        mediaPlayer.setOnBufferingUpdateListener(null);
        mediaPlayer.setOnSeekCompleteListener(null);
        mediaPlayer.setOnTimedTextListener(null);
        mediaPlayer.release();
        mMediaInfoCallbackHander.clear();
        mMediaPlayerKitEventHandler.clear();
        instanceName = null;
    }

    @Override
    public int getState() {
        return mCurrentState.ordinal();
    }


    private void getTrackInfo() {
        try {
            mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_PLAY;
            ITrackInfo[] trackInfo = mediaPlayer.getTrackInfo();
            ArrayList<ITrackInfo> audioTrackInfo = new ArrayList<>();
            ArrayList<ITrackInfo> videoTrackInfo = new ArrayList<>();
            if (DEBUG_FLAG) {
                Log.i(TAG, instanceName + " MediaPlayerKit getTrackInfo " + android.os.Process.myTid()
                        + " " + trackInfo.length);
            }
            for (int i = 0; i < trackInfo.length; i++) {
                switch (trackInfo[i].getTrackType()) {
                    case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                        audioTrackInfo.add(trackInfo[i]);
                        break;
                    case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                        videoTrackInfo.add(trackInfo[i]);
                        break;
                }
                if (DEBUG_FLAG) {
                    Log.i(TAG, instanceName + " MediaPlayerKit TrackInfo " + android.os.Process.myTid()
                            + " " + trackInfo[i].toString());
                }
            }
            onMediaInfoCallbackHanderEvent(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO, audioTrackInfo.toArray());
            onMediaInfoCallbackHanderEvent(ITrackInfo.MEDIA_TRACK_TYPE_VIDEO, videoTrackInfo.toArray());
        } catch (Exception e) {
            onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_ERROR, AgoraKitErrorType.AG_PROCESS_ERROR, "paly process error");
        }
    }


    //callback

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            if (DEBUG_FLAG) {
                Log.i(TAG, instanceName + " MediaPlayerKit onPrepared " + android.os.Process.myTid());
            }
            getTrackInfo();
            onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_READY, AgoraKitErrorType.AG_OK, null);
        }
    };

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {

        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            if (DEBUG_FLAG) {
                Log.i(TAG, instanceName + " MediaPlayerKit onVideoSizeChanged " + android.os.Process.myTid()
                        + " " + width + " " + height + " " + sar_num + " " + sar_den);
            }
        }
    };

    IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            if (DEBUG_FLAG) {
                Log.i(TAG, instanceName + " MediaPlayerKit onCompletion " + android.os.Process.myTid());
            }
            onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_COMPLETE, AgoraKitErrorType.AG_OK, null);
        }
    };

    IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            if (DEBUG_FLAG) {
                Log.i(TAG, instanceName + " MediaPlayerKit onBufferingUpdate " + android.os.Process.myTid()
                        + " " + percent);
            }
            //onMediaInfoCallbackHanderEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_BUFFERING,null);
        }
    };

    IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if (DEBUG_FLAG) {
                Log.i(TAG, instanceName + " MediaPlayerKit onSeekComplete " + android.os.Process.myTid());
            }
            onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_SEEKING_COMPLETE, AgoraKitErrorType.AG_OK, null);
            mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_PLAY;
        }
    };


    IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if (DEBUG_FLAG) {
                Log.i(TAG, instanceName + " MediaPlayerKit onError " + android.os.Process.myTid()
                        + " " + what + " " + extra);
            }
            mCurrentState = AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_ERROR;
            onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_ERROR, AgoraKitErrorType.AG_PLAYER_ERROR, new int[]{what, extra});
            return true;

        }
    };

    IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if (DEBUG_FLAG) {
                Log.i(TAG, instanceName + " MediaPlayerKit onInfo " + android.os.Process.myTid()
                        + " " + what + " " + extra);
            }
            return true;
        }
    };

    IMediaPlayer.OnTimedTextListener mOnTimedTextListener = new IMediaPlayer.OnTimedTextListener() {

        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            if (DEBUG_FLAG) {
                Log.e(TAG, instanceName + " MediaPlayerKit onTimedText " + android.os.Process.myTid()
                        + " " + text.getText() + " " + text.getBounds());
            }
        }
    };


    private void onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState eventCode, AgoraKitErrorType errorType, Object errorInfo) {
        try {
            Iterator it = this.mMediaPlayerKitEventHandler.keySet().iterator();

            while (it.hasNext()) {
                MediaPlayerKitEventHandler m = (MediaPlayerKitEventHandler) it.next();
                if (m == null) {
                    it.remove();
                } else {
                    dealWithMediaPlayerKitEventCallback(m, eventCode, errorType, errorInfo);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onMediaPlayerKitEventHandlerEvent: " + e.toString());
        }

    }


    private void onMediaInfoCallbackHanderEvent(int eventCode, Object errorInfo) {
        try {
            Iterator it = this.mMediaInfoCallbackHander.keySet().iterator();

            while (it.hasNext()) {
                MediaInfoCallback m = (MediaInfoCallback) it.next();
                if (m == null) {
                    it.remove();
                } else {
                    dealWithMediaInfoCallback(m, eventCode, errorInfo);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, instanceName + " onMediaInfoCallbackHanderEvent: " + e.toString());
            onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_ERROR, AgoraKitErrorType.AG_PROCESS_ERROR, "media track callback error");
        }

    }

    private void dealWithMediaPlayerKitEventCallback(MediaPlayerKitEventHandler handler, AgoraMediaPlayerState state, AgoraKitErrorType errorType, Object eventData) {
        if (DEBUG_FLAG) {
            Log.i(TAG, "MediaPlayerKit dealWithMediaPlayerKitEventCallback " + android.os.Process.myTid()
                    + " " + state + " " + errorType + " " + handler);
        }
        if (handler != null) {
            switch (state) {
                case AG_MEDIA_PLAYER_STATE_READY:
                case AG_MEDIA_PLAYER_STATE_COMPLETE:
                case AG_MEDIA_PLAYER_STATE_SEEKING_COMPLETE:
                    if (DEBUG_FLAG) {
                        Log.i(TAG, "MediaPlayerKit AG_MEDIA_PLAYER_STATE " + android.os.Process.myTid()
                                + " " + state.ordinal());
                    }
                    handler.onPlayerStateChanged(state.ordinal());
                    break;
                case AG_MEDIA_PLAYER_STATE_ERROR:
                    switch (errorType) {
                        case AG_PLAYER_ERROR:
                            int what = ((int[]) eventData)[0];
                            int extra = ((int[]) eventData)[0];
                            handler.onKitError(what, Constants.getPlayerError(what));
                            break;
                        case AG_PROCESS_ERROR:
                            handler.onKitError(state.ordinal(), (String) eventData);
                            break;
                    }
                    break;
                default:
                    Log.e(TAG, "dealWithMediaPlayerKitEventCallback error state");
            }
        }
    }

    private void dealWithMediaInfoCallback(MediaInfoCallback mediaInfoCallback, int eventCode, Object eventData) {
        if (DEBUG_FLAG) {
            Log.i(TAG, "MediaPlayerKit dealWithMediaInfoCallback " + android.os.Process.myTid()
                    + " " + eventCode + " " + mediaInfoCallback);
        }
        if (mediaInfoCallback != null) {
            switch (eventCode) {
                case META_DATA_TYPE_VIDEO:
                    if (((Object[]) eventData).length == 0) {
                        mediaInfoCallback.onVideoTrackInfoCallBack(null);
                    } else {
                        VideoTrackInfo[] videoTrackInfos = new VideoTrackInfo[((Object[]) eventData).length];
                        for (int i = 0; i < ((Object[]) eventData).length; i++) {
                            IjkTrackInfo videoTrackInfo = (IjkTrackInfo)((Object[]) eventData)[0];
                            videoTrackInfos[i] = new VideoTrackInfo(
                                    videoTrackInfo.getIndex(),
                                    videoTrackInfo.getCodecType(),
                                    videoTrackInfo.getBitrateInline(),
                                    videoTrackInfo.getResolutionInline(),
                                    videoTrackInfo.getFpsInline());
                        }
                        mediaInfoCallback.onVideoTrackInfoCallBack(videoTrackInfos);
                    }
                    break;
                case META_DATA_TYPE_AUDIO:
                    if (((Object[]) eventData).length == 0) {
                        mediaInfoCallback.onAudioTrackInfoCallBack(null);
                    } else {
                        AudioTrackInfo[] audioTrackInfos = new AudioTrackInfo[((Object[]) eventData).length];
                        for (int i = 0; i < ((Object[]) eventData).length; i++) {
                            IjkTrackInfo audioTrackInfo = (IjkTrackInfo)((Object[]) eventData)[0];
                            audioTrackInfos[i] = new AudioTrackInfo(
                                    audioTrackInfo.getIndex(),
                                    audioTrackInfo.getCodecType(),
                                    audioTrackInfo.getBitrateInline(),
                                    audioTrackInfo.getSampleRateInline(),
                                    audioTrackInfo.getChannelLayoutInline());
                        }
                        mediaInfoCallback.onAudioTrackInfoCallBack(audioTrackInfos);
                    }
                    break;
                default:
                    onMediaPlayerKitEventHandlerEvent(AgoraMediaPlayerState.AG_MEDIA_PLAYER_STATE_ERROR, AgoraKitErrorType.AG_PROCESS_ERROR, "media track info deal error");
                    break;
            }
        }
    }
}
