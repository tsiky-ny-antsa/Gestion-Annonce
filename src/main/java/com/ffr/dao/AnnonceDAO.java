package com.ffr.dao;

import com.ffr.models.Annonce;
import com.ffr.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnnonceDAO {

    public List<Annonce> getAllAnnonces() throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String query = "SELECT a.*, c.name as category_name, u.username as created_by_username " +
                      "FROM annonces a " +
                      "LEFT JOIN categories c ON a.category_id = c.id " +
                      "LEFT JOIN users u ON a.created_by = u.id " +
                      "ORDER BY a.date_cre DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                annonces.add(extractAnnonceFromResultSet(rs));
            }
        }
        return annonces;
    }

    public Annonce getAnnonceById(Long id) throws SQLException {
        String query = "SELECT a.*, c.name as category_name, u.username as created_by_username " +
                      "FROM annonces a " +
                      "LEFT JOIN categories c ON a.category_id = c.id " +
                      "LEFT JOIN users u ON a.created_by = u.id " +
                      "WHERE a.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractAnnonceFromResultSet(rs);
            }
        }
        return null;
    }

    public Long createAnnonce(Annonce annonce) throws SQLException {
        String query = "INSERT INTO annonces (title, content, category_id, audio_path, prop, type, nbr_prev, created_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, annonce.getTitle());
            stmt.setString(2, annonce.getContent());
            if (annonce.getCategoryId() != null) {
                stmt.setLong(3, annonce.getCategoryId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, annonce.getAudioPath());
            stmt.setString(5, annonce.getProp());
            stmt.setString(6, annonce.getType());
            stmt.setInt(7, annonce.getNbrPrev());
            stmt.setLong(8, annonce.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }
        return null;
    }

    public boolean updateAnnonce(Annonce annonce) throws SQLException {
        String query = "UPDATE annonces SET title = ?, content = ?, category_id = ?, " +
                      "audio_path = ?, prop = ?, type = ?, nbr_prev = ?, date_upd = CURRENT_TIMESTAMP " +
                      "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, annonce.getTitle());
            stmt.setString(2, annonce.getContent());
            if (annonce.getCategoryId() != null) {
                stmt.setLong(3, annonce.getCategoryId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, annonce.getAudioPath());
            stmt.setString(5, annonce.getProp());
            stmt.setString(6, annonce.getType());
            stmt.setInt(7, annonce.getNbrPrev());
            stmt.setLong(8, annonce.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAnnonce(Long id) throws SQLException {
        String query = "DELETE FROM annonces WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Annonce extractAnnonceFromResultSet(ResultSet rs) throws SQLException {
        Annonce annonce = new Annonce();
        annonce.setId(rs.getLong("id"));
        annonce.setTitle(rs.getString("title"));
        annonce.setContent(rs.getString("content"));
        
        long categoryId = rs.getLong("category_id");
        if (!rs.wasNull()) {
            annonce.setCategoryId(categoryId);
            annonce.setCategoryName(rs.getString("category_name"));
        }
        
        annonce.setAudioPath(rs.getString("audio_path"));
        annonce.setProp(rs.getString("prop"));
        annonce.setType(rs.getString("type"));
        annonce.setNbrDif(rs.getInt("nbr_dif"));
        annonce.setNbrPrev(rs.getInt("nbr_prev"));
        
        long createdBy = rs.getLong("created_by");
        if (!rs.wasNull()) {
            annonce.setCreatedBy(createdBy);
            annonce.setCreatedByUsername(rs.getString("created_by_username"));
        }
        
        String dateCre = rs.getString("date_cre");
        if (dateCre != null) {
            annonce.setDateCre(LocalDateTime.parse(dateCre.replace(" ", "T")));
        }
        
        String dateUpd = rs.getString("date_upd");
        if (dateUpd != null) {
            annonce.setDateUpd(LocalDateTime.parse(dateUpd.replace(" ", "T")));
        }
        
        return annonce;
    }
}
