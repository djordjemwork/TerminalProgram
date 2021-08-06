package entity;

public class StatusCode {
    public static int OK = 0;

    public static int NotFoundCardReader = 1;
    public static int NoCardPresent = 2;
    public static int CardReaderIsNotInitialized = 3;
    public static int CardServiceException = 4;
    public static int NoSuchAlgorithmException = 5;
    public static int NoSuchPaddingException = 6;
    public static int InvalidKeyException = 7;
    public static int IllegalBlockSizeException = 8;
    public static int BadPaddingException = 9;
    public static int CertificateException = 10;
    public static int InvalidKeySpecException = 11;
    public static int IOException = 12;
    public static int FingerprintScannerException = 13;
    public static int TimeoutException = 14;
    public static int FingerprintServiceException = 15;
    public static int SignatureException = 16;
    public static int BadFingerprint = 17;
    public static int IllegalArgumentException = 18;
    public static int PACEException = 19;
    public static int PACENotDone = 20;

    public static int MOCCounterWasNotReset = 100;
    public static int UserPINWasNotReset = 101;
    public static int MasterPINWasNotReset = 102;
    public static int UserPINWasNotChanged = 103;
    public static int OldUserPINWasNotCorrect = 104;
    public static int UnauthorizedUser = 105;
    public static int MasterPINIsNotVerified = 106;
    public static int UserPINIsNotVerified = 107;
    public static int UserAndMasterPINIsNotVerified = 108;
    public static int WrongLength = 109;
    public static int ThereIsNoKeyToBeSelected = 110;
    public static int ConditionNotSatisfiedDeriveKey = 111;
    public static int FunctionNotSupported = 112;
    public static int CommandNotAllowed = 113;
    public static int FileNotFound = 114;
    public static int EncCertificateAlreadyImported = 115;
    public static int EncPrivateKeyAlreadyImported = 116;
    public static int FingerprintWasNotSuccessfulEnrolled = 117;
    public static int CardInternalError = 118;
    public static int CardGenericError = 119;
    public static int SW_WrongP1P2 = 120;
    public static int AuthenticationMethodBlockedNoMoreRemainingRetries = 121;
    public static int PinWasNotCorrect = 122;
    public static int VerificationFailedCardIsLocked = 123;
    public static int VerificationFailedOneMoreAttemptLeft = 124;
    public static int VerificationFailedTwoMoreAttemptLeft = 125;
    public static int SignatureIsNotCorrect = 126;
    public static int CannotSignEncryptText = 127;
    public static int CannotSignEncryptedKey = 128;
    public static int CannotValidateEncryptedKey = 129;
    public static int CannotValidateEncryptedText = 130;
    public static int CannotDecryptEncryptedAESKey = 131;
    public static int FingerprintRequired = 132;
    public static int MemoryFailure = 133;
    public static int IllegalAdditionalDataFormatOnCard = 133;

    public static int FingerprintScannerNotConnectedException = 205;

    public static int RequiredRemovingCard = 300;

    public static int RequestedTerminalRecovery = 998;
    public static int GenericError = 999;
}
