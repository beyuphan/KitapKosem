document.addEventListener('DOMContentLoaded', function () {

    // ---- YORUM METNİNİ GENİŞLETME/DARALTMA ----
    const commentGrid = document.querySelector('.comments-grid');

    document.querySelectorAll('.toggle-comment-text').forEach(toggleLink => {
        toggleLink.addEventListener('click', function (event) {
            event.stopPropagation();

            const targetId = this.getAttribute('data-target-id');
            const commentTextElement = document.getElementById(targetId);
            const parentCard = this.closest('.comment-card');

            if (commentTextElement && parentCard) {
                if (parentCard.classList.contains('expanded-full-width')) {
                    commentTextElement.classList.remove('expanded');
                    parentCard.classList.remove('expanded-full-width');
                    this.textContent = 'Devamını Oku';
                } else {
                    if (commentGrid) { 
                        const currentlyExpandedCard = commentGrid.querySelector('.comment-card.expanded-full-width');
                        if (currentlyExpandedCard && currentlyExpandedCard !== parentCard) {
                            const expandedText = currentlyExpandedCard.querySelector('.comment-text.expanded');
                            const expandedLink = currentlyExpandedCard.querySelector('.toggle-comment-text');

                            if (expandedText)
                                expandedText.classList.remove('expanded');
                            if (expandedLink)
                                expandedLink.textContent = 'Devamını Oku';
                            currentlyExpandedCard.classList.remove('expanded-full-width');
                        }
                    }

                    commentTextElement.classList.add('expanded');
                    parentCard.classList.add('expanded-full-width');
                    this.textContent = 'Daha Az Göster';
                }
            }
        });
    });

    // ---- YILDIZ PUANLAMA SİSTEMİ ----
    const stars = document.querySelectorAll('.star-rating i');
    const selectedRatingInput = document.getElementById('selected-rating');

    if (!selectedRatingInput) {
        console.warn("bookDetail.js: Gizli rating input'u ('selected-rating') sayfada bulunamadı. Yıldız puanlama formu olmayabilir.");
    }
    if (stars.length === 0 && selectedRatingInput) {
        console.warn("bookDetail.js: Puanlama yıldızları ('.star-rating i') sayfada bulunamadı ama 'selected-rating' inputu var.");
    }

    if (selectedRatingInput && stars.length > 0) {
        stars.forEach(star => {
            star.addEventListener('click', function () {
                const rating = this.getAttribute('data-rating');
                selectedRatingInput.value = rating;
                stars.forEach((s, index) => {
                    if (index < rating) {
                        s.classList.remove('far');
                        s.classList.add('fas', 'active');
                    } else {
                        s.classList.remove('fas', 'active');
                        s.classList.add('far');
                    }
                });
            });

            star.addEventListener('mouseover', function () {
                const rating = this.getAttribute('data-rating');
                stars.forEach((s, index) => {
                    if (index < rating) {
                        s.classList.remove('far');
                        s.classList.add('fas');
                    } else {
                        s.classList.remove('fas');
                        s.classList.add('far');
                    }
                });
            });

            star.addEventListener('mouseout', function () {
                const currentRating = selectedRatingInput.value || 0;
                stars.forEach((s, index) => {
                    if (index < currentRating) {
                        s.classList.remove('far');
                        s.classList.add('fas', 'active');
                    } else {
                        s.classList.remove('fas', 'active');
                        s.classList.add('far');
                    }
                });
            });
        });
    }

  
}); 