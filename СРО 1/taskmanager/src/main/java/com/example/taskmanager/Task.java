package com.example.taskmanager;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty priority = new SimpleStringProperty();
    private final StringProperty dueDate = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Task(String title, String priority, String dueDate, String status) {
        this.title.set(title); this.priority.set(priority); this.dueDate.set(dueDate); this.status.set(status);
    }
    public String getTitle() { return title.get(); }
    public StringProperty titleProperty() { return title; }
    public String getPriority() { return priority.get(); }
    public StringProperty priorityProperty() { return priority; }
    public String getDueDate() { return dueDate.get(); }
    public StringProperty dueDateProperty() { return dueDate; }
    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}
