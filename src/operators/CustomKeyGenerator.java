package operators;

import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CustomKeyGenerator {
    public static SecretKey generateKey(String algorithm) {
        SecretKey key = null;
        try {
            KeyGenerator genClaves =KeyGenerator.getInstance(algorithm);
            SecureRandom srand = SecureRandom.getInstance("SHA1PRNG");
            genClaves.init(srand);
            key = genClaves.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }
}
