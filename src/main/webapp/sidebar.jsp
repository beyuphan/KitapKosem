<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%-- JSTL Core kütüphanesi --%>

<div class="burger" id="burgerMenu">
    <div id="burgerToggle"></div>
    <div class="sidebar-content">

        <c:if test="${not empty sessionScope.loggedInUser}"> <%-- Kullanıcı giriş yapmışsa --%>
            <div class="user-profile">
                <%-- Avatar için JSTL mantığı --%>
                <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                <c:set var="dbAvatarUrl" value="${sessionScope.loggedInUser.profileAvatarUrl}" />
                <c:set var="defaultAvatarUrl" value="${contextPath}/assets/user-avatar.jpg" /> <%-- Veya kendi varsayılan avatar yolun --%>
                <c:set var="sidebarAvatarToShow" value="${defaultAvatarUrl}" /> <%-- Varsayılan olarak ata --%>

                <c:if test="${not empty dbAvatarUrl}">
                    <c:choose>
                        <c:when test="${fn:startsWith(dbAvatarUrl, 'http://') or fn:startsWith(dbAvatarUrl, 'https://')}">
                            <c:set var="sidebarAvatarToShow" value="${dbAvatarUrl}" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="sidebarAvatarToShow" value="${contextPath}${fn:startsWith(dbAvatarUrl, '/') ? '' : '/'}${dbAvatarUrl}" />
                        </c:otherwise>
                    </c:choose>
                </c:if>

                <img src="${sidebarAvatarToShow}" alt="Profil Fotoğrafı" class="user-avatar">

                    <div class="user-info">
                        <h3 class="username"><c:out value="${sessionScope.loggedInUser.username}" /></h3> 
                        <p class="user-stats">Hoş Geldin!</p>
                    </div>
            </div>
            <div class="add-book-container">
                <a href="${pageContext.request.contextPath}/addBook.jsp" class="add-book-btn"> <%-- Kitap ekleme sayfasına link --%>
                    <i class="fas fa-plus-circle"></i>
                    <span>Yeni Kitap Ekle</span>
                </a>
            </div>
            <nav class="sidebar-nav">
                <ul>
                    <li>
                        <a href="${pageContext.request.contextPath}/profile?username=${sessionScope.loggedInUser.username}">                            <i class="fas fa-user"></i>
                            <span>Profilim</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/index"> <%-- Ana sayfaya link --%>
                            <i class="fas fa-home"></i>
                            <span>Ana Sayfa</span>
                        </a>
                    </li>
                    <li>
                        <a href="${pageContext.request.contextPath}/timeline">
                            <i class="fas fa-stream"></i>
                            <span>Timeline</span>
                        </a>
                    </li>

                </ul>
            </nav>
            <div class="sidebar-footer">
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn"> <%-- Çıkış Servlet'ine link --%>
                    <i class="fas fa-sign-out-alt"></i>
                    <span>Çıkış Yap</span>
                </a>
            </div>
        </c:if>

        <c:if test="${empty sessionScope.loggedInUser}"> <%-- Kullanıcı giriş yapmamışsa --%>
            <div class="user-profile">
                <img src="${pageContext.request.contextPath}/assets/default-avatar.png" alt="Avatar" class="user-avatar">
                    <div class="user-info">
                        <h3 class="username">Misafir</h3>
                    </div>
            </div>
            <div class="add-book-container" style="text-align: center; padding: 20px;">
                <p>İçerikleri görmek ve kitap eklemek için lütfen giriş yapın veya kayıt olun.</p>
                <a href="${pageContext.request.contextPath}/auth.jsp" class="auth-btn" style="display: inline-block; margin-top:10px; text-decoration:none;">Giriş Yap / Kayıt Ol</a>
            </div>
            <%-- Giriş yapmamış kullanıcı için belki sadece ana sayfa linki --%>
            <nav class="sidebar-nav">
                <ul>
                    <li>
                        <a href="${pageContext.request.contextPath}/index">
                            <i class="fas fa-home"></i>
                            <span>Ana Sayfa</span>
                        </a>
                    </li>
                    <%-- Belki "Popüler Kitaplar" gibi herkesin görebileceği bir link daha --%>
                </ul>
            </nav>
        </c:if>

    </div>
</div>