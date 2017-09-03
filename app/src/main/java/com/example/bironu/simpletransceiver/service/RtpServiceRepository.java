package com.example.bironu.simpletransceiver.service;

import com.example.bironu.simpletransceiver.data.db.CursorLoadListener;

import java.net.InetAddress;

/**
 * Created by unko on 2017/08/28.
 */

public interface RtpServiceRepository {
    InetAddress getLocalIpAddress();
    int getRtpPort();
    int getCtrlPort();
//    Cursor getSendTargetCursor();
//    List<Entity.SendTarget> getSendTargetList();
    void setCursorLoadListener(CursorLoadListener listener);
}
