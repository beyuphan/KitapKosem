# KitapKöşem - Sosyal Kitap Platformu

"KitapKöşem", kullanıcıların kitapları keşfedebileceği, yorum yapabileceği, puanlayabileceği, beğenebileceği ve diğer kullanıcılarla etkileşimde bulunabileceği bir sosyal kitap platformudur. Bu proje, Java Servlet/JSP teknolojileri kullanılarak MVC mimarisine uygun olarak geliştirilmiştir.

## Kullanılan Teknolojiler

* **Backend:** Java 17, Jakarta EE (Servlets, JSP), Apache Tomcat 11
* **Veritabanı:** MySQL 8.x
* **Frontend:** HTML, CSS, JavaScript, JSTL 3.0
* **Build Aracı:** Apache Maven
* **Kütüphaneler:**
    * `mysql-connector-java` (MySQL JDBC Sürücüsü)
    * `jbcrypt` (Şifre Hashleme)
    * `jakarta.servlet.jsp.jstl-api` ve `org.glassfish.web:jakarta.servlet.jsp.jstl` (JSTL)
    * `com.google.code.gson` (JSON İşleme)
    * `org.apache.httpcomponents:httpclient`, `httpcore`, `httpmime` (Apache HttpClient 4.x - Resim Yükleme için)
* **Harici Servisler:**
    * imgBB (Resim barındırma için API)

## Kurulum ve Çalıştırma Talimatları

Bu projeyi lokal makinenizde çalıştırmak için aşağıdaki adımları izleyin:

### 1. Ön Gereksinimler

* **Java JDK 17** (veya üstü) kurulu olmalı.
* **Apache Maven** kurulu olmalı.
* **Apache Tomcat 11** (veya Jakarta EE 10/11 destekleyen bir servlet container) kurulu olmalı.
* **MySQL Server 8.x** (veya uyumlu bir sürüm) kurulu ve çalışır durumda olmalı.
* **imgBB API Anahtarı:** [https://api.imgbb.com/](https://api.imgbb.com/) adresinden ücretsiz bir API anahtarı alın.

### 2. Veritabanı Kurulumu

1.  MySQL sunucunuzda `kitap_kosem_db` adında yeni bir veritabanı (schema) oluşturun:
    ```sql
    CREATE DATABASE kitap_kosem_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    USE kitap_kosem_db;
    ```
2.  Proje ile birlikte verilen `veritabani_yedegi.sql` dosyayı bu veritabanına import edin. Bu dosya, gerekli tüm tabloları ve varsa örnek verileri içerecektir.
    * MySQL Workbench kullanıyorsanız: `Server > Data Import` seçeneğiyle `.sql` dosyasını seçip import edebilirsiniz.
    * Komut satırından: `mysql -u kullanici_adiniz -p kitap_kosem_db < veritabani_yedegi.sql`

### 3. Proje Kodlarının Yapılandırılması

1.  **Veritabanı Bağlantı Bilgileri:**
    Proje içindeki DAO sınıflarında (örn: `src/main/java/com/kitapkosem/dao/UserDAO.java`, `BookDAO.java` vb.) bulunan aşağıdaki satırları kendi MySQL bağlantı bilgilerinizle güncelleyin:
    ```java
    private String jdbcURL = "jdbc:mysql://localhost:3306/kitap_kosem_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private final String jdbcUsername = "root"; // Kendi MySQL kullanıcı adınız
    private final String jdbcPassword = "SENIN_MYSQL_SIFREN"; // Kendi MySQL şifreniz
    ```
    **Not:** Güvenlik nedeniyle, bu bilgileri doğrudan koda gömmek yerine ortam değişkenlerinden veya bir yapılandırma dosyasından okumak daha iyi bir pratiktir. Bu proje için basitlik amacıyla doğrudan kod içinde ayarlanmıştır.

2.  **imgBB API Anahtarı:**
    `src/main/java/com/kitapkosem/service/ImageUploadService.java` dosyasını açın ve aşağıdaki satırdaki placeholder'ı kendi imgBB API anahtarınızla değiştirin:
    ```java
    private static final String IMGBB_API_KEY = "SENIN_GERCEK_IMGBB_API_ANAHTARIN";
    ```

### 4. Projeyi Derleme ve Dağıtma (Deploy)

1.  Projenin kök dizininde bir terminal açın.
2.  Projeyi Maven ile derleyip paketleyin:
    ```bash
    mvn clean install
    ```
    Bu komut, `target` klasörünün altında `KitapKosem-1.0-SNAPSHOT.war` (veya benzeri bir isimde) bir WAR dosyası oluşturacaktır.
3.  Oluşan bu `.war` dosyasını Apache Tomcat sunucunuzun `webapps` klasörüne kopyalayın.
4.  Tomcat sunucusunu başlatın (veya zaten çalışıyorsa yeniden başlatın). Tomcat, WAR dosyasını otomatik olarak deploy edecektir.

### 5. Uygulamayı Çalıştırma

Tomcat başarıyla başladıktan ve uygulama deploy olduktan sonra, web tarayıcınızdan aşağıdaki adrese giderek uygulamaya erişebilirsiniz:

`http://localhost:8080/KitapKosem-1.0-SNAPSHOT/`

(Eğer Tomcat farklı bir portta çalışıyorsa veya uygulamanızın context path'i farklıysa, URL'i ona göre düzenleyin. Genellikle `http://localhost:8080/ARTIFACT_ID/` şeklinde olur.)

## Proje Özellikleri (Özet)

* Kullanıcı Kayıt ve Giriş Sistemi (Şifre hashleme ile)
* Profil Görüntüleme ve Düzenleme (Avatar ve Kapak Fotoğrafı Yükleme ile)
* Kitap Ekleme, Listeleme, Detay Görüntüleme ve Arama
* Kitaplara Yorum Yapma ve Puan Verme
* Kitapları ve Yorumları Beğenme
* Kullanıcıları Takip Etme Sistemi
* Kişisel Timeline (Takip Edilenlerin Aktiviteleri)
* Kullanıcının Kendi Eklediği Kitapları ve Yorumları Silebilmesi

## Katkıda Bulunan
* Eyüphan Binici



