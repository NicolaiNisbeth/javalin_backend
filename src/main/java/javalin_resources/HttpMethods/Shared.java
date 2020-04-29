package javalin_resources.HttpMethods;

import database.dto.UserDTO;
import database.Controller;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public class Shared {
    public static boolean checkAdminCredentials(String username, String password, Context ctx) {
        UserDTO admin;
        //Hent admin - den der opretter brugeren
        try {
            admin = Controller.getInstance().getUser(username);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            ctx.status(401);
            ctx.result("Unauthorized - Wrong admin username");
            return false;
        }

        if (!admin.getStatus().equalsIgnoreCase("admin")) {
            ctx.status(401);
            ctx.result("Unauthorized - Wrong admin status");
            return false;
        }


        if (password.equalsIgnoreCase(admin.getPassword())) {
            return true;
        }

        if (BCrypt.checkpw(password, admin.getPassword())) {
            return true;
        } else {
            ctx.status(401);
            ctx.result("Unauthorized - Wrong admin setPassword");
            return false;
        }
    }

    public static void saveProfilePicture(String username, BufferedImage bufferedImage) {
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

    public static void saveProfilePicture2(Context ctx) {
        BufferedImage bufferedImage = null;
        String username = ctx.formParam("username");

        try {
            bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Shared.saveProfilePicture(username, bufferedImage);
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
}
