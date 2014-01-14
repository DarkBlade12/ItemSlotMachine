package com.darkblade12.itemslotmachine.reader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public final class CompressedStringReader extends FileReader {
	public CompressedStringReader(String fileName, String directoryName) {
		super(fileName, directoryName);
	}

	public String readFromFile() throws Exception {
		if (!outputFile.exists())
			return null;
		InputStream i = new InflaterInputStream(new FileInputStream(outputFile));
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int len;
		while ((len = i.read(buffer)) > 0)
			b.write(buffer, 0, len);
		i.close();
		return new String(b.toByteArray(), "UTF-8");
	}

	public boolean saveToFile(String s) {
		if (!outputFile.exists())
			new File(outputPath).mkdirs();
		try {
			OutputStream out = new DeflaterOutputStream(new FileOutputStream(outputFile));
			out.write(s.getBytes("UTF-8"));
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void deleteFile() {
		super.deleteFile();
	}
}