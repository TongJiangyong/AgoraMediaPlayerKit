package tv.danmaku.ijk.media.player.mode;

/**
 * Created by yong on 2019/6/27.
 */

public class VideoTrackInfo {
    private int index;
    private String codecType;
    private String bitrate;
    private String resolution;
    private String fps;

    public VideoTrackInfo(int index, String codecType, String bitrate, String resolution, String fps) {
        this.index = index;
        this.codecType = codecType;
        this.bitrate = bitrate;
        this.resolution = resolution;
        this.fps = fps;
    }

    public int getIndex() {
        return index;
    }

    public String getCodecType() {
        return codecType;
    }

    public String getBitrate() {
        return bitrate;
    }

    public String getResolution() {
        return resolution;
    }

    public String getFps() {
        return fps;
    }


}
