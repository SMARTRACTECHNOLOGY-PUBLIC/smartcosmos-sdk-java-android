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
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;
import retrofit.http.QueryMap;

public class ProfilesRestApi {

    // Methods

    public interface IProfilesMethods {
        @GET("/rest/test/ping")
        ProfilesErrorResponse getTestPing();

        @Deprecated
        @GET("/rest/account")
        GetAccountResponse getAccount();

        @GET("/rest/tag/tdn/{tagId}")
        GetTagTdnResponse getTagTdn(@Path("tagId") String tagId);

        @GET("/rest/tag/properties/definition/{tagId}")
        GetTagMetadataDefinitionResponse getTagMetadataDefinition(
                @Path("tagId") String tagId,
                @Query("namelike") String nameLike);

        @GET("/rest/tag/queryBatches")
        GetQueryBatchesResponse getQueryBatches(@QueryMap Map<ProfilesQueryBatchProperty, Object> queryParams);

        @GET("/rest/tag/queryTags")
        GetQueryTagsResponse getQueryTags(@QueryMap Map<ProfilesQueryTagProperty, Object> queryParams);

        @POST("/rest/tag/key")
        GetTagKeyResponse postGetTagKey(@Body PostGetTagKey pGtk);

        @POST("/rest/tag/value")
        GetTagValueResponse postGetTagValue(@Body PostGetTagValue pGtv);

        @PUT("/rest/tag/value")
        UpdateTagValueResponse putUpdateTagValue(@Body PutUpdateTagValue pUtv);

        @POST("/rest/tag/auth/nxp")
        VerifyNxpTagResponse postVerifyNxpTag(@Body PostVerifyNxpRequest pNxp);

        @POST("/rest/tag/auth/otp/request")
        RequestAuthOtpResponse postRequestAuthOtp(@Body PostRequestAuthOtpRequest pOtpRequest);

        @POST("/rest/tag/auth/otp/validate")
        ValidateAuthOtpResponse postValidateAuthOtp(@Body PostValidateAuthOtpRequest pOtpValidate);

        @Deprecated
        @POST("/rest/verification/message")
        GetVerificationMessageResponse postGetVerificationMessage (@Body PostGetVerificationMessage pGvm);

        @POST("/rest/verification/tags")
        GetVerificationTagsResponse postGetVerificationTags (@Body PostGetVerificationTags pGvt);

        @Deprecated
        @POST("/rest/transaction/{handler}")
        ProfilesErrorResponse postTransaction (
                @Path("handler") String handler,
                @Body ProfilesTransactionRequest[] profilesTransactionRequest);
    }

    // Standard response object for all HTTP 4xx responses  
    public static class ProfilesErrorResponse {
        int code;
        String message;
    }		

    // Input/output types

    public static class GetAccountResponse {
        long lastModifiedTimestamp;
        String name;
        String description;
        boolean activeFlag;
        String urn;
    }

    public static class GetTagTdnResponse {
        int code;
        String value;
    }

    public static class GetTagMetadataDefinitionResponse {
        int code;
        String tagId;
        TagMetadataDefinitionProperty[] properties;
    }

    public static class TagMetadataDefinitionProperty {
        String propertyId;
        String propertyName;
        String dataType;
        boolean dataAvailable;
    }

    public static class GetQueryBatchesResponse {
        int code;
        String[] batchUrns;
    }

    public static class GetQueryTagsResponse {
        int code;
        String[] tagIds;
    }

    public static class PostGetTagKey {
        String[] tagIds;
        String appId;
    }
    
    public static class GetTagKeyResponse {
        int code;
        TagKeyItem[] result;
    }
    
    public static class TagKeyItem {
        String tagId;
        int tagCode;
        String key;
    }

    public static class PostGetTagValue {
        String[] tagIds;
        String appId;
    }

    public static class GetTagValueResponse {
        int code;
        TagValueItem[] result;
    }

    public static class PutUpdateTagValue {
        String appId;
        TagValuesToWrite[] tags;
    }

    public static class TagValuesToWrite {
        String tagId;
        String value;
        boolean locked;
    }

    public static class UpdateTagValueResponse {
        int code;
        TagValueWrite[] result;
    }

    public static class TagValueWrite {
        String tagId;
        int tagCode;
    }

    public static class TagValueItem {
        String tagId;
        int tagCode;
        String value;
        boolean locked;
    }
    
    public static class PostVerifyNxpRequest {
        String tagId;
        String tagVersion;
        String signature;
    }
    
    public static class VerifyNxpTagResponse {
        int code;
        String message;
        String tagId;
    }

    public static class PostRequestAuthOtpRequest {
        String tagId;
        String appId;
    }

    public static class RequestAuthOtpResponse {
        int code;
        String message;
        String tagId;
        String otpRequestId;
        String otpVector;
    }

    public static class PostValidateAuthOtpRequest {
        long timestamp;
        String otpRequestId;
        int otpResult;
    }

    public static class ValidateAuthOtpResponse {
        int code;
        String message;
        String tagId;
    }

    public static class PostGetVerificationMessage {
        String verificationType;
        int verificationState;
    }

    public static class GetVerificationMessageResponse {
        int code;
        String message;
    }

    public static class PostGetVerificationTags {
        String[] tagIds;
        String verificationType;
    }

    public static class GetVerificationTagsResponse {
        int code;
        TagVerificationItem[] result;
    }

    public static class TagVerificationItem {
        public String tagId;
        public int tagCode;
        public int state;
    }
}
