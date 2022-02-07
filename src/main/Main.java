package main;

import FileManager.EncryptionFileManager;
import FileManager.KeyFileManager;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class Main {
//    private static String algorithm = "AES";
    public static File keyAlgorithmFile;
    public static KeyFileManager keyManager;
    public static EncryptionFileManager decryptManager;
    public static EncryptionFileManager encryptManager;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String respuesta = "4";
        while (!respuesta.equals("0")) {
            System.out.println("""
                    Seleccione la operacion que desea realizar
                    1.Seleccionar encriptacion
                    2.Encriptar fichero
                    3.Desencriptar fichero
                    0.Salir
                    """);
            respuesta = sc.next();
            switch (respuesta) {
                case "1" -> selectKey(sc);
                case "2" -> encryptFile(sc);
                case "3" -> decryptFile(sc);
            }

        }
    }

    private static void decryptFile(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero que contiene la clave");
        String keyFileName = sc.next();
        keyManager = new KeyFileManager(keyFileName);
        System.out.println("Introduzca el nombre del fichero que desea desencriptar");
        String encryptedFileName = sc.next();
        encryptManager = new EncryptionFileManager(encryptedFileName);
        System.out.println("Introduzca el nombre del fichero que contendra el resultado");
        String decryptedFileName = sc.next();
        decryptManager = new EncryptionFileManager(decryptedFileName);
        byte[] input = encryptManager.readAllBytes();
        Cipher cipher = getCipher();
//        IvParameterSpec ivParameterSpec = getIvParameterSpec();
//        generateKey(algorithm);
        SecretKey privateKey = new SecretKeySpec(keyManager.readKey(), getAlgorithm());
        byte[] decrypted = decryptInput(input, privateKey, cipher);
        decryptManager.writeAllBytes(decrypted);
    }

    private static String getAlgorithm() {
        String algorithm="";
        try (BufferedReader is= new BufferedReader(new FileReader(keyAlgorithmFile))){
            algorithm=is.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return algorithm;
    }

    private static void encryptFile(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero que contiene la clave");
        String keyFileName = sc.next();
        keyManager = new KeyFileManager(keyFileName);
        System.out.println("Introduzca el nombre del fichero que desea encriptar");
        String decryptedFileName = sc.next();
        decryptManager = new EncryptionFileManager(decryptedFileName);
        System.out.println("Introduzca el nombre del fichero que contendra el resultado");
        String encryptedFileName = sc.next();
        encryptManager = new EncryptionFileManager(encryptedFileName);
        byte[] input = decryptManager.readAllBytes();
        Cipher cipher = getCipher();
//        IvParameterSpec ivParameterSpec = getIvParameterSpec();
//        generateKey(algorithm);
        SecretKey privateKey = new SecretKeySpec(keyManager.readKey(), getAlgorithm());
        byte[] encrypted = encryptInput(input, privateKey, cipher);
        encryptManager.writeAllBytes(encrypted);
    }

    private static void selectKey(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero en el que desea generar la clave");
        String fileName = sc.next();
        keyManager = new KeyFileManager(fileName);
        System.out.println("""
                Seleccione el algoritmo para generar la clave
                1.AES
                2.DES
                3.DESede
                """);
        String choice = sc.next();
        switch (choice) {
            case "2" -> writeAlgorithm("DES");
            case "3" -> writeAlgorithm("DESede");
            default -> writeAlgorithm("AES");
        }
        generateKey(getAlgorithm());
    }

    private static void writeAlgorithm(String algorithm) {
        try(BufferedWriter os= new BufferedWriter(new FileWriter(keyAlgorithmFile)))
        {
            os.write(algorithm);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static Cipher getCipher() {
        Cipher cipher = null;
        getIvParameterSpec();
        String algorithm=getAlgorithm();
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

    private static IvParameterSpec getIvParameterSpec() {
        SecureRandom random;
        byte[] bytes = new byte[16];
        IvParameterSpec ivSpec = null;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(bytes);
            ivSpec = new IvParameterSpec(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ivSpec;
    }

    private static void generateKey(String algorithm) {
        SecretKey key;
        try {
            KeyGenerator genClaves = KeyGenerator.getInstance(algorithm);
            SecureRandom srand = SecureRandom.getInstance("SHA1PRNG");
            genClaves.init(srand);
            key = genClaves.generateKey();
            writeKey(algorithm, key);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void writeKey(String algorithm, SecretKey key) {
        switch (algorithm) {
            case "DESede" -> {
                try {
                    SecretKeyFactory keySpecFactory = SecretKeyFactory.getInstance(algorithm);
                    DESedeKeySpec keySpec = (DESedeKeySpec) keySpecFactory.getKeySpec(key, DESedeKeySpec.class);
                    keyManager.writeByteKey(keySpec.getKey());
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            case "DES" -> {
                try {

                    SecretKeyFactory keySpecFactory = SecretKeyFactory.getInstance(algorithm);
                    DESKeySpec keySpec = (DESKeySpec) keySpecFactory.getKeySpec(key, DESKeySpec.class);
                    keyManager.writeByteKey(keySpec.getKey());
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            case "AES" -> {
                SecretKeySpec keySpec= new SecretKeySpec(key.getEncoded(), algorithm);
//                    PBEKeySpec keyspec = (PBEKeySpec) keySpec.getKeySpec(key, PBEKeySpec.class);
                keyManager.writeByteKey(keySpec.getEncoded());
            }
        }
    }

    private static byte[] decryptInput(byte[] input, SecretKey privateKey, Cipher cipher) {
        byte[] decryptedOutput = new byte[0];
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptedOutput = cipher.doFinal(input);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return decryptedOutput;
    }

    private static byte[] encryptInput(byte[] input, SecretKey privateKey, Cipher cipher) {
        byte[] encryptedOutput = new byte[cipher.getBlockSize()];
        try {
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            encryptedOutput = cipher.doFinal(input);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedOutput;
    }

}
