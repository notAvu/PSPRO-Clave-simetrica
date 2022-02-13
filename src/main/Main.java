package main;

import FileManager.EncryptionFileManager;
import FileManager.KeyFileManager;
import operators.Encrypter;
import operators.CustomKeyGenerator;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.util.Scanner;

public class Main {
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
        String algorithm= readAlgorithm();
        String decryptedFileName = encryptedFileName + ".decrypt";
        decryptManager = new EncryptionFileManager(decryptedFileName);
        byte[] input = encryptManager.readAllBytes();
        SecretKey privateKey = new SecretKeySpec(keyManager.readKey(), algorithm);
        byte[] decrypted =new Encrypter(privateKey, algorithm).decryptInput(input);
        decryptManager.writeAllBytes(decrypted);
    }

    private static void encryptFile(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero que contiene la clave");
        String keyFileName = sc.next();
        keyManager = new KeyFileManager(keyFileName);
        System.out.println("Introduzca el nombre del fichero que desea encriptar");
        String decryptedFileName = sc.next();
        decryptManager = new EncryptionFileManager(decryptedFileName);
        String algorithm= readAlgorithm();
        String encryptedFileName = decryptedFileName+"."+algorithm;
        encryptManager = new EncryptionFileManager(encryptedFileName);
        byte[] input = decryptManager.readAllBytes();
        SecretKey privateKey = new SecretKeySpec(keyManager.readKey(), algorithm);
        encryptManager.writeAllBytes(new Encrypter(privateKey, algorithm).encryptInput(input));
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
        String algorithm= readAlgorithm();
        keyManager.writeKey(algorithm, CustomKeyGenerator.generateKey(algorithm));
    }

    private static String readAlgorithm() {
        String algorithm="";
        try (BufferedReader is= new BufferedReader(new FileReader(keyManager.getKeyFile().getName()+".algor"))){
            algorithm=is.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return algorithm;
    }
    private static void writeAlgorithm(String algorithm) {
        try(BufferedWriter os= new BufferedWriter(new FileWriter(keyManager.getKeyFile().getName()+".algor")))
        {
            os.write(algorithm);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }
//    private static IvParameterSpec getIvParameterSpec() {
//        SecureRandom random;
//        byte[] bytes = new byte[16];
//        IvParameterSpec ivSpec = null;
//        try {
//            random = SecureRandom.getInstance("SHA1PRNG");
//            random.nextBytes(bytes);
//            ivSpec = new IvParameterSpec(bytes);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return ivSpec;
//    }
}
