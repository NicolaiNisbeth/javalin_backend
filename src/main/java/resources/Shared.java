package resources;

import database.Controller;
import database.dto.UserDTO;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;

import javax.mail.Message;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
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
      ctx.json("Unauthorized - Wrong admin username");
      ctx.contentType("json");
      return false;
    }

    if (!admin.getStatus().equalsIgnoreCase("admin")) {
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
      ctx.json("Unauthorized - Wrong admin password");
      ctx.contentType("json");
      return false;
    }
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
