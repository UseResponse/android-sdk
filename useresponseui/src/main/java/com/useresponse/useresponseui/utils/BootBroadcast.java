package com.useresponse.useresponseui.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.useresponse.useresponseui.NotificationsService;

public class BootBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        ctx.startService(new Intent(ctx, NotificationsService.class));
    }

}