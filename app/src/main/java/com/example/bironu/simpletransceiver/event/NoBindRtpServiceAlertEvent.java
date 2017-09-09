package com.example.bironu.simpletransceiver.event;

import android.app.DialogFragment;
import android.content.Context;

import com.example.bironu.simpletransceiver.R;
import com.example.bironu.simpletransceiver.common.AlertDialogFragment;

/**
 * RtpServiceとbindに失敗している時のDialog作成イベント。
 */
public class NoBindRtpServiceAlertEvent
{
    public String getFragmentTag() {
        return this.getClass().getSimpleName();
    }

    public DialogFragment createAlertDialogFragment(Context context) {
        AlertDialogFragment.Builder b = new AlertDialogFragment.Builder(context);
        b.setMessage(R.string.no_bind_rtp_service_message);
        b.setPositiveButton(android.R.string.ok);
        return b.create();
    }
}
