package FileManager;

import java.io.*;

public class KeyFileManager {
    private File keyFile;
    private FileOutputStream keyWriter;
    private FileInputStream keyReader;
    public KeyFileManager(String fileName)
    {
        keyFile=new File(fileName);
        iniWriter();
        iniReader();
    }

    private void iniReader() {
        try {
            keyReader= new FileInputStream(keyFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void iniWriter() {
        try {
            keyWriter= new FileOutputStream(keyFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void WriteBytes(byte[] inputBytes) {
        iniWriter();
        try {
            keyWriter.write(inputBytes);
            keyWriter.flush();
            keyWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] ReadFileBytes() {
        byte[] readBytes= new byte[0];
        iniReader();
        try {
            readBytes= keyReader.readAllBytes();
            keyReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBytes;
    }
}
