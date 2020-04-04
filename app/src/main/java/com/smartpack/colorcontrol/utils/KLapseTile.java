/*
 * Copyright (C) 2020-2021 sunilpaulmathew <sunil.kde@gmail.com>
 *
 * This file is part of Screen Color Control, an app made to offer advanced control
 * over the screen colour of smart devices having KCAL/K-lapse support.
 *
 */

package com.smartpack.colorcontrol.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on April 04, 2020
 */

@SuppressLint("Registered")
@TargetApi(Build.VERSION_CODES.N)
public class KLapseTile extends TileService {

    private int id = Klapse.getklapseEnable();
    private int nightR = Klapse.getklapseRed();
    private int nightG = Klapse.getklapseGreen();
    private int nightB = Klapse.getklapseBlue();

    @Override
    public void onStartListening() {
        resetTileStatus();
    }

    @Override
    public void onClick() {
        updateTile();
    }

    private void updateTile() {
        Tile tile = this.getQsTile();
        String newLabel;
        int newState;

        // Update tile and set profile
        if (!(Klapse.supported())) {
            newLabel = "No K-Lapse support";
            newState = Tile.STATE_INACTIVE;
        } else {
            if (id == 1) {
                newLabel = "K-Lapse\nBrightness";
                Utils.toast("Brightness Mode Applied", getApplicationContext());
                newState = Tile.STATE_ACTIVE;
                id +=1;
                Klapse.setklapseEnable(2);
                Klapse.setklapseRed(nightR);
                Klapse.setklapseGreen(nightG);
                Klapse.setklapseBlue(nightB);
            } else if (id == 2) {
                newLabel = "K-Lapse\nTurned-Off";
                Utils.toast("K-Lapse is Turned-Off", getApplicationContext());
                newState = Tile.STATE_ACTIVE;
                id +=1;
                Klapse.setklapseEnable(0);
            } else {
                newLabel = "K-Lapse\nTime";
                Utils.toast("Time Mode Applied", getApplicationContext());
                newState = Tile.STATE_ACTIVE;
                id = 1;
                Klapse.setklapseEnable(1);
                Klapse.setklapseRed(nightR);
                Klapse.setklapseGreen(nightG);
                Klapse.setklapseBlue(nightB);
            }
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setState(newState);
        tile.updateTile();
    }

    private void resetTileStatus() {
        int status = Klapse.getklapseEnable();
        Tile tile = this.getQsTile();
        String newLabel;
        int newState;

        // Update tile
        if (!(Klapse.supported())) {
            newLabel = "No K-Lapse support";
            newState = Tile.STATE_INACTIVE;
        } else {
            if (status == 2) {
                newLabel = "K-Lapse\nBrightness";
                newState = Tile.STATE_ACTIVE;
            } else if (status == 1){
                newLabel = "K-Lapse\nTime";
                newState = Tile.STATE_ACTIVE;
            } else {
                newLabel = "K-Lapse\nTurned-Off";
                newState = Tile.STATE_ACTIVE;
            }
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setState(newState);
        tile.updateTile();
    }

}