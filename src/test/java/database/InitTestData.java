package database;

import com.mongodb.WriteResult;
import database.collections.Details;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;
import database.dao.*;
import database.exceptions.NoModificationException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InitTestData {
    static IController controller = Controller.getInstance();

    @Test
    void initFreshData() throws NoModificationException {
        killAll();

        List<String> usernames = initUsers();
        List<String> playgroundNames = initPlaygrounds();

        addPedagoguesToPlaygrounds(usernames, playgroundNames);
        List<String> eventIDs = addEventsToPlaygrounds(playgroundNames);
        addMessagesToPlaygrounds(playgroundNames);
        addParticipantsToEvent(usernames, eventIDs);
        System.out.println("Database is ready with test data!");
    }

    private static void killAll() {
        controller.killAll();
        System.out.println("Collections are deleted");
    }

    private List<String> initPlaygrounds() {
        List<String> playgroundNames = null;
        try(BufferedReader br = new BufferedReader(new FileReader("src/test/data/playgroundData"))) {
            playgroundNames = new ArrayList<>();
            String line = br.readLine(); // skip first line
            line = br.readLine();

            Playground playground;
            while (line != null) {
                String[] data = line.split(",");

                playgroundNames.add(data[0]);
                playground = new Playground.Builder(data[0])
                        .setStreetName(data[1])
                        .setStreetNumber(Integer.parseInt(data[2]))
                        .setCommune(data[3])
                        .setZipCode(Integer.parseInt(data[4]))
                        .setImagePath(data[5])
                        .setToiletPossibilities(data[6].equals("true"))
                        .setHasSoccerField(data[7].equals("true"))
                        .build();

                controller.createPlayground(playground);
                line = br.readLine();
            }
        } catch (IOException | NoModificationException e) {
            e.printStackTrace();
        }
        System.out.println("playgrounds are created");
        return playgroundNames;
    }

    private List<String> initUsers() {
        List<String> usernames = null;
        try(BufferedReader br = new BufferedReader(new FileReader("src/test/data/userData"))) {
            usernames = new ArrayList<>();
            String line = br.readLine(); // skip first line
            line = br.readLine();

            User user;
            while (line != null) {
                String[] data = line.split(",");

                usernames.add(data[0]);
                user = new User.Builder(data[0])
                        .setPassword(data[1])
                        .setFirstname(data[2])
                        .setLastname(data[3])
                        .setEmail(data[4])
                        .phoneNumbers(data[5].split(" "))
                        .imagePath(data[6])
                        .status(data[7])
                        .build();

                controller.createUser(user);
                line = br.readLine();
            }
        } catch (IOException | NoModificationException e) {
            e.printStackTrace();
        }

        System.out.println("Users are created");
        return usernames;
    }

    private void addMessagesToPlaygrounds(List<String> playgroundNames) {
        try(BufferedReader br = new BufferedReader(new FileReader("src/test/data/messageData"))) {
            String line = br.readLine(); // skip first line
            line = br.readLine();

            Message message;
            int i = 0;
            while (line != null) {
                String[] data = line.split(",");

                message = new Message.Builder()
                        .setCategory(data[0])
                        .setIcon(data[1])
                        .setMessageString(data[2])
                        .build();

                controller.createPlaygroundMessage(playgroundNames.get(i), message);
                i = (i + 1) % playgroundNames.size();
                line = br.readLine();
            }
        } catch (IOException | NoModificationException e) {
            e.printStackTrace();
        }
        System.out.println("Messages are added to playgrounds");
    }

    private List<String> addEventsToPlaygrounds(List<String> playgroundNames) {
        List<String> eventID = null;
        try(BufferedReader br = new BufferedReader(new FileReader("src/test/data/eventData"))) {
            eventID = new ArrayList<>();
            String line = br.readLine(); // skip first line
            line = br.readLine();

            Event event;
            int i = 0;
            while (line != null) {
                String[] data = line.split(",");

                event = new Event.Builder()
                        .name(data[0])
                        .description(data[1])
                        .imagePath(data[2])
                        .details(new Details(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis())))
                        .build();

                WriteResult wr = controller.createPlaygroundEvent(playgroundNames.get(i), event);
                eventID.add(wr.getUpsertedId().toString());
                i = (i + 1) % playgroundNames.size();
                line = br.readLine();
            }
        } catch (IOException | NoModificationException e) {
            e.printStackTrace();
        }
        System.out.println("Events are added to playgrounds");
        return eventID;
    }

    private void addPedagoguesToPlaygrounds(List<String> usernames, List<String> playgroundNames) throws NoModificationException {
        controller.addPedagogueToPlayground(playgroundNames.get(0), usernames.get(1));
        controller.addPedagogueToPlayground(playgroundNames.get(0), usernames.get(2));

        controller.addPedagogueToPlayground(playgroundNames.get(1), usernames.get(1));

        controller.addPedagogueToPlayground(playgroundNames.get(2), usernames.get(1));
        controller.addPedagogueToPlayground(playgroundNames.get(2), usernames.get(9));

        controller.addPedagogueToPlayground(playgroundNames.get(3), usernames.get(2));

        controller.addPedagogueToPlayground(playgroundNames.get(4), usernames.get(2));
        controller.addPedagogueToPlayground(playgroundNames.get(4), usernames.get(11));

        controller.addPedagogueToPlayground(playgroundNames.get(5), usernames.get(9));

        controller.addPedagogueToPlayground(playgroundNames.get(6), usernames.get(10));
        controller.addPedagogueToPlayground(playgroundNames.get(6), usernames.get(13));

        controller.addPedagogueToPlayground(playgroundNames.get(7), usernames.get(11));
        controller.addPedagogueToPlayground(playgroundNames.get(7), usernames.get(18));
        System.out.println("Pedagogues are added to playgrounds");
    }

    private void addParticipantsToEvent(List<String> usernames, List<String> eventID) throws NoModificationException {
        for (String id : eventID){
            int i = (int) (Math.random() * usernames.size() + 1) ;
            for (int j = 0; j < i; j++) {
                controller.addUserToEvent(id, usernames.get(j));
            }
        }
        System.out.println("Participants are added to events");
    }
}