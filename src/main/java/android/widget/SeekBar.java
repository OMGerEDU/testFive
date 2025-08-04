package android.widget;

import android.view.View;

/** Minimal stub of Android SeekBar. */
public class SeekBar extends View {
    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
        void onStartTrackingTouch(SeekBar seekBar);
        void onStopTrackingTouch(SeekBar seekBar);
    }

    private int progress;
    private OnSeekBarChangeListener listener;

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        this.listener = l;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (listener != null) {
            listener.onProgressChanged(this, progress, true);
        }
    }

    public int getProgress() {
        return progress;
    }
}
