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


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import java.util.List;

public class IntentLauncher
{
    private static final String EXTRA_LAUNCH_INTENT = "launchIntent";

    private Activity activity;

    /**
     * Create a new IntentLauncher from the calling activity.
     *
     * @param callingActivity
     */
    public IntentLauncher(Activity callingActivity)
    {
        activity = callingActivity;
    }

    /**
     * Open an URL in the browser registered to it's URI scheme.
     *
     * @param url URL to be opened in the browser.
     * @return true on success, false otherwise
     */
	public boolean launchUrl(String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			return tryStartActivity(intent);
		}
		catch (Exception e) {
			return false;
		}
	}

    /**
     * Create a new email in the registered email editor.
     *
     * @param recipient email recipient
     * @param subject email subject
     * @param body template body of the email
     * @return true on success, false otherwise
     */
	public boolean launchEmail(String recipient, String subject, String body) {
		try {
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:" + recipient));
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent.putExtra(Intent.EXTRA_TEXT, body);
			return tryStartActivity(intent);
		}
		catch (Exception e) {
			return false;
		}
	}

    /**
     * Dispatch a generic intent.
     *
     * @param intent Intent to be dispatched
     * @return true on success, false otherwise
     */
    public boolean launchIntent(Intent intent) {
        return tryStartActivity(intent);
    }

	private boolean tryStartActivity(Intent intentToStart) {
    	try {
	    	Context context = activity.getApplicationContext();
	    	PackageManager packageManager = context.getPackageManager();

	        List<ResolveInfo> activities = packageManager.queryIntentActivities(
	                intentToStart, 0);
	        if (activities.size() > 0) {
	        	Intent rootIntent = Intent.createChooser(intentToStart, "Select app");
	            rootIntent.putExtra(EXTRA_LAUNCH_INTENT, intentToStart);
	            rootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            context.startActivity(rootIntent);
	        }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
}
