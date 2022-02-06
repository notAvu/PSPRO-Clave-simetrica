package FileManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
    private byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    public void writeByteKey(byte[] inputBytes) {
        try {
            iniWriter();
            keyWriter.write(inputBytes);
            keyWriter.flush();
            keyWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeCharKey(char[] inputBytes) {
        try {
            iniWriter();
            keyWriter.write(toBytes(inputBytes));
            keyWriter.flush();
            keyWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] readKey() {
        byte[] readBytes= new byte[0];
        try {
            iniReader();
            readBytes= keyReader.readAllBytes();
            keyReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readBytes;
    }
}
