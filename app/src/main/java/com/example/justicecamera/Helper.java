package com.example.justicecamera;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.File;



/**
 * Created by admin on 16.08.2016.
 */
public final class Helper extends Object {
    private static final String PHOTO_DIRECTORY = "UsersPhoto";
    private static final String VIDEO_DIRECTORY = "video";
    private static final boolean OVERWRITE = true;

    private Helper(){}

    public static void deletePreviousPhoto(String pathToPhoto){
        Backendless.Files.remove(pathToPhoto);
    }

    public static void uploadVideo(File file) throws Exception {
        Backendless.Files.upload(file, VIDEO_DIRECTORY, OVERWRITE );
    }

    public static void updateUser (BackendlessUser user){
        Backendless.UserService.update(user);
    }

    public static void updateUserWithPhoto (BackendlessUser user, File file) throws Exception {
        Backendless.Files.upload(file, PHOTO_DIRECTORY, OVERWRITE );
        String photoUrl = "https://api.backendless.com/" + Defaults.APPLICATION_ID + "/" + Defaults.VERSION + "/files/" + PHOTO_DIRECTORY +
                "/" + "userPhoto_" + user.getProperty("objectId")+".jpg";
//        String photoUrl = "test";
        user.setProperty("photoUrl", photoUrl);
        Backendless.UserService.update(user);
    }

    public static void uploadViolation(File file) throws Exception {
        Backendless.Files.upload(file, PHOTO_DIRECTORY, OVERWRITE );
    }

    public static BackendlessCollection<Category_id>  getAllCategories(){
        return Backendless.Persistence.of(Category_id.class).find();
    }

    public static BackendlessCollection<Violation> getAllViolations(String dataQuery){
        BackendlessDataQuery dataQ = new BackendlessDataQuery();
        dataQ.setWhereClause(dataQuery);
        return Backendless.Data.of(Violation.class).find(dataQ);
    }
}