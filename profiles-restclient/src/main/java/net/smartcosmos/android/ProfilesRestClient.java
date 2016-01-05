package net.smartcosmos.android;

/*
 * *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*
 * SMART COSMOS Profiles RestClient for Android
 * ===============================================================================
 * Copyright (C) 2015 Smartrac Technology Fletcher, Inc.
 * ===============================================================================
 * SMART COSMOS SDK
 * (C) Copyright 2015, Smartrac Technology Fletcher, Inc.
 * 267 Cane Creek Rd, Fletcher, NC, 28732, USA
 * All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
 */


import java.util.Map;
import retrofit.RestAdapter;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import net.smartcosmos.android.ProfilesRestApi.GetVerificationTagsResponse;
import net.smartcosmos.android.ProfilesRestApi.IProfilesMethods;
import net.smartcosmos.android.ProfilesRestApi.PostGetVerificationTags;
import net.smartcosmos.android.ProfilesRestApi.ProfilesErrorResponse;
import net.smartcosmos.android.ProfilesRestApi.GetTagKeyResponse;
import net.smartcosmos.android.ProfilesRestApi.PostGetTagKey;
import net.smartcosmos.android.ProfilesRestApi.GetTagValueResponse;
import net.smartcosmos.android.ProfilesRestApi.PostGetTagValue;
import net.smartcosmos.android.ProfilesRestApi.PutUpdateTagValue;
import net.smartcosmos.android.ProfilesRestApi.UpdateTagValueResponse;
import net.smartcosmos.android.ProfilesRestApi.TagValuesToWrite;
import net.smartcosmos.android.ProfilesRestApi.PostVerifyNxpRequest;
import net.smartcosmos.android.ProfilesRestApi.TagVerificationItem;
import net.smartcosmos.android.ProfilesRestApi.VerifyNxpTagResponse;
import net.smartcosmos.android.ProfilesRestApi.PostRequestAuthOtpRequest;
import net.smartcosmos.android.ProfilesRestApi.RequestAuthOtpResponse;
import net.smartcosmos.android.ProfilesRestApi.PostValidateAuthOtpRequest;
import net.smartcosmos.android.ProfilesRestApi.ValidateAuthOtpResponse;
import net.smartcosmos.android.ProfilesRestApi.GetVerificationMessageResponse;
import net.smartcosmos.android.ProfilesRestApi.PostGetVerificationMessage;
import net.smartcosmos.android.ProfilesRestApi.GetTagTdnResponse;
import net.smartcosmos.android.ProfilesRestApi.GetQueryTagsResponse;

import net.smartcosmos.android.utility.AsciiHexConverter;
import net.smartcosmos.android.utility.Rfc6238;

public class ProfilesRestClient {

    private String _sServer;
    private String _sUser;
    private String _sPassword;
    private ProfilesApiRequestInterceptor _apiRequestInterceptor;
    private RestAdapter _restAdapter;
    private ProfilesRestErrorHandler _restErrorHandler;

    private ProfilesRestResult parseErrorResponse(RuntimeException exResponse)
    {
        ProfilesRestResult ret = new ProfilesRestResult();
        String sMessage = exResponse.getMessage();
        try {
            if (sMessage.substring(0, 4).equals("HTTP"))
            {
                int iBodyStart = sMessage.indexOf('\n');
                ret.httpStatus = Integer.parseInt(sMessage.substring(5, iBodyStart));
                ProfilesErrorResponse resp = new Gson().fromJson(sMessage.substring(iBodyStart),
                                                        ProfilesErrorResponse.class);
                ret.iCode = resp.code;
                ret.sMessage = resp.message;
            }
            else
            {
                ret.sMessage = exResponse.getMessage();
            }
        }
        catch (Exception exJson)
        {
            ret.sMessage = sMessage;
        }
        return ret;
    }

    static final String TAG = "ProfilesRestClient";
   
