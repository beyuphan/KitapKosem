/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.kitapkosem.controller;


import com.kitapkosem.dao.UserDAO;
import com.kitapkosem.model.User;
import com.kitapkosem.util.PasswordUtil; 


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author eyuph
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String usernameOrEmail = request.getParameter("usernameOrEmail"); 
        String password = request.getParameter("password");

        HttpSession session = request.getSession();
        User user = null;

         if (usernameOrEmail != null && !usernameOrEmail.trim().isEmpty() && 
            password != null && !password.isEmpty()) {
            
            String input = usernameOrEmail.trim();

            System.out.println("LoginServlet: Kullanıcı adı ile deneniyor: " + input);
            user = userDAO.getUserByUsername(input);

            if (user == null && input.contains("@")) {
                System.out.println("LoginServlet: Kullanıcı adı ile bulunamadı, e-posta ile deneniyor: " + input);
                user = userDAO.getUserByEmail(input); 
            }
        }

        if (user != null && user.isActive()) {
            if (PasswordUtil.checkPassword(password, user.getPasswordHash())) {
                session.setAttribute("loggedInUser", user);
                session.removeAttribute("loginError");
                
             
                String redirectURL = request.getContextPath() + "/index";
                
                String previousPage = (String) session.getAttribute("loginRedirect");
                if (previousPage != null && !previousPage.isEmpty()) {
                    redirectURL = previousPage;
                    session.removeAttribute("loginRedirect"); 
                    System.out.println("LoginServlet: Önceki sayfaya yönlendiriliyor: " + redirectURL);
                } else {
                    String authRedirect = request.getParameter("redirect"); 
                    if ("addBook".equals(authRedirect)) {
                         redirectURL = request.getContextPath() + "/addBook";
                         System.out.println("LoginServlet: addBook'a yönlendiriliyor.");
                    } else if ("timeline".equals(authRedirect)) {
                         redirectURL = request.getContextPath() + "/timeline";
                         System.out.println("LoginServlet: timeline'a yönlendiriliyor.");
                    }
                }

                response.sendRedirect(redirectURL);
                return; 
            }
        }

        session.setAttribute("loginError", "Kullanıcı adı/e-posta veya şifre hatalı ya da hesap aktif değil.");
        response.sendRedirect(request.getContextPath() + "/auth.jsp"); 
    }
}