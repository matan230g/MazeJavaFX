package View;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimerTask;

public class TimerLabel extends Label implements ActionListener {
    int seconds = 0;
    Timer timer;

    public TimerLabel() {
        this.setText("0:00");
        this.setAlignment(Pos.CENTER);

        timer = new Timer(1000, this);
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
        Platform.runLater(() -> TimerLabel.this.setText(String.format("%d:%02d",seconds / 60,seconds % 60)));
    }
}
