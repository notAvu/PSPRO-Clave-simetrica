package operators;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyGenerator {
    public static SecretKey generateKey(String algorithm) {
        SecretKey key = null;
        try {
            javax.crypto.KeyGenerator genClaves = javax.crypto.KeyGenerator.getInstance(algorithm);
            SecureRandom srand = SecureRandom.getInstance("SHA1PRNG");
            genClaves.init(srand);
            key = genClaves.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }
}
