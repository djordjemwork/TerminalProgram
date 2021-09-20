package application;

import application.exceptions.CardInitException;
import javafx.scene.control.ComboBox;
import org.jmrtd.lds.PACEInfo;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;


public class SmartCardCommunication {

    private static SmartCardCommunication communication = null;
    private CardTerminal cardTerminal;
    private boolean secureChannelEstablished;
    private SecretKey secretKey;

    private SmartCardCommunication() {

    }

    public static SmartCardCommunication getInstance() {
        if (communication == null) {
            communication = new SmartCardCommunication();
        }
        return communication;
    }

    public boolean isSecureChannelEstablished() {
        return secureChannelEstablished;
    }

    public void setSecureChannelEstablished(boolean secureChannelEstablished) {
        this.secureChannelEstablished = secureChannelEstablished;
    }

    public void setCardTerminal(String cardTerminal) {
        List<CardTerminal> terminals = getAllTerminals();
        if (terminals == null) {
            return;
        }
        for (CardTerminal terminal : terminals) {
            if (terminal.toString().split("PC/SC terminal ")[1].equals(cardTerminal)) {
                this.cardTerminal = terminal;
                return;
            }
        }
    }

    public SecretKey getSymmetricKey() throws Exception {
        try {
            Card connection = cardTerminal.connect("T=1");
            CardChannel cs = connection.getBasicChannel();
            SecureRandom random = new SecureRandom();
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            DHParameterSpec dhParameterSpec = (DHParameterSpec) PACEInfo.toParameterSpec(PACEInfo.PARAM_ID_GFP_1024_160);
            BigInteger G = dhParameterSpec.getG();
            BigInteger P = dhParameterSpec.getP();
            boolean sameKeys = false;
            SecretKeySpec sKeyS = null;
            byte[] randData = new byte[16];
            random.nextBytes(randData);
            int counterSymetricKeyFail = 0;
            while (!sameKeys) {
                byte[] bByte = new byte[129];
                random.nextBytes(bByte);
                BigInteger b = new BigInteger(bByte);
                b = b.mod(P);

                CommandAPDU commandAPDU = new CommandAPDU(0x0C, 0x88, 0x00, 0x00, G.modPow(b, P).toByteArray());
                ResponseAPDU responseAPDU = cs.transmit(commandAPDU);
                if (responseAPDU.getSW() != 0x9000) {
                    if (responseAPDU.getSW() == 0x6F00) {
                        throw new Exception("Something went wrong! Internal Error!");
                    } else {
                        throw new Exception(responseAPDU.toString());
                    }
                }

                byte[] data = responseAPDU.getData();
                byte[] a = new byte[data.length + 1];
                System.arraycopy(data, 0, a, 1, data.length);

                BigInteger A = new BigInteger(a);
                BigInteger K = A.modPow(b, P);
                byte[] sha1;
                if (K.toByteArray().length != 128) {
                    byte[] kbyte = K.toByteArray();
                    byte[] kOK = new byte[128];
                    int bytes = K.toByteArray().length - 128;
                    if (bytes > 0) {
                        System.arraycopy(kbyte, bytes, kOK, 0, 128);
                    } else {
                        System.arraycopy(kbyte, 0, kOK, -bytes, 128 + bytes);
                    }
                    sha1 = Arrays.copyOf(kOK, kOK.length + 4);
                } else {
                    sha1 = Arrays.copyOf(K.toByteArray(), K.toByteArray().length + 4);
                }
                sha1[sha1.length - 1] = 0x01;
                sha1 = md.digest(sha1);
                sha1 = Arrays.copyOf(sha1, 16);
                sKeyS = new SecretKeySpec(sha1, "AES");

                byte[] enc = new byte[randData.length + 1];
                enc[0] = (byte) randData.length;
                System.arraycopy(randData, 0, enc, 1, randData.length);
                enc = encrypt(enc, sKeyS);
                commandAPDU = new CommandAPDU(0x0C, 0x89, 0x00, 0x00, enc);
                responseAPDU = cs.transmit(commandAPDU);
                if (responseAPDU.getSW() != 0x9000) {
                    if (responseAPDU.getSW() == 0x6F00) {
                        throw new Exception("Something went wrong! Internal Error!");
                    } else {
                        throw new Exception(responseAPDU.toString());
                    }
                }
                byte[] dec = decrypt(responseAPDU.getData(), sKeyS);
                int len = 16;
                byte[] decRandData = new byte[len];
                System.arraycopy(dec, 2, decRandData, 0, len);
                if (Arrays.equals(randData, decRandData)) {
                    sameKeys = true;
                } else {
                    counterSymetricKeyFail++;
                }
                if (counterSymetricKeyFail > 5) {
                    return null;
                }
            }
            this.secureChannelEstablished = true;
            return sKeyS;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private byte[] encrypt(byte[] message, SecretKey sKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKey);
            return cipher.doFinal(message);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    private byte[] decrypt(byte[] encrypted, SecretKey sKey) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, sKey);
            return cipher.doFinal(encrypted);
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public void loadCardReaders(ComboBox<String> cardReaders) {
        List<CardTerminal> terminals = getAllTerminals();
        if (terminals == null)
            return;
        for (CardTerminal terminal : terminals) {
            cardReaders.getItems().add(terminal.toString().split("PC/SC terminal ")[1].trim());
            cardReaders.setValue(terminal.toString().split("PC/SC terminal ")[1].trim());
        }
    }

    private List<CardTerminal> getAllTerminals() {
        try {
            TerminalFactory tf = TerminalFactory.getDefault();
            return tf.terminals().list();
        } catch (CardException ex) {
            return null;
        }
    }

    public void establishSecureChannel() throws Exception {
        try {
            if (cardTerminal == null) {
                throw new CardInitException("Error Initalization! CardReader is not Initialized");
            }
            if (!selectApplet()) {
                System.out.println("Error in selecting Applet");
                return;
            } else {
                System.out.println("Applet selected");
            }

            SecretKey secretKey = getSymmetricKey();
            if (secretKey == null) {
                return;
            }
            this.secretKey = secretKey;
            this.secureChannelEstablished = true;

        }catch (Exception e) {
            throw new Exception(e);
        }

    }

    public void verifyPin(String pinString) throws Exception {
        try {
            Card connection = cardTerminal.connect("T=1");
            CardChannel cs = connection.getBasicChannel();

            byte[] pin = pinString.getBytes(StandardCharsets.US_ASCII);
            byte[] enc = new byte[pin.length + 1];
            enc[0] = (byte) pin.length;
            System.arraycopy(pin, 0, enc, 1, pin.length);
            enc = encrypt(enc, this.secretKey);

            CommandAPDU commandAPDU = new CommandAPDU(0x0C, 0x20, 0x00, 0x00, enc);
            ResponseAPDU responseAPDU = cs.transmit(commandAPDU);
            if (responseAPDU.getSW() != 0x9000) {
                System.out.println((short) responseAPDU.getSW());
                if (responseAPDU.getSW() == 0x6B00) {
                    System.out.println("SW wrong P1P2!");
                } else if (responseAPDU.getSW() == 0x6983) {
                    System.out.println("Authentication method blocked! No more remaining retries!");
                } else if (responseAPDU.getSW() == 0x6982) {
                    System.out.println("Pin was not correct!");
                } else if (responseAPDU.getSW() == 0x63C0) {
                    System.out.println("Verification failed! Card is locked!");
                } else if (responseAPDU.getSW() == 0x63C1) {
                    System.out.println("Verification failed! One more attempt left!");
                } else if (responseAPDU.getSW() == 0x63C2) {
                    System.out.println("Verification failed! Two more attempt left!");
                } else if (responseAPDU.getSW() == 0x6F00) {
                    System.out.println("Required Removing Card! Function not supported");
                }
            }
            System.out.println(responseAPDU.getSW());
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private boolean selectApplet() throws CardException {
        Card connection = cardTerminal.connect("T=1");
        CardChannel cardChannel = connection.getBasicChannel();
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00, hexStringToByteArray("A0000002481101"));
        ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);
        return responseAPDU.getSW() == 0x9000;
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
