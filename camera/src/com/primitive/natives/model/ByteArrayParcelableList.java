package com.primitive.natives.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ByteArrayParcelableList implements Parcelable
{
	public static final Parcelable.Creator<ByteArrayParcelableList> CREATOR = new Parcelable.Creator<ByteArrayParcelableList>()
	{
		public ByteArrayParcelableList createFromParcel(Parcel src) {
			List<byte[]> l = new ArrayList<byte[]>();
			src.readList(l, null);
			return new ByteArrayParcelableList(l);
		}
		public ByteArrayParcelableList[] newArray(int size) {
			return new ByteArrayParcelableList[size];
		}
	};

	private List<byte[]> list;

	/**
	 *
	 * @param list
	 */
	public ByteArrayParcelableList(List<byte[]> list)
	{
		this.list = list;
	}

	/**
	 *
	 * @return
	 */
	public List<byte[]> getList()
	{
		return list;
	}

	/**
	 *
	 * @param list
	 */
	public void setList(List<byte[]> list)
	{
		this.list = list;
	}

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeList(list);
	}
}