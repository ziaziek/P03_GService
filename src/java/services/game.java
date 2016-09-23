/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import domain.GameStatus;
import domain.HibernateUtil;
import helpers.TokenHelper;
import java.util.ArrayList;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 *
 * @author Przemo
 */
@WebService(serviceName = "game")
public class game {

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    @WebMethod(operationName="statuses")
    public ArrayList<String> statuses(){
        ArrayList<String> r = new ArrayList();
        for(Object gs : HibernateUtil.runQuery("from GameStatus")){
            if(gs instanceof GameStatus)
                r.add(((GameStatus)gs).getName());
        }
        return r;
    }
    
    @WebMethod(operationName="save")
    public ArrayList<String> save(){
        ArrayList<String> r = new ArrayList();
        r.add(TokenHelper.generateToken(6));
        r.add(TokenHelper.generateToken(25));
        return r;
    }
}
