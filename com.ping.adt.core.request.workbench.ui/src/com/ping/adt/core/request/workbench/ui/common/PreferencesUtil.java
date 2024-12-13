package com.ping.adt.core.request.workbench.ui.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PreferencesUtil {
	public static String calculateAndFormatDate(Date date, int valueToBeSubtracted) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		if (date == null) {
			date = new Date();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(5, valueToBeSubtracted);
		String formattedDate = simpleDateFormat.format(calendar.getTime());
		return formattedDate;
	}

	public static String calculateAndFormatDate(int year, int month, int day) {
		++month;
		String dateInStringFormat = Integer.toString(year);
		dateInStringFormat = dateInStringFormat + (month < 10 ? "0" + month : Integer.toString(month));
		dateInStringFormat = dateInStringFormat + (day < 10 ? "0" + day : Integer.toString(day));
		return dateInStringFormat;
	}

	public static Date convertToDate(int year, int month, int day) throws ParseException {
		String dateInStringFormat = calculateAndFormatDate(year, month, day);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		return simpleDateFormat.parse(dateInStringFormat);
	}

	public static String getYearFromDate(String dateInStringFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		Date date = sdf.parse(dateInStringFormat);
		SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ENGLISH);
		return df.format(date);
	}

	public static String getMonthFromDate(String dateInStringFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		Date date = sdf.parse(dateInStringFormat);
		SimpleDateFormat df = new SimpleDateFormat("MM", Locale.ENGLISH);
		return df.format(date);
	}

	public static String getDayFromDate(String dateInStringFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		Date date = sdf.parse(dateInStringFormat);
		SimpleDateFormat df = new SimpleDateFormat("dd", Locale.ENGLISH);
		return df.format(date);
	}

	public static boolean isDateGreaterThan1968(Date date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date firstAcceptedDate = sdf.parse("1968-01-01");
		return firstAcceptedDate.before(date);
	}

	public static boolean isDateGreaterThanToday(Date date) throws ParseException {
		Date today = new Date();
		return date.before(today);
	}

//	public static List<TMPreferenceTreeNode> convertFacetsOrderToTreeConfigurationLevels(String facetsOrder,
//			IFacets facets) {
//		ITMFacetsService facetsService = TMViewServiceFactory.createTMFacetsService();
//		String[] levels = facetsOrder.split(",");
//		List<TMPreferenceTreeNode> treeConfigurationLevels = new ArrayList();
//		String[] var8 = levels;
//		int var7 = levels.length;
//
//		for (int var6 = 0; var6 < var7; ++var6) {
//			String level = var8[var6];
//			if (level.strip() != "repository") {
//				String domainName = facetsService.getDomainNameFromFacetID(level.strip(), facets);
//				String displayname = facetsService.getDisplayNameFromFacetID(level.strip(), facets);
//				if (displayname != null) {
//					treeConfigurationLevels.add(new TMPreferenceTreeNode(domainName, level.strip(), displayname));
//				}
//			}
//		}
//
//		return treeConfigurationLevels;
//	}
}
