package com.example.bironu.simpletransceiver;

import java.io.IOException;

public interface DataInputter {
	int input() throws IOException;
	byte[] getBuffer();
	void close();
}
