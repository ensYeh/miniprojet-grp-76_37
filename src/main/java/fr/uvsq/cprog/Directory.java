package fr.uvsq.cprog;

import java.util.ArrayList;
import java.util.List;

public class Directory extends FileElement {
    private final List<FileElement> elements;

    public Directory(int NER, String name, String path) {
        super(NER, name, "directory", path);
        this.elements = new ArrayList<>();
    }
    public List<FileElement> getElements() {
        return elements;
    }
    public void addElement(FileElement element) {
        elements.add(element);
    }
}
