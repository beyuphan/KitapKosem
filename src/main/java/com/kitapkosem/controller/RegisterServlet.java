/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.kitapkosem.controller;

import com.kitapkosem.dao.UserDAO;
import com.kitapkosem.model.User;
import com.kitapkosem.util.PasswordUtil; // YENİ: PasswordUtil'i import et

import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

   
    private UserDAO userDAO;
    // private PasswordUtil passwordUtil; // Şifreleme için

    @Override
    public void init() {
        userDAO = new UserDAO();
        // passwordUtil = new PasswordUtil(); // Şifreleme için
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8"); // Türkçe karakterler için

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        HttpSession session = request.getSession();

        // Basit Doğrulamalar
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            session.setAttribute("registerError", "Tüm alanlar doldurulmalıdır.");
            response.sendRedirect("auth.jsp"); // Kayıt formunun olduğu JSP'ye yönlendir
            return;
        }

        if (!password.equals(confirmPassword)) {
            session.setAttribute("registerError", "Şifreler eşleşmiyor.");
            response.sendRedirect("auth.jsp");
            return;
        }

        

        // ŞİFRE HASHLEME (ÇOK ÖNEMLİ!)
        String hashedPassword = PasswordUtil.hashPassword(password);
        


        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword); // Hash'lenmiş şifreyi set et
        newUser.setActive(true);

        boolean result = userDAO.addUser(newUser);

        if (result) {
            session.setAttribute("registerSuccess", "Kayıt başarılı! Lütfen giriş yapın.");
            response.sendRedirect("auth.jsp"); // Veya direkt login.jsp
        } else {
            session.setAttribute("registerError", "Kayıt sırasında bir hata oluştu. Lütfen tekrar deneyin.");
            response.sendRedirect("auth.jsp");
        }
    }
}