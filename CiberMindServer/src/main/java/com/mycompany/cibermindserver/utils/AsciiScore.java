/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.cibermindserver.utils;
/**
 *
 * @author Yoav
 */
public class AsciiScore {
    public static int calculateScore(String word){
        int score = 0;
        for(int i=0; i < word.length();i++){
            score+= (int)word.charAt(i);
        }     
        return score;
    }    
}
