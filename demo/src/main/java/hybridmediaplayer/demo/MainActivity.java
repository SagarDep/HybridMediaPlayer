package hybridmediaplayer.demo;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.google.android.exoplayer2.Player;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import hybridmediaplayer.ExoMediaPlayer;
import hybridmediaplayer.HybridMediaPlayer;
import hybridmediaplayer.MediaSourceInfo;
import hybridplayer.demo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ExoMediaPlayer mediaPlayer;
    private boolean isPrepared;
    private int time;
    float speed = 1;
    private SurfaceView playerView;


    //Chromecast
    private CastContext castContext;
    private MediaRouteButton mediaRouteButton;
    private List<MediaSourceInfo> sources;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btPlay = findViewById(R.id.btPlay);
        Button btPause = findViewById(R.id.btPause);
        Button btFastForward = findViewById(R.id.fastForward);
        Button btSpeed = findViewById(R.id.btSpeed);
        Button btStop = findViewById(R.id.btStop);
        Button btNext = findViewById(R.id.btNext);
        Button btCreatePlayer = findViewById(R.id.btCreatePlayer);


        btPlay.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btFastForward.setOnClickListener(this);
        btSpeed.setOnClickListener(this);
        btStop.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btCreatePlayer.setOnClickListener(this);

        playerView = findViewById(R.id.playerView);

        //Chromecast:
        mediaRouteButton = findViewById(R.id.media_route_button);

        castContext = CastContext.getSharedInstance(this);
        castContext.addCastStateListener(new CastStateListener() {
            @Override
            public void onCastStateChanged(int state) {
                if (state == CastState.NO_DEVICES_AVAILABLE)
                    mediaRouteButton.setVisibility(View.GONE);
                else {
                    if (mediaRouteButton.getVisibility() == View.GONE)
                        mediaRouteButton.setVisibility(View.VISIBLE);
                }
            }
        });

        CastButtonFactory.setUpMediaRouteButton(this, mediaRouteButton);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.release();
    }

    private void createPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        String url = "https://play.podtrac.com/npr-510289/npr.mc.tritondigital.com/NPR_510289/media/anon.npr-mp3/npr/pmoney/2017/03/20170322_pmoney_20170322_pmoney_pmpod.mp3";
        String url2 = "http://217.74.72.11/RADIOFONIA";
        String url3 = "https://github.com/mediaelement/mediaelement-files/blob/master/big_buck_bunny.mp4?raw=true";
        mediaPlayer = new ExoMediaPlayer(this, castContext, 0);
        //mediaPlayer.setDataSource(url);
        MediaSourceInfo source1 = new MediaSourceInfo.Builder().setUrl(url)
                .setTitle("Podcast 0")
                .setImageUrl("https://cdn.dribbble.com/users/20781/screenshots/573506/podcast_logo.jpg")
                .build();
        MediaSourceInfo source3 = new MediaSourceInfo.Builder().setUrl("http://rss.art19.com/episodes/d93a35f0-e171-4a92-887b-35cee645f835.mp3")
                .setTitle("Podcast 1")
                .setImageUrl("https://cdn.dribbble.com/users/20781/screenshots/573506/podcast_logo.jpg")
                .build();
        MediaSourceInfo source4 = new MediaSourceInfo.Builder().setUrl("http://rss.art19.com/episodes/d93a35f0-e171-4a92-887b-35cee645f835.mp3") //http://stream3.polskieradio.pl:8904/;
                .setTitle("Podcast 2")
                .setImageUrl("https://cdn.dribbble.com/users/20781/screenshots/573506/podcast_logo.jpg")
                .build();
        MediaSourceInfo source2 = new MediaSourceInfo.Builder().setUrl(url2)
                .setTitle("Movie")
                .setImageUrl("http://www.pvhc.net/img29/amkulkkbogfvmihgspru.png")
                .isVideo(true)
                .build();

        sources = new ArrayList<>();
//        sources.add(source1);
        sources.add(source2);
        sources.add(source4);
