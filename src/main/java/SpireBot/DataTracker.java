package SpireBot;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class DataTracker {
    private HashMap<String, String> data = new HashMap<>();
    private static final String filename = "spirebot.json";

    public DataTracker() {
    }

    // Returns true if data is new
    public boolean set(String key, String value) {
        if (data.containsKey(key) && data.get(key) == value) {
            return false;
        }

        data.put(key, value);
        return true;
    }

    public void setAndWriteIfNew(String key, String value) {
        if (set(key, value)) {
            writeToFile();
        }
    }

    private JsonObject toJsonObj() {
        JsonObject obj = new JsonObject();

        for (String key : data.keySet()) {
            String val = data.get(key);
            obj.addProperty(key, val);
        }

        return obj;
    }

    private String asJSONString() {
        return toJsonObj().toString();
    }

    public void writeToFile() {
        System.out.println("OJB: Data Tracker is writing to file: " + filename);

        try {
            File file = new File(filename);
            file.createNewFile();

            FileWriter fw = new FileWriter(file);
            fw.write(asJSONString());

            System.out.println(asJSONString());

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
