package com.example.com.wingsbangladesh.util;

import com.example.com.wingsbangladesh.pockdata.PocketPos;

public class Printer {
	
	public Printer(){}

	public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, (byte) 255, 3};

	
	public static byte[] printfont (String content,byte fonttype,byte fontalign,byte linespace,byte language){
		
		if (content != null && content.length() > 0) {
			
			content = content + "\n";
			byte[] temp = null;
			temp = PocketPos.convertPrintData(content, 0,content.length(), language, fonttype,fontalign,linespace);
			
			return temp;
		}else{
			return null;
		}
	}
	
}