package pt.ua.smartWars.OnGameData;

import com.firebase.client.Firebase;

import java.util.HashMap;

/**
 * Created by Tiago on 04/04/2016.
 * <p/>
 * can be improved using hashmap, needs testing. only purpose is to store teams positions so far
 */
public class FirePlayers {


    private static int PLAYERS_PER_TEAM = 7;
    private static FirePlayers nInstance = null;
    private Firebase ref;
    private Position[] team_pos = null;

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getMatch_id() {
        return match_id;
    }

    public void setMatch_id(String match_id) {
        this.match_id = match_id;
    }

    private String team;
    private String match_id;
    private HashMap track;//to future tracking


    private FirePlayers() {
        this.ref = new Firebase("https://pei.firebaseio.com");
        this.team_pos = new Position[PLAYERS_PER_TEAM];
        track = new HashMap();
        this.team = "RED";
        this.match_id = "abcd";
    }

    public static FirePlayers getInstance() {
        if (nInstance == null) {
            nInstance = new FirePlayers();
        }
        return nInstance;
    }

    public Position getTeam_pos(String player_id) {
        for (int i = 0; i < this.team_pos.length; i++) {
            if (this.team_pos[i].getpId().equals(player_id))
                return this.team_pos[i];
        }
        return null;
    }

    public Position[] getTeamP() {

        return this.team_pos;
    }

    public void setTeam_pos(String player_id, double x, double y, int hRate) {
        for (int i = 0; i < this.team_pos.length; i++) {
            if (this.team_pos[i] == null) {
                this.team_pos[i] = new Position(player_id, x, y, hRate);
                System.out.println("setted");
                break;
            } else if (this.team_pos[i].getpId().equals(player_id)) {
                this.team_pos[i].setX(x);
                this.team_pos[i].setY(y);
                break;
            }
            //else player does not exist
        }
    }

    public void setTeam_hr(String player_id, int hRate) {
        for (int i = 0; i < this.team_pos.length; i++) {
            if (this.team_pos[i] == null) {
                this.team_pos[i] = new Position(player_id, 0, 0, hRate);
                //System.out.println("setted");
                break;
            } else if (this.team_pos[i].getpId().equals(player_id)) {
                this.team_pos[i].sethRate(hRate);
                break;
            }
            //else player does not exist
        }
    }

}


