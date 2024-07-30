package com.example.draft1;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class OneWayHashing {

    public String hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }
    /*
    public static void main(String[] args) {
        try {
            String input = "Hello, World!";
            String hashedValue = hash(input);

            System.out.println("Input: " + input);
            System.out.println("Hashed: " + hashedValue);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
     */
}
