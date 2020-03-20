package resources;


import database.collections.Playground;

import java.util.ArrayList;

//@Path("/galgeleg")
public class PlaygroundResource {
    public static ArrayList<Playground> getPlaygoundsList() {

        ArrayList<Playground> playgrounds = null;
      /*  try {
            playgrounds = (ArrayList<Playground>) DatabaseManagement.getPlaygrounds();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return playgrounds;
    }
}
