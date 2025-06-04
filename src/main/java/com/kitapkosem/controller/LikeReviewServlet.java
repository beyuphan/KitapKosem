package com.kitapkosem.controller;

import com.kitapkosem.dao.ReviewDAO;
import com.kitapkosem.model.User;
import com.kitapkosem.dao.ActivityDAO;
import com.kitapkosem.model.Activity;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/likeReview")
public class LikeReviewServlet extends HttpServlet {

    private ReviewDAO reviewDAO;
    private ActivityDAO activityDAO; 
        private Gson gson = new Gson();

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
          
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bu işlem için giriş yapmalısınız.");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getUserId();

        // 2. Formdan gelen parametreleri al
        String reviewIdStr = request.getParameter("reviewId");
        String bookIdStr = request.getParameter("bookId"); 
        String action = request.getParameter("action"); 

        if (reviewIdStr == null || reviewIdStr.trim().isEmpty() ||
            bookIdStr == null || bookIdStr.trim().isEmpty() || 
            action == null || (!action.equals("like") && !action.equals("unlike"))) {

            session.setAttribute("globalError", "Beğeni işlemi için gerekli parametreler eksik.");
            String redirectBookId = (bookIdStr != null && !bookIdStr.isEmpty()) ? bookIdStr : "";
            if (!redirectBookId.isEmpty()) {
                 response.sendRedirect(request.getContextPath() + "/book?id=" + redirectBookId);
            } else {
                response.sendRedirect(request.getContextPath() + "/index");
            }
            return;
        }

        int reviewId = 0;
        int bookId = 0;
        try {
            reviewId = Integer.parseInt(reviewIdStr);
            bookId = Integer.parseInt(bookIdStr);
        } catch (NumberFormatException e) {
            session.setAttribute("globalError", "Geçersiz ID formatı.");
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        // 3. DAO üzerinden işlemi gerçekleştir
        boolean success = false;
        if ("like".equals(action)) {
           
            System.out.println("LikeReviewServlet: Liking reviewId: " + reviewId + " by userId: " + userId);
            success = reviewDAO.likeReview(reviewId, userId);
            if (success) { 
        Activity activity = new Activity(
            userId,
            "LIKED_REVIEW", 
            reviewId,      
            bookId          
        );
        activityDAO.addActivity(activity);
        System.out.println("LikeReviewServlet: LIKED_REVIEW activity logged for reviewId: " + reviewId + " by userId: " + userId);
    }
        } else if ("unlike".equals(action)) {
            System.out.println("LikeReviewServlet: Unliking reviewId: " + reviewId + " by userId: " + userId);
            success = reviewDAO.unlikeReview(reviewId, userId);
        }

        if (success) {
            
             System.out.println("LikeReviewServlet: Action '" + action + "' for reviewId " + reviewId + " was successful.");
        } else {
            
            System.out.println("LikeReviewServlet: Action '" + action + "' for reviewId " + reviewId + " FAILED or no change.");
        }

        
        response.sendRedirect(request.getContextPath() + "/book?id=" + bookId);
    }
}