    // default result object for rest calls
    public class ProfilesRestResult {

        public ProfilesRestResult() {
            httpStatus = -1;
            iCode = -1;
            sMessage = "";
        }

        public int httpStatus;
        public int iCode;
        public String sMessage;
    }

    public class ProfilesAuthOtpState extends ProfilesRestResult {

        public byte[] tagId;
        public String otpRequestId;
        public byte[] otpVector;
        public int otpResult;
        public long timestamp;
    }

    /**
     * Constructs a new OtpRestClient targetting the given server and credentials.
     *
     * @param sServer URL of REST server
     * @param sUser user name (or null if no authentication is used)
     * @param sPassword password (or null if no authentication is used)
     */
    public ProfilesRestClient(String sServer, String sUser, String sPassword) {
       _sServer = sServer;
       _sUser = sUser;
       _sPassword = sPassword;
       _restErrorHandler = new ProfilesRestErrorHandler();
       
       if ((_sUser != null) && (_sPassword != null)) {
           _apiRequestInterceptor = new ProfilesApiRequestInterceptor();
           _apiRequestInterceptor.setUser(_sUser);
           _apiRequestInterceptor.setPassword(_sPassword);

           _restAdapter = new RestAdapter.Builder ()
                .setRequestInterceptor(_apiRequestInterceptor)
                .setEndpoint(_sServer)
                .setErrorHandler(_restErrorHandler)
                .build();
       } else {
           _restAdapter = new RestAdapter.Builder ()
                .setEndpoint(_sServer)
                .setErrorHandler(_restErrorHandler)
                .build();
       }
    }

