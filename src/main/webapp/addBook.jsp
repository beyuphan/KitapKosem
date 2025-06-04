<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- fn:startsWith gibi fonksiyonlar gerekirse diye ekleyelim --%>

<%-- Giriş yapılmamışsa auth.jsp'ye yönlendir --%>
<c:if test="${empty sessionScope.loggedInUser}">
    <c:redirect url="auth.jsp">
        <c:param name="redirect" value="addBook.jsp" /> <%-- Giriş yaptıktan sonra buraya geri dönmesi için --%>
    </c:redirect>
</c:if>

<!DOCTYPE html>
<html lang="tr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Yeni Kitap Ekle | KitapKöşem</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">    
            </head>
            <body>
                <jsp:include page="sidebar.jsp" />

            <div class="main">
                <jsp:include page="header.jsp" />

                <div class="page-container" style="padding: 20px;">
                    <h2>Yeni Kitap Ekle</h2>

                    <%-- Hata veya Başarı Mesajları (AddBookServlet'ten gelecek) --%>
                    <% String formError = (String) session.getAttribute("addBookError"); %>
                    <% if (formError != null) {%>
                    <div class="form-message error-message">
                        <%= formError%>
                    </div>
                    <% session.removeAttribute("addBookError"); %>
                    <% } %>

                    <% String formSuccess = (String) session.getAttribute("addBookSuccess"); %>
                    <% if (formSuccess != null) {%>
                    <div class="form-message success-message">
                        <%= formSuccess%>
                    </div>
                    <% session.removeAttribute("addBookSuccess"); %>
                    <% }%>

                    <form class="add-book-form" method="POST"  enctype="multipart/form-data" action="${pageContext.request.contextPath}/addBook">
                        <div class="input-group">
                            <label for="title">Kitap Başlığı:</label>
                            <input type="text" id="title" name="title" required>
                        </div>

                        <div class="input-group">
                            <label for="author">Yazar:</label>
                            <input type="text" id="author" name="author" required>
                        </div>

                        <div class="input-group">
                            <label for="description">Açıklama:</label>
                            <textarea id="description" name="description" rows="5"></textarea>
                        </div>

                        <div class="input-group">
                            <label for="isbn">ISBN (Opsiyonel):</label>
                            <input type="text" id="isbn" name="isbn">
                        </div>

                        <div class="input-group">
                            <label for="publisher">Yayınevi (Opsiyonel):</label>
                            <input type="text" id="publisher" name="publisher">
                        </div>

                        <div class="input-group">
                            <label for="publicationYear">Yayın Yılı (Opsiyonel):</label>
                            <input type="number" id="publicationYear" name="publicationYear" min="0" max="<%=(new java.util.Date()).getYear() + 1900 + 1%>"> <%-- Min 0, Max gelecek yıl --%>
                        </div>

                        <div class="input-group">
                            <label for="coverImageUrl">Kapak Fotoğrafı (Opsiyonel):</label>
                            <input type="file" id="coverImageUrl" name="coverImageFile" accept="image/png, image/jpeg">
                        </div>

                        <div class="form-actions">
                            <button type="submit">Kitabı Ekle</button>
                        </div>
                    </form>
                </div>
            </div>
            <script src="${pageContext.request.contextPath}/js/script.js"></script>

            </body>
            </html>