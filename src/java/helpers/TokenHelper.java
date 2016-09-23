package helpers;


import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Przemo
 */
public class TokenHelper {
    
    final static String charset="ABCDEFGHIJKLMNOPQRSTUWVXYZ0123456789abcdefghijklmnopqrstuwvxyz";
    
    public static String generateToken(int n){
        Random r = new Random();
        StringBuilder sb = new StringBuilder(n);
        for(int i=0; i<n; i++){
            sb.append(charset.charAt(r.nextInt(charset.length())));
        }
        return sb.toString();
    }
}
