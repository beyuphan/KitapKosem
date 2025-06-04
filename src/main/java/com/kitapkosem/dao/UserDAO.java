/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kitapkosem.dao;

/**
 *
 * @author eyuph
 */
import com.kitapkosem.model.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAO {

    private String jdbcURL = "jdbc:mysql://localhost:3306/kitap_kosem_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private String jdbcUsername = "root";
    private String jdbcPassword = "SENIN_MYSQL_SIFREN";

    // Veritabanı bağlantısını alacak metot
    protected Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return connection;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        User user = null;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setFullName(rs.getString("full_name"));
                user.setBio(rs.getString("bio"));
                user.setProfileAvatarUrl(rs.getString("profile_avatar_url"));
                user.setCoverPhotoUrl(rs.getString("cover_photo_url"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setActive(rs.getBoolean("is_active"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Yeni bir kullanıcıyı veritabanına ekler. Şifrenin bu metoda gelmeden önce
     * hash'lenmiş olması beklenir.
     *
     * * @param user Eklenecek kullanıcı nesnesi (passwordHash alanı dolu
     * olmalı)
     * @return Ekleme başarılıysa true, değilse false döner.
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (username, email, password_hash, is_active) VALUES (?, ?, ?, ?)";
        boolean rowInserted = false;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setBoolean(4, user.isActive());

            rowInserted = statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT user_id, username, email, password_hash, created_at, is_active, full_name, bio, profile_avatar_url, cover_photo_url FROM Users WHERE username = ?";
        User user = null;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setActive(rs.getBoolean("is_active"));
                user.setFullName(rs.getString("full_name"));
                user.setBio(rs.getString("bio"));
                user.setProfileAvatarUrl(rs.getString("profile_avatar_url"));
                user.setCoverPhotoUrl(rs.getString("cover_photo_url"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUserByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM Users WHERE email = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setFullName(rs.getString("full_name"));
                user.setBio(rs.getString("bio"));
                user.setProfileAvatarUrl(rs.getString("profile_avatar_url"));
                user.setCoverPhotoUrl(rs.getString("cover_photo_url"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setActive(rs.getBoolean("is_active"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean updateUserProfile(User user) {
        String sql = "UPDATE Users SET full_name = ?, bio = ?, profile_avatar_url = ?, cover_photo_url = ? WHERE user_id = ?";
        boolean rowUpdated = false;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getFullName());
            statement.setString(2, user.getBio());
            statement.setString(3, user.getProfileAvatarUrl());
            statement.setString(4, user.getCoverPhotoUrl());
            statement.setInt(5, user.getUserId());

            rowUpdated = statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

}
