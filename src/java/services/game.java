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
import java.util.Date;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

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
    public ArrayList<String> save(@WebParam(name = "time")final long ptime){
        ArrayList<String> r = new ArrayList();
        r.add(TokenHelper.generateToken(gameTokenLength));
        r.add(TokenHelper.generateToken(playerTokenLength));
        Game g = new Game();
        g.setGameKey(r.get(0));
        g.setWhiteKey(r.get(1));
        g.setGameStatus((GameStatus) HibernateUtil.runQuery("from GameStatus where name='Created'").get(0));
        g.setCreatedOn(new Date());
        g.setOnMove(false);
        g.setWhiteTime(ptime);
        g.setBlackTime(ptime);
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
     * Adds a player to the game and returns the newly generated player key and game time comma separated.
     * @param gameKey
     * @return 
     */
    @WebMethod(operationName = "join")
    public String joinGame(@WebParam(name = "gkey")final String gameKey){
        Game g = (Game) HibernateUtil.runQuery("from Game where gameKey='"+gameKey+"'").get(0);
        if(g!=null && g.getId()>0 && g.getBlackKey()==null && g.getWhiteKey()!=null){ //potential threat of using wrong player key, main in the middle attack
            final String s = TokenHelper.generateToken(playerTokenLength);
            g.setBlackKey(s);
            HibernateUtil.save(g);
            return s+","+String.valueOf(g.getBlackTime());
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
        Game g = (Game) HibernateUtil.runQuery("from Game t join fetch t.gameStatus where t.gameKey='"+gameKey+"'").get(0);
        if(g!=null && !g.getGameStatus().getName().equals("Finished")){
            long t=0;
            if(g.getGameStatus().getId()==1){ //For Created
                g.setGameStatus((GameStatus) HibernateUtil.runQuery("from GameStatus where name='Running'").get(0));
                g.setOnMove(Boolean.TRUE);
            }
            if(g.getWhiteKey().equals(playerKey) && !g.getOnMove()){
                g.setWhiteTime(playerTime);
                g.setOnMove(Boolean.TRUE);
                t=g.getBlackTime();
            } else if(g.getBlackKey().equals(playerKey) && g.getOnMove()){
                g.setBlackTime(playerTime);
                g.setOnMove(Boolean.FALSE);
                t=g.getWhiteTime();
            }
            HibernateUtil.save(g);
            return t;
        }
        return 0;
    }
}
    
