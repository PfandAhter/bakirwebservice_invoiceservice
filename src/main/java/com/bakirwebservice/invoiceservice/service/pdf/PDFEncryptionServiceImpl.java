package com.bakirwebservice.invoiceservice.service.pdf;

import com.bakirwebservice.invoiceservice.exceptions.EncryptionException;
import com.bakirwebservice.invoiceservice.service.IPDFEncryptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PDFEncryptionServiceImpl implements IPDFEncryptionService {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final int ITERATION_COUNT = 65536;

    private static final int KEY_LENGTH = 256;

    private static final int SALT_LENGTH = 32;

    private static final int IV_LENGTH = 8;

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";


    @Value("${encryption.pepper}")
    private String pepper;

    public record EncryptedData(byte[] data, String salt){
    }

    @Override
    public EncryptedData encryptPDF (byte[] pdfContent, String password) throws EncryptionException{
        validatePassword(password);

        try{
            byte[] salt = generateSalt();
            SecretKey key = deriveKey(password, salt);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE,key);

            byte[] iv = cipher.getIV();
            byte[] encryptedContent = cipher.doFinal(pdfContent);
            ByteBuffer byteBuffer = ByteBuffer.allocate(IV_LENGTH + encryptedContent.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedContent);

            return new EncryptedData (byteBuffer.array(), Base64.getEncoder().encodeToString(salt));
        }catch (Exception e){
            log.error("Error while encrypting PDF", e);
            throw new EncryptionException("Error while encrypting PDF", e);
        }
    }

    @Override
    public byte[] decryptPDF(byte[] encryptedData,String password, String saltString) throws EncryptionException{
        validatePassword(password);

        try{
            byte[] salt = Base64.getDecoder().decode(saltString);
            SecretKey key = deriveKey(password, salt);

            if(encryptedData.length < IV_LENGTH){
                throw new EncryptionException("Invalid encrypted data format");
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
            byte[] iv = new byte[IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encryptedContent = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedContent);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            return cipher.doFinal(encryptedContent);
        }catch (Exception e){
            log.error("Error while decrypting PDF", e);
            throw new EncryptionException("Error while decrypting PDF", e);
        }
    }


    private SecretKey deriveKey (String password, byte[] salt) throws Exception{
        String pepperedPassword = password + pepper;

        PBEKeySpec spec = new PBEKeySpec(
                pepperedPassword.toCharArray(),
                salt,
                ITERATION_COUNT,
                KEY_LENGTH
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(keyBytes, "AES");
    }

    private byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        return salt;
    }

    private void validatePassword (String password) throws EncryptionException {
        if(password == null || password.isEmpty()){
            throw new EncryptionException();
        }

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        if (!pattern.matcher(password).matches()) {
            throw new EncryptionException(
                    "Password must contain at least 8 characters, including uppercase, " +
                            "lowercase, numbers and special characters"
            );
        }
    }


}