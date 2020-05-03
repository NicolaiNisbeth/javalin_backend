package javalin_resources.collections;

import database.dto.UserDTO;
import database.Controller;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Properties;

public class Shared {
    public static boolean checkAdminCredentials(String username, String password, Context ctx) {
        UserDTO admin;
        //Hent admin - den der opretter brugeren
        try {
            admin = Controller.getInstance().getUser(username);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            ctx.status(401);
            ctx.json("Unauthorized - Wrong admin username");
            ctx.contentType("json");
            return false;
        }

        if (!admin.getStatus().equalsIgnoreCase("admin")) {
            ctx.status(401);
            ctx.json("Unauthorized - Wrong admin status");
            ctx.contentType("json");
            return false;
        }

        if (password.equalsIgnoreCase(admin.getPassword())) {
            return true;
        }

        if (BCrypt.checkpw(password, admin.getPassword())) {
            return true;
        } else {
            ctx.status(401);
            ctx.json("Unauthorized - Wrong admin password");
            ctx.contentType("json");
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

    public static void saveMessageImage(String messageID, BufferedImage bufferedImage) {
        File homeFolder = new File(System.getProperty("user.home"));
        Path path = Paths.get(String.format(homeFolder.toPath() +
                "/server_resource/message_images/%s.png", messageID));

        File imageFile = new File(path.toString());
        try {
            ImageIO.write(bufferedImage, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMessageImage(String messageID) {
        File homeFolder = new File(System.getProperty("user.home"));
        Path path = Paths.get(String.format(homeFolder.toPath() +
                "/server_resource/message_images/%s.png", messageID));

        File imageFile = new File(path.toString());
        if (imageFile.delete()) {
            System.out.println("Image delete for message with ID: " + messageID);
        } else {
            System.out.println("No image found for the deleted message.");
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

    public static void sendMail(String emne, String tekst, String modtagere) throws MessagingException {
        // Husk først at sænke sikkerheden på https://www.google.com/settings/security/lesssecureapps
        // Eller bruge en 'Genereret app-adgangskode' - se https://support.google.com/accounts/answer/185833?hl=da
        final String afsender = "nicolailarsen2100@gmail.com";
        System.out.println("sendMail " + emne + " " + modtagere + " " + tekst.replace('\n', ' '));
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // FØLGENDE KRÆVER JavaMail-BIBLIOTEKET
        // fjern evt koden, da du ikke skal sende mail fra din PC (det gør serveren jo)

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                //Path kodefil = Paths.get("gmail-adgangskode.txt");
                //String adgangskode = new String(Files.readAllBytes(kodefil));
                String adgangskode = "Explorer1984";
                return new PasswordAuthentication(afsender, adgangskode);
            }
        });
        javax.mail.Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(afsender));
        message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(modtagere));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("nicolailarsen2100@gmail.com"));
        message.setSubject(emne);
        message.setText(tekst);
        Transport.send(message);
    }

    public static void main(String[] args) throws MessagingException {
        sendMail("Test af Genereret app-adgangskode", "test", "nicolailarsen2100@gmail.com");
    }
}
