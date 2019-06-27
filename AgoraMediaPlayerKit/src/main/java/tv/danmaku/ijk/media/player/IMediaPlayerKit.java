package tv.danmaku.ijk.media.player;

import android.view.Surface;

import java.io.IOException;

/**
 * Created by yong on 2019/6/26.
 */

public interface IMediaPlayerKit {

    void setVideoView(Surface surface);

    void load(String source, boolean auto_play) throws IOException;

    long getDuration();

    void play();

    void pause();

    void resume();

    long getCurrentPosition();

    void seekTo(int msec);

    void adjustPlaybackSignalVolume(int volume); // from 0 to 400, 100 stands for original

    void stop();

    void unload();

    void destroy();

    int getState();
}