//        sources.add(source2);
        mediaPlayer.setPlayerView(this, playerView);
        mediaPlayer.setSupportingSystemEqualizer(true);
        mediaPlayer.setOnTrackChangedListener(new ExoMediaPlayer.OnTrackChangedListener() {
            @Override
            public void onTrackChanged(boolean isFinished) {
                KLog.w("abc isFinished " + isFinished + " " + mediaPlayer.getDuration() + " window = " + mediaPlayer.getCurrentWindow());
            }
        });



        mediaPlayer.setOnPreparedListener(new HybridMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(HybridMediaPlayer player) {
                KLog.w(mediaPlayer.hasVideo());
                KLog.d("ppp " + mediaPlayer.getCurrentPlayer());
            }
        });

        mediaPlayer.setOnPlayerStateChanged(new ExoMediaPlayer.OnPlayerStateChanged() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                KLog.d("cvv " + playbackState);
                if(mediaPlayer.isCasting() && playbackState == Player.STATE_IDLE)
                    mediaPlayer.pause();
            }
        });

        mediaPlayer.setOnCompletionListener(new HybridMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(HybridMediaPlayer player) {
                KLog.i("cvv complete");

            }
        });

        mediaPlayer.setOnLoadingChanged(new ExoMediaPlayer.OnLoadingChanged() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                KLog.d("loadd "+isLoading);
            }
        });

        mediaPlayer.setInitialWindowNum(0);
        mediaPlayer.setDataSource(sources, sources);
        mediaPlayer.setOnAudioSessionIdSetListener(new ExoMediaPlayer.OnAudioSessionIdSetListener() {
            @Override
            public void onAudioSessionIdset(int audioSessionId) {
                KLog.d("audio session id = "+audioSessionId);
            }
        });
        mediaPlayer.prepare();
        mediaPlayer.play();

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btPlay) {
            if (mediaPlayer == null)
                createPlayer();
            else {
                mediaPlayer.play();
            }
        } else if (view.getId() == R.id.btPause) {
            mediaPlayer.pause();
            KLog.d(mediaPlayer.getCurrentPosition());
            KLog.i(mediaPlayer.getDuration());
        } else if (view.getId() == R.id.fastForward) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 2000);
        } else if (view.getId() == R.id.btSpeed) {
//            if (speed == 1)
//                speed = 2f;
//            else speed = 1;
//            mediaPlayer.setPlaybackParams(speed, 1);


//            int msec = mediaPlayer.getCurrentPosition() - 2000;
//            if(msec<0)
//                msec = 1;
//            mediaPlayer.seekTo(msec);

            loadOtherSources();
        } else if (view.getId() == R.id.btStop) {
            mediaPlayer.release();
            mediaPlayer = null;
        } else if (view.getId() == R.id.btNext) {
//            pm.selectQueueItem(pm.getCurrentItemIndex()+1);
            KLog.i("abc "+ mediaPlayer.getCurrentWindow() + " / "+mediaPlayer.getWindowCount());
            mediaPlayer.seekTo((mediaPlayer.getCurrentWindow() + 1) % mediaPlayer.getWindowCount(), 0);
        } else if (view.getId() == R.id.btCreatePlayer) {
//            pm = PlayerManager.createPlayerManager(new PlayerManager.QueuePositionListener() {
//                @Override
//                public void onQueuePositionChanged(int previousIndex, int newIndex) {
//
//                }
//            }, this, castContext);
//            MediaSourceInfo source2 = new MediaSourceInfo.Builder().setUrl("http://rss.art19.com/episodes/d93a35f0-e171-4a92-887b-35cee645f835.mp3")
//                    .setTitle("Movie")
//                    .setImageUrl("http://www.pvhc.net/img29/amkulkkbogfvmihgspru.png")
//                    .build();
//            MediaSourceInfo source3 = new MediaSourceInfo.Builder().setUrl("http://rss.art19.com/episodes/d93a35f0-e171-4a92-887b-35cee645f835.mp3")
//                    .setTitle("Source 3")
//                    .setImageUrl("http://www.pvhc.net/img29/amkulkkbogfvmihgspru.png")
//                    .build();
//            pm.addItem(source2);
//            pm.addItem(source2);
//            pm.selectQueueItem(0);

            createPlayer();

        }
    }

    private void loadOtherSources() {
        List<MediaSourceInfo> sources2 = new ArrayList<>();
        MediaSourceInfo source = new MediaSourceInfo.Builder().setUrl("http://api.spreaker.com/download/episode/14404535/dlaczego_rezygnujemy.mp3")
                .setTitle("NEW Podcast")
                .setImageUrl("https://github.com/mkaflowski/HybridMediaPlayer/blob/master/images/cover.jpg?raw=true")
                .build();
        MediaSourceInfo source2 = new MediaSourceInfo.Builder().setUrl("http://api.spreaker.com/download/episode/14404535/dlaczego_rezygnujemy.mp3")
                .setTitle("NEW Podcast 2")
                .setImageUrl("https://github.com/mkaflowski/HybridMediaPlayer/blob/master/images/cover.jpg?raw=true")
                .build();
        sources2.add(source);
        sources2.add(source2);
        sources2.add(source2);
        mediaPlayer.setInitialWindowNum(2);
        mediaPlayer.setDataSource(sources2, sources2);
        mediaPlayer.prepare();
        mediaPlayer.seekTo(10000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
        return true;
    }


}