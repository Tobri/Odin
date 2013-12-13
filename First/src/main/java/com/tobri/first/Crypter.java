package com.tobri.first;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mkn on 13.12.13.
 */
public class Crypter {
    protected final static String ALGORITHM = "SHA-512";
    protected MessageDigest messageDigest;

    public Crypter() {
        try {
            this.messageDigest = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException nsae) { }
    }

    public String cryptPassword(String password) {
        this.messageDigest.update(password.getBytes(), 0, password.length());
        String tmp  = toHex(this.messageDigest.digest());
        String salt = tmp.substring(0, this.messageDigest.getDigestLength() / 2);
        tmp         = mergeSalt(tmp, salt);
        this.messageDigest.reset();
        this.messageDigest.update(tmp.getBytes(), 0, tmp.length());

        /* Overwrite variables */
        tmp = "0000000000000000000000000000000000000000000000000000000000000000" +
              "0000000000000000000000000000000000000000000000000000000000000000";
        salt = tmp;
        tmp  = salt;

        return toHex(this.messageDigest.digest());
    }

    private String toHex(byte[] digest) {
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < digest.length; i++)
        {
            int halfbyte = (digest[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = digest[i] & 0x0F;
            }
            while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    private String mergeSalt(String enc, String salt) {
        int i;
        StringBuffer sb = new StringBuffer();

        for (i = 0; i < salt.length(); i++) {
            sb.append(enc.charAt(i) ^ salt.charAt(i));
        }

        sb.append(enc.substring(i, enc.length()));

        return sb.toString();
    }
}
