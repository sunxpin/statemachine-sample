package com.baoxian.common.util;

import com.baoxian.common.bean.Page;

public class PageUtil {
	protected static final ThreadLocal<Page> LOCAL_PAGE = new ThreadLocal<Page>();

	public static Page getPage() {
		return LOCAL_PAGE.get();
	}

	public static void setPage(Page page) {
		LOCAL_PAGE.set(page);
	}

	public static void clearPage() {
		LOCAL_PAGE.remove();
	}

}
