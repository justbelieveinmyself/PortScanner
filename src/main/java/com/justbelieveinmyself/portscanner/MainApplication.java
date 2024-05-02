package com.justbelieveinmyself.portscanner;

import com.justbelieveinmyself.portscanner.config.ScannerConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 500);
        stage.setTitle("Scanner");
        Window.getWindows().addListener((ListChangeListener<Window>) c -> {
            while (c.next()) {
                for (Window window : c.getAddedSubList()) {
                    if (window instanceof Stage) {
                        ((Stage) window).getIcons().add(new Image(getClass().getResourceAsStream("images/icon-main.png")));
                    }
                }
            }
        });
        stage.setScene(scene);
        stage.show();
        Platform.setImplicitExit(false);

        stage.setOnCloseRequest(event -> {
            ScannerConfig.getConfig().saveConfig();
            Platform.exit();
        });

    }

    public static void main(String[] args) {
        launch();
    }

    public static void showAlertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Oшибка");
        alert.setHeaderText(null);
        alert.setContentText("Произошла ошибка: " + message);
        alert.showAndWait();
    }

}
