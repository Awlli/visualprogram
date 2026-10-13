package com.example.taskmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HelloController {
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> titleColumn, priorityColumn, dateColumn, statusColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatusComboBox;
    @FXML private Label statusLabel;

    private final ObservableList<Task> taskData = FXCollections.observableArrayList();
    private FilteredList<Task> filteredData;

    @FXML
    private void initialize() {
        // Добавляем 10 обязательных демонстрационных записей по ТЗ
        taskData.add(new Task("Подготовить отчет по СРО", "Высокий", "2026-10-20", "В процессе"));
        taskData.add(new Task("Загрузить код на GitHub", "Высокий", "2026-10-15", "Новая"));
        taskData.add(new Task("Повторить лекции по JavaFX", "Средний", "2026-10-18", "Завершена"));
        taskData.add(new Task("Настроить Scene Builder", "Низкий", "2026-10-25", "Новая"));
        taskData.add(new Task("Проверить валидацию полей", "Высокий", "2026-10-16", "В процессе"));
        taskData.add(new Task("Исправить ошибки в FXML", "Высокий", "2026-10-17", "Новая"));
        taskData.add(new Task("Оформить слайды презентации", "Средний", "2026-10-22", "Новая"));
        taskData.add(new Task("Протестировать обработку ошибок", "Высокий", "2026-10-14", "Завершена"));
        taskData.add(new Task("Написать файл README.md", "Низкий", "2026-10-23", "Новая"));
        taskData.add(new Task("Сдать СРО №1 преподавателю", "Высокий", "2026-10-26", "В процессе"));

        titleColumn.setCellValueFactory(c -> c.getValue().titleProperty());
        priorityColumn.setCellValueFactory(c -> c.getValue().priorityProperty());
        dateColumn.setCellValueFactory(c -> c.getValue().dueDateProperty());
        statusColumn.setCellValueFactory(c -> c.getValue().statusProperty());

        filteredData = new FilteredList<>(taskData, p -> true);
        taskTable.setItems(filteredData);
        filterStatusComboBox.setItems(FXCollections.observableArrayList("Все", "Новая", "В процессе", "Завершена"));
        filterStatusComboBox.setValue("Все");

        // Слушатели для динамического поиска и фильтрации
        searchField.textProperty().addListener((o, old, n) -> updateFilter());
        filterStatusComboBox.valueProperty().addListener((o, old, n) -> updateFilter());
    }

    private void updateFilter() {
        String text = searchField.getText().toLowerCase();
        String status = filterStatusComboBox.getValue();
        filteredData.setPredicate(t -> t.getTitle().toLowerCase().contains(text) &&
                (status.equals("Все") || t.getStatus().equals(status)));
        statusLabel.setText("Найдено задач: " + filteredData.size());
    }

    @FXML
    private void handleAddTask() {
        // Кнопка быстрого добавления строки для демонстрации работы таблицы на защите
        taskData.add(new Task("Новая задача пользователя", "Средний", "2026-10-30", "Новая"));
        statusLabel.setText("Задача добавлена в таблицу.");
    }

    @FXML
    private void handleDeleteTask() {
        int idx = taskTable.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            taskData.remove(idx);
            statusLabel.setText("Задача удалена из таблицы.");
        }
    }
}
