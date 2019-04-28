/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.mycompany.cibermindserver.dao;

import com.mycompany.cibermindserver.utils.AsciiScore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
/**
 *
 * @author Yoav
 */
public class WordsContainer {
    private static WordsContainer instance = null;
    private static final Object lock  = new Object();
    private final Map<Integer,Set<String>> wordsMap;
    private AtomicInteger wordsMapSize;
    
    public static WordsContainer getInstance(){
        //Double-Checked Locking
        if(instance == null){
            synchronized(lock){
                if(instance == null){
                    instance = new WordsContainer();
                }
            }
        }
        return instance;
    }
    
    private WordsContainer(){
        wordsMap = new ConcurrentSkipListMap();
        wordsMapSize = new AtomicInteger(0);
    }
    
    public void insert(String word){
        Integer wordAsciiValue = AsciiScore.calculateScore(word);
        if(!wordsMap.containsKey(wordAsciiValue)){
            wordsMap.put(wordAsciiValue, new HashSet<>());
        }
        wordsMap.get(wordAsciiValue).add(word);
        wordsMapSize.incrementAndGet();
    }
    
    public List<String> getTopMatchWordsByAscii(String word){
        Integer asciiSumValue = AsciiScore.calculateScore(word);
        List<String> results = null;
        results = find3topClosestWords(asciiSumValue);
        return results;
    }
    
    private List<String> find3topClosestWords(Integer inputKey){
        
        //Base case: map is empty:
        if(wordsMap.isEmpty())
            return null;
        
        int maxResultsSize = (wordsMapSize.get() < 3)?  wordsMapSize.get(): 3;
        List<String> results = new ArrayList<>(maxResultsSize);
        
        //Base case: map size is less than 4:
        if(wordsMapSize.get() < 4){
            return wordsMap.values().stream()
                    .flatMap((list) -> list.stream())
                    .collect(Collectors.toList());
        }
        
        //Base case: map contains key
        if(wordsMap.containsKey(inputKey)){
            results.addAll(getWordsFromList(wordsMap.get(inputKey),maxResultsSize));
        }
        
        //Use binary search to find the closest keys:
        Integer floorKey = getFloorKey(inputKey);
        Integer ceilingKey = getCeilingKey(inputKey);
        
        while(results.size() < maxResultsSize){
            int maxWordsToAdd = maxResultsSize - results.size();
            
            //If floor Key is null, select words from the ceiling key:
            if(floorKey == null){
                results.addAll(getWordsFromList(wordsMap.get(ceilingKey), maxWordsToAdd));
                ceilingKey = getCeilingKey(ceilingKey);
                continue;
            }
            
            //If ceiling Key is null, select words from the floor key:
            if(ceilingKey == null){
                results.addAll(getWordsFromList(wordsMap.get(floorKey), maxWordsToAdd));
                floorKey = getFloorKey(floorKey);
                continue;
            }
            
            //Otherwize, check which key is the closest to the input key and select the words from it:
            if(Math.abs(floorKey - inputKey) < Math.abs(ceilingKey - inputKey)){
                results.addAll(getWordsFromList(wordsMap.get(floorKey), maxWordsToAdd));
                floorKey = getFloorKey(floorKey);
            }else{
                results.addAll(getWordsFromList(wordsMap.get(ceilingKey), maxWordsToAdd));
                ceilingKey = getCeilingKey(ceilingKey);
            }
        }
        return results;
    }
    
    private List<String> getWordsFromList(Set<String> list, int maxNumOfWords){
        return list.stream().limit(maxNumOfWords).collect(toList());
    }
    
    private Integer getFloorKey(Integer inputKey){
        return (Integer)((ConcurrentSkipListMap)wordsMap).floorKey(inputKey - 1);
    }
    
    private Integer getCeilingKey(Integer inputKey){
        return (Integer)((ConcurrentSkipListMap)wordsMap).ceilingKey(inputKey + 1);
    }
}