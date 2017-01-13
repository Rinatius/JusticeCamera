package com.example.justicecamera;

import android.content.Context;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessException;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.File;


/**
 * Created by admin on 16.08.2016.
 */
public final class Helper extends Object {
    private static final String USERS_PHOTO_DIRECTORY = "UsersPhoto";
    private static final String VIOL_PHOTO_DIRECTORY = "ViolationsPhoto";
    private static final String VIDEO_DIRECTORY = "video";
    private static final boolean OVERWRITE = true;
    private static String report;


    private Helper() {
    }

    public static String uploadVideo(File file) throws Exception {
        BackendlessFile uploadedFile = Backendless.Files.upload(file, VIDEO_DIRECTORY, OVERWRITE);
        return uploadedFile.getFileURL();

    }

    public static String uploadPhoto(File file) throws Exception {
        BackendlessFile uploadedFile = Backendless.Files.upload(file, VIOL_PHOTO_DIRECTORY);
        return uploadedFile.getFileURL();

    }

    public static void updateUser(BackendlessUser user) throws BackendlessException {
        Backendless.UserService.update(user);
    }

    public static void updateUserWithPhoto(BackendlessUser user, File file) throws Exception {
        BackendlessFile uploadedUserPhoto = Backendless.Files.upload(file, USERS_PHOTO_DIRECTORY, OVERWRITE);
        String photoUrl = uploadedUserPhoto.getFileURL();

        user.setProperty("photoUrl", photoUrl);
        Backendless.UserService.update(user);
    }

    public static BackendlessCollection<Category_id> getAllCategories() {
        return Backendless.Persistence.of(Category_id.class).find();
    }

    public static BackendlessCollection<Violation> getAllViolations(String dataQuery) {
        BackendlessDataQuery dataQ = new BackendlessDataQuery();
        dataQ.setWhereClause(dataQuery);
        return Backendless.Data.of(Violation.class).find(dataQ);
    }

    public static BackendlessCollection<Violation> getAllViolations() {

        return Backendless.Data.of(Violation.class).find();
    }

    public static void deleteViolation(Violation violation) throws Exception {
        String fullUrl = violation.getVideoUrl();
        String fileName = fullUrl.substring(fullUrl.lastIndexOf('/') + 1);
        Backendless.Files.remove(VIDEO_DIRECTORY + "/" + fileName);
        Backendless.Persistence.of(Violation.class).remove(violation);
    }

    public static void showToast(String text, Context context) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void updateViolation(Violation violation) throws BackendlessException {
        Backendless.Persistence.save(violation);
    }

    public static Offerta findLastOffer() {
        return Backendless.Persistence.of(Offerta.class).findLast();
    }

    public static Violation findViolationById(String objectId) {
        return Backendless.Data.of(Violation.class).findById(objectId);
    }

    public static BackendlessUser findUserById(String userID) {
        try {
            return Backendless.UserService.findById(userID);
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveReport(Report report) throws BackendlessException{
            Backendless.Persistence.save(report);
    }

}
