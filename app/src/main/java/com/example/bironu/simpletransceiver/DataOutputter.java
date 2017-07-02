package com.example.bironu.simpletransceiver;

import java.io.IOException;

public interface DataOutputter {
	void output(byte[] buf, int length) throws IOException;
	void close();
}
