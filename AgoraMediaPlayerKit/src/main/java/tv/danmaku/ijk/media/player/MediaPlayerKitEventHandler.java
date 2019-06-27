package tv.danmaku.ijk.media.player;

/**
 * Created by yong on 2019/6/26.
 */

public interface MediaPlayerKitEventHandler {
    void onKitError(int errorCode, String errMsg); // Unrecoverable
    void onPlayerStateChanged(int state); // UNINITIALIZED, READY, PLAY, BUFFERING, STOP, PAUSE, COMPLETE


}
