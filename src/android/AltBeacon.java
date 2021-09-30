/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.vienom.cordova.plugin.altbeacon;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Context;

import java.nio.charset.StandardCharsets;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import android.os.RemoteException;

/**
* Get Beacon Data
*/
public class AltBeacon extends CordovaPlugin {

    protected static final String TAG = "RangingActivity";

    private Context c;

    CallbackContext measureContext;

    private BeaconManager beaconManager;

    /**
    * Constructor.
    */
    public AltBeacon(){}

    /**
    * Sets the context of the Command.
    *
    * @param cordova The context of the main Activity.
    * @param webView The CordovaWebView Cordova is running in.
    */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      super.initialize(cordova, webView);
      c = cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("startRangingBeacons")) {
            measureContext = callbackContext;
            this.startRangingBeacons();
            return true;
        }
        return false;
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    /**
     * start ranging beacons
     *
     */
     private void startRangingBeacons() {

       beaconManager = BeaconManager.getInstanceForApplication(c);
       // To detect proprietary beacons, you must add a line like below corresponding to your beacon
       // type.  Do a web search for "setBeaconLayout" to get the proper expression.
       // beaconManager.getBeaconParsers().add(new BeaconParser().
       //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
       beaconManager.addRangeNotifier(new RangeNotifier() {
           @Override
           public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
               if (beacons.size() > 0) {
                 sendResult("The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                 Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
               }
           }
       });

       try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            sendResult("start ranging");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

     PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
     pluginResult.setKeepCallback(true);
     measureContext.sendPluginResult(pluginResult);

    }

    private void sendResult(String res){
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, res);
      pluginResult.setKeepCallback(true); // keep callback
      measureContext.sendPluginResult(pluginResult);
    }



}
