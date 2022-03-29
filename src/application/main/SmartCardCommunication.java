package application.main;

import application.exceptions.CardInitException;
import application.entity.LoginMessage;
import application.entity.StatusCode;
import application.entity.UserAccountMessage;
import javafx.scene.control.ComboBox;
import org.jmrtd.lds.PACEInfo;

import javax.crypto.*;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.smartcardio.*;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


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

    public void setCardTerminal(LoginMessage loginMessage) throws CardException {
        try {
            String cardTerminal = loginMessage.getCardReader();
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
        } catch (CardException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            throw new CardException(ex);
        }
    }

    public SecretKey getSymmetricKey(LoginMessage loginMessage) throws CardException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
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
            CommandAPDU commandAPDU = new CommandAPDU(0x0C, 0x88, 0x00, 0x00,
                    G.modPow(b, P).toByteArray());
            ResponseAPDU responseAPDU = cs.transmit(commandAPDU);
            if (responseAPDU.getSW() != 0x9000) {
                if (responseAPDU.getSW() == 0x6F00) {
                    loginMessage.setStatusCode(StatusCode.CardInternalError);
                    throw new CardException("Something went wrong! Internal Error!");
                } else {
                    loginMessage.setStatusCode(StatusCode.GenericError);
                    throw new CardException(responseAPDU.toString());
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
                    loginMessage.setStatusCode(StatusCode.CardInternalError);
                    throw new CardException("Something went wrong! Internal Error!");
                } else {
                    loginMessage.setStatusCode(StatusCode.GenericError);
                    throw new CardException(responseAPDU.toString());
                }
            }
            byte[] dec = decrypt(responseAPDU.getData(), sKeyS);
            int len = 16;
            byte[] decRandData = new byte[len];
            System.arraycopy(dec, 2, decRandData, 0, len);
            if (Arrays.equals(randData, decRandData)) {
                sameKeys = true;
            }
             else {
                counterSymetricKeyFail++;
            }
            if (counterSymetricKeyFail > 5) {
                loginMessage.setStatusCode(StatusCode.SymmetricKeyFail);
                throw new CardException("Error in establishing symmetric key");
            }
        }
        this.secureChannelEstablished = true;
        return sKeyS;
    }

    private byte[] encrypt(byte[] message, SecretKey sKey) throws
            NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sKey);
        return cipher.doFinal(message);
    }

    private byte[] decrypt(byte[] encrypted, SecretKey sKey) throws
            NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, sKey);
        return cipher.doFinal(encrypted);
    }

    public void loadCardReaders(ComboBox<String> cardReaders) {
        try {
            List<CardTerminal> terminals = getAllTerminals();
            if (terminals == null)
                return;
            for (CardTerminal terminal : terminals) {
                cardReaders.getItems().add(terminal.toString().split("PC/SC terminal ")[1].trim());
                cardReaders.setValue(terminal.toString().split("PC/SC terminal ")[1].trim());
            }
        } catch (CardException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    private List<CardTerminal> getAllTerminals() throws CardException {
        TerminalFactory tf = TerminalFactory.getDefault();
        return tf.terminals().list();
    }

    public void establishSecureChannel(LoginMessage loginMessage) throws CardInitException, CardException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException,
            InvalidKeyException {
        try {
            if (cardTerminal == null) {
                throw new CardInitException("Error Initialization! CardReader is not Initialized");
            }
            selectApplet(loginMessage);
            this.secretKey = getSymmetricKey(loginMessage);
            this.secureChannelEstablished = true;
        } catch (CardInitException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            loginMessage.setStatusCode(StatusCode.CardReaderIsNotInitialized);
            throw new CardInitException(ex.getMessage());
        } catch (CardException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            throw new CardException(ex);
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.NoSuchPaddingException);
            throw new NoSuchAlgorithmException(e.getMessage());
        } catch (InvalidKeyException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.InvalidKeyException);
            throw new InvalidKeyException(e.getMessage());
        } catch (NoSuchPaddingException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.NoSuchPaddingException);
            throw new NoSuchPaddingException(e.getMessage());
        } catch (BadPaddingException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.BadPaddingException);
            throw new BadPaddingException(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.IllegalBlockSizeException);
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    public void verifyPin(LoginMessage loginMessage) throws CardException, NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        try {
            Card connection = cardTerminal.connect("T=1");
            CardChannel cs = connection.getBasicChannel();

            String pinString = loginMessage.getUserPin();
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
                    loginMessage.setStatusCode(StatusCode.SW_WrongP1P2);
                    throw new CardException("SW wrong P1 and P2");
                } else if (responseAPDU.getSW() == 0x6983) {
                    loginMessage.setStatusCode(StatusCode.AuthenticationMethodBlockedNoMoreRemainingRetries);
                    throw new CardException("Authentication method blocked! No more remaining retries!");
                } else if (responseAPDU.getSW() == 0x6982) {
                    loginMessage.setStatusCode(StatusCode.PinWasNotCorrect);
                    throw new CardException("Pin was not correct!");
                } else if (responseAPDU.getSW() == 0x63C0) {
                    loginMessage.setStatusCode(StatusCode.VerificationFailedCardIsLocked);
                    throw new CardException("Verification failed! Card is locked!");
                } else if (responseAPDU.getSW() == 0x63C1) {
                    loginMessage.setStatusCode(StatusCode.VerificationFailedOneMoreAttemptLeft);
                    throw new CardException("Verification failed! One more attempt left!");
                } else if (responseAPDU.getSW() == 0x63C2) {
                    loginMessage.setStatusCode(StatusCode.VerificationFailedTwoMoreAttemptLeft);
                    throw new CardException("Verification failed! Two more attempt left!");
                } else if (responseAPDU.getSW() == 0x6F00) {
                    loginMessage.setStatusCode(StatusCode.RequiredRemovingCard);
                    throw new CardException("Required Removing Card! Function not supported");
                }
            }
        } catch (CardException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            throw new CardException(ex);
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.NoSuchAlgorithmException);
            throw new NoSuchAlgorithmException(e.getMessage());
        } catch (InvalidKeyException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.InvalidKeyException);
            throw new InvalidKeyException(e.getMessage());
        } catch (NoSuchPaddingException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.NoSuchPaddingException);
            throw new NoSuchPaddingException(e.getMessage());
        } catch (BadPaddingException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.BadPaddingException);
            throw new BadPaddingException(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, e);
            loginMessage.setStatusCode(StatusCode.IllegalBlockSizeException);
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    public void putUserAccountDataToCard(UserAccountMessage userAccountMessage) throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, CardException {

        try {
            userAccountMessage.setStatusCode(StatusCode.GenericError);
            Card connection = cardTerminal.connect("T=1");
            CardChannel cs = connection.getBasicChannel();
            String userAccountDataJSON = userAccountMessage.getUserAccountList();
            int le = userAccountDataJSON.getBytes(StandardCharsets.UTF_8).length;
            byte[] data = new byte[le + 2];
            data[0] = (byte) ((le >> 0x08) & 0xFF);
            data[1] = (byte) (le & 0xFF);
            System.arraycopy(userAccountDataJSON.getBytes("UTF-8"), 0, data, 2, le);

            byte[] enc = new byte[data.length + 2];
            enc[0] = (byte) ((data.length >> 0x08) & 0xFF);
            enc[1] = (byte) (data.length & 0xFF);
            System.arraycopy(data, 0, enc, 2, data.length);
            enc = encrypt(enc, this.secretKey);
            CommandAPDU commandAPDU = new CommandAPDU(0x0C, 0xDC, 0x00, 0x00, enc);
            ResponseAPDU responseAPDU = cs.transmit(commandAPDU);
            if (responseAPDU.getSW() != 0x9000) {
                if (responseAPDU.getSW() == 0x6982) {
                    userAccountMessage.setStatusCode(StatusCode.UnauthorizedUser);
                    throw new CardException("Unauthorized user!");
                } else if (responseAPDU.getSW() == 0x6F00) {
                    userAccountMessage.setStatusCode(StatusCode.CardInternalError);
                    throw new CardException("Something went wrong! Internal Error!");
                } else {
                    userAccountMessage.setStatusCode(StatusCode.GenericError);
                    throw new CardException(responseAPDU.toString());
                }
            }
            userAccountMessage.setStatusCode(StatusCode.OK);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.IllegalBlockSizeException);
            throw new IllegalBlockSizeException(ex.getMessage());
        } catch (InvalidKeyException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.InvalidKeyException);
            throw new InvalidKeyException(ex.getMessage());
        } catch (BadPaddingException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.BadPaddingException);
            throw new BadPaddingException(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.NoSuchAlgorithmException);
            throw new NoSuchAlgorithmException(ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.NoSuchPaddingException);
            throw new NoSuchPaddingException(ex.getMessage());
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.UnsupportedEncodingException);
            throw new UnsupportedEncodingException(ex.getMessage());
        } catch (CardException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            throw new CardException(ex.getMessage());
        }

    }

    public void getUserAccountDataFromCard(UserAccountMessage userAccountMessage) throws CardException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            userAccountMessage.setStatusCode(StatusCode.GenericError);
            userAccountMessage.setUserAccountList(null);
            Card connection = cardTerminal.connect("T=1");
            CardChannel cs = connection.getBasicChannel();
            CommandAPDU commandAPDU = new CommandAPDU(0x0C, 0xCC, 0x00, 0x00);
            ResponseAPDU responseAPDU = cs.transmit(commandAPDU);
            if (responseAPDU.getSW() != 0x9000) {
                if (responseAPDU.getSW() == 0x6982) {
                    userAccountMessage.setStatusCode(StatusCode.UnauthorizedUser);
                    throw new CardException("Unauthorized user!");
                } else if (responseAPDU.getSW() == 0x6F00) {
                    userAccountMessage.setStatusCode(StatusCode.CardInternalError);
                    throw new CardException("Something went wrong! Internal Error!");
                } else {
                    userAccountMessage.setStatusCode(StatusCode.CardGenericError);
                    throw new CardException(responseAPDU.toString());
                }
            }
            byte[] dec = decrypt(responseAPDU.getData(), this.secretKey);
            int len = (dec[0] & 0xff) * 256 + (dec[1] & 0xff);
            if (len == 0)
                return;
            byte[] data = new byte[len];
            System.arraycopy(dec, 2, data, 0, len);
            int lenUserAccountListData = (data[0] & 0xff) * 256 + (data[1] & 0xff);
            byte[] userAccountListByte = new byte[lenUserAccountListData];
            if (lenUserAccountListData > data.length) {
                userAccountMessage.setStatusCode(StatusCode.WrongLength);
                throw new CardException("Wrong additional data format on card!");
            }
            System.arraycopy(data, 2, userAccountListByte, 0, lenUserAccountListData);
            String s = new String(userAccountListByte);
            userAccountMessage.setUserAccountList(s);
            userAccountMessage.setStatusCode(StatusCode.OK);
        } catch (CardException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            throw new CardException(ex.getMessage());
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.IllegalBlockSizeException);
            throw new IllegalBlockSizeException(ex.getMessage());
        } catch (InvalidKeyException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.InvalidKeyException);
            throw new InvalidKeyException(ex.getMessage());
        } catch (BadPaddingException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.BadPaddingException);
            throw new BadPaddingException(ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.NoSuchAlgorithmException);
            throw new NoSuchAlgorithmException(ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(SmartCardCommunication.class.getName()).log(Level.SEVERE, null, ex);
            userAccountMessage.setStatusCode(StatusCode.NoSuchPaddingException);
            throw new NoSuchPaddingException(ex.getMessage());
        }
    }

    private void selectApplet(LoginMessage loginMessage) throws CardException {
        Card connection = cardTerminal.connect("T=1");
        CardChannel cardChannel = connection.getBasicChannel();
        CommandAPDU commandAPDU = new CommandAPDU(0x00, 0xA4, 0x04, 0x00, hexStringToByteArray(Util.appletID));

        ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);

        if (responseAPDU.getSW() != 0x9000) {
            loginMessage.setStatusCode(StatusCode.NoSelectingApplet);
            throw new CardException("Error in selecting applet");
        }
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
