package com.example.bironu.simpletransceiver.common;

import java.io.IOException;

public interface DataInputter {
	int input() throws IOException;
	byte[] getBuffer();
	void close();
}
