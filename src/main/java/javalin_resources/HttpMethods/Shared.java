package javalin_resources.HttpMethods;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Shared {

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
}
