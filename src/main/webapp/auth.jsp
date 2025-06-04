<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="tr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>KitapKöşem - Giriş Yap / Kayıt Ol</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"> <%-- CSS yolu güncellendi --%>
            </head>
            <body class="auth-page">
            <div class="auth-container">
                <div class="auth-logo">
                    <img src="${pageContext.request.contextPath}/assets/logo.png" alt="KitapKöşem"> <%-- Logo yolu güncellendi --%>
                </div>

                <%-- Hata ve Başarı Mesajları --%>
                <% String registerError = (String) session.getAttribute("registerError"); %>
                <% if (registerError != null) {%>
                <div class="form-message error-message">
                    <%= registerError%>
                </div>
                <% session.removeAttribute("registerError"); // Mesajı gösterdikten sonra session'dan sil %>
                <% } %>

                <% String loginError = (String) session.getAttribute("loginError"); %>
                <% if (loginError != null) { %>
                <div class="form-message error-message">
                    <%=loginError%>
                </div>
                <% session.removeAttribute("loginError"); %>
                <% } %>

                <% String registerSuccess = (String) session.getAttribute("registerSuccess"); %>
                <% if (registerSuccess != null) {%>
                <div class="form-message success-message">
                    <%= registerSuccess%>
                </div>
                <% session.removeAttribute("registerSuccess"); %>
                <% }%>

                <div class="auth-tabs">
                    <button class="auth-tab active" data-tab="login">Giriş Yap</button>
                    <button class="auth-tab" data-tab="register">Kayıt Ol</button>
                </div>

                <form class="auth-form active" id="login-form" method="POST" action="${pageContext.request.contextPath}/login">
                    <div class="input-group">
                        <i class="fas fa-user"></i> <%-- İkonu email yerine genel user olarak değiştirdim --%>
                        <input type="text" name="usernameOrEmail" placeholder="Kullanıcı Adı veya E-posta" required>
                    </div>

                    <div class="input-group">
                        <i class="fas fa-lock"></i>
                        <input type="password" name="password" placeholder="Şifre" required>
                            <button type="button" class="show-password">
                                <i class="fas fa-eye"></i>
                            </button>
                    </div>

                    <div class="auth-options">
                        <label>
                            <input type="checkbox" name="rememberMe"> Beni Hatırla <%-- Name eklendi --%>
                        </label>
                        <a href="#">Şifremi Unuttum</a>
                    </div>

                    <button type="submit" class="auth-btn">Giriş Yap</button>

                    <div class="auth-divider">
                        <span>veya</span>
                    </div>

                    <button type="button" class="auth-social">
                        <i class="fab fa-google"></i>
                        Google ile Giriş Yap
                    </button>
                </form>

                <form class="auth-form" id="register-form" method="POST" action="${pageContext.request.contextPath}/register">
                    <div class="input-group">
                        <i class="fas fa-user"></i>
                        <input type="text" name="username" placeholder="Kullanıcı Adı" required>
                    </div>

                    <div class="input-group">
                        <i class="fas fa-envelope"></i>
                        <input type="email" name="email" placeholder="E-posta" required>
                    </div>

                    <div class="input-group">
                        <i class="fas fa-lock"></i>
                        <input type="password" name="password" placeholder="Şifre" required>
                            <button type="button" class="show-password">
                                <i class="fas fa-eye"></i>
                            </button>
                    </div>

                    <div class="input-group">
                        <i class="fas fa-lock"></i>
                        <input type="password" name="confirmPassword" placeholder="Şifre Tekrar" required>
                            <button type="button" class="show-password"> <%-- Şifre gösterme butonu eklendi --%>
                                <i class="fas fa-eye"></i>
                            </button>
                    </div>

                    <button type="submit" class="auth-btn">Kayıt Ol</button>

                    <div class="auth-divider">
                        <span>veya</span>
                    </div>

                    <button type="button" class="auth-social">
                        <i class="fab fa-google"></i>
                        Google ile Kayıt Ol
                    </button>
                </form>
            </div>

            <script src="${pageContext.request.contextPath}/js/auth.js"></script> <%-- JS yolu güncellendi --%>
            </body>
            </html>