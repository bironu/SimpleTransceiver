package com.example.bironu.simpletransceiver.service;

import java.net.InetAddress;

/**
 *
 */

public interface RtpServiceRepository
{
    InetAddress getLocalIpAddress();

    int getRtpPort();

    int getCtrlPort();
}
