package tv.danmaku.ijk.media.player;

import tv.danmaku.ijk.media.player.mode.AudioTrackInfo;
import tv.danmaku.ijk.media.player.mode.VideoTrackInfo;

/**
 * Created by yong on 2019/6/26.
 */

public interface MediaInfoCallback {
    void onAudioTrackInfoCallBack(AudioTrackInfo[] audioTrackInfos);
    void onVideoTrackInfoCallBack(VideoTrackInfo[] videoTrackInfos);
}
