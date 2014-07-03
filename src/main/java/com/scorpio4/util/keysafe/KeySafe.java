package com.scorpio4.util.keysafe;
/*
 *   Scorpio4 - CONFIDENTIAL
 *   Unpublished Copyright (c) 2009-2014 Lee Curtis, All Rights Reserved.
 *
 *   NOTICE:  All information contained herein is, and remains the property of Lee Curtis. The intellectual and technical concepts contained
 *   herein are proprietary to Lee Curtis and may be covered by Australian, U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law.
 *   Dissemination of this information or reproduction of this material is strictly forbidden unless prior written permission is obtained
 *   from Lee Curtis.  Access to the source code contained herein is hereby forbidden to anyone except current Lee Curtis employees, managers or contractors who have executed
 *   Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 *   The copyright notice above does not evidence any actual or intended publication or disclosure  of  this source code, which includes
 *   information that is confidential and/or proprietary, and is a trade secret, of Lee Curtis.   ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC  PERFORMANCE,
 *   OR PUBLIC DISPLAY OF OR THROUGH USE  OF THIS  SOURCE CODE  WITHOUT  THE EXPRESS WRITTEN CONSENT OF LEE CURTIS IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE
 *   LAWS AND INTERNATIONAL TREATIES.  THE RECEIPT OR POSSESSION OF  THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR IMPLY ANY RIGHTS
 *   TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT  MAY DESCRIBE, IN WHOLE OR IN PART.
 *
 */
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * scorpio4 (c) 2013
 * Module: com.scorpio4.security.keysafe
 * User  : lee
 * Date  : 3/11/2013
 * Time  : 12:21 PM
 *
 * Provides encryption and decryption of Strings or Map<String,String>
 *
 */

public class KeySafe {

    private char[] PASSWORD = null;
    private byte[] SALT = { (byte) 0xfe, (byte) 0x19, (byte) 0x32, (byte) 0x02, (byte) 0xfe, (byte) 0x91, (byte) 0x56, (byte) 0xbb };

    private SecretKeyFactory keyFactory = null;
    private SecretKey key = null;

    private KeySafe() {
    }

    public KeySafe(String pass) throws KeySafeException {
        unlock(pass);
    }

    public void unlock(String pass) throws KeySafeException {
        try {
            this.PASSWORD = pass.toCharArray();
            this.keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            this.key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
            sanityCheck();
        } catch (InvalidKeySpecException e) {
            throw new KeySafeException("Invalid Key",e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeySafeException("No Algorithm",e);
        }
    }

    private void sanityCheck() throws KeySafeException {
        if (PASSWORD==null) throw new KeySafeException("Not Unlocked: No password");
        if (keyFactory==null) throw new KeySafeException("Not Unlocked: No secret key factory");
        if (key==null) throw new KeySafeException("Not Unlocked: No secret key");
    }

    public Map encrypt(Map<String,String> src) throws KeySafeException {
        return encrypt(src, false);
    }


    public Map encrypt(Map<String,String> src, boolean keysToo) throws KeySafeException {
        sanityCheck();
        Map dst = new HashMap();
        for(String key:src.keySet()) {
            dst.put( keysToo?encrypt(key):key, encrypt(src.get(key)));
        }
        return dst;
    }

    public Map decrypt(Map<Object,Object> src) throws KeySafeException {
        return decrypt(src, false);
    }

    public Map decrypt(Map<Object,Object> src, boolean keysToo) throws KeySafeException {
        sanityCheck();
        Map dst = new HashMap();
        for(Object key:src.keySet()) {
            dst.put( keysToo?decrypt(key.toString()):key, decrypt((String)src.get(key)));
        }
        return dst;
    }

    public String encrypt(String property) throws KeySafeException {
        sanityCheck();
        try {
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
        } catch (IOException e) {
            throw new KeySafeException("IO Exception",e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeySafeException("No Such Algorithm Exception",e);
        } catch (InvalidKeyException e) {
            throw new KeySafeException("Invalid Key Exception",e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new KeySafeException("Invalid Algorithm Parameter Exception",e);
        } catch (NoSuchPaddingException e) {
            throw new KeySafeException("No Such Padding Exception",e);
        } catch (BadPaddingException e) {
            throw new KeySafeException("Bad Padding Exception",e);
        } catch (IllegalBlockSizeException e) {
            throw new KeySafeException("Illegal Block Size Exception",e);
        }
    }

    private String base64Encode(byte[] bytes) {
        // NB: This class is internal - should use another impl
        return new BASE64Encoder().encode(bytes);
    }

    public String decrypt(String property) throws KeySafeException {
        sanityCheck();
        try {
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
        } catch (IOException e) {
            throw new KeySafeException("IO Exception",e);
        } catch (NoSuchAlgorithmException e) {
            throw new KeySafeException("No Such Algorithm Exception",e);
        } catch (InvalidKeyException e) {
            throw new KeySafeException("Invalid Key Exception",e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new KeySafeException("Invalid Algorithm Parameter Exception",e);
        } catch (NoSuchPaddingException e) {
            throw new KeySafeException("No Such Padding Exception",e);
        } catch (BadPaddingException e) {
            throw new KeySafeException("Bad Padding Exception",e);
        } catch (IllegalBlockSizeException e) {
            throw new KeySafeException("Illegal Block Size Exception",e);
        }
    }

    private byte[] base64Decode(String property) throws IOException {
        // NB: This class is internal - should use another impl
        return new BASE64Decoder().decodeBuffer(property);
    }

    public Properties load(File file) throws IOException, KeySafeException {
        Properties locked = new Properties();
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            locked.store(fileOutputStream, "KeyStore: "+file.getName());
            fileOutputStream.close();
        } else {
            FileInputStream fileInputStream = new FileInputStream(file);
            locked.load(fileInputStream);
            fileInputStream.close();
        }
        Properties unlocked = new Properties();
        unlocked.putAll(decrypt(locked));
        return unlocked;
    }

}
