package com.josholadele.bakersfield;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.josholadele.bakersfield.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment implements ExoPlayer.EventListener {

    public static String TAG = "Recipe Details";
    public static final String ARG_ITEM = "item";
    public static final String ARG_STEP_POSITION = "step-position";
    static int CURRENT_STEP;


    private Recipe recipe;
    TextView shortDescriptionTextView;
    TextView descriptionTextView;
    TextView nextTextView;
    TextView previousTextView;
    SimpleExoPlayerView mPlayerView;
    SimpleExoPlayer mExoPlayer;
    LinearLayout bottomLayout;
    LinearLayout playerWrapper;

    JSONArray stepsJSON;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    long playerPosition = 0;

    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            if (getArguments().containsKey(ARG_ITEM)) {
                recipe = getArguments().getParcelable(ARG_ITEM);
                CURRENT_STEP = getArguments().getInt(ARG_STEP_POSITION);
                try {
                    stepsJSON = new JSONArray(recipe.getSteps());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_detail, container, false);

        shortDescriptionTextView = (TextView) rootView.findViewById(R.id.tv_short_description);
        descriptionTextView = (TextView) rootView.findViewById(R.id.tv_description);
        nextTextView = (TextView) rootView.findViewById(R.id.next_step);
        previousTextView = (TextView) rootView.findViewById(R.id.previous_step);
        mPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.step_video_player);
        bottomLayout = (LinearLayout) rootView.findViewById(R.id.step_bottom_layout);
//        playerWrapper = (LinearLayout) rootView.findViewById(R.id.player_layout);
        if (savedInstanceState != null) {

            recipe = savedInstanceState.getParcelable("recipe");
            CURRENT_STEP = savedInstanceState.getInt("current step", 0);
            boolean hasVideo = false;
            try {
                stepsJSON = new JSONArray(recipe.getSteps());
                hasVideo = !Utils.isEmpty(stepsJSON.getJSONObject(CURRENT_STEP).getString("videoURL"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            playerPosition = savedInstanceState.getLong("video position", 0);
            boolean isLandScape = getResources().getBoolean(R.bool.is_landscape);
            if (isLandScape) {
                if (hasVideo) {
                    AppCompatActivity compatActivity = (AppCompatActivity) getActivity();

                    if (compatActivity.getSupportActionBar() != null) {
                        compatActivity.getSupportActionBar().hide();
                    }
                }
            } else {
                AppCompatActivity compatActivity = (AppCompatActivity) getActivity();

                if (compatActivity.getSupportActionBar() != null) {
                    compatActivity.getSupportActionBar().show();
                }
            }

        } else

        {
            if (getArguments().containsKey(ARG_ITEM)) {
                recipe = getArguments().getParcelable(ARG_ITEM);
                CURRENT_STEP = getArguments().getInt(ARG_STEP_POSITION);
                try {
                    stepsJSON = new JSONArray(recipe.getSteps());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        nextTextView.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View view) {
                                                setupCurrentStep(CURRENT_STEP + 1);
                                            }
                                        }

        );
        previousTextView.setOnClickListener(new View.OnClickListener()

                                            {
                                                @Override
                                                public void onClick(View view) {
                                                    setupCurrentStep(CURRENT_STEP - 1);
                                                }
                                            }

        );
        // Show the dummy content as text in a TextView.
        if (recipe != null)

        {
            initializeMediaSession();
            setupCurrentStep(CURRENT_STEP);
        }

        return rootView;
    }

    private void setupCurrentStep(int currentStep) {
        if (currentStep == 0) {
            previousTextView.setVisibility(View.GONE);
            nextTextView.setVisibility(View.VISIBLE);
        } else if (currentStep == stepsJSON.length() - 1) {
            previousTextView.setVisibility(View.VISIBLE);
            nextTextView.setVisibility(View.GONE);
        } else {
            previousTextView.setVisibility(View.VISIBLE);
            nextTextView.setVisibility(View.VISIBLE);
        }
        try {
            shortDescriptionTextView.setText(stepsJSON.getJSONObject(currentStep).getString("shortDescription"));
            descriptionTextView.setText(stepsJSON.getJSONObject(currentStep).getString("description"));
            boolean hasVideo = !Utils.isEmpty(stepsJSON.getJSONObject(currentStep).getString("videoURL"));
            initializePlayer(Uri.parse(stepsJSON.getJSONObject(currentStep).getString("videoURL")), currentStep, hasVideo);
            CURRENT_STEP = currentStep;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            outState.putLong("video position", mExoPlayer.getCurrentPosition());
        } else {
            outState.putLong("video position", 0);
        }
        outState.putInt("current step", CURRENT_STEP);
        outState.putParcelable("recipe", recipe);
    }

    private void initializePlayer(Uri mediaUri, int currentStep, boolean hasVideo) {
        if (hasVideo) {
            if (mExoPlayer == null || CURRENT_STEP != currentStep) {
                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
                mPlayerView.setPlayer(mExoPlayer);

                // Set the ExoPlayer.EventListener to this activity.
                mExoPlayer.addListener(this);

                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(getContext(), "BakersField");
                MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                        getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(true);
                mExoPlayer.seekTo(playerPosition);

            }
        } else {
            mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.recipe));
        }
    }

    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    public void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }


    @Override
    public void onPositionDiscontinuity() {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
