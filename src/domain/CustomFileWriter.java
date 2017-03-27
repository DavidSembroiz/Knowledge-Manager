package domain;

import java.io.FileWriter;
import java.io.IOException;


public class CustomFileWriter {

    private FileWriter writer;

    public CustomFileWriter(String file) {
        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String s) {
        try {
            s = s.replace(".", ",");
            writer.write(s + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
