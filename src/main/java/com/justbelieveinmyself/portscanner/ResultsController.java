package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.core.ScanningResultList;
import com.justbelieveinmyself.portscanner.core.ScanningResultList.ScanInfo;
import com.justbelieveinmyself.portscanner.core.UserErrorException;
import com.justbelieveinmyself.portscanner.di.Injector;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ResultsController {

    @FXML
    private Label aliveCountLabel;

    @FXML
    private Label feederInfoLabel;

    @FXML
    private Label hostCountLabel;

    @FXML
    private ImageView resultImage;

    @FXML
    private Label scanTimeAverageLabel;

    @FXML
    private Label scanTimeTotalLabel;

    @FXML
    private Label withPortsCountLabel;

    private ScanningResultList scanningResults = MainController.getInjector().require(ScanningResultList.class);

    @FXML
    private void initialize() {
        if (scanningResults.isInfoAvailable()) {
            ScanInfo scanInfo = scanningResults.getScanInfo();

            Image successImage = new Image(getClass().getResourceAsStream("images/icon-completed.png"));
            Image failedImage = new Image(getClass().getResourceAsStream("images/icon-uncompleted.png"));

            resultImage.setImage(scanInfo.isCompletedNormally() ? successImage : failedImage);

            feederInfoLabel.setText(scanningResults.getFeederInfo());

            scanTimeTotalLabel.setText(timeToText(scanInfo.getScanTime()));
            scanTimeAverageLabel.setText(timeToText((double) scanInfo.getScanTime() / scanInfo.getHostCount()));

            hostCountLabel.setText(String.valueOf(scanInfo.getHostCount()));
            aliveCountLabel.setText(String.valueOf(scanInfo.getAliveCount()));
            withPortsCountLabel.setText(String.valueOf(scanInfo.getWithPortsCount()));
        } else {
            throw new UserErrorException("Нет результатов");
        }
    }

    private String timeToText(double scanTime) {
        double seconds = scanTime / 1000;
        double minutes = seconds / 60;
        double hours = minutes / 60;
        NumberFormat format = new DecimalFormat("#.##");
        if (hours >= 1) {
            return format.format(hours) + " ч";
        }
        if (minutes >= 1) {
            return format.format(minutes) + " мин";
        }
        return format.format(seconds) + " сек";
    }

}
