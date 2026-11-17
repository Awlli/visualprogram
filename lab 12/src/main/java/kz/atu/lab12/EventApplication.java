package kz.atu.lab12;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EventApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EventApplication.class.getResource("event-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 720, 520);
        stage.setTitle("Лабораторная работа №12 — Каталог мероприятий (Вариант 13)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}