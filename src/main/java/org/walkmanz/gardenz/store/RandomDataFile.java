package org.walkmanz.gardenz.store;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;

/**
 * 
 * 使用RandomAccessFile实现带有缓冲的文件读写工具类
 *
 */
public class RandomDataFile extends DataFile {

	/**
	 * 文件实例
	 */
	private final File file;

	/**
	 * IO通道
	 */
	private RandomAccessFile randomAccessFile;
	
	
	protected boolean isCreateFile = false;

	/**
	 * 构造函数，会打开指定的文件
	 * 
	 * @param file
	 * @throws java.io.IOException
	 */
	public RandomDataFile(File file, boolean force) throws IOException {
		this(file.getAbsolutePath(),force);
	}

	public RandomDataFile(String path, boolean force) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
			this.isCreateFile = true;
		}

		this.file = file;
		this.randomAccessFile = new RandomAccessFile(file, force ? "rws" : "rw");
		// 指针移到首字节
		this.randomAccessFile.seek(0);
	}
	

	/**
	 * 获得文件名
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public String getFileName() throws IOException {
		return this.file.getName();
	}

	/**
	 * 删除文件
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public boolean delete() throws IOException {
		this.close();
		return this.file.delete();
	}

	/**
	 * 获取大小
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public long size() throws IOException {
		return this.randomAccessFile.length();
	}

	/**
	 * 获取指针位置
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public long position() throws IOException {
		return this.randomAccessFile.getFilePointer();
	}
	
	/**
	 * 设置指针位置
	 * @param position
	 * @throws java.io.IOException
	 */
	@Override
	public void position(long position) throws IOException {
		this.randomAccessFile.seek(position);
	}
	

	/**
	 * 同步数据到磁盘
	 *
	 * @throws IOException
	 */
	@Override
	public void sync() throws IOException {}
	
	/**
	 * 关闭通道
	 * 
	 * @throws java.io.IOException
	 */
	@Override
	public void close() throws IOException {
		this.sync();
		this.randomAccessFile.close();
		this.randomAccessFile = null;
	}
	
	/**
	 * 写一个bytes到文件的当前指针位置, 文件的指针会向后移动bytes的长度
	 * 
	 * @param bytes
	 * @return
	 * @throws java.io.IOException
	 */
	@Override
	public long write(final byte[] bytes) throws IOException {
		this.randomAccessFile.write(bytes);
		return this.position();
	}

	/**
	 * 写一个bytes到文件的指定位置, 文件指针不会移动
	 * 
	 * @param bytes
	 * @param position
	 * @throws java.io.IOException
	 */
	@Override
	public void write(final byte[] bytes, final long position)
			throws IOException {
		long pos = this.position();
		this.position(position);
		this.randomAccessFile.write(bytes);
		this.position(pos);
	}
	
	/**
	 * 按当前位置文件读取数据到bytes，直到读满或者读到文件结尾。 文件的指针会向后移动bytes的大小
	 * 
	 * @param bytes
	 * @throws java.io.IOException
	 */
	@Override
	public void read(final byte[] bytes) throws IOException {
		this.randomAccessFile.readFully(bytes);
	}

	/**
	 * 从文件的指定位置读取数据到bytes, 直到读满或者读到文件结尾。 文件指针不会移动
	 * 
	 * @param bytes
	 * @param position
	 * @throws java.io.IOException
	 */
	@Override
	public void read(final byte[] bytes, final long position)
			throws IOException {
		long pos = this.position();
		this.position(position);
		this.randomAccessFile.readFully(bytes);
		this.position(pos);
		
	}

	/**
	 * 获取一个MappedByteBuffer
	 * 
	 * @param mode
	 * @param position
	 * @param size
	 * @return
	 * @throws java.io.IOException
	 */
	public MappedByteBuffer map(MapMode mode,
			 long position, long size) throws IOException{
		return this.randomAccessFile.getChannel().map(mode, position, size);
	}
}
