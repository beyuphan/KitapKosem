<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="tr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${not empty book ? book.title : 'Kitap Detayı'}" /> | KitapKöşem</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

            </head>
            <body>
                <jsp:include page="sidebar.jsp" />

            <div class="main">
                <jsp:include page="header.jsp" />

                <div class="page-container">
                    <c:choose>
                        <c:when test="${not empty book}">
                            <div class="book-detail-container">
                                <%-- Kitap Başlık ve Kapak Alanı --%>
                                <div class="book-header">
                                    <div class="book-cover-container">
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
                                        <img src="${finalCoverUrl}" alt="<c:out value='${book.title}'/> Kapak" class="book-detail-cover">
                                    </div>

                                    <div class="book-info">
                                        <h1 class="book-detail-title"><c:out value="${book.title}"/></h1>
                                        <p class="book-detail-author">Yazar: <c:out value="${book.author}"/></p>

                                        <div class="book-detail-description">
                                            <h4>Açıklama:</h4>
                                            <p><c:out value="${book.description}" escapeXml="false"/></p>
                                        </div>

                                        <div class="book-meta">
                                            <div class="book-detail-actions" style="margin-top: 15px; margin-bottom:20px; display: flex; align-items: center; gap: 20px;">
                                                <%-- Beğeni Sayısı --%>
                                                <span class="like-count book-like-count">
                                                    <i class="fas fa-heart" style="color: var(--c-text-secondary);"></i> <c:out value="${book.likesCount}"/> beğeni
                                                </span>

                                                <%-- Beğenme/Beğeniyi Geri Alma Butonu (Sadece giriş yapmış kullanıcılar için) --%>
                                                <c:if test="${not empty sessionScope.loggedInUser}">
                                                    <form action="${pageContext.request.contextPath}/likeBook" method="POST" style="display: inline;">
                                                        <input type="hidden" name="bookId" value="${book.bookId}">
                                                            <input type="hidden" name="sourcePage" value="detail"> <%-- Kaynak sayfa "detail" --%>
                                                                <c:choose>
                                                                    <c:when test="${book.likedByCurrentUser}">
                                                                        <input type="hidden" name="action" value="unlike">
                                                                            <button type="submit" class="like-btn book-like-btn liked" title="Beğeniyi Geri Al">
                                                                                <i class="fas fa-heart"></i> Beğenildi
                                                                            </button>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <input type="hidden" name="action" value="like">
                                                                                <button type="submit" class="like-btn book-like-btn" title="Beğen">
                                                                                    <i class="far fa-heart"></i> Beğen
                                                                                </button>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                        </form>
                                                                    </c:if>
                                                                    </div>
                                                                    <div class="book-meta-item">
                                                                        <i class="fas fa-star"></i>
                                                                        <span>
                                                                            <strong>Ortalama Puan:</strong> 
                                                                            <c:choose>
                                                                                <c:when test="${averageRatingValue > 0}">
                                                                                    <span class="rating-stars">
                                                                                        <%-- Dolu Yıldızlar --%>
                                                                                        <c:if test="${fullStars > 0}">
                                                                                            <c:forEach begin="1" end="${fullStars}">
                                                                                                <i class="fas fa-star"></i>
                                                                                            </c:forEach>
                                                                                        </c:if>
                                                                                        <%-- Yarım Yıldız --%>
                                                                                        <c:if test="${hasHalfStar}">
                                                                                            <i class="fas fa-star-half-alt"></i>
                                                                                        </c:if>
                                                                                        <%-- Boş Yıldızlar --%>
                                                                                        <c:if test="${emptyStars > 0}">
                                                                                            <c:forEach begin="1" end="${emptyStars}">
                                                                                                <i class="far fa-star"></i>
                                                                                            </c:forEach>
                                                                                        </c:if>
                                                                                    </span>
                                                                                    (<fmt:formatNumber value="${averageRatingValue}" maxFractionDigits="1"/> / 5)
                                                                                    <%-- (<c:out value="${fn:length(reviews)}"/> değerlendirme) --%> <%-- Yorum sayısı fn:length ile --%>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                    Henüz puanlanmamış.
                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </span>
                                                                    </div>


                                                                    <div class="book-meta-item">
                                                                        <i class="fas fa-building"></i>
                                                                        <span><strong>Yayınevi:</strong> <c:out value="${not empty book.publisher ? book.publisher : 'Belirtilmemiş'}"/></span>
                                                                    </div>
                                                                    <div class="book-meta-item">
                                                                        <i class="fas fa-barcode"></i>
                                                                        <span><strong>ISBN:</strong> <c:out value="${not empty book.isbn ? book.isbn : 'Belirtilmemiş'}"/></span>
                                                                    </div>
                                                                    <div class="book-meta-item">
                                                                        <i class="fas fa-calendar-alt"></i>
                                                                        <span><strong>Yayın Yılı:</strong> <c:out value="${book.publicationYear > 0 ? book.publicationYear : 'Belirtilmemiş'}"/></span>
                                                                    </div>

                                                                    <c:if test="${not empty book.uploaderUsername}">
                                                                        <div class="book-meta-item">
                                                                            <i class="fas fa-user-edit"></i> <%-- Veya fas fa-user --%>
                                                                            <span>
                                                                                <strong>Ekleyen:</strong> 
                                                                                <a href="${pageContext.request.contextPath}/profile?username=${book.uploaderUsername}" class="uploader-link">
                                                                                    @<c:out value="${book.uploaderUsername}"/>
                                                                                </a>
                                                                            </span>
                                                                        </div>
                                                                    </c:if>


                                                                    <c:if test="${not empty sessionScope.loggedInUser && sessionScope.loggedInUser.userId == book.addedByUserId}">
                                                                        <div class="book-detail-delete-action" style="position:absolute; right:0; top:0; border-top: 1px solid var(--c-border-primary); text-align: right;">
                                                                            <form action="${pageContext.request.contextPath}/deleteBook" method="POST" onsubmit="return confirm('Bu kitabı silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.');">
                                                                                <input type="hidden" name="bookId" value="${book.bookId}">
                                                                                    <%-- Silme işlemi kitap detay sayfasından yapıldığı için, ana sayfaya yönlendirebiliriz --%>
                                                                                    <%-- sourcePageUsername göndermeye gerek yok, DeleteBookServlet varsayılan olarak /index'e yönlendirebilir --%>
                                                                                    <button type="submit" class="delete-btn" title="Kitabı Sil"> <%-- Daha büyük bir buton için .delete-btn --%>
                                                                                        <i class="fas fa-trash-alt"></i> Bu Kitabı Sil
                                                                                    </button>
                                                                            </form>
                                                                        </div>
                                                                    </c:if>
                                                                    </div>
                                                                    </div>
                                                                    </div>

                                                                    <%-- Yorumlar Bölümü --%>
                                                                    <div class="comments-section">
                                                                        <h3 class="comments-title">Yorumlar (<c:out value="${fn:length(reviews)}"/>)</h3>

                                                                        <c:choose>
                                                                            <c:when test="${not empty reviews}">
                                                                                <div class="comments-grid">
                                                                                    <c:forEach var="review" items="${reviews}">
                                                                                        <div class="comment-card">
                                                                                            <div class="comment-header">
                                                                                                <div class="comment-avatar">
                                                                                                    <%-- YENİ AVATAR GÖSTERİM MANTIĞI --%>
                                                                                                    <a href="${pageContext.request.contextPath}/profile?username=${review.username}">
                                                                                                        <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                                                                                                        <c:set var="commenterAvatarUrl" value="${review.userProfileAvatarUrl}" />
                                                                                                        <c:set var="defaultInitial" value="${fn:toUpperCase(fn:substring(review.username, 0, 1))}" />

                                                                                                        <c:choose>
                                                                                                            <c:when test="${not empty commenterAvatarUrl}">
                                                                                                                <c:choose>
                                                                                                                    <c:when test="${fn:startsWith(commenterAvatarUrl, 'http://') or fn:startsWith(commenterAvatarUrl, 'https://')}">
                                                                                                                        <img src="${commenterAvatarUrl}" alt="${review.username}'s avatar" class="commenter-avatar-img">
                                                                                                                        </c:when>
                                                                                                                        <c:otherwise>
                                                                                                                            <img src="${contextPath}${fn:startsWith(commenterAvatarUrl, '/') ? '' : '/'}${commenterAvatarUrl}" alt="${review.username}'s avatar" class="commenter-avatar-img">
                                                                                                                            </c:otherwise>
                                                                                                                        </c:choose>
                                                                                                                    </c:when>
                                                                                                                    <c:otherwise>
                                                                                                                        <%-- Resim yoksa baş harfi göster (bunun için CSS gerekebilir) --%>
                                                                                                                        <span class="commenter-avatar-initial">${defaultInitial}</span>
                                                                                                                    </c:otherwise>
                                                                                                                </c:choose>
                                                                                                                </a>
                                                                                                                </div>
                                                                                                                <div class="comment-user">
                                                                                                                    <div class="comment-username">
                                                                                                                        <a href="${pageContext.request.contextPath}/profile?username=${review.username}" class="comment-username-link">
                                                                                                                            <c:out value="${review.username}"/>
                                                                                                                        </a>
                                                                                                                    </div>
                                                                                                                    <div class="comment-date">
                                                                                                                        <fmt:formatDate value="${review.reviewDate}" pattern="dd MMMM yyyy, HH:mm"/>
                                                                                                                    </div>
                                                                                                                </div>
                                                                                                                <c:if test="${review.rating != null && review.rating > 0}">
                                                                                                                    <div class="comment-rating">
                                                                                                                        <c:forEach begin="1" end="${review.rating}">
                                                                                                                            <i class="fas fa-star"></i>
                                                                                                                        </c:forEach>
                                                                                                                        <c:forEach begin="1" end="${5 - review.rating}">
                                                                                                                            <i class="far fa-star"></i>
                                                                                                                        </c:forEach>
                                                                                                                    </div>
                                                                                                                </c:if>
                                                                                                                </div>
                                                                                                                <div class="comment-body">
                                                                                                                    <p class="comment-text"  id="comment-text-${review.reviewId}"><c:out value="${review.commentText}"/></p>
                                                                                                                    <c:if test="${fn:length(review.commentText) > 200}"> <%-- 200 karakterden uzunsa (bu sayıyı ayarla) --%>
                                                                                                                        <span class="toggle-comment-text" data-target-id="comment-text-${review.reviewId}">Devamını Oku</span>
                                                                                                                    </c:if>
                                                                                                                </div>

                                                                                                                <div class="action-form">
                                                                                                                    <div class="review-actions" style="margin-top: 10px; display: flex; align-items: center;">
                                                                                                                        <c:if test="${not empty sessionScope.loggedInUser}">
                                                                                                                            <form action="${pageContext.request.contextPath}/likeReview" method="POST" style="display: inline-block; margin-right: 10px;">
                                                                                                                                <input type="hidden" name="reviewId" value="${review.reviewId}">
                                                                                                                                    <input type="hidden" name="bookId" value="${book.bookId}"> <%-- Geri yönlendirme için --%>
                                                                                                                                        <c:choose>
                                                                                                                                            <c:when test="${review.likedByCurrentUser}">
                                                                                                                                                <input type="hidden" name="action" value="unlike">
                                                                                                                                                    <button type="submit" class="like-btn liked">
                                                                                                                                                        <i class="fas fa-heart"></i> Beğeniyi Geri Al
                                                                                                                                                    </button>
                                                                                                                                                </c:when>
                                                                                                                                                <c:otherwise>
                                                                                                                                                    <input type="hidden" name="action" value="like">
                                                                                                                                                        <button type="submit" class="like-btn">
                                                                                                                                                            <i class="far fa-heart"></i> Beğen
                                                                                                                                                        </button>
                                                                                                                                                    </c:otherwise>
                                                                                                                                                </c:choose>
                                                                                                                                                </form>
                                                                                                                                            </c:if>
                                                                                                                                            <span class="like-count">
                                                                                                                                                <i class="fas fa-thumbs-up" style="color: var(--c-text-secondary);"></i> <c:out value="${review.likesCount}"/> beğeni
                                                                                                                                            </span>
                                                                                                                                            </div>
                                                                                                                                            <%-- YENİ EKLENEN YORUM SİLME BUTONU --%>
                                                                                                                                            <c:if test="${not empty sessionScope.loggedInUser && sessionScope.loggedInUser.userId == review.userId}">
                                                                                                                                                <form action="${pageContext.request.contextPath}/deleteReview" method="POST" onsubmit="return confirm('Bu yorumu silmek istediğinizden emin misiniz?');" style="display: inline;">
                                                                                                                                                    <input type="hidden" name="reviewId" value="${review.reviewId}">
                                                                                                                                                        <input type="hidden" name="bookId" value="${book.bookId}"> <%-- Geri yönlendirme için kitabın ID'si --%>
                                                                                                                                                            <button type="submit" class="delete-btn-small" title="Yorumu Sil">
                                                                                                                                                                <i class="fas fa-trash-alt"></i> Sil
                                                                                                                                                            </button>
                                                                                                                                                            </form>
                                                                                                                                                        </c:if>
                                                                                                                                                        <%-- YENİ EKLENEN YORUM SİLME BUTONU BİTİŞİ --%>
                                                                                                                                                        </div>
                                                                                                                                                        </div>
                                                                                                                                                    </c:forEach>
                                                                                                                                                    </div>
                                                                                                                                                </c:when>
                                                                                                                                                <c:otherwise>
                                                                                                                                                    <div class="no-comments">
                                                                                                                                                        Bu kitap için henüz yorum yapılmamış.
                                                                                                                                                    </div>
                                                                                                                                                </c:otherwise>
                                                                                                                                            </c:choose>

                                                                                                                                            <%-- Yorum Formu --%>
                                                                                                                                            <c:if test="${not empty sessionScope.reviewSuccess}">
                                                                                                                                                <div class="form-message success-message">
                                                                                                                                                    <c:out value="${sessionScope.reviewSuccess}"/>
                                                                                                                                                </div>
                                                                                                                                                <% session.removeAttribute("reviewSuccess"); %>
                                                                                                                                            </c:if>

                                                                                                                                            <c:if test="${not empty sessionScope.reviewError}">
                                                                                                                                                <div class="form-message error-message">
                                                                                                                                                    <c:out value="${sessionScope.reviewError}"/>
                                                                                                                                                </div>
                                                                                                                                                <% session.removeAttribute("reviewError"); %>
                                                                                                                                            </c:if>

                                                                                                                                            <c:choose>
                                                                                                                                                <c:when test="${not empty sessionScope.loggedInUser}">
                                                                                                                                                    <div class="review-form-container">
                                                                                                                                                        <h4 class="review-form-title">Yorum Yap / Puan Ver</h4>
                                                                                                                                                        <form action="${pageContext.request.contextPath}/addReview" method="POST" class="add-review-form">
                                                                                                                                                            <input type="hidden" name="bookId" value="${book.bookId}">
                                                                                                                                                                <input type="hidden" name="rating" id="selected-rating" value="">

                                                                                                                                                                    <div class="rating-input">
                                                                                                                                                                        <label for="rating">Puanınız:</label>
                                                                                                                                                                        <div class="star-rating" id="star-rating">
                                                                                                                                                                            <i class="far fa-star" data-rating="1"></i>
                                                                                                                                                                            <i class="far fa-star" data-rating="2"></i>
                                                                                                                                                                            <i class="far fa-star" data-rating="3"></i>
                                                                                                                                                                            <i class="far fa-star" data-rating="4"></i>
                                                                                                                                                                            <i class="far fa-star" data-rating="5"></i>
                                                                                                                                                                        </div>
                                                                                                                                                                    </div>

                                                                                                                                                                    <textarea name="commentText" id="commentText" class="review-textarea" placeholder="Kitap hakkındaki düşünceleriniz..."></textarea>

                                                                                                                                                                    <button type="submit" class="submit-review-btn">Yorumu Gönder</button>
                                                                                                                                                                    </form>
                                                                                                                                                                    </div>
                                                                                                                                                                </c:when>
                                                                                                                                                                <c:otherwise>
                                                                                                                                                                    <div class="login-prompt">
                                                                                                                                                                        Yorum yapmak veya puan vermek için 
                                                                                                                                                                        <a href="${pageContext.request.contextPath}/auth.jsp?redirect=book?id=${book.bookId}" class="login-link">giriş yapmanız</a> 
                                                                                                                                                                        gerekmektedir.
                                                                                                                                                                    </div>
                                                                                                                                                                </c:otherwise>
                                                                                                                                                            </c:choose>
                                                                                                                                                            </div>
                                                                                                                                                            </div>
                                                                                                                                                        </c:when>
                                                                                                                                                        <c:otherwise>
                                                                                                                                                            <c:if test="${not empty sessionScope.globalError}">
                                                                                                                                                                <div class="form-message error-message" style="max-width: 800px; margin: 20px auto;">
                                                                                                                                                                    <c:out value="${sessionScope.globalError}"/>
                                                                                                                                                                </div>
                                                                                                                                                                <% session.removeAttribute("globalError");%>
                                                                                                                                                            </c:if>
                                                                                                                                                            <p class="no-content">Kitap bilgileri bulunamadı.</p>
                                                                                                                                                        </c:otherwise>
                                                                                                                                                    </c:choose>
                                                                                                                                                    </div>
                                                                                                                                                    </div>
                                                                                                                                                    <script src="${pageContext.request.contextPath}/js/script.js"></script>
                                                                                                                                                    <script src="${pageContext.request.contextPath}/js/bookDetail.js"></script>

                                                                                                                                                    </body>
                                                                                                                                                    </html>