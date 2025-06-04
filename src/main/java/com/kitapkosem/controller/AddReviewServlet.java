package com.kitapkosem.controller;

import com.kitapkosem.dao.ReviewDAO;
import com.kitapkosem.model.Review;
import com.kitapkosem.model.User;
import com.kitapkosem.dao.ActivityDAO;
import com.kitapkosem.model.Activity;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/addReview")
public class AddReviewServlet extends HttpServlet {
    
    private ActivityDAO activityDAO; 
    private ReviewDAO reviewDAO;

    @Override
    public void init() {
        reviewDAO = new ReviewDAO();
        activityDAO = new ActivityDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // 1. Kullanıcı giriş yapmış mı kontrol et
        if (session == null || session.getAttribute("loggedInUser") == null) {
            // Giriş yapılmamışsa, bir hata mesajı ile veya direkt giriş sayfasına yönlendir
            session.setAttribute("globalError", "Yorum yapmak için lütfen giriş yapınız.");
            String bookIdForRedirect = request.getParameter("bookId");
            if (bookIdForRedirect != null && !bookIdForRedirect.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/book?id=" + bookIdForRedirect);
            } else {
                response.sendRedirect(request.getContextPath() + "/auth.jsp");
            }
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
         int currentUserId = loggedInUser.getUserId();

        // 2. Formdan gelen verileri al
        String bookIdStr = request.getParameter("bookId");
        String ratingStr = request.getParameter("rating");
        String commentText = request.getParameter("commentText");

        // 3. Gerekli doğrulamaları yap
        if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
            session.setAttribute("reviewError", "Kitap ID'si bulunamadı, yorum eklenemiyor.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        int bookId = 0;
        try {
            bookId = Integer.parseInt(bookIdStr);
        } catch (NumberFormatException e) {
            session.setAttribute("reviewError", "Geçersiz kitap ID'si.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        boolean isRatingProvided = ratingStr != null && !ratingStr.trim().isEmpty();
        boolean isCommentProvided = commentText != null && !commentText.trim().isEmpty();

        if (!isRatingProvided && !isCommentProvided) {
            session.setAttribute("reviewError", "Lütfen puan verin veya bir yorum yazın.");
            response.sendRedirect(request.getContextPath() + "/book?id=" + bookId); 
            return;
        }

        Integer rating = null;
        if (isRatingProvided) {
            try {
                rating = Integer.valueOf(ratingStr);
                        System.out.println("Servlet - Parse edilen Integer rating: " + rating);
                if (rating < 1 || rating > 5) { 
                    session.setAttribute("reviewError", "Puan 1 ile 5 arasında olmalıdır.");
                    response.sendRedirect(request.getContextPath() + "/book?id=" + bookId);
                    return;
                }
            } catch (NumberFormatException e) {
                session.setAttribute("reviewError", "Geçersiz puan formatı.");
                response.sendRedirect(request.getContextPath() + "/book?id=" + bookId);
                return;
            }
        }else {
    System.out.println("Servlet - Rating sağlanmadı, Integer rating null olarak kalacak.");
}

        String finalCommentText = isCommentProvided ? commentText.trim() : null;

        Review existingReview = reviewDAO.getReviewByUserAndBook(loggedInUser.getUserId(), bookId);       

        if (existingReview != null) {
            System.out.println("AddReviewServlet: Mevcut yorum güncelleniyor. ReviewID: " + existingReview.getReviewId());
            
            boolean changed = false; 
            if (isRatingProvided) { 
               
                if (existingReview.getRating() == null || !existingReview.getRating().equals(rating)) {
                    existingReview.setRating(rating);
                    changed = true;
                }
            } else {
                if (existingReview.getRating() != null) { 
                    existingReview.setRating(null);
                    changed = true;
                }
            }

            if (isCommentProvided) { 
                String newTrimmedComment = commentText.trim();
                if (existingReview.getCommentText() == null || !existingReview.getCommentText().equals(newTrimmedComment)) {
                    existingReview.setCommentText(newTrimmedComment);
                    changed = true;
                }
            } else {
              
            }
            
            boolean updateSuccess = false;
            if (changed) { 
                updateSuccess = reviewDAO.updateReview(existingReview);
                System.out.println("AddReviewServlet: updateReview (değişiklik var) sonucu: " + updateSuccess);
            } else {
                updateSuccess = true; 
                session.setAttribute("reviewInfo", "Değerlendirmenizde herhangi bir değişiklik yapılmadı.");
                System.out.println("AddReviewServlet: Değerlendirmede değişiklik yok, güncelleme yapılmadı.");
            }

            if (updateSuccess) {
                if (changed) { 
                    session.setAttribute("reviewSuccess", "Değerlendirmeniz başarıyla güncellendi!");
                    Activity activity = new Activity(
                        currentUserId,
                        "UPDATED_REVIEW", 
                        bookId, 
                        existingReview.getReviewId()
                    );
                    activityDAO.addActivity(activity);
                    System.out.println("AddReviewServlet: UPDATED_REVIEW aktivitesi loglandı.");
                }
            } else {
                session.setAttribute("reviewError", "Değerlendirme güncellenirken bir sorun oluştu.");
                System.out.println("AddReviewServlet: Değerlendirme güncellenemedi (DAO false döndü).");
            }
        } else {
            System.out.println("AddReviewServlet: Yeni yorum ekleniyor.");
            Review newReview = new Review();
            newReview.setBookId(bookId);
            newReview.setUserId(currentUserId);
            newReview.setRating(rating);
            newReview.setCommentText(finalCommentText); 

            int generatedReviewId = reviewDAO.addReview(newReview);
            System.out.println("AddReviewServlet: addReview sonucu (generatedReviewId): " + generatedReviewId);

            if (generatedReviewId > 0) {
                session.setAttribute("reviewSuccess", "Değerlendirmeniz başarıyla eklendi!");
                Activity activity = new Activity(
                    currentUserId,
                    "NEW_REVIEW", 
                    bookId,
                    generatedReviewId 
                );
                activityDAO.addActivity(activity);
                System.out.println("AddReviewServlet: NEW_REVIEW aktivitesi loglandı.");
            } else {
                session.setAttribute("reviewError", "Değerlendirme eklenirken bir sorun oluştu.");
                System.out.println("AddReviewServlet: Yeni değerlendirme eklenemedi (DAO 0 veya negatif ID döndü).");
            }
        }

        response.sendRedirect(request.getContextPath() + "/book?id=" + bookId);
    }
}