<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Giriş yapılmamışsa auth.jsp'ye yönlendir --%>
<c:if test="${empty sessionScope.loggedInUser}">
    <c:redirect url="auth.jsp?redirect=timeline" />
</c:if>

<!DOCTYPE html>
<html lang="tr">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Zaman Akışı | KitapKöşem</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

            <script>
        const APP_CONTEXT_PATH = "${pageContext.request.contextPath}";
            </script>
            </head>
            <body>
                <jsp:include page="sidebar.jsp" />

            <div class="main">
                <jsp:include page="header.jsp" />

                <div class="timeline-container">
                    <h2 class="page-title"><i class="fas fa-stream"></i> Zaman Akışınız</h2>


                    <c:choose>
                        <c:when test="${not empty requestScope.timelineActivities}">
                            <ul class="activity-feed">
                                <c:forEach var="activity" items="${requestScope.timelineActivities}">
                                    <li class="activity-item">
                                        <div class="activity-header">
                                            <c:set var="contextPath" value="${pageContext.request.contextPath}" />
                                            <c:set var="actorAvatarUrl" value="${activity.actorProfileAvatarUrl}" />
                                            <c:set var="defaultAvatar" value="${contextPath}/assets/user-avatar.png" /> <%-- Varsayılan avatarını kontrol et, .jpg mi .png mi? --%>
                                            <c:set var="finalActorAvatar" value="${defaultAvatar}" />

                                            <c:if test="${not empty actorAvatarUrl}">
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(actorAvatarUrl, 'http://') or fn:startsWith(actorAvatarUrl, 'https://')}">
                                                        <c:set var="finalActorAvatar" value="${actorAvatarUrl}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="finalActorAvatar" value="${contextPath}${fn:startsWith(actorAvatarUrl, '/') ? '' : '/'}${actorAvatarUrl}" />
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:if>
                                            <a href="${contextPath}/profile?username=${activity.actorUsername}">
                                                <img src="${finalActorAvatar}" alt="<c:out value='${activity.actorUsername}'/> kullanıcısının avatarı" class="activity-actor-avatar">
                                            </a>
                                            <%-- ... (avatar ve ana aktivite mesajı aynı) ... --%>
                                            <p class="activity-message">
                                                <a href="${pageContext.request.contextPath}/profile?username=${activity.actorUsername}"><strong><c:out value="${activity.actorUsername}"/></strong></a>
                                                        <c:choose>
                                                            <c:when test="${activity.activityType == 'NEW_BOOK'}">
                                                        yeni bir kitap ekledi: <a href="${pageContext.request.contextPath}/book?id=${activity.targetItemId}"><c:out value="${activity.targetItemTitle}"/></a>
                                                    </c:when>
                                                    <c:when test="${activity.activityType == 'NEW_REVIEW' or activity.activityType == 'UPDATED_REVIEW'}">
                                                        <a href="${pageContext.request.contextPath}/book?id=${activity.targetItemId}"><c:out value="${activity.targetItemTitle}"/></a> kitabına bir yorum yaptı.
                                                    </c:when>
                                                    <c:when test="${activity.activityType == 'LIKED_BOOK'}">
                                                        <a href="${pageContext.request.contextPath}/book?id=${activity.targetItemId}"><c:out value="${activity.targetItemTitle}"/></a> kitabını beğendi.
                                                    </c:when>
                                                    <c:when test="${activity.activityType == 'LIKED_REVIEW'}">
                                                        <%-- TimelineServlet'te secondaryTargetItemTitle'a yorumu yapanın adını atamıştık --%>
                                                        <c:if test="${not empty activity.secondaryTargetItemTitle}">
                                                            <a href="${pageContext.request.contextPath}/profile?username=${activity.secondaryTargetItemTitle}"><c:out value="${activity.secondaryTargetItemTitle}"/></a> adlı kullanıcının
                                                        </c:if>
                                                        <a href="${pageContext.request.contextPath}/book?id=${activity.targetItemId}"><c:out value="${activity.targetItemTitle}"/></a> kitabındaki yorumunu beğendi.
                                                    </c:when>
                                                    <c:when test="${activity.activityType == 'STARTED_FOLLOWING'}">
                                                        <a href="${pageContext.request.contextPath}/profile?username=${activity.targetItemTitle}">@<c:out value="${activity.targetItemTitle}"/></a> adlı kullanıcıyı takip etmeye başladı.
                                                    </c:when>
                                                    <c:otherwise>
                                                        bir aktivitede bulundu.
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </div>

                                        <%-- Aktivite tipine göre ek içerik (yorum metni snippet'i gibi) --%>
                                        <%-- ARTIK activity.commentSnippet KULLANILIYOR --%>
                                        <c:if test="${(activity.activityType == 'NEW_REVIEW' or activity.activityType == 'UPDATED_REVIEW' or activity.activityType == 'LIKED_REVIEW') and not empty activity.commentSnippet}">
                                            <div class="activity-content-snippet">
                                                <p>
                                                    <c:if test="${activity.activityType == 'LIKED_REVIEW'}">Yorum: </c:if>
                                                <i>"<c:out value="${activity.commentSnippet}"/>"</i>
                                                </p>
                                            </div>
                                        </c:if>
                                        <%-- Kitap açıklaması snippet'i için de benzer bir mantık (Activity modeline ekleyip Servlet'te doldurarak) --%>
                                        <c:if test="${activity.activityType == 'NEW_BOOK' and not empty activity.commentSnippet}"> <%-- commentSnippet'i kitap açıklaması için de kullanabiliriz --%>
                                            <div class="activity-content-snippet">
                                                <p><i>"<c:out value="${activity.commentSnippet}"/>"</i></p>
                                            </div>
                                        </c:if>

                                        <p class="activity-meta">
                                            <fmt:formatDate value="${activity.createdAt}" type="both" dateStyle="medium" timeStyle="short"/>
                                        </p>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <p class="no-activities">Takip ettiğiniz kişilerden henüz yeni bir aktivite yok veya kimseyi takip etmiyorsunuz.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <script src="${pageContext.request.contextPath}/js/script.js"></script>
            <script src="${pageContext.request.contextPath}/js/timeline.js"></script>

            </body>
            </html>