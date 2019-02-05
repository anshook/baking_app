package com.udacity.ak.bakingapp.ui;

import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.udacity.ak.bakingapp.R;
import com.udacity.ak.bakingapp.model.Step;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StepFragment extends Fragment {
    private static final String TAG = StepFragment.class.getSimpleName();

    public static final String ARG_STEP_DATA = "step_data";
    private static final String PLAY_MEDIA_URI = "play_media_uri";
    private final String PLAY_POSITION = "play_position";
    private final String PLAY_WHEN_READY = "play_when_ready";

    @BindView(R.id.tv_step_title) TextView mStepTitleView;
    @BindView(R.id.tv_step_description) TextView mStepDescription;
    @BindView(R.id.iv_step_thumbnail) ImageView mStepThumbnail;
    @BindView(R.id.pv_step_video) PlayerView mPlayerView;
    @BindString(R.string.step_title) String mStepTitle;

    private Step step;
    private Integer mStepNumber;
    SimpleExoPlayer mSimpleExoPlayer;
    private boolean playWhenReady = true;
    private long playPosition = -1;

    public StepFragment() {
        // Required empty public constructor
    }

    public static StepFragment newInstance(Step step) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP_DATA, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getParcelable(ARG_STEP_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null){
            playPosition = savedInstanceState.getLong(PLAY_POSITION);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, view);

        populateUI();
        return view;
    }

    private void populateUI(){
        mStepTitleView.setText(step.getShortDescription());
        mStepDescription.setText(step.getDescription());

        if (!TextUtils.isEmpty(step.getVideoURL())) {
            //mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(step.getVideoURL()));
        }
        else if (!TextUtils.isEmpty(step.getThumbnailURL())) {
            mStepThumbnail.setVisibility(View.VISIBLE);
            Picasso.with(getContext())
                    .load(step.getThumbnailURL())
                    .into(mStepThumbnail);
        }
    }

    private void initializePlayer(Uri mediaUri){
        if (mSimpleExoPlayer==null){

            //Create the player
            TrackSelector trackSelector = new DefaultTrackSelector();
            mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector);

            //Bind player to view
            mPlayerView.setPlayer(mSimpleExoPlayer);

            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            // Produce DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                    Util.getUserAgent(getActivity(), "BakingApp"),
                    bandwidthMeter);

            // Create MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                                .createMediaSource(mediaUri);

            // Prepare the player with the source.
            mSimpleExoPlayer.prepare(videoSource);

            if (playPosition>0)
                mSimpleExoPlayer.seekTo(playPosition);
            mSimpleExoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSimpleExoPlayer != null){
            playPosition = mSimpleExoPlayer.getCurrentPosition();
            playWhenReady = mSimpleExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop();
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSimpleExoPlayer == null) {
            if (!TextUtils.isEmpty(step.getVideoURL()))
                initializePlayer(Uri.parse(step.getVideoURL()));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PLAY_POSITION, playPosition);
        outState.putBoolean(PLAY_WHEN_READY , playWhenReady);
    }

}

