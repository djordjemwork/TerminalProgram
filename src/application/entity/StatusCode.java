package application.entity;

public class StatusCode {
    public static int OK = 0;

    public final static int NotFoundCardReader = 1;
    public final static int NoCardPresent = 2;
    public final static int CardReaderIsNotInitialized = 3;
    public final static int CardServiceException = 4;
    public final static int NoSuchAlgorithmException = 5;
    public final static int NoSuchPaddingException = 6;
    public final static int InvalidKeyException = 7;
    public final static int IllegalBlockSizeException = 8;
    public final static int BadPaddingException = 9;
    public final static int CertificateException = 10;
    public final static int InvalidKeySpecException = 11;
    public final static int IOException = 12;
    public final static int FingerprintScannerException = 13;
    public final static int TimeoutException = 14;
    public final static int FingerprintServiceException = 15;
    public final static int SignatureException = 16;
    public final static int BadFingerprint = 17;
    public final static int IllegalArgumentException = 18;
    public final static int PACEException = 19;
    public final static int PACENotDone = 20;
    public final static int UnsupportedEncodingException = 21;
    public final static int NoSelectingApplet = 22;
    public final static int SymmetricKeyFail = 23;

    public final static int MOCCounterWasNotReset = 100;
    public final static int UserPINWasNotReset = 101;
    public final static int MasterPINWasNotReset = 102;
    public final static int UserPINWasNotChanged = 103;
    public final static int OldUserPINWasNotCorrect = 104;
    public final static int UnauthorizedUser = 105;
    public final static int MasterPINIsNotVerified = 106;
    public final static int UserPINIsNotVerified = 107;
    public final static int UserAndMasterPINIsNotVerified = 108;
    public final static int WrongLength = 109;
    public final static int ThereIsNoKeyToBeSelected = 110;
    public final static int ConditionNotSatisfiedDeriveKey = 111;
    public final static int FunctionNotSupported = 112;
    public final static int CommandNotAllowed = 113;
    public final static int FileNotFound = 114;
    public final static int EncCertificateAlreadyImported = 115;
    public final static int EncPrivateKeyAlreadyImported = 116;
    public final static int FingerprintWasNotSuccessfulEnrolled = 117;
    public final static int CardInternalError = 118;
    public final static int CardGenericError = 119;
    public final static int SW_WrongP1P2 = 120;
    public final static int AuthenticationMethodBlockedNoMoreRemainingRetries = 121;
    public final static int PinWasNotCorrect = 122;
    public final static int VerificationFailedCardIsLocked = 123;
    public final static int VerificationFailedOneMoreAttemptLeft = 124;
    public final static int VerificationFailedTwoMoreAttemptLeft = 125;
    public final static int SignatureIsNotCorrect = 126;
    public final static int CannotSignEncryptText = 127;
    public final static int CannotSignEncryptedKey = 128;
    public final static int CannotValidateEncryptedKey = 129;
    public final static int CannotValidateEncryptedText = 130;
    public final static int CannotDecryptEncryptedAESKey = 131;
    public final static int FingerprintRequired = 132;
    public final static int MemoryFailure = 133;
    public final static int IllegalAdditionalDataFormatOnCard = 133;

    public final static int FingerprintScannerNotConnectedException = 205;

    public final static int RequiredRemovingCard = 300;

    public final static int RequestedTerminalRecovery = 998;
    public final static int GenericError = 999;
}
