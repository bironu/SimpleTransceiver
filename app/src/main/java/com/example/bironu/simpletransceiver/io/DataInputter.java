package com.example.bironu.simpletransceiver.io;

import java.io.IOException;

public interface DataInputter
{
    int input() throws IOException;

    byte[] getBuffer();

    void close();
}
