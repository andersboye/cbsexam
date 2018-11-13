package cache;

import controllers.UserController;
import model.User;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it. - FIXED TROR JEG, TEST DET!
public class UserCache {

    //The list of users
    private ArrayList<User> users;

    //Cache "live time"
    private long ttl;

    //Sets when the cache is created
    private long created;

    public UserCache() {
        this.ttl = Config.getUserTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate){

        // If we whis to clear cache, we can set force update.
        // Otherwise we look at the age of the cache and figure out if we should update.
        // If the list is empty we also check for new users

        if (forceUpdate
                || ((this.created + this.ttl) <= System.currentTimeMillis() / 1000L)
                || this.users.isEmpty()){

            // Get users from controller, since we wish to update.
            ArrayList<User> users = UserController.getUsers();

            // Set users for the instance and set created timestamp
            this.users = users;
            this.created = System.currentTimeMillis() / 1000L;
        }

        // Return the info
        return this.users;
    }





}
