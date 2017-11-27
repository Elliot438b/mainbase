package javaS.IO;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Student {
    private long id;
    private String name;
    private int age;
    private double height;

    public Student() {
    }

    public Student(long id, String name, int age, double height) {
        super();
        this.id = id;
        this.name = name;
        this.age = age;
        this.height = height;
    }

    public void write(RandomAccessFile raf) throws IOException {
        raf.writeLong(id);
        raf.writeBytes(name);
        raf.writeInt(age);
        raf.writeDouble(height);
    }
}
