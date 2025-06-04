/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.kitapkosem.controller;

import com.kitapkosem.dao.FollowDAO;
import com.kitapkosem.dao.UserDAO; 
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

/**
 *
 * @author eyuph
 */
@WebServlet("/toggleFollow")
public class FollowServlet extends HttpServlet {

    private FollowDAO followDAO;
    private UserDAO userDAO; 
    private ActivityDAO activityDAO; 

    @Override
    public void init() {
        followDAO = new FollowDAO();
        userDAO = new UserDAO(); 
        activityDAO = new ActivityDAO();
    }

    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    request.setCharacterEncoding("UTF-8");
    HttpSession session = request.getSession(false);
    System.out.println("FollowServlet: doPost çağrıldı."); 

    if (session == null || session.getAttribute("loggedInUser") == null) {
        System.out.println("FollowServlet: Kullanıcı giriş yapmamış, auth.jsp'ye yönlendiriliyor."); 
        response.sendRedirect(request.getContextPath() + "/auth.jsp");
        return;
    }

    User loggedInUser = (User) session.getAttribute("loggedInUser");
    int followerId = loggedInUser.getUserId();
    System.out.println("FollowServlet: Takip Eden Kullanıcı ID (followerId): " + followerId); 

    String profileUserIdStr = request.getParameter("profileUserId");
    String action = request.getParameter("action");
    System.out.println("FollowServlet: Gelen profileUserIdStr: " + profileUserIdStr); 
    System.out.println("FollowServlet: Gelen action: " + action);


    if (profileUserIdStr == null || action == null || 
        (!action.equals("follow") && !action.equals("unfollow"))) {
        System.out.println("FollowServlet: Parametreler eksik veya action geçersiz. Yönlendiriliyor."); 
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    int profileUserId = 0;
    try {
        profileUserId = Integer.parseInt(profileUserIdStr);
    } catch (NumberFormatException e) {
        System.out.println("FollowServlet: Geçersiz profileUserId formatı. Yönlendiriliyor.");
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    System.out.println("FollowServlet: Takip Edilecek Kullanıcı ID (profileUserId): " + profileUserId); 

    if (followerId == profileUserId) {
        System.out.println("FollowServlet: Kullanıcı kendini takip etmeye çalıştı."); 
         response.sendRedirect(request.getContextPath() + "/profile?username=" + loggedInUser.getUsername());
         return;
    }

    boolean success = false;
    if ("follow".equals(action)) {
        System.out.println("FollowServlet: followUser çağrılıyor..."); 
        success = followDAO.followUser(followerId, profileUserId);
        System.out.println("FollowServlet: followUser sonucu: " + success); 
          if (success) { 
        Activity activity = new Activity(
            followerId, 
            "STARTED_FOLLOWING", 
            profileUserId,
            null          
        );
        activityDAO.addActivity(activity);
        System.out.println("FollowServlet: STARTED_FOLLOWING activity logged. Follower: " + followerId + ", Following: " + profileUserId);
    }
    } else if ("unfollow".equals(action)) {
        System.out.println("FollowServlet: unfollowUser çağrılıyor...");
        success = followDAO.unfollowUser(followerId, profileUserId);
        System.out.println("FollowServlet: unfollowUser sonucu: " + success);
    }

    User profileUser = userDAO.getUserById(profileUserId); 
    String redirectUsername = (profileUser != null) ? profileUser.getUsername() : loggedInUser.getUsername(); 

    if (success) {
        System.out.println("FollowServlet: İşlem başarılı. Yönlendiriliyor: /profile?username=" + redirectUsername); 
    } else {
        System.out.println("FollowServlet: İşlem BAŞARISIZ. Yönlendiriliyor: /profile?username=" + redirectUsername); 
    }

    response.sendRedirect(request.getContextPath() + "/profile?username=" + redirectUsername);
}
}