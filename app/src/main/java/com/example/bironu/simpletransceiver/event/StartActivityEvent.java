package com.example.bironu.simpletransceiver.event;

import android.content.Context;
import android.content.Intent;

/**
 * 明示的IntentによるActivity開始イベント。
 */

public interface StartActivityEvent
{
    Intent getIntent(Context context);
}
