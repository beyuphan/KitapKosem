<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="tr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>KitapKöşem</title>
    <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
            <script>
        const APP_CONTEXT_PATH = "${pageContext.request.contextPath}";
            </script>
            </head>
            <body>
                <jsp:include page="sidebar.jsp" />

            <div class="main">
                <jsp:include page="header.jsp" />

                <div class="page-container" style="padding-top: 20px;">
                    <c:if test="${not empty topLikedBooksList}">
                        <div class="top-liked-books-section" style="margin-bottom: 40px;">
                            <h2 class="page-title" style="text-align: center; font-size: 1.8rem; margin-bottom: 15px;"> En Çok Beğenilenler</h2>
                            <div class="books-container"> <%-- Mevcut .books-container stilini kullanır --%>
                                <c:forEach var="book" items="${topLikedBooksList}">
                                    <%-- BURAYA SENİN MEVCUT KİTAP KARTI KODUNU KOYACAĞIZ --%>
                                    <%-- Bu kart, ${book} değişkenini kullanarak bilgileri gösterecek --%>
                                    <div class="book-card">
                                        <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                                        <c:set var="dbCoverUrl" value="${book.coverImageUrl}" />
                                        <c:set var="defaultCoverUrl" value="${contextPath}/assets/book.png" /> 
                                        <c:set var="finalCoverUrl" value="${defaultCoverUrl}" />
                                        <c:if test="${not empty dbCoverUrl}"><c:choose><c:when test="${fn:startsWith(dbCoverUrl, 'http://') or fn:startsWith(dbCoverUrl, 'https://')}"><c:set var="finalCoverUrl" value="${dbCoverUrl}" /></c:when><c:otherwise><c:set var="finalCoverUrl" value="${contextPath}${fn:startsWith(dbCoverUrl, '/') ? '' : '/'}${dbCoverUrl}" /></c:otherwise></c:choose></c:if>
                                        <a href="${contextPath}/book?id=${book.bookId}" class="book-cover-link">
                                            <img src="${finalCoverUrl}" alt="<c:out value='${book.title}'/>" class="book-cover">
                                        </a>
                                        <div class="book-details">
                                            <h3 class="book-title"><a href="${contextPath}/book?id=${book.bookId}"><c:out value="${book.title}"/></a></h3>
                                            <p class="book-author"><c:out value="${book.author}"/></p>
                                            <div class="rating">
                                                <c:choose><c:when test="${book.averageRating > 0}"><span class="rating-stars"><c:if test="${book.fullStars > 0}"><c:forEach begin="1" end="${book.fullStars}"><i class="fas fa-star"></i></c:forEach></c:if><c:if test="${book.hasHalfStar}"><i class="fas fa-star-half-alt"></i></c:if><c:if test="${book.emptyStars > 0}"><c:forEach begin="1" end="${book.emptyStars}"><i class="far fa-star"></i></c:forEach></c:if></span><span class="rating-value">(<fmt:formatNumber value="${book.averageRating}" maxFractionDigits="1"/> / 5)</span></c:when><c:otherwise><span class="rating-stars"><c:forEach begin="1" end="5"><i class="far fa-star"></i></c:forEach></span><span class="rating-value">(Puanlanmamış)</span></c:otherwise></c:choose>
                                                            </div>

                                                            <div class="uploader-action">

                                                <c:if test="${not empty book.uploaderUsername}"><p class="book-uploader"><i class="fas fa-user-edit" style="margin-right: 5px; color: var(--c-text-secondary);"></i>Ekleyen: <a href="${contextPath}/profile?username=${book.uploaderUsername}" class="uploader-link">@<c:out value="${book.uploaderUsername}"/></a></p></c:if>

                                                    <div class="book-actions" style="margin-top: 10px; display: flex; column-gap:10px ; align-items: center;"
                                                         <span class="like-count book-like-count"><i class="fas fa-heart" style="color: var(--c-text-secondary);"></i> <c:out value="${book.likesCount}"/></span>
                                                    <c:if test="${not empty sessionScope.loggedInUser}"><form action="${contextPath}/likeBook" method="POST" style="display: inline;"><input type="hidden" name="bookId" value="${book.bookId}"><input type="hidden" name="sourcePage" value="index"><c:choose><c:when test="${book.likedByCurrentUser}"><input type="hidden" name="action" value="unlike"><button type="submit" class="like-btn book-like-btn liked" title="Beğeniyi Geri Al"><i class="fas fa-heart"></i></button></c:when><c:otherwise><input type="hidden" name="action" value="like"><button type="submit" class="like-btn book-like-btn" title="Beğen"><i class="far fa-heart"></i></button></c:otherwise></c:choose></form></c:if>
                                                                                    </div>
                                                                                    </div>
                                                                                    </div>
                                                                                    </div>
                                                                    </c:forEach>
                                                                    </div>
                                                                    </div>
                                                                </c:if>

                                                                <%-- YENİ EKLENEN KISIM: Arama Bilgisi ve Mesajları --%>
                                                                <c:if test="${not empty searchQuery}">
                                                                    <div class="search-info">
                                                                        <h3>Arama Sonuçları: "<c:out value="${searchQuery}"/>"</h3>
                                                                        <c:if test="${not empty searchMessage}"> <%-- Arama sonucu bulunamadıysa mesaj --%>
                                                                            <p><c:out value="${searchMessage}"/></p>
                                                                        </c:if>
                                                                    </div>
                                                                </c:if>
                                                                <c:if test="${empty searchQuery}"> <%-- Arama yapılmadıysa normal başlık --%>
                                                                    <h2 class="page-title">En Yeni Kitaplar</h2>
                                                                </c:if>
                                                                <%-- YENİ EKLENEN KISIM BİTİŞİ --%>

                                                                <%-- Kitap Ekleme sonrası gösterilecek mesaj --%>
                                                                <c:if test="${not empty sessionScope.addBookSuccess}">
                                                                    <div class="form-message success-message">
                                                                        <c:out value="${sessionScope.addBookSuccess}"/>
                                                                    </div>
                                                                    <% session.removeAttribute("addBookSuccess");%>
                                                                </c:if>

                                                                <%-- Genel hata mesajı (örn: kitaplar listelenirken sorun oluştu) --%>
                                                                <c:if test="${not empty requestScope.globalError}">
                                                                    <div class="form-message error-message">
                                                                        <c:out value="${requestScope.globalError}"/>
                                                                    </div>
                                                                </c:if>

                                                                <div class="books-container"> <%-- Bu class için style.css'de stillerin vardı --%>
                                                                    <c:choose>
                                                                        <c:when test="${not empty bookList}">
                                                                            <c:forEach var="book" items="${bookList}">
                                                                                <div class="book-card">
                                                                                    <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                                                                                    <c:set var="dbCoverUrl" value="${book.coverImageUrl}" />
                                                                                    <c:set var="defaultCoverUrl" value="${contextPath}/assets/book.png" /> 
                                                                                    <c:set var="finalCoverUrl" value="${defaultCoverUrl}" />

                                                                                    <c:if test="${not empty dbCoverUrl}">
                                                                                        <c:choose>
                                                                                            <c:when test="${fn:startsWith(dbCoverUrl, 'http://') or fn:startsWith(dbCoverUrl, 'https://')}">
                                                                                                <c:set var="finalCoverUrl" value="${dbCoverUrl}" />
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <c:set var="finalCoverUrl" value="${contextPath}${fn:startsWith(dbCoverUrl, '/') ? '' : '/'}${dbCoverUrl}" />
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </c:if>

                                                                                    <a href="${pageContext.request.contextPath}/book?id=${book.bookId}" class="book-cover-link">
                                                                                        <img src="${finalCoverUrl}" alt="<c:out value='${book.title}'/>" class="book-cover">
                                                                                    </a>

                                                                                    <div class="book-details">
                                                                                        <h3 class="book-title">
                                                                                            <a href="${pageContext.request.contextPath}/book?id=${book.bookId}"><c:out value="${book.title}"/></a>
                                                                                        </h3>



                                                                                        <p class="book-author"><c:out value="${book.author}"/></p>



                                                                                        <div class="rating">
                                                                                            <c:choose>
                                                                                                <c:when test="${book.averageRating > 0}">
                                                                                                    <%-- Yıldızları saran span için class="rating-stars" kullanıyoruz --%>
                                                                                                    <span class="rating-stars"> 
                                                                                                        <c:if test="${book.fullStars > 0}">
                                                                                                            <c:forEach begin="1" end="${book.fullStars}">
                                                                                                                <i class="fas fa-star"></i>
                                                                                                            </c:forEach>
                                                                                                        </c:if>
                                                                                                        <c:if test="${book.hasHalfStar}">
                                                                                                            <i class="fas fa-star-half-alt"></i>
                                                                                                        </c:if>
                                                                                                        <c:if test="${book.emptyStars > 0}">
                                                                                                            <c:forEach begin="1" end="${book.emptyStars}">
                                                                                                                <i class="far fa-star"></i>
                                                                                                            </c:forEach>
                                                                                                        </c:if>
                                                                                                    </span>
                                                                                                    <span class="rating-value">
                                                                                                        <%-- Sayısal puanı formatlamak için fmt:formatNumber kullanalım --%>
                                                                                                        (<fmt:formatNumber value="${book.averageRating}" maxFractionDigits="1"/> / 5)
                                                                                                    </span>
                                                                                                </c:when>
                                                                                                <c:otherwise>
                                                                                                    <%-- Puanlanmamışsa da rating-stars class'ını kullanalım ki stil tutarlı olsun --%>
                                                                                                    <span class="rating-stars">
                                                                                                        <c:forEach begin="1" end="5"><i class="far fa-star"></i></c:forEach>
                                                                                                        </span>
                                                                                                        <span class="rating-value">(Puanlanmamış)</span>
                                                                                                </c:otherwise>
                                                                                            </c:choose>
                                                                                        </div>
                                                                                        <div class="uploader-action">
                                                                                            <%-- YENİ EKLENEN KISIM: Kitabı Ekleyen Kullanıcı --%>
                                                                                            <c:if test="${not empty book.uploaderUsername}">
                                                                                                <p class="book-uploader">
                                                                                                <i class="fas fa-user-edit" style="margin-right: 5px; color: var(--c-text-secondary);"></i> <%-- Küçük bir ikon (opsiyonel) --%>
                                                                                                Ekleyen: 
                                                                                                <a href="${pageContext.request.contextPath}/profile?username=${book.uploaderUsername}" class="uploader-link">
                                                                                                    @<c:out value="${book.uploaderUsername}"/>
                                                                                                </a>
                                                                                                </p>
                                                                                            </c:if>
                                                                                            <div class="book-actions" style="margin-top: 10px; display: flex; align-items: center; column-gap: 10px">
                                                                                                <%-- Beğeni Sayısı --%>
                                                                                                <span class="like-count book-like-count" id="like-count-${book.bookId}"> <%-- Yeni class: book-like-count --%>
                                                                                                    <i class="fas fa-heart" style="color: var(--c-text-secondary);"></i> <c:out value="${book.likesCount}"/>
                                                                                                </span>

                                                                                                <%-- Beğenme/Beğeniyi Geri Alma Butonu (Sadece giriş yapmış kullanıcılar için) --%>
                                                                                                <c:if test="${not empty sessionScope.loggedInUser}">
                                                                                                    <%-- Formun submit'ini JS ile yakalayacağımız için action ve method'u JS'te de yönetebiliriz
                                                                                                         ama form etiketinin kalması, JS olmayan durumlar için bir fallback olabilir.
                                                                                                         Şimdilik JS ile yöneteceğimiz için forma da bir ID verelim. --%>
                                                                                                    <form class="like-book-form" data-book-id="${book.bookId}" style="display: inline;">
                                                                                                        <%-- action="${pageContext.request.contextPath}/likeBook" method="POST" --%>
                                                                                                        <%-- Bu inputlar JS tarafından okunup AJAX ile gönderilecek --%>
                                                                                                        <input type="hidden" name="bookId" value="${book.bookId}">
                                                                                                            <input type="hidden" name="sourcePage" value="index">

                                                                                                                <c:choose>
                                                                                                                    <c:when test="${book.likedByCurrentUser}">
                                                                                                                        <input type="hidden" name="action" value="unlike">
                                                                                                                            <button type="submit" class="like-btn book-like-btn liked" title="Beğeniyi Geri Al">
                                                                                                                                <i class="fas fa-heart"></i>
                                                                                                                            </button>
                                                                                                                        </c:when>
                                                                                                                        <c:otherwise>
                                                                                                                            <input type="hidden" name="action" value="like">
                                                                                                                                <button type="submit" class="like-btn book-like-btn" title="Beğen">
                                                                                                                                    <i class="far fa-heart"></i>
                                                                                                                                </button>
                                                                                                                            </c:otherwise>
                                                                                                                        </c:choose>
                                                                                                                        </form>
                                                                                                                    </c:if>
                                                                                                                    </div>
                                                                                                                    </div>
                                                                                                                    </div>
                                                                                                                    </div>
                                                                                                                </c:forEach>
                                                                                                            </c:when>
                                                                                                            <c:otherwise>
                                                                                                                <c:if test="${empty searchQuery && empty requestScope.globalError}">
                                                                                                                    <p class="no-books-message">Sistemde henüz hiç kitap bulunmuyor. <a href="${pageContext.request.contextPath}/addBook">İlk kitabı sen ekle!</a></p>
                                                                                                                </c:if>
                                                                                                            </c:otherwise>
                                                                                                        </c:choose>
                                                                                                        </div>
                                                                                                        </div>
                                                                                                        </div>

                                                                                                        <script src="${pageContext.request.contextPath}/js/script.js"></script>
                                                                                                        <script src="${pageContext.request.contextPath}/js/index.js"></script>


                                                                                                        </body>
                                                                                                        </html>