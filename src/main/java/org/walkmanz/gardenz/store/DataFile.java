package org.walkmanz.gardenz.store;

import org.walkmanz.gardenz.util.BytesUtils;

import java.io.IOException;

public abstract class DataFile {
	
	/**
	 * 获得文件名
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	public abstract String getFileName() throws IOException;

	/**
	 * 删除文件
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	public abstract boolean delete() throws IOException;
	
	/**
	 * 同步数据到磁盘
	 *
	 * @throws Exception
	 */
	public abstract void sync() throws IOException;
	
	/**
	 * 获取大小
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	public abstract long size() throws IOException;
	
	
	/**
	 * 设置文件指针位置, 可能会改变缓冲区在通道中的取值位置
	 * @param position
	 * @throws java.io.IOException
	 */
	public abstract void position(long position) throws IOException;

	/**
	 * 获取文件指针位置
	 * 
	 * @return
	 * @throws java.io.IOException
	 */
	public abstract long position() throws IOException;

	/**
	 * 关闭通道
	 * 
	 * @throws java.io.IOException
	 */
	public abstract void close() throws IOException;

	
	/**
	 * 写一个bytes到文件的当前指针位置, 文件的指针会向后移动bytes的长度
	 * 
	 * @param bytes
	 * @throws java.io.IOException
	 */
	public abstract long write(final byte[] bytes) throws IOException;

	/**
	 * 写一个bytes到文件的指定位置, 文件指针不会移动
	 * 
	 * @param bytes
	 * @param position
	 * @throws java.io.IOException
	 */
	public abstract void write(final byte[] bytes, final long position)
			throws IOException;
	
