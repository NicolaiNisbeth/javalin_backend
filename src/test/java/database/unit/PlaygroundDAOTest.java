package database.unit;

import com.mongodb.WriteResult;
import database.TestDB;
import database.dao.IPlaygroundDAO;
import database.dao.PlaygroundDAO;
import database.dto.PlaygroundDTO;
import database.exceptions.NoModificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaygroundDAOTest {
  static IPlaygroundDAO playgroundDAO = new PlaygroundDAO(TestDB.getInstance());

  @BeforeAll
  static void killAll() {
    playgroundDAO.deleteAllPlaygrounds();
  }

  @Test
  void createdPlaygroundShouldBeFetchedPlayground() throws NoModificationException {
    PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegepladsen i Fælledparken")
      .setCommune("København Ø")
      .setZipCode(2100)
      .setStreetName("Gunnar Nu Hansens Plads")
      .setStreetNumber(10)
      .setImagePath("https://www.google.com/imgres?imgurl=https%3A%2F%2Fberlingske.bmcdn.dk%2Fmedia%2Fcache%2Fresolve%2Fembedded_image_600x%2Fimage%2F29%2F297771%2F17762859-vandlegepladsen1.jpg&imgrefurl=https%3A%2F%2Fwww.berlingske.dk%2Fdet-gode-liv%2Fsommerferie-med-boern-her-er-6-af-de-bedste-legepladser-i-koebenhavn&tbnid=8KS7AmfvvL2R9M&vet=12ahUKEwip7qn96qnoAhXTNuwKHWZvBakQMygAegUIARDKAQ..i&docid=NnIEE3O_4_SjKM&w=600&h=400&q=Vandlegepladsen&ved=2ahUKEwip7qn96qnoAhXTNuwKHWZvBakQMygAegUIARDKAQ")
      .build();

    PlaygroundDTO playground2 = new PlaygroundDTO.Builder("Naturlegepladsen i Valbyparken")
      .setCommune("København SV")
      .setZipCode(2450)
      .setStreetName("Hammelstrupvej")
      .setStreetNumber(41)
      .setImagePath("https://www.kk.dk/sites/default/files/styles/flexslider_full/public/uploaded-images/naturlegepladsen_i_valbyparken_800x500.jpg?itok=8vuJum3Z")
      .build();

    PlaygroundDTO playground3 = new PlaygroundDTO.Builder("Legepladsen i Kildevældsparken")
      .setCommune("København Ø")
      .setZipCode(2100)
      .setStreetName("Vognmandsmarken")
      .setStreetNumber(69)
      .setToiletPossibilities(true)
      .setHasSoccerField(true)
      .setImagePath("https://scontent-ams4-1.xx.fbcdn.net/v/t1.0-9/35925882_1752144438212095_2872486595854860288_o.jpg?_nc_cat=110&_nc_sid=6e5ad9&_nc_ohc=niAAIcBtSkEAX_InvHT&_nc_ht=scontent-ams4-1.xx&oh=9244ce211671c878bbb58aeb41d6e1d8&oe=5E9AE2B2")
      .build();

    PlaygroundDTO playground4 = new PlaygroundDTO.Builder("Legepladsen på Bispeengen")
      .setCommune("København N")
      .setZipCode(2200)
      .setStreetName("Hillerødgade 23B")
      .setStreetNumber(69)
      .setToiletPossibilities(true)
      .setHasSoccerField(true)
      .setImagePath("https://kk.sites.itera.dk/apps/kk_legepladser_ny/images/stor/lp_37_02.jpg")
      .build();

    WriteResult wr = playgroundDAO.createPlayground(playground);
    WriteResult wr2 = playgroundDAO.createPlayground(playground2);
    WriteResult wr3 = playgroundDAO.createPlayground(playground3);
    WriteResult wr4 = playgroundDAO.createPlayground(playground4);

    PlaygroundDTO fetchedPlayground = playgroundDAO.getPlayground(playground.getName());
    PlaygroundDTO fetchedPlayground2 = playgroundDAO.getPlayground(playground2.getName());
    PlaygroundDTO fetchedPlayground3 = playgroundDAO.getPlayground(playground3.getName());
    PlaygroundDTO fetchedPlayground4 = playgroundDAO.getPlayground(playground4.getName());

    Assertions.assertAll(
      () -> Assertions.assertEquals(playground, fetchedPlayground),
      () -> Assertions.assertEquals(playground2, fetchedPlayground2),
      () -> Assertions.assertEquals(playground3, fetchedPlayground3),
      () -> Assertions.assertEquals(playground4, fetchedPlayground4)
    );

    playgroundDAO.deletePlayground(playground.getName());
    playgroundDAO.deletePlayground(playground2.getName());
    playgroundDAO.deletePlayground(playground3.getName());
    playgroundDAO.deletePlayground(playground4.getName());
  }

  @Test
  void createTwoPlaygroundsShouldFetchListSizeTwo() throws NoModificationException {
    PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegepladsen i Fælledparken")
      .setCommune("København Ø")
      .setZipCode(2100)
      .setStreetName("Gunnar Nu Hansens Plads")
      .setStreetNumber(10)
      .setImagePath("https://www.google.com/imgres?imgurl=https%3A%2F%2Fberlingske.bmcdn.dk%2Fmedia%2Fcache%2Fresolve%2Fembedded_image_600x%2Fimage%2F29%2F297771%2F17762859-vandlegepladsen1.jpg&imgrefurl=https%3A%2F%2Fwww.berlingske.dk%2Fdet-gode-liv%2Fsommerferie-med-boern-her-er-6-af-de-bedste-legepladser-i-koebenhavn&tbnid=8KS7AmfvvL2R9M&vet=12ahUKEwip7qn96qnoAhXTNuwKHWZvBakQMygAegUIARDKAQ..i&docid=NnIEE3O_4_SjKM&w=600&h=400&q=Vandlegepladsen&ved=2ahUKEwip7qn96qnoAhXTNuwKHWZvBakQMygAegUIARDKAQ")
      .build();

    PlaygroundDTO playground2 = new PlaygroundDTO.Builder("Naturlegepladsen i Valbyparken")
      .setCommune("København SV")
      .setZipCode(2450)
      .setStreetName("Hammelstrupvej")
      .setStreetNumber(41)
      .setImagePath("https://www.kk.dk/sites/default/files/styles/flexslider_full/public/uploaded-images/naturlegepladsen_i_valbyparken_800x500.jpg?itok=8vuJum3Z")
      .build();


    WriteResult wr = playgroundDAO.createPlayground(playground);
    WriteResult wr2 = playgroundDAO.createPlayground(playground2);

    List<PlaygroundDTO> playgroundList = playgroundDAO.getPlaygroundList();
    Assertions.assertAll(
      () -> Assertions.assertEquals(2, playgroundList.size()),
      () -> Assertions.assertEquals(playground, playgroundList.get(0)),
      () -> Assertions.assertEquals(playground2, playgroundList.get(1))
    );

    playgroundDAO.deletePlayground(playground.getName());
    playgroundDAO.deletePlayground(playground2.getName());
  }

  @Test
  void updatePlaygroundShouldFetchUpdatedPlayground() throws NoModificationException {
    PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
      .setStreetName("Agervænget")
      .setStreetNumber(34)
      .setZipCode(3650)
      .setCommune("Egedal")
      .setToiletPossibilities(true)
      .setHasSoccerField(true)
      .setImagePath("asd97a9s8d89asd.jpg")
      .build();

    playgroundDAO.createPlayground(playground);
    PlaygroundDTO fetchedPlayground = playgroundDAO.getPlayground(playground.getName());

    // update values
    fetchedPlayground.setStreetName("Sohoj");
    fetchedPlayground.setStreetNumber(12);
    fetchedPlayground.setZipCode(1223);
    fetchedPlayground.setCommune("Ballerup");
    playgroundDAO.updatePlayground(fetchedPlayground);

    // check that playground has updated values
    PlaygroundDTO updatedPlayground = playgroundDAO.getPlayground(fetchedPlayground.getName());
    Assertions.assertAll(
      () -> assertEquals("Sohoj", updatedPlayground.getStreetName()),
      () -> assertEquals(12, updatedPlayground.getStreetNumber()),
      () -> assertEquals(1223, updatedPlayground.getZipCode()),
      () -> assertEquals("Ballerup", updatedPlayground.getCommune())
    );

    playgroundDAO.deletePlayground(updatedPlayground.getName());
  }

  @Test
  void deleteAllPlaygroundsInCollection() throws NoModificationException {
    PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegepladsen i Fælledparken")
      .setCommune("København Ø")
      .setZipCode(2100)
      .setStreetName("Gunnar Nu Hansens Plads")
      .setStreetNumber(10)
      .setImagePath("https://www.google.com/imgres?imgurl=https%3A%2F%2Fberlingske.bmcdn.dk%2Fmedia%2Fcache%2Fresolve%2Fembedded_image_600x%2Fimage%2F29%2F297771%2F17762859-vandlegepladsen1.jpg&imgrefurl=https%3A%2F%2Fwww.berlingske.dk%2Fdet-gode-liv%2Fsommerferie-med-boern-her-er-6-af-de-bedste-legepladser-i-koebenhavn&tbnid=8KS7AmfvvL2R9M&vet=12ahUKEwip7qn96qnoAhXTNuwKHWZvBakQMygAegUIARDKAQ..i&docid=NnIEE3O_4_SjKM&w=600&h=400&q=Vandlegepladsen&ved=2ahUKEwip7qn96qnoAhXTNuwKHWZvBakQMygAegUIARDKAQ")
      .build();

    PlaygroundDTO playground2 = new PlaygroundDTO.Builder("Naturlegepladsen i Valbyparken")
      .setCommune("København SV")
      .setZipCode(2450)
      .setStreetName("Hammelstrupvej")
      .setStreetNumber(41)
      .setImagePath("https://www.kk.dk/sites/default/files/styles/flexslider_full/public/uploaded-images/naturlegepladsen_i_valbyparken_800x500.jpg?itok=8vuJum3Z")
      .build();


    WriteResult wr = playgroundDAO.createPlayground(playground);
    WriteResult wr2 = playgroundDAO.createPlayground(playground2);

    Assertions.assertAll(
      () -> Assertions.assertNotNull(playgroundDAO.getPlayground(playground.getName())),
      () -> Assertions.assertNotNull(playgroundDAO.getPlayground(playground2.getName()))
    );

    playgroundDAO.deleteAllPlaygrounds();
    Assertions.assertThrows(NoSuchElementException.class, () -> playgroundDAO.getPlaygroundList());
  }

  @Test
  void nullInCreateShouldThrowIllegalArgument() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> playgroundDAO.createPlayground(null));
  }

  @Test
  void nullInGetShouldThrowIlleArgument() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> playgroundDAO.getPlayground(null));
  }

  @Test
  void emptyIdInGetShouldThrowIlleArgument() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> playgroundDAO.getPlayground(""));
  }

  @Test
  void noEventsInGetEventsShouldThrowNoSuchElements() {
    Assertions.assertThrows(NoSuchElementException.class, () -> playgroundDAO.getPlaygroundList());
  }

  @Test
  void nullInUpdateShouldThrowIlleArgument() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> playgroundDAO.updatePlayground(null));
  }

  @Test
  void nullInDeleteShouldThrowIlleArgument() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> playgroundDAO.deletePlayground(null));
  }

  @Test
  void emptyIdInDeleteShouldThrowIlleArgument() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> playgroundDAO.deletePlayground(""));
  }
}
