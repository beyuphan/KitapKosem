<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %> <%-- BU SATIR ÖNEMLİ --%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %> <%-- VEYA http://java.sun.com/jsp/jstl/fmt --%>

<%-- Giriş yapılmamışsa auth.jsp'ye yönlendir (Basit bir güvenlik önlemi) --%>
<c:if test="${empty sessionScope.loggedInUser}">
    <c:redirect url="auth.jsp" />
</c:if>

<!DOCTYPE html>
<html lang="tr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profili Düzenle | KitapKöşem</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

            </head>
            <body>
                <jsp:include page="sidebar.jsp" />

            <div class="main">
                <jsp:include page="header.jsp" />

                <div class="page-container" style="padding: 20px;">
                    <h2>Profili Düzenle</h2>

                    <%-- Hata veya Başarı Mesajları (EditProfileServlet'ten gelecek) --%>
                    <% String formError = (String) session.getAttribute("editProfileError"); %>
                    <% if (formError != null) {%>
                    <div class="form-message error-message">
                        <%= formError%>
                    </div>
                    <% session.removeAttribute("editProfileError"); %>
                    <% } %>

                    <% String formSuccess = (String) session.getAttribute("editProfileSuccess"); %>
                    <% if (formSuccess != null) {%>
                    <div class="form-message success-message">
                        <%= formSuccess%>
                    </div>
                    <% session.removeAttribute("editProfileSuccess"); %>
                    <% }%>

                    <%-- 
                        EditProfileServlet'in doGet'i, düzenlenecek kullanıcı bilgilerini 
                        "userToEdit" adında bir request attribute olarak bu sayfaya gönderecek.
                        Eğer kullanıcı kendi profilini düzenliyorsa, bu sessionScope.loggedInUser ile aynı kişi olacak.
                    --%>
                    <c:set var="userToEdit" value="${requestScope.userToEdit != null ? requestScope.userToEdit : sessionScope.loggedInUser}" />

                    <form class="edit-profile-form" method="POST"  enctype="multipart/form-data" action="${pageContext.request.contextPath}/editProfile">
                        <div class="input-group">
                            <label for="fullName">Tam Adınız:</label>
                            <input type="text" id="fullName" name="fullName" value="<c:out value='${userToEdit.fullName}'/>">
                        </div>

                        <div class="input-group">
                            <label for="username">Kullanıcı Adı:</label>
                            <input type="text" id="username" name="username" value="<c:out value='${userToEdit.username}'/>" readonly> 
                                <small>Kullanıcı adı değiştirilemez.</small>
                        </div>

                        <div class="input-group">
                            <label for="email">E-posta:</label>
                            <input type="email" id="email" name="email" value="<c:out value='${userToEdit.email}'/>" readonly>
                                <small>E-posta adresi değiştirilemez.</small>
                        </div>

                        <div class="input-group">
                            <label for="bio">Biyografi:</label>
                            <textarea id="bio" name="bio"><c:out value='${userToEdit.bio}'/></textarea>
                        </div>

                        <div class="input-group">
                            <label for="profileAvatarFile">Yeni Profil Fotoğrafı Yükle (Opsiyonel):</label>
                            <%-- Mevcut avatarı göstermek istersen: --%>
                            <c:if test="${not empty userToEdit.profileAvatarUrl}">
                                <div style="margin-bottom:10px;">
                                    <%-- Önce contextPath ve veritabanındaki URL'i değişkenlere alalım --%>
                                    <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                                    <c:set var="dbUserAvatarUrl" value="${userToEdit.profileAvatarUrl}" />
                                    <c:set var="finalDisplayAvatarUrl" value="" /> <%-- Sonradan doldurulacak --%>
                                    <div style="margin-bottom:10px; border: 1px solid #ccc; padding: 5px; display: inline-block;">
                                        <p style="margin:0 0 5px 0; font-size:0.9em; color:#555;">Mevcut Kapak Fotoğrafı:</p>
                                        <c:choose>
                                            <%-- 1. Durum: URL http:// veya https:// ile başlıyorsa (dışarıdan tam URL) --%>
                                            <c:when test="${fn:startsWith(dbUserAvatarUrl, 'http://') or fn:startsWith(dbUserAvatarUrl, 'https://')}">
                                                <c:set var="finalDisplayAvatarUrl" value="${dbUserAvatarUrl}" />
                                            </c:when>
                                            <%-- 2. Durum: URL / ile başlıyorsa (context path'e göre göreli) --%>
                                            <c:when test="${fn:startsWith(dbUserAvatarUrl, '/')}">
                                                <c:set var="finalDisplayAvatarUrl" value="${contextPath}${dbUserAvatarUrl}" />
                                            </c:when>
                                            <%-- 3. Durum: URL / ile başlamıyorsa (direkt dosya adı veya alt klasör + dosya adı) --%>
                                            <c:otherwise>
                                                <c:set var="finalDisplayAvatarUrl" value="${contextPath}/${dbUserAvatarUrl}" />
                                            </c:otherwise>
                                        </c:choose>

                                        <%-- finalDisplayCoverUrl doluysa resmi göster --%>
                                        <c:if test="${not empty finalDisplayAvatarUrl}">
                                            <img src="${finalDisplayAvatarUrl}" 
                                                 alt="Mevcut Kapak Fotoğrafı" 
                                                 style="width: 100px; height: 100px; object-fit: cover; display: block;">
                                            </c:if>
                                    </div>
                                </c:if>
                                <c:if test="${empty userToEdit.coverPhotoUrl}">
                                    <div style="margin-bottom:10px;">
                                        <p style="font-size:0.9em; color:#555;">Mevcut kapak fotoğrafı yok. İsterseniz varsayılan bir görsel gösterebilirsiniz:</p>
                                    </div>
                                </c:if>

                                <input type="file" id="profileAvatarFile" name="profileAvatarFile" accept="image/*">
                            </div>
                            <div class="input-group">
                                <label for="coverPhotoFile">Yeni Kapak Fotoğrafı Yükle (Opsiyonel):</label>

                                <%-- MEVCUT KAPAK FOTOĞRAFINI GÖSTERME BLOĞU --%>
                                <c:if test="${not empty userToEdit.coverPhotoUrl}">
                                    <div style="margin-bottom:10px; border: 1px solid #ccc; padding: 5px; display: inline-block;">
                                        <p style="margin:0 0 5px 0; font-size:0.9em; color:#555;">Mevcut Kapak Fotoğrafı:</p>
                                        <%-- Değişkenleri set edelim --%>
                                        <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                                        <c:set var="dbUserCoverUrl" value="${userToEdit.coverPhotoUrl}" />
                                        <c:set var="finalDisplayCoverUrl" value="" /> <%-- Başlangıçta boş --%>

                                        <c:choose>
                                            <%-- 1. Durum: URL http:// veya https:// ile başlıyorsa --%>
                                            <c:when test="${fn:startsWith(dbUserCoverUrl, 'http://') or fn:startsWith(dbUserCoverUrl, 'https://')}">
                                                <c:set var="finalDisplayCoverUrl" value="${dbUserCoverUrl}" />
                                            </c:when>
                                            <%-- 2. Durum: URL / ile başlıyorsa (context path'e göre göreli) --%>
                                            <c:when test="${fn:startsWith(dbUserCoverUrl, '/')}">
                                                <c:set var="finalDisplayCoverUrl" value="${contextPath}${dbUserCoverUrl}" />
                                            </c:when>
                                            <%-- 3. Durum: URL / ile başlamıyorsa (direkt dosya adı veya alt klasör + dosya adı) --%>
                                            <c:otherwise>
                                                <%-- Eğer dbUserCoverUrl boş değilse ama yukarıdaki koşullara uymuyorsa, 
                                                     başına contextPath ve / ekleyerek deneyelim. Bu, 'uploads/cover.jpg' gibi yolları düzeltir. --%>
                                                <c:if test="${not empty dbUserCoverUrl}">
                                                    <c:set var="finalDisplayCoverUrl" value="${contextPath}/${dbUserCoverUrl}" />
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>

                                        <%-- finalDisplayCoverUrl doluysa resmi göster --%>
                                        <c:if test="${not empty finalDisplayCoverUrl}">
                                            <img src="${finalDisplayCoverUrl}" 
                                                 alt="Mevcut Kapak Fotoğrafı" 
                                                 style="width: 100px; height: 100px; object-fit: cover; display: block;">
                                            </c:if>
                                        
                                    </div>
                                </c:if>
                                <%-- Eğer kullanıcının hiç kapak fotoğrafı yoksa ve bir varsayılan göstermek istersen: --%>
                                <c:if test="${empty userToEdit.coverPhotoUrl}">
                                    <div style="margin-bottom:10px;">
                                        <p style="font-size:0.9em; color:#555;">Mevcut kapak fotoğrafı yok. İsterseniz varsayılan bir görsel gösterebilirsiniz:</p>
                                        <%-- <img src="${pageContext.request.contextPath}/assets/default-cover.jpg" 
                                             alt="Varsayılan Kapak Fotoğrafı" 
                                             style="max-width: 300px; max-height: 150px; object-fit: cover; display: block;"> --%>
                                    </div>
                                </c:if>
                                <%-- MEVCUT KAPAK FOTOĞRAFINI GÖSTERME BLOĞU BİTİŞİ --%>

                                <input type="file" id="coverPhotoFile" name="coverPhotoFile" accept="image/*">
                            </div>

                            <div class="form-actions">
                                <button type="submit">Değişiklikleri Kaydet</button>
                            </div>
                    </form>
                </div>
            </div>
            <script src="${pageContext.request.contextPath}/js/script.js"></script>

            </body>
            </html>