	/**
	 * 当前位置写一个int, 指针会移动
	 * @param data
	 * @throws java.io.IOException
	 */
	public void writeInt(final int data) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer);
	}
	
	/**
	 * 去指定位置写一个int, 指针不会移动
	 * @param data
	 * @param position
	 * @throws java.io.IOException
	 */
	public void writeInt(final int data, final long position) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer, position);
	}
	
	/**
	 * 当前位置写一个float, 指针会移动
	 * @param data
	 * @throws java.io.IOException
	 */
	public void writeFloat(final float data) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer);
	}
	
	/**
	 * 去指定位置写一个float, 指针不会移动
	 * @param data
	 * @param position
	 * @throws java.io.IOException
	 */
	public void writeFloat(final float data, final long position) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer, position);
	}

	/**
	 * 当前位置写一个long, 指针会移动
	 * @param data
	 * @throws java.io.IOException
	 */
	public void writeLong(final long data) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer);
	}

	/**
	 * 去指定位置写一个long, 指针不会移动
	 * @param data
	 * @param position
	 * @throws java.io.IOException
	 */
	public void writeLong(final long data, final long position) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer, position);
	}
	
	/**
	 * 当前位置写一个double, 指针会移动
	 * @param data
	 * @throws java.io.IOException
	 */
	public void writeDouble(final double data) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer);
	}

	/**
	 * 去指定位置写一个double, 指针不会移动
	 * @param data
	 * @param position
	 * @throws java.io.IOException
	 */
	public void writeDouble(final double data, final long position) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer, position);
	}
	
	/**
	 * 当前位置写一个String, 指针会移动
	 * @param data
	 * @throws java.io.IOException
	 */
	public void writeUTF(final String data) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer);
	}

	/**
	 * 去指定位置写一个String, 指针不会移动
	 * @param data
	 * @param position
	 * @throws java.io.IOException
	 */
	public void writeUTF(final String data, final long position) throws IOException{
		byte[] buffer = BytesUtils.getBytes(data);
		this.write(buffer, position);
	}
	
	
	/**
	 * 按当前位置文件读取数据到bytes，直到读满或者读到文件结尾。 文件的指针会向后移动bytes的大小
	 * 
	 * @param bytes
	 * @throws java.io.IOException
	 */
	public abstract void read(final byte[] bytes) throws IOException;
	
	/**
	 * 从文件的指定位置读取数据到bytes, 直到读满或者读到文件结尾。 文件指针不会移动
	 * 
	 * @param bytes
	 * @param position
	 * @throws java.io.IOException
	 */
	public abstract void read(final byte[] bytes, final long position)
			throws IOException;
	
	/**
	 * 按当前位置文件读取数据到bytes，直到读满或者读到文件结尾。 文件的指针会向后移动bytes的大小
	 * 
	 * @param size
	 * @return
	 * @throws java.io.IOException
	 */
	public byte[] read(int size) throws IOException {
		byte[] bytes = new byte[size];
		this.read(bytes);
		return bytes;
	}
	
	/**
	 * 从文件的指定位置读取数据到bytes，直到读满或者读到文件结尾。 文件指针不会移动
	 * 
	 * @param size
	 * @param position
	 * @return
	 * @throws java.io.IOException
	 */
    public byte[] read(int size, long position) throws IOException {
    	byte[] bytes = new byte[size];
		this.read(bytes, position);
		return bytes;
	}
	

	/**
	 * 当前位置读一个int, 指针会移动
	 * @return
	 * @throws java.io.IOException
	 */
	public int readInt() throws IOException {
		byte[] bytes = new byte[4];
		this.read(bytes);
		return BytesUtils.getInt(bytes);
	}
	
	/**
	 * 从指定位置度一个int, 指针不会移动
	 * @param position
	 * @return
	 * @throws java.io.IOException
	 */
	public int readInt(long position)  throws IOException {
		byte[] bytes = new byte[4];
		this.read(bytes, position);
		return BytesUtils.getInt(bytes);
	}
	
	/**
	 * 当前位置读一个float, 指针会移动
	 * @return
	 * @throws java.io.IOException
	 */
	public float readFloat() throws IOException {
		byte[] bytes = new byte[4];
		this.read(bytes);
		return BytesUtils.getFloat(bytes);
	}
	
	/**
	 * 从指定位置度一个float, 指针不会移动
	 * @param position
	 * @return
	 * @throws java.io.IOException
	 */
	public float readFloat(long position) throws IOException {
		byte[] bytes = new byte[4];
		this.read(bytes, position);
		return BytesUtils.getFloat(bytes);
	}
	
	/**
	 * 当前位置读一个long, 指针会移动
	 * @return
	 * @throws java.io.IOException
	 */
	public long readLong() throws IOException {
		byte[] bytes = new byte[8];
		this.read(bytes);
		return BytesUtils.getLong(bytes);
	}
	
	/**
	 * 从指定位置度一个long, 指针不会移动
	 * @param position
	 * @return
	 * @throws java.io.IOException
	 */
	public long readLong(long position)  throws IOException {
		byte[] bytes = new byte[8];
		this.read(bytes, position);
		return BytesUtils.getLong(bytes);
	}
	
	
	/**
	 * 当前位置读一个double, 指针会移动
	 * @return
	 * @throws java.io.IOException
	 */
	public double readDouble()  throws IOException {
		byte[] bytes = new byte[8];
		this.read(bytes);
		return BytesUtils.getDouble(bytes);
	}
	
	/**
	 * 从指定位置度一个double, 指针不会移动
	 * @param position
	 * @return
	 * @throws java.io.IOException
	 */
	public double readDouble(long position)  throws IOException {
		byte[] bytes = new byte[8];
		this.read(bytes, position);
		return BytesUtils.getDouble(bytes);
	}
	
	/**
	 * 从当前位置读一个大小为size的字符串, 指针会移动
	 * @param size
	 * @return
	 * @throws java.io.IOException
	 */
	public String readUTF(int size) throws IOException {
		byte[] bytes = new byte[size];
		this.read(bytes);
		return new String(bytes);
	}
	
	/**
	 * 从指定位置position读一个大小为size的字符串, 指针不会移动
	 * @param size
	 * @param position
	 * @return
	 * @throws java.io.IOException
	 */
	public String readUTF(int size, long position) throws IOException {
		byte[] bytes = new byte[size];
		this.read(bytes, position);
		return new String(bytes);
	}
}
