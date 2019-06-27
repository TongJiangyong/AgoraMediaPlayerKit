package tv.danmaku.ijk.media.player;

/**
 * Created by yong on 2019/6/26.
 */

public class Constants {


    public static enum AgoraKitError {
        AG_ERROR_PLAYER_UNKNOWN(1),
        AG_ERROR_SERVER_DIED(100),

        AG_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK(200),

        AG_ERROR_IO( -1004),

        AG_ERROR_MALFORMED( -1007),

        AG_ERROR_UNSUPPORTED( -1010),

        AG_ERROR_TIMED_OUT( -110);
        private int value;

        private AgoraKitError(int v) {
            this.value = v;
        }

        public static int getValue(Constants.AgoraKitError type) {
            return type.value;
        }
    }

    public static String getPlayerError(int what) {
        if(what == AgoraKitError.AG_ERROR_PLAYER_UNKNOWN.value){
            return "player internal error";
        }else if(what == AgoraKitError.AG_ERROR_SERVER_DIED.value){
            return "player server died";
        }else if(what == AgoraKitError.AG_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK.value){
            return "player not vaild for progressive playback";
        }else if(what == AgoraKitError.AG_ERROR_IO.value){
            return "player io error";
        }else if(what == AgoraKitError.AG_ERROR_MALFORMED.value){
            return "player malformed error";
        }else if(what == AgoraKitError.AG_ERROR_UNSUPPORTED.value){
            return "player unsupported error";
        }else if(what == AgoraKitError.AG_ERROR_TIMED_OUT.value){
            return "player time out error";
        }else{
            return "player unknown error";
        }

    }

    public static enum AgoraKitErrorType {
        AG_OK,
        AG_PLAYER_ERROR,
        AG_PROCESS_ERROR,
    }

    public static enum AgoraMediaPlayerState {
        AG_MEDIA_PLAYER_STATE_UNINITIALIZED,
        AG_MEDIA_PLAYER_STATE_READY,
        AG_MEDIA_PLAYER_STATE_PLAY,
        AG_MEDIA_PLAYER_STATE_PAUSE,
        AG_MEDIA_PLAYER_STATE_STOP,
        AG_MEDIA_PLAYER_STATE_BUFFERING,
        AG_MEDIA_PLAYER_STATE_SEEKING_COMPLETE,
        AG_MEDIA_PLAYER_STATE_COMPLETE,
        AG_MEDIA_PLAYER_STATE_ERROR,
    }

    ;

    public static float DEFAULT_VOLUME = 0.25F;
    public static float VOLUME_LEVEL = 400F;

    public static final int META_DATA_TYPE_VIDEO = 1;
    public static final int META_DATA_TYPE_AUDIO = 2;

    public static final int ADAPT_DOWN_BANDWIDTH = 2;
    public static final int LASTMILE_PROBE_RESULT_COMPLETE = 1;
    public static final int LASTMILE_PROBE_RESULT_INCOMPLETE_NO_BWE = 2;
    public static final int LASTMILE_PROBE_RESULT_UNAVAILABLE = 3;
    public static final int QUALITY_UNKNOWN = 0;
    public static final int QUALITY_EXCELLENT = 1;
    public static final int QUALITY_GOOD = 2;
    public static final int QUALITY_POOR = 3;
    public static final int QUALITY_BAD = 4;
    public static final int QUALITY_VBAD = 5;
    public static final int QUALITY_DOWN = 6;


}
