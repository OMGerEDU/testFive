package android.widget;

import android.view.View;

/** Minimal stub of Android Button. */
public class Button extends View {
    public interface OnClickListener {
        void onClick(View v);
    }
    private OnClickListener listener;
    public void setOnClickListener(OnClickListener l) {
        this.listener = l;
    }
    public void performClick() {
        if (listener != null) {
            listener.onClick(this);
        }
    }
}
