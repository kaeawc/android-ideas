package io.kaeawc.ideas.app.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.kaeawc.frame.Frame;
import io.kaeawc.ideas.app.R;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Frame {

    TextView mSampledText;
    TextView mAverageVsyncText;
    TextView mPeakVsyncText;
    TextView mDroppedFramesText;
    TextView mMissingFrameText;
    TextView mFramesPerSecondText;

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mSampledText = (TextView) view.findViewById(R.id.sampled_value);
        mAverageVsyncText = (TextView) view.findViewById(R.id.average_vsync_value);
        mPeakVsyncText = (TextView) view.findViewById(R.id.peak_vsync_value);
        mDroppedFramesText = (TextView) view.findViewById(R.id.dropped_frames_value);
        mMissingFrameText = (TextView) view.findViewById(R.id.missing_frame_value);
        mFramesPerSecondText = (TextView) view.findViewById(R.id.fps_value);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        Timber.v("onPause unhooking choreographer");
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // If we already have a Surface, we just need to resume the frame notifications.
        Choreographer.getInstance().postFrameCallback(this);
    }

    /*
     * Choreographer callback, called near vsync.
     *
     * @see android.view.Choreographer.FrameCallback#doFrame(long)
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        super.doFrame(frameTimeNanos);

        mSampledText.setText(String.format("%s frames", mAvailableDataPoints));
        mAverageVsyncText.setText(String.format("%.2f ms", mAverageDuration));
        mPeakVsyncText.setText(String.format("%.2f ms", mPeak));
        mFramesPerSecondText.setText(String.format("%.2f FPS", mFps));
        mMissingFrameText.setText(String.format("%.2f %%", mMissingPercentage));
        mDroppedFramesText.setText(String.format("%.2f %%", mDroppedPercentage));
    }
}
