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


import java.io.UnsupportedEncodingException;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MimeUtil;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedByteArray;

public class ProfilesRestErrorHandler implements ErrorHandler {
	public Throwable handleError(RetrofitError cause) {
		Response r = cause.getResponse();
		if (r != null) {
			String sBody;
			String sError = "HTTP " + String.format("%03d",r.getStatus());
			TypedInput body = r.getBody();
			if (body != null)
			{
	            byte[] bodyBytes = ((TypedByteArray) body).getBytes();
	            String bodyMime = body.mimeType();
	            String bodyCharset = MimeUtil.parseCharset(bodyMime, "UTF-8");
	            try
	            {
	            	sBody = new String(bodyBytes, bodyCharset);
	            }
	            catch (UnsupportedEncodingException e)
	            {
	            	sBody = e.getMessage();
	            }
				sError = sError + "\n" + sBody;
			}
			throw new RuntimeException(sError);
		}
		return cause;
	}

}	
