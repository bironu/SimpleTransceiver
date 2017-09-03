package com.example.bironu.simpletransceiver.data;

/**
 *
 DataStoreで扱うことができるデータの静的なモデル
 Entity自身を直接操作することはせず、Value objectとして使用する
 EntityはPresentation層では使用されない

 */

public class Entity {
    public static class SendTarget {
        private final String mName;
        private final String mIpAddressString;
        private final int mRtpPort;
        private final int mCtrlPort;

        public SendTarget(String name, String ipAddressString, int rtpPort, int ctrlPort) {
            mName = name;
            mIpAddressString = ipAddressString;
            mRtpPort = rtpPort;
            mCtrlPort = ctrlPort;
        }

        public String getName() {
            return mName;
        }

        public String getIpAddressString() {
            return mIpAddressString;
        }

        public int getRtpPort() {
            return mRtpPort;
        }

        public int getCtrlPort() {
            return mCtrlPort;
        }
    }
}
