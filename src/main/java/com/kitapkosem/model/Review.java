package com.kitapkosem.model;

import java.sql.Timestamp;

public class Review {

    private int reviewId;
    private String username;
    private String userProfileAvatarUrl;

    private int bookId;
    private int userId;
    private Integer rating;
    private String commentText;
    private Timestamp reviewDate;
    private String bookTitle;
    private int likesCount;
    private boolean likedByCurrentUser;
    private Timestamp dateLiked;

    // 1. Boş Constructor
    public Review() {
    }

    // 2. Yeni yorum/puan eklerken kullanılabilecek Constructor
    public Review(int bookId, int userId, Integer rating, String commentText) {
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
        this.commentText = commentText;
    }

    // 3. Tüm alanları içeren Constructor (Veritabanından okurken kullanışlı olabilir)
    public Review(int reviewId, int bookId, int userId, Integer rating, String commentText, Timestamp reviewDate, String username) {
        this.reviewId = reviewId;
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
        this.commentText = commentText;
        this.reviewDate = reviewDate;
        this.username = username;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserProfileAvatarUrl() {
        return userProfileAvatarUrl;
    }

    public void setUserProfileAvatarUrl(String userProfileAvatarUrl) {
        this.userProfileAvatarUrl = userProfileAvatarUrl;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Timestamp getDateLiked() {
        return dateLiked;
    }

    public void setDateLiked(Timestamp dateLiked) {
        this.dateLiked = dateLiked;
    }

    // 5. toString() Metodu (Debug için faydalı - İsteğe bağlı)
    @Override
    public String toString() {
        return "Review{"
                + "reviewId=" + reviewId
                + ", bookId=" + bookId
                + ", userId=" + userId
                + ", rating=" + rating
                + ", commentText='" + (commentText != null ? commentText.substring(0, Math.min(commentText.length(), 30)) + "..." : "N/A") + '\''
                + // Yorumun ilk 30 karakteri
                ", reviewDate=" + reviewDate
                + '}';
    }
}
