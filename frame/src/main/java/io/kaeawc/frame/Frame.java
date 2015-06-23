package io.kaeawc.frame;

import android.support.v4.app.Fragment;
import android.view.Choreographer;

public abstract class Frame extends Fragment implements Choreographer.FrameCallback {

    static final int BUFFER_SIZE = 60;
    long mLastVsync;
    long[] mVsyncBuffer = new long[BUFFER_SIZE];
    int mVsyncIndex = 0;

    protected int mAvailableDataPoints = 0;
    protected float mAverageDuration;
    protected float mMissingPercentage;
    protected float mDroppedPercentage;
    protected float mRenderedFrames;
    protected float mFps;
    protected float mPeak = 0;

    public Frame() {


        mLastVsync = System.currentTimeMillis();

        for (int i = 0; i < BUFFER_SIZE; i++) {
            mVsyncBuffer[i] = 0;
        }
    }

    /*
     * Choreographer callback, called near vsync.
     *
     * @see android.view.Choreographer.FrameCallback#doFrame(long)
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        long now = System.currentTimeMillis();

        mVsyncBuffer[mVsyncIndex] = now - mLastVsync;

        float sum = 0L;
        mAvailableDataPoints = 0;
        float dropped = 0;
        float missing = 0;
        mPeak = 0;

        for (long vsync : mVsyncBuffer) {
            if (vsync <= 0) {
                continue;
            }

            sum += vsync;
            mAvailableDataPoints++;

            if (vsync > mPeak) {
                mPeak = vsync;
            }

            if (vsync > 17) {
                missing++;
            } else if (vsync > 34) {
                dropped++;
            }
        }

        // 1 second / total vsync time for 60 frames
        mAverageDuration = sum / mAvailableDataPoints;
        mMissingPercentage = 100 * (missing / mAvailableDataPoints);
        mDroppedPercentage = 100 * (dropped / mAvailableDataPoints);
        mRenderedFrames = mAvailableDataPoints - (missing + dropped);
        mFps = mAvailableDataPoints * (1000 / sum);

        mLastVsync = now;
        mVsyncIndex++;
        if (mVsyncIndex >= BUFFER_SIZE) {
            mVsyncIndex = 0;
        }

        Choreographer.getInstance().postFrameCallback(this);
    }
}
