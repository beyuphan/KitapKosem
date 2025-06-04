/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.kitapkosem.controller;

import com.kitapkosem.dao.BookDAO; 
import com.kitapkosem.dao.FollowDAO; 
import com.kitapkosem.dao.ReviewDAO; 
import com.kitapkosem.dao.UserDAO;
import com.kitapkosem.model.User;
import com.kitapkosem.model.Book; 
import com.kitapkosem.model.Review;  

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List; 
import java.util.ArrayList; 
import java.util.Collections; 
import java.util.Comparator;
import java.util.HashMap; 
import java.util.Map; 
import java.sql.Timestamp; 
import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author eyuph
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO;
    private FollowDAO followDAO; 
    private BookDAO bookDAO; 
        private ReviewDAO reviewDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
        followDAO = new FollowDAO(); 
        bookDAO = new BookDAO();
                reviewDAO = new ReviewDAO(); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String requestedUsername = request.getParameter("username"); 
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int currentLoggedInUserId = 0; 

        if (requestedUsername == null || requestedUsername.trim().isEmpty()) {
       
            if (loggedInUser != null) {
                response.sendRedirect(request.getContextPath() + "/profile?username=" + loggedInUser.getUsername());
                return;
            } else {
               
                response.sendRedirect(request.getContextPath() + "/auth.jsp");
                return;
            }
        }

        User profileUser = userDAO.getUserByUsername(requestedUsername.trim());

        if (profileUser != null) {
            request.setAttribute("profileUser", profileUser);
            
            
            boolean isOwnProfile = false;
            if (loggedInUser != null && loggedInUser.getUserId() == profileUser.getUserId()) {
                isOwnProfile = true;
            }
            request.setAttribute("isOwnProfile", isOwnProfile);

            // ---- YENİ EKLENEN TAKİP BİLGİLERİ ----
            int followerCount = followDAO.getFollowerCount(profileUser.getUserId());
            int followingCount = followDAO.getFollowingCount(profileUser.getUserId());
            
            boolean isCurrentlyFollowing = false;
            if (loggedInUser != null && !isOwnProfile) { // Giriş yapmış ve başkasının profiline bakıyorsa
                isCurrentlyFollowing = followDAO.isFollowing(loggedInUser.getUserId(), profileUser.getUserId());
            }

            request.setAttribute("followerCount", followerCount);
            request.setAttribute("followingCount", followingCount);
            request.setAttribute("isCurrentlyFollowing", isCurrentlyFollowing); 
            List<User> followersList = followDAO.getFollowers(profileUser.getUserId());
            List<User> followingsList = followDAO.getFollowings(profileUser.getUserId());

            request.setAttribute("followersList", followersList);
            request.setAttribute("followingsList", followingsList);
            
            List<Book> userAddedBooks = bookDAO.getBooksAddedByUser(profileUser.getUserId(), currentLoggedInUserId); 
              
                if (userAddedBooks != null) {
                    for (Book book : userAddedBooks) {
                

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
                    }
                }
                request.setAttribute("userAddedBooks", userAddedBooks);

             // ---- YENİ EKLENEN KULLANICININ YAPTIĞI YORUMLAR LİSTESİ ----
            List<Review> userReviewsList = reviewDAO.getReviewsByUserId(profileUser.getUserId());
            request.setAttribute("userReviewsList", userReviewsList);
            // --------------------------------------------------------------
                        // ---- YENİ: KULLANICININ BEĞENDİĞİ KİTAPLAR VE YORUMLAR ----
            List<Book> likedBooks = bookDAO.getLikedBooksByUser(profileUser.getUserId(), currentLoggedInUserId);
            System.out.println("ProfileServlet - Çekilen Beğenilmiş Kitap Sayısı: " + (likedBooks != null ? likedBooks.size() : "null"));
            List<Review> likedReviews = reviewDAO.getLikedReviewsByUser(profileUser.getUserId());

            List<Map<String, Object>> allLikedItems = new ArrayList<>();

            if (likedBooks != null) {
                for (Book book : likedBooks) {
                   
                    double avgRating = book.getAverageRating();
                    int fullStars = (int) Math.floor(avgRating);
                    boolean hasHalfStar = (avgRating - fullStars) >= 0.5;
                    int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
                    if (emptyStars < 0) emptyStars = 0;
                    book.setFullStars(fullStars);
                    book.setHasHalfStar(hasHalfStar);
                    book.setEmptyStars(emptyStars);
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", "BOOK");
                    item.put("data", book);
                    item.put("likedDate", book.getDateLiked()); // Book modelinde getDateLiked() olmalı
                    allLikedItems.add(item);
                }
            }

            if (likedReviews != null) {
                for (Review review : likedReviews) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", "REVIEW");
                    item.put("data", review);
                    item.put("likedDate", review.getDateLiked()); // Review modelinde getDateLiked() olmalı
                    allLikedItems.add(item);
                }
            }
            System.out.println("ProfileServlet - Birleştirme sonrası allLikedItems boyutu (sıralamadan önce): " + allLikedItems.size());
            // Birleşik listeyi beğenilme tarihine göre tersten sırala (en yeni en üstte)
            Collections.sort(allLikedItems, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Timestamp t1 = (Timestamp) o1.get("likedDate");
                    Timestamp t2 = (Timestamp) o2.get("likedDate");
                    if (t1 == null && t2 == null) return 0;
                    if (t1 == null) return 1; // null'ları sona at
                    if (t2 == null) return -1; // null'ları sona at
                    return t2.compareTo(t1); // Tersten sıralama için t2.compareTo(t1)
                }
            });
            System.out.println("ProfileServlet - Sıralama sonrası allLikedItems boyutu: " + allLikedItems.size());
            request.setAttribute("allLikedItems", allLikedItems);
         

            RequestDispatcher dispatcher = request.getRequestDispatcher("profile.jsp");
            dispatcher.forward(request, response);

        } else {
            session.setAttribute("globalError", "İstenen kullanıcı profili bulunamadı.");
            response.sendRedirect(request.getContextPath() + "/index"); 
        }
    }
}