    public ProfilesRestResult getTestPing()
    {
        ProfilesRestResult ret = new ProfilesRestResult();
        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            client.getTestPing();
            ret.httpStatus = 204;
            ret.iCode = 0;
            ret.sMessage = "Connection successful";
        }
        catch (RuntimeException ex) {
            ret = parseErrorResponse(ex);
        }
        Log.d(TAG, "getTestPing: HTTP " + ret.httpStatus +
                   ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Function get TDN data from a tag.
     *
     * @param uid
     * @return ProfilesRestResult
     *			.httpStatus: HTTP Status of the request or negative value in case of network error
     *			.iCode = 0 if successful
     *			.sMessage = Tag TDN data if code == 0, error message otherwise
     */
    public ProfilesRestResult getTagTdn(byte[] uid)
    {
        ProfilesRestResult ret = new ProfilesRestResult();
        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            GetTagTdnResponse tdnResp = client.getTagTdn(AsciiHexConverter.bytesToHex(uid));
            ret.httpStatus = 200;
            ret.iCode = tdnResp.code;
            ret.sMessage = tdnResp.value;
        }
        catch (RuntimeException ex) {
            ret = parseErrorResponse(ex);
        }
        Log.d(TAG, "getTagTdn: HTTP " + ret.httpStatus +
                   ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Look up an array of all tag ids which match the given criterias.
     *
     * @param propertyMap   map of tag properties as search criteria (incl. max count of results)
     * @return matching Tag IDs
     * @throws Exception
     */
    public String[] getTagsByProperties(Map<ProfilesQueryTagProperty, Object> propertyMap)
            throws Exception
    {
        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            GetQueryTagsResponse tags = client.getQueryTags(propertyMap);
            if (tags.code == 0)
                return tags.tagIds;
        }
        catch (RuntimeException ex) {
            ProfilesRestResult prr = parseErrorResponse(ex);
            String sError = "getTagsByProperties: HTTP " + prr.httpStatus +
                       ", code = " + prr.iCode + ", message = " + prr.sMessage;
            Log.d(TAG, sError);
            throw new Exception(sError);
        }
        return null;
    }

    /**
     * Function to get an application key from a tag.
     *
     * @param uid		tag UID
     * @param appId 	Application ID of the key
     * @return ProfilesRestResult
     *			.httpStatus: HTTP Status of the request or negative value in case of network error
     *			.iCode = 0 if successful
     *			.sMessage = Tag key if code == 0, error message otherwise
     */
    public ProfilesRestResult getSingleTagKey(byte[] uid, String appId)
    {
        ProfilesRestResult ret = new ProfilesRestResult();
        PostGetTagKey pGtk = new PostGetTagKey();
        pGtk.tagIds = new String[1];
        pGtk.tagIds[0] = AsciiHexConverter.bytesToHex(uid);
        pGtk.appId = appId;
        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            GetTagKeyResponse resp = client.postGetTagKey(pGtk);
            ret.httpStatus = 200;
            if (resp.result.length == 1)
            {
                ret.iCode = resp.result[0].tagCode;
                ret.sMessage = resp.result[0].key;
            }
        }
        catch (RuntimeException ex) {
            ret = parseErrorResponse(ex);
        }
        Log.d(TAG, "getSingleTagKey: HTTP " + ret.httpStatus +
                ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Function to get an application value from a tag.
     *
     * @param uid		tag UID
     * @param appId    Application ID of the value
     * @return ProfilesRestResult
     *			.httpStatus: HTTP Status of the request or negative value in case of network error
     *			.iCode = 0 if successful
     *			.sMessage = Tag value if code == 0, error message otherwise
     */
    public ProfilesRestResult getSingleTagValue(byte[] uid, String appId) {
        ProfilesRestResult ret = new ProfilesRestResult();
        PostGetTagValue pGtv = new PostGetTagValue();
        pGtv.tagIds = new String[1];
        pGtv.tagIds[0] = AsciiHexConverter.bytesToHex(uid);
        pGtv.appId = appId;
        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            GetTagValueResponse resp = client.postGetTagValue(pGtv);
            ret.httpStatus = 200;
            if (resp.result.length == 1)
            {
                ret.iCode = resp.result[0].tagCode;
                ret.sMessage = resp.result[0].value;
            }
        }
        catch (RuntimeException ex) {
            ret = parseErrorResponse(ex);
        }
        Log.d(TAG, "getSingleTagValue: HTTP " + ret.httpStatus +
                ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Function to update a value for a tag.
     *
     * @param uid		tag UID
     * @param appId     Application ID of the value
     * @param value     value to be updated (or null if value should remain unchanged)
     * @return ProfilesRestResult
     *			.httpStatus: HTTP Status of the request or negative value in case of network error
     *			.iCode = 0 if successful
     *			.sMessage = Tag value if code == 0, error message otherwise
     */
    public ProfilesRestResult updateSingleTagValue(byte[] uid, String appId, String value) {
        return updateSingleTagValue(uid, appId, value, false);
    }

    /**
     * Function to update a value for a tag.
     *
     * @param uid	    tag UID
     * @param appId     Application ID of the value
     * @param value     value to be updated (or null if value should remain unchanged)
     * @param locked    set to true to lock the tag
     * @return ProfilesRestResult
     *			.httpStatus: HTTP Status of the request or negative value in case of network error
     *			.iCode = 0 if successful
     *			.sMessage = Tag value if code == 0, error message otherwise
     */
    public ProfilesRestResult updateSingleTagValue(byte[] uid, String appId, String value, boolean locked) {
        ProfilesRestResult ret = new ProfilesRestResult();
        TagValuesToWrite tVtw = new TagValuesToWrite();
        tVtw.tagId = AsciiHexConverter.bytesToHex(uid);
        tVtw.value = value;
        tVtw.locked = locked;
        PutUpdateTagValue pUtv = new PutUpdateTagValue();
        pUtv.appId = appId;
        pUtv.tags = new TagValuesToWrite[] {tVtw};
        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            UpdateTagValueResponse resp = client.putUpdateTagValue(pUtv);
            ret.httpStatus = 200;
            if (resp.result.length == 1)
            {
                ret.iCode = resp.result[0].tagCode;
                ret.sMessage = resp.result[0].tagId;
            }
        }
        catch (RuntimeException ex) {
            ret = parseErrorResponse(ex);
        }
        Log.d(TAG, "updateSingleTagValue: HTTP " + ret.httpStatus +
                ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Function to verify the signature of a NXP NTAG.
     *
     * @param uid		tag UID
     * @param version 	NXP version info
     * @param signature	ECDSA signature of the tag
     * @return ProfilesRestResult
     *			.httpStatus: HTTP Status of the request or negative value in case of network error
     *			.iCode = 0 if successful
     *			.sMessage = status message
     */
    public ProfilesRestResult verifyNxpTag(byte[] uid, byte[] version, byte[] signature)
    {
        ProfilesRestResult ret = new ProfilesRestResult();
        PostVerifyNxpRequest pNxp = new PostVerifyNxpRequest();
        pNxp.tagId = AsciiHexConverter.bytesToHex(uid);
        pNxp.tagVersion = AsciiHexConverter.bytesToHex(version);
        pNxp.signature = Base64.encodeToString(signature, Base64.NO_WRAP);

        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            VerifyNxpTagResponse resp = client.postVerifyNxpTag(pNxp);
            ret.httpStatus = 200;
            ret.iCode = resp.code;
            ret.sMessage = resp.message;
        }
        catch (RuntimeException ex) {
            ret = parseErrorResponse(ex);
        }
        Log.d(TAG, "verifyNxpTag: HTTP " + ret.httpStatus +
                   ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Function for OTP authentication - step 1.
     *
     * @param uid		tag UID
     * @param appId 	should be "hmac"
     * @return ProfilesAuthOtpState - new OTP authentication state object
     *			.iCode = 0 if successful
     */
    public ProfilesAuthOtpState requestOtpAuthentication(byte[] uid, String appId)
    {
        ProfilesAuthOtpState ret = new ProfilesAuthOtpState();
        PostRequestAuthOtpRequest pROtp = new PostRequestAuthOtpRequest();
        pROtp.tagId = AsciiHexConverter.bytesToHex(uid);
        pROtp.appId = appId;

        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            RequestAuthOtpResponse resp = client.postRequestAuthOtp(pROtp);
            ret.httpStatus = 200;
            ret.iCode = resp.code;
            ret.sMessage = resp.message;
            ret.tagId = AsciiHexConverter.hexToBytes(resp.tagId);
            ret.otpRequestId = resp.otpRequestId;
            ret.otpVector = Base64.decode(resp.otpVector, 0);
        }
        catch (RuntimeException ex) {
            ret = (ProfilesAuthOtpState)parseErrorResponse(ex);
        }
        Log.d(TAG, "requestOtpAuthentication: HTTP " + ret.httpStatus +
                   ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Function for OTP authentication - step 2.
     *
     * @param otpState  ProfilesAuthOtpState from step 1
     * @param hmac	    OTP calculation secret from tag with given uid
     * @return ProfilesAuthOtpState - updated OTP authentication state object
     *			.otpResult (contains the current one-time password now)
     */
    public ProfilesAuthOtpState calculateOtpAuthResult(ProfilesAuthOtpState otpState, byte[] hmac)
    {
        // build the OTP secret from otpVector and tag secret (stored in tag:hmac:auth metadata)
        try {
            byte[] secret = new byte[20];
            for (int i = 0; i < 20; i++)
            {
                secret[i] = hmac[otpState.otpVector[i]];
            }
            otpState.timestamp = Rfc6238.getCurrentTimeIndex();
            otpState.otpResult = Rfc6238.getCodeForTimeIndex(secret, otpState.timestamp);
            Log.d(TAG, "Calculated OTP Result: " + otpState.otpResult);
        }
        catch (Exception e) {
            Log.d(TAG, "OTP calculation error: " + e.getMessage());
            return null;
        }
        return otpState;
    }

    /**
     * Function for OTP authentication - step 3.
     *
     * @param otpState ProfilesAuthOtpState from step 2
     * @return ProfilesRestResult
     *			.httpStatus: HTTP Status of the request or negative value in case of network error
     *			.iCode = 0 if successful
     *			.sMessage = status message
     */
    public ProfilesRestResult validateOtpAuthentication(ProfilesAuthOtpState otpState)
    {
        ProfilesRestResult ret = new ProfilesRestResult();
        PostValidateAuthOtpRequest pVOtp = new PostValidateAuthOtpRequest();
        pVOtp.timestamp = otpState.timestamp;
        pVOtp.otpRequestId = otpState.otpRequestId;
        pVOtp.otpResult = otpState.otpResult;

        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            ValidateAuthOtpResponse resp = client.postValidateAuthOtp(pVOtp);
            ret.httpStatus = 200;
            ret.iCode = resp.code;
            ret.sMessage = resp.message;
        }
        catch (RuntimeException ex) {
            ret = parseErrorResponse(ex);
        }
        Log.d(TAG, "validateOtpAuthentication: HTTP " + ret.httpStatus +
                   ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Get the user-friendly verification message String from the tag verification code.
     *
     * @param verificationType
     * @param verificationState
     * @return ProfilesRestResult
     *			.httpStatus: HTTP Status of the request or negative value in case of network error
     *			.iCode = 0 if successful
     *			.sMessage = status message
     */
    public ProfilesRestResult getVerificationMessage(String verificationType,
                                                     int verificationState)
    {
        ProfilesRestResult ret = new ProfilesRestResult();
        PostGetVerificationMessage pGvm = new PostGetVerificationMessage();
        pGvm.verificationType = verificationType;
        pGvm.verificationState = verificationState;

        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            GetVerificationMessageResponse resp = client.postGetVerificationMessage(pGvm);
            ret.httpStatus = 200;
            ret.iCode = resp.code;
            ret.sMessage = resp.message;
        }
        catch (RuntimeException ex) {
            ret = parseErrorResponse(ex);
        }
        Log.d(TAG, "getVerificationMessage: HTTP " + ret.httpStatus +
                   ", code = " + ret.iCode + ", message = " + ret.sMessage);
        return ret;
    }

    /**
     * Function for Tag License Verification.
     *
     * @param tagIds Tag Ids (HF UID or UHF TID)
     * @param verificationType	  License descriptor
     * @return TagVerificationItem[]: array of verified tags
     *          .tagId: tag ID
     *          .tagCode:
     *              0 = Tag exists in Profiles and contains a license state
     *              1 = Tag does not exist in Profiles
     *              3 = Tag exists in Profiles, but does not contain license info
     *          .state: License state (use @link getVerificationMessage to get the description)
     * @throws Exception
     */
    public TagVerificationItem[] getVerificationState(String[] tagIds, String verificationType)
        throws Exception {
        PostGetVerificationTags pGvt = new PostGetVerificationTags();
        pGvt.tagIds = tagIds.clone();
        pGvt.verificationType = verificationType;

        try {
            IProfilesMethods client = _restAdapter.create(IProfilesMethods.class);
            GetVerificationTagsResponse tags = client.postGetVerificationTags(pGvt);
            for (TagVerificationItem tagItem : tags.result)
            {
                Log.d(TAG, "Tag ID: " + tagItem.tagId + ", Code: " + tagItem.tagCode + " State: " + tagItem.state);
            }
            if (tags.code == 0)
                return tags.result;
        }
        catch (RuntimeException ex) {
            ProfilesRestResult prr = parseErrorResponse(ex);
            String sError = "getVerificationTags: HTTP " + prr.httpStatus +
                       ", code = " + prr.iCode + ", message = " + prr.sMessage;
            Log.d(TAG, sError);
            throw new Exception(sError);
        }
        return null;
    }
}
