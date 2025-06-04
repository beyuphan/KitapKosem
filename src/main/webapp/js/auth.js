// Tab geçişleri
document.querySelectorAll('.auth-tab').forEach(tab => {
    tab.addEventListener('click', () => {
        // Aktif tabı değiştir
        document.querySelector('.auth-tab.active').classList.remove('active');
        tab.classList.add('active');

        // Aktif formu değiştir
        const tabName = tab.getAttribute('data-tab');
        document.querySelector('.auth-form.active').classList.remove('active');
        document.getElementById(`${tabName}-form`).classList.add('active');
    });
});

// Şifre göster/gizle
document.querySelectorAll('.show-password').forEach(button => {
    button.addEventListener('click', (e) => {
        const input = e.currentTarget.parentElement.querySelector('input[type="password"], input[type="text"]'); 
        const icon = e.currentTarget.querySelector('i');

        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    });
});

// Form submit işlemleri
document.getElementById('login-form').addEventListener('submit', (e) => {
    console.log('Giriş formu gönderiliyor...');
});

document.getElementById('register-form').addEventListener('submit', (e) => {
    console.log('Kayıt formu gönderiliyor...');
});