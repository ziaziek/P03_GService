/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import domain.Game;
import domain.GameStatus;
import domain.HibernateUtil;
import helpers.TokenHelper;
import java.util.ArrayList;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Przemo
 */
@WebService(serviceName = "game")
public class game {
    
    private final int gameTokenLength = 6;
    private final int playerTokenLength=25;
    
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
        r.add(TokenHelper.generateToken(gameTokenLength));
        r.add(TokenHelper.generateToken(playerTokenLength));
        LoggerFactory.getLogger(getClass()).info("Save game request for game token: "+r.get(0));
        Game g = new Game();
        g.setGameKey(r.get(0));
        g.setWhiteKey(r.get(1));
        g.setGameStatus((GameStatus) HibernateUtil.runQuery("from GameStatus where name='Created'").get(0));
        LoggerFactory.getLogger(getClass()).info("Trying to save Game of token: "+g.getGameKey());
        HibernateUtil.save(g);
        return r;
    }
    
    /**
     * Returns status of the game
     * @param gameKey
     * @return 
     */
    @WebMethod(operationName = "status" )
    public Object getGameStatus(@WebParam(name = "gkey")final String gameKey){
        return HibernateUtil.runQuery("from Game where gameKey='"+gameKey+"'").get(0);
    }
    
    /**
     * Adds a player to the game and returns the newly generated player key.
     * @param gameKey
     * @return 
     */
    @WebMethod(operationName = "join")
    public String joinGame(@WebParam(name = "gkey")final String gameKey){
        Game g = (Game) HibernateUtil.runQuery("from GAme where gameKey='"+gameKey+"'").get(0);
        if(g!=null && g.getId()>0 && g.getBlackKey()==null && g.getWhiteKey()!=null){ //potential threat of using wrong player key, main in the middle attack
            final String s = TokenHelper.generateToken(playerTokenLength);
            g.setBlackKey(s);
            return s;
        }
        return "";
    }
    
    /**
     * Next move has been hit. Updates time of the calling player and returns time of its opponent as recorded in the database. Also updates
     * which player is on move.
     * @param playerKey
     * @param gameKey
     * @param playerTime
     * @return 
     */
    @WebMethod(operationName = "next")
    public long nextMove(@WebParam(name = "pkey")final String playerKey, @WebParam(name = "gkey")final String gameKey, @WebParam(name = "ptime")long playerTime){
        return 0;
    }
}
    
