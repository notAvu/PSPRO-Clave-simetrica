package main;

import FileManager.EncryptionFileManager;
import FileManager.KeyFileManager;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Scanner;

public class Main {
    private static String algorithm="AES";
    public static KeyFileManager keyManager;
    public static EncryptionFileManager decryptManager;
    public static EncryptionFileManager encryptManager;
        public static void main(String[] args) {
            Scanner sc= new Scanner(System.in);
            String respuesta="4";
            while(!respuesta.equals("0")) {
                System.out.println("""
                Seleccione la operacion que desea realizar
                1.Seleccionar encriptacion
                2.Encriptar fichero
                3.Desencriptar fichero
                0.Salir
                """);
                respuesta=sc.next();
                switch (respuesta) {
                    case "1"-> selectKey(sc);
                    case "2"-> encryptFile(sc);
                    case "3"-> decryptFile(sc);
                }

            }
    }

    private static void decryptFile(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero que contiene la clave");
        String keyFileName=sc.next();
        keyManager= new KeyFileManager(keyFileName);
        System.out.println("Introduzca el nombre del fichero que desea desencriptar");
        String encryptedFileName=sc.next();
        encryptManager=new EncryptionFileManager(encryptedFileName);
        System.out.println("Introduzca el nombre del fichero que contendra el resultado");
        String decryptedFileName=sc.next();
        decryptManager= new EncryptionFileManager(decryptedFileName);
        byte[] input=encryptManager.readAllBytes();
        Cipher cipher = getCipher();
        IvParameterSpec ivParameterSpec= getIvParameterSpec();
        generateKey(algorithm);
        SecretKey privateKey = new SecretKeySpec(keyManager.ReadFileBytes(),algorithm);
        byte[] decrypted =decryptInput(input, privateKey, cipher, ivParameterSpec);
        decryptManager.writeAllBytes(decrypted);
    }

    private static void encryptFile(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero que contiene la clave");
        String keyFileName=sc.next();
        keyManager= new KeyFileManager(keyFileName);
        System.out.println("Introduzca el nombre del fichero que desea encriptar");
        String decryptedFileName=sc.next();
        decryptManager= new EncryptionFileManager(decryptedFileName);
        System.out.println("Introduzca el nombre del fichero que contendra el resultado");
        String encryptedFileName=sc.next();
        encryptManager=new EncryptionFileManager(encryptedFileName);
        byte[] input=decryptManager.readAllBytes();
        Cipher cipher = getCipher();
        IvParameterSpec ivParameterSpec= getIvParameterSpec();
        generateKey(algorithm);
        SecretKey privateKey = new SecretKeySpec(keyManager.ReadFileBytes(),algorithm);
        byte[] encrypted =encryptInput(input, privateKey, cipher, ivParameterSpec);
        encryptManager.writeAllBytes(encrypted);
    }

    private static void selectKey(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero en el que desea generar la clave");
        String fileName= sc.next();
        keyManager=new KeyFileManager(fileName);
        System.out.println("""
                Seleccione el algoritmo para generar la clave
                1.AES
                2.DES
                3.DESede
                """);
        String choice= sc.next();
        switch (choice) {
            case "2"->algorithm="DES";
            case "3"->algorithm="DESede";
            default -> algorithm="AES";
        }
        generateKey(algorithm);
    }

    private static Cipher getCipher() {
        Cipher cipher=null;
        getIvParameterSpec();
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
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
    private static void generateKey(String algorithm) {
        KeyGenerator generator;
        SecretKey key=null;
        SecretKeyFactory keyFactory=null;
        try {
            keyFactory= SecretKeyFactory.getInstance(algorithm);
            generator= KeyGenerator.getInstance(algorithm);
            generator.init(192);//TODO Check for AES, DES & DESede
            key=generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        SecretKey clave = genClaves.generateKey();
//
        try {
            PBEKeySpec keySpec = (PBEKeySpec) keyFactory.getKeySpec(key, PBEKeySpec.class);
            keyManager.WriteBytes(keySpec.getSalt());
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
//
//        byte[] valorClave = keySpec.getKey();
    }

    private static byte[] decryptInput(byte[] input, SecretKey privateKey, Cipher cipher, IvParameterSpec ivParameterSpec) {
        byte[] decryptedOutput = new byte[0];
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey, ivParameterSpec);
            decryptedOutput= cipher.doFinal(input);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decryptedOutput;
    }

    private static byte[] encryptInput(byte[] input, SecretKey privateKey, Cipher cipher, IvParameterSpec ivParameterSpec) {
        byte[] encryptedOutput = new byte[cipher.getBlockSize()];
        try {
            cipher.init(Cipher.ENCRYPT_MODE, privateKey, ivParameterSpec);
            encryptedOutput=cipher.doFinal(input);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedOutput;
    }

}
