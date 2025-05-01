package com.mapbox.android.telemetry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.VisibleForTesting;

class AlarmSchedulerFlusher implements SchedulerFlusher {
    private final Context context;
    private final AlarmManager manager;
    private final AlarmReceiver receiver;
    private PendingIntent pendingIntent;

    AlarmSchedulerFlusher(Context context, AlarmManager manager, AlarmReceiver receiver) {
        this.context = context;
        this.manager = manager;
        this.receiver = receiver;
    }

    public void register() {
        Intent alarmIntent = this.receiver.supplyIntent();
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        this.pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarmIntent, flags);
        IntentFilter filter = new IntentFilter("com.mapbox.scheduler_flusher");
        //this.context.registerReceiver(this.receiver, filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.context.registerReceiver(this.receiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            this.context.registerReceiver(this.receiver, filter);
        }
    }

    public void schedule(long elapsedRealTime) {
        long firstFlushingInMillis = elapsedRealTime + SchedulerFlusherFactory.flushingPeriod;
        this.manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, firstFlushingInMillis, SchedulerFlusherFactory.flushingPeriod, this.pendingIntent);
    }

    @VisibleForTesting
    boolean scheduleExact(long interval) {
        if (Build.VERSION.SDK_INT >= 19) {
            this.manager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + interval, this.pendingIntent);
            return true;
        } else {
            return false;
        }
    }

    public void unregister() {
        if (this.pendingIntent != null) {
            this.manager.cancel(this.pendingIntent);
        }

        try {
            this.context.unregisterReceiver(this.receiver);
        } catch (IllegalArgumentException var2) {
        }

    }

    PendingIntent obtainPendingIntent() {
        return this.pendingIntent;
    }
}
