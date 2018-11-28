package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

  /**
   * @param idUser
   * @return Responses
   */
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON - FIXED!
    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);

    // Crypteringslinjen herunder:
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down?
    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
  }

  /**
   * @return Responses
   */
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    // Her er der lagt cache på, forceupdate sættes til false.
    UserCache userCache = new UserCache();
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON - FIXED!
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);

    // Crypteringslinjen herunder:
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    //Læser json fra bodyen og omdanner den til user klassen.
    User user = new Gson().fromJson(body, User.class);

    //Får brugeren tilbage med det tilføjede ID og returnerer den til user.
    String token = UserController.loginUser(user);

    //Returnere dataen til useren.
    // Hvis token er forskellig fra "ikke noget" så skal den kørre status 200.
    //Hvis token ikke er blevet oprettet/ikke indeholder noget, køres status 400 og en fejlmedelelse udskrives.
    if (token != "") {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    } else {
      return Response.status(400).entity("Something went wrong. Could not create user").build();
    }
  }

  // TODO: Make the system able to delete users
  @DELETE
  @Path("/{userId}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteUser(@PathParam("userId") int userId, @PathParam("token") String token) {

    Boolean isDeleted = UserController.deleteUser(token);

    if (isDeleted) {
      //Hvis isDeleted er true, sendes en status 200 med en meddelelse om at brugeren blev slettet.
      return Response.status(200).entity("User has been deleted").build();
    } else {
      //Hvis isDeleted er false, sendes status 400 med en meddelelse om at brugeren ikke kunne slettes.
      return Response.status(400).entity("Could not delete user").build();
    }
  }


  // TODO: Make the system able to update users
  @PUT
  @Path("/{idUser}/{token}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(@PathParam("token") String token, String body) {

    User user = new Gson().fromJson(body, User.class);

    Boolean isUpdated = UserController.updateUser(user, token);

    if (isUpdated) {
      //Hvis isUpdated er true, sendes en status 200 med en meddelelse om at brugeren blev opdateret.
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity("The user has been updated").build();
    } else {
      //Hvis isUpdated er false, sendes status 400 med en meddelelse om at brugeren ikke kunne opdateres.
      return Response.status(400).entity("Could not update user").build();
    }

  }
}
