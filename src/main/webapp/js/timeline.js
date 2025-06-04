
document.addEventListener('DOMContentLoaded', function () {

    const activityFeed = document.querySelector('.activity-feed');
    let currentPage = 1;
    let isLoading = false;
    let noMoreActivities = false;

    if (!activityFeed) {
        return;
    }



    function createActivityHtml(activity) {
        function escapeHtml(unsafe) {
            if (unsafe === null || typeof unsafe === 'undefined')
                return '';
            return unsafe.toString()
                    .replace(/&/g, "&amp;")
                    .replace(/</g, "&lt;")
                    .replace(/>/g, "&gt;")
                    .replace(/"/g, "&quot;")
                    .replace(/'/g, "&#039;");
        }

        // Avatar URL'sini oluşturma
        let finalActorAvatar = `${APP_CONTEXT_PATH}/assets/user-avatar.png`;
        if (activity.actorProfileAvatarUrl) {
            if (activity.actorProfileAvatarUrl.startsWith('http://') || activity.actorProfileAvatarUrl.startsWith('https://')) {
                finalActorAvatar = activity.actorProfileAvatarUrl;
            } else {
                finalActorAvatar = `${APP_CONTEXT_PATH}${activity.actorProfileAvatarUrl.startsWith('/') ? '' : '/'}${activity.actorProfileAvatarUrl}`;
            }
        }

        // Aktivite mesajını oluşturma
        let activityMessageText = '';
        const actorLink = `<a href="${APP_CONTEXT_PATH}/profile?username=${escapeHtml(activity.actorUsername)}"><strong>${escapeHtml(activity.actorUsername)}</strong></a>`;

        switch (activity.activityType) {
            case 'NEW_BOOK':
                activityMessageText = `${actorLink} yeni bir kitap ekledi: <a href="${APP_CONTEXT_PATH}/book?id=${activity.targetItemId}"><strong>${escapeHtml(activity.targetItemTitle)}</strong></a>`;
                break;
            case 'NEW_REVIEW':
            case 'UPDATED_REVIEW':
                activityMessageText = `${actorLink} <a href="${APP_CONTEXT_PATH}/book?id=${activity.targetItemId}"><strong>${escapeHtml(activity.targetItemTitle)}</strong></a> kitabına bir yorum yaptı.`;
                break;
            case 'LIKED_BOOK':
                activityMessageText = `${actorLink} <a href="${APP_CONTEXT_PATH}/book?id=${activity.targetItemId}"><strong>${escapeHtml(activity.targetItemTitle)}</strong></a> kitabını beğendi.`;
                break;
            case 'LIKED_REVIEW':
                const commentedBy = activity.secondaryTargetItemTitle ? escapeHtml(activity.secondaryTargetItemTitle) : 'bir kullanıcının';
                activityMessageText = `${actorLink}, ${commentedBy} adlı kullanıcının <a href="${APP_CONTEXT_PATH}/book?id=${activity.targetItemId}#review-${activity.targetItemId}"><strong>${escapeHtml(activity.targetItemTitle)}</strong></a> kitabındaki yorumunu beğendi.`;
                break;
            case 'STARTED_FOLLOWING':
                activityMessageText = `${actorLink} <a href="${APP_CONTEXT_PATH}/profile?username=${escapeHtml(activity.targetItemTitle)}"><strong>@${escapeHtml(activity.targetItemTitle)}</strong></a> adlı kullanıcıyı takip etmeye başladı.`;
                break;
            default:
                activityMessageText = `${actorLink} bir aktivitede bulundu.`;
        }

        // Tarih formatlama
        let formattedDate = '';
        if (activity.createdAt) {
            try {
                const date = new Date(activity.createdAt);
                formattedDate = date.toLocaleDateString('tr-TR', {
                    day: 'numeric', month: 'long', year: 'numeric',
                    hour: '2-digit', minute: '2-digit'
                });
            } catch (e) {
                console.warn("JS Tarih formatlama hatası:", activity.createdAt, e);
                formattedDate = activity.createdAt;
            }
        }

        let commentSnippetHtml = '';
        if ((activity.activityType === 'NEW_REVIEW' || activity.activityType === 'UPDATED_REVIEW' || activity.activityType === 'LIKED_REVIEW') && activity.commentSnippet) {
            commentSnippetHtml = `
            <div class="activity-content-snippet">
                <p><i>"${escapeHtml(activity.commentSnippet)}"</i></p>
            </div>`;
        }
        if (activity.activityType === 'NEW_BOOK' && activity.commentSnippet) {
            commentSnippetHtml = `
            <div class="activity-content-snippet">
                <p><i>"${escapeHtml(activity.commentSnippet)}"</i></p>
            </div>`;
        }


        return `
        <li class="activity-item">
            <div class="activity-header">
                <a href="${APP_CONTEXT_PATH}/profile?username=${escapeHtml(activity.actorUsername)}">
                    <img src="${finalActorAvatar}" alt="${escapeHtml(activity.actorUsername)}'s avatar" class="activity-actor-avatar">
                </a>
                <p class="activity-message">${activityMessageText}</p>
            </div>
            ${commentSnippetHtml}
            <p class="activity-meta">${formattedDate}</p>
        </li>
    `;
    }



    function loadMoreActivities() {
        if (isLoading || noMoreActivities) {
            return;
        }
        isLoading = true;
        currentPage++;

        console.log(`AJAX: Sayfa ${currentPage} yükleniyor...`);

        fetch(`${APP_CONTEXT_PATH}/timeline?page=${currentPage}&requestType=ajax`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok ' + response.statusText);
                    }
                    return response.json();
                })
                .then(newActivities => {
                    if (newActivities && newActivities.length > 0) {
                        newActivities.forEach(activity => {
                            const activityHtml = createActivityHtml(activity);
                            activityFeed.insertAdjacentHTML('beforeend', activityHtml);
                        });
                    } else {
                        noMoreActivities = true;
                        console.log("AJAX: Daha fazla aktivite bulunamadı.");

                        if (activityFeed.querySelector('.all-activities-loaded-message') == null) {
                            activityFeed.insertAdjacentHTML('beforeend', '<li class="no-activities" style="padding:10px 0; box-shadow:none; border:none;">Tüm aktiviteler yüklendi.</li>');
                        }
                    }
                    isLoading = false;
                })
                .catch(error => {
                    console.error('AJAX Hata: Aktiviteler yüklenemedi:', error);
                    isLoading = false;
                });
    }

    // Scroll event listener
    window.addEventListener('scroll', () => {

        if ((window.innerHeight + window.pageYOffset) >= document.body.offsetHeight - 100) { // Sayfanın sonuna 100px kala
            loadMoreActivities();
        }
    });


}); // DOMContentLoaded Sonu