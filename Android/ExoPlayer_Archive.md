# Archive of doing things

## Time and Play/Pause synced with PlayerView
**The Controller**
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:app="http://schemas.android.com/apk/res-auto"
              xmlns:tools="http://schemas.android.com/tools"
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              android:layout_gravity="bottom"
              android:layoutDirection="ltr"
              android:background="@color/transparent"
              android:orientation="vertical"
              tools:targetApi="28">

	<androidx.constraintlayout.widget.ConstraintLayout
			android:layout_width="match_parent"
			android:layout_height="wrap_content"
			android:layout_marginTop="4dp"
			android:orientation="horizontal">

		<com.airbnb.lottie.LottieAnimationView
				android:id="@+id/exo_play_pause"
				android:layout_width="0dp"
				android:layout_height="0dp"
				app:lottie_fileName="playPauseLottie.json"
				app:lottie_autoPlay="false"
				android:scaleType="centerCrop"
				app:layout_constraintBottom_toBottomOf="parent"
				app:layout_constraintLeft_toLeftOf="parent"/>

		<TextView
				android:id="@id/exo_position"
				android:layout_width="wrap_content"
				android:layout_height="wrap_content"
				android:textStyle="bold"
				android:includeFontPadding="false"
				android:textColor="@color/blue0"
				app:layout_constraintBottom_toBottomOf="parent"
				app:layout_constraintRight_toRightOf="parent"/>

	</androidx.constraintlayout.widget.ConstraintLayout>

</LinearLayout>
```
**The PlayerView**
```
		<com.meetkai.app.and.customviews.FitnessPlayerView
				android:id="@+id/fitness_ex_player_view"
				android:layout_width="match_parent"
				android:layout_height="0dp"
				app:layout_height="@{DeviceUtilities.screenWidthDp * 0.853f}"
				app:resize_mode="zoom"
				app:shutter_background_color="@color/white"
				app:hide_on_touch="false"
				app:show_timeout="0"
				app:layout_constraintTop_toTopOf="parent"
				app:controller_layout_id="@layout/fitness_player_controller_view"/>
```
**The Custom PlayerControlView**
```
package com.meetkai.app.and.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.LottieValueCallback;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;
import com.meetkai.app.and.R;
import com.meetkai.app.and.utilities.DeviceUtilities;
import com.meetkai.app.and.utilities.ViewUtilities;

import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;

/**
 * A view for controlling {@link Player} instances.
 *
 * <p>A PlayerControlView can be customized by setting attributes (or calling corresponding
 * methods), overriding the view's layout file or by specifying a custom view layout file, as
 * outlined below.
 *
 * <h3>Attributes</h3>
 *
 * The following attributes can be set on a PlayerControlView when used in a layout XML file:
 *
 * <ul>
 *   <li><b>{@code show_timeout}</b> - The time between the last user interaction and the controls
 *       being automatically hidden, in milliseconds. Use zero if the controls should not
 *       automatically timeout.
 *       <ul>
 *         <li>Corresponding method: {@link #setShowTimeoutMs(int)}
 *         <li>Default: {@link #DEFAULT_SHOW_TIMEOUT_MS}
 *       </ul>
 *   <li><b>{@code rewind_increment}</b> - The duration of the rewind applied when the user taps the
 *       rewind button, in milliseconds. Use zero to disable the rewind button.
 *       <ul>
 *         <li>Corresponding method: {@link #setRewindIncrementMs(int)}
 *         <li>Default: {@link #DEFAULT_REWIND_MS}
 *       </ul>
 *   <li><b>{@code fastforward_increment}</b> - Like {@code rewind_increment}, but for fast forward.
 *       <ul>
 *         <li>Corresponding method: {@link #setFastForwardIncrementMs(int)}
 *         <li>Default: {@link #DEFAULT_FAST_FORWARD_MS}
 *       </ul>
 *   <li><b>{@code repeat_toggle_modes}</b> - A flagged enumeration value specifying which repeat
 *       mode toggle options are enabled. Valid values are: {@code none}, {@code one}, {@code all},
 *       or {@code one|all}.
 *       <ul>
 *         <li>Corresponding method: {@link #setRepeatToggleModes(int)}
 *         <li>Default: {@link FitnessPlayerControlView#DEFAULT_REPEAT_TOGGLE_MODES}
 *       </ul>
 *   <li><b>{@code show_shuffle_button}</b> - Whether the shuffle button is shown.
 *       <ul>
 *         <li>Corresponding method: {@link #setShowShuffleButton(boolean)}
 *         <li>Default: false
 *       </ul>
 *   <li><b>{@code controller_layout_id}</b> - Specifies the id of the layout to be inflated. See
 *       below for more details.
 *       <ul>
 *         <li>Corresponding method: None
 *         <li>Default: {@code R.layout.exo_player_control_view}
 *       </ul>
 * </ul>
 *
 * <h3>Overriding the layout file</h3>
 *
 * To customize the layout of PlayerControlView throughout your app, or just for certain
 * configurations, you can define {@code exo_player_control_view.xml} layout files in your
 * application {@code res/layout*} directories. These layouts will override the one provided by the
 * ExoPlayer library, and will be inflated for use by PlayerControlView. The view identifies and
 * binds its children by looking for the following ids:
 *
 * <p>
 *
 * <ul>
 *   <li><b>{@code exo_play}</b> - The play button.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_pause}</b> - The pause button.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_ffwd}</b> - The fast forward button.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_rew}</b> - The rewind button.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_prev}</b> - The previous track button.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_next}</b> - The next track button.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_repeat_toggle}</b> - The repeat toggle button.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_shuffle}</b> - The shuffle button.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_position}</b> - Text view displaying the current playback position.
 *       <ul>
 *         <li>Type: {@link TextView}
 *       </ul>
 *   <li><b>{@code exo_duration}</b> - Text view displaying the current media duration.
 *       <ul>
 *         <li>Type: {@link TextView}
 *       </ul>
 *   <li><b>{@code exo_progress}</b> - Time bar that's updated during playback and allows seeking.
 *       <ul>
 *         <li>Type: {@link TimeBar}
 *       </ul>
 * </ul>
 *
 * <p>All child views are optional and so can be omitted if not required, however where defined they
 * must be of the expected type.
 *
 * <h3>Specifying a custom layout file</h3>
 *
 * Defining your own {@code exo_player_control_view.xml} is useful to customize the layout of
 * PlayerControlView throughout your application. It's also possible to customize the layout for a
 * single instance in a layout file. This is achieved by setting the {@code controller_layout_id}
 * attribute on a PlayerControlView. This will cause the specified layout to be inflated instead of
 * {@code exo_player_control_view.xml} for only the instance on which the attribute is set.
 */
