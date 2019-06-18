package View;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class TimerLabel extends Label implements ActionListener {
    int seconds = 0;
    Timer timer;

    public TimerLabel() {
        updateText();
        this.setAlignment(Pos.CENTER);
        this.setStyle(" -fx-text-fill: #00ff00; -fx-font-family: Consolas;");

        timer = new Timer(1000, this);
    }


    public void reset() {
        seconds = 0;
        updateText();
    }

    public void start() {
        seconds = 0;
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        seconds++;
        Platform.runLater(this::updateText);
    }

    private void updateText() {
        TimerLabel.this.setText(getTimeString());
    }

    public String getTimeString() {
        return String.format("%d:%02d",seconds / 60,seconds % 60);
    }

    public boolean isRunning() {
        return timer.isRunning();
    }
}
