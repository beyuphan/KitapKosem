// LikeBookServlet.java
package com.kitapkosem.controller;

import com.kitapkosem.dao.BookDAO;
import com.kitapkosem.dao.ActivityDAO;
import com.kitapkosem.model.User;
import com.kitapkosem.model.Activity;
import com.google.gson.Gson; 

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap; 
import java.util.Map;   
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/likeBook")
@MultipartConfig
public class LikeBookServlet extends HttpServlet {

    private BookDAO bookDAO;
    private ActivityDAO activityDAO;
    private Gson gson = new Gson(); 

    @Override
    public void init() {
        bookDAO = new BookDAO();
        activityDAO = new ActivityDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User loggedInUser = null;
        int userId = 0;

        if (session != null && session.getAttribute("loggedInUser") != null) {
            loggedInUser = (User) session.getAttribute("loggedInUser");
            userId = loggedInUser.getUserId();
        }

       
        boolean isAjaxRequest = "true".equals(request.getParameter("ajax"));


        if (userId == 0) { // Kullanıcı giriş yapmamış
            if (isAjaxRequest) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Yetkisiz
                PrintWriter out = response.getWriter();
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Bu işlem için giriş yapmalısınız.");
                jsonResponse.put("redirectTo", request.getContextPath() + "/auth.jsp");
                out.print(gson.toJson(jsonResponse));
                out.flush();
            } else {
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer != null ? referer : request.getContextPath() + "/auth.jsp");
            }
            return;
        }

        String bookIdStr = request.getParameter("bookId");
        String action = request.getParameter("action");
        String sourcePage = request.getParameter("sourcePage");
        String profileUsernameForRedirect = request.getParameter("profileUsername");

        if (bookIdStr == null || bookIdStr.trim().isEmpty() ||
            action == null || (!action.equals("like") && !action.equals("unlike"))) {
            if (isAjaxRequest) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Hatalı İstek
                PrintWriter out = response.getWriter();
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Eksik veya geçersiz parametreler.");
                out.print(gson.toJson(jsonResponse));
                out.flush();
            } else {
                session.setAttribute("globalError", "Beğeni işlemi için gerekli parametreler eksik.");
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer != null ? referer : request.getContextPath() + "/index.jsp");
            }
            return;
        }

        int bookId = 0;
        try {
            bookId = Integer.parseInt(bookIdStr.trim());
        } catch (NumberFormatException e) {
             if (isAjaxRequest) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Geçersiz Kitap ID formatı.");
                out.print(gson.toJson(jsonResponse));
                out.flush();
            } else {
                session.setAttribute("globalError", "Geçersiz Kitap ID formatı.");
                String referer = request.getHeader("Referer");
                response.sendRedirect(referer != null ? referer : request.getContextPath() + "/index.jsp");
            }
            return;
        }

        boolean dmlSuccess = false;
        String message = "";

        if ("like".equals(action)) {
            dmlSuccess = bookDAO.likeBook(bookId, userId);
            if (dmlSuccess) {
                message = "Kitap beğenildi!";
                Activity activity = new Activity(userId, "LIKED_BOOK", bookId, null);
                activityDAO.addActivity(activity);
            } else {
                
                message = "Kitap zaten beğenilmiş veya bir hata oluştu.";
            }
        } else if ("unlike".equals(action)) {
            dmlSuccess = bookDAO.unlikeBook(bookId, userId);
            if (dmlSuccess) {
                message = "Beğeni geri alındı!";
            } else {
                message = "Beğeni geri alınamadı veya zaten beğenilmemişti.";
            }
        }

        if (isAjaxRequest) {
            // AJAX isteğine JSON yanıtı ver
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("success", dmlSuccess);
            jsonResponse.put("message", message);
           
            int newLikesCount = bookDAO.getLikeCountForBook(bookId);
            boolean newLikedByCurrentUser = bookDAO.hasUserLikedBook(bookId, userId);

            jsonResponse.put("likesCount", newLikesCount);
            jsonResponse.put("likedByCurrentUser", newLikedByCurrentUser);
            jsonResponse.put("bookId", bookId); 

            out.print(gson.toJson(jsonResponse));
            out.flush();
        } else {
            if (dmlSuccess) {
                session.setAttribute("globalSuccess", message);
            } else {
                session.setAttribute("globalError", message);
            }
            String redirectUrl = request.getContextPath();
             if ("detail".equals(sourcePage)) {
                redirectUrl += "/book?id=" + bookId;
            } else if ("profileLikes".equals(sourcePage) && profileUsernameForRedirect != null && !profileUsernameForRedirect.isEmpty()) {
                 redirectUrl += "/profile?username=" + profileUsernameForRedirect;
            } else if ("profileBooks".equals(sourcePage) && profileUsernameForRedirect != null && !profileUsernameForRedirect.isEmpty()) {
                 redirectUrl += "/profile?username=" + profileUsernameForRedirect;
            } else { 
                redirectUrl += "/index"; 
                String currentQuery = request.getParameter("currentQuery"); 
                if (currentQuery != null && !currentQuery.isEmpty()) {
                    try {
                        redirectUrl += "?query=" + java.net.URLEncoder.encode(currentQuery, "UTF-8");
                    } catch (java.io.UnsupportedEncodingException e) { /* Hata yönetimi */ }
                }
            }
            response.sendRedirect(redirectUrl);
        }
    }
}