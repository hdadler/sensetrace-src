package com.ipv.sensetrace.commandservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.*;

public class Lock {
	RandomAccessFile file = null; // The file we'll lock
	FileChannel f = null; // The channel to the file
	FileLock lock = null; // The lock object we hold

	Lock() {

		String tmpdir = System.getProperty("java.io.tmpdir");
		String filename = Lock.class.getName() + ".lock";
		File lockfile = new File(tmpdir, filename);

		// Create a FileChannel that can read and write that file.
		// Note that we rely on the java.io package to open the file,
		// in read/write mode, and then just get a channel from it.
		// This will create the file if it doesn't exit. We'll arrange
		// for it to be deleted below, if we succeed in locking it.
		try {
			file = new RandomAccessFile(lockfile, "rw");
		} catch (FileNotFoundException e) {
			if (lock != null) {
				// System.out.println("Release lock");
				// lock.release();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f = file.getChannel();

	}

	public boolean LockFile() {
		// Try to get an exclusive lock on the file.
		// This method will return a lock or null, but will not block.
		// See also FileChannel.lock() for a blocking variant.
		System.out.println("Try to lock file");
		try {
			lock = f.tryLock();
		} catch (IOException e) {
			e.printStackTrace();

			// This is an abnormal exit, so set an exit code.
			System.exit(1);

		}
		if (lock != null) {
			System.out.println("Locking file!");
			return true;
			// accessTheLockedFile();
		} else {
			System.out.println("File has allready been locked!");
			return false;
			// accessTheLockedFile();
		}
	}

}
