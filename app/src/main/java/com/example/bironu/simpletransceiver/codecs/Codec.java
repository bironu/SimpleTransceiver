/*
 * Copyright (C) 2010 The Sipdroid Open Source Project
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


public interface Codec {
//	PT	符号化方式	A/V	Hz	内容
//	0	PCMU	A	8000	ITU-T G.711 A-law
//	1	1016	A	8000	US標準1016
//	2	G721	A	8000	ITU-T G.721
//	3	GSM	A	8000	欧州GSM06.10音声符号化
//	4	unassigned	A	8000	-
//	5	DVI4	A	8000	8kHzサンプリングDVI-ADPCM
//	6	DVI4	A	16000	16kHzサンプリングDVI-ADPCM
//	7	LPC	A	8000	Xerox線形予測符号化
//	8	PCMA	A	8000	ITU-T G.711μ-law
//	9	G722	A	8000	ITU-T G.722
//	10	L16	A	44100	8bitリニアオーディオステレオ
//	11	L16	A	44000	16bitリニアオーディモノラル
//	12	unassigned	A	-	-
//	13	unassigned	A	-	-
//	14	MPA	A	90000	MPEG-1/2オーディオ
//	15	G728	A	8000	ITU-T G.728
//	16-23	unassigned	A	-	-
//	24	unassigned	V	-	-
//	25	CelB	V	90000	Sun Cell-B符号化
//	26	JPEG	V	90000	JPEG
//	27	unassigned	V	-	-
//	28	nv	V	90000	Xeroxプログラムnv用符号化
//	29	unassigned	V	-	-
//	30	unassigned	V	-	-
//	31	H261	V	90000	ITU-T H.261
//	32	MPV	V	90000	MPEG-1/2ビデオ
//	33	MP2T	AV	90000	MPEG-2トランスポートストリーム
//	34-71	unassigned	-	-	-
//	72-76	reserved	N/A	N/A	N/A
//	77-95	unassigned	-	-	-
//	96-127	dynamic	-	-	動的割り当て
int TYPE_ULAW = 0;
	int TYPE_GSM = 3;
	int TYPE_ALAW = 8;
	int TYPE_G722 = 9;
	int TYPE_G729 = 18;
	int TYPE_SPEEX = 97;
	int TYPE_BV16 = 106;
	int TYPE_SILK8 = 117;
	int TYPE_SILK16 = 119;
	int TYPE_SILK24 = 120;

	int decode(byte encoded[], int offset, short lin[], int size);
	int encode(short lin[], int offset, byte alaw[], int frames);
	int samp_rate();
	int frame_size();
	int open();
	void close();
	String name();
	int number();
}
