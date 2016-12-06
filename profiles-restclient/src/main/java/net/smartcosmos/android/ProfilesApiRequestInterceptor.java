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


import android.util.Base64;
import java.io.UnsupportedEncodingException;
import retrofit.RequestInterceptor;

/**
 * Interceptor used to authorize requests.
 */
public class ProfilesApiRequestInterceptor implements RequestInterceptor {

    private String _sUser;
    private String _sPassword;

    public void intercept(RequestFacade requestFacade) {

        if (_sUser != null) {
            try {
                final String authorizationValue = encodeCredentialsForBasicAuthorization();
                requestFacade.addHeader("Authorization", authorizationValue);
            }
            catch (UnsupportedEncodingException e) {
                // Do nothing - overridden method does not support throwing exceptions.
                // The UTF-8 encoding is part of the Android runtime, so this exception is unlikely to be ever thrown.
            }

        }
    }

    private String encodeCredentialsForBasicAuthorization()
            throws UnsupportedEncodingException {
        final String userAndPassword = _sUser + ":" + _sPassword;
        return "Basic " + Base64.encodeToString(userAndPassword.getBytes("UTF-8"), Base64.NO_WRAP);
    }

    public String getUser() {
        return _sUser;
    }

    public void setUser(String sUser) {
    	_sUser = sUser;
    }
    
    public String getPassword() {
        return _sPassword;
    }

    public void setPassword(String sPassword) {
    	_sPassword = sPassword;
    }    
}