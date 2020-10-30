package SpireBot.utils;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Credentials {
    public String oauth, username, channel;

    public Credentials(String oauth, String username, String channel) {
        this.oauth = oauth;
        this.username = username;
        this.channel = channel;
    }

    public static Credentials loadFromJSON(String filename) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filename));
            Gson gson = new Gson();
            return gson.fromJson(reader, Credentials.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
