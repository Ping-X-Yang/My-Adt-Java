package com.ping.adt.core.request.workbench.ui.model;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import com.ping.adt.core.request.workbench.ui.common.MyPluginContants;
import com.ping.adt.core.tools.MyAdtTools;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.tools.core.AbapCore;

//public class SettingModel {
//	
//	static SettingModel instance;
//	
//	
//	private String username = "";
//	private String fromDate = "00000000";
//	private String toDate = "9999121";
//	
//	SettingModel(){
//		Preferences preference = InstanceScope.INSTANCE.getNode(MyPluginContants.SETTING_ID);
//		setUsername(preference.get("username", ""));
//		setFromDate(preference.get("fromDate", "00000000"));
//		setToDate(preference.get("toDate", "9999121"));
//	}
//	
//	public void save() {
//		Preferences preference = InstanceScope.INSTANCE.getNode(MyPluginContants.SETTING_ID);
//		preference.put("username", getUsername());
//		preference.put("fromDate", getFromDate());
//		preference.put("toDate", getToDate());
//		try {
//			preference.flush();
//		} catch (BackingStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public static SettingModel get() {
//		if (instance == null) {
//			instance = new SettingModel();
//		}
//		return instance;
//	}
//	
//
//	public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}
//
//	public String getFromDate() {
//		return fromDate;
//	}
//
//	public void setFromDate(String fromDate) {
//		this.fromDate = fromDate;
//	}
//
//	public String getToDate() {
//		return toDate;
//	}
//
//	public void setToDate(String toDate) {
//		this.toDate = toDate;
//	}
//	
//}

public class SettingModel {
	static SettingModel instance;
	
	String preferenceId = "";

	private String userName;
	private boolean isCustomizing;
	private boolean isWorkbench;
	private boolean isTransportofCopies;
	private boolean isModifiable;
	private boolean isReleased;
	private boolean isFilterReleasedComboEnabled;
	private int filterReleasedComboSelection;
	private boolean isFromDateTextEnabled;
	private boolean isToDateTextEnabled;
	private String fromDateText;
	private String toDateText;
	private boolean isRequestObjectVisible;
//	   private String selectedTreeLevelsHierarchy;

	public SettingModel() {
		this.preferenceId = MyPluginContants.SETTING_ID + "." + MyAdtTools.getActiveProject().getName();
		Preferences preference = InstanceScope.INSTANCE.getNode(this.preferenceId);
		setUserName(preference.get("userName", MyAdtTools.getActiveAdtProject().getDestinationData().getUser()));
		setCustomizing(Boolean.valueOf(preference.get("isCustomizing", "true")));
		setWorkbench(Boolean.valueOf(preference.get("isWorkbench", "true")));
		setTransportofCopies(Boolean.valueOf(preference.get("isTransportofCopies", "true")));
		setModifiable(Boolean.valueOf(preference.get("isModifiable", "true")));
		setReleased(Boolean.valueOf(preference.get("isReleased", "true")));
		setFilterReleasedComboEnabled(Boolean.valueOf(preference.get("isFilterReleasedComboEnabled", "true")));
		setFilterReleasedComboSelection(Integer.valueOf(preference.get("filterReleasedComboSelection", "2")));	//默认查最近一个月的请求
		setFromDateTextEnabled(Boolean.valueOf(preference.get("isFromDateTextEnabled", "")));
		setToDateTextEnabled(Boolean.valueOf(preference.get("isToDateTextEnabled", "")));
		setFromDateText(preference.get("fromDateText", ""));
		setToDateText(preference.get("toDateText", ""));
		setRequestObjectVisible(Boolean.valueOf(preference.get("isRequestObjectVisible", "true")));
	}

	public void clearAll() {
		this.setUserName((String) null);// 33
		this.setCustomizing(false);// 34
		this.setWorkbench(false);// 35
		this.setTransportofCopies(false);// 36
		this.setModifiable(false);// 37
		this.setReleased(false);// 38
		this.setFilterReleasedComboEnabled(false);// 39
		this.setFilterReleasedComboSelection(-1);// 40
		this.setFromDateTextEnabled(false);// 41
		this.setFromDateText((String) null);// 42
		this.setToDateTextEnabled(false);// 43
		this.setToDateText((String) null);// 44
//	      this.setSelectedTreeLevelsHierarchy((String)null);// 45
	}// 47

	public String getUserName() {
		return this.userName;// 74
	}

	public void setUserName(String userName) {
		this.userName = userName;// 78
	}// 79

	public boolean isCustomizing() {
		return this.isCustomizing;// 82
	}

	public void setCustomizing(boolean isCustomizing) {
		this.isCustomizing = isCustomizing;// 86
	}// 87

	public boolean isWorkbench() {
		return this.isWorkbench;// 90
	}

