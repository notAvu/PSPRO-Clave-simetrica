package FileManager;

import java.io.*;

public class EncryptionFileManager {
    private File file;
    private FileInputStream fis ;
    private FileOutputStream fos ;
    private BufferedInputStream is ;
    private BufferedOutputStream os ;
    public EncryptionFileManager(String fileName)
    {
        file=new File(fileName);
    }
    private void iniStreams() {
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file);
            is = new BufferedInputStream(fis);
            os = new BufferedOutputStream(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void closeStreams()
    {
        try {
            os.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public byte[] readAllBytes()
    {
        byte[] readBytes=null;
        iniStreams();
        try {
            readBytes=is.readAllBytes();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        closeStreams();
        return readBytes;
    }
    public void writeAllBytes(byte[] bytes)
    {
        iniStreams();
        try {
            os.write(bytes);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        closeStreams();
//        byte[] buff = new byte[cifrado.getBlockSize()];
//        while(is.read(bytes) != -1) {
//            os.write(cifrado.update(buff));
//        }
//        os.write(cifrado.doFinal());
    }
}
