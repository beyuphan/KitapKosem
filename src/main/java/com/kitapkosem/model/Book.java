/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kitapkosem.model;

/**
 *
 * @author eyuph
 */
import java.sql.Timestamp;

public class Book {

    private int bookId;
    private String title;
    private String author;
    private String description;
    private String isbn;
    private String publisher;
    private int publicationYear;
    private String uploaderUsername;
    private Timestamp dateLiked;

    private double averageRating;
    private int fullStars;
    private boolean hasHalfStar;
    private int emptyStars;

    private int likesCount;
    private boolean likedByCurrentUser;

    private String coverImageUrl;
    private int addedByUserId;
    private Timestamp createdAt;

    // 1. Boş Constructor
    public Book() {
    }

    // 2. Bazı temel alanları içeren Constructor (Yeni kitap eklerken kullanışlı olabilir)
    public Book(String title, String author, String description, String isbn, String publisher, int publicationYear, String coverImageUrl, int addedByUserId) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.coverImageUrl = coverImageUrl;
        this.addedByUserId = addedByUserId;
    }

    // 3. Tüm alanları içeren Constructor (Veritabanından okurken kullanışlı olabilir)
    public Book(int bookId, String title, String author, String description, String isbn, String publisher, int publicationYear, String coverImageUrl, int addedByUserId, Timestamp createdAt) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.description = description;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.coverImageUrl = coverImageUrl;
        this.addedByUserId = addedByUserId;
        this.createdAt = createdAt;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploaderUsername() {
        return uploaderUsername;
    }

    public void setUploaderUsername(String uploaderUsername) {
        this.uploaderUsername = uploaderUsername;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public int getAddedByUserId() {
        return addedByUserId;
    }

    public void setAddedByUserId(int addedByUserId) {
        this.addedByUserId = addedByUserId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getFullStars() {
        return fullStars;
    }

    public void setFullStars(int fullStars) {
        this.fullStars = fullStars;
    }

    public boolean isHasHalfStar() {
        return hasHalfStar;
    }

    public void setHasHalfStar(boolean hasHalfStar) {
        this.hasHalfStar = hasHalfStar;
    }

    public int getEmptyStars() {
        return emptyStars;
    }

    public void setEmptyStars(int emptyStars) {
        this.emptyStars = emptyStars;
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

    public Timestamp getDateLiked() {
        return dateLiked;
    }

    public void setDateLiked(Timestamp dateLiked) {
        this.dateLiked = dateLiked;
    }

    // 5. toString() Metodu (Debug için faydalı - İsteğe bağlı)
    // (NetBeans'te sağ tık > Insert Code... > toString()... seçeneğiyle hızlıca oluşturabilirsin)
    @Override
    public String toString() {
        return "Book{"
                + "bookId=" + bookId
                + ", title='" + title + '\''
                + ", author='" + author + '\''
                + ", isbn='" + isbn + '\''
                + ", addedByUserId=" + addedByUserId
                + ", createdAt=" + createdAt
                + '}';
    }
}
