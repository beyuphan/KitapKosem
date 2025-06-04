/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */



document.querySelectorAll('.like-book-form').forEach(form => {
    form.addEventListener('submit', function (event) {
        event.preventDefault(); 

        const bookId = this.dataset.bookId;
        const actionInput = this.querySelector('input[name="action"]');
        const action = actionInput ? actionInput.value : null;

        const likeButton = this.querySelector('.like-btn');
        const likeCountSpan = document.getElementById(`like-count-${bookId}`);

        if (!bookId || !action || !likeButton || !likeCountSpan) {
            console.error("Beğenme formu için gerekli elementler bulunamadı.", this);
            return;
        }

        likeButton.disabled = true;

        // AJAX isteği için FormData oluştur
        const formData = new FormData();
        formData.append('bookId', bookId);
        formData.append('action', action);
        formData.append('sourcePage', 'index'); 
        formData.append('ajax', 'true'); 

        fetch(`${APP_CONTEXT_PATH}/likeBook`, {
            method: 'POST',
            body: formData
        })
                .then(response => {
                    if (!response.ok) {
                        if (response.status === 401) {
                            console.warn("Kullanıcı giriş yapmamış veya yetkisi yok.");
                            
                            return response.json().then(errData => Promise.reject(errData));
                        }
                        throw new Error('Network response was not ok. Status: ' + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log("LikeBookServlet'ten gelen yanıt:", data);
                    if (data.success) {
                        // Beğeni sayısını güncelle
                        if (likeCountSpan.querySelector('i')) { 
                            likeCountSpan.innerHTML = `<i class="fas fa-heart" style="color: var(--c-text-secondary);"></i> ${data.likesCount}`;
                        } else {
                            likeCountSpan.textContent = data.likesCount; 
                        }

                        // Butonun görünümünü güncelle
                        const icon = likeButton.querySelector('i');
                        if (data.likedByCurrentUser) {
                            likeButton.classList.add('liked');
                            likeButton.title = 'Beğeniyi Geri Al';
                            if (icon) {
                                icon.classList.remove('far');
                                icon.classList.add('fas');
                            }
                            // action input'unun değerini 'unlike' yap
                            if (actionInput)
                                actionInput.value = 'unlike';
                        } else {
                            likeButton.classList.remove('liked');
                            likeButton.title = 'Beğen';
                            if (icon) {
                                icon.classList.remove('fas');
                                icon.classList.add('far');
                            }
                            // action input'unun değerini 'like' yap
                            if (actionInput)
                                actionInput.value = 'like';
                        }
                    } else {
                        console.error("Beğenme işlemi sunucuda başarısız oldu:", data.message);
                       
                        if (data.redirectTo) { 
                            console.warn("Sunucu yönlendirme önerdi:", data.redirectTo);
                        }
                    }
                })
                .catch(error => {
                    console.error('AJAX beğenme hatası:', error);
                  
                })
                .finally(() => {
                    likeButton.disabled = false; 
                });
    });
});