package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import com.justbelieveinmyself.portscanner.core.PortIterator;
import com.justbelieveinmyself.portscanner.fetchers.FetcherException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import static com.justbelieveinmyself.portscanner.MainApplication.showAlertMessage;

public class SettingsController {

    @FXML
    private TabPane tabPane;

    @FXML
    private CheckBox askConfirmationCheckbox;

    @FXML
    private TextField maxThreadInput;

    @FXML
    private TextField portTimeoutInput;

    @FXML
    private CheckBox showInfoCheckbox;

    @FXML
    private CheckBox skipBroadcastsCheckbox;

    @FXML
    private TextField threadDelayInput;

    @FXML
    private TextArea portTextArea;

    private ScannerConfig scannerConfig = ScannerConfig.getConfig();
    private boolean isCharValid = false;

    @FXML
    public void initialize() {
        askConfirmationCheckbox.setSelected(scannerConfig.askConfirmation);
        maxThreadInput.setText(String.valueOf(scannerConfig.maxThreads));
        portTimeoutInput.setText(String.valueOf(scannerConfig.portTimeout));
        showInfoCheckbox.setSelected(scannerConfig.showInfo);
        skipBroadcastsCheckbox.setSelected(scannerConfig.skipBroadcastAddresses);
        threadDelayInput.setText(String.valueOf(scannerConfig.threadDelay));
        portTextArea.setText(scannerConfig.portString);

        portTextArea.setCache(false);
        portTextArea.setSnapToPixel(false); //TODO: portTextArea not blurred
        portTextArea.addEventFilter(KeyEvent.KEY_TYPED, (event) -> {
            if (!isCharValid) event.consume();
        });
    }

    @FXML
    private void savePreferences(ActionEvent event) {
        try {
            new PortIterator(portTextArea.getText());
        } catch (Exception e) {
            tabPane.getSelectionModel().clearAndSelect(1);
            portTextArea.requestFocus();
            showAlertMessage("Проверьте ввод! Пример: 1,2,6,15-300,6000");
            return;
        }

        try {
            scannerConfig.maxThreads = parseIntValue(maxThreadInput);
            scannerConfig.portTimeout = parseIntValue(portTimeoutInput);
            scannerConfig.threadDelay = parseIntValue(threadDelayInput);
        } catch (NumberFormatException e) {
            showAlertMessage("Проверьте ввод! Введите целое число!");
            return;
        }

        scannerConfig.skipBroadcastAddresses = skipBroadcastsCheckbox.isSelected();
        scannerConfig.askConfirmation = askConfirmationCheckbox.isSelected();
        scannerConfig.showInfo = showInfoCheckbox.isSelected();
        scannerConfig.portString = portTextArea.getText();
        closeWindow(event);
    }

    @FXML
    private void closeWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void validatePorts(KeyEvent event) {
        KeyCode code = event.getCode();

        if (Character.isISOControl(code.getChar().charAt(0))) {
            return;
        }

        String characterString = event.getText();

        char c = characterString.charAt(0);

        this.isCharValid = validateChar(c, portTextArea.getText(), portTextArea.getCaretPosition());
    }

    private static int parseIntValue(TextField textField) {
        try {
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            textField.requestFocus();
            throw e;
        }
    }

    private boolean validateChar(char c, String text, int caretPos) {
        char pc = 0;
        for (int i = caretPos - 1; i >= 0; i--) {
            pc = text.charAt(i);
            if (!Character.isWhitespace(pc)) {
                break;
            }
        }

        boolean isCurDigit = Character.isDigit(c);
        boolean isPrevDigit = Character.isDigit(pc);
        return isPrevDigit && (isCurDigit || c == '-' || c == ',') ||
                isCurDigit && (pc == '-' || pc == ',' || pc == 0) ||
                Character.isWhitespace(c) && pc == ',';
    }

}
