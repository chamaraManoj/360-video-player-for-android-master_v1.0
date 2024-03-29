/**
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * Licensed under the Creative Commons CC BY-NC 4.0 Attribution-NonCommercial
 * License (the "License"). You may obtain a copy of the License at
 * https://creativecommons.org/licenses/by-nc/4.0/.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.oculus.sample;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.oculus.sample.player.SphericalVideoPlayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SphericalPlayerActivity extends AppCompatActivity{
    private final String SAMPLE_VIDEO_PATH =
            "android.resource://com.oculus.sample/raw/" + R.raw.sample360;

    private static final String VIDEO_SAMPLE =
            "https://developers.google.com/training/images/tacoma_narrows.mp4";

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0x1;
    private static final String CURRENT_POSITION = SphericalPlayerActivity.CURRENT_POSITION;

    private SphericalVideoPlayer videoPlayer;
    private long setPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            setPosition = savedInstanceState.getLong(CURRENT_POSITION);
        }
        else{
            setPosition=0;
        }

        setContentView(R.layout.activity_main);

        /**Custom layout, How can we extend our own texture view to the layout???*/
        videoPlayer = (SphericalVideoPlayer) findViewById(R.id.spherical_video_player);
        //videoPlayer.setVideoURIPath(getMedia(VIDEO_SAMPLE));
        videoPlayer.setVideoURIPath(SAMPLE_VIDEO_PATH);
        videoPlayer.playWhenReady();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //requestExternalStoragePermission();
        ///videoPlayer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
        init();
        videoPlayer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(CURRENT_POSITION,videoPlayer.getCurrentPositionInternalPlayer());
    }

    private String getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            // media name is an external URL
            return (mediaName);
        } else { // media name is a raw resource embedded in the app
            return ("android.resource://" + getPackageName() +
                    "/raw/" + mediaName);
        }
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(
                context,
                msg,
                Toast.LENGTH_SHORT).show();
    }

    private void init() {
        videoPlayer.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                videoPlayer.initRenderThread(surface, width, height,setPosition);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                //Toast.makeText(getApplicationContext(),"Surface changed",Toast.LENGTH_LONG);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                videoPlayer.releaseResources();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        if (requestCode != PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            return;
        }

        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            toast(this, "Access not granted for reading video file :(");
            return;
        }

        //init();
    }

    public static String readRawTextFile(Context context, int resId) {
        InputStream is = context.getResources().openRawResource(resId);
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader buf = new BufferedReader(reader);
        StringBuilder text = new StringBuilder();
        try {
            String line;
            while ((line = buf.readLine()) != null) {
                text.append(line).append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }

    /*private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                        PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            init();
        }
    }*/



}
