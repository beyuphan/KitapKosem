package com.kitapkosem.controller;

import com.kitapkosem.dao.ActivityDAO;
import com.kitapkosem.dao.BookDAO; // Gerekli olacak
import com.kitapkosem.dao.ReviewDAO; // Gerekli olacak
import com.kitapkosem.dao.UserDAO; // Gerekli olacak
import com.kitapkosem.model.Activity;
import com.kitapkosem.model.User;
import com.kitapkosem.model.Book;   // Gerekli olacak
import com.kitapkosem.model.Review; // Gerekli olacak

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson; // YENİ: Gson importu
import java.io.PrintWriter; // YENİ: JSON response için

@WebServlet(name = "TimelineServlet", urlPatterns = "/timeline")
public class TimelineServlet extends HttpServlet {

    private ActivityDAO activityDAO;
    private BookDAO bookDAO;     // Aktiviteleri zenginleştirmek için
    private ReviewDAO reviewDAO;   // Aktiviteleri zenginleştirmek için
    private UserDAO userDAO;     // Aktiviteleri zenginleştirmek için

    private final int TIMELINE_ACTIVITIES_LIMIT = 20; // Sayfada gösterilecek aktivite sayısı

    @Override
    public void init() {
        activityDAO = new ActivityDAO();
        bookDAO = new BookDAO();
        reviewDAO = new ReviewDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        // response.setContentType("text/html;charset=UTF-8"); // Bunu eklemek de iyi bir pratiktir

        HttpSession session = request.getSession(false);
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        System.out.println("--- TimelineServlet: doGet çağrıldı ---"); // LOG

        if (loggedInUser == null) {
            System.out.println("TimelineServlet: Kullanıcı giriş yapmamış, auth.jsp?redirect=timeline adresine yönlendiriliyor."); // LOG
            response.sendRedirect(request.getContextPath() + "/auth.jsp?redirect=timeline");
            return;
        }

        int loggedInUserId = loggedInUser.getUserId();
        System.out.println("TimelineServlet: Giriş yapan kullanıcı ID: " + loggedInUserId); // LOG
        String pageStr = request.getParameter("page");
        int page = 1; // Varsayılan olarak ilk sayfa
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                page = 1; // Hatalı formatta varsayılan sayfa
            }
        }
        int offset = (page - 1) * TIMELINE_ACTIVITIES_LIMIT;

        // İsteğin AJAX olup olmadığını kontrol et (basit bir parametre ile)
        String requestType = request.getParameter("requestType");
        boolean isAjaxRequest = "ajax".equalsIgnoreCase(requestType);

        System.out.println("TimelineServlet: doGet - UserID: " + loggedInUserId
                + ", Page: " + page + ", Offset: " + offset
                + ", IsAjax: " + isAjaxRequest);
        try {
            List<Activity> rawActivities = activityDAO.getTimelineForUser(loggedInUserId, TIMELINE_ACTIVITIES_LIMIT, offset);
            List<Activity> enrichedActivities = new ArrayList<>();
            if (rawActivities != null) {
                for (Activity activity : rawActivities) {
                    try {
                        if ("NEW_BOOK".equals(activity.getActivityType()) || "LIKED_BOOK".equals(activity.getActivityType())) {
                            if (activity.getTargetItemId() != null) {
                                Book targetBook = bookDAO.getBookById(activity.getTargetItemId(), 0);
                                if (targetBook != null) {
                                    activity.setTargetItemTitle(targetBook.getTitle());
                                    // Kitap açıklaması snippet'i de eklenebilir
                                    // activity.setCommentSnippet(targetBook.getDescription().substring(0, Math.min(100, targetBook.getDescription().length())) + "...");
                                }
                            }
                        } else if ("NEW_REVIEW".equals(activity.getActivityType())
                                || "UPDATED_REVIEW".equals(activity.getActivityType())
                                || "LIKED_REVIEW".equals(activity.getActivityType())) {

                            Integer reviewIdToFetch = null;
                            Integer bookIdForTitle = null;

                            if ("LIKED_REVIEW".equals(activity.getActivityType())) {
                                reviewIdToFetch = activity.getTargetItemId();
                                bookIdForTitle = activity.getSecondaryTargetItemId();
                            } else { // NEW_REVIEW, UPDATED_REVIEW
                                reviewIdToFetch = activity.getSecondaryTargetItemId();
                                bookIdForTitle = activity.getTargetItemId();
                            }

                            // Kitap başlığını set et
                            if (bookIdForTitle != null && bookIdForTitle > 0) {
                                Book reviewedBook = bookDAO.getBookById(bookIdForTitle, 0);
                                if (reviewedBook != null) {
                                    activity.setTargetItemTitle(reviewedBook.getTitle());
                                }
                            }

                            // Yorum metnini ve yorumu yapanın adını (LIKED_REVIEW için) set et
                            if (reviewIdToFetch != null && reviewIdToFetch > 0) {
                                Review targetReview = reviewDAO.getReviewById(reviewIdToFetch); // YENİ METOT ÇAĞRISI
                                if (targetReview != null) {
                                    if (targetReview.getCommentText() != null) {
                                        activity.setCommentSnippet(
                                                targetReview.getCommentText().substring(0, Math.min(100, targetReview.getCommentText().length()))
                                                + (targetReview.getCommentText().length() > 100 ? "..." : "")
                                        );
                                    }
                                    if ("LIKED_REVIEW".equals(activity.getActivityType())) {
                                        activity.setSecondaryTargetItemTitle(targetReview.getUsername()); // Yorumu yapanın kullanıcı adı
                                    }
                                }
                            }
                        } else if ("STARTED_FOLLOWING".equals(activity.getActivityType())) {
                            if (activity.getTargetItemId() != null) {
                                User targetUser = userDAO.getUserById(activity.getTargetItemId());
                                if (targetUser != null) {
                                    activity.setTargetItemTitle(targetUser.getUsername());
                                }
                            }
                        }
                        enrichedActivities.add(activity);
                    } catch (Exception e) {
                        System.err.println("TimelineServlet: Aktivite zenginleştirilirken hata - ActivityID: " + activity.getActivityId() + ", Hata: " + e.getMessage());
                        e.printStackTrace();
                        enrichedActivities.add(activity);
                    }
                }
            }
            if (isAjaxRequest) {
                // AJAX isteği ise JSON olarak yanıt ver
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();
                String jsonResponse = new Gson().toJson(enrichedActivities);
                System.out.println("TimelineServlet: AJAX response gönderiliyor: " + jsonResponse.substring(0, Math.min(200, jsonResponse.length())) + "..."); // Log için kısaltılmış
                out.print(jsonResponse);
                out.flush();
            } else {
                // Normal sayfa yüklemesi ise JSP'ye forward et (sadece ilk sayfa verisiyle)
                request.setAttribute("timelineActivities", enrichedActivities);
                System.out.println("TimelineServlet: Normal forward, timeline.jsp'ye. Aktivite sayısı: " + enrichedActivities.size());
                RequestDispatcher dispatcher = request.getRequestDispatcher("timeline.jsp");
                dispatcher.forward(request, response);
            }

        } catch (Exception e) {
            System.err.println("TimelineServlet: doGet içinde genel bir hata oluştu!");
            e.printStackTrace();
            if (!response.isCommitted()) {
                // Hata durumunda AJAX isteğine de bir JSON hata mesajı dönebilirsin
                if (isAjaxRequest) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 hatası
                    PrintWriter out = response.getWriter();
                    out.print("{\"error\":\"Timeline yüklenirken bir sorun oluştu.\"}");
                    out.flush();
                } else {
                    request.setAttribute("globalError", "Timeline yüklenirken bir sorun oluştu.");
                    RequestDispatcher dispatcher = request.getRequestDispatcher("errorPage.jsp"); // Veya timeline.jsp'ye bir hata mesajıyla
                    dispatcher.forward(request, response);
                }
            }
        }
    }
}
