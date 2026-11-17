package kz.atu.lab12.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import kz.atu.lab12.model.Event;
import kz.atu.lab12.repository.EventRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class EventController {

    @FXML private TableView<Event> tableEvents;
    @FXML private TableColumn<Event, Number> colId;
    @FXML private TableColumn<Event, String> colTitle;
    @FXML private TableColumn<Event, String> colDate;
    @FXML private TableColumn<Event, Number> colParticipants;

    @FXML private TextField txtTitle;
    @FXML private TextField txtDate;
    @FXML private TextField txtParticipants;
    @FXML private TextField txtSearch;
    @FXML private Label lblStatus;

    private final EventRepository repository = new EventRepository();
    private final ObservableList<Event> items = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().id()));
        colTitle.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().title()));
        colDate.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().eventDate()));
        colParticipants.setCellValueFactory(d -> new ReadOnlyObjectWrapper<>(d.getValue().participants()));

        FilteredList<Event> filtered = new FilteredList<>(items, e -> true);
        tableEvents.setItems(filtered);

        txtSearch.textProperty().addListener((obs, oldV, newV) -> {
            String q = (newV == null) ? "" : newV.trim().toLowerCase();
            filtered.setPredicate(e -> e.title().toLowerCase().contains(q));
        });

        tableEvents.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtTitle.setText(newSel.title());
                txtDate.setText(newSel.eventDate());
                txtParticipants.setText(String.valueOf(newSel.participants()));
            }
        });

        try {
            repository.initializeDatabase();
            refresh();
        } catch (SQLException e) {
            showError("Не удалось открыть базу данных");
        }
    }

    private void refresh() throws SQLException {
        items.setAll(repository.findAll());
        lblStatus.setText("Записей: " + items.size());
    }

    private int validateAndReadParticipants() {
        String title = txtTitle.getText().trim();
        String dateStr = txtDate.getText().trim();

        if (title.isBlank() || dateStr.isBlank()) {
            throw new IllegalArgumentException("Заполните название и дату мероприятия");
        }

        try {
            LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Дата должна быть в формате ГГГГ-ММ-ДД (например, 2026-11-20)");
        }

        int participants;
        try {
            participants = Integer.parseInt(txtParticipants.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Количество участников должно быть целым числом");
        }

        if (participants < 0) {
            throw new IllegalArgumentException("Количество участников не может быть отрицательным");
        }
        return participants;
    }

    @FXML
    private void onAdd() {
        try {
            int participants = validateAndReadParticipants();
            repository.insert(txtTitle.getText().trim(), txtDate.getText().trim(), participants);
            clearFields();
            refresh();
        } catch (IllegalArgumentException | SQLException e) {
            showError(e instanceof SQLException ? "Ошибка сохранения в БД" : e.getMessage());
        }
    }

    @FXML
    private void onUpdate() {
        Event selected = tableEvents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите мероприятие в таблице для изменения");
            return;
        }
        try {
            int participants = validateAndReadParticipants();
            int updated = repository.update(selected.id(), txtTitle.getText().trim(), txtDate.getText().trim(), participants);
            if (updated == 0) {
                showError("Запись не найдена в базе данных");
            }
            clearFields();
            refresh();
        } catch (IllegalArgumentException | SQLException e) {
            showError(e instanceof SQLException ? "Ошибка обновления в БД" : e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Event selected = tableEvents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Выберите мероприятие в таблице для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение удаления");
        confirm.setHeaderText("Удалить мероприятие «" + selected.title() + "»?");
        confirm.setContentText("Это действие нельзя отменить.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                repository.delete(selected.id());
                clearFields();
                refresh();
            } catch (SQLException e) {
                showError("Ошибка при удалении из БД");
            }
        }
    }

    @FXML
    private void onRefresh() {
        try {
            txtSearch.clear();
            clearFields();
            refresh();
        } catch (SQLException e) {
            showError("Ошибка чтения данных из БД");
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtDate.clear();
        txtParticipants.clear();
        tableEvents.getSelectionModel().clearSelection();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}