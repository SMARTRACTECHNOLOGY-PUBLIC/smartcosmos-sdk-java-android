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


public class AsciiHexConverter {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xF0) == 0) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(bytes [i] & 0xFF));
        }
        return sb.toString().toUpperCase();
    }

    public static String bytesToHexReverse(byte[] bytes) {

        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; i--) {
            if ((bytes[i] & 0xF0) == 0) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(bytes [i] & 0xFF));
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] hexToBytes(String s) {

        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte)(0xFF & Integer.valueOf(s.substring(i * 2, i * 2 + 2), 16));
        }
        return data;
    }

    public static byte[] hexToBytesReverse(String s) {

        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < data.length; i++) {
            data[data.length - i - 1] = (byte)(0xFF & Integer.valueOf(s.substring(i * 2, i * 2 + 2), 16));
        }
        return data;
    }
}