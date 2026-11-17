package kz.atu.lab12.repository;

import kz.atu.lab12.model.Event;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private static final String URL = "jdbc:sqlite:events.db";

    public void initializeDatabase() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS events (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                event_date TEXT NOT NULL,
                participants INTEGER NOT NULL CHECK (participants >= 0)
            )
            """;
        try (Connection c = DriverManager.getConnection(URL);
             Statement st = c.createStatement()) {
            st.execute(sql);
        }
    }

    public List<Event> findAll() throws SQLException {
        List<Event> result = new ArrayList<>();
        String sql = "SELECT id, title, event_date, participants FROM events ORDER BY id";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("event_date"),
                        rs.getInt("participants")
                ));
            }
        }
        return result;
    }

    public void insert(String title, String eventDate, int participants) throws SQLException {
        String sql = "INSERT INTO events(title, event_date, participants) VALUES (?, ?, ?)";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, eventDate);
            ps.setInt(3, participants);
            ps.executeUpdate();
        }
    }

    public int update(int id, String title, String eventDate, int participants) throws SQLException {
        String sql = "UPDATE events SET title=?, event_date=?, participants=? WHERE id=?";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, eventDate);
            ps.setInt(3, participants);
            ps.setInt(4, id);
            return ps.executeUpdate();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM events WHERE id=?";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}