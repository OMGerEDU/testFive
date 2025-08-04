package test.five.ui;

import javax.swing.*;
import java.awt.*;

import test.five.display.DisplayAdjuster;
import test.five.display.WindowsDisplayAdjuster;

/**
 * Simple Swing UI for adjusting display settings on Windows.
 * Uses large, high-contrast components suitable for remote navigation.
 */
public class WindowsDisplayFrame extends JFrame {
    private final DisplayAdjuster adjuster = new WindowsDisplayAdjuster();

    public WindowsDisplayFrame() {
        super("Display Adjuster");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        Font largeFont = new Font("SansSerif", Font.BOLD, 24);

        JLabel brightnessLabel = new JLabel("Brightness");
        brightnessLabel.setFont(largeFont);
        brightnessLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(brightnessLabel, gbc);

        JSlider brightness = new JSlider(0, 100, (int)(DisplayAdjuster.DEFAULT_BRIGHTNESS * 100));
        brightness.setFont(largeFont);
        brightness.setFocusable(true);
        brightness.addChangeListener(e -> adjuster.setBrightness(brightness.getValue() / 100.0));
        gbc.gridy = 1;
        add(brightness, gbc);

        JLabel colorLabel = new JLabel("Color Temp");
        colorLabel.setFont(largeFont);
        colorLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        add(colorLabel, gbc);

        JSlider color = new JSlider(DisplayAdjuster.MIN_COLOR_TEMPERATURE,
                DisplayAdjuster.MAX_COLOR_TEMPERATURE,
                DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE);
        color.setFont(largeFont);
        color.setFocusable(true);
        color.addChangeListener(e -> adjuster.setColorTemperature(color.getValue()));
        gbc.gridy = 3;
        add(color, gbc);

        JButton reset = new JButton("Reset to Safe Defaults");
        reset.setFont(largeFont);
        reset.setBackground(Color.WHITE);
        reset.setForeground(Color.BLACK);
        reset.setFocusable(true);
        reset.addActionListener(e -> {
            adjuster.setBrightness(DisplayAdjuster.DEFAULT_BRIGHTNESS);
            adjuster.setColorTemperature(DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE);
            brightness.setValue((int)(DisplayAdjuster.DEFAULT_BRIGHTNESS * 100));
            color.setValue(DisplayAdjuster.DEFAULT_COLOR_TEMPERATURE);
        });
        gbc.gridy = 4;
        add(reset, gbc);

        getContentPane().setBackground(Color.BLACK);
        setSize(500, 400);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WindowsDisplayFrame().setVisible(true));
    }
}
