package main;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.*;
import java.util.Scanner;

//try {
//        FileWriter keyFw= new FileWriter(keyFile);
//        keyFw.write();
//        } catch (IOException exception) {
//        exception.printStackTrace();
//        }
public class Main {
    public static File keyFile;
    public static FileWriter keyFw;
    public static BufferedReader keyFr;
    public static File encryptedFile;
    public static FileOutputStream encryptedFw;
    public static FileInputStream encryptedFr;
    public static File decryptedFile;
    public static FileOutputStream decryptedFw;
    public static FileInputStream decryptedFr;
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
            case "1"->getAlgorithm(sc);
            case "2"->{
                try {
                    decryptedFw.write(encryptFile(sc));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                try {
                    decryptedFw.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            case "3"->{
                try {
                    encryptedFw.write(decryptFile(sc));
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                try {
                    encryptedFr.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }

        }
    }

    private static String readKey() {
        String algorithm=null;
        try {
            keyFr= new BufferedReader(new FileReader(keyFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            algorithm = keyFr.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return algorithm;
    }
    private static byte[] encryptFile(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero que contiene la clave");
        String keyFileName=sc.next();
        generateKeyFile(keyFileName);
        System.out.println("Introduzca el nombre del fichero que desea encriptar");
        String decryptedFileName=sc.next();
        generateDecryptFile(decryptedFileName);
        System.out.println("Introduzca el nombre del fichero que contendra el resultado");
        String encryptedFileName=sc.next();
        generateEncryptedFile(encryptedFileName);
//        String text=readFile(decryptedFr).;
        byte[] input= new byte[0];
        try {
            input = decryptedFr.readAllBytes();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        Cipher cipher = getCipher();
        IvParameterSpec ivParameterSpec= getIvParameterSpec();
        Key privateKey = generateKey(readKey());
        closeKeyReader();
        return encryptInput(input, privateKey, cipher, ivParameterSpec);

    }
    private static byte[] decryptFile(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero que contiene la clave");
        String keyFileName=sc.next();
        generateKeyFile(keyFileName);
        System.out.println("Introduzca el nombre del fichero que desea desencriptar");
        String encryptedFileName=sc.next();
        generateEncryptedFile(encryptedFileName);
        System.out.println("Introduzca el nombre del fichero que contendra el resultado");
        String decryptedFileName=sc.next();
        generateDecryptFile(decryptedFileName);

        byte[] input= new byte[0];
        try {
            input = encryptedFr.readAllBytes();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        Cipher cipher = getCipher();
        IvParameterSpec ivParameterSpec= getIvParameterSpec();
        Key privateKey = generateKey(readKey());
        closeKeyReader();
        return decryptOutput(input, privateKey, cipher, ivParameterSpec);

    }

    private static void closeKeyReader() {
        try {
            keyFr.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    //    private static void generateWriteReadFile(String keyFileName, File file, FileWriter fw, BufferedReader read) {
//        file= new File(keyFileName);
//        try {
//            fw= new FileWriter(file);
//        } catch (IOException exception) {
//            exception.printStackTrace();
//        }
//        try {
//            read= new BufferedReader(new FileReader(keyFile));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
    private static void generateKeyFile(String keyFileName) {
        keyFile= new File(keyFileName);
        try {
            keyFw= new FileWriter(keyFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            keyFr= new BufferedReader(new FileReader(keyFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void generateDecryptFile(String fileName) {
//        if()
        decryptedFile= new File(fileName);
        try {
            decryptedFw= new FileOutputStream(decryptedFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            decryptedFr= new FileInputStream(decryptedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void generateEncryptedFile(String fileName) {
        encryptedFile= new File(fileName);
        try {
            encryptedFw= new FileOutputStream(encryptedFile);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            encryptedFr= new FileInputStream(encryptedFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(BufferedReader reader)
    {
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            line = reader.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        while (line != null) {
            sb.append(line);
            try {
                line = reader.readLine();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return sb.toString();
    }
    private static void getAlgorithm(Scanner sc) {
        System.out.println("Introduzca el nombre del fichero en el que desea generar la clave");
        String fileName= sc.next();
        keyFile= new File(fileName);
        System.out.println("""
                Seleccione el algoritmo para generar la clave
                1.AES
                2.DES
                3.DESede
                """);
        String algorithm;
        String choice= sc.next();
        switch (choice) {
            case "2"->algorithm="DES";
            case "3"->algorithm="DESede";
            default -> algorithm="AES";
        }
        try {
            keyFw= new FileWriter(keyFile, false);
            keyFr= new BufferedReader(new FileReader(keyFile));
            keyFw.write(algorithm);
            keyFw.flush();
            keyFw.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
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
