package userData;

import com.firebase.client.Firebase;

/**
 * Created by Tiago on 17/03/2016.
 */
public class userInfo {

    public enum logginTypes{
        FACEBOOK, MAILPW, NONE
    }

    private static userInfo nInstance = null;
    private boolean loggedIn;//loggedIn?
    private logginTypes loggedInWith;//logged in with what tech?
    private Firebase ref;

    private userInfo(){
        this.loggedIn=false;
        this.loggedInWith= logginTypes.NONE;
        this.ref= new Firebase("https://paintmonitor.firebaseio.com");
    }


    public void setLoggedInWith(String loggedInWith) {
        if(loggedInWith.equals("FACEBOOK")){
        this.loggedInWith = logginTypes.FACEBOOK;}
        else if(loggedInWith.equals("MAILPW")){
            this.loggedInWith = logginTypes.MAILPW;}
        else
        {
            this.loggedInWith = logginTypes.NONE;
        }
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getLoggedInWith() {
        return loggedInWith.toString();
    }

    public Firebase getRef() {
        return ref;
    }

    public static userInfo getInstance()
    {
        if (nInstance==null){
            nInstance= new userInfo();
        }
        return nInstance;
    }
}
