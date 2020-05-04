package database;

import com.mongodb.WriteResult;
import database.dto.DetailsDTO;
import database.dto.EventDTO;
import database.dto.MessageDTO;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;
import database.exceptions.NoModificationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InitTestData {
    static IController controller = Controller.getInstance(); // production database by default


    public static void main(String[] args) throws NoModificationException {
        //controller.setDataSource(TestDB.getInstance());
        controller.killAll();
        System.out.println("Collections are deleted");

        List<String> usernames = initUsers();
        List<String> playgroundNames = initPlaygrounds();
        List<String> eventIDs = addEventsToPlaygrounds(playgroundNames);

        addUsersToEvent(usernames, eventIDs);
        addPedagoguesToPlaygrounds(usernames, playgroundNames);
        addMessagesToPlaygrounds(playgroundNames);
        System.out.println("Database is ready with test data!");
    }

    private static List<String> initPlaygrounds() {
        List<String> playgroundNames = null;
        try(BufferedReader br = new BufferedReader(new FileReader("src/test/data/playgroundData"))) {
            playgroundNames = new ArrayList<>();
            String line = br.readLine(); // skip first line
            line = br.readLine();

            PlaygroundDTO playground;
            while (line != null) {
                String[] data = line.split(",");

                playgroundNames.add(data[0]);
                playground = new PlaygroundDTO.Builder(data[0])
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

    private static List<String> initUsers() {
        List<String> usernames = null;
        try(BufferedReader br = new BufferedReader(new FileReader("src/test/data/userData"))) {
            usernames = new ArrayList<>();
            String line = br.readLine(); // skip first line
            line = br.readLine();

            UserDTO user;
            while (line != null) {
                String[] data = line.split(",");

                usernames.add(data[0]);
                user = new UserDTO.Builder(data[0])
                        .setPassword(data[1])
                        .setFirstname(data[2])
                        .setLastname(data[3])
                        .setEmail(data[4])
                        .phoneNumbers(data[5].split(" "))
                        .setImagePath(String.format("http://18.185.121.182:8080/rest/users/%s/profile-picture",data[0] ))
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

    private static void addMessagesToPlaygrounds(List<String> playgroundNames) {
        try(BufferedReader br = new BufferedReader(new FileReader("src/test/data/messageData"))) {
            String line = br.readLine(); // skip first line
            line = br.readLine();

            MessageDTO message;
            int i = 0;
            while (line != null) {
                String[] data = line.split(",");

                message = new MessageDTO.Builder()
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

    private static List<String> addEventsToPlaygrounds(List<String> playgroundNames) {
        List<String> eventID = null;
        try(BufferedReader br = new BufferedReader(new FileReader("src/test/data/eventData"))) {
            eventID = new ArrayList<>();
            String line = br.readLine(); // skip first line
            line = br.readLine();

            EventDTO event;
            int i = 0;
            while (line != null) {
                String[] data = line.split(",");

                event = new EventDTO.Builder()
                        .name(data[0])
                        .description(data[1])
                        .imagePath(data[2])
                        .details(new DetailsDTO(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis())))
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

    private static void addPedagoguesToPlaygrounds(List<String> usernames, List<String> playgroundNames) throws NoModificationException {
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

    private static void addUsersToEvent(List<String> usernames, List<String> eventID) throws NoModificationException {
        for (String id : eventID){
            int i = (int) (Math.random() * usernames.size() + 1) ;
            for (int j = 0; j < i; j++) {
                controller.addUserToEvent(id, usernames.get(j));
            }
        }
        System.out.println("Users are added to events");
    }
}