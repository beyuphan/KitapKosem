/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.kitapkosem.controller;

import com.kitapkosem.dao.BookDAO;
import com.kitapkosem.dao.ReviewDAO; 
import com.kitapkosem.model.Book;
import com.kitapkosem.model.Review; 
import com.kitapkosem.model.User;

import java.io.IOException;
import java.util.List; 
import jakarta.servlet.RequestDispatcher;
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
@WebServlet(name = "BookDetailServlet", urlPatterns = {"/book"})
public class BookDetailServlet extends HttpServlet {
 private BookDAO bookDAO;
 private ReviewDAO reviewDAO; 
    @Override
    public void init() {
        bookDAO = new BookDAO();
        reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(); 

        String bookIdStr = request.getParameter("id");
        int bookId = 0;
        User loggedInUser = (User) session.getAttribute("loggedInUser");
int currentLoggedInUserId = 0; 
if (loggedInUser != null) {
    currentLoggedInUserId = loggedInUser.getUserId();
}

        if (bookIdStr == null || bookIdStr.trim().isEmpty()) {
            session.setAttribute("globalError", "Kitap ID'si belirtilmemiş.");
            response.sendRedirect(request.getContextPath() + "/index"); // Ana sayfaya yönlendir
            return;
        }

        try {
            bookId = Integer.parseInt(bookIdStr.trim());
        } catch (NumberFormatException e) {
            session.setAttribute("globalError", "Geçersiz Kitap ID formatı.");
            response.sendRedirect(request.getContextPath() + "/index");
            return;
        }

        Book book = bookDAO.getBookById(bookId, currentLoggedInUserId);

       if (book != null) {
            request.setAttribute("book", book);

            List<Review> reviews = reviewDAO.getReviewsByBookId(bookId, currentLoggedInUserId);
            double avgRating = reviewDAO.getAverageRatingForBook(bookId); // Ortalama puanı al

            request.setAttribute("reviews", reviews);
            request.setAttribute("averageRatingValue", avgRating);

            int fullStars = (int) Math.floor(avgRating);
            boolean hasHalfStar = (avgRating - fullStars) >= 0.5;
            int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
            
            if (emptyStars < 0) {
                emptyStars = 0;
            }

            request.setAttribute("fullStars", fullStars);
            request.setAttribute("hasHalfStar", hasHalfStar);
            request.setAttribute("emptyStars", emptyStars);

            RequestDispatcher dispatcher = request.getRequestDispatcher("bookDetail.jsp");
            dispatcher.forward(request, response);
        } else {
            session.setAttribute("globalError", "Belirtilen ID'ye sahip kitap bulunamadı.");
            response.sendRedirect(request.getContextPath() + "/index");
        }
    }
}