	public void setWorkbench(boolean isWorkbench) {
		this.isWorkbench = isWorkbench;// 94
	}// 95

	public boolean isTransportofCopies() {
		return this.isTransportofCopies;// 98
	}

	public void setTransportofCopies(boolean isTransportofCopies) {
		this.isTransportofCopies = isTransportofCopies;// 102
	}// 103

	public boolean isModifiable() {
		return this.isModifiable;// 106
	}

	public void setModifiable(boolean isModifiable) {
		this.isModifiable = isModifiable;// 110
	}// 111

	public boolean isReleased() {
		return this.isReleased;// 114
	}

	public void setReleased(boolean isReleased) {
		this.isReleased = isReleased;// 118
	}// 119

	public boolean isFilterReleasedComboEnabled() {
		return this.isFilterReleasedComboEnabled;// 122
	}

	public void setFilterReleasedComboEnabled(boolean isFilterReleasedComboEnabled) {
		this.isFilterReleasedComboEnabled = isFilterReleasedComboEnabled;// 126
	}// 127

	public int getFilterReleasedComboSelection() {
		return this.filterReleasedComboSelection;// 130
	}

	public void setFilterReleasedComboSelection(int filterReleasedComboSelection) {
		this.filterReleasedComboSelection = filterReleasedComboSelection;// 134
	}// 135

	public boolean isFromDateTextEnabled() {
		return this.isFromDateTextEnabled;// 138
	}

	public void setFromDateTextEnabled(boolean isFromDateText) {
		this.isFromDateTextEnabled = isFromDateText;// 142
	}// 143

	public boolean isToDateTextEnabled() {
		return this.isToDateTextEnabled;// 146
	}

	public void setToDateTextEnabled(boolean isToDateText) {
		this.isToDateTextEnabled = isToDateText;// 150
	}// 151

	public String getFromDateText() {
		return this.fromDateText;// 154
	}

	public void setFromDateText(String fromDateText) {
		this.fromDateText = fromDateText;// 158
	}// 159

	public String getToDateText() {
		return this.toDateText;// 162
	}

	public void setToDateText(String toDateText) {
		this.toDateText = toDateText;// 166
	}// 167

//	   public String getSelectedTreeLevelsHierarchy() {
//	      return this.selectedTreeLevelsHierarchy;// 170
//	   }
//
//	   public void setSelectedTreeLevelsHierarchy(String selectedTreeLevelsHierarchy) {
//	      this.selectedTreeLevelsHierarchy = selectedTreeLevelsHierarchy;// 174
//	   }// 175

//	   public boolean equals(Object obj) {
//	      if (obj == this) {// 179
//	         return true;// 180
//	      } else if (!(obj instanceof TMPreferenceModel)) {// 182
//	         return false;// 183
//	      } else {
//	         TMPreferenceModel preferenceStoreModel = (TMPreferenceModel)obj;// 185
//	         if (preferenceStoreModel.getProjectName() != null) {// 186
//	            return preferenceStoreModel.getProjectName().equals(this.getProjectName());// 187
//	         } else {
//	            return this.getProjectName() == null;// 189
//	         }
//	      }
//	   }

//	   public int hashCode() {
//	      int result = 31 * (this.projectName == null ? 0 : this.projectName.hashCode());// 196
//	      return result;// 197
//	   }

	public static SettingModel get() {
		if (instance == null) {
			instance = new SettingModel();
		}
		return instance;
	}

	public void save() {
		Preferences preference = InstanceScope.INSTANCE.getNode(this.preferenceId);		
		preference.put("userName", getUserName());
		preference.put("isCustomizing", String.valueOf(isCustomizing()));
		preference.put("isWorkbench", String.valueOf(isWorkbench()));
		preference.put("isTransportofCopies", String.valueOf(isTransportofCopies()));
		preference.put("isModifiable", String.valueOf(isModifiable()));
		preference.put("isReleased", String.valueOf(isReleased()));
		preference.put("isFilterReleasedComboEnabled", String.valueOf(isFilterReleasedComboEnabled()));
		preference.put("filterReleasedComboSelection", String.valueOf(getFilterReleasedComboSelection()));
		preference.put("isFromDateTextEnabled", String.valueOf(isFromDateTextEnabled()));
		preference.put("isToDateTextEnabled", String.valueOf(isToDateTextEnabled()));
		preference.put("fromDateText", getFromDateText());
		preference.put("toDateText", getToDateText());
		preference.put("isRequestObjectVisible", String.valueOf(isRequestObjectVisible()));
		
		try {
			preference.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public boolean isRequestObjectVisible() {
		return isRequestObjectVisible;
	}

	public void setRequestObjectVisible(boolean isRequestObjectVisible) {
		this.isRequestObjectVisible = isRequestObjectVisible;
	}

}
