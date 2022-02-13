package FileManager;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class KeyFileManager {
    private File keyFile;

    public File getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(File keyFile) {
        this.keyFile = keyFile;
    }

    public KeyFileManager(String fileName) {
                keyFile = new File(fileName);
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
        try (FileOutputStream keywriter= new FileOutputStream(keyFile)){
            keywriter.write(inputBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeKey(String algorithm, SecretKey key) {
        switch (algorithm) {
            case "DESede" -> {
                try {
                    SecretKeyFactory keySpecFactory = SecretKeyFactory.getInstance(algorithm);
                    DESedeKeySpec keySpec = (DESedeKeySpec) keySpecFactory.getKeySpec(key, DESedeKeySpec.class);
                    writeByteKey(keySpec.getKey());
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            case "DES" -> {
                try {
                    SecretKeyFactory keySpecFactory = SecretKeyFactory.getInstance(algorithm);
                    DESKeySpec keySpec = (DESKeySpec) keySpecFactory.getKeySpec(key, DESKeySpec.class);
                    writeByteKey(keySpec.getKey());
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            case "AES" -> {
                SecretKeySpec keySpec= new SecretKeySpec(key.getEncoded(), algorithm);
//                    PBEKeySpec keyspec = (PBEKeySpec) keySpec.getKeySpec(key, PBEKeySpec.class);
                writeByteKey(keySpec.getEncoded());
            }
        }
    }
    public byte[] readKey() {
        byte[] readBytes ;
        try (FileInputStream keyReader= new FileInputStream(keyFile)){
//            iniReader();
            readBytes = keyReader.readAllBytes();
        } catch (IOException e) {
            readBytes=new byte[0];
            e.printStackTrace();
        }
        return readBytes;
    }
}
