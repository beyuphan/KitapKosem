package com.kitapkosem.controller;

import com.kitapkosem.dao.ReviewDAO;
import com.kitapkosem.model.User;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/deleteReview")
public class DeleteReviewServlet extends HttpServlet {

    private ReviewDAO reviewDAO;

    @Override
    public void init() {
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        // 1. Kullanıcı giriş yapmış mı kontrol et
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth.jsp");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getUserId();

        // 2. Formdan gelen reviewId ve bookId'yi (geri yönlendirme için) al
        String reviewIdStr = request.getParameter("reviewId");
        String bookIdStr = request.getParameter("bookId"); 
        String sourcePageUsername = request.getParameter("sourcePageUsername"); 

        if (reviewIdStr == null || reviewIdStr.trim().isEmpty()) {
            session.setAttribute("globalError", "Silinecek yorum ID'si bulunamadı.");
            if (bookIdStr != null && !bookIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/book?id=" + bookIdStr);
            } else if (sourcePageUsername != null && !sourcePageUsername.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/profile?username=" + sourcePageUsername);
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
            return;
        }

        int reviewId = 0;
        try {
            reviewId = Integer.parseInt(reviewIdStr.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("globalError", "Geçersiz yorum ID formatı.");
            if (bookIdStr != null && !bookIdStr.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/book?id=" + bookIdStr);
            } else if (sourcePageUsername != null && !sourcePageUsername.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/profile?username=" + sourcePageUsername);
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
            return;
        }

        
        int bookId = 0;
        if (bookIdStr != null && !bookIdStr.trim().isEmpty()) {
            try {
                bookId = Integer.parseInt(bookIdStr.trim());
            } catch (NumberFormatException e) {
                System.err.println("DeleteReviewServlet: Geri yönlendirme için geçersiz bookId formatı: " + bookIdStr);
            }
        }


        // 3. DAO üzerinden silme işlemini gerçekleştir
        boolean success = reviewDAO.deleteReview(reviewId, userId);

       

        String redirectUrl = request.getContextPath();
        if (bookId > 0) { 
            redirectUrl += "/book?id=" + bookId;
        } else if (sourcePageUsername != null && !sourcePageUsername.isEmpty()) {
           
            redirectUrl += "/profile?username=" + sourcePageUsername;
        } else {
           
            redirectUrl += "/index.jsp";
        }

        if (success) {
            session.setAttribute("globalSuccess", "Yorum başarıyla silindi!");
        } else {
            session.setAttribute("globalError", "Yorum silinirken bir sorun oluştu veya bu yorumu silme yetkiniz yok.");
        }

        response.sendRedirect(redirectUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}