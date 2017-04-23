package xyz.garywzh.loveleak.ui.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import xyz.garywzh.loveleak.R;

/**
 * Created by garywzh on 2016/9/15.
 */
public class PrettyPlayerView extends FrameLayout {

    private final View surfaceView;
    private final View shutterView;
    private final AspectRatioFrameLayout layout;
    private final PrettyControlView controller;
    private final ComponentListener componentListener;
    private SimpleExoPlayer player;
    private boolean useController = true;
    private boolean isPortrait = false;

    public PrettyPlayerView(Context context) {
        this(context, null);
    }

    public PrettyPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrettyPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_pretty_video, this);
        componentListener = new ComponentListener();
        layout = (AspectRatioFrameLayout) findViewById(R.id.video_frame);
        controller = (PrettyControlView) findViewById(R.id.control);
        shutterView = findViewById(R.id.shutter);

        View view = new SurfaceView(context);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        surfaceView = view;
        layout.addView(surfaceView, 0);
    }

    /**
     * Set the {@link SimpleExoPlayer} to use. The {@link SimpleExoPlayer#setTextOutput} and
     * {@link SimpleExoPlayer#setVideoListener} method of the player will be called and previous
     * assignments are overridden.
     *
     * @param player The {@link SimpleExoPlayer} to use.
     */
    public void setPlayer(SimpleExoPlayer player) {
        if (this.player != null) {
            this.player.setTextOutput(null);
            this.player.setVideoListener(null);
            this.player.setVideoSurface(null);
        }
        this.player = player;

        if (player != null) {
            if (surfaceView instanceof TextureView) {
                player.setVideoTextureView((TextureView) surfaceView);
            } else if (surfaceView instanceof SurfaceView) {
                player.setVideoSurfaceView((SurfaceView) surfaceView);
            }
            player.setVideoListener(componentListener);
        }
        setUseController(useController);
    }

    /**
     * Set the {@code useController} flag which indicates whether the playback control view should
     * be used or not. If set to {@code false} the controller is never visible and is disconnected
     * from the player.
     *
     * @param useController If {@code false} the playback control is never used.
     */
    public void setUseController(boolean useController) {
        this.useController = useController;
        if (useController) {
            controller.setPlayer(player);
        } else {
            controller.hide();
            controller.setPlayer(null);
        }
    }

    public void setFullscreenToggleListener(PrettyControlView.FullscreenClickListener listener) {
        controller.setFullscreenToggleListener(listener);
    }

    public boolean isPortrait() {
        return isPortrait;
    }

    /**
     * Get the view onto which video is rendered. This is either a {@link SurfaceView} (default)
     * or a {@link TextureView} if the {@code use_texture_view} view attribute has been set to true.
     *
     * @return either a {@link SurfaceView} or a {@link TextureView}.
     */
    public View getVideoSurfaceView() {
        return surfaceView;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return useController ? controller.dispatchKeyEvent(event) : super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (useController && ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (controller.isVisible()) {
                controller.hide();
            } else {
                controller.show();
            }
        }
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (!useController) {
            return false;
        }
        controller.show();
        return true;
    }

    private final class ComponentListener implements SimpleExoPlayer.VideoListener {

        // SimpleExoPlayer.VideoListener implementation

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
            float pixelWidthHeightRatio) {
            float aspectRatio = height == 0 ? 1 : (width * pixelWidthHeightRatio) / height;
            isPortrait = aspectRatio < 1;
            layout.setAspectRatio(aspectRatio);
        }

        @Override
        public void onRenderedFirstFrame() {
            shutterView.setVisibility(GONE);
        }
    }
}
