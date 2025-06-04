<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%-- JSTL Core kütüphanesi --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- YENİ EKLENDİ --%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="tr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%-- Sayfa başlığını dinamik olarak profil kullanıcısının adıyla ayarlayalım --%>
    <title><c:out value="${profileUser.fullName != null ? profileUser.fullName : profileUser.username}" /> | KitapKöşem</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            </head>
            <body>
                <jsp:include page="sidebar.jsp" />

            <div class="main">
                <jsp:include page="header.jsp" />

                <div class="profile-container">
                    <c:if test="${not empty profileUser}"> <%-- profileUser varsa içeriği göster --%>
                        <div class="profile-header">
                            <%-- Kapak fotoğrafı: Varsa kullanıcınınki, yoksa varsayılan --%>
                            <%-- Kapak fotoğrafı için URL'yi alalım --%>
                            <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                            <c:set var="dbCoverUrl" value="${profileUser.coverPhotoUrl}" />
                            <c:set var="defaultCoverUrl" value="${contextPath}/assets/logo.jpg" />

                            <c:set var="coverPhoto"> <%-- Değişkeni burada set ediyoruz --%>
                                <c:choose>
                                    <c:when test="${not empty dbCoverUrl}">
                                        <c:choose>
                                            <c:when test="${fn:startsWith(dbCoverUrl, 'http://') or fn:startsWith(dbCoverUrl, 'https://')}">
                                                <c:out value="${dbCoverUrl}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:out value="${contextPath}${fn:startsWith(dbCoverUrl, '/') ? '' : '/'}${dbCoverUrl}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${defaultCoverUrl}"/>
                                    </c:otherwise>
                                </c:choose>
                            </c:set>
                            <div class="cover-photo" style="background-image: url('${coverPhoto}')"></div>

                            <div class="profile-info">
                                <div class="profile-avatar">
                                    <%-- Profil avatarı için URL'yi alalım (contextPath yukarıda zaten set edildi) --%>
                                    <c:set var="dbAvatarUrl" value="${profileUser.profileAvatarUrl}" />
                                    <c:set var="defaultAvatarUrl" value="${contextPath}/assets/user-avatar.jpg" />

                                    <c:set var="avatarPhoto"> <%-- Değişkeni burada set ediyoruz --%>
                                        <c:choose>
                                            <c:when test="${not empty dbAvatarUrl}">
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(dbAvatarUrl, 'http://') or fn:startsWith(dbAvatarUrl, 'https://')}">
                                                        <c:out value="${dbAvatarUrl}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:out value="${contextPath}${fn:startsWith(dbAvatarUrl, '/') ? '' : '/'}${dbAvatarUrl}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <c:out value="${defaultAvatarUrl}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:set>
                                    <img src="${avatarPhoto}" alt="Profil Fotoğrafı">
                                </div>

                                <div class="profile-details">
                                    <h1 class="profile-name">
                                        <c:out value="${not empty profileUser.fullName ? profileUser.fullName : profileUser.username}" />
                                    </h1>
                                    <p class="profile-username">@<c:out value="${profileUser.username}" /></p>

                                    <div class="profile-stats">
                                        <a href="#" class="stat-link" data-modal-trigger="followersContentModal">
                                            <span class="stat-number"><c:out value="${followerCount}"/></span>
                                            <span class="stat-label">Takipçi</span>
                                        </a>
                                        <a href="#" class="stat-link" data-modal-trigger="followingsContentModal">
                                            <span class="stat-number"><c:out value="${followingCount}"/></span>
                                            <span class="stat-label">Takip Edilen</span>
                                        </a>

                                        <%-- Kitap sayısı aynı kalacak --%>
                                        <div class="stat-item">
                                            <span class="stat-number">0</span> <%-- TODO: Dinamik kitap sayısı --%>
                                            <span class="stat-label">Kitap</span>
                                        </div>
                                    </div>


                                    <p class="profile-bio">
                                        <c:choose>
                                            <c:when test="${not empty profileUser.bio}">
                                                <c:out value="${profileUser.bio}" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:if test="${isOwnProfile}">
                                                    Biyografinizi eklemek için profilinizi düzenleyin.
                                                </c:if>
                                                <c:if test="${not isOwnProfile}">
                                                    Kullanıcının henüz bir biyografisi yok.
                                                </c:if>
                                            </c:otherwise>
                                        </c:choose>
                                    </p>

                                    <div class="profile-actions">
                                        <c:if test="${isOwnProfile}">
                                            <button class="edit-profile-btn" onclick="window.location.href = '${pageContext.request.contextPath}/editProfile'">
                                                <i class="fas fa-edit"></i> Profili Düzenle
                                            </button>
                                        </c:if>
                                        <c:if test="${not isOwnProfile and not empty sessionScope.loggedInUser}">
                                            <%-- YENİ: Dinamik Takip Et/Takipten Çık Butonu --%>
                                            <form action="${pageContext.request.contextPath}/toggleFollow" method="POST" style="display: inline;">
                                                <input type="hidden" name="profileUserId" value="${profileUser.userId}">
                                                    <input type="hidden" name="action" value="${isCurrentlyFollowing ? 'unfollow' : 'follow'}">
                                                        <button type="submit" class="follow-btn ${isCurrentlyFollowing ? 'following' : ''}">
                                                            <c:choose>
                                                                <c:when test="${isCurrentlyFollowing}">
                                                                    <i class="fas fa-user-check"></i> Takip Ediliyor
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <i class="fas fa-user-plus"></i> Takip Et
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </button>
                                                        </form>
                                                        <button class="message-btn"> <%-- Mesaj butonu şimdilik işlevsiz kalabilir --%>
                                                            <i class="fas fa-envelope"></i>
                                                        </button>
                                                    </c:if>
                                                    </div>
                                                    </div>
                                                    </div>
                                                    </div>

                                                </c:if>

                                                <nav class="profile-nav">
                                                    <ul>
                                                        <%-- Başlangıçta "Kitaplığım" sekmesi aktif olsun --%>
                                                        <li class="active"><a href="#" data-tab-target="kitapligimContent">Kitaplık</a></li>
                                                        <li><a href="#" data-tab-target="yorumlarimContent">Yorumlar</a></li>
                                                        <li><a href="#" data-tab-target="begendiklerimContent">Beğeniler</a></li> <%-- YENİ SEKME --%>
                                                            <%-- İleride eklenecek diğer sekmeler buraya gelebilir (Beğenilerim, Listelerim vb.) --%>
                                                    </ul>
                                                </nav>

                                                <%-- Profil Sekmelerinin İçerik Alanları --%>
                                                <div class="profile-tabs-content">
                                                    <%-- 1. Kitaplığım Sekmesi İçeriği --%>
                                                    <div id="kitapligimContent" class="profile-tab-pane active"> <%-- Başlangıçta bu aktif --%>
                                                        <h4><i class="fas fa-book-open"></i> Kitaplık (Eklenen Kitaplar)</h4>
                                                        <c:choose>
                                                            <c:when test="${not empty userAddedBooks}">
                                                                <div class="books-container profile-books-grid">
                                                                    <c:forEach var="book" items="${userAddedBooks}">
                                                                        <div class="book-card">
                                                                            <%-- Kitap kartı içeriği (index.jsp'dekine benzer) --%>
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
                                                                            <a href="${contextPath}/book?id=${book.bookId}" class="book-cover-link">
                                                                                <img src="${finalCoverUrl}" alt="<c:out value='${book.title}'/>" class="book-cover">
                                                                            </a>
                                                                            <div class="book-details">
                                                                                <h5 class="book-title-small">
                                                                                    <a href="${contextPath}/book?id=${book.bookId}"><c:out value="${book.title}"/></a>
                                                                                </h5>
                                                                                <p class="book-author-small"><c:out value="${book.author}"/></p>
                                                                                <div class="rating small-rating">
                                                                                    <c:if test="${book.averageRating > 0}">
                                                                                        <span class="rating-stars">
                                                                                            <c:if test="${book.fullStars > 0}"><c:forEach begin="1" end="${book.fullStars}"><i class="fas fa-star"></i></c:forEach></c:if>
                                                                                            <c:if test="${book.hasHalfStar}"><i class="fas fa-star-half-alt"></i></c:if>
                                                                                            <c:if test="${book.emptyStars > 0}"><c:forEach begin="1" end="${book.emptyStars}"><i class="far fa-star"></i></c:forEach></c:if>
                                                                                                </span>
                                                                                    </c:if>
                                                                                </div>


                                                                            </div>
                                                                        </div>
                                                                    </c:forEach>
                                                                </div>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <p><c:out value="${profileUser.username}"/> henüz hiç kitap eklememiş.</p>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>

                                                    <%-- 2. Yorumlarım Sekmesi İçeriği --%>
                                                    <div id="yorumlarimContent" class="profile-tab-pane">
                                                        <h4><i class="fas fa-comments"></i> Yorumlar (<c:out value="${fn:length(userReviewsList)}"/>)</h4>
                                                        <c:choose>
                                                            <c:when test="${not empty userReviewsList}">
                                                                <div class="user-reviews-list">
                                                                    <c:forEach var="review" items="${userReviewsList}">
                                                                        <div class="user-review-item">
                                                                            <div class="review-item-header">
                                                                                <span class="review-book-title">
                                                                                    Kitap: <a href="${pageContext.request.contextPath}/book?id=${review.bookId}"><c:out value="${review.bookTitle}"/></a>
                                                                                </span>
                                                                                <span class="review-date">
                                                                                    <fmt:formatDate value="${review.reviewDate}" pattern="dd MMMM yyyy, HH:mm"/>
                                                                                </span>
                                                                            </div>
                                                                            <c:if test="${review.rating != null && review.rating > 0}">
                                                                                <div class="rating small-rating comment-rating-display"> <%-- Yorum kartı içindeki puan için --%>
                                                                                    <span class="stars">
                                                                                        <c:forEach begin="1" end="${review.rating}"><i class="fas fa-star"></i></c:forEach>
                                                                                        <c:forEach begin="1" end="${5 - review.rating}"><i class="far fa-star"></i></c:forEach>
                                                                                        </span>
                                                                                    </div>
                                                                            </c:if>
                                                                            <div class="review-item-text">
                                                                                <p><c:out value="${review.commentText}"/></p>
                                                                            </div>

                                                                            <%-- YENİ EKLENEN YORUM SİLME BUTONU (Sadece kendi profilindeyse) --%>
                                                                            <c:if test="${isOwnProfile}"> <%-- Veya daha garantili: sessionScope.loggedInUser.userId == review.userId --%>
                                                                                <div class="review-delete-action" style="margin-top: 8px; text-align: right;">
                                                                                    <form action="${pageContext.request.contextPath}/deleteReview" method="POST" onsubmit="return confirm('Bu yorumu silmek istediğinizden emin misiniz?');" style="display: inline;">
                                                                                        <input type="hidden" name="reviewId" value="${review.reviewId}">
                                                                                            <input type="hidden" name="bookId" value="${review.bookId}"> <%-- Kitap ID'si, belki yönlendirme için --%>
                                                                                                <input type="hidden" name="sourcePageUsername" value="${profileUser.username}"> <%-- Profil sayfasına geri dönmek için --%>
                                                                                                    <button type="submit" class="delete-btn-small" title="Yorumu Sil">
                                                                                                        <i class="fas fa-trash-alt"></i> Sil
                                                                                                    </button>
                                                                                                    </form>
                                                                                                    </div>
                                                                                                </c:if>
                                                                                                <%-- YENİ EKLENEN YORUM SİLME BUTONU BİTİŞİ --%>
                                                                                                </div>
                                                                                            </c:forEach>
                                                                                            </div>
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                            <p><c:out value="${profileUser.username}"/> henüz hiç yorum yapmamış.</p>
                                                                                        </c:otherwise>
                                                                                    </c:choose>
                                                                                    </div>


                                                                                    <%-- 3. BeğenilerSekmesi İçeriği --%>

                                                                                    <div id="begendiklerimContent" class="profile-tab-pane">
                                                                                        <h4><i class="fas fa-thumbs-up"></i> Beğeniler (<c:out value="${fn:length(allLikedItems)}"/>)</h4>    

                                                                                        <c:choose>
                                                                                            <c:when test="${not empty requestScope.allLikedItems}">
                                                                                                <div class="liked-activity-list"> <%-- Yeni bir sarmalayıcı class --%>
                                                                                                    <c:forEach var="likedItem" items="${requestScope.allLikedItems}">
                                                                                                        <div class="liked-item-card">
                                                                                                            <c:choose>
                                                                                                                <%-- Eğer Beğenilen Öğe bir KİTAP ise --%>
                                                                                                                <c:when test="${likedItem.type == 'BOOK'}">
                                                                                                                    <c:set var="book" value="${likedItem.data}" />
                                                                                                                    <div class="liked-item-header">
                                                                                                                        <i class="fas fa-book" style="color: var(--c-accent); margin-right: 8px;"></i>
                                                                                                                        <strong><c:out value="${profileUser.username}"/></strong>, 
                                                                                                                        <a href="${pageContext.request.contextPath}/book?id=${book.bookId}"><c:out value="${book.title}"/></a> adlı kitabı beğendi.
                                                                                                                        <span class="liked-date">(<fmt:formatDate value="${likedItem.likedDate}" pattern="dd MMM yy, HH:mm"/>)</span>
                                                                                                                    </div>
                                                                                                                    <p class="liked-item-snippet">
                                                                                                                        <c:out value="${fn:substring(book.description, 0, 150)}"/>
                                                                                                                        <c:if test="${fn:length(book.description) > 150}">...</c:if>
                                                                                                                        </p>
                                                                                                                    <%-- Kitap için beğeni butonu (bu listede tekrar beğenme/beğeniyi geri alma) --%>
                                                                                                                    <div class="book-actions" style="margin-top: 5px; font-size:0.8em;">
                                                                                                                        <span class="like-count book-like-count" style="margin-right:10px;"><i class="fas fa-heart"></i> <c:out value="${book.likesCount}"/></span>
                                                                                                                        <c:if test="${not empty sessionScope.loggedInUser}"><form action="${pageContext.request.contextPath}/likeBook" method="POST" style="display: inline;"><input type="hidden" name="bookId" value="${book.bookId}"><input type="hidden" name="sourcePage" value="profileLikes"><input type="hidden" name="profileUsername" value="${profileUser.username}"><c:choose><c:when test="${book.likedByCurrentUser}"><input type="hidden" name="action" value="unlike"><button type="submit" class="like-btn book-like-btn liked" title="Beğeniyi Geri Al" style="padding: 3px 6px; font-size:0.8em;"><i class="fas fa-heart"></i></button></c:when><c:otherwise><input type="hidden" name="action" value="like"></c:otherwise></c:choose></form></c:if>
                                                                                                                                                            </div>


                                                                                                                                            </c:when>

                                                                                                                                            <%-- Eğer Beğenilen Öğe bir YORUM ise --%>
                                                                                                                                            <c:when test="${likedItem.type == 'REVIEW'}">
                                                                                                                                                <c:set var="review" value="${likedItem.data}" />
                                                                                                                                                <div class="liked-item-header">
                                                                                                                                                    <i class="fas fa-comment-dots" style="color: var(--c-accent); margin-right: 8px;"></i>
                                                                                                                                                    <strong><c:out value="${profileUser.username}"/></strong>,
                                                                                                                                                    <a href="${pageContext.request.contextPath}/profile?username=${review.username}"><c:out value="${review.username}"/></a> adlı kullanıcının
                                                                                                                                                    <a href="${pageContext.request.contextPath}/book?id=${review.bookId}#review-${review.reviewId}"><c:out value="${review.bookTitle}"/></a> kitabına yaptığı yorumu beğendi.
                                                                                                                                                    <span class="liked-date">(<fmt:formatDate value="${likedItem.likedDate}" pattern="dd MMM yy, HH:mm"/>)</span>
                                                                                                                                                </div>
                                                                                                                                                <div class="liked-item-snippet" style="background-color: var(--c-bg-secondary); padding: 8px; border-radius: 4px; margin-top:5px;">
                                                                                                                                                    <p><i>"<c:out value="${fn:substring(review.commentText, 0, 150)}"/>
                                                                                                                                                        <c:if test="${fn:length(review.commentText) > 150}">...</c:if>"</i></p>
                                                                                                                                                        <c:if test="${review.rating != null && review.rating > 0}">
                                                                                                                                                        <div class="rating small-rating comment-rating-display" style="justify-content: flex-start; margin-top:5px;">
                                                                                                                                                            <span class="stars"><c:forEach begin="1" end="${review.rating}"><i class="fas fa-star"></i></c:forEach><c:forEach begin="1" end="${5 - review.rating}"><i class="far fa-star"></i></c:forEach></span>
                                                                                                                                                            </div>
                                                                                                                                                    </c:if>
                                                                                                                                                </div>
                                                                                                                                                <%-- Yorumun kendi beğeni sayısı ve o anki kullanıcının bu yorumu beğenme durumu --%>
                                                                                                                                                <div class="review-actions" style="margin-top: 5px; font-size:0.8em;">
                                                                                                                                                    <span class="like-count book-like-count" style="margin-right:10px;"><i class="fas fa-thumbs-up"></i> <c:out value="${review.likesCount}"/></span>
                                                                                                                                                    <c:if test="${not empty sessionScope.loggedInUser}"><form action="${pageContext.request.contextPath}/likeReview" method="POST" style="display:inline;"><input type="hidden" name="reviewId" value="${review.reviewId}"><input type="hidden" name="bookId" value="${review.bookId}"><input type="hidden" name="sourcePage" value="profileLikes"><input type="hidden" name="profileUsername" value="${profileUser.username}"><c:choose><c:when test="${review.likedByCurrentUser}"><input type="hidden" name="action" value="unlike"></c:when><c:otherwise><input type="hidden" name="action" value="like"><button type="submit" class="like-btn book-like-btn" title="Beğen" style="padding: 3px 6px; font-size:0.8em;"><i class="far fa-heart"></i></button></c:otherwise></c:choose></form></c:if>
                                                                                                                                                                                            </div>
                                                                                                                                                                            </c:when>
                                                                                                                                                                        </c:choose>
                                                                                                                                                                        </div>
                                                                                                                                                                    </c:forEach>
                                                                                                                                                                    </div>
                                                                                                                                                                </c:when>
                                                                                                                                                                <c:otherwise>
                                                                                                                                                                    <p><c:out value="${profileUser.username}"/> henüz hiç kitap veya yorum beğenmemiş.</p>
                                                                                                                                                                </c:otherwise>
                                                                                                                                                            </c:choose>
                                                                                                                                                            </div>




                                                                                                                                                            <c:if test="${empty profileUser}">
                                                                                                                                                                <p style="text-align:center; padding: 50px; font-size: 1.2em;">Kullanıcı profili bulunamadı.</p>
                                                                                                                                                            </c:if>
                                                                                                                                                            </div> <%-- .profile-container kapanışı --%>
                                                                                                                                                            </div> <%-- .main kapanışı --%>
                                                                                                                                                            </div>
                                                                                                                                                            </div>


                                                                                                                                                            <div id="followModal" class="modal-overlay" style="display:none;">
                                                                                                                                                                <div class="modal-content">
                                                                                                                                                                    <span class="modal-close-btn" onclick="closeFollowModal()">&times;</span>

                                                                                                                                                                    <div class="modal-tabs">
                                                                                                                                                                        <button class="modal-tab-btn" data-tab-target="followersContent" onclick="setActiveTabAndContent('followersContent')">Takipçiler (<c:out value="${followerCount}"/>)</button>
                                                                                                                                                                        <button class="modal-tab-btn" data-tab-target="followingsContent" onclick="setActiveTabAndContent('followingsContent')">Takip Edilenler (<c:out value="${followingCount}"/>)</button>
                                                                                                                                                                    </div>

                                                                                                                                                                    <%-- Takipçiler İçeriği --%>
                                                                                                                                                                    <div id="followersContent" class="modal-tab-content active">
                                                                                                                                                                        <h4>Takipçiler</h4>
                                                                                                                                                                        <c:choose>
                                                                                                                                                                            <c:when test="${not empty followersList}">
                                                                                                                                                                                <ul class="user-list-modal">
                                                                                                                                                                                    <c:forEach var="follower" items="${followersList}">
                                                                                                                                                                                        <li>
                                                                                                                                                                                            <a href="${pageContext.request.contextPath}/profile?username=${follower.username}" class="user-list-item-modal">
                                                                                                                                                                                                <%-- Avatar gösterme mantığı --%>
                                                                                                                                                                                                <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                                                                                                                                                                                                <c:set var="userAvatarUrl" value="${follower.profileAvatarUrl}" />
                                                                                                                                                                                                <c:set var="defaultAvatar" value="${contextPath}/assets/user-avatar.jpg" />
                                                                                                                                                                                                <c:set var="finalUserAvatar" value="${defaultAvatar}" />
                                                                                                                                                                                                <c:if test="${not empty userAvatarUrl}">
                                                                                                                                                                                                    <c:choose>
                                                                                                                                                                                                        <c:when test="${fn:startsWith(userAvatarUrl, 'http://') or fn:startsWith(userAvatarUrl, 'https://')}">
                                                                                                                                                                                                        <c:set var="finalUserAvatar" value="${userAvatarUrl}" />
                                                                                                                                                                                                        </c:when>
                                                                                                                                                                                                        <c:otherwise>
                                                                                                                                                                                                        <c:set var="finalUserAvatar" value="${contextPath}${fn:startsWith(userAvatarUrl, '/') ? '' : '/'}${userAvatarUrl}" />
                                                                                                                                                                                                        </c:otherwise>
                                                                                                                                                                                                    </c:choose>
                                                                                                                                                                                                </c:if>
                                                                                                                                                                                                <img src="${finalUserAvatar}" alt="${follower.username}'s avatar" class="user-list-avatar-modal">
                                                                                                                                                                                                    <span class="user-list-name-modal">
                                                                                                                                                                                                        <c:out value="${not empty follower.fullName ? follower.fullName : follower.username}"/>
                                                                                                                                                                                                        (@<c:out value="${follower.username}"/>)
                                                                                                                                                                                                    </span>
                                                                                                                                                                                            </a>
                                                                                                                                                                                        </li>
                                                                                                                                                                                    </c:forEach>
                                                                                                                                                                                </ul>
                                                                                                                                                                            </c:when>
                                                                                                                                                                            <c:otherwise>
                                                                                                                                                                                <p>Henüz hiç takipçisi yok.</p>
                                                                                                                                                                            </c:otherwise>
                                                                                                                                                                        </c:choose>
                                                                                                                                                                    </div>

                                                                                                                                                                    <%-- Takip Edilenler İçeriği --%>
                                                                                                                                                                    <div id="followingsContent" class="modal-tab-content">
                                                                                                                                                                        <h4>Takip Edilenler</h4>
                                                                                                                                                                        <c:choose>
                                                                                                                                                                            <c:when test="${not empty followingsList}">
                                                                                                                                                                                <ul class="user-list-modal">
                                                                                                                                                                                    <c:forEach var="followingUser" items="${followingsList}">
                                                                                                                                                                                        <li>
                                                                                                                                                                                            <a href="${pageContext.request.contextPath}/profile?username=${followingUser.username}" class="user-list-item-modal">
                                                                                                                                                                                                <%-- Avatar gösterme mantığı --%>
                                                                                                                                                                                                <c:set var="userAvatarUrl" value="${followingUser.profileAvatarUrl}" />
                                                                                                                                                                                                <c:set var="defaultAvatar" value="${contextPath}/assets/user-avatar.jpg" />
                                                                                                                                                                                                <c:set var="finalUserAvatar" value="${defaultAvatar}" />
                                                                                                                                                                                                <c:if test="${not empty userAvatarUrl}">
                                                                                                                                                                                                    <c:choose>
                                                                                                                                                                                                        <c:when test="${fn:startsWith(userAvatarUrl, 'http://') or fn:startsWith(userAvatarUrl, 'https://')}">
                                                                                                                                                                                                        <c:set var="finalUserAvatar" value="${userAvatarUrl}" />
                                                                                                                                                                                                        </c:when>
                                                                                                                                                                                                        <c:otherwise>
                                                                                                                                                                                                        <c:set var="finalUserAvatar" value="${contextPath}${fn:startsWith(userAvatarUrl, '/') ? '' : '/'}${userAvatarUrl}" />
                                                                                                                                                                                                        </c:otherwise>
                                                                                                                                                                                                    </c:choose>
                                                                                                                                                                                                </c:if>
                                                                                                                                                                                                <img src="${finalUserAvatar}" alt="${followingUser.username}'s avatar" class="user-list-avatar-modal">
                                                                                                                                                                                                    <span class="user-list-name-modal">
                                                                                                                                                                                                        <c:out value="${not empty followingUser.fullName ? followingUser.fullName : followingUser.username}"/>
                                                                                                                                                                                                        (@<c:out value="${followingUser.username}"/>)
                                                                                                                                                                                                    </span>
                                                                                                                                                                                            </a>
                                                                                                                                                                                        </li>
                                                                                                                                                                                    </c:forEach>
                                                                                                                                                                                </ul>
                                                                                                                                                                            </c:when>
                                                                                                                                                                            <c:otherwise>
                                                                                                                                                                                <p>Henüz kimseyi takip etmiyor.</p>
                                                                                                                                                                            </c:otherwise>
                                                                                                                                                                        </c:choose>
                                                                                                                                                                    </div>




                                                                                                                                                                    <script src="${pageContext.request.contextPath}/js/script.js"></script>
                                                                                                                                                                    <script src="${pageContext.request.contextPath}/js/profile.js"></script>

                                                                                                                                                                    </body>
                                                                                                                                                                    </html>