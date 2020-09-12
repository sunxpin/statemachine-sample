package com.baoxian.common.util;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;

public class ZipUtil {
	static int bufferSize = 8192;// 单位bytes

	public static void doCompress(File srcFile, File destFile) throws IOException {
		ZipArchiveOutputStream out = null;
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(srcFile), bufferSize);
			out = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(destFile), bufferSize));
			ZipArchiveEntry entry = new ZipArchiveEntry(srcFile.getName());
			entry.setSize(srcFile.length());
			out.putArchiveEntry(entry);
			IOUtils.copy(is, out);
			out.closeArchiveEntry();
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(out);
		}
	} 
	public static void doCompressFiles(List<File> srcFiles, List<String> srcPaths, File destFile) throws IOException {
		ZipArchiveOutputStream out = null;
		InputStream is = null;
		try {
			out = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(destFile), bufferSize));
			for(int i = 0; i< srcFiles.size(); i++ ){
				is = new BufferedInputStream(new FileInputStream(srcFiles.get(i)), bufferSize);
				ZipArchiveEntry entry = new ZipArchiveEntry(srcPaths.get(i));
				entry.setSize(srcFiles.get(i).length());
				out.putArchiveEntry(entry);
				IOUtils.copy(is, out);
			}
			out.closeArchiveEntry();
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(out);
		}
	}

	public static void doDecompress(File srcFile, File destDir) throws IOException {
		ZipArchiveInputStream is = null;
		try {
			is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(srcFile), bufferSize));
			ZipArchiveEntry entry = null;
			while ((entry = is.getNextZipEntry()) != null) {
				if (entry.isDirectory()) {
					File directory = new File(destDir, entry.getName());
					directory.mkdirs();
				} else {
					OutputStream os = null;
					try {
						os = new BufferedOutputStream(new FileOutputStream(new File(destDir, entry.getName())),
								bufferSize);
						IOUtils.copy(is, os);
					} finally {
						IOUtils.closeQuietly(os);
					}
				}
			}
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
}