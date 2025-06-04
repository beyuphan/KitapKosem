const toggleButton = document.getElementById('t_mode');

function updateTheme() {
    const isDarkMode = document.body.classList.contains('dark-theme');
    toggleButton.textContent = isDarkMode ? 'Light Mode' : 'Dark Mode';
}

function toggleTheme() {
    document.body.classList.toggle('dark-theme');
    localStorage.setItem('theme', document.body.classList.contains('dark-theme') ? 'dark' : 'light');
    updateTheme();
}

function checkTheme() {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-theme');
    } else {
        document.body.classList.remove('dark-theme');
    }
    updateTheme();
}

toggleButton.addEventListener('click', toggleTheme);

window.addEventListener('load', checkTheme);



const burger = document.getElementById('burgerMenu');
const toggle = document.getElementById('burgerToggle');
function setInitialMenuState() {
    if (window.innerWidth < 1000) { // Eşik değer 1000px
        if (burger && !burger.classList.contains('hidden')) {
            burger.style.transition = 'none';
            burger.classList.add('hidden');

            setTimeout(() => {
                if (burger)
                    burger.style.transition = '';
            }, 50);
        }
    } else {
      
        if (burger && burger.classList.contains('hidden')) {
        }
    }
}

// 1. Sayfa ilk yüklendiğinde durumu kontrol et
setInitialMenuState();

// 2. Ekran boyutu değiştiğinde (resize) durumu tekrar kontrol et
window.addEventListener('resize', setInitialMenuState);

toggle.addEventListener('click', () => {
    burger.classList.toggle('hidden');
});


function createStars(rating) {
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);

    let starsHTML = '';

    // Tam yıldızlar
    for (let i = 0; i < fullStars; i++) {
        starsHTML += '<i class="fas fa-star"></i>';
    }

    // Yarım yıldız
    if (hasHalfStar) {
        starsHTML += '<i class="fas fa-star-half-alt"></i>';
    }

    // Boş yıldızlar
    for (let i = 0; i < emptyStars; i++) {
        starsHTML += '<i class="far fa-star empty"></i>';
    }

    return starsHTML;
}

