package javalin_resources.HttpMethods;

import database.exceptions.DALException;
import database.collections.*;
import database.collections.Event;
import database.dao.Controller;
import io.javalin.http.Handler;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class Put implements Tag {

    public static class PutEvent {

        public static Handler updateEventToPlaygroundPut = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            Playground playground = Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME));

            if (playground != null) {

                if (jsonObject.has(EVENT_ID)) {
                    event.setId(jsonObject.getString(EVENT_ID));
                }
                if (jsonObject.has(EVENT_NAME)) {
                    event.setName(jsonObject.getString(EVENT_NAME));
                }
                if (jsonObject.has(EVENT_IMAGEPATH)) {
                    event.setImagepath(jsonObject.getString(EVENT_IMAGEPATH));
                }
                if (jsonObject.has(EVENT_PARTICIPANTS)) {
                    event.setParticipants(jsonObject.getInt(EVENT_PARTICIPANTS));
                }
                if (jsonObject.has(EVENT_DESCRIPTION)) {
                    event.setDescription(jsonObject.getString(EVENT_DESCRIPTION));
                }
                if (jsonObject.has(EVENT_DETAILS)) {
                    //TODO: Change this
                    event.setDetails(null);
                }
                if (jsonObject.has(EVENT_ASSIGNED_USERS)) {
                    Set<User> assignedUsers = new HashSet<>();
                    for (int i = 0; i < jsonObject.getJSONArray(EVENT_ASSIGNED_USERS).length(); i++) {
                        String assignedUserId = jsonObject.getJSONArray(EVENT_ASSIGNED_USERS).getJSONObject(i).getString(EVENT_ASSIGNED_USERS);
                        assignedUsers.add(Controller.getInstance().getUser(assignedUserId));
                    }
                    event.setAssignedUsers(assignedUsers);
                }
                if (jsonObject.has(PLAYGROUND_NAME)) {
                    event.setPlayground(PLAYGROUND_NAME);
                }
                Controller.getInstance().updatePlaygroundEvent(event);
                if (true) {
                    ctx.status(200).result("Event is updated");
                }
            } else {
                ctx.status(404).result("Couldn't update event");
            }
        };

    }

    public static class PutPlayground {

        public static Handler updatePlaygroundPut = ctx -> {
            Playground playground = Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME));
            JSONObject jsonObject = new JSONObject(ctx.body());
            if (playground != null) {
                if (jsonObject.has(PLAYGROUND_STREET_NAME))
                    playground.setStreetName(jsonObject.getString(PLAYGROUND_STREET_NAME));

                if (jsonObject.has(PLAYGROUND_PEDAGOGUES)) {
                    Set<User> pedagoges = new HashSet<>();

                    for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_PEDAGOGUES).length(); i++) {
                        String username = jsonObject.getJSONArray(PLAYGROUND_PEDAGOGUES).getString(i);
                        pedagoges.add(Controller.getInstance().getUser(username));
                    }
                    playground.setAssignedPedagogue(pedagoges);
                }

                if (jsonObject.has(PLAYGROUND_COMMUNE))
                    playground.setCommune(jsonObject.getString(PLAYGROUND_COMMUNE));

                if (jsonObject.has(PLAYGROUND_EVENTS)) {
                    Set<Event> eventSet = new HashSet<>();
                    for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_EVENTS).length(); i++) {
                        String eventid = jsonObject.getJSONArray(PLAYGROUND_EVENTS).getJSONObject(i).getString(PLAYGROUND_EVENTS);
                        eventSet.add(Controller.getInstance().getEvent(eventid));
                    }
                    playground.setEvents(eventSet);
                }
                if (jsonObject.has(PLAYGROUND_HASSOCCERFIELD))
                    playground.setHasSoccerField(jsonObject.getBoolean(PLAYGROUND_HASSOCCERFIELD));

                if (jsonObject.has(PLAYGROUND_ID))
                    playground.setId(jsonObject.getString(PLAYGROUND_ID));

                if (jsonObject.has(PLAYGROUND_IMAGEPATH))
                    playground.setImagePath(jsonObject.getString(PLAYGROUND_IMAGEPATH));

                if (jsonObject.has(PLAYGROUND_MESSAGES)) {
                    Set<Message> messagesSet = new HashSet<>();
                    for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_MESSAGE_ID).length(); i++) {
                        String messageid = jsonObject.getJSONArray(PLAYGROUND_MESSAGE_ID).getJSONObject(i).getString(PLAYGROUND_MESSAGE_ID);
                        messagesSet.add(Controller.getInstance().getMessage(messageid));
                    }
                    playground.setMessages(messagesSet);
                }

                if (jsonObject.has(PLAYGROUND_STREET_NUMBER))
                    playground.setStreetNumber(jsonObject.getInt(PLAYGROUND_STREET_NUMBER));

                if (jsonObject.has(PLAYGROUND_TOILETS))
                    playground.setToiletPossibilities(jsonObject.getBoolean(PLAYGROUND_TOILETS));

                if (jsonObject.has(PLAYGROUND_ZIPCODE))
                    playground.setZipCode(jsonObject.getInt(PLAYGROUND_ZIPCODE));

                Controller.getInstance().updatePlayground(playground);
                if (true) {
                    ctx.status(200).result("Playground updated");
                    //Test
                    System.out.println("update playground with name " + playground.getName());
                }
            } else {
                ctx.status(404).result("Playground didn't update");
            }
        };

    }

    public static class PutPedagogue {

        public static Handler updatePedagogueToPlayGroundPut = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Playground playground = Controller.getInstance().getPlayground(jsonObject.getString(PLAYGROUND_NAME));
            User user = Controller.getInstance().getUser(jsonObject.getString(PEDAGOGUE));
            playground.getAssignedPedagogue().add(user);
            Controller.getInstance().updatePlayground(playground);
            if (jsonObject.getString(PEDAGOGUE) != null && jsonObject.getString(PLAYGROUND_NAME) != null) {
                ctx.status(200).result("Updated Successfull");
            } else {
                ctx.status(404).result("Failed to update");
            }
        };

    }

    public static class PutUser {

        public static Handler resetPassword = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            String username = jsonObject.getString(USERNAME);
            User user = null;

            try {
                user = Controller.getInstance().getUser(username);
            } catch (NoSuchElementException e) {
                ctx.status(401).result("Unauthorized");
                e.printStackTrace();
            }
            if (user.getEmail() == null) {
                //reset setPassword
            } else {
                try {
                    String newPassword = "1234";
                    user.setPassword(newPassword);
                    Controller.getInstance().updateUser(user);
                    Controller.getInstance().getUser(user.getUsername());
                    SendMail.sendMail("Your new setPassword", "Your new setPassword is: " + newPassword, user.getEmail());
                } catch (MessagingException | NoSuchElementException e) {
                    e.printStackTrace();
                }
            }

            ctx.status(401).result("Unauthorized - Wrong setPassword");
        };

        public static Handler updateUser = ctx -> {
            BufferedImage bufferedImage;
            String usernameAdmin, passwordAdmin, username, password,
                    firstName, lastName, email, status, website;
            JSONArray phoneNumbers, playgroundIDs;

            try {
                String usermodel = ctx.formParam(("usermodel"));
                JSONObject jsonObject = new JSONObject(usermodel);
                usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
                passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
                username = jsonObject.getString(USERNAME);
                password = jsonObject.getString(PASSWORD);
                firstName = jsonObject.getString(FIRSTNAME);
                lastName = jsonObject.getString(LASTNAME);
                email = jsonObject.getString(EMAIL);
                status = jsonObject.getString(STATUS);
                website = jsonObject.getString(WEBSITE);
                playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
                phoneNumbers = jsonObject.getJSONArray(PHONENUMBERS);

                if (username.length() < 1 || password.length() < 1)
                    throw new DALException("Missing username or setPassword");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(400);
                ctx.result("Bad Request - Error in user data");
                return;
            }

            User userToUpdate = null;

            //Hvis user ikke opdaterer sig selv, er det en admin der opdaterer
            if (!username.equalsIgnoreCase(usernameAdmin)) {
                boolean adminAuthorized = Shared.checkAdminCredentials(usernameAdmin, passwordAdmin, ctx);
                if (!adminAuthorized) {
                    return;
                }
            }

            try {
                userToUpdate = Controller.getInstance().getUser(username);
            } catch (NoSuchElementException e) {
                ctx.status(401);
                ctx.result("Unauthorized - Username doesn't exist");
            }
            userToUpdate.setFirstname(firstName);
            userToUpdate.setLastname(lastName);
            userToUpdate.setStatus(status);
            userToUpdate.setEmail(email);
            userToUpdate.setWebsite(website);
            userToUpdate.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", username));

            String[] usersNewPhoneNumbers = new String[phoneNumbers.length()];
            for (int i = 0; i < phoneNumbers.length(); i++) {
                try {
                    usersNewPhoneNumbers[i] = (String) phoneNumbers.get(i);
                } catch (ClassCastException e) {
                }
            }
            userToUpdate.setPhoneNumbers(usersNewPhoneNumbers);

            Set<String> usersOldPGIds = userToUpdate.getPlaygroundsIDs();
            System.out.println("Old pgs " + usersOldPGIds);

            Set<String> usersNewPGIds = new HashSet<>();
            for (int i = 0; i < playgroundIDs.length(); i++) {
                try {
                    usersNewPGIds.add((String) playgroundIDs.get(i));
                } catch (ClassCastException e) {
                }
            }
            userToUpdate.setPlaygroundsIDs(usersNewPGIds);

            if (usersOldPGIds != null || usersOldPGIds.size() > 0) {
                for (String oldPlaygroundName : usersOldPGIds) {
                    if (!usersNewPGIds.contains(oldPlaygroundName)) {
                        Controller.getInstance().removePedagogueFromPlayground(oldPlaygroundName, userToUpdate.getUsername());
                    }
                }
            }

           /* for (String oldPlaygroundName : usersOldPGIds) {
                boolean match = false;
                for (String newPlaygroundName : usersNewPGIds) {
                    if (oldPlaygroundName.equalsIgnoreCase(newPlaygroundName)) {
                        match = true;
                    }
                    if (!match) {
                        Controller.getInstance().removePedagogueFromPlayground(oldPlaygroundName, userToUpdate.getUsername());
                    }
                }
            }*/

            try {
                bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
                Shared.saveProfilePicture(username, bufferedImage);
            } catch (
                    Exception e) {
                System.out.println("Server: No image in upload");
            }
            if (Controller.getInstance().updateUser(userToUpdate).wasAcknowledged()) {
                ctx.status(201);
                ctx.result("User updated");
                ctx.json(userToUpdate);

                //Tilføj brugeren til de playgrounds han er tilknyttet
                //Controller.getInstance().addPedagogueToPlayground(userToUpdate);

            } else {
                ctx.status(500);
                ctx.result("User not updated");
            }
        };
    }

    public static class PutMessage {

        public static Handler updatePlaygroundMessagePut = ctx -> {

            JSONObject jsonObject = new JSONObject(ctx.body());
            Message message = Controller.getInstance().getMessage(ctx.pathParam("id"));

            // TODO Hvordan kommer den detail parameter til at foregå?
            if (jsonObject.get(HOUR) != null) {
                Calendar cal = Calendar.getInstance();

                cal.set(Calendar.YEAR, jsonObject.getInt(YEAR));
                cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(DAY));
                cal.set(Calendar.MONTH, jsonObject.getInt(MONTH));


                cal.set(Calendar.HOUR, jsonObject.getInt(HOUR));
                cal.set(Calendar.MINUTE, jsonObject.getInt(MINUTE));
                message.setDate(cal.getTime());
            }
            if (jsonObject.get(MESSAGE_CATEGORY) != null)
                message.setCategory(jsonObject.getString(MESSAGE_CATEGORY));

            if (jsonObject.get(MESSAGE_ICON) != null)
                message.setIcon(jsonObject.getString(MESSAGE_ICON));

            if (jsonObject.get(MESSAGE_STRING) != null)
                message.setMessageString(jsonObject.getString(MESSAGE_STRING));

            if (jsonObject.get(PLAYGROUND_ID) != null)
                message.setPlaygroundID(jsonObject.getString(PLAYGROUND_ID));

            if (jsonObject.get(MESSAGE_WRITTENBY_ID) != null)
                message.setWrittenByID(MESSAGE_WRITTENBY_ID);

            if (Controller.getInstance().createPlaygroundMessage(jsonObject.getString(PLAYGROUND_ID), message).wasAcknowledged())
                ctx.status(200).result("The message was created for the playground " + jsonObject.getString(PLAYGROUND_ID));

            else {
                ctx.status(404).result("There was an error");
            }
        };
    }

}

