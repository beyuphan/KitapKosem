// AddBookServlet.java
package com.kitapkosem.controller;

import com.kitapkosem.dao.BookDAO;
import com.kitapkosem.model.Book;
import com.kitapkosem.model.User;
import com.kitapkosem.dao.ActivityDAO;
import com.kitapkosem.model.Activity;
import com.kitapkosem.service.ImageUploadService; 

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(name = "AddBookServlet", urlPatterns = {"/addBook"})
@MultipartConfig
public class AddBookServlet extends HttpServlet {

    private BookDAO bookDAO;
    private ActivityDAO activityDAO;
    private ImageUploadService imageUploadService;
    
    @Override
    public void init() {
        bookDAO = new BookDAO();
        activityDAO = new ActivityDAO();
        imageUploadService = new ImageUploadService(); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth.jsp?redirect=addBook");
            return;
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("addBook.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth.jsp");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String description = request.getParameter("description");
        String isbnFromForm = request.getParameter("isbn");
        String publisher = request.getParameter("publisher");
        String publicationYearStr = request.getParameter("publicationYear");

        String uploadedImageUrl = null; // Başa taşıdım

        if (title == null || title.trim().isEmpty() || author == null || author.trim().isEmpty()) {
            session.setAttribute("addBookError", "Kitap başlığı ve yazar alanları zorunludur.");
            response.sendRedirect(request.getContextPath() + "/addBook"); // JSP'ye geri yönlendir
            return;
        }

        try {
            Part filePart = request.getPart("coverImageFile");

            System.out.println("--- AddBookServlet - Dosya Part Kontrolü ---");

            if (filePart == null) {
                System.out.println("DEBUG: filePart nesnesi NULL geldi!");
            } else {
                System.out.println("DEBUG: filePart.getName(): [" + filePart.getName() + "]");
                System.out.println("DEBUG: filePart.getSize(): " + filePart.getSize());
                System.out.println("DEBUG: filePart.getContentType(): [" + filePart.getContentType() + "]");
                System.out.println("DEBUG: filePart.getSubmittedFileName(): [" + filePart.getSubmittedFileName() + "]");

                if (filePart.getSubmittedFileName() == null) {
                    System.out.println("DEBUG: filePart.getSubmittedFileName() NULL.");
                } else if (filePart.getSubmittedFileName().trim().isEmpty()) {
                    System.out.println("DEBUG: filePart.getSubmittedFileName() BOŞ (sadece boşluk).");
                }
            }

            if (filePart != null && filePart.getSize() > 0
                    && filePart.getSubmittedFileName() != null && !filePart.getSubmittedFileName().trim().isEmpty()) {

                System.out.println("AddBookServlet: Kapak resmi alınıyor: " + filePart.getSubmittedFileName() + ", Boyut: " + filePart.getSize());

                uploadedImageUrl = imageUploadService.uploadImageToImgBB(filePart);

                if (uploadedImageUrl != null) {
                    System.out.println("AddBookServlet: Resim imgBB'ye yüklendi, URL: " + uploadedImageUrl);
                } else {
                    System.err.println("AddBookServlet: imgBB'ye resim yükleme başarısız oldu. uploadedImageUrl null döndü.");
                
                }
            } else {
                System.out.println("AddBookServlet: Kullanıcı kapak resmi yüklemedi veya dosya boş.");
            }
        } catch (Exception e) {
            System.err.println("AddBookServlet: Dosya yükleme (request.getPart veya imageUploadService çağrısı) sırasında hata: " + e.getMessage());
            e.printStackTrace();
            
        }

        Book newBook = new Book();
        newBook.setTitle(title.trim());
        newBook.setAuthor(author.trim());
        newBook.setDescription(description != null ? description.trim() : null);
        newBook.setIsbn((isbnFromForm != null && !isbnFromForm.trim().isEmpty()) ? isbnFromForm.trim() : null);
        newBook.setPublisher(publisher != null ? publisher.trim() : null);

        if (publicationYearStr != null && !publicationYearStr.trim().isEmpty()) {
            try {
                newBook.setPublicationYear(Integer.parseInt(publicationYearStr.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Geçersiz yayın yılı formatı: " + publicationYearStr);
                newBook.setPublicationYear(0);
            }
        } else {
            newBook.setPublicationYear(0);
        }

        newBook.setCoverImageUrl(uploadedImageUrl); 
        newBook.setAddedByUserId(loggedInUser.getUserId());

        int generatedBookId = bookDAO.addBook(newBook);

        if (generatedBookId > 0) {
            session.setAttribute("addBookSuccess", "Kitap başarıyla eklendi! (ID: " + generatedBookId + ")");
            Activity activity = new Activity(
                    loggedInUser.getUserId(),
                    "NEW_BOOK",
                    generatedBookId,
                    null
            );
            activityDAO.addActivity(activity);
            response.sendRedirect(request.getContextPath() + "/book?id=" + generatedBookId);
        } else {
            session.setAttribute("addBookError", "Kitap veritabanına eklenirken bir hata oluştu veya ID alınamadı.");
            response.sendRedirect(request.getContextPath() + "/addBook"); // JSP'ye geri yönlendir
        }
    }
}
