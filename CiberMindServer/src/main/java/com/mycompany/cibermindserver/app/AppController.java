/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.mycompany.cibermindserver.app;
/**
 *
 * @author Yoav
 */
import com.mycompany.cibermindserver.dao.WordsContainer;
import java.util.List;
import java.util.Random;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.simple.JSONObject;


@RestController
public class AppController {
    
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public String insert(@RequestParam(value="word") String word) {
        Boolean success = false;
        try{
            WordsContainer.getInstance().insert(word);
            success = true;
        }catch(Exception ex){}
        
        return createInsertJsonResponse(word,success).toJSONString();
    }
    
    @RequestMapping(value = "/match", method = RequestMethod.GET)
    public String match(@RequestParam(value="word") String word) {
        List<String> matches = WordsContainer.getInstance().getTopMatchWordsByAscii(word);
        return createMatchJsonResponse(word,matches).toJSONString();
    }
    
    private JSONObject createInsertJsonResponse(String word, Boolean success){
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("success", success.toString());
        if(!success){
            Random random = new Random();
            jsonObj.put("err", random.nextInt());
        }
        return jsonObj;
    }
    
    private JSONObject createMatchJsonResponse(String word,List<String> matches){
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("top", matches.toString());
        jsonObj.put("query", word);
        return jsonObj;
    }
}