public class FitnessPlayerControlView extends FrameLayout {

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.ui");
    }

    /** Listener to be notified about changes of the visibility of the UI control. */
    public interface VisibilityListener {

        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either {@link View#VISIBLE} or {@link View#GONE}.
         */
        void onVisibilityChange(int visibility);
    }

    /** The default fast forward increment, in milliseconds. */
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    /** The default rewind increment, in milliseconds. */
    public static final int DEFAULT_REWIND_MS = 5000;
    /** The default show timeout, in milliseconds. */
    public static final int DEFAULT_SHOW_TIMEOUT_MS = 5000;
    /** The default repeat toggle modes. */
    public static final @RepeatModeUtil.RepeatToggleModes int DEFAULT_REPEAT_TOGGLE_MODES =
            RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE;

    /** The maximum number of windows that can be shown in a multi-window time bar. */
    public static final int MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR = 100;

    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;

    private final ComponentListener componentListener;
    private final View previousButton;
    private final View nextButton;
    private final LottieAnimationView playPauseButton;
    private final View fastForwardButton;
    private final View rewindButton;
    private final ImageView repeatToggleButton;
    private final View shuffleButton;
    private final TextView durationView;
    private final TextView positionView;
    private final TimeBar timeBar;
    private final StringBuilder formatBuilder;
    private final Formatter formatter;
    private final Timeline.Period period;
    private final Timeline.Window window;
    private final Runnable updateProgressAction;
    private final Runnable hideAction;

    private final Drawable repeatOffButtonDrawable;
    private final Drawable repeatOneButtonDrawable;
    private final Drawable repeatAllButtonDrawable;
    private final String repeatOffButtonContentDescription;
    private final String repeatOneButtonContentDescription;
    private final String repeatAllButtonContentDescription;

    private Player player;
    private com.google.android.exoplayer2.ControlDispatcher controlDispatcher;
    private VisibilityListener visibilityListener;
    private @Nullable
    PlaybackPreparer playbackPreparer;

    private boolean isAttachedToWindow;
    private boolean showMultiWindowTimeBar;
    private boolean multiWindowTimeBar;
    private boolean scrubbing;
    private int rewindMs;
    private int fastForwardMs;
    private int showTimeoutMs;
    private @RepeatModeUtil.RepeatToggleModes int repeatToggleModes;
    private boolean showShuffleButton;
    private long hideAtMs;
    private long[] adGroupTimesMs;
    private boolean[] playedAdGroups;
    private long[] extraAdGroupTimesMs;
    private boolean[] extraPlayedAdGroups;

    public FitnessPlayerControlView(Context context) {
        this(context, null);
    }

    public FitnessPlayerControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitnessPlayerControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, attrs);
    }

    public FitnessPlayerControlView(
            Context context, AttributeSet attrs, int defStyleAttr, AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr);
        int controllerLayoutId = R.layout.exo_player_control_view;
        rewindMs = DEFAULT_REWIND_MS;
        fastForwardMs = DEFAULT_FAST_FORWARD_MS;
        showTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS;
        repeatToggleModes = DEFAULT_REPEAT_TOGGLE_MODES;
        hideAtMs = C.TIME_UNSET;
        showShuffleButton = false;
        if (playbackAttrs != null) {
            TypedArray a =
                    context
                            .getTheme()
                            .obtainStyledAttributes(playbackAttrs, R.styleable.PlayerControlView, 0, 0);
            try {
                rewindMs = a.getInt(R.styleable.PlayerControlView_rewind_increment, rewindMs);
                fastForwardMs =
                        a.getInt(R.styleable.PlayerControlView_fastforward_increment, fastForwardMs);
                showTimeoutMs = a.getInt(R.styleable.PlayerControlView_show_timeout, showTimeoutMs);
                controllerLayoutId =
                        a.getResourceId(R.styleable.PlayerControlView_controller_layout_id, controllerLayoutId);
                repeatToggleModes = getRepeatToggleModes(a, repeatToggleModes);
                showShuffleButton =
                        a.getBoolean(R.styleable.PlayerControlView_show_shuffle_button, showShuffleButton);
            } finally {
                a.recycle();
            }
        }
        period = new Timeline.Period();
        window = new Timeline.Window();
        formatBuilder = new StringBuilder();
        formatter = new Formatter(formatBuilder, Locale.getDefault());
        adGroupTimesMs = new long[0];
        playedAdGroups = new boolean[0];
        extraAdGroupTimesMs = new long[0];
        extraPlayedAdGroups = new boolean[0];
        componentListener = new ComponentListener();
        controlDispatcher = new com.google.android.exoplayer2.DefaultControlDispatcher();
        updateProgressAction = this::updateProgress;
        hideAction = this::hide;

        LayoutInflater.from(context).inflate(controllerLayoutId, this);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        int horizontalMargin = DeviceUtilities.dpToPx(DeviceUtilities.screenDiagDp * 0.01944f);
        int verticalMargin = DeviceUtilities.dpToPx(DeviceUtilities.screenDiagDp * 0.02187f);

        durationView = findViewById(R.id.exo_duration);
        positionView = findViewById(R.id.exo_position);
        positionView.setTextSize(DeviceUtilities.screenDiagDp * 0.051f);
        ViewUtilities.setViewMargins(
                positionView,
                null,
                null,
                horizontalMargin,
                verticalMargin
        );
        timeBar = findViewById(R.id.exo_progress);
        if (timeBar != null) {
            timeBar.addListener(componentListener);
        }
        playPauseButton = findViewById(R.id.exo_play_pause);
        if(playPauseButton != null) {
            playPauseButton.enableMergePathsForKitKatAndAbove(true);
            playPauseButton.addValueCallback(
                    new KeyPath("**"),
                    LottieProperty.COLOR,
                    new LottieValueCallback<>(getResources().getColor(R.color.blue0, null)));
            int size = DeviceUtilities.dpToPx(DeviceUtilities.screenDiagDp * 0.055f);
            ViewUtilities.setViewDimensions(
                    playPauseButton,
                    size,
                    size
            );
            ViewUtilities.setViewMargins(
                    playPauseButton,
                    horizontalMargin,
                    null,
                    null,
                    verticalMargin
            );
            playPauseButton.setOnClickListener(componentListener);
        }
        previousButton = findViewById(R.id.exo_prev);
        if (previousButton != null) {
            previousButton.setOnClickListener(componentListener);
        }
        nextButton = findViewById(R.id.exo_next);
        if (nextButton != null) {
            nextButton.setOnClickListener(componentListener);
        }
        rewindButton = findViewById(R.id.exo_rew);
        if (rewindButton != null) {
            rewindButton.setOnClickListener(componentListener);
        }
        fastForwardButton = findViewById(R.id.exo_ffwd);
        if (fastForwardButton != null) {
            fastForwardButton.setOnClickListener(componentListener);
        }
        repeatToggleButton = findViewById(R.id.exo_repeat_toggle);
        if (repeatToggleButton != null) {
            repeatToggleButton.setOnClickListener(componentListener);
        }
        shuffleButton = findViewById(R.id.exo_shuffle);
        if (shuffleButton != null) {
            shuffleButton.setOnClickListener(componentListener);
        }
        Resources resources = context.getResources();
        repeatOffButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_off, null);
        repeatOneButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_one, null);
        repeatAllButtonDrawable = resources.getDrawable(R.drawable.exo_controls_repeat_all, null);
        repeatOffButtonContentDescription =
                resources.getString(R.string.exo_controls_repeat_off_description);
        repeatOneButtonContentDescription =
                resources.getString(R.string.exo_controls_repeat_one_description);
        repeatAllButtonContentDescription =
                resources.getString(R.string.exo_controls_repeat_all_description);
    }

    @SuppressWarnings("ResourceType")
    private static @RepeatModeUtil.RepeatToggleModes int getRepeatToggleModes(
            TypedArray a, @RepeatModeUtil.RepeatToggleModes int repeatToggleModes) {
        return a.getInt(R.styleable.PlayerControlView_repeat_toggle_modes, repeatToggleModes);
    }

    /**
     * Returns the {@link Player} currently being controlled by this view, or null if no player is
     * set.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the {@link Player} to control.
     *
     * @param player The {@link Player} to control, or {@code null} to detach the current player. Only
     *     players which are accessed on the main thread are supported ({@code
     *     player.getApplicationLooper() == Looper.getMainLooper()}).
     */
    public void setPlayer(@Nullable Player player) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        Assertions.checkArgument(
                player == null || player.getApplicationLooper() == Looper.getMainLooper());
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
        }
        this.player = player;
        if (player != null) {
            player.addListener(componentListener);
        }
        updateAll();
    }

    /**
     * Sets whether the time bar should show all windows, as opposed to just the current one. If the
     * timeline has a period with unknown duration or more than {@link
     * #MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR} windows the time bar will fall back to showing a single
     * window.
     *
     * @param showMultiWindowTimeBar Whether the time bar should show all windows.
     */
    public void setShowMultiWindowTimeBar(boolean showMultiWindowTimeBar) {
        this.showMultiWindowTimeBar = showMultiWindowTimeBar;
        updateTimeBarMode();
    }

    /**
     * Sets the millisecond positions of extra ad markers relative to the start of the window (or
     * timeline, if in multi-window mode) and whether each extra ad has been played or not. The
     * markers are shown in addition to any ad markers for ads in the player's timeline.
     *
     * @param extraAdGroupTimesMs The millisecond timestamps of the extra ad markers to show, or
     *     {@code null} to show no extra ad markers.
     * @param extraPlayedAdGroups Whether each ad has been played, or {@code null} to show no extra ad
     *     markers.
     */
    public void setExtraAdGroupMarkers(
            @Nullable long[] extraAdGroupTimesMs, @Nullable boolean[] extraPlayedAdGroups) {
        if (extraAdGroupTimesMs == null) {
            this.extraAdGroupTimesMs = new long[0];
            this.extraPlayedAdGroups = new boolean[0];
        } else {
            Assertions.checkArgument(extraAdGroupTimesMs.length == extraPlayedAdGroups.length);
            this.extraAdGroupTimesMs = extraAdGroupTimesMs;
            this.extraPlayedAdGroups = extraPlayedAdGroups;
        }
        updateProgress();
    }

    /**
     * Sets the {@link VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void setVisibilityListener(VisibilityListener listener) {
        this.visibilityListener = listener;
    }

    /**
     * Sets the {@link PlaybackPreparer}.
     *
     * @param playbackPreparer The {@link PlaybackPreparer}.
     */
    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        this.playbackPreparer = playbackPreparer;
    }

    /**
     * Sets the {@link com.google.android.exoplayer2.ControlDispatcher}.
     *
     * @param controlDispatcher The {@link com.google.android.exoplayer2.ControlDispatcher}, or null
     *     to use {@link com.google.android.exoplayer2.DefaultControlDispatcher}.
     */
    public void setControlDispatcher(
            @Nullable com.google.android.exoplayer2.ControlDispatcher controlDispatcher) {
        this.controlDispatcher =
                controlDispatcher == null
                        ? new com.google.android.exoplayer2.DefaultControlDispatcher()
                        : controlDispatcher;
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
     *     rewind button to be disabled.
     */
    public void setRewindIncrementMs(int rewindMs) {
        this.rewindMs = rewindMs;
        updateNavigation();
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
     *     cause the fast forward button to be disabled.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        this.fastForwardMs = fastForwardMs;
        updateNavigation();
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input.
     *
     * @return The duration in milliseconds. A non-positive value indicates that the controls will
     *     remain visible indefinitely.
     */
    public int getShowTimeoutMs() {
        return showTimeoutMs;
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input.
     *
     * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
     *     to remain visible indefinitely.
     */
    public void setShowTimeoutMs(int showTimeoutMs) {
        this.showTimeoutMs = showTimeoutMs;
        if (isVisible()) {
            // Reset the timeout.
            hideAfterTimeout();
        }
    }

    /**
     * Returns which repeat toggle modes are enabled.
     *
     * @return The currently enabled {@link RepeatModeUtil.RepeatToggleModes}.
     */
    public @RepeatModeUtil.RepeatToggleModes int getRepeatToggleModes() {
        return repeatToggleModes;
    }

    /**
     * Sets which repeat toggle modes are enabled.
     *
     * @param repeatToggleModes A set of {@link RepeatModeUtil.RepeatToggleModes}.
     */
    public void setRepeatToggleModes(@RepeatModeUtil.RepeatToggleModes int repeatToggleModes) {
        this.repeatToggleModes = repeatToggleModes;
        if (player != null) {
            @Player.RepeatMode int currentMode = player.getRepeatMode();
            if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE
                    && currentMode != Player.REPEAT_MODE_OFF) {
                controlDispatcher.dispatchSetRepeatMode(player, Player.REPEAT_MODE_OFF);
            } else if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_ONE
                    && currentMode == Player.REPEAT_MODE_ALL) {
                controlDispatcher.dispatchSetRepeatMode(player, Player.REPEAT_MODE_ONE);
            } else if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_ALL
                    && currentMode == Player.REPEAT_MODE_ONE) {
                controlDispatcher.dispatchSetRepeatMode(player, Player.REPEAT_MODE_ALL);
            }
        }
        updateRepeatModeButton();
    }

    /** Returns whether the shuffle button is shown. */
    public boolean getShowShuffleButton() {
        return showShuffleButton;
    }

    /**
     * Sets whether the shuffle button is shown.
     *
     * @param showShuffleButton Whether the shuffle button is shown.
     */
    public void setShowShuffleButton(boolean showShuffleButton) {
        this.showShuffleButton = showShuffleButton;
        updateShuffleButton();
    }

    /**
     * Shows the playback controls. If {@link #getShowTimeoutMs()} is positive then the controls will
     * be automatically hidden after this duration of time has elapsed without user input.
     */
    public void show() {
        if (!isVisible()) {
            setVisibility(VISIBLE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            updateAll();
            requestPlayPauseFocus();
        }
        // Call hideAfterTimeout even if already visible to reset the timeout.
        hideAfterTimeout();
    }

    /** Hides the controller. */
    public void hide() {
        if (isVisible()) {
            setVisibility(GONE);
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(getVisibility());
            }
            removeCallbacks(updateProgressAction);
            removeCallbacks(hideAction);
            hideAtMs = C.TIME_UNSET;
        }
    }

    /** Returns whether the controller is currently visible. */
    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }

    private void hideAfterTimeout() {
        removeCallbacks(hideAction);
        if (showTimeoutMs > 0) {
            hideAtMs = SystemClock.uptimeMillis() + showTimeoutMs;
            if (isAttachedToWindow) {
                postDelayed(hideAction, showTimeoutMs);
            }
        } else {
            hideAtMs = C.TIME_UNSET;
        }
    }

    private void updateAll() {
        updatePlayPauseButton();
        updateNavigation();
        updateRepeatModeButton();
        updateShuffleButton();
        updateProgress();
    }

    private void updatePlayPauseButton() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        boolean requestPlayPauseFocus = playPauseButton.isFocused();
        boolean isPlaying = isPlaying();

        if(isPlaying) {
            playPauseButton.setSpeed(1);
            playPauseButton.playAnimation();
        } else {
            playPauseButton.setSpeed(-1);
            playPauseButton.playAnimation();
        }

        if (requestPlayPauseFocus) {
            requestPlayPauseFocus();
        }
    }

    private void updateNavigation() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }
        Timeline timeline = player != null ? player.getCurrentTimeline() : null;
        boolean haveNonEmptyTimeline = timeline != null && !timeline.isEmpty();
        boolean isSeekable = false;
        boolean enablePrevious = false;
        boolean enableNext = false;
        if (haveNonEmptyTimeline && !player.isPlayingAd()) {
            int windowIndex = player.getCurrentWindowIndex();
            timeline.getWindow(windowIndex, window);
            isSeekable = window.isSeekable;
            enablePrevious = isSeekable || !window.isDynamic || player.hasPrevious();
            enableNext = window.isDynamic || player.hasNext();
        }
        setButtonEnabled(enablePrevious, previousButton);
        setButtonEnabled(enableNext, nextButton);
        setButtonEnabled(fastForwardMs > 0 && isSeekable, fastForwardButton);
        setButtonEnabled(rewindMs > 0 && isSeekable, rewindButton);
        if (timeBar != null) {
            timeBar.setEnabled(isSeekable);
        }
    }

    private void updateRepeatModeButton() {
        if (!isVisible() || !isAttachedToWindow || repeatToggleButton == null) {
            return;
        }
        if (repeatToggleModes == RepeatModeUtil.REPEAT_TOGGLE_MODE_NONE) {
            repeatToggleButton.setVisibility(View.GONE);
            return;
        }
        if (player == null) {
            setButtonEnabled(false, repeatToggleButton);
            return;
        }
        setButtonEnabled(true, repeatToggleButton);
        switch (player.getRepeatMode()) {
            case Player.REPEAT_MODE_OFF:
                repeatToggleButton.setImageDrawable(repeatOffButtonDrawable);
                repeatToggleButton.setContentDescription(repeatOffButtonContentDescription);
                break;
            case Player.REPEAT_MODE_ONE:
                repeatToggleButton.setImageDrawable(repeatOneButtonDrawable);
                repeatToggleButton.setContentDescription(repeatOneButtonContentDescription);
                break;
            case Player.REPEAT_MODE_ALL:
                repeatToggleButton.setImageDrawable(repeatAllButtonDrawable);
                repeatToggleButton.setContentDescription(repeatAllButtonContentDescription);
                break;
            default:
                // Never happens.
        }
        repeatToggleButton.setVisibility(View.VISIBLE);
    }

    private void updateShuffleButton() {
        if (!isVisible() || !isAttachedToWindow || shuffleButton == null) {
            return;
        }
        if (!showShuffleButton) {
            shuffleButton.setVisibility(View.GONE);
        } else if (player == null) {
            setButtonEnabled(false, shuffleButton);
        } else {
            shuffleButton.setAlpha(player.getShuffleModeEnabled() ? 1f : 0.3f);
            shuffleButton.setEnabled(true);
            shuffleButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateTimeBarMode() {
        if (player == null) {
            return;
        }
        multiWindowTimeBar =
                showMultiWindowTimeBar && canShowMultiWindowTimeBar(player.getCurrentTimeline(), window);
    }

    private void updateProgress() {
        if (!isVisible() || !isAttachedToWindow) {
            return;
        }

        long position = 0;
        long bufferedPosition = 0;
        long duration = 0;
        if (player != null) {
            long currentWindowTimeBarOffsetMs = 0;
            long durationUs = 0;
            int adGroupCount = 0;
            Timeline timeline = player.getCurrentTimeline();
            if (!timeline.isEmpty()) {
                int currentWindowIndex = player.getCurrentWindowIndex();
                int firstWindowIndex = multiWindowTimeBar ? 0 : currentWindowIndex;
                int lastWindowIndex =
                        multiWindowTimeBar ? timeline.getWindowCount() - 1 : currentWindowIndex;
                for (int i = firstWindowIndex; i <= lastWindowIndex; i++) {
                    if (i == currentWindowIndex) {
                        currentWindowTimeBarOffsetMs = C.usToMs(durationUs);
                    }
                    timeline.getWindow(i, window);
                    if (window.durationUs == C.TIME_UNSET) {
                        Assertions.checkState(!multiWindowTimeBar);
                        break;
                    }
                    for (int j = window.firstPeriodIndex; j <= window.lastPeriodIndex; j++) {
                        timeline.getPeriod(j, period);
                        int periodAdGroupCount = period.getAdGroupCount();
                        for (int adGroupIndex = 0; adGroupIndex < periodAdGroupCount; adGroupIndex++) {
                            long adGroupTimeInPeriodUs = period.getAdGroupTimeUs(adGroupIndex);
                            if (adGroupTimeInPeriodUs == C.TIME_END_OF_SOURCE) {
                                if (period.durationUs == C.TIME_UNSET) {
                                    // Don't show ad markers for postrolls in periods with unknown duration.
                                    continue;
                                }
                                adGroupTimeInPeriodUs = period.durationUs;
                            }
                            long adGroupTimeInWindowUs = adGroupTimeInPeriodUs + period.getPositionInWindowUs();
                            if (adGroupTimeInWindowUs >= 0 && adGroupTimeInWindowUs <= window.durationUs) {
                                if (adGroupCount == adGroupTimesMs.length) {
                                    int newLength = adGroupTimesMs.length == 0 ? 1 : adGroupTimesMs.length * 2;
                                    adGroupTimesMs = Arrays.copyOf(adGroupTimesMs, newLength);
                                    playedAdGroups = Arrays.copyOf(playedAdGroups, newLength);
                                }
                                adGroupTimesMs[adGroupCount] = C.usToMs(durationUs + adGroupTimeInWindowUs);
                                playedAdGroups[adGroupCount] = period.hasPlayedAdGroup(adGroupIndex);
                                adGroupCount++;
                            }
                        }
                    }
                    durationUs += window.durationUs;
                }
            }
            duration = C.usToMs(durationUs);
            position = currentWindowTimeBarOffsetMs + player.getContentPosition();
            bufferedPosition = currentWindowTimeBarOffsetMs + player.getContentBufferedPosition();
            if (timeBar != null) {
                int extraAdGroupCount = extraAdGroupTimesMs.length;
                int totalAdGroupCount = adGroupCount + extraAdGroupCount;
                if (totalAdGroupCount > adGroupTimesMs.length) {
                    adGroupTimesMs = Arrays.copyOf(adGroupTimesMs, totalAdGroupCount);
                    playedAdGroups = Arrays.copyOf(playedAdGroups, totalAdGroupCount);
                }
                System.arraycopy(extraAdGroupTimesMs, 0, adGroupTimesMs, adGroupCount, extraAdGroupCount);
                System.arraycopy(extraPlayedAdGroups, 0, playedAdGroups, adGroupCount, extraAdGroupCount);
                timeBar.setAdGroupTimesMs(adGroupTimesMs, playedAdGroups, totalAdGroupCount);
            }
        }
        if (durationView != null) {
            durationView.setText(Util.getStringForTime(formatBuilder, formatter, duration));
        }
        if (positionView != null && !scrubbing) {
            positionView.setText(formatTime(position));
        }
        if (timeBar != null) {
            timeBar.setPosition(position);
            timeBar.setBufferedPosition(bufferedPosition);
            timeBar.setDuration(duration);
        }

        // Cancel any pending updates and schedule a new one if necessary.
        removeCallbacks(updateProgressAction);
        int playbackState = player == null ? Player.STATE_IDLE : player.getPlaybackState();
        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            long delayMs;
            if (player.getPlayWhenReady() && playbackState == Player.STATE_READY) {
                float playbackSpeed = player.getPlaybackParameters().speed;
                if (playbackSpeed <= 0.1f) {
                    delayMs = 1000;
                } else if (playbackSpeed <= 5f) {
                    long mediaTimeUpdatePeriodMs = 1000 / Math.max(1, Math.round(1 / playbackSpeed));
                    long mediaTimeDelayMs = mediaTimeUpdatePeriodMs - (position % mediaTimeUpdatePeriodMs);
                    if (mediaTimeDelayMs < (mediaTimeUpdatePeriodMs / 5)) {
                        mediaTimeDelayMs += mediaTimeUpdatePeriodMs;
                    }
                    delayMs =
                            playbackSpeed == 1 ? mediaTimeDelayMs : (long) (mediaTimeDelayMs / playbackSpeed);
                } else {
                    delayMs = 200;
                }
            } else {
                delayMs = 1000;
            }
            postDelayed(updateProgressAction, delayMs);
        }
    }

    private void requestPlayPauseFocus() {
        playPauseButton.requestFocus();
    }

    private void setButtonEnabled(boolean enabled, View view) {
        if (view == null) {
            return;
        }
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1f : 0.3f);
        view.setVisibility(VISIBLE);
    }

    private void previous() {
        Timeline timeline = player.getCurrentTimeline();
        if (timeline.isEmpty() || player.isPlayingAd()) {
            return;
        }
        int windowIndex = player.getCurrentWindowIndex();
        timeline.getWindow(windowIndex, window);
        int previousWindowIndex = player.getPreviousWindowIndex();
        if (previousWindowIndex != C.INDEX_UNSET
                && (player.getCurrentPosition() <= MAX_POSITION_FOR_SEEK_TO_PREVIOUS
                || (window.isDynamic && !window.isSeekable))) {
            seekTo(previousWindowIndex, C.TIME_UNSET);
        } else {
            seekTo(0);
        }
    }

    private void next() {
        Timeline timeline = player.getCurrentTimeline();
        if (timeline.isEmpty() || player.isPlayingAd()) {
            return;
        }
        int windowIndex = player.getCurrentWindowIndex();
        int nextWindowIndex = player.getNextWindowIndex();
        if (nextWindowIndex != C.INDEX_UNSET) {
            seekTo(nextWindowIndex, C.TIME_UNSET);
        } else if (timeline.getWindow(windowIndex, window).isDynamic) {
            seekTo(windowIndex, C.TIME_UNSET);
        }
    }

    private void rewind() {
        if (rewindMs <= 0) {
            return;
        }
        seekTo(Math.max(player.getCurrentPosition() - rewindMs, 0));
    }

    private void fastForward() {
        if (fastForwardMs <= 0) {
            return;
        }
        long durationMs = player.getDuration();
        long seekPositionMs = player.getCurrentPosition() + fastForwardMs;
        if (durationMs != C.TIME_UNSET) {
            seekPositionMs = Math.min(seekPositionMs, durationMs);
        }
        seekTo(seekPositionMs);
    }

    private void seekTo(long positionMs) {
        seekTo(player.getCurrentWindowIndex(), positionMs);
    }

    private void seekTo(int windowIndex, long positionMs) {
        boolean dispatched = controlDispatcher.dispatchSeekTo(player, windowIndex, positionMs);
        if (!dispatched) {
            // The seek wasn't dispatched. If the progress bar was dragged by the user to perform the
            // seek then it'll now be in the wrong position. Trigger a progress update to snap it back.
            updateProgress();
        }
    }

    private void seekToTimeBarPosition(long positionMs) {
        int windowIndex;
        Timeline timeline = player.getCurrentTimeline();
        if (multiWindowTimeBar && !timeline.isEmpty()) {
            int windowCount = timeline.getWindowCount();
            windowIndex = 0;
            while (true) {
                long windowDurationMs = timeline.getWindow(windowIndex, window).getDurationMs();
                if (positionMs < windowDurationMs) {
                    break;
                } else if (windowIndex == windowCount - 1) {
                    // Seeking past the end of the last window should seek to the end of the timeline.
                    positionMs = windowDurationMs;
                    break;
                }
                positionMs -= windowDurationMs;
                windowIndex++;
            }
        } else {
            windowIndex = player.getCurrentWindowIndex();
        }
        seekTo(windowIndex, positionMs);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
        if (hideAtMs != C.TIME_UNSET) {
            long delayMs = hideAtMs - SystemClock.uptimeMillis();
            if (delayMs <= 0) {
                hide();
            } else {
                postDelayed(hideAction, delayMs);
            }
        } else if (isVisible()) {
            hideAfterTimeout();
        }
        updateAll();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
        removeCallbacks(updateProgressAction);
        removeCallbacks(hideAction);
    }

    @Override
    public final boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            removeCallbacks(hideAction);
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            hideAfterTimeout();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    /**
     * Called to process media key events. Any {@link KeyEvent} can be passed but only media key
     * events will be handled.
     *
     * @param event A key event.
     * @return Whether the key event was handled.
     */
    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (player == null || !isHandledMediaKey(keyCode)) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
                fastForward();
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND) {
                rewind();
            } else if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        controlDispatcher.dispatchSetPlayWhenReady(player, !player.getPlayWhenReady());
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        controlDispatcher.dispatchSetPlayWhenReady(player, true);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        controlDispatcher.dispatchSetPlayWhenReady(player, false);
                        break;
                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        next();
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        previous();
                        break;
                    default:
                        break;
                }
            }
        }
        return true;
    }

    private boolean isPlaying() {
        return player != null
                && player.getPlaybackState() != Player.STATE_ENDED
                && player.getPlaybackState() != Player.STATE_IDLE
                && player.getPlayWhenReady();
    }

    @SuppressLint("InlinedApi")
    private static boolean isHandledMediaKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT
                || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS;
    }

    /**
     * Formats the time to be output to the screen
     *
     * @return A formatted String in the format of :59 before 60 seconds
     */
    private String formatTime(long time) {
        long beforeColon = time / 60000;
        if(beforeColon > 1) formatBuilder.append(beforeColon);
        formatBuilder.append(":");
        long afterColon = Math.round((time % 60000f) / 1000f);
        if(afterColon < 10) formatBuilder.append("0");
        formatBuilder.append(afterColon);

        String toReturn = formatBuilder.toString();
        // Clear the StringBuilder to be used again later
        formatBuilder.setLength(0);
        return toReturn;
    }

    /**
     * Returns whether the specified {@code timeline} can be shown on a multi-window time bar.
     *
     * @param timeline The {@link Timeline} to check.
     * @param window A scratch {@link Timeline.Window} instance.
     * @return Whether the specified timeline can be shown on a multi-window time bar.
     */
    private static boolean canShowMultiWindowTimeBar(Timeline timeline, Timeline.Window window) {
        if (timeline.getWindowCount() > MAX_WINDOWS_FOR_MULTI_WINDOW_TIME_BAR) {
            return false;
        }
        int windowCount = timeline.getWindowCount();
        for (int i = 0; i < windowCount; i++) {
            if (timeline.getWindow(i, window).durationUs == C.TIME_UNSET) {
                return false;
            }
        }
        return true;
    }

    private final class ComponentListener
            implements Player.EventListener, TimeBar.OnScrubListener, OnClickListener {

        @Override
        public void onScrubStart(TimeBar timeBar, long position) {
            scrubbing = true;
        }

        @Override
        public void onScrubMove(TimeBar timeBar, long position) {
            if (positionView != null) {
                positionView.setText(formatTime(position));
            }
        }

        @Override
        public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
            scrubbing = false;
            if (!canceled && player != null) {
                seekToTimeBarPosition(position);
            }
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updatePlayPauseButton();
            updateProgress();
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            updateRepeatModeButton();
            updateNavigation();
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            updateShuffleButton();
            updateNavigation();
        }

        @Override
        public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
            updateNavigation();
            updateProgress();
        }

        @Override
        public void onTimelineChanged(
                Timeline timeline, @Nullable Object manifest, @Player.TimelineChangeReason int reason) {
            updateNavigation();
            updateTimeBarMode();
            updateProgress();
        }

        @Override
        public void onClick(View view) {
            if (player != null) {
                if (nextButton == view) {
                    next();
                } else if (previousButton == view) {
                    previous();
                } else if (fastForwardButton == view) {
                    fastForward();
                } else if (rewindButton == view) {
                    rewind();
                } else if (playPauseButton == view && !isPlaying()) {
                    if (player.getPlaybackState() == Player.STATE_IDLE) {
                        if (playbackPreparer != null) {
                            playbackPreparer.preparePlayback();
                        }
                    } else if (player.getPlaybackState() == Player.STATE_ENDED) {
                        controlDispatcher.dispatchSeekTo(player, player.getCurrentWindowIndex(), C.TIME_UNSET);
                    }
                    controlDispatcher.dispatchSetPlayWhenReady(player, true);
                } else if (playPauseButton == view && isPlaying()) {
                    controlDispatcher.dispatchSetPlayWhenReady(player, false);
                } else if (repeatToggleButton == view) {
                    controlDispatcher.dispatchSetRepeatMode(
                            player, RepeatModeUtil.getNextRepeatMode(player.getRepeatMode(), repeatToggleModes));
                } else if (shuffleButton == view) {
                    controlDispatcher.dispatchSetShuffleModeEnabled(player, !player.getShuffleModeEnabled());
                }
            }
        }
    }
}
```

**The Custom PlayerView**
```
package com.meetkai.app.and.customviews;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.DiscontinuityReason;
import com.google.android.exoplayer2.Player.VideoComponent;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsLoader;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.ResizeMode;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.ui.spherical.SingleTapListener;
import com.google.android.exoplayer2.ui.spherical.SphericalSurfaceView;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.RepeatModeUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.meetkai.app.and.R;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * A high level view for {@link Player} media playbacks. It displays video, subtitles and album art
 * during playback, and displays playback controls using a {@link PlayerControlView}.
 *
 * <p>A PlayerView can be customized by setting attributes (or calling corresponding methods),
 * overriding the view's layout file or by specifying a custom view layout file, as outlined below.
 *
 * <h3>Attributes</h3>
 *
 * The following attributes can be set on a PlayerView when used in a layout XML file:
 *
 * <ul>
 *   <li><b>{@code use_artwork}</b> - Whether artwork is used if available in audio streams.
 *       <ul>
 *         <li>Corresponding method: {@link #setUseArtwork(boolean)}
 *         <li>Default: {@code true}
 *       </ul>
 *   <li><b>{@code default_artwork}</b> - Default artwork to use if no artwork available in audio
 *       streams.
 *       <ul>
 *         <li>Corresponding method: {@link #setDefaultArtwork(Drawable)}
 *         <li>Default: {@code null}
 *       </ul>
 *   <li><b>{@code use_controller}</b> - Whether the playback controls can be shown.
 *       <ul>
 *         <li>Corresponding method: {@link #setUseController(boolean)}
 *         <li>Default: {@code true}
 *       </ul>
 *   <li><b>{@code hide_on_touch}</b> - Whether the playback controls are hidden by touch events.
 *       <ul>
 *         <li>Corresponding method: {@link #setControllerHideOnTouch(boolean)}
 *         <li>Default: {@code true}
 *       </ul>
 *   <li><b>{@code auto_show}</b> - Whether the playback controls are automatically shown when
 *       playback starts, pauses, ends, or fails. If set to false, the playback controls can be
 *       manually operated with {@link #showController()} and {@link #hideController()}.
 *       <ul>
 *         <li>Corresponding method: {@link #setControllerAutoShow(boolean)}
 *         <li>Default: {@code true}
 *       </ul>
 *   <li><b>{@code hide_during_ads}</b> - Whether the playback controls are hidden during ads.
 *       Controls are always shown during ads if they are enabled and the player is paused.
 *       <ul>
 *         <li>Corresponding method: {@link #setControllerHideDuringAds(boolean)}
 *         <li>Default: {@code true}
 *       </ul>
 *   <li><b>{@code show_buffering}</b> - Whether the buffering spinner is displayed when the player
 *       is buffering. Valid values are {@code never}, {@code when_playing} and {@code always}.
 *       <ul>
 *         <li>Corresponding method: {@link #setShowBuffering(int)}
 *         <li>Default: {@code never}
 *       </ul>
 *   <li><b>{@code resize_mode}</b> - Controls how video and album art is resized within the view.
 *       Valid values are {@code fit}, {@code fixed_width}, {@code fixed_height} and {@code fill}.
 *       <ul>
 *         <li>Corresponding method: {@link #setResizeMode(int)}
 *         <li>Default: {@code fit}
 *       </ul>
 *   <li><b>{@code surface_type}</b> - The type of surface view used for video playbacks. Valid
 *       values are {@code surface_view}, {@code texture_view}, {@code spherical_view} and {@code
 *       none}. Using {@code none} is recommended for audio only applications, since creating the
 *       surface can be expensive. Using {@code surface_view} is recommended for video applications.
 *       Note, TextureView can only be used in a hardware accelerated window. When rendered in
 *       software, TextureView will draw nothing.
 *       <ul>
 *         <li>Corresponding method: None
 *         <li>Default: {@code surface_view}
 *       </ul>
 *   <li><b>{@code shutter_background_color}</b> - The background color of the {@code exo_shutter}
 *       view.
 *       <ul>
 *         <li>Corresponding method: {@link #setShutterBackgroundColor(int)}
 *         <li>Default: {@code unset}
 *       </ul>
 *   <li><b>{@code keep_content_on_player_reset}</b> - Whether the currently displayed video frame
 *       or media artwork is kept visible when the player is reset.
 *       <ul>
 *         <li>Corresponding method: {@link #setKeepContentOnPlayerReset(boolean)}
 *         <li>Default: {@code false}
 *       </ul>
 *   <li><b>{@code player_layout_id}</b> - Specifies the id of the layout to be inflated. See below
 *       for more details.
 *       <ul>
 *         <li>Corresponding method: None
 *         <li>Default: {@code R.layout.exo_player_view}
 *       </ul>
 *   <li><b>{@code controller_layout_id}</b> - Specifies the id of the layout resource to be
 *       inflated by the child {@link PlayerControlView}. See below for more details.
 *       <ul>
 *         <li>Corresponding method: None
 *         <li>Default: {@code R.layout.exo_player_control_view}
 *       </ul>
 *   <li>All attributes that can be set on a {@link PlayerControlView} can also be set on a
 *       PlayerView, and will be propagated to the inflated {@link PlayerControlView} unless the
 *       layout is overridden to specify a custom {@code exo_controller} (see below).
 * </ul>
 *
 * <h3>Overriding the layout file</h3>
 *
 * To customize the layout of PlayerView throughout your app, or just for certain configurations,
 * you can define {@code exo_player_view.xml} layout files in your application {@code res/layout*}
 * directories. These layouts will override the one provided by the ExoPlayer library, and will be
 * inflated for use by PlayerView. The view identifies and binds its children by looking for the
 * following ids:
 *
 * <p>
 *
 * <ul>
 *   <li><b>{@code exo_content_frame}</b> - A frame whose aspect ratio is resized based on the video
 *       or album art of the media being played, and the configured {@code resize_mode}. The video
 *       surface view is inflated into this frame as its first child.
 *       <ul>
 *         <li>Type: {@link AspectRatioFrameLayout}
 *       </ul>
 *   <li><b>{@code exo_shutter}</b> - A view that's made visible when video should be hidden. This
 *       view is typically an opaque view that covers the video surface, thereby obscuring it when
 *       visible. Obscuring the surface in this way also helps to prevent flicker at the start of
 *       playback when {@code surface_type="surface_view"}.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_buffering}</b> - A view that's made visible when the player is buffering.
 *       This view typically displays a buffering spinner or animation.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_subtitles}</b> - Displays subtitles.
 *       <ul>
 *         <li>Type: {@link SubtitleView}
 *       </ul>
 *   <li><b>{@code exo_artwork}</b> - Displays album art.
 *       <ul>
 *         <li>Type: {@link ImageView}
 *       </ul>
 *   <li><b>{@code exo_error_message}</b> - Displays an error message to the user if playback fails.
 *       <ul>
 *         <li>Type: {@link TextView}
 *       </ul>
 *   <li><b>{@code exo_controller_placeholder}</b> - A placeholder that's replaced with the inflated
 *       {@link PlayerControlView}. Ignored if an {@code exo_controller} view exists.
 *       <ul>
 *         <li>Type: {@link View}
 *       </ul>
 *   <li><b>{@code exo_controller}</b> - An already inflated {@link PlayerControlView}. Allows use
 *       of a custom extension of {@link PlayerControlView}. Note that attributes such as {@code
 *       rewind_increment} will not be automatically propagated through to this instance. If a view
 *       exists with this id, any {@code exo_controller_placeholder} view will be ignored.
 *       <ul>
 *         <li>Type: {@link PlayerControlView}
 *       </ul>
 *   <li><b>{@code exo_ad_overlay}</b> - A {@link FrameLayout} positioned on top of the player which
 *       is used to show ad UI (if applicable).
 *       <ul>
 *         <li>Type: {@link FrameLayout}
 *       </ul>
 *   <li><b>{@code exo_overlay}</b> - A {@link FrameLayout} positioned on top of the player which
 *       the app can access via {@link #getOverlayFrameLayout()}, provided for convenience.
 *       <ul>
 *         <li>Type: {@link FrameLayout}
 *       </ul>
 * </ul>
 *
 * <p>All child views are optional and so can be omitted if not required, however where defined they
 * must be of the expected type.
 *
 * <h3>Specifying a custom layout file</h3>
 *
 * Defining your own {@code exo_player_view.xml} is useful to customize the layout of PlayerView
 * throughout your application. It's also possible to customize the layout for a single instance in
 * a layout file. This is achieved by setting the {@code player_layout_id} attribute on a
 * PlayerView. This will cause the specified layout to be inflated instead of {@code
 * exo_player_view.xml} for only the instance on which the attribute is set.
 */
public class FitnessPlayerView extends FrameLayout implements AdsLoader.AdViewProvider {

    // LINT.IfChange
    /**
     * Determines when the buffering view is shown. One of {@link #SHOW_BUFFERING_NEVER}, {@link
     * #SHOW_BUFFERING_WHEN_PLAYING} or {@link #SHOW_BUFFERING_ALWAYS}.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHOW_BUFFERING_NEVER, SHOW_BUFFERING_WHEN_PLAYING, SHOW_BUFFERING_ALWAYS})
    public @interface ShowBuffering {}
    /** The buffering view is never shown. */
    public static final int SHOW_BUFFERING_NEVER = 0;
    /**
     * The buffering view is shown when the player is in the {@link Player#STATE_BUFFERING buffering}
     * state and {@link Player#getPlayWhenReady() playWhenReady} is {@code true}.
     */
    public static final int SHOW_BUFFERING_WHEN_PLAYING = 1;
    /**
     * The buffering view is always shown when the player is in the {@link Player#STATE_BUFFERING
     * buffering} state.
     */
    public static final int SHOW_BUFFERING_ALWAYS = 2;
    // LINT.ThenChange(../../../../../../res/values/attrs.xml)

    // LINT.IfChange
    private static final int SURFACE_TYPE_NONE = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;
    private static final int SURFACE_TYPE_MONO360_VIEW = 3;
    // LINT.ThenChange(../../../../../../res/values/attrs.xml)

    @Nullable
    private final AspectRatioFrameLayout contentFrame;
    private final View shutterView;
    @Nullable private final View surfaceView;
    private final ImageView artworkView;
    private final SubtitleView subtitleView;
    @Nullable private final View bufferingView;
    @Nullable private final TextView errorMessageView;
    @Nullable private final FitnessPlayerControlView controller;
    private final ComponentListener componentListener;
    @Nullable private final FrameLayout adOverlayFrameLayout;
    @Nullable private final FrameLayout overlayFrameLayout;

    private Player player;
    private boolean useController;
    private boolean useArtwork;
    @Nullable private Drawable defaultArtwork;
    private @ShowBuffering int showBuffering;
    private boolean keepContentOnPlayerReset;
    @Nullable private ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider;
    @Nullable private CharSequence customErrorMessage;
    private int controllerShowTimeoutMs;
    private boolean controllerAutoShow;
    private boolean controllerHideDuringAds;
    private boolean controllerHideOnTouch;
    private int textureViewRotation;

    public FitnessPlayerView(Context context) {
        this(context, null);
    }

    public FitnessPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitnessPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            contentFrame = null;
            shutterView = null;
            surfaceView = null;
            artworkView = null;
            subtitleView = null;
            bufferingView = null;
            errorMessageView = null;
            controller = null;
            componentListener = null;
            adOverlayFrameLayout = null;
            overlayFrameLayout = null;
            ImageView logo = new ImageView(context);
            if (Util.SDK_INT >= 23) {
                configureEditModeLogoV23(getResources(), logo);
            } else {
                configureEditModeLogo(getResources(), logo);
            }
            addView(logo);
            return;
        }

        boolean shutterColorSet = false;
        int shutterColor = 0;
        int playerLayoutId = R.layout.exo_player_view;
        boolean useArtwork = true;
        int defaultArtworkId = 0;
        boolean useController = true;
        int surfaceType = SURFACE_TYPE_SURFACE_VIEW;
        int resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
        int controllerShowTimeoutMs = PlayerControlView.DEFAULT_SHOW_TIMEOUT_MS;
        boolean controllerHideOnTouch = true;
        boolean controllerAutoShow = true;
        boolean controllerHideDuringAds = true;
        int showBuffering = SHOW_BUFFERING_NEVER;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PlayerView, 0, 0);
            try {
                shutterColorSet = a.hasValue(R.styleable.PlayerView_shutter_background_color);
                shutterColor = a.getColor(R.styleable.PlayerView_shutter_background_color, shutterColor);
                playerLayoutId = a.getResourceId(R.styleable.PlayerView_player_layout_id, playerLayoutId);
                useArtwork = a.getBoolean(R.styleable.PlayerView_use_artwork, useArtwork);
                defaultArtworkId =
                        a.getResourceId(R.styleable.PlayerView_default_artwork, defaultArtworkId);
                useController = a.getBoolean(R.styleable.PlayerView_use_controller, useController);
                surfaceType = a.getInt(R.styleable.PlayerView_surface_type, surfaceType);
                resizeMode = a.getInt(R.styleable.PlayerView_resize_mode, resizeMode);
                controllerShowTimeoutMs =
                        a.getInt(R.styleable.PlayerView_show_timeout, controllerShowTimeoutMs);
                controllerHideOnTouch =
                        a.getBoolean(R.styleable.PlayerView_hide_on_touch, controllerHideOnTouch);
                controllerAutoShow = a.getBoolean(R.styleable.PlayerView_auto_show, controllerAutoShow);
                showBuffering = a.getInteger(R.styleable.PlayerView_show_buffering, showBuffering);
                keepContentOnPlayerReset =
                        a.getBoolean(
                                R.styleable.PlayerView_keep_content_on_player_reset, keepContentOnPlayerReset);
                controllerHideDuringAds =
                        a.getBoolean(R.styleable.PlayerView_hide_during_ads, controllerHideDuringAds);
            } finally {
                a.recycle();
            }
        }

        LayoutInflater.from(context).inflate(playerLayoutId, this);
        componentListener = new ComponentListener();
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        // Content frame.
        contentFrame = findViewById(R.id.exo_content_frame);
        if (contentFrame != null) {
            setResizeModeRaw(contentFrame, resizeMode);
        }

        // Shutter view.
        shutterView = findViewById(R.id.exo_shutter);
        if (shutterView != null && shutterColorSet) {
            shutterView.setBackgroundColor(shutterColor);
        }

        // Create a surface view and insert it into the content frame, if there is one.
        if (contentFrame != null && surfaceType != SURFACE_TYPE_NONE) {
            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            switch (surfaceType) {
                case SURFACE_TYPE_TEXTURE_VIEW:
                    surfaceView = new TextureView(context);
                    break;
                case SURFACE_TYPE_MONO360_VIEW:
                    Assertions.checkState(Util.SDK_INT >= 15);
                    SphericalSurfaceView sphericalSurfaceView = new SphericalSurfaceView(context);
                    sphericalSurfaceView.setSurfaceListener(componentListener);
                    sphericalSurfaceView.setSingleTapListener(componentListener);
                    surfaceView = sphericalSurfaceView;
                    break;
                default:
                    surfaceView = new SurfaceView(context);
                    break;
            }
            surfaceView.setLayoutParams(params);
            contentFrame.addView(surfaceView, 0);
        } else {
            surfaceView = null;
        }

        // Ad overlay frame layout.
        adOverlayFrameLayout = findViewById(R.id.exo_ad_overlay);

        // Overlay frame layout.
        overlayFrameLayout = findViewById(R.id.exo_overlay);

        // Artwork view.
        artworkView = findViewById(R.id.exo_artwork);
        this.useArtwork = useArtwork && artworkView != null;
        if (defaultArtworkId != 0) {
            defaultArtwork = ContextCompat.getDrawable(getContext(), defaultArtworkId);
        }

        // Subtitle view.
        subtitleView = findViewById(R.id.exo_subtitles);
        if (subtitleView != null) {
            subtitleView.setUserDefaultStyle();
            subtitleView.setUserDefaultTextSize();
        }

        // Buffering view.
        bufferingView = findViewById(R.id.exo_buffering);
        if (bufferingView != null) {
            bufferingView.setVisibility(View.GONE);
        }
        this.showBuffering = showBuffering;

        // Error message view.
        errorMessageView = findViewById(R.id.exo_error_message);
        if (errorMessageView != null) {
            errorMessageView.setVisibility(View.GONE);
        }

        // Playback control view.
        FitnessPlayerControlView customController = findViewById(R.id.exo_controller);
        View controllerPlaceholder = findViewById(R.id.exo_controller_placeholder);
        if (customController != null) {
            this.controller = customController;
        } else if (controllerPlaceholder != null) {
            // Propagate attrs as playbackAttrs so that PlayerControlView's custom attributes are
            // transferred, but standard FrameLayout attributes (e.g. background) are not.
            this.controller = new FitnessPlayerControlView(context, null, 0, attrs);
            controller.setLayoutParams(controllerPlaceholder.getLayoutParams());
            ViewGroup parent = ((ViewGroup) controllerPlaceholder.getParent());
            int controllerIndex = parent.indexOfChild(controllerPlaceholder);
            parent.removeView(controllerPlaceholder);
            parent.addView(controller, controllerIndex);
        } else {
            this.controller = null;
        }
        this.controllerShowTimeoutMs = controller != null ? controllerShowTimeoutMs : 0;
        this.controllerHideOnTouch = controllerHideOnTouch;
        this.controllerAutoShow = controllerAutoShow;
        this.controllerHideDuringAds = controllerHideDuringAds;
        this.useController = useController && controller != null;
        hideController();
    }

    /**
     * Switches the view targeted by a given {@link Player}.
     *
     * @param player The player whose target view is being switched.
     * @param oldPlayerView The old view to detach from the player.
     * @param newPlayerView The new view to attach to the player.
     */
    public static void switchTargetView(
            Player player, @Nullable PlayerView oldPlayerView, @Nullable PlayerView newPlayerView) {
        if (oldPlayerView == newPlayerView) {
            return;
        }
        // We attach the new view before detaching the old one because this ordering allows the player
        // to swap directly from one surface to another, without transitioning through a state where no
        // surface is attached. This is significantly more efficient and achieves a more seamless
        // transition when using platform provided video decoders.
        if (newPlayerView != null) {
            newPlayerView.setPlayer(player);
        }
        if (oldPlayerView != null) {
            oldPlayerView.setPlayer(null);
        }
    }

    /** Returns the player currently set on this view, or null if no player is set. */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set the {@link Player} to use.
     *
     * <p>To transition a {@link Player} from targeting one view to another, it's recommended to use
     * {@link #switchTargetView(Player, PlayerView, PlayerView)} rather than this method. If you do
     * wish to use this method directly, be sure to attach the player to the new view <em>before</em>
     * calling {@code setPlayer(null)} to detach it from the old one. This ordering is significantly
     * more efficient and may allow for more seamless transitions.
     *
     * @param player The {@link Player} to use, or {@code null} to detach the current player. Only
     *     players which are accessed on the main thread are supported ({@code
     *     player.getApplicationLooper() == Looper.getMainLooper()}).
     */
    public void setPlayer(@Nullable Player player) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper());
        Assertions.checkArgument(
                player == null || player.getApplicationLooper() == Looper.getMainLooper());
        if (this.player == player) {
            return;
        }
        if (this.player != null) {
            this.player.removeListener(componentListener);
            Player.VideoComponent oldVideoComponent = this.player.getVideoComponent();
            if (oldVideoComponent != null) {
                oldVideoComponent.removeVideoListener(componentListener);
                if (surfaceView instanceof TextureView) {
                    oldVideoComponent.clearVideoTextureView((TextureView) surfaceView);
                } else if (surfaceView instanceof SphericalSurfaceView) {
                    ((SphericalSurfaceView) surfaceView).setVideoComponent(null);
                } else if (surfaceView instanceof SurfaceView) {
                    oldVideoComponent.clearVideoSurfaceView((SurfaceView) surfaceView);
                }
            }
            Player.TextComponent oldTextComponent = this.player.getTextComponent();
            if (oldTextComponent != null) {
                oldTextComponent.removeTextOutput(componentListener);
            }
        }
        this.player = player;
        if (useController) {
            controller.setPlayer(player);
        }
        if (subtitleView != null) {
            subtitleView.setCues(null);
        }
        updateBuffering();
        updateErrorMessage();
        updateForCurrentTrackSelections(/* isNewPlayer= */ true);
        if (player != null) {
            Player.VideoComponent newVideoComponent = player.getVideoComponent();
            if (newVideoComponent != null) {
                if (surfaceView instanceof TextureView) {
                    newVideoComponent.setVideoTextureView((TextureView) surfaceView);
                } else if (surfaceView instanceof SphericalSurfaceView) {
                    ((SphericalSurfaceView) surfaceView).setVideoComponent(newVideoComponent);
                } else if (surfaceView instanceof SurfaceView) {
                    newVideoComponent.setVideoSurfaceView((SurfaceView) surfaceView);
                }
                newVideoComponent.addVideoListener(componentListener);
            }
            Player.TextComponent newTextComponent = player.getTextComponent();
            if (newTextComponent != null) {
                newTextComponent.addTextOutput(componentListener);
            }
            player.addListener(componentListener);
            maybeShowController(false);
        } else {
            hideController();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (surfaceView instanceof SurfaceView) {
            // Work around https://github.com/google/ExoPlayer/issues/3160.
            surfaceView.setVisibility(visibility);
        }
    }

    /**
     * Sets the {@link ResizeMode}.
     *
     * @param resizeMode The {@link ResizeMode}.
     */
    public void setResizeMode(@ResizeMode int resizeMode) {
        Assertions.checkState(contentFrame != null);
        contentFrame.setResizeMode(resizeMode);
    }

    /** Returns the {@link ResizeMode}. */
    public @ResizeMode int getResizeMode() {
        Assertions.checkState(contentFrame != null);
        return contentFrame.getResizeMode();
    }

    /** Returns whether artwork is displayed if present in the media. */
    public boolean getUseArtwork() {
        return useArtwork;
    }

    /**
     * Sets whether artwork is displayed if present in the media.
     *
     * @param useArtwork Whether artwork is displayed.
     */
    public void setUseArtwork(boolean useArtwork) {
        Assertions.checkState(!useArtwork || artworkView != null);
        if (this.useArtwork != useArtwork) {
            this.useArtwork = useArtwork;
            updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }
    }

    /** Returns the default artwork to display. */
    public @Nullable Drawable getDefaultArtwork() {
        return defaultArtwork;
    }

    /**
     * Sets the default artwork to display if {@code useArtwork} is {@code true} and no artwork is
     * present in the media.
     *
     * @param defaultArtwork the default artwork to display.
     * @deprecated use (@link {@link #setDefaultArtwork(Drawable)} instead.
     */
    @Deprecated
    public void setDefaultArtwork(@Nullable Bitmap defaultArtwork) {
        setDefaultArtwork(
                defaultArtwork == null ? null : new BitmapDrawable(getResources(), defaultArtwork));
    }

    /**
     * Sets the default artwork to display if {@code useArtwork} is {@code true} and no artwork is
     * present in the media.
     *
     * @param defaultArtwork the default artwork to display
     */
    public void setDefaultArtwork(@Nullable Drawable defaultArtwork) {
        if (this.defaultArtwork != defaultArtwork) {
            this.defaultArtwork = defaultArtwork;
            updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }
    }

    /** Returns whether the playback controls can be shown. */
    public boolean getUseController() {
        return useController;
    }

    /**
     * Sets whether the playback controls can be shown. If set to {@code false} the playback controls
     * are never visible and are disconnected from the player.
     *
     * @param useController Whether the playback controls can be shown.
     */
    public void setUseController(boolean useController) {
        Assertions.checkState(!useController || controller != null);
        if (this.useController == useController) {
            return;
        }
        this.useController = useController;
        if (useController) {
            controller.setPlayer(player);
        } else if (controller != null) {
            controller.hide();
            controller.setPlayer(null);
        }
    }

    /**
     * Sets the background color of the {@code exo_shutter} view.
     *
     * @param color The background color.
     */
    public void setShutterBackgroundColor(int color) {
        if (shutterView != null) {
            shutterView.setBackgroundColor(color);
        }
    }

    /**
     * Sets whether the currently displayed video frame or media artwork is kept visible when the
     * player is reset. A player reset is defined to mean the player being re-prepared with different
     * media, the player transitioning to unprepared media, {@link Player#stop(boolean)} being called
     * with {@code reset=true}, or the player being replaced or cleared by calling {@link
     * #setPlayer(Player)}.
     *
     * <p>If enabled, the currently displayed video frame or media artwork will be kept visible until
     * the player set on the view has been successfully prepared with new media and loaded enough of
     * it to have determined the available tracks. Hence enabling this option allows transitioning
     * from playing one piece of media to another, or from using one player instance to another,
     * without clearing the view's content.
     *
     * <p>If disabled, the currently displayed video frame or media artwork will be hidden as soon as
     * the player is reset. Note that the video frame is hidden by making {@code exo_shutter} visible.
     * Hence the video frame will not be hidden if using a custom layout that omits this view.
     *
     * @param keepContentOnPlayerReset Whether the currently displayed video frame or media artwork is
     *     kept visible when the player is reset.
     */
    public void setKeepContentOnPlayerReset(boolean keepContentOnPlayerReset) {
        if (this.keepContentOnPlayerReset != keepContentOnPlayerReset) {
            this.keepContentOnPlayerReset = keepContentOnPlayerReset;
            updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }
    }

    /**
     * Sets whether a buffering spinner is displayed when the player is in the buffering state. The
     * buffering spinner is not displayed by default.
     *
     * @deprecated Use {@link #setShowBuffering(int)}
     * @param showBuffering Whether the buffering icon is displayed
     */
    @Deprecated
    public void setShowBuffering(boolean showBuffering) {
        setShowBuffering(showBuffering ? SHOW_BUFFERING_WHEN_PLAYING : SHOW_BUFFERING_NEVER);
    }

    /**
     * Sets whether a buffering spinner is displayed when the player is in the buffering state. The
     * buffering spinner is not displayed by default.
     *
     * @param showBuffering The mode that defines when the buffering spinner is displayed. One of
     *     {@link #SHOW_BUFFERING_NEVER}, {@link #SHOW_BUFFERING_WHEN_PLAYING} and
     *     {@link #SHOW_BUFFERING_ALWAYS}.
     */
    public void setShowBuffering(@ShowBuffering int showBuffering) {
        if (this.showBuffering != showBuffering) {
            this.showBuffering = showBuffering;
            updateBuffering();
        }
    }

    /**
     * Sets the optional {@link ErrorMessageProvider}.
     *
     * @param errorMessageProvider The error message provider.
     */
    public void setErrorMessageProvider(
            @Nullable ErrorMessageProvider<? super ExoPlaybackException> errorMessageProvider) {
        if (this.errorMessageProvider != errorMessageProvider) {
            this.errorMessageProvider = errorMessageProvider;
            updateErrorMessage();
        }
    }

    /**
     * Sets a custom error message to be displayed by the view. The error message will be displayed
     * permanently, unless it is cleared by passing {@code null} to this method.
     *
     * @param message The message to display, or {@code null} to clear a previously set message.
     */
    public void setCustomErrorMessage(@Nullable CharSequence message) {
        Assertions.checkState(errorMessageView != null);
        customErrorMessage = message;
        updateErrorMessage();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (player != null && player.isPlayingAd()) {
            return super.dispatchKeyEvent(event);
        }
        boolean isDpadWhenControlHidden =
                isDpadKey(event.getKeyCode()) && useController && !controller.isVisible();
        boolean handled =
                isDpadWhenControlHidden || dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event);
        if (handled) {
            maybeShowController(true);
        }
        return handled;
    }

    /**
     * Called to process media key events. Any {@link KeyEvent} can be passed but only media key
     * events will be handled. Does nothing if playback controls are disabled.
     *
     * @param event A key event.
     * @return Whether the key event was handled.
     */
    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        return useController && controller.dispatchMediaKeyEvent(event);
    }

    /** Returns whether the controller is currently visible. */
    public boolean isControllerVisible() {
        return controller != null && controller.isVisible();
    }

    /**
     * Shows the playback controls. Does nothing if playback controls are disabled.
     *
     * <p>The playback controls are automatically hidden during playback after {{@link
     * #getControllerShowTimeoutMs()}}. They are shown indefinitely when playback has not started yet,
     * is paused, has ended or failed.
     */
    public void showController() {
        showController(shouldShowControllerIndefinitely());
    }

    /** Hides the playback controls. Does nothing if playback controls are disabled. */
    public void hideController() {
        if (controller != null) {
            controller.hide();
        }
    }

    /**
     * Returns the playback controls timeout. The playback controls are automatically hidden after
     * this duration of time has elapsed without user input and with playback or buffering in
     * progress.
     *
     * @return The timeout in milliseconds. A non-positive value will cause the controller to remain
     *     visible indefinitely.
     */
    public int getControllerShowTimeoutMs() {
        return controllerShowTimeoutMs;
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input and with playback or buffering in progress.
     *
     * @param controllerShowTimeoutMs The timeout in milliseconds. A non-positive value will cause the
     *     controller to remain visible indefinitely.
     */
    public void setControllerShowTimeoutMs(int controllerShowTimeoutMs) {
        Assertions.checkState(controller != null);
        this.controllerShowTimeoutMs = controllerShowTimeoutMs;
        if (controller.isVisible()) {
            // Update the controller's timeout if necessary.
            showController();
        }
    }

    /** Returns whether the playback controls are hidden by touch events. */
    public boolean getControllerHideOnTouch() {
        return controllerHideOnTouch;
    }

    /**
     * Sets whether the playback controls are hidden by touch events.
     *
     * @param controllerHideOnTouch Whether the playback controls are hidden by touch events.
     */
    public void setControllerHideOnTouch(boolean controllerHideOnTouch) {
        Assertions.checkState(controller != null);
        this.controllerHideOnTouch = controllerHideOnTouch;
    }

    /**
     * Returns whether the playback controls are automatically shown when playback starts, pauses,
     * ends, or fails. If set to false, the playback controls can be manually operated with {@link
     * #showController()} and {@link #hideController()}.
     */
    public boolean getControllerAutoShow() {
        return controllerAutoShow;
    }

    /**
     * Sets whether the playback controls are automatically shown when playback starts, pauses, ends,
     * or fails. If set to false, the playback controls can be manually operated with {@link
     * #showController()} and {@link #hideController()}.
     *
     * @param controllerAutoShow Whether the playback controls are allowed to show automatically.
     */
    public void setControllerAutoShow(boolean controllerAutoShow) {
        this.controllerAutoShow = controllerAutoShow;
    }

    /**
     * Sets whether the playback controls are hidden when ads are playing. Controls are always shown
     * during ads if they are enabled and the player is paused.
     *
     * @param controllerHideDuringAds Whether the playback controls are hidden when ads are playing.
     */
    public void setControllerHideDuringAds(boolean controllerHideDuringAds) {
        this.controllerHideDuringAds = controllerHideDuringAds;
    }

    /**
     * Set the {@link PlayerControlView.VisibilityListener}.
     *
     * @param listener The listener to be notified about visibility changes.
     */
    public void setControllerVisibilityListener(FitnessPlayerControlView.VisibilityListener listener) {
        Assertions.checkState(controller != null);
        controller.setVisibilityListener(listener);
    }

    /**
     * Sets the {@link PlaybackPreparer}.
     *
     * @param playbackPreparer The {@link PlaybackPreparer}.
     */
    public void setPlaybackPreparer(@Nullable PlaybackPreparer playbackPreparer) {
        Assertions.checkState(controller != null);
        controller.setPlaybackPreparer(playbackPreparer);
    }

    /**
     * Sets the {@link ControlDispatcher}.
     *
     * @param controlDispatcher The {@link ControlDispatcher}, or null to use {@link
     *     DefaultControlDispatcher}.
     */
    public void setControlDispatcher(@Nullable ControlDispatcher controlDispatcher) {
        Assertions.checkState(controller != null);
        controller.setControlDispatcher(controlDispatcher);
    }

    /**
     * Sets the rewind increment in milliseconds.
     *
     * @param rewindMs The rewind increment in milliseconds. A non-positive value will cause the
     *     rewind button to be disabled.
     */
    public void setRewindIncrementMs(int rewindMs) {
        Assertions.checkState(controller != null);
        controller.setRewindIncrementMs(rewindMs);
    }

    /**
     * Sets the fast forward increment in milliseconds.
     *
     * @param fastForwardMs The fast forward increment in milliseconds. A non-positive value will
     *     cause the fast forward button to be disabled.
     */
    public void setFastForwardIncrementMs(int fastForwardMs) {
        Assertions.checkState(controller != null);
        controller.setFastForwardIncrementMs(fastForwardMs);
    }

    /**
     * Sets which repeat toggle modes are enabled.
     *
     * @param repeatToggleModes A set of {@link RepeatModeUtil.RepeatToggleModes}.
     */
    public void setRepeatToggleModes(@RepeatModeUtil.RepeatToggleModes int repeatToggleModes) {
        Assertions.checkState(controller != null);
        controller.setRepeatToggleModes(repeatToggleModes);
    }

    /**
     * Sets whether the shuffle button is shown.
     *
     * @param showShuffleButton Whether the shuffle button is shown.
     */
    public void setShowShuffleButton(boolean showShuffleButton) {
        Assertions.checkState(controller != null);
        controller.setShowShuffleButton(showShuffleButton);
    }

    /**
     * Sets whether the time bar should show all windows, as opposed to just the current one.
     *
     * @param showMultiWindowTimeBar Whether to show all windows.
     */
    public void setShowMultiWindowTimeBar(boolean showMultiWindowTimeBar) {
        Assertions.checkState(controller != null);
        controller.setShowMultiWindowTimeBar(showMultiWindowTimeBar);
    }

    /**
     * Sets the millisecond positions of extra ad markers relative to the start of the window (or
     * timeline, if in multi-window mode) and whether each extra ad has been played or not. The
     * markers are shown in addition to any ad markers for ads in the player's timeline.
     *
     * @param extraAdGroupTimesMs The millisecond timestamps of the extra ad markers to show, or
     *     {@code null} to show no extra ad markers.
     * @param extraPlayedAdGroups Whether each ad has been played, or {@code null} to show no extra ad
     *     markers.
     */
    public void setExtraAdGroupMarkers(
            @Nullable long[] extraAdGroupTimesMs, @Nullable boolean[] extraPlayedAdGroups) {
        Assertions.checkState(controller != null);
        controller.setExtraAdGroupMarkers(extraAdGroupTimesMs, extraPlayedAdGroups);
    }

    /**
     * Set the {@link AspectRatioFrameLayout.AspectRatioListener}.
     *
     * @param listener The listener to be notified about aspect ratios changes of the video content or
     *     the content frame.
     */
    public void setAspectRatioListener(AspectRatioFrameLayout.AspectRatioListener listener) {
        Assertions.checkState(contentFrame != null);
        contentFrame.setAspectRatioListener(listener);
    }

    /**
     * Gets the view onto which video is rendered. This is a:
     *
     * <ul>
     *   <li>{@link SurfaceView} by default, or if the {@code surface_type} attribute is set to {@code
     *       surface_view}.
     *   <li>{@link TextureView} if {@code surface_type} is {@code texture_view}.
     *   <li>{@link SphericalSurfaceView} if {@code surface_type} is {@code spherical_view}.
     *   <li>{@code null} if {@code surface_type} is {@code none}.
     * </ul>
     *
     * @return The {@link SurfaceView}, {@link TextureView}, {@link SphericalSurfaceView} or {@code
     *     null}.
     */
    public View getVideoSurfaceView() {
        return surfaceView;
    }

    /**
     * Gets the overlay {@link FrameLayout}, which can be populated with UI elements to show on top of
     * the player.
     *
     * @return The overlay {@link FrameLayout}, or {@code null} if the layout has been customized and
     *     the overlay is not present.
     */
    @Nullable
    public FrameLayout getOverlayFrameLayout() {
        return overlayFrameLayout;
    }

    /**
     * Gets the {@link SubtitleView}.
     *
     * @return The {@link SubtitleView}, or {@code null} if the layout has been customized and the
     *     subtitle view is not present.
     */
    public SubtitleView getSubtitleView() {
        return subtitleView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        return performClick();
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return toggleControllerVisibility();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (!useController || player == null) {
            return false;
        }
        maybeShowController(true);
        return true;
    }

    /**
     * Should be called when the player is visible to the user and if {@code surface_type} is {@code
     * spherical_view}. It is the counterpart to {@link #onPause()}.
     *
     * <p>This method should typically be called in {@link Activity#onStart()}, or {@link
     * Activity#onResume()} for API versions &lt;= 23.
     */
    public void onResume() {
        if (surfaceView instanceof SphericalSurfaceView) {
            ((SphericalSurfaceView) surfaceView).onResume();
        }
    }

    /**
     * Should be called when the player is no longer visible to the user and if {@code surface_type}
     * is {@code spherical_view}. It is the counterpart to {@link #onResume()}.
     *
     * <p>This method should typically be called in {@link Activity#onStop()}, or {@link
     * Activity#onPause()} for API versions &lt;= 23.
     */
    public void onPause() {
        if (surfaceView instanceof SphericalSurfaceView) {
            ((SphericalSurfaceView) surfaceView).onPause();
        }
    }

    /**
     * Called when there's a change in the aspect ratio of the content being displayed. The default
     * implementation sets the aspect ratio of the content frame to that of the content, unless the
     * content view is a {@link SphericalSurfaceView} in which case the frame's aspect ratio is
     * cleared.
     *
     * @param contentAspectRatio The aspect ratio of the content.
     * @param contentFrame The content frame, or {@code null}.
     * @param contentView The view that holds the content being displayed, or {@code null}.
     */
    protected void onContentAspectRatioChanged(
            float contentAspectRatio,
            @Nullable AspectRatioFrameLayout contentFrame,
            @Nullable View contentView) {
        if (contentFrame != null) {
            contentFrame.setAspectRatio(
                    contentView instanceof SphericalSurfaceView ? 0 : contentAspectRatio);
        }
    }

    // AdsLoader.AdViewProvider implementation.

    @Override
    public ViewGroup getAdViewGroup() {
        return Assertions.checkNotNull(
                adOverlayFrameLayout, "exo_ad_overlay must be present for ad playback");
    }

    @Override
    public View[] getAdOverlayViews() {
        ArrayList<View> overlayViews = new ArrayList<>();
        if (overlayFrameLayout != null) {
            overlayViews.add(overlayFrameLayout);
        }
        if (controller != null) {
            overlayViews.add(controller);
        }
        return overlayViews.toArray(new View[0]);
    }

    // Internal methods.

    private boolean toggleControllerVisibility() {
        if (!useController || player == null) {
            return false;
        }
        if (!controller.isVisible()) {
            maybeShowController(true);
        } else if (controllerHideOnTouch) {
            controller.hide();
        }
        return true;
    }

    /** Shows the playback controls, but only if forced or shown indefinitely. */
    private void maybeShowController(boolean isForced) {
        if (isPlayingAd() && controllerHideDuringAds) {
            return;
        }
        if (useController) {
            boolean wasShowingIndefinitely = controller.isVisible() && controller.getShowTimeoutMs() <= 0;
            boolean shouldShowIndefinitely = shouldShowControllerIndefinitely();
            if (isForced || wasShowingIndefinitely || shouldShowIndefinitely) {
                showController(shouldShowIndefinitely);
            }
        }
    }

    private boolean shouldShowControllerIndefinitely() {
        if (player == null) {
            return true;
        }
        int playbackState = player.getPlaybackState();
        return controllerAutoShow
                && (playbackState == Player.STATE_IDLE
                || playbackState == Player.STATE_ENDED
                || !player.getPlayWhenReady());
    }

    private void showController(boolean showIndefinitely) {
        if (!useController) {
            return;
        }
        controller.setShowTimeoutMs(showIndefinitely ? 0 : controllerShowTimeoutMs);
        controller.show();
    }

    private boolean isPlayingAd() {
        return player != null && player.isPlayingAd() && player.getPlayWhenReady();
    }

    private void updateForCurrentTrackSelections(boolean isNewPlayer) {
        if (player == null || player.getCurrentTrackGroups().isEmpty()) {
            if (!keepContentOnPlayerReset) {
                hideArtwork();
                closeShutter();
            }
            return;
        }

        if (isNewPlayer && !keepContentOnPlayerReset) {
            // Hide any video from the previous player.
            closeShutter();
        }

        TrackSelectionArray selections = player.getCurrentTrackSelections();
        for (int i = 0; i < selections.length; i++) {
            if (player.getRendererType(i) == C.TRACK_TYPE_VIDEO && selections.get(i) != null) {
                // Video enabled so artwork must be hidden. If the shutter is closed, it will be opened in
                // onRenderedFirstFrame().
                hideArtwork();
                return;
            }
        }

        // Video disabled so the shutter must be closed.
        closeShutter();
        // Display artwork if enabled and available, else hide it.
        if (useArtwork) {
            for (int i = 0; i < selections.length; i++) {
                TrackSelection selection = selections.get(i);
                if (selection != null) {
                    for (int j = 0; j < selection.length(); j++) {
                        Metadata metadata = selection.getFormat(j).metadata;
                        if (metadata != null && setArtworkFromMetadata(metadata)) {
                            return;
                        }
                    }
                }
            }
            if (setDrawableArtwork(defaultArtwork)) {
                return;
            }
        }
        // Artwork disabled or unavailable.
        hideArtwork();
    }

    private boolean setArtworkFromMetadata(Metadata metadata) {
        for (int i = 0; i < metadata.length(); i++) {
            Metadata.Entry metadataEntry = metadata.get(i);
            if (metadataEntry instanceof ApicFrame) {
                byte[] bitmapData = ((ApicFrame) metadataEntry).pictureData;
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
                return setDrawableArtwork(new BitmapDrawable(getResources(), bitmap));
            }
        }
        return false;
    }

    private boolean setDrawableArtwork(@Nullable Drawable drawable) {
        if (drawable != null) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            if (drawableWidth > 0 && drawableHeight > 0) {
                float artworkAspectRatio = (float) drawableWidth / drawableHeight;
                onContentAspectRatioChanged(artworkAspectRatio, contentFrame, artworkView);
                artworkView.setImageDrawable(drawable);
                artworkView.setVisibility(VISIBLE);
                return true;
            }
        }
        return false;
    }

    private void hideArtwork() {
        if (artworkView != null) {
            artworkView.setImageResource(android.R.color.transparent); // Clears any bitmap reference.
            artworkView.setVisibility(INVISIBLE);
        }
    }

    private void closeShutter() {
        if (shutterView != null) {
            shutterView.setVisibility(View.VISIBLE);
        }
    }

    private void updateBuffering() {
        if (bufferingView != null) {
            boolean showBufferingSpinner =
                    player != null
                            && player.getPlaybackState() == Player.STATE_BUFFERING
                            && (showBuffering == SHOW_BUFFERING_ALWAYS
                            || (showBuffering == SHOW_BUFFERING_WHEN_PLAYING && player.getPlayWhenReady()));
            bufferingView.setVisibility(showBufferingSpinner ? View.VISIBLE : View.GONE);
        }
    }

    private void updateErrorMessage() {
        if (errorMessageView != null) {
            if (customErrorMessage != null) {
                errorMessageView.setText(customErrorMessage);
                errorMessageView.setVisibility(View.VISIBLE);
                return;
            }
            ExoPlaybackException error = null;
            if (player != null
                    && player.getPlaybackState() == Player.STATE_IDLE
                    && errorMessageProvider != null) {
                error = player.getPlaybackError();
            }
            if (error != null) {
                CharSequence errorMessage = errorMessageProvider.getErrorMessage(error).second;
                errorMessageView.setText(errorMessage);
                errorMessageView.setVisibility(View.VISIBLE);
            } else {
                errorMessageView.setVisibility(View.GONE);
            }
        }
    }

    @TargetApi(23)
    private static void configureEditModeLogoV23(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(R.drawable.exo_edit_mode_logo, null));
        logo.setBackgroundColor(resources.getColor(R.color.exo_edit_mode_background_color, null));
    }

    @SuppressWarnings("deprecation")
    private static void configureEditModeLogo(Resources resources, ImageView logo) {
        logo.setImageDrawable(resources.getDrawable(R.drawable.exo_edit_mode_logo));
        logo.setBackgroundColor(resources.getColor(R.color.exo_edit_mode_background_color));
    }

    @SuppressWarnings("ResourceType")
    private static void setResizeModeRaw(AspectRatioFrameLayout aspectRatioFrame, int resizeMode) {
        aspectRatioFrame.setResizeMode(resizeMode);
    }

    /** Applies a texture rotation to a {@link TextureView}. */
    private static void applyTextureViewRotation(TextureView textureView, int textureViewRotation) {
        float textureViewWidth = textureView.getWidth();
        float textureViewHeight = textureView.getHeight();
        if (textureViewWidth == 0 || textureViewHeight == 0 || textureViewRotation == 0) {
            textureView.setTransform(null);
        } else {
            Matrix transformMatrix = new Matrix();
            float pivotX = textureViewWidth / 2;
            float pivotY = textureViewHeight / 2;
            transformMatrix.postRotate(textureViewRotation, pivotX, pivotY);

            // After rotation, scale the rotated texture to fit the TextureView size.
            RectF originalTextureRect = new RectF(0, 0, textureViewWidth, textureViewHeight);
            RectF rotatedTextureRect = new RectF();
            transformMatrix.mapRect(rotatedTextureRect, originalTextureRect);
            transformMatrix.postScale(
                    textureViewWidth / rotatedTextureRect.width(),
                    textureViewHeight / rotatedTextureRect.height(),
                    pivotX,
                    pivotY);
            textureView.setTransform(transformMatrix);
        }
    }

    @SuppressLint("InlinedApi")
    private boolean isDpadKey(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_UP_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_DOWN_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_UP_LEFT
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER;
    }

    private final class ComponentListener
            implements Player.EventListener,
            TextOutput,
            VideoListener,
            OnLayoutChangeListener,
            SphericalSurfaceView.SurfaceListener,
            SingleTapListener {

        // TextOutput implementation

        @Override
        public void onCues(List<Cue> cues) {
            if (subtitleView != null) {
                subtitleView.onCues(cues);
            }
        }

        // VideoListener implementation

        @Override
        public void onVideoSizeChanged(
                int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            float videoAspectRatio =
                    (height == 0 || width == 0) ? 1 : (width * pixelWidthHeightRatio) / height;

            if (surfaceView instanceof TextureView) {
                // Try to apply rotation transformation when our surface is a TextureView.
                if (unappliedRotationDegrees == 90 || unappliedRotationDegrees == 270) {
                    // We will apply a rotation 90/270 degree to the output texture of the TextureView.
                    // In this case, the output video's width and height will be swapped.
                    videoAspectRatio = 1 / videoAspectRatio;
                }
                if (textureViewRotation != 0) {
                    surfaceView.removeOnLayoutChangeListener(this);
                }
                textureViewRotation = unappliedRotationDegrees;
                if (textureViewRotation != 0) {
                    // The texture view's dimensions might be changed after layout step.
                    // So add an OnLayoutChangeListener to apply rotation after layout step.
                    surfaceView.addOnLayoutChangeListener(this);
                }
                applyTextureViewRotation((TextureView) surfaceView, textureViewRotation);
            }

            onContentAspectRatioChanged(videoAspectRatio, contentFrame, surfaceView);
        }

        @Override
        public void onRenderedFirstFrame() {
            if (shutterView != null) {
                shutterView.setVisibility(INVISIBLE);
            }
        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
            updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }

        // Player.EventListener implementation

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            updateBuffering();
            updateErrorMessage();
            if (isPlayingAd() && controllerHideDuringAds) {
                hideController();
            } else {
                maybeShowController(false);
            }
        }

        @Override
        public void onPositionDiscontinuity(@DiscontinuityReason int reason) {
            if (isPlayingAd() && controllerHideDuringAds) {
                hideController();
            }
        }

        // OnLayoutChangeListener implementation

        @Override
        public void onLayoutChange(
                View view,
                int left,
                int top,
                int right,
                int bottom,
                int oldLeft,
                int oldTop,
                int oldRight,
                int oldBottom) {
            applyTextureViewRotation((TextureView) view, textureViewRotation);
        }

        // SphericalSurfaceView.SurfaceTextureListener implementation

        @Override
        public void surfaceChanged(@Nullable Surface surface) {
            if (player != null) {
                VideoComponent videoComponent = player.getVideoComponent();
                if (videoComponent != null) {
                    videoComponent.setVideoSurface(surface);
                }
            }
        }

        // SingleTapListener implementation

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return toggleControllerVisibility();
        }
    }
}
```

