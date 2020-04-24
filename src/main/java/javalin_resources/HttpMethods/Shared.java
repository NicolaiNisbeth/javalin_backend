package javalin_resources.HttpMethods;

import io.javalin.http.Context;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Shared {

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
