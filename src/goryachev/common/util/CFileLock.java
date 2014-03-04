// Copyright (c) 2010-2014 Andy Goryachev <andy@goryachev.com>
package goryachev.common.util;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class CFileLock
{
	private File file;
	private FileChannel channel;
	private FileLock lock;


	public CFileLock(File file)
	{
		this.file = file;
	}


	/** returns true if lock has been successfully acquired */
	public boolean lock()
	{
		try
		{
			if(!file.exists())
			{
				file.getParentFile().mkdirs();
			}
			channel = new RandomAccessFile(file, "rw").getChannel();

			lock = channel.tryLock();
			if(lock != null)
			{
				return true;
			}
		}
		catch(Exception e)
		{ 
			Log.err(e);
		}
		
		unlock();

		return false;
	}


	public void unlock()
	{
		if(lock != null)
		{
			try
			{
				lock.release();
			}
			catch(Exception e)
			{ }
			
			lock = null;
		}
		
		if(channel != null)
		{
			CKit.close(channel);
			channel = null;
			file.delete();
		}
	}
	
	
	public void checkLock() throws CFileLockedException
	{
		if(lock() == false)
		{
			throw new CFileLockedException();
		}
	}
}
