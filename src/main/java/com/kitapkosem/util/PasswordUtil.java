/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kitapkosem.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author eyuph
 */
public class PasswordUtil {
     /**
     * Verilen düz metin şifreyi BCrypt ile hash'ler.
     *
     * @param plainPassword Hash'lenecek düz metin şifre.
     * @return BCrypt ile hash'lenmiş şifre.
     */
    public static String hashPassword(String plainPassword) {
        
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verilen düz metin şifrenin, veritabanından alınan hash'lenmiş şifreyle
     * eşleşip eşleşmediğini kontrol eder.
     *
     * @param plainPassword Kullanıcının girdiği düz metin şifre.
     * @param hashedPasswordFromDB Veritabanında saklanan hash'lenmiş şifre.
     * @return Şifreler eşleşiyorsa true, eşleşmiyorsa false döner.
     */
    public static boolean checkPassword(String plainPassword, String hashedPasswordFromDB) {
        return BCrypt.checkpw(plainPassword, hashedPasswordFromDB);
    }
}