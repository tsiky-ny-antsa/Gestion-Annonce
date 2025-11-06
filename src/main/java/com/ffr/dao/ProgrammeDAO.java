package com.ffr.dao;

import com.ffr.models.Programme;
import com.ffr.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProgrammeDAO {

    public List<Programme> getAllProgrammes() throws SQLException {
        List<Programme> programmes = new ArrayList<>();
        String query = "SELECT p.*, a.title as annonce_title " +
                      "FROM programme p " +
                      "LEFT JOIN annonces a ON p.annonce_id = a.id " +
                      "ORDER BY p.date_pro DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                programmes.add(extractProgrammeFromResultSet(rs));
            }
        }
        return programmes;
    }

    public List<Programme> getProgrammesByDate(LocalDate date) throws SQLException {
        List<Programme> programmes = new ArrayList<>();
        String query = "SELECT p.*, a.title as annonce_title " +
                      "FROM programme p " +
                      "LEFT JOIN annonces a ON p.annonce_id = a.id " +
                      "WHERE DATE(p.date_pro) = ? " +
                      "ORDER BY p.date_pro";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, date.toString());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                programmes.add(extractProgrammeFromResultSet(rs));
            }
        }
        return programmes;
    }

    public List<Programme> getProgrammesByAnnonce(Long annonceId) throws SQLException {
        List<Programme> programmes = new ArrayList<>();
        String query = "SELECT p.*, a.title as annonce_title " +
                      "FROM programme p " +
                      "LEFT JOIN annonces a ON p.annonce_id = a.id " +
                      "WHERE p.annonce_id = ? " +
                      "ORDER BY p.date_pro";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, annonceId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                programmes.add(extractProgrammeFromResultSet(rs));
            }
        }
        return programmes;
    }

    public List<Programme> getTodayProgrammes() throws SQLException {
        return getProgrammesByDate(LocalDate.now());
    }

    public boolean createProgramme(Programme programme) throws SQLException {
        String query = "INSERT INTO programme (date_pro, dif1, dif2, dif3, annonce_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, programme.getDatePro().toString());
            stmt.setBoolean(2, programme.isDif1());
            stmt.setBoolean(3, programme.isDif2());
            stmt.setBoolean(4, programme.isDif3());
            stmt.setLong(5, programme.getAnnonceId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateProgramme(Programme programme) throws SQLException {
        String query = "UPDATE programme SET date_pro = ?, dif1 = ?, dif2 = ?, dif3 = ?, etat = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, programme.getDatePro().toString());
            stmt.setBoolean(2, programme.isDif1());
            stmt.setBoolean(3, programme.isDif2());
            stmt.setBoolean(4, programme.isDif3());
            stmt.setBoolean(5, programme.isEtat());
            stmt.setLong(6, programme.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteProgramme(Long id) throws SQLException {
        String query = "DELETE FROM programme WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteOldProgrammes(int daysOld) throws SQLException {
        String query = "DELETE FROM programme WHERE date_pro < datetime('now', '-' || ? || ' days')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, daysOld);
            return stmt.executeUpdate() > 0;
        }
    }

    private Programme extractProgrammeFromResultSet(ResultSet rs) throws SQLException {
        Programme programme = new Programme();
        programme.setId(rs.getLong("id"));
        
        String datePro = rs.getString("date_pro");
        if (datePro != null) {
            programme.setDatePro(LocalDateTime.parse(datePro.replace(" ", "T")));
        }
        
        programme.setDif1(rs.getBoolean("dif1"));
        programme.setDif2(rs.getBoolean("dif2"));
        programme.setDif3(rs.getBoolean("dif3"));
        programme.setEtat(rs.getBoolean("etat"));
        programme.setNbrDif(rs.getInt("nbr_dif"));
        
        long annonceId = rs.getLong("annonce_id");
        if (!rs.wasNull()) {
            programme.setAnnonceId(annonceId);
            programme.setAnnonceTitle(rs.getString("annonce_title"));
        }
        
        return programme;
    }
}
