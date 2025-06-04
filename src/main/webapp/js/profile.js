document.addEventListener('DOMContentLoaded', function () {

    // ---- PROFİL SAYFASI ANA SEKMELERİ İÇİN ----
    const profileNavLinks = document.querySelectorAll('.profile-nav a[data-tab-target]');
    const profileTabPanes = document.querySelectorAll('.profile-tabs-content .profile-tab-pane');

    function setActiveProfileTab(targetId) {
        profileNavLinks.forEach(innerLink => innerLink.parentElement.classList.remove('active'));
        profileTabPanes.forEach(pane => pane.classList.remove('active'));

        const targetLink = document.querySelector(`.profile-nav a[data-tab-target="${targetId}"]`);
        if (targetLink) {
            targetLink.parentElement.classList.add('active');
        }

        const targetPane = document.getElementById(targetId);
        if (targetPane) {
            targetPane.classList.add('active');
        }
    }

    if (profileNavLinks.length > 0 && profileTabPanes.length > 0) {
        profileNavLinks.forEach(link => {
            link.addEventListener('click', function (e) {
                e.preventDefault();
                const targetId = this.getAttribute('data-tab-target');
                setActiveProfileTab(targetId);
            });
        });


        let initiallyActiveProfileLink = document.querySelector('.profile-nav li.active a');
        if (!initiallyActiveProfileLink && profileNavLinks.length > 0) {
            profileNavLinks[0].parentElement.classList.add('active');
            initiallyActiveProfileLink = profileNavLinks[0];
        }

        if (initiallyActiveProfileLink) {
            const initialTargetId = initiallyActiveProfileLink.getAttribute('data-tab-target');
            setActiveProfileTab(initialTargetId);
        }
    }

    // ---- TAKİPÇİ/TAKİP EDİLEN MODALI İÇİN ----
    const followModalElement = document.getElementById('followModal');
    const modalCloseButton = document.querySelector('#followModal .modal-close-btn');
    const followerStatLink = document.querySelector('.profile-stats a.stat-link[data-modal-trigger="followersContentModal"]');
    const followingStatLink = document.querySelector('.profile-stats a.stat-link[data-modal-trigger="followingsContentModal"]');

    function openFollowModal(defaultTabContentId) {
        if (followModalElement) {
            followModalElement.style.display = 'flex';
            setActiveModalTabAndContent(defaultTabContentId);
        }
    }

    function closeFollowModal() {
        if (followModalElement) {
            followModalElement.style.display = 'none';
        }
    }

    function setActiveModalTabAndContent(activeContentId) {
        if (!followModalElement)
            return;

        const tabContents = followModalElement.querySelectorAll('.modal-tab-content');
        tabContents.forEach(content => {
            content.style.display = 'none';
            content.classList.remove('active');
        });

        const tabButtons = followModalElement.querySelectorAll('.modal-tab-btn');
        tabButtons.forEach(button => {
            button.classList.remove('active');
        });

        const targetContent = document.getElementById(activeContentId);
        if (targetContent) {
            targetContent.style.display = 'block';
            targetContent.classList.add('active');
        }

        const targetButton = followModalElement.querySelector(`.modal-tab-btn[data-tab-target="${activeContentId}"]`);
        if (targetButton) {
            targetButton.classList.add('active');
        }
    }

    if (followModalElement) {
        if (followerStatLink) {
            followerStatLink.addEventListener('click', function (e) {
                e.preventDefault();
                openFollowModal(this.getAttribute('data-modal-trigger'));
            });
        }
        if (followingStatLink) {
            followingStatLink.addEventListener('click', function (e) {
                e.preventDefault();
                openFollowModal(this.getAttribute('data-modal-trigger'));
            });
        }

        if (modalCloseButton) {
            modalCloseButton.addEventListener('click', closeFollowModal);
        }

        followModalElement.addEventListener('click', function (event) {
            if (event.target === followModalElement) {
                closeFollowModal();
            }
        });

        const modalTabButtons = followModalElement.querySelectorAll('.modal-tab-btn');
        modalTabButtons.forEach(button => {
            button.addEventListener('click', function () {
                const targetContentId = this.getAttribute('data-tab-target');
                setActiveModalTabAndContent(targetContentId);
            });
        });
    }



    const bookshelfTabs = document.querySelectorAll('.bookshelf-tabs .shelf-tab');
    if (bookshelfTabs.length > 0) {
        if (!document.querySelector('.bookshelf-tabs .shelf-tab.active')) {
            bookshelfTabs[0].classList.add('active');
        }

        bookshelfTabs.forEach(tab => {
            tab.addEventListener('click', function () {
                const currentActiveShelfTab = document.querySelector('.bookshelf-tabs .shelf-tab.active');
                if (currentActiveShelfTab) {
                    currentActiveShelfTab.classList.remove('active');
                }
                this.classList.add('active');

                console.log(this.textContent.trim() + " kitaplık içi sekmesi tıklandı. İçerik güncelleme mantığı eklenecek.");
            });
        });
    }

}); // DOMContentLoaded Sonu