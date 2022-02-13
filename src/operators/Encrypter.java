package operators;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Encrypter {
    Cipher cipher;
    SecretKey privateKey;
    public Encrypter(SecretKey privateKey, String algorithm)
    {
        this.cipher= createCipher(algorithm);
        this.privateKey=privateKey;
    }
    public byte[] encryptInput(byte[] input) {
        byte[] encryptedOutput = new byte[cipher.getBlockSize()];
        try {
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            encryptedOutput = cipher.doFinal(input);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedOutput;
    }
    public byte[] decryptInput(byte[] input) {
        byte[] encryptedOutput = new byte[cipher.getBlockSize()];
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            encryptedOutput = cipher.doFinal(input);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedOutput;
    }
    private static Cipher createCipher(String algorithm) {
        Cipher cipher = null;
        try {
            if (algorithm.equals("AES"))
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            else
                cipher=Cipher.getInstance(algorithm);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return cipher;
    }
}
