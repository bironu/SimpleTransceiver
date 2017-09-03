package com.example.bironu.simpletransceiver.activitys;

import android.app.DialogFragment;
import android.content.Context;

import com.example.bironu.simpletransceiver.R;
import com.example.bironu.simpletransceiver.common.AlertDialogFragment;

/**
 * Created by unko on 2017/08/28.
 */

public class NoBindRtpServiceAlertEvent {
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
