package resources;

import com.mongodb.WriteResult;
import database.DALException;
import database.collections.User;
import database.dao.Controller;
import org.json.JSONArray;
import org.json.JSONObject;
import io.javalin.http.Context;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// todo gør noget ved phonenumbers

public class UserAdminResource {
    final static String USERNAME_ADMIN = "usernameAdmin";
    final static String PASSWORD_ADMIN = "passwordAdmin";
    final static String USERNAME = "username";
    final static String PASSWORD = "password";
    final static String FIRSTNAME = "firstname";
    final static String LASTNAME = "lastname";
    final static String EMAIL = "email";
    final static String STATUS = "status";
    final static String STATUS_PEDAGOG = "pedagog";
    final static String STATUS_ADMIN = "admin";
    final static String PLAYGROUNDSIDS = "playgroundsIDs";
    final static String WEBSITE = "website";
    final static String PHONENUMBER = "phoneNumber";
    //todo ret addressen inden deployment
    //final static String IMAGEPATH = "http://localhost:8088/rest/user";
    final static String IMAGEPATH = "http://130.225.170.204:8088/rest/user";

    /**
     * Create
     * Metoden bruges af admins til at give en bruger rettigheder,
     * INDEN brugeren er logget på første gang
     *
     * @param ctx
     * @return
     */
    public static List<User> createUser(Context ctx) {
        BufferedImage bufferedImage;
        String usermodel = ctx.formParam(("usermodel"));
        JSONObject jsonObject = new JSONObject(usermodel);
        String usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
        String passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
        String username = jsonObject.getString(USERNAME);
        String password = jsonObject.getString(PASSWORD);
        String firstName = jsonObject.getString(FIRSTNAME);
        String lastName = jsonObject.getString(FIRSTNAME);
        String email = jsonObject.getString(EMAIL);
        String status = jsonObject.getString(STATUS);
        JSONArray playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
        String phoneNumber = jsonObject.getString(PHONENUMBER);
        String website = jsonObject.getString(WEBSITE);
        User admin = null;
        User newUser;

        try {
            admin = Controller.getInstance().getUser(usernameAdmin);
        } catch (DALException e) {
            ctx.status(401).result("Unauthorized - Forkert brugernavn eller adgangskode...");
            e.printStackTrace();
        }
        if (admin.getPassword().equalsIgnoreCase(passwordAdmin)) {

            newUser = new User.Builder(username)
                    .status(status)
                    .build();
            newUser.setFirstname(firstName);
            newUser.setLastname(lastName);
            newUser.setStatus(status);
            newUser.setEmail(email);
            newUser.setWebsite(website);
            String[] phoneNumbers = new String[1];
            phoneNumbers[0] = phoneNumber;
            newUser.setPhonenumbers(phoneNumbers);
            newUser.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", username));

            try {
                bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
                saveProfilePicture(username, bufferedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Object id : playgroundIDs) {
                newUser.getPlaygroundsIDs().add(id.toString());
            }

            WriteResult ws = Controller.getInstance().createUser(newUser);
            if (ws.wasAcknowledged()) {
                ctx.status(201).result("User was created");
            } else {
                ctx.status(401).result("User was not created");
                return Controller.getInstance().getUsers();
            }
            // Hvis admin har skrevet forkert adgangskode
        } else {
            ctx.status(401).result("Unauthorized - Forkert kodeord...");
        }
        return Controller.getInstance().getUsers();
    }

    //todo gem en bruger - marker ham som inaktiv

    /**
     * UPDATE
     *
     * @param ctx
     * @return
     */
    public static List<User> updateUser(Context ctx) {
        BufferedImage bufferedImage;
        String usermodel = ctx.formParam(("usermodel"));
        JSONObject jsonObject = new JSONObject(usermodel);
        String usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
        String passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
        String username = jsonObject.getString(USERNAME);
        String password = jsonObject.getString(PASSWORD);
        String firstName = jsonObject.getString(FIRSTNAME);
        String lastName = jsonObject.getString(LASTNAME);
        String email = jsonObject.getString(EMAIL);
        String status = jsonObject.getString(STATUS);
        JSONArray playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
        String phoneNumber = jsonObject.getString(PHONENUMBER);
        String website = jsonObject.getString(WEBSITE);

        User admin = null;
        User userToUpdate = null;
        try {
            admin = Controller.getInstance().getUser(usernameAdmin);
        } catch (DALException e) {
            e.printStackTrace();
        }
        if (!admin.getPassword().equalsIgnoreCase(passwordAdmin)) {
            System.out.println(admin.getPassword());
            System.out.println(passwordAdmin);
            ctx.status(401).result("Unauthorized - Kodeord er forkert...");
            return Controller.getInstance().getUsers();
        } else {
            try {
                userToUpdate = Controller.getInstance().getUser(username);
            } catch (DALException e) {
                e.printStackTrace();
            }
            userToUpdate.setFirstname(firstName);
            userToUpdate.setLastname(lastName);
            userToUpdate.setStatus(status);
            userToUpdate.setEmail(email);
            userToUpdate.setWebsite(website);
            userToUpdate.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", username));
            String[] phoneNumbers = new String[1];
            phoneNumbers[0] = phoneNumber;
            userToUpdate.setPhonenumbers(phoneNumbers);
            userToUpdate.getPlaygroundsIDs().removeAll(userToUpdate.getPlaygroundsIDs());
            for (Object id : playgroundIDs) {
                userToUpdate.getPlaygroundsIDs().add(id.toString());
            }
            try {
                bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
                saveProfilePicture(username, bufferedImage);
            } catch (Exception e) {
               //e.printStackTrace();
                System.out.println("Server: intet billede i upload");
            }
            if (Controller.getInstance().updateUser(userToUpdate)) {
                ctx.status(401).result("User was updated");
            } else {
                ctx.status(401).result("User was not updated");
                return Controller.getInstance().getUsers();
            }
        }
        return Controller.getInstance().getUsers();
    }

    /**
     * Delete
     *
     * @param body
     * @param ctx
     * @return
     */
    public static List<User> deleteUser(String body, Context ctx) {
        JSONObject jsonObject = new JSONObject(ctx.body());
        String usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
        String passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
        String username = jsonObject.getString(USERNAME);
        // todo slet ham fra legeplader også
        //  JSONArray adminRightsOfNewUser = jsonObject.getJSONArray("userAdminRights");

        User admin = null;
        try {
            admin = Controller.getInstance().getUser(usernameAdmin);
        } catch (DALException e) {
            e.printStackTrace();
        }
        if (!admin.getPassword().equalsIgnoreCase(passwordAdmin)) {
            ctx.status(401).result("Unauthorized - Forkert kodeord...");
        } else {
            Controller.getInstance().deleteUser(username);
        }
        return Controller.getInstance().getUsers();
    }

    public static void printImage(BufferedImage bufferedImage) {
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 900, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel(new ImageIcon(bufferedImage));
        //label.setBounds(0, 0, 100, 200);
        panel.add(label, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    static void saveProfilePicture(String username, BufferedImage bufferedImage) {
        //String path = String.format("src/main/resources/images/profile_pictures/%s.png", username);

        File homeFolder = new File(System.getProperty("user.home"));
        Path path = Paths.get(String.format(homeFolder.toPath() +
                "/server_resource/profile_images/%s.png", username));

        //String path = String.format("src/main/resources/images/profile_pictures/%s.png", username);
        File imageFile = new File(path.toString());
        try {
            ImageIO.write(bufferedImage, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static InputStream getProfilePicture(String username) {
        File homeFolder = new File(System.getProperty("user.home"));
        Path path = Paths.get(String.format(homeFolder.toPath() +
                "/server_resource/profile_images/%s.png", username));

        File initialFile = new File(path.toString());
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(initialFile);
/*            BufferedImage in = ImageIO.read(initialFile);
            UserAdminResource.printImage(in);*/
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Server: User have no profile picture...");
        }

        if (targetStream != null) {
            return targetStream;
        } else {
            System.out.println("Server: Returning random user picture...");
            targetStream = UserLoginResource.class.getResourceAsStream("/images/profile_pictures/random_user.png");
            return targetStream;
        }
    }
}
