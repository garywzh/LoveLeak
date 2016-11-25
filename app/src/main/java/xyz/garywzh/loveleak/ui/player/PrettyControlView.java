package xyz.garywzh.loveleak.ui.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;

import java.util.Formatter;
import java.util.Locale;

import xyz.garywzh.loveleak.R;

/**
 * Created by garywzh on 2016/9/15.
 */
public class PrettyControlView extends RelativeLayout {

    public static final int DEFAULT_SHOW_DURATION_MS = 3000;
    private static final int PROGRESS_BAR_MAX = 1000;

    private final ComponentListener componentListener;
    private final ImageButton playButton;
    private final TextView time;
    private final TextView timeCurrent;
    private final ImageButton fullscreen;
    private final SeekBar progressBar;

    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Timeline.Window currentWindow;

    private SimpleExoPlayer player;
    private FullscreenClickListener listener;

    private boolean dragging;
    private int showDurationMs = DEFAULT_SHOW_DURATION_MS;

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private final Runnable hideAction = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public PrettyControlView(Context context) {
        this(context, null);
    }

    public PrettyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrettyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        currentWindow = new Timeline.Window();
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        componentListener = new ComponentListener();

        LayoutInflater.from(context).inflate(R.layout.view_media_controller, this);
        time = (TextView) findViewById(R.id.time);
        timeCurrent = (TextView) findViewById(R.id.time_current);
        progressBar = (SeekBar) findViewById(R.id.mediacontroller_progress);
        progressBar.setOnSeekBarChangeListener(componentListener);
        progressBar.setMax(PROGRESS_BAR_MAX);
        playButton = (ImageButton) findViewById(R.id.play);
        playButton.setOnClickListener(componentListener);
        fullscreen = (ImageButton) findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(componentListener);
        updateAll();
    }

    /**
     * Sets the {@link ExoPlayer} to control.
     *
     * @param player the {@code ExoPlayer} to control.
     */
    public void setPlayer(SimpleExoPlayer player) {
        if (this.player != null) {
            this.player.removeListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);
        }
        updateAll();
    }

    public void setFullscreenToggleListener(FullscreenClickListener listener) {
        this.listener = listener;
    }

    public void show() {
        setVisibility(VISIBLE);
        updateAll();
        hideDeferredIfPlaying();
    }

    /**
     * Hides the controller.
     */
    public void hide() {
        setVisibility(GONE);
        removeCallbacks(updateProgressAction);
        removeCallbacks(hideAction);
    }

    /**
     * Returns whether the controller is currently visible.
     */
    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    private void hideDeferredIfPlaying() {
        removeCallbacks(hideAction);
        if (player.getPlaybackState() == ExoPlayer.STATE_READY && player.getPlayWhenReady()) {
            postDelayed(hideAction, showDurationMs);
        }
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateProgress();
    }

    private void updatePlayPauseButton() {
        if (!isVisible()) {
            return;
        }
        boolean playing = player != null && player.getPlayWhenReady();
        playButton.setImageResource(playing ? com.google.android.exoplayer2.R.drawable.exo_controls_pause : com.google.android.exoplayer2.R.drawable.exo_controls_play);
        playButton.setContentDescription(
                getResources().getString(playing ? com.google.android.exoplayer2.R.string.exo_controls_pause_description : com.google.android.exoplayer2.R.string.exo_controls_play_description));
    }

    private void updateNavigation() {
        if (!isVisible()) {
            return;
        }
        Timeline currentTimeline = player != null ? player.getCurrentTimeline() : null;
        boolean haveTimeline = currentTimeline != null;
        boolean isSeekable = false;
        if (haveTimeline) {
            int currentWindowIndex = player.getCurrentWindowIndex();
            currentTimeline.getWindow(currentWindowIndex, currentWindow);
            isSeekable = currentWindow.isSeekable;
        }
        progressBar.setEnabled(isSeekable);
    }

    private void updateProgress() {
        if (!isVisible()) {
            return;
        }
        long duration = player == null ? 0 : player.getDuration();
        long position = player == null ? 0 : player.getCurrentPosition();
        time.setText(stringForTime(duration));
        if (!dragging) {
            timeCurrent.setText(stringForTime(position));
        }
        if (!dragging) {
            progressBar.setProgress(progressBarValue(position));
        }
        long bufferedPosition = player == null ? 0 : player.getBufferedPosition();
        progressBar.setSecondaryProgress(progressBarValue(bufferedPosition));
        // Remove scheduled updates.
        removeCallbacks(updateProgressAction);
        // Schedule an update if necessary.
        int playbackState = player == null ? ExoPlayer.STATE_IDLE : player.getPlaybackState();
        if (playbackState != ExoPlayer.STATE_IDLE && playbackState != ExoPlayer.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == ExoPlayer.STATE_READY) {
                delayMs = 1000 - (position % 1000);
                if (delayMs < 200) {
                    delayMs += 1000;
                }
            } else {
                delayMs = 1000;
            }
            postDelayed(updateProgressAction, delayMs);
        }
    }

    private String stringForTime(long timeMs) {
        if (timeMs == C.TIME_UNSET) {
            timeMs = 0;
        }
        long totalSeconds = (timeMs + 500) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        formatBuilder.setLength(0);
        return hours > 0 ? formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
                : formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    private int progressBarValue(long position) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        return duration == C.TIME_UNSET || duration == 0 ? 0
                : (int) ((position * PROGRESS_BAR_MAX) / duration);
    }

    private long positionValue(int progress) {
        long duration = player == null ? C.TIME_UNSET : player.getDuration();
        return duration == C.TIME_UNSET ? 0 : ((duration * progress) / PROGRESS_BAR_MAX);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player == null || event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                player.setPlayWhenReady(!player.getPlayWhenReady());
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                player.setPlayWhenReady(true);
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                player.setPlayWhenReady(false);
                break;
            default:
                return false;
        }
        show();
        return true;
    }

    private final class ComponentListener implements ExoPlayer.EventListener,
            SeekBar.OnSeekBarChangeListener, OnClickListener {

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeCallbacks(hideAction);
            dragging = true;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                timeCurrent.setText(stringForTime(positionValue(progress)));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            dragging = false;
            player.seekTo(positionValue(seekBar.getProgress()));
            hideDeferredIfPlaying();
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            show();
        }

        @Override
        public void onPositionDiscontinuity() {
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            // Do nothing.
        }

        @Override
        public void onClick(View view) {
            if (playButton == view) {
                player.setPlayWhenReady(!player.getPlayWhenReady());
            } else if (fullscreen == view) {
                listener.onFullscreenClick();
            }

            removeCallbacks(hideAction);
            hideDeferredIfPlaying();
        }
    }

    public interface FullscreenClickListener {
        void onFullscreenClick();
    }
}
