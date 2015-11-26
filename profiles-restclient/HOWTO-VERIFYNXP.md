# How-to: Verify a NXP NTAG Signature with Profiles RestClient

This guide describes how to verify the authenticity of a NXP chip using the Profiles signature feature.

	import net.smartcosmos.android.ProfilesRestClient;
	import net.smartcosmos.android.ProfilesRestClient.ProfilesRestResult;

	public class mySignatureVerification {

	/* ... */

		boolean verifyNxpSignatureExample(
			byte[] uid,			// NTAG uid
			byte[] version,		// NTAG version
			byte[] signature	// NTAG signature
		) {
			ProfilesRestResult result;
			ProfilesRestClient client = new ProfilesRestClient("https://profiles.example.com", "user-foo", "pw-bar");
	
			result = client.verifyNxpTag(uid, version, signature);
			switch (result.iCode)
			{
				case 0:
					/* handle VALID signature */
					return true;
				break;
				case 1:
					/* handle INVALID signature */
					return false;
				break;
				default:
					/* handle error state */
					throw SomewhatException(result.sMessage);
			}
		}
	}

Hint:

To obtain the *uid*, *version* and *signature* of the NTAG, use the NfcNtag API available on GitHub:

[https://github.com/SMARTRACTECHNOLOGY-PUBLIC/smartrac-sdk-java-android-nfc](https://github.com/SMARTRACTECHNOLOGY-PUBLIC/smartrac-sdk-java-android-nfc "NfcNtag API")