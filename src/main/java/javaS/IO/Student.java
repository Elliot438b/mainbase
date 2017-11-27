package javaS.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        raf.writeUTF(name);// 采用UTF的编码方式写入字符串
        raf.writeInt(age);
        raf.writeDouble(height);
    }

    /**
     * 要严格按照写入顺序读取，这也是ORM的意义
     * 
     * @param raf
     * @throws IOException
     */
    public void read(RandomAccessFile raf) throws IOException {
        this.id = raf.readLong();
        this.name = raf.readUTF();
        this.age = raf.readInt();
        this.height = raf.readDouble();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id:");
        sb.append(this.id);
        sb.append(" ");
        sb.append("name:");
        sb.append(this.name);
        sb.append(" ");
        sb.append("age:");
        sb.append(this.age);
        sb.append(" ");
        sb.append("height:");
        sb.append(this.height);
        return sb.toString();
    }

    /**
     * 在RandomAccessFile指定位置插入数据，先将位置后面的数据放入缓冲区，插入数据以后再将其写回来。
     * 
     * @param file
     *            能找到该文件的路径，是字符串类型
     * @param position
     *            其实外部调用的时候能找到这个位置比较难，因为不确定数据长度是多少，弄不好就会将数据拆分引起混乱。
     * @param content
     */
    public void insert(String file, long position, Student s) throws IOException {
        /**
         * 创建一个临时文件
         * 
         * 在使用完以后就将其删除
         */
        File tempFile = File.createTempFile("temp", null);
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream("temp");
        /**
         * 将插入位置后面的数据缓存到临时文件
         */
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(position);
        byte[] buffer = new byte[20];
        while (raf.read(buffer) > -1) {
            fos.write(buffer);
        }
        raf.seek(position);
        /**
         * 向RandomAccessFile写入插入内容
         */
        s.write(raf);
        /**
         * 从临时文件中写回缓存数据到RandomAccessFile
         */
        FileInputStream fis = new FileInputStream("temp");
        while (fis.read(buffer) > -1) {
            raf.write(buffer);
        }
        fos.close();
        fis.close();
        raf.close();
    }

}
