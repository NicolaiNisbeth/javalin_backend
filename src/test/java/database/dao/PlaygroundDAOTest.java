package database.dao;

import database.DALException;
import database.collections.Playground;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PlaygroundDAOTest {
    static PlaygroundDAO playgroundDAO;

    @BeforeAll
    public static void init() {
        playgroundDAO = new PlaygroundDAO();
    }

    @Test
    void createPlayground() throws DALException {
        Playground playground = new Playground.Builder("Vandlegepladsen i Fælledparken")
                .setCommune("København Ø")
                .setZipCode(2100)
                .setStreetName("Gunnar Nu Hansens Plads")
                .setStreetNumber(10)
                .setImagePath("https://www.google.com/imgres?imgurl=https%3A%2F%2Fberlingske.bmcdn.dk%2Fmedia%2Fcache%2Fresolve%2Fembedded_image_600x%2Fimage%2F29%2F297771%2F17762859-vandlegepladsen1.jpg&imgrefurl=https%3A%2F%2Fwww.berlingske.dk%2Fdet-gode-liv%2Fsommerferie-med-boern-her-er-6-af-de-bedste-legepladser-i-koebenhavn&tbnid=8KS7AmfvvL2R9M&vet=12ahUKEwip7qn96qnoAhXTNuwKHWZvBakQMygAegUIARDKAQ..i&docid=NnIEE3O_4_SjKM&w=600&h=400&q=Vandlegepladsen&ved=2ahUKEwip7qn96qnoAhXTNuwKHWZvBakQMygAegUIARDKAQ")
                .build();
        playgroundDAO.createPlayground(playground);

        playground = new Playground.Builder("Naturlegepladsen i Valbyparken")
                .setCommune("København SV")
                .setZipCode(2450)
                .setStreetName("Hammelstrupvej")
                .setStreetNumber(41)
                .setImagePath("https://www.kk.dk/sites/default/files/styles/flexslider_full/public/uploaded-images/naturlegepladsen_i_valbyparken_800x500.jpg?itok=8vuJum3Z")
                .build();
        playgroundDAO.createPlayground(playground);

        playground = new Playground.Builder("Legepladsen i Kildevældsparken")
                .setCommune("København Ø")
                .setZipCode(2100)
                .setStreetName("Vognmandsmarken")
                .setStreetNumber(69)
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("https://scontent-ams4-1.xx.fbcdn.net/v/t1.0-9/35925882_1752144438212095_2872486595854860288_o.jpg?_nc_cat=110&_nc_sid=6e5ad9&_nc_ohc=niAAIcBtSkEAX_InvHT&_nc_ht=scontent-ams4-1.xx&oh=9244ce211671c878bbb58aeb41d6e1d8&oe=5E9AE2B2")
                .build();
        playgroundDAO.createPlayground(playground);

        playground = new Playground.Builder("Legepladsen på Bispeengen")
                .setCommune("København N")
                .setZipCode(2200)
                .setStreetName("Hillerødgade 23B")
                .setStreetNumber(69)
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("https://kk.sites.itera.dk/apps/kk_legepladser_ny/images/stor/lp_37_02.jpg")
                .build();
        playgroundDAO.createPlayground(playground);


    }

    @Test
    void getPlayground() throws DALException {
        System.out.println(playgroundDAO.getPlayground("5e7500a29c55065cb293b635"));
    }

    @Test
    void getPlaygroundList() throws DALException {
        for (Playground playground : playgroundDAO.getPlaygroundList()) {
            System.out.println(playground);
        }
    }

    @Test
    void flereTing() throws DALException {
        Playground playground =
        playgroundDAO.getPlayground("5e75205dd466d77198dda9f1");
        String im =  "https://scontent-amt2-1.xx.fbcdn.net/v/t1.0-9/67077432_101575182585" +
                "57915_6578597722201260032_n.jpg?_nc_cat=108&_nc_sid=110474&_nc_ohc=" +
                "uPMRVS0UttoAX8LRLn0&_nc_ht=scontent-amt2-1.xx&oh=afd" +
                "560c10b71f35881c2552475615e4e&oe=5E9E4B50";
        playground.setImagePath(im);
        playgroundDAO.updatePlayground(playground);

    }

    @Test
    void updatePlayground() throws DALException {



        Playground playground = playgroundDAO.getPlayground("5e7500a29c55065cb293b635");
        System.out.println(playground);
        playground.setName("Ny plads");
        playground.setCommune("SNaps");
        playgroundDAO.updatePlayground(playground);
        System.out.println(playgroundDAO.getPlayground("5e7500a29c55065cb293b635"));
    }

    @Test
    void deletePlayground() throws DALException {
        playgroundDAO.deletePlayground("5e7500a29c55065cb293b635");

    }

    @Test
    void deleteAll() throws DALException {
        playgroundDAO.deleteAllPlaygrounds();
        getPlaygroundList();
    }
}