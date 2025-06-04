package com.kitapkosem.controller;

import com.kitapkosem.dao.BookDAO;
import com.kitapkosem.model.User;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/deleteBook")
public class DeleteBookServlet extends HttpServlet {

    private BookDAO bookDAO;

    @Override
    public void init() {
        bookDAO = new BookDAO();
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

        // 2. Formdan gelen bookId'yi al
        String bookIdStr = request.getParameter("bookId");
        String sourcePageUsername = request.getParameter("sourcePageUsername"); 

        if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
            session.setAttribute("globalError", "Silinecek kitap ID'si bulunamadı.");
            String redirectUrl = (sourcePageUsername != null && !sourcePageUsername.isEmpty()) ? 
                                 request.getContextPath() + "/profile?username=" + sourcePageUsername : 
                                 request.getContextPath() + "/index";
            response.sendRedirect(redirectUrl);
            return;
        }

        int bookId = 0;
        try {
            bookId = Integer.parseInt(bookIdStr.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("globalError", "Geçersiz kitap ID formatı.");
            String redirectUrl = (sourcePageUsername != null && !sourcePageUsername.isEmpty()) ? 
                                 request.getContextPath() + "/profile?username=" + sourcePageUsername : 
                                 request.getContextPath() + "/index";
            response.sendRedirect(redirectUrl);
            return;
        }

        // 3. DAO üzerinden silme işlemini gerçekleştir
        boolean success = bookDAO.deleteBook(bookId, userId);

        // 4. Sonuca göre kullanıcıyı yönlendir ve mesaj ver
        String redirectUrl = request.getContextPath();
        if (sourcePageUsername != null && !sourcePageUsername.isEmpty()) {
            redirectUrl += "/profile?username=" + sourcePageUsername;
        } else {
            redirectUrl += "/index";
        }


        if (success) {
            session.setAttribute("globalSuccess", "Kitap başarıyla silindi!");
        } else {
            session.setAttribute("globalError", "Kitap silinirken bir sorun oluştu veya bu kitabı silme yetkiniz yok.");
        }

        response.sendRedirect(redirectUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
       
        response.sendRedirect(request.getContextPath() + "/index");
    }
}