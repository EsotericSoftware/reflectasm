package com.esotericsoftware.reflectasm;

import sun.misc.Unsafe;

import java.lang.reflect.*;

@SuppressWarnings("restriction")
class FieldAccessUnsafe extends FieldAccess {
	FieldAccessUnsafe(Class<?> clazz, Unsafe unsafe)
	{
		this.unsafe = unsafe;
		
		Field[] fields = clazz.getFields();
		
		super.fieldNames = new String[fields.length];
		super.fieldTypes = new Class<?>[fields.length];
		this.addresses = new long[fields.length];
		
		for(int i = 0; i < fields.length; i++)
		{
			super.fieldNames[i] = fields[i].getName();
			super.fieldTypes[i] = fields[i].getType();
			this.addresses[i] = unsafe.objectFieldOffset(fields[i]);
		}
	}
	
	@Override
	public void set(Object instance, int fieldIndex, Object value) 
	{
		unsafe.putObject(instance, addresses[fieldIndex], value);
	}

	@Override
	public void setBoolean(Object instance, int fieldIndex, boolean value) 
	{
		unsafe.putBoolean(instance, addresses[fieldIndex], value);
	}

	@Override
	public void setByte(Object instance, int fieldIndex, byte value) 
	{
		unsafe.putByte(instance, addresses[fieldIndex], value);
	}

	@Override
	public void setShort(Object instance, int fieldIndex, short value) 
	{
		unsafe.putShort(instance, addresses[fieldIndex], value);
	}

	@Override
	public void setInt(Object instance, int fieldIndex, int value) 
	{
		unsafe.putInt(instance, addresses[fieldIndex], value);
	}

	@Override
	public void setLong(Object instance, int fieldIndex, long value) 
	{
		unsafe.putLong(instance, addresses[fieldIndex], value);
	}

	@Override
	public void setDouble(Object instance, int fieldIndex, double value) 
	{
		unsafe.putDouble(instance, addresses[fieldIndex], value);
	}

	@Override
	public void setFloat(Object instance, int fieldIndex, float value) 
	{
		unsafe.putFloat(instance, addresses[fieldIndex], value);
	}

	@Override
	public void setChar(Object instance, int fieldIndex, char value) 
	{
		unsafe.putChar(instance, addresses[fieldIndex], value);
	}

	@Override
	public Object get(Object instance, int fieldIndex) 
	{
		return unsafe.getObject(instance, addresses[fieldIndex]);
	}

	@Override
	public String getString(Object instance, int fieldIndex) 
	{
		return (String)unsafe.getObject(instance, addresses[fieldIndex]);
	}

	@Override
	public char getChar(Object instance, int fieldIndex) 
	{
		return unsafe.getChar(instance, addresses[fieldIndex]);
	}

	@Override
	public boolean getBoolean(Object instance, int fieldIndex) 
	{
		return unsafe.getBoolean(instance, addresses[fieldIndex]);
	}

	@Override
	public byte getByte(Object instance, int fieldIndex) 
	{
		return unsafe.getByte(instance, addresses[fieldIndex]);
	}

	@Override
	public short getShort(Object instance, int fieldIndex) 
	{
		return unsafe.getShort(instance, addresses[fieldIndex]);
	}

	@Override
	public int getInt(Object instance, int fieldIndex) 
	{
		return unsafe.getInt(instance, addresses[fieldIndex]);
	}

	@Override
	public long getLong(Object instance, int fieldIndex) 
	{
		return unsafe.getLong(instance, addresses[fieldIndex]);
	}

	@Override
	public double getDouble(Object instance, int fieldIndex) 
	{
		return unsafe.getDouble(instance, addresses[fieldIndex]);
	}

	@Override
	public float getFloat(Object instance, int fieldIndex) 
	{
		return unsafe.getFloat(instance, addresses[fieldIndex]);
	}
	
	long[] addresses;

	private final Unsafe unsafe;
}
