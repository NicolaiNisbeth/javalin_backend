package soap;


import database.Controller;
import database.IController;
import database.dto.EventDTO;
import database.dto.MessageDTO;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.util.List;

@WebService(endpointInterface = "soap.ISoap")
public class Soap implements ISoap {
    private final String NOT_IMPLEMENTED_STR = "NOT IMPLEMENTED";
    private final int NOT_IMPLEMENTED_INT = -1;
    private IController controller = Controller.getInstance();

    public static void main(String[] args) {
        System.out.println("Publicerer som " + ISoap.URL);
        ISoap server = new Soap();
        Endpoint.publish(ISoap.URL, server);
        System.out.println("Soap service publiceret");
    }

    @Override
    public String displayUserStatistics() {
        List<UserDTO> users = controller.getUsers();
        int n = users.size();
        int numClients = 0;
        int numPedagogue = 0;
        int numAdmin = 0;

        for (UserDTO user : users){
            if (user.getStatus() == null) continue;
            switch (user.getStatus()) {
                case "client": numClients++; break;
                case "pædagog": numPedagogue++; break;
                case "pedagog": numPedagogue++; break;
                case "admin": numAdmin++; break;
                default:
                    throw new IllegalStateException(String.format("User %s has role %s", user.getId(), user.getStatus()));
            }
        }
        return String.format(
                "\n### USERS ###\n" +
                "n = %s\n" +
                "Clients = %s\n" +
                "Pedagogues = %s\n" +
                "Admins = %s\n",
                n, numClients, numPedagogue, numAdmin);
    }

    @Override
    public String displayEventStatistics() {
        List<EventDTO> events = controller.getEvents();
        int n = events.size();
        int numParticipants = 0;
        double avgParticipants;
        int maxParticipants = Integer.MIN_VALUE;
        String maxParticipantsID = "-1";

        for (EventDTO event : events){
            int participants = event.getAssignedUsers().size();
            numParticipants += participants;
            if (participants > maxParticipants){
                maxParticipants = participants;
                maxParticipantsID = event.getID();
            }
        }

        avgParticipants = (double) numParticipants / n;

        return String.format(
                "\n### EVENT ###\n" +
                "n = %s\n" +
                "Total participants = %s\n" +
                "Average participation = %s\n" +
                "Maximum participation = %s and event ID = %s\n",
                n, numParticipants, avgParticipants, maxParticipants, maxParticipantsID);
    }

    @Override
    public String displayPlaygroundStatistics() {
        List<PlaygroundDTO> playgrounds = controller.getPlaygrounds();
        int n = playgrounds.size();
        int numPedagogues = 0;
        double avgPedagogues;

        int numEvents = 0;
        double avgEvents;
        int maxEvents = Integer.MIN_VALUE;
        String maxEventID = "-1";
        int minEvents = Integer.MAX_VALUE;
        String minEventID = "-1";

        int numMessages = 0;
        double avgMessages = 0;
        int maxMessages = Integer.MIN_VALUE;
        String maxMessagesID = "-1";
        int minMessages = Integer.MAX_VALUE;
        String minMessagesID = "-1";

        for (PlaygroundDTO playground : playgrounds){
            numPedagogues += playground.getAssignedPedagogue().size();
            numEvents += playground.getEvents().size();
            numMessages += playground.getMessages().size();

            if (numEvents > maxEvents){
                maxEvents = numEvents;
                maxEventID = playground.getId();
            }
            if (numEvents < minEvents){
                minEvents = numEvents;
                minEventID = playground.getId();
            }
            if (numMessages > maxMessages){
                maxMessages = numMessages;
                maxMessagesID = playground.getId();
            }
            if (numMessages < minMessages){
                minMessages = numMessages;
                minMessagesID = playground.getId();
            }
        }

        avgPedagogues = (double)numPedagogues / n;
        avgEvents = (double)numEvents / n;

        return String.format(
                "\n### PLAYGROUND ###\n" +
                "n = %s\n" +
                "Total pedagogues %s\n" +
                "Average pedagogues %s\n" +
                "Total events %s\n" +
                "Average events %s\n" +
                "Maximum event %s and playground ID %s\n" +
                "Minimum event %s and playground ID %s\n" +
                "Total messages %s\n" +
                "Average messages %s\n" +
                "Maximum message %s and Playground ID %s\n" +
                "Minimum message %s and Playground ID %s\n",
                n, numPedagogues, avgPedagogues,
                numEvents, avgEvents, maxEvents, maxEventID, minEvents, minEventID,
                numMessages, avgMessages, maxMessages, maxMessagesID, minMessages, minMessagesID);

    }

    @Override
    public String displayMessageStatistics() {
        List<MessageDTO> messages = controller.getmessages();
        int n = messages.size();
        int numInteraction = NOT_IMPLEMENTED_INT;
        double avgInteraction = NOT_IMPLEMENTED_INT;
        int maxInteraction = NOT_IMPLEMENTED_INT;
        String maxInteractionID = NOT_IMPLEMENTED_STR;

        /*
        for (Message message : messages){
            int interactions = message.getInteractions();
            numInteraction += message.getInteractions();
            if (interactions > maxInteraction){
                maxInteraction = interactions;
                maxInteractionID = message.getId();
            }
        }
         */

        //avgInteraction = (double) numInteraction / n;

        return String.format(
                "\n### MESSAGE ###\n" +
                "n = %s\n" +
                "Total interactions = %s\n" +
                "Average interactions = %s\n" +
                "Maximum interactions = %s and playground ID = %s\n",
                n, numInteraction, avgInteraction, maxInteraction, maxInteractionID);
    }
}
