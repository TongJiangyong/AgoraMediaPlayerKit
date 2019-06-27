package tv.danmaku.ijk.media.player.mode;

/**
 * Created by yong on 2019/6/27.
 */

public class AudioTrackInfo {
    private int index;
    private String codecType;
    private String bitrate;
    private String sampleRate;
    private String channels;

    public AudioTrackInfo(int index, String codecType, String bitrate, String sampleRate, String channels) {
        this.index = index;
        this.codecType = codecType;
        this.bitrate = bitrate;
        this.sampleRate = sampleRate;
        this.channels = channels;
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

    public String getSampleRate() {
        return sampleRate;
    }

    public String getChannels() {
        return channels;
    }
}
