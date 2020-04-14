package javalin_resources.HttpMethods;


import database.dao.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {


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
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(afsender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(modtagere));
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("nicolailarsen2100@gmail.com"));
            message.setSubject(emne);
            message.setText(tekst);
            Transport.send(message);
        }

        public static void main(String[] args) throws MessagingException {


            sendMail("Test af Genereret app-adgangskode", "test", "nicolailarsen2100@gmail.com");
        }
    }

