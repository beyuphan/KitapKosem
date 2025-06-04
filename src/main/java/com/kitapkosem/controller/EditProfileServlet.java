package com.kitapkosem.controller;

import com.kitapkosem.dao.UserDAO;
import com.kitapkosem.model.User;
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

@WebServlet(name = "EditProfileServlet", urlPatterns = {"/editProfile"})
@MultipartConfig 
public class EditProfileServlet extends HttpServlet {

    private UserDAO userDAO;
    private ImageUploadService imageUploadService; 

    @Override
    public void init() {
        userDAO = new UserDAO();
        imageUploadService = new ImageUploadService(); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/auth.jsp?redirect=editProfile"); 
            return;
        }
        User loggedInUser = (User) session.getAttribute("loggedInUser");
       
        request.setAttribute("userToEdit", loggedInUser); 
        RequestDispatcher dispatcher = request.getRequestDispatcher("editProfile.jsp");
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
        
        User userToUpdate = userDAO.getUserById(loggedInUser.getUserId()); 
        
        if (userToUpdate == null) { 
            session.setAttribute("editProfileError", "Kullanıcı bilgileri alınamadı, lütfen tekrar giriş yapın.");
            response.sendRedirect(request.getContextPath() + "/logout"); // Güvenli çıkış yapıp login'e yönlendir
            return;
        }

        // Formdan gelen metin bilgilerini al
        String fullName = request.getParameter("fullName");
        String bio = request.getParameter("bio");

        if (fullName != null) { 
            userToUpdate.setFullName(fullName.trim().isEmpty() ? null : fullName.trim());
        }
        if (bio != null) { 
            userToUpdate.setBio(bio.trim().isEmpty() ? null : bio.trim());
        }

        String newProfileAvatarUrl = null;
        String newCoverPhotoUrl = null;
        boolean imageUploadErrorOccurred = false;

        try {
            // Profil fotoğrafını işle
            Part profileAvatarFilePart = request.getPart("profileAvatarFile");
            if (profileAvatarFilePart != null && profileAvatarFilePart.getSize() > 0 &&
                profileAvatarFilePart.getSubmittedFileName() != null && !profileAvatarFilePart.getSubmittedFileName().trim().isEmpty()) {
                
                System.out.println("EditProfileServlet: Profil fotoğrafı yükleniyor: " + profileAvatarFilePart.getSubmittedFileName());
                newProfileAvatarUrl = imageUploadService.uploadImageToImgBB(profileAvatarFilePart);
                if (newProfileAvatarUrl != null) {
                    userToUpdate.setProfileAvatarUrl(newProfileAvatarUrl); 
                    System.out.println("EditProfileServlet: Yeni profil fotoğrafı URL: " + newProfileAvatarUrl);
                } else {
                    System.err.println("EditProfileServlet: Profil fotoğrafı imgBB'ye yüklenemedi.");
                    session.setAttribute("editProfileError", (session.getAttribute("editProfileError") == null ? "" : session.getAttribute("editProfileError") + " ") + "Profil fotoğrafı yüklenirken sorun oluştu.");
                    imageUploadErrorOccurred = true;
                }
            }

            // Kapak fotoğrafını işle
            Part coverPhotoFilePart = request.getPart("coverPhotoFile");
            if (coverPhotoFilePart != null && coverPhotoFilePart.getSize() > 0 &&
                coverPhotoFilePart.getSubmittedFileName() != null && !coverPhotoFilePart.getSubmittedFileName().trim().isEmpty()) {
                
                System.out.println("EditProfileServlet: Kapak fotoğrafı yükleniyor: " + coverPhotoFilePart.getSubmittedFileName());
                newCoverPhotoUrl = imageUploadService.uploadImageToImgBB(coverPhotoFilePart);
                if (newCoverPhotoUrl != null) {
                    userToUpdate.setCoverPhotoUrl(newCoverPhotoUrl);
                    System.out.println("EditProfileServlet: Yeni kapak fotoğrafı URL: " + newCoverPhotoUrl);
                } else {
                    System.err.println("EditProfileServlet: Kapak fotoğrafı imgBB'ye yüklenemedi.");
                    session.setAttribute("editProfileError", (session.getAttribute("editProfileError") == null ? "" : session.getAttribute("editProfileError") + " ") + "Kapak fotoğrafı yüklenirken sorun oluştu.");
                    imageUploadErrorOccurred = true;
                }
            }

        } catch (Exception e) {
            System.err.println("EditProfileServlet: Dosya yükleme sırasında genel hata: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("editProfileError", "Resim yüklenirken beklenmedik bir hata oluştu.");
            imageUploadErrorOccurred = true; 
        }
        
      
        boolean dbUpdateSuccess = userDAO.updateUserProfile(userToUpdate);
        if (dbUpdateSuccess) {
           
            User updatedUserFromDB = userDAO.getUserById(userToUpdate.getUserId());
            if(updatedUserFromDB != null) {
                session.setAttribute("loggedInUser", updatedUserFromDB);
            } else {
               
                session.setAttribute("loggedInUser", userToUpdate); 
                System.err.println("EditProfileServlet: Kullanıcı DB'den güncellenmiş olarak çekilemedi, session'daki kısmen güncellendi.");
            }
            
            if (!imageUploadErrorOccurred) { 
                session.setAttribute("editProfileSuccess", "Profiliniz başarıyla güncellendi!");
            } else {
                session.setAttribute("editProfileInfo", "Profil bilgileriniz güncellendi ancak bazı resimler yüklenirken sorun oluştu.");
            }
            
            response.sendRedirect(request.getContextPath() + "/profile?username=" + loggedInUser.getUsername());
        } else {
            session.setAttribute("editProfileError", (session.getAttribute("editProfileError") == null ? "Profil güncellenirken bir veritabanı hatası oluştu." : session.getAttribute("editProfileError")));
            response.sendRedirect(request.getContextPath() + "/editProfile");
        }
    }
}