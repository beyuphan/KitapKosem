package com.kitapkosem.dao;

import com.kitapkosem.model.Activity;
import com.kitapkosem.model.User;
import com.kitapkosem.model.Book;
import com.kitapkosem.model.Review;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAO {

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

    /**
     * Yeni bir aktiviteyi veritabanındaki Activities tablosuna ekler.
     *
     * @param activity Eklenecek Activity nesnesi. userId, activityType dolu
     * olmalı. targetItemId ve secondaryTargetItemId aktivite türüne göre null
     * olabilir.
     * @return Ekleme başarılıysa true, değilse false döner.
     */
    public boolean addActivity(Activity activity) {
        String sql = "INSERT INTO Activities (user_id, activity_type, target_item_id, secondary_target_item_id, created_at) VALUES (?, ?, ?, ?, ?)";
        boolean rowInserted = false;

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, activity.getUserId());
            statement.setString(2, activity.getActivityType());

            if (activity.getTargetItemId() != null) {
                statement.setInt(3, activity.getTargetItemId());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            if (activity.getSecondaryTargetItemId() != null) {
                statement.setInt(4, activity.getSecondaryTargetItemId());
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            if (activity.getCreatedAt() != null) {
                statement.setTimestamp(5, activity.getCreatedAt());
            } else {
                statement.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            rowInserted = statement.executeUpdate() > 0;
            if (rowInserted) {
                System.out.println("ActivityDAO: Yeni aktivite loglandı -> UserID: " + activity.getUserId() + ", Type: " + activity.getActivityType());
            }

        } catch (SQLException e) {
            System.err.println("ActivityDAO: addActivity SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        return rowInserted;
    }

    // ActivityDAO.java - getTimelineForUser metodu
    public List<Activity> getTimelineForUser(int loggedInUserId, int limit, int offset) {
        List<Activity> timelineActivities = new ArrayList<>();
        System.out.println("--- ActivityDAO: getTimelineForUser çağrıldı ---");
        System.out.println("ActivityDAO: Giriş Yapan Kullanıcı ID (loggedInUserId): " + loggedInUserId + ", Limit: " + limit);

        List<Integer> followedUserIds = new ArrayList<>();
        String getFollowedUsersSql = "SELECT following_user_id FROM Follows WHERE follower_user_id = ?";
        try (Connection connection = getConnection(); PreparedStatement followedStmt = connection.prepareStatement(getFollowedUsersSql)) {
            followedStmt.setInt(1, loggedInUserId);
            ResultSet rsFollowed = followedStmt.executeQuery();
            while (rsFollowed.next()) {
                followedUserIds.add(rsFollowed.getInt("following_user_id"));
            }
            System.out.println("ActivityDAO: Takip edilen kullanıcı ID'leri: " + followedUserIds);
        } catch (SQLException e) {
            System.err.println("ActivityDAO: Takip edilen kullanıcıları çekerken SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
            return timelineActivities;
        }

        if (followedUserIds.isEmpty()) {
            System.out.println("ActivityDAO: Takip edilen kullanıcı bulunamadı, boş timeline dönülüyor.");
            return timelineActivities;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < followedUserIds.size(); i++) {
            placeholders.append("?");
            if (i < followedUserIds.size() - 1) {
                placeholders.append(",");
            }
        }

        String getActivitiesSql = "SELECT a.*, u.username as actor_username, u.profile_avatar_url as actor_avatar_url "
                + "FROM Activities a JOIN Users u ON a.user_id = u.user_id "
                + "WHERE a.user_id IN (" + placeholders.toString() + ") "
                + "ORDER BY a.created_at DESC LIMIT ? OFFSET ?";

        System.out.println("ActivityDAO: Çalıştırılacak SQL: " + getActivitiesSql);

        try (Connection connection = getConnection(); PreparedStatement activitiesStmt = connection.prepareStatement(getActivitiesSql)) {

            int paramIndex = 1;
            System.out.println("ActivityDAO: Parametreler set ediliyor...");
            for (Integer followedId : followedUserIds) {
                System.out.println("ActivityDAO: Setting param #" + paramIndex + " (followedId): " + followedId);
                activitiesStmt.setInt(paramIndex, followedId);
                paramIndex++;
            }
            System.out.println("ActivityDAO: Setting param #" + paramIndex + " (limit): " + limit);
            activitiesStmt.setInt(paramIndex, limit);
            paramIndex++; // Artırmayı burada yap

            System.out.println("ActivityDAO: Setting param #" + paramIndex + " (offset): " + offset);
            activitiesStmt.setInt(paramIndex, offset);  // Sonra offset
            System.out.println("ActivityDAO: Aktiviteler sorgusu için parametreler set edildi. Takip edilen ID sayısı: " + followedUserIds.size() + ", Limit: " + limit);

            ResultSet rsActivities = activitiesStmt.executeQuery();
            System.out.println("ActivityDAO: Aktiviteler sorgusu çalıştırıldı.");
            int activityCount = 0;
            while (rsActivities.next()) {
                activityCount++;
                Activity activity = new Activity();
                activity.setActivityId(rsActivities.getInt("activity_id"));
                activity.setUserId(rsActivities.getInt("user_id"));
                activity.setActivityType(rsActivities.getString("activity_type"));
                activity.setTargetItemId(rsActivities.getObject("target_item_id") != null ? rsActivities.getInt("target_item_id") : null);
                activity.setSecondaryTargetItemId(rsActivities.getObject("secondary_target_item_id") != null ? rsActivities.getInt("secondary_target_item_id") : null);
                activity.setCreatedAt(rsActivities.getTimestamp("created_at"));
                activity.setActorUsername(rsActivities.getString("actor_username"));
                activity.setActorProfileAvatarUrl(rsActivities.getString("actor_avatar_url"));

                System.out.println("ActivityDAO: Aktivite bulundu -> UserID: " + activity.getUserId() + ", Type: " + activity.getActivityType() + ", Actor: " + activity.getActorUsername()); // LOG 9
                timelineActivities.add(activity);
            }
            System.out.println("ActivityDAO: Toplam " + activityCount + " aktivite bulundu ve listeye eklendi.");

        } catch (SQLException e) {
            System.err.println("ActivityDAO: Aktiviteleri çekerken SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("ActivityDAO: getTimelineForUser metodu " + timelineActivities.size() + " aktivite ile dönüyor.");
        return timelineActivities;
    }
}
