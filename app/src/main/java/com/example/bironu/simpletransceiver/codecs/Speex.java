/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.example.bironu.simpletransceiver.codecs;

public class Speex
implements Codec
{
	static {
		System.loadLibrary("speex_jni");
	}
	
	/* quality
	 * 1 : 4kbps (very noticeable artifacts, usually intelligible)
	 * 2 : 6kbps (very noticeable artifacts, good intelligibility)
	 * 4 : 8kbps (noticeable artifacts sometimes)
	 * 6 : 11kpbs (artifacts usually only noticeable with headphones)
	 * 8 : 15kbps (artifacts not usually noticeable)
	 */
	private static final int DEFAULT_COMPRESSION = 6;
	
	public Speex() {
	}

	public native int open(int compression);
	public native int decode(byte encoded[], int offset, short lin[], int size);
	public native int encode(short lin[], int offset, byte encoded[], int size);
	public native void close();

	@Override
	public int samp_rate() {
		return 8000;
	}

	@Override
	public int frame_size() {
		return 160;
	}

	@Override
	public int open() {
		return open(DEFAULT_COMPRESSION);
	}

	@Override
	public String name() {
		return "speex";
	}

	@Override
	public int number() {
		return 97;
	}
}
