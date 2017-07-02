package com.example.bironu.simpletransceiver.codecs;


public class alaw
implements Codec
{
	public alaw() {
	}

	public int open() {
		G711.init();
		return 0;
	}

	public int decode(byte enc[], short lin[], int frames) {
		G711.alaw2linear(enc, lin, frames);
		return frames;
	}

	public int encode(short lin[], int offset, byte enc[], int frames) {
		G711.linear2alaw(lin, offset, enc, frames);
		return frames;
	}

	public void close() {
	}

	@Override
	public int samp_rate() {
		return 8000;
	}

	@Override
	public int frame_size() {
		return 160;
	}

	@Override
	public String name() {
		return "PCMA";
	}

	@Override
	public int number() {
		return 8;
	}
}
