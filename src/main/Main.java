package main;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.util.Scanner;

public class Main {
    private static final String ALGORITMO="AES";
    public static void main(String[] args) {
        Scanner sc= new Scanner(System.in);
        System.out.println("Introduzca el nombre del fichero en el que desea generar la clave");
        System.out.println("""
                Seleccione el algoritmo para generar la clave
                1.AES
                2.DES
                3.DESede
                """);
        String text="";
        byte[] input= text.getBytes();
        Cipher cipher = getCipher();
        IvParameterSpec ivParameterSpec= getIvParameterSpec();
        Key privateKey = generateKey("AES");
        byte[] encryptedOutput=encryptInput(input, privateKey, cipher, ivParameterSpec);
        byte[] decryptedOutput=decryptOutput(input, privateKey, cipher, ivParameterSpec);
    }

    private static Cipher getCipher() {
        Cipher cipher=null;
        getIvParameterSpec();
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return cipher;
    }

    private static IvParameterSpec getIvParameterSpec() {
        SecureRandom random;
        byte[] bytes= new byte[16];
        IvParameterSpec ivSpec = null;
        try {
            random= SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(bytes);
            ivSpec=new IvParameterSpec(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ivSpec;
    }
    private static Key generateKey(String algorithm) {
        KeyGenerator generator;
        Key key=null;
        try {
            generator= KeyGenerator.getInstance(algorithm);
            generator.init(192);
            key=generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }

    private static byte[] decryptOutput(byte[] input, Key privateKey, Cipher cipher, IvParameterSpec ivParameterSpec) {
        byte[] decryptedOutput = new byte[0];
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey, ivParameterSpec);
            decryptedOutput= cipher.doFinal(input);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decryptedOutput;
    }

    private static byte[] encryptInput(byte[] input, Key privateKey, Cipher cipher, IvParameterSpec ivParameterSpec) {
        byte[] encryptedOutput = new byte[0];
        try {
            cipher.init(Cipher.ENCRYPT_MODE, privateKey, ivParameterSpec);
            encryptedOutput=cipher.doFinal(input);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedOutput;
    }

}
