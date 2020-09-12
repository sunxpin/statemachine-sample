package com.baoxian.common.bean;

import com.baoxian.common.util.CastUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RowSet {
	private List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
	private String[] columnNames;
	private int rowIndex;
	private int colIndex;

	public RowSet(List<Map<String, Object>> result) {
		if (result == null || result.size() == 0)
			return;
		columnNames = result.get(0).keySet().toArray(new String[0]);
		this.result = result;
		rowIndex = result.size();
		colIndex = columnNames.length;
	}

	private void validate(int rowIndex, int colIndex) {
		if (result == null)
			throw new RuntimeException("结果集不存在");
		if (rowIndex < 0 || rowIndex > this.rowIndex || colIndex < 0 || colIndex > this.colIndex)
			throw new RuntimeException("(" + colIndex + ", " + colIndex + ")索引超出范围, 行范围: 0-" + this.colIndex
					+ ", 列范围: 0-" + this.colIndex);
	}

	private void validate(int rowIndex, String columnName) {
		if (result == null)
			throw new RuntimeException("结果集不存在");
		if (rowIndex < 0 || rowIndex > this.rowIndex)
			throw new RuntimeException("(" + colIndex + ")索引超出范围, 行范围: 0-" + this.colIndex);
		if (!result.get(0).containsKey(columnName))
			throw new RuntimeException("指定的列名没有找到:" + columnName);
	}

	public Object get(int rowIndex, int colIndex) {
		validate(rowIndex, colIndex);
		return result.get(rowIndex).get(columnNames[colIndex]);
	}

	public Object get(int rowIndex, String columnName) {
		validate(rowIndex, columnName);
		return result.get(rowIndex).get(columnName);
	}

	public String getString(int rowIndex, int colIndex) {
		validate(rowIndex, colIndex);
		return CastUtil.castToString(result.get(rowIndex).get(columnNames[colIndex]));
	}

	public String getString(int rowIndex, String columnName) {
		validate(rowIndex, columnName);
		return CastUtil.castToString(result.get(rowIndex).get(columnName));
	}

	public int getInt(int rowIndex, int colIndex) {
		validate(rowIndex, colIndex);
		return CastUtil.castToInteger(result.get(rowIndex).get(columnNames[colIndex]));
	}

	public int getInt(int rowIndex, String columnName) {
		validate(rowIndex, columnName);
		return CastUtil.castToInteger(result.get(rowIndex).get(columnName));
	}

	public long getLong(int rowIndex, int colIndex) {
		validate(rowIndex, colIndex);
		return CastUtil.castToLong(result.get(rowIndex).get(columnNames[colIndex]));
	}

	public long getLong(int rowIndex, String columnName) {
		validate(rowIndex, columnName);
		return CastUtil.castToLong(result.get(rowIndex).get(columnName));
	}

	public double getDouble(int rowIndex, int colIndex) {
		validate(rowIndex, colIndex);
		return CastUtil.castToDouble(result.get(rowIndex).get(columnNames[colIndex]));
	}

	public double getDouble(int rowIndex, String columnName) {
		validate(rowIndex, columnName);
		return CastUtil.castToDouble(result.get(rowIndex).get(columnName));
	}

	public Date getDate(int rowIndex, int colIndex) {
		validate(rowIndex, colIndex);
		return CastUtil.castToDate(result.get(rowIndex).get(columnNames[colIndex]));
	}

	public Date getDate(int rowIndex, String columnName) {
		validate(rowIndex, columnName);
		return CastUtil.castToDate(result.get(rowIndex).get(columnName));
	}

	public int getRowCount() {
		return this.result.size();
	}

	public boolean isEmpty() {
		return columnNames == null;
	}

	public boolean isNotEmpty() {
		return columnNames != null;
	}

	public int getColCount() {
		if (columnNames == null)
			return 0;
		return this.columnNames.length;
	}

	public Object[] getRow(int rowIndex) {
		if (rowIndex < 0 || rowIndex > this.rowIndex)
			throw new RuntimeException("(" + colIndex + ")索引超出范围, 行范围: 0-" + this.colIndex);
		return result.get(rowIndex).values().toArray();
	}
}
