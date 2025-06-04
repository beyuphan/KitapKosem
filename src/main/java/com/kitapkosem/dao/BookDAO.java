package com.kitapkosem.dao;

import com.kitapkosem.model.Book;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    private String jdbcURL = "jdbc:mysql://localhost:3306/kitap_kosem_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "SENIN_MYSQL_SIFREN";

    private ReviewDAO reviewDAO;

    public BookDAO() {
        reviewDAO = new ReviewDAO();
    }

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

    private Book extractBookFromResultSet(ResultSet rs, int loggedInUserId) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setDescription(rs.getString("description"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublisher(rs.getString("publisher"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setCoverImageUrl(rs.getString("cover_image_url"));
        book.setAddedByUserId(rs.getInt("added_by_user_id"));
        book.setCreatedAt(rs.getTimestamp("created_at"));

        if (hasColumn(rs, "uploader_username")) {
            book.setUploaderUsername(rs.getString("uploader_username"));
        }

        if (hasColumn(rs, "likes_count")) {
            book.setLikesCount(rs.getInt("likes_count"));
        }

        if (reviewDAO != null) {
            double averageRating = reviewDAO.getAverageRatingForBook(book.getBookId());
            book.setAverageRating(averageRating);
        } else {
            book.setAverageRating(0.0);
            System.err.println("BookDAO (extractBookFromResultSet): reviewDAO null! Ortalama puan hesaplanamadı.");
        }

        if (loggedInUserId > 0) {
            book.setLikedByCurrentUser(hasUserLikedBook(book.getBookId(), loggedInUserId));
        } else {
            book.setLikedByCurrentUser(false);
        }

        double avgRating = book.getAverageRating();
        int fullStars = (int) Math.floor(avgRating);
        boolean hasHalfStar = (avgRating - fullStars) >= 0.5;
        int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        if (emptyStars < 0) {
            emptyStars = 0;
        }
        book.setFullStars(fullStars);
        book.setHasHalfStar(hasHalfStar);
        book.setEmptyStars(emptyStars);

        if (hasColumn(rs, "date_liked")) {
            book.setDateLiked(rs.getTimestamp("date_liked"));
        }

        return book;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // === KİTAP EKLEME, SİLME, BEĞENME VB. METOTLAR ===
    public int addBook(Book book) {
        String sql = "INSERT INTO Books (title, author, description, isbn, publisher, publication_year, cover_image_url, added_by_user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedBookId = 0;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getDescription());
            statement.setString(4, book.getIsbn());
            statement.setString(5, book.getPublisher());
            if (book.getPublicationYear() > 0) {
                statement.setInt(6, book.getPublicationYear());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER);
            }
            statement.setString(7, book.getCoverImageUrl());
            statement.setInt(8, book.getAddedByUserId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedBookId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedBookId;
    }

    public boolean deleteBook(int bookId, int userId) {
        String sql = "DELETE FROM Books WHERE book_id = ? AND added_by_user_id = ?";
        boolean rowDeleted = false;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            statement.setInt(2, userId);
            rowDeleted = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowDeleted;
    }

    public boolean hasUserLikedBook(int bookId, int userId) {
        String sql = "SELECT 1 FROM Book_Likes WHERE book_id = ? AND user_id = ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean likeBook(int bookId, int userId) {
        String insertLikeSQL = "INSERT INTO Book_Likes (book_id, user_id) VALUES (?, ?)";
        String updateCountSQL = "UPDATE Books SET likes_count = likes_count + 1 WHERE book_id = ?";
        Connection connection = null;
        PreparedStatement insertStatement = null;
        PreparedStatement updateStatement = null;
        boolean overallSuccess = false;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            insertStatement = connection.prepareStatement(insertLikeSQL);
            insertStatement.setInt(1, bookId);
            insertStatement.setInt(2, userId);
            int rowInserted = insertStatement.executeUpdate();
            if (rowInserted > 0) {
                updateStatement = connection.prepareStatement(updateCountSQL);
                updateStatement.setInt(1, bookId);
                int rowUpdated = updateStatement.executeUpdate();
                if (rowUpdated > 0) {
                    overallSuccess = true;
                }
            }
            if (overallSuccess) {
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
            if (!(e.getSQLState().equals("23000") || e.getErrorCode() == 1062)) { // Zaten beğenilmişse hata basma
                e.printStackTrace();
            }
            overallSuccess = false;
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
        return overallSuccess;
    }

    public boolean unlikeBook(int bookId, int userId) {
        String deleteLikeSQL = "DELETE FROM Book_Likes WHERE book_id = ? AND user_id = ?";
        String updateCountSQL = "UPDATE Books SET likes_count = GREATEST(0, likes_count - 1) WHERE book_id = ?";
        Connection connection = null;
        PreparedStatement deleteStatement = null;
        PreparedStatement updateStatement = null;
        boolean success = false;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            deleteStatement = connection.prepareStatement(deleteLikeSQL);
            deleteStatement.setInt(1, bookId);
            deleteStatement.setInt(2, userId);
            int rowDeleted = deleteStatement.executeUpdate();
            if (rowDeleted > 0) {
                updateStatement = connection.prepareStatement(updateCountSQL);
                updateStatement.setInt(1, bookId);
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

    // === KİTAP GETİRME METOTLARI (Yardımcı Metot Kullanılarak Güncellendi) ===
    public List<Book> getAllBooks(int loggedInUserId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, u.username as uploader_username, b.likes_count "
                + "FROM Books b JOIN Users u ON b.added_by_user_id = u.user_id "
                + "ORDER BY b.created_at DESC";

        try (Connection connection = getConnection(); Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs, loggedInUserId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public Book getBookById(int bookId, int loggedInUserId) {
        String sql = "SELECT b.*, u.username as uploader_username, b.likes_count "
                + "FROM Books b JOIN Users u ON b.added_by_user_id = u.user_id "
                + "WHERE b.book_id = ?";
        Book book = null;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                book = extractBookFromResultSet(rs, loggedInUserId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }

    public List<Book> getBooksAddedByUser(int userId, int loggedInUserId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, u.username as uploader_username, b.likes_count "
                + "FROM Books b JOIN Users u ON b.added_by_user_id = u.user_id "
                + "WHERE b.added_by_user_id = ? "
                + "ORDER BY b.created_at DESC";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs, loggedInUserId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooks(String query, int loggedInUserId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, u.username as uploader_username, b.likes_count "
                + "FROM Books b JOIN Users u ON b.added_by_user_id = u.user_id "
                + "WHERE (LOWER(b.title) LIKE LOWER(?) OR LOWER(b.author) LIKE LOWER(?)) "
                + "ORDER BY b.title ASC";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            String searchQuery = "%" + query.toLowerCase() + "%";
            statement.setString(1, searchQuery);
            statement.setString(2, searchQuery);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs, loggedInUserId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getTopLikedBooks(int limit, int loggedInUserId) {
        List<Book> topBooks = new ArrayList<>();
        String sql = "SELECT b.*, u.username as uploader_username, b.likes_count "
                + "FROM Books b JOIN Users u ON b.added_by_user_id = u.user_id "
                + "ORDER BY b.likes_count DESC, b.title ASC "
                + "LIMIT ?";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                topBooks.add(extractBookFromResultSet(rs, loggedInUserId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topBooks;
    }

    public List<Book> getLikedBooksByUser(int profileOwnerUserId, int loggedInUserId) {
        List<Book> likedBooks = new ArrayList<>();
        // date_liked'ı da çekiyoruz
        String sql = "SELECT b.*, u.username as uploader_username, b.likes_count, bl.created_at as date_liked "
                + "FROM Books b "
                + "JOIN Users u ON b.added_by_user_id = u.user_id "
                + "JOIN Book_Likes bl ON b.book_id = bl.book_id "
                + "WHERE bl.user_id = ? "
                + "ORDER BY bl.created_at DESC";
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, profileOwnerUserId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                likedBooks.add(extractBookFromResultSet(rs, loggedInUserId)); // date_liked'ı da set edecek
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likedBooks;
    }

    public int getLikeCountForBook(int bookId) {
        String sql = "SELECT likes_count FROM Books WHERE book_id = ?";
        int likeCount = 0;
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
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
}
