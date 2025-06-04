<%-- 
    Document   : header
    Created on : 29 May 2025, 00:42:43
    Author     : eyuph
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<header>
    <a href="${pageContext.request.contextPath}/index" class="logo-link" title="Ana Sayfaya Git">
        <img src="${pageContext.request.contextPath}/assets/logo.png" alt="KitapKöşem Logo" class="logo">
    </a>
    <form action="${pageContext.request.contextPath}/index" method="GET" class="search-form">
        <div class="search-container">
            <input type="text" class="search-input" name="query" placeholder="Kitap ara..." value="${searchQuery}"/>
            <button type="submit" class="search-button">
                <svg class="search-icon" viewBox="0 0 24 24">
                <path d="M15.5 14h-.79l-.28-.27a6.5 6.5 0 0 0 1.48-5.34c-.47-2.78-2.79-5-5.59-5.34a6.505 6.505 0 0 0-7.27 7.27c.34 2.8 2.56 5.12 5.34 5.59a6.5 6.5 0 0 0 5.34-1.48l.27.28v.79l4.25 4.25c.41.41 1.08.41 1.49 0 .41-.41.41-1.08 0-1.49L15.5 14zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
                </svg>
            </button>
        </div>
    </form>

    <button class="toggle" id="t_mode">Switch</button>
</header>
