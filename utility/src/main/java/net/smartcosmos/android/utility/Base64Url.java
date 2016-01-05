package net.smartcosmos.android.utility;

/*
 * *#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*
 * SMARTRAC Utility SDK for Android
 * ===============================================================================
 * Copyright (C) 2015 - 2016 Smartrac Technology Fletcher, Inc.
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

public class Base64Url {

    /**
     * Decodes a Base64Url encoded String
     *
     * @param input Base64Url encoded String
     * @return Decoded result from input
     */
    public static String decode(String input) {
        String result = null;
        byte[] decodedBytes = Base64.decode(input, Base64.URL_SAFE);
        try {
            result = new String(decodedBytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return result;
    }

    /**
     * Encodes a String with Base64Url and no padding
     *
     * @param input String to be encoded
     * @return Encoded result from input
     */
    public static String encode(String input) {
        String result = null;
        try {
            byte[] encodeBytes = input.getBytes("UTF-8");
            result = Base64.encodeToString(encodeBytes, Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return result;
    }
}
