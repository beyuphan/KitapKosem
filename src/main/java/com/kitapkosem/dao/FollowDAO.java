package com.kitapkosem.dao;
import com.kitapkosem.model.User; 


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; 
import java.util.List;    

public class FollowDAO {

    private String jdbcURL = "jdbc:mysql://localhost:3306/kitap_kosem_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private String jdbcUsername = "root";
    private String jdbcPassword = "SENIN_MYSQL_SIFREN"; 

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
     * Bir kullanıcının başka bir kullanıcıyı takip etmesini sağlar.
     *
     * @param followerId Takip eden kullanıcının ID'si.
     * @param followingId Takip edilen kullanıcının ID'si.
     * @return Takip işlemi başarılıysa true, değilse false döner.
     */
    public boolean followUser(int followerId, int followingId) {
    if (followerId == followingId) {
        System.out.println("FollowDAO: Kullanıcı kendini takip edemez. followerId: " + followerId); 
        return false; 
    }
    String sql = "INSERT INTO Follows (follower_user_id, following_user_id) VALUES (?, ?)";
    boolean rowInserted = false;
    System.out.println("FollowDAO: followUser çağrıldı. followerId: " + followerId + ", followingId: " + followingId); 

    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setInt(1, followerId);
        statement.setInt(2, followingId);
        rowInserted = statement.executeUpdate() > 0;
        System.out.println("FollowDAO: executeUpdate sonucu (rowInserted): " + rowInserted); 

    } catch (SQLException e) {
        System.err.println("FollowDAO: followUser SQL Hatası! Mesaj: " + e.getMessage()); 
        System.err.println("SQLState: " + e.getSQLState());
        System.err.println("ErrorCode: " + e.getErrorCode()); 
        e.printStackTrace(); 
    }
    return rowInserted;
}

    /**
     * Bir kullanıcının başka bir kullanıcıyı takipten çıkmasını sağlar.
     *
     * @param followerId Takipten çıkan kullanıcının ID'si.
     * @param followingId Takipten çıkılan kullanıcının ID'si.
     * @return Takipten çıkma işlemi başarılıysa true, değilse false döner.
     */
    public boolean unfollowUser(int followerId, int followingId) {
        String sql = "DELETE FROM Follows WHERE follower_user_id = ? AND following_user_id = ?";
        boolean rowDeleted = false;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, followerId);
            statement.setInt(2, followingId);
            rowDeleted = statement.executeUpdate() > 0;

        } catch (SQLException e) {
        }
        return rowDeleted;
    }

    /**
     * Bir kullanıcının başka bir kullanıcıyı takip edip etmediğini kontrol eder.
     *
     * @param followerId Kontrol edilecek takipçi kullanıcının ID'si.
     * @param followingId Kontrol edilecek takip edilen kullanıcının ID'si.
     * @return Takip ediyorsa true, etmiyorsa false döner.
     */
    public boolean isFollowing(int followerId, int followingId) {
        String sql = "SELECT 1 FROM Follows WHERE follower_user_id = ? AND following_user_id = ?";
        boolean isFollowing = false;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, followerId);
            statement.setInt(2, followingId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) { 
                isFollowing = true;
            }

        } catch (SQLException e) {
            
        }
        return isFollowing;
    }

   public int getFollowerCount(int userId) {
        String sql = "SELECT COUNT(*) as follower_count FROM Follows WHERE following_user_id = ?";
        int followerCount = 0;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId); 
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                followerCount = rs.getInt("follower_count");
            }

        } catch (SQLException e) {
        }
        return followerCount;
    }

    /**
     * Belirli bir kullanıcının takip ettiği kişi sayısını döndürür.
     *
     * @param userId Takip ettiği kişi sayısı öğrenilmek istenen kullanıcının ID'si.
     * @return Kullanıcının takip ettiği kişi sayısı.
     */
    public int getFollowingCount(int userId) {
        String sql = "SELECT COUNT(*) as following_count FROM Follows WHERE follower_user_id = ?";
        int followingCount = 0;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId); 
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                followingCount = rs.getInt("following_count");
            }

        } catch (SQLException e) {
        }
        return followingCount;
    }
    
       public List<User> getFollowers(int userId) {
        List<User> followers = new ArrayList<>();
        
        String sql = "SELECT u.user_id, u.username, u.full_name, u.profile_avatar_url " +
                     "FROM Users u JOIN Follows f ON u.user_id = f.follower_user_id " +
                     "WHERE f.following_user_id = ? " +
                     "ORDER BY u.username ASC"; // Kullanıcı adına göre sırala

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                User follower = new User();
                follower.setUserId(rs.getInt("user_id"));
                follower.setUsername(rs.getString("username"));
                follower.setFullName(rs.getString("full_name"));
                follower.setProfileAvatarUrl(rs.getString("profile_avatar_url"));
                followers.add(follower);
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return followers;
    }

    /**
     * Belirli bir kullanıcının takip ettiği kişilerin listesini getirir.
     *
     * @param userId Takip ettikleri listelenecek kullanıcının ID'si.
     * @return Takip edilen User nesnelerinin listesi.
     */
    public List<User> getFollowings(int userId) {
        List<User> followings = new ArrayList<>();
     
        String sql = "SELECT u.user_id, u.username, u.full_name, u.profile_avatar_url " +
                     "FROM Users u JOIN Follows f ON u.user_id = f.following_user_id " +
                     "WHERE f.follower_user_id = ? " +
                     "ORDER BY u.username ASC"; 

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                User following = new User();
                following.setUserId(rs.getInt("user_id"));
                following.setUsername(rs.getString("username"));
                following.setFullName(rs.getString("full_name"));
                following.setProfileAvatarUrl(rs.getString("profile_avatar_url"));
                followings.add(following);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followings;
    }
}