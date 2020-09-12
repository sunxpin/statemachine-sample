package com.baoxian.common.bean;

import java.util.Map;

public class Page {
	private int currentPage;
	private int pageRecords;
	private int pageTotal;
	private int totalRecords;
	private int startRecord;
	private String customText;

	public Page() {
		pageRecords = 20;
		currentPage = 1;
		totalRecords = -1;
		pageTotal = -1;
		startRecord = 0;
	}

	public Page(Map<String, ?> map) {
		this();
		setCurrentPage(String.valueOf(map.get("currentPage")));
		setPageRecords(String.valueOf(map.get("pageRecords")));
		setTotalRecords(String.valueOf(map.get("totalRecords")));
	}

	private void init() {
		this.startRecord = (this.currentPage - 1) * pageRecords;
		if (totalRecords >= 0) {
			if (this.totalRecords % pageRecords == 0)
				pageTotal = this.totalRecords / pageRecords;
			else
				pageTotal = this.totalRecords / pageRecords + 1;
		}
	}

	public int getStartRecord() {
		return startRecord;
	}

	public void setStartRecord(int startRecord) {
		if (startRecord < 0 || startRecord > totalRecords)
			return;
		this.startRecord = startRecord;
	}

	public void setStartRecord(String startRecord) {
		if (startRecord != null && startRecord.matches("^\\d+$"))
			this.setStartRecord(Integer.parseInt(startRecord));
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		if (currentPage <= 0)
			this.currentPage = 0;
		if(pageTotal >=0 && currentPage > pageTotal)
			this.currentPage = pageTotal - 1;
		this.currentPage = currentPage;
		init();
	}

	public void setCurrentPage(String currentPage) {
		if (currentPage != null && currentPage.matches("^\\d+$"))
			this.setCurrentPage(Integer.parseInt(currentPage));
	}

	public int getPageRecords() {
		return pageRecords;
	}

	public void setPageRecords(int pageRecords) {
		if (pageRecords <= 0)
			return;
		this.pageRecords = pageRecords;
		init();
	}

	public void setPageRecords(String pageRecords) {
		if (pageRecords != null && pageRecords.matches("^\\d+$"))
			this.setPageRecords(Integer.parseInt(pageRecords));
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		if (pageTotal < 0)
			return;
		this.pageTotal = pageTotal;
	}

	public void setPageTotal(String pageTotal) {
		if (pageTotal != null && pageTotal.matches("^\\d+$"))
			this.setPageTotal(Integer.parseInt(pageTotal));
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
		init();
	}

	public void setTotalRecords(String totalRecords) {
		if (totalRecords != null && totalRecords.matches("^-?\\d+$"))
			this.setTotalRecords(Integer.parseInt(totalRecords));
	}

	public String getCustomText() {
		return customText;
	}

	public void setCustomText(String customText) {
		this.customText = customText;
	}
}
