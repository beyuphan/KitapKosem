package com.kitapkosem.dao;

import com.kitapkosem.model.Review;
import com.kitapkosem.model.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

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
     * Yeni bir yorumu/puanı veritabanına ekler.
     *
     * @param review Eklenecek Review nesnesi. Hem rating hem de commentText
     * null olabilir (ama biri dolu olmalı mantığı servlet'te kontrol edilecek).
     * @return Ekleme başarılıysa true, değilse false döner.
     */
    public int addReview(Review review) {

        String sql = "INSERT INTO Reviews (book_id, user_id, rating, comment_text, review_date) VALUES (?, ?, ?, ?, ?)";

        int generatedReviewId = 0;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, review.getBookId());
            statement.setInt(2, review.getUserId());

            if (review.getRating() != null) {
                statement.setInt(3, review.getRating());
            } else {
                statement.setNull(3, Types.INTEGER);
            }

            if (review.getCommentText() != null && !review.getCommentText().trim().isEmpty()) {
                statement.setString(4, review.getCommentText().trim());
            } else {
                statement.setNull(4, Types.VARCHAR);
            }
            if (review.getReviewDate() != null) {
                statement.setTimestamp(5, review.getReviewDate());
            } else {
                statement.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedReviewId = generatedKeys.getInt(1);
                        System.out.println("ReviewDAO: Yorum eklendi, ReviewID: " + generatedReviewId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO: addReview SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        return generatedReviewId;
    }

    public boolean deleteReview(int reviewId, int userId) {

        String sql = "DELETE FROM Reviews WHERE review_id = ? AND user_id = ?";
        boolean rowDeleted = false;

        System.out.println("ReviewDAO: deleteReview çağrıldı. ReviewID: " + reviewId + ", UserID: " + userId);

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, reviewId);
            statement.setInt(2, userId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                rowDeleted = true;
                System.out.println("ReviewDAO: Yorum başarıyla silindi. ReviewID: " + reviewId);

            } else {
                System.out.println("ReviewDAO: Yorum silinemedi (ya bulunamadı ya da kullanıcıya ait değil). ReviewID: " + reviewId + ", UserID: " + userId);
            }

        } catch (SQLException e) {
            System.err.println("ReviewDAO: deleteReview SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        return rowDeleted;
    }

    public List<Review> getReviewsByBookId(int bookId, int loggedInUserId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username, u.profile_avatar_url as reviewer_avatar_url "
                + "FROM Reviews r JOIN Users u ON r.user_id = u.user_id "
                + "WHERE r.book_id = ? "
                + "ORDER BY r.review_date DESC";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            System.out.println("ReviewDAO: getReviewsByBookId çağrıldı. Book ID: " + bookId + ", LoggedInUserID: " + loggedInUserId);
            statement.setInt(1, bookId);
            ResultSet rs = statement.executeQuery();
            System.out.println("ReviewDAO: SQL sorgusu çalıştırıldı (getReviewsByBookId)."); // TEST

            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setBookId(rs.getInt("book_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setUsername(rs.getString("username"));

                Integer rating = (Integer) rs.getObject("rating");
                if (rs.wasNull()) {
                    review.setRating(null);
                } else {
                    review.setRating(rating);
                }

                review.setCommentText(rs.getString("comment_text"));
                review.setReviewDate(rs.getTimestamp("review_date"));

                review.setUsername(rs.getString("username"));
                review.setUserProfileAvatarUrl(rs.getString("reviewer_avatar_url"));
                review.setLikesCount(rs.getInt("likes_count"));

                if (loggedInUserId > 0) {
                    review.setLikedByCurrentUser(hasUserLikedReview(review.getReviewId(), loggedInUserId));
                } else {
                    review.setLikedByCurrentUser(false);
                }

                System.out.println("ReviewDAO: Yorum bulundu -> Yapan: " + review.getUsername()
                        + ", Puan: " + review.getRating()
                        + ", Beğeni: " + review.getLikesCount()
                        + ", MevcutKullaniciBeğendiMi: " + review.isLikedByCurrentUser());
                reviews.add(review);

            }

        } catch (SQLException e) {
            System.err.println("ReviewDAO: addReview SQL Hatası! Mesaj: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
        }
        System.out.println("ReviewDAO: getReviewsByBookId " + reviews.size() + " yorum/puan ile dönüyor.");
        return reviews;
    }

    public double getAverageRatingForBook(int bookId) {
        String sql = "SELECT AVG(rating) as average_rating FROM Reviews WHERE book_id = ? AND rating IS NOT NULL";
        double averageRating = 0.0;

        System.out.println("ReviewDAO: getAverageRatingForBook metodu çağrıldı. Book ID: " + bookId);

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);
            ResultSet rs = statement.executeQuery();
            System.out.println("ReviewDAO: SQL sorgusu çalıştırıldı (getAverageRatingForBook).");

            if (rs.next()) {

                averageRating = rs.getDouble("average_rating");
                if (rs.wasNull()) {
                    averageRating = 0.0;
                }
            }

        } catch (SQLException e) {
            System.err.println("ReviewDAO: getAverageRatingForBook SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("ReviewDAO: getAverageRatingForBook " + averageRating + " ortalama puan ile dönüyor.");
        return averageRating;
    }

    public Review getReviewByUserAndBook(int userId, int bookId) {
        Review review = null;

        String sql = "SELECT r.*, u.username FROM Reviews r JOIN Users u ON r.user_id = u.user_id WHERE r.user_id = ? AND r.book_id = ?";

        System.out.println("ReviewDAO: getReviewByUserAndBook çağrıldı. UserID: " + userId + ", BookID: " + bookId);

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setBookId(rs.getInt("book_id"));
                review.setUserId(rs.getInt("user_id"));

                Integer rating = (Integer) rs.getObject("rating");
                review.setRating(rs.wasNull() ? null : rating);

                review.setCommentText(rs.getString("comment_text"));
                review.setReviewDate(rs.getTimestamp("review_date"));
                review.setUsername(rs.getString("username"));
                System.out.println("ReviewDAO: Mevcut yorum bulundu UserID: " + userId + ", BookID: " + bookId);
            } else {
                System.out.println("ReviewDAO: Mevcut yorum bulunamadı UserID: " + userId + ", BookID: " + bookId);
            }

        } catch (SQLException e) {
            System.err.println("ReviewDAO: getReviewByUserAndBook SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        return review;
    }

    public boolean updateReview(Review review) {
        String sql = "UPDATE Reviews SET rating = ?, comment_text = ?, review_date = CURRENT_TIMESTAMP WHERE review_id = ?";
        boolean rowUpdated = false;

        System.out.println("ReviewDAO: updateReview çağrıldı. ReviewID: " + review.getReviewId());

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            if (review.getRating() != null) {
                statement.setInt(1, review.getRating());
            } else {
                statement.setNull(1, java.sql.Types.INTEGER);
            }

            if (review.getCommentText() != null && !review.getCommentText().trim().isEmpty()) {
                statement.setString(2, review.getCommentText().trim());
            } else {
                statement.setNull(2, java.sql.Types.VARCHAR);
            }

            statement.setInt(3, review.getReviewId());

            rowUpdated = statement.executeUpdate() > 0;
            System.out.println("ReviewDAO: updateReview sonucu: " + rowUpdated);

        } catch (SQLException e) {
            System.err.println("ReviewDAO: updateReview SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        return rowUpdated;
    }

    public List<Review> getReviewsByUserId(int userId) {
        List<Review> userReviews = new ArrayList<>();

        String sql = "SELECT r.*, b.title as book_title "
                + "FROM Reviews r JOIN Books b ON r.book_id = b.book_id "
                + "WHERE r.user_id = ? "
                + "ORDER BY r.review_date DESC";

        System.out.println("ReviewDAO: getReviewsByUserId çağrıldı. UserID: " + userId);

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            System.out.println("ReviewDAO: SQL sorgusu çalıştırıldı (getReviewsByUserId).");

            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setBookId(rs.getInt("book_id"));
                review.setUserId(rs.getInt("user_id"));

                Integer rating = (Integer) rs.getObject("rating");
                review.setRating(rs.wasNull() ? null : rating);

                review.setCommentText(rs.getString("comment_text"));
                review.setReviewDate(rs.getTimestamp("review_date"));

                review.setBookTitle(rs.getString("book_title"));

                userReviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println("ReviewDAO: getReviewsByUserId SQL Hatası! Mesaj: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("ReviewDAO: getReviewsByUserId " + userReviews.size() + " yorum/puan ile dönüyor.");
        return userReviews;
    }

    public boolean hasUserLikedReview(int reviewId, int userId) {
        String sql = "SELECT 1 FROM Review_Likes WHERE review_id = ? AND user_id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Bir yoruma yapılan beğeniyi veritabanına ekler ve Reviews tablosundaki
     * likes_count'u artırır.
     *
     * @param reviewId Beğenilecek yorumun ID'si.
     * @param userId Beğenen kullanıcının ID'si.
     * @return İşlem başarılıysa true, değilse false.
     */
    public boolean likeReview(int reviewId, int userId) {

        String insertLikeSQL = "INSERT INTO Review_Likes (review_id, user_id) VALUES (?, ?)";
        String updateCountSQL = "UPDATE Reviews SET likes_count = likes_count + 1 WHERE review_id = ?";
        Connection connection = null;
        PreparedStatement insertStatement = null;
        PreparedStatement updateStatement = null;
        boolean success = false;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            insertStatement = connection.prepareStatement(insertLikeSQL);
            insertStatement.setInt(1, reviewId);
            insertStatement.setInt(2, userId);
            int rowInserted = insertStatement.executeUpdate();

            if (rowInserted > 0) {

                updateStatement = connection.prepareStatement(updateCountSQL);
                updateStatement.setInt(1, reviewId);
                int rowUpdated = updateStatement.executeUpdate();
                if (rowUpdated > 0) {
                    success = true;
                }
            }

            if (success) {
                connection.commit();
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            e.printStackTrace();
            success = false;
        } finally {
            try {
                if (insertStatement != null) {
                    insertStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (updateStatement != null) {
                    updateStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Bir yoruma yapılan beğeniyi veritabanından kaldırır ve Reviews
     * tablosundaki likes_count'u azaltır.
     *
     * @param reviewId Beğenisi kaldırılacak yorumun ID'si.
     * @param userId Beğeniyi kaldıran kullanıcının ID'si.
     * @return İşlem başarılıysa true, değilse false.
     */
    public boolean unlikeReview(int reviewId, int userId) {
        String deleteLikeSQL = "DELETE FROM Review_Likes WHERE review_id = ? AND user_id = ?";
        String updateCountSQL = "UPDATE Reviews SET likes_count = GREATEST(0, likes_count - 1) WHERE review_id = ?";
        Connection connection = null;
        PreparedStatement deleteStatement = null;
        PreparedStatement updateStatement = null;
        boolean success = false;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            deleteStatement = connection.prepareStatement(deleteLikeSQL);
            deleteStatement.setInt(1, reviewId);
            deleteStatement.setInt(2, userId);
            int rowDeleted = deleteStatement.executeUpdate();

            if (rowDeleted > 0) {
                updateStatement = connection.prepareStatement(updateCountSQL);
                updateStatement.setInt(1, reviewId);
                int rowUpdated = updateStatement.executeUpdate();
                if (rowUpdated > 0) {
                    success = true;
                }
            } else {

            }

            if (success) {
                connection.commit();
            } else {

                connection.rollback();
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            success = false;
        } finally {
            try {
                if (deleteStatement != null) {
                    deleteStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (updateStatement != null) {
                    updateStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    /**
     * Belirli bir yorumun toplam beğeni sayısını getirir. (Bu metot artık
     * doğrudan Review nesnesindeki likesCount'tan okunacağı için veya
     * getReviewsByBookId içinde çekileceği için sık kullanılmayabilir, ama
     * anlık kontrol için faydalı olabilir.)
     *
     * @param reviewId Beğeni sayısı öğrenilmek istenen yorumun ID'si.
     * @return Yorumun beğeni sayısı.
     */
    public int getLikeCountForReview(int reviewId) {
        String sql = "SELECT likes_count FROM Reviews WHERE review_id = ?";
        int likeCount = 0;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    likeCount = rs.getInt("likes_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likeCount;
    }

    public List<Review> getLikedReviewsByUser(int userId) {
        List<Review> likedReviews = new ArrayList<>();
        String sql = "SELECT r.*, u.username as original_commenter_username, u.profile_avatar_url as original_commenter_avatar, "
                + "b.book_id as liked_review_book_id, b.title as liked_review_book_title, rl.created_at as date_liked "
                + "FROM Review_Likes rl "
                + "JOIN Reviews r ON rl.review_id = r.review_id "
                + "JOIN Users u ON r.user_id = u.user_id "
                + // Yorumu yapan kullanıcı
                "JOIN Books b ON r.book_id = b.book_id "
                + // Yorumun yapıldığı kitap
                "WHERE rl.user_id = ? "
                + // Beğeniyi yapan kullanıcı
                "ORDER BY rl.created_at DESC"; // En son beğenilenler en üstte

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Review review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setBookId(rs.getInt("liked_review_book_id")); // Kitabın ID'si
                review.setUserId(rs.getInt("user_id")); // Yorumu asıl yapanın ID'si

                Integer rating = (Integer) rs.getObject("rating");
                review.setRating(rs.wasNull() ? null : rating);

                review.setCommentText(rs.getString("comment_text"));
                review.setReviewDate(rs.getTimestamp("review_date")); // Yorumun asıl yapılma tarihi

                review.setUsername(rs.getString("original_commenter_username")); // Yorumu asıl yapanın adı
                review.setUserProfileAvatarUrl(rs.getString("original_commenter_avatar")); // Yorumu asıl yapanın avatarı
                review.setLikesCount(rs.getInt("likes_count")); // Yorumun kendi beğeni sayısı

                review.setBookTitle(rs.getString("liked_review_book_title")); // Yorumun yapıldığı kitabın başlığı
                review.setDateLiked(rs.getTimestamp("date_liked")); // Bu yorumun NE ZAMAN beğenildiği

                review.setLikedByCurrentUser(true);

                likedReviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likedReviews;
    }

    public Review getReviewById(int reviewId) {
        Review review = null;
        String sql = "SELECT r.*, u.username, u.profile_avatar_url, b.title as book_title "
                + "FROM Reviews r "
                + "JOIN Users u ON r.user_id = u.user_id "
                + "JOIN Books b ON r.book_id = b.book_id "
                + "WHERE r.review_id = ?";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                review = new Review();
                review.setReviewId(rs.getInt("review_id"));
                review.setBookId(rs.getInt("book_id"));
                review.setUserId(rs.getInt("user_id"));
                Integer ratingValue = (Integer) rs.getObject("rating");
                review.setRating(rs.wasNull() ? null : ratingValue);
                review.setCommentText(rs.getString("comment_text"));
                review.setReviewDate(rs.getTimestamp("review_date"));
                review.setLikesCount(rs.getInt("likes_count"));

                review.setUsername(rs.getString("username"));
                review.setUserProfileAvatarUrl(rs.getString("profile_avatar_url"));
                review.setBookTitle(rs.getString("book_title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return review;
    }
}
