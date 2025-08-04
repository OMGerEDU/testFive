package test.five.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import test.five.display.AndroidTvDisplayAdjuster;
import test.five.display.DisplayAdjuster;

/**
 * Android TV style activity that adjusts display settings using remote friendly widgets.
 */
public class AndroidTvActivity extends Activity {
    private final DisplayAdjuster adjuster = new AndroidTvDisplayAdjuster();
    private SeekBar brightness;
    private SeekBar colorTemp;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        brightness = new SeekBar();
        brightness.setFocusable(true);
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjuster.setBrightness(progress / 100.0);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        brightness.setProgress((int)(DisplayAdjuster.DEFAULT_BRIGHTNESS * 100));

        colorTemp = new SeekBar();
        colorTemp.setFocusable(true);
        colorTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                adjuster.setColorTemperature(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        colorTemp.setProgress(DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE);

        reset = new Button();
        reset.setFocusable(true);
        reset.setOnClickListener(v -> {
            adjuster.setBrightness(DisplayAdjuster.DEFAULT_BRIGHTNESS);
            adjuster.setColorTemperature(DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE);
            brightness.setProgress((int)(DisplayAdjuster.DEFAULT_BRIGHTNESS * 100));
            colorTemp.setProgress(DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE);
        });
    }
}
