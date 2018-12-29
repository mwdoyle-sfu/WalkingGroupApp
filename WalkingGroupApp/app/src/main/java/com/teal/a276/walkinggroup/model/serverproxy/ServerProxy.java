package com.teal.a276.walkinggroup.model.serverproxy;

import android.support.annotation.Nullable;

import com.teal.a276.walkinggroup.model.dataobjects.Group;
import com.teal.a276.walkinggroup.model.dataobjects.Message;
import com.teal.a276.walkinggroup.model.dataobjects.permissions.Permission;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.dataobjects.UserLocation;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Server ServerProxy interface exposing the end points and logout to the views
 * For more information visit http://www.cs.sfu.ca/CourseCentral/276/bfraser/project/APIDocs_WalkingGroupApp.pdf
 *
 * When a method returns Void the result method in the ServerResult Interface should be empty
 */

public interface ServerProxy {
    /**
     *
     * @param user User to create new account with
     * @return A valid user object
     */
    @POST("/users/signup")
    Call<User> createNewUser(@Body User user);

    /**
     *
     * @param userWithEmailAndPassword User to login
     * @return Nothing
     */
    @POST("/login")
    Call<Void> login(@Body User userWithEmailAndPassword);

    /**
     *
     * @return A list of all users
     */
    @GET("/users")
    Call<List<User>> getUsers();

    /**
     *
     * @param userId Id of the user
     * @Param user User to update Server with
     * @return The user with the specified id
     */
    @POST("/users/{id}")
    Call<User> updateUser(@Path("id") Long userId, @Body User user, @Header("JSON-DEPTH") Long depth);

    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") Long userId, @Header("JSON-DEPTH") Long depth);

    /**
     *
     * @param email Email of the user
     * @return The user with the specified email
     */
    @GET("/users/byEmail")
    Call<User> getUserByEmail(@Query("email") String email, @Header("JSON-DEPTH") Long depth);

    /**
     * @param monitorId Id of the monitor
     * @return A list of users that the specified user monitors
     */
    @GET("/users/{id}/monitorsUsers")
    Call<List<User>> getMonitors(@Path("id") Long monitorId, @Header("JSON-DEPTH") Long depth);

    /**
     * @param userId Id for the user
     * @return A list of all monitors monitoring the user
     */
    @GET("/users/{id}/monitoredByUsers")
    Call<List<User>> getMonitoredBy(@Path("id") Long userId);

    /**
     * Creates a monitor relation between the two users
     * @param monitorId Id for the user who will be the monitor
     * @param userWithId User who the monitor will monitor
     * @return The list of users that the monitor now monitors after adding the new relationship
     */
    @POST("/users/{id}/monitorsUsers")
    Call<List<User>> monitorUser(@Path("id") Long monitorId,
                            @Body User userWithId);

    /**
     * Creates a monitor relation between the two users
     * @param monitorId Id for the user who will be the monitor
     * @param userWithId User who the monitor will monitor
     * @return The list of users that the monitor now monitors after adding the new relationship
     */
    @POST("/users/{id}/monitoredByUsers")
    Call<List<User>> monitoredByUser(@Path("id") Long monitorId,
                                 @Body User userWithId);

    /**
     * Deletes a monitor relationship between A and B.
     * @param idA Id for the monitor
     * @param idB Id for the monitoree
     * @return Nothing
     */
    @DELETE("/users/{idA}/monitorsUsers/{idB}")
    Call<Void> endMonitoring(@Path("idA") Long idA, @Path("idB") Long idB);

    /**
     * @return All Groups
     */
    @GET("/groups")
    Call<List<Group>> getGroups(@Header("JSON-DEPTH") Long depth);

    /**
     * Creates a new group. The group must have at least a leader and a description
     * @param group The group to create
     * @return The created group object.
     */
    @POST("/groups")
    Call<Group> createGroup(@Body Group group);

    /**
     * Gets a group specified by Id
     * @param groupId Id of the group
     * @return The group with the id of groupId
     */
    @GET("/groups/{id}")
    Call<Group> getGroup(@Path("id") Long groupId);

    /**
     *
     * @param groupId Id of the group to update
     * @param group Group information to update
     * @return The new group object
     */
    @POST("/groups/{id}")
    Call<Group> updateGroup(@Path("id") Long groupId, @Body Group group);

    /**
     *
     * @param groupId Id of group to delete
     * @return Nothing
     */
    @DELETE("/groups/{id}")
    Call<Void> deleteGroup(@Path("id") Long groupId);

    /**
     *
     * @param groupId Id of group
     * @return A list of all users in the group
     */
    @GET("/groups/{id}/memberUsers")
    Call<List<User>> getGroupMembers(@Path("id") Long groupId);

    /**
     *
     * @param groupId Id of group
     * @param user User to add to group
     * @return A list of all users in the group
     */
    @POST("/groups/{id}/memberUsers")
    Call<List<User>> addUserToGroup(@Path("id") Long groupId, @Body User user);

    /**
     *
     * @param groupId Id of the group to remove the user from
     * @param userId Id of user to remove from the group
     * @return Placeholder void object, can be ignored
     */
    @DELETE("/groups/{groupId}/memberUsers/{userId}")
    Call<Void> deleteUserFromGroup(@Path("groupId") Long groupId, @Path("userId") Long userId);


    @GET("/messages")
    Call<List<Message>> getMessages(@Nullable @QueryMap Map<String, Object> options);

    @POST("/messages/toparentsof/{id}")
    Call<Message> sendMessageToMonitors(@Path("id") Long userId, @Body Message message);

    @POST("/messages/togroup/{groupId}")
    Call<Message> sendMessageToGroup(@Path("groupId") Long groupId,  @Body Message message);

    @POST("/messages/{messageId}/readby/{userId}")
    Call<User> setMessageRead(@Path("messageId") Long messageId, @Path("userId") Long userId, @Body Boolean readStatus);

    @GET("/users/{id}/lastGpsLocation")
    Call<UserLocation> getLastGpsLocation(@Path("id") Long userId);

    @POST("/users/{id}/lastGpsLocation")
    Call<UserLocation> setLastLocation(@Path("id") Long userId, @Body UserLocation userLocation);

    @GET("/permissions")
    Call<List<Permission>> getPermissions(@Nullable @QueryMap Map<String, Object> options, @Header("JSON-DEPTH") Long depth);

    @POST("/permissions/{id}")
    Call<Permission> setPermissionStatus(@Path("id") Long id, @Body String status, @Header("JSON-DEPTH") Long depth);

}
