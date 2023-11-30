package fr.uvsq.cprog;

import java.io.Serializable;

public class FileElement implements Serializable {
    private final int NER;
    private final String name;
    private String type;
    private String path;
    private boolean isDirectory;

    public FileElement(int NER, String name, String type, String path) {
        this.NER = NER;
        this.name = name;
        this.type = type;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public int getNER() {
        return NER;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getPath() {
        return path;
    }
}
