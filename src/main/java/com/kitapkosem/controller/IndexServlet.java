/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.kitapkosem.controller;

import com.kitapkosem.dao.BookDAO;
import com.kitapkosem.model.Book;
import com.kitapkosem.model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList; 
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; 
/**
 *
 * @author eyuph
 */
@WebServlet(name = "IndexServlet", urlPatterns = {"/index"})
public class IndexServlet extends HttpServlet {
    
    private BookDAO bookDAO;
    private final int TOP_LIKED_BOOKS_LIMIT = 5;
    @Override
    public void init() {
        bookDAO = new BookDAO();
        
    }
  @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        User loggedInUser = null;
        int currentLoggedInUserId = 0;

        if (session != null && session.getAttribute("loggedInUser") != null) {
            loggedInUser = (User) session.getAttribute("loggedInUser");
            currentLoggedInUserId = loggedInUser.getUserId();
        }

        String searchQuery = request.getParameter("query");
        List<Book> bookList = null;
        List<Book> topLikedBooksList = null;

        try {
            // 1. Ana Kitap Listesini (veya Arama Sonuçlarını) Al
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                bookList = bookDAO.searchBooks(searchQuery.trim(), currentLoggedInUserId);
                request.setAttribute("searchQuery", searchQuery.trim());
                if (bookList.isEmpty()) {
                    request.setAttribute("searchMessage", "Aradığınız kriterlere uygun kitap bulunamadı: '" + searchQuery.trim() + "'");
                }
            } else {
                System.out.println("IndexServlet: Tüm kitaplar listeleniyor.");
                bookList = bookDAO.getAllBooks(currentLoggedInUserId);
                topLikedBooksList = bookDAO.getTopLikedBooks(TOP_LIKED_BOOKS_LIMIT, currentLoggedInUserId);
                request.setAttribute("topLikedBooksList", topLikedBooksList != null ? topLikedBooksList : new ArrayList<Book>());
            }

            // 2. Ana Kitap Listesindeki (bookList) Kitaplar İçin Yıldız Sayılarını Hesapla
            if (bookList != null) {
                for (Book book : bookList) {
                    double avgRating = book.getAverageRating(); 
                    int fullStars = (int) Math.floor(avgRating);
                    boolean hasHalfStar = (avgRating - fullStars) >= 0.5;
                    int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
                    if (emptyStars < 0) emptyStars = 0;

                    book.setFullStars(fullStars);
                    book.setHasHalfStar(hasHalfStar);
                    book.setEmptyStars(emptyStars);
                    
                   
                }
            }
            request.setAttribute("bookList", bookList != null ? bookList : new ArrayList<Book>());

           
            if (topLikedBooksList != null) {
                System.out.println("IndexServlet: Alınan en çok beğenilen kitap sayısı: " + topLikedBooksList.size());
                for (Book book : topLikedBooksList) {
                    double avgRating = book.getAverageRating(); // Bu DAO'dan gelmeli
                    int fullStars = (int) Math.floor(avgRating);
                    boolean hasHalfStar = (avgRating - fullStars) >= 0.5;
                    int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
                    if (emptyStars < 0) emptyStars = 0;

                    book.setFullStars(fullStars);
                    book.setHasHalfStar(hasHalfStar);
                    book.setEmptyStars(emptyStars);
                    
                  
                }
            }
           
            request.setAttribute("topLikedBooksList", topLikedBooksList != null ? topLikedBooksList : new ArrayList<Book>());

          
            System.out.println("IndexServlet: index.jsp'ye forward ediliyor...");
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("!!! IndexServlet: ASIL HATA index.jsp'DEN GELİYOR OLMALI !!!");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            e.printStackTrace(); 
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}