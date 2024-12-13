package com.ping.adt.core.request.workbench.ui.widgets;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.ping.adt.core.request.workbench.ui.common.PreferencesUtil;
import com.ping.adt.core.request.workbench.ui.model.SettingModel;
import com.ping.adt.core.tools.MyAdtTools;
import com.sap.adt.tools.core.ui.system.UserContentAssistProcessor;
import com.sap.adt.tools.core.ui.userinfo.AdtUserServiceUIFactory;
import com.sap.adt.tools.core.ui.userinfo.IAdtUserServiceUI;
import com.sap.adt.util.satomparser.ParseException;
import com.sap.adt.util.ui.contentassist.FieldHistoryProposalProvider;
import com.sap.adt.util.ui.swt.AdtSWTUtilFactory;

import jakarta.inject.Inject;
import jakarta.inject.Named;

public class SettingDialog extends TitleAreaDialog {
	   private Composite root;
	   Label abapProjectLabel;
	   TextViewer abapProjectField;
	   Button abapProjectBrowseButton;
	   private Label userNameLabel;
	   TextViewer userNameTextField;
	   Button userNameBrowseButton;
	   Button customizingRequests;
	   Button workbenchRequests;
	   Button transportofCopies;
	   Button modifiable;
	   Button released;
	   Combo filterReleasedDateCombo;
	   private volatile UserContentAssistProcessor assist = null;
	   Label fromDateLabel;
	   Label toDateLabel;
	   Label requestType;
	   Label requestStatus;
	   DateTime fromDateTime;
	   DateTime toDateTime;
	   @Inject
	   @Named("activeProject")
	   IProject project;
//	   IProjectInfo projectInfo;
	   private StyledText userNameStyledText;
	   boolean isSystemLoggedOn;
	   private String projectName;
	   private boolean isValidProject;
	   private String userName;
	   private boolean isCustomizingRequests;
	   private boolean isWorkbenchRequests;
	   private boolean isTransportOfCopies;
	   private boolean isModifiable;
	   private boolean isReleased;
	   private String fromReleasedDate;
	   private String toReleasedDate;
	   private Boolean abapProjectError;
	   private boolean isCreationSupportedInTheProject = true;
	   private int optionNumberForfilterReleasedDateCombo = -1;
	   private String destinationId;
	   SettingModel preferenceStoreModel;
	   public String currentProjectName;
	   String oldProjectName;
	   boolean isBackendConfigurationStoreSupported;
	   boolean isTreeHierarchyCustomizationSupported;

	   public SettingDialog(Shell parentShell) {
	      super(parentShell);
	      this.setHelpAvailable(false);
	      this.setShellStyle(this.getShellStyle() | 16);
	      this.project = MyAdtTools.getActiveProject();
	      this.preferenceStoreModel = new SettingModel();	// SettingModel.get();
	      this.setUserName(preferenceStoreModel.getUserName());
	   }

	   public void create() {
	      super.create();
//	      this.setTitle(Messages.SettingDialog_Title_XTIT);
//	      String descriptionMessage = NLS.bind(Messages.TMPreferencesDailog_Description_Message_XTIT, this.project.getName());
//	      this.setMessage(descriptionMessage, 1);
	   }

	   protected void configureShell(Shell newShell) {
	      super.configureShell(newShell);
	      newShell.setText("设置");
	      newShell.setLocation(500, 200);
	   }

	   protected Control createDialogArea(Composite parent) {
	      this.root = new Composite(parent, 0);
	      this.root.setFont(parent.getFont());
	      GridLayout gridLayout = new GridLayout(4, false);
	      this.root.setLayout(gridLayout);
	      GridData gridData = new GridData(4, 4, true, true, 3, 1);
	      this.root.setLayoutData(gridData);
	      this.addUserNameLabel(this.root);
	      this.addUserNameTextField(this.root);
	      this.addUserNameBrowseButton(this.root);
	      this.addRequestTypeGroup();
	      this.addRequestStatusGroup();
	      return this.root;
	   }

	   private void addRequestStatusGroup() {
	      Group requestStatusGroup = new Group(this.root, 0);
	      requestStatusGroup.setText("请求状态组");
	      requestStatusGroup.setLayout(new GridLayout(4, false));
	      GridDataFactory.fillDefaults().indent(0, 5).grab(true, false).span(4, 1).applyTo(requestStatusGroup);
	      this.addModifiableCheckBox(requestStatusGroup);
	      this.addReleasedCheckBox(requestStatusGroup);
	      this.addFilterReleasedCombo(requestStatusGroup);
	      Label blankLabel1 = new Label(requestStatusGroup, 0);
	      GridData gdblankLabel1 = new GridData(16384, 16777216, true, false);
	      blankLabel1.setLayoutData(gdblankLabel1);
	      Label blankLabel2 = new Label(requestStatusGroup, 0);
	      GridData gdblankLabel2 = new GridData(16384, 16777216, false, false);
	      blankLabel2.setLayoutData(gdblankLabel2);
	      Composite fromDateComposite = this.createFromDateComposite(requestStatusGroup);
	      this.addFromDateLabel(fromDateComposite);

	      try {
	         this.addFromDateText(fromDateComposite);
	      } catch (Exception var10) {
	         var10.printStackTrace();
	      }

	      Composite toDateComposite = this.createToDateComposite(requestStatusGroup);
	      this.addToDateLabel(toDateComposite);

	      try {
	         this.addToDateText(toDateComposite);
	      } catch (Exception var9) {
	         var9.printStackTrace();
	      }

	      requestStatusGroup.setTabList(new Control[]{this.modifiable, this.released, this.filterReleasedDateCombo, blankLabel1, blankLabel2, fromDateComposite, toDateComposite});
	      requestStatusGroup.pack();
	   }

	   private void addRequestTypeGroup() {
	      Group requestTypeGroup = new Group(this.root, 0);
	      requestTypeGroup.setText("请求类型组");
	      requestTypeGroup.setLayout(new GridLayout(4, false));
	      GridDataFactory.fillDefaults().indent(0, 5).grab(true, false).span(4, 1).applyTo(requestTypeGroup);
	      this.addCustomizingRequestsCheckBox(requestTypeGroup);
	      this.addWorkBenchRequestsCheckBox(requestTypeGroup);
	      this.addTransportofCopiesCheckBox(requestTypeGroup);
	      requestTypeGroup.setTabList(new Control[]{this.customizingRequests, this.workbenchRequests, this.transportofCopies});
	      requestTypeGroup.pack();
	   }

	   private void addToDateText(Composite toDateComposite) throws Exception {
	      this.toDateTime = new DateTime(toDateComposite, 36);
	      GridDataFactory.swtDefaults().align(16384, 16777216).grab(true, false).applyTo(this.toDateTime);
	      this.toDateTime.setEnabled(this.preferenceStoreModel.isToDateTextEnabled());
	      if (this.preferenceStoreModel.getFilterReleasedComboSelection() == 3 && this.preferenceStoreModel.getToDateText() != null && !this.preferenceStoreModel.getToDateText().isEmpty()) { 
	         int year = Integer.parseInt(PreferencesUtil.getYearFromDate(this.preferenceStoreModel.getToDateText()));
	         int month = Integer.parseInt(PreferencesUtil.getMonthFromDate(this.preferenceStoreModel.getToDateText())) - 1;
	         int day = Integer.parseInt(PreferencesUtil.getDayFromDate(this.preferenceStoreModel.getToDateText()));
	         this.toDateTime.setDate(year, month, day);
	         this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate(this.toDateTime.getYear(), this.toDateTime.getMonth(), this.toDateTime.getDay())); 
	      }

	      this.toDateTime.addSelectionListener(new SelectionListener() {
	         public void widgetSelected(SelectionEvent e) {
	            SettingDialog.this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate(SettingDialog.this.toDateTime.getYear(), SettingDialog.this.toDateTime.getMonth(), SettingDialog.this.toDateTime.getDay())); 
	            SettingDialog.this.setErrorMessage((String)null);
	            SettingDialog.this.enableApplyFilterButton(true);
	         }

	         public void widgetDefaultSelected(SelectionEvent e) {
	         }
	      });
	   }

	   private void addToDateLabel(Composite toDateComposite) {
	      this.toDateLabel = new Label(toDateComposite, 0);
	      this.toDateLabel.setText("结束日期");
	      GridDataFactory.fillDefaults().align(16384, 16777216).applyTo(this.toDateLabel);
	   }

	   private Composite createToDateComposite(Composite group) {
	      Composite toDateComposite = new Composite(group, 0);
	      toDateComposite.setLayout(new GridLayout(2, false));
	      GridData toDateCompGridData = new GridData(4, 4, true, true);
	      toDateComposite.setLayoutData(toDateCompGridData);
	      return toDateComposite;
	   }

	   private void addFromDateText(Composite fromDateComposite) throws Exception {
	      this.fromDateTime = new DateTime(fromDateComposite, 36);
	      GridDataFactory.swtDefaults().align(16384, 16777216).grab(true, false).applyTo(this.fromDateTime);
	      this.fromDateTime.setEnabled(this.preferenceStoreModel.isFromDateTextEnabled());
	      if (this.preferenceStoreModel.getFilterReleasedComboSelection() == 3 && this.preferenceStoreModel.getFromDateText() != null && !this.preferenceStoreModel.getFromDateText().isEmpty()) { 
	         int year = Integer.parseInt(PreferencesUtil.getYearFromDate(this.preferenceStoreModel.getFromDateText()));
	         int month = Integer.parseInt(PreferencesUtil.getMonthFromDate(this.preferenceStoreModel.getFromDateText())) - 1;
	         int day = Integer.parseInt(PreferencesUtil.getDayFromDate(this.preferenceStoreModel.getFromDateText()));
	         this.fromDateTime.setDate(year, month, day);
	         this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate(this.fromDateTime.getYear(), this.fromDateTime.getMonth(), this.fromDateTime.getDay())); 
	      }

	      this.fromDateTime.addSelectionListener(new SelectionAdapter() {
	         public void widgetSelected(SelectionEvent e) {
	        	 SettingDialog.this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate(SettingDialog.this.fromDateTime.getYear(), SettingDialog.this.fromDateTime.getMonth(), SettingDialog.this.fromDateTime.getDay())); 
	        	 SettingDialog.this.setErrorMessage((String)null);
	        	 SettingDialog.this.enableApplyFilterButton(true);
	         }
	      });
	   }

	   private void addFromDateLabel(Composite fromDateComposite) {
	      this.fromDateLabel = new Label(fromDateComposite, 0);
	      this.fromDateLabel.setText("开始日期");
	      GridDataFactory.fillDefaults().align(16384, 16777216).applyTo(this.fromDateLabel);
	   }

	   private Composite createFromDateComposite(Composite group) {
	      Composite fromDateComposite = new Composite(group, 0);
	      fromDateComposite.setLayout(new GridLayout(2, false));
	      GridDataFactory.fillDefaults().grab(true, true).applyTo(fromDateComposite);
	      GridData fromDateCompGridData = new GridData(4, 4, true, true);
	      fromDateComposite.setLayoutData(fromDateCompGridData);
	      return fromDateComposite;
	   }

	   private void addFilterReleasedCombo(Composite composite) {
	      this.filterReleasedDateCombo = new Combo(composite, 8);
	      String[] filterCriteria = new String[]{"到昨天", "最近两周", "最近一个月", "自由选择"};  
	      this.filterReleasedDateCombo.setItems(filterCriteria);
	      if (this.isReleased()) {
	         this.filterReleasedDateCombo.setEnabled(true);
	         this.filterReleasedDateCombo.select(this.preferenceStoreModel.getFilterReleasedComboSelection());
	         this.setOptionNumberForFilterReleasedDateCombo(this.preferenceStoreModel.getFilterReleasedComboSelection());
	      } else {
	         this.filterReleasedDateCombo.setEnabled(false);
	         this.filterReleasedDateCombo.select(1);
	         this.setOptionNumberForFilterReleasedDateCombo(1);
	      }

	      this.calculateDatesBasedOnComboSelection();
	      GridData gdfilterReleasedDateCombo = new GridData(4, 16777216, true, false, 2, 1);
	      this.filterReleasedDateCombo.setLayoutData(gdfilterReleasedDateCombo);
	      this.filterReleasedDateCombo.addSelectionListener(new SelectionAdapter() {
	         public void widgetSelected(SelectionEvent event) {
	        	 SettingDialog.this.fromDateTime.setEnabled(false);
	        	 SettingDialog.this.toDateTime.setEnabled(false);
	            if (SettingDialog.this.filterReleasedDateCombo.getSelectionIndex() == 0) {
	            	SettingDialog.this.setOptionNumberForFilterReleasedDateCombo(0);
	            	SettingDialog.this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, 0));
	            	SettingDialog.this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, -1));

	               try {
	                  if (SettingDialog.this.validateSelectedPreferences()) {
	                	  SettingDialog.this.enableApplyFilterButton(true);
	                  }
	               } catch (ParseException var5) {
	                  var5.printStackTrace();
	               }
	            } else if (1 == SettingDialog.this.filterReleasedDateCombo.getSelectionIndex()) { 
	            	SettingDialog.this.setOptionNumberForFilterReleasedDateCombo(1);
	            	SettingDialog.this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, 0));
	            	SettingDialog.this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, -14));

	               try {
	                  if (SettingDialog.this.validateSelectedPreferences()) {
	                     SettingDialog.this.enableApplyFilterButton(true);
	                  }
	               } catch (ParseException var4) {
	                  var4.printStackTrace();
	               }
	            } else if (2 == SettingDialog.this.filterReleasedDateCombo.getSelectionIndex()) { 
	               SettingDialog.this.setOptionNumberForFilterReleasedDateCombo(2);
	               SettingDialog.this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, 0));
	               SettingDialog.this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, -28));

	               try {
	                  if (SettingDialog.this.validateSelectedPreferences()) {
	                     SettingDialog.this.enableApplyFilterButton(true);
	                  }
	               } catch (ParseException var3) {
	                  var3.printStackTrace();
	               }
	            } else if (3 == SettingDialog.this.filterReleasedDateCombo.getSelectionIndex()) {
	               SettingDialog.this.setOptionNumberForFilterReleasedDateCombo(3);
	               SettingDialog.this.setDatesBasedOnCalendarWidgetSelection();
	               SettingDialog.this.fromDateTime.setEnabled(true);
	               SettingDialog.this.toDateTime.setEnabled(true);
	            }

	         }
	      });
	   }

	   private void calculateDatesBasedOnComboSelection() {
	      if (this.filterReleasedDateCombo != null) {
	         if (this.filterReleasedDateCombo.getSelectionIndex() == 0) {
	            this.setOptionNumberForFilterReleasedDateCombo(0);
	            this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, 0));
	            this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, -1));
	         } else if (1 == this.filterReleasedDateCombo.getSelectionIndex()) {
	            this.setOptionNumberForFilterReleasedDateCombo(1);
	            this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, 0));
	            this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, -14));
	         } else if (2 == this.filterReleasedDateCombo.getSelectionIndex()) {
	            this.setOptionNumberForFilterReleasedDateCombo(2);
	            this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, 0));
	            this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate((Date)null, -28));
	         } else if (3 == this.filterReleasedDateCombo.getSelectionIndex()) {
	            this.setOptionNumberForFilterReleasedDateCombo(3);
	            this.setDatesBasedOnCalendarWidgetSelection();
	         }
	      }

	   }

	   private void setDatesBasedOnCalendarWidgetSelection() {
	      if (this.fromDateTime != null) {
	         this.setFromReleasedDate(PreferencesUtil.calculateAndFormatDate(this.fromDateTime.getYear(), this.fromDateTime.getMonth(), this.fromDateTime.getDay())); 
	      } else {
	         this.setFromReleasedDate((String)null);
	      }

	      if (this.toDateTime != null) {
	         this.setToReleasedDate(PreferencesUtil.calculateAndFormatDate(this.toDateTime.getYear(), this.toDateTime.getMonth(), this.toDateTime.getDay())); 
	      } else {
	         this.setToReleasedDate((String)null);
	      }

	   }

	   private void addReleasedCheckBox(Composite composite) {
	      this.released = new Button(composite, 32);
	      this.released.setText("已释放");
	      this.released.setSelection(this.preferenceStoreModel.isReleased());
	      this.setReleased(this.preferenceStoreModel.isReleased());
	      GridData gdreleased = new GridData();
	      this.released.setLayoutData(gdreleased);
	      this.released.addSelectionListener(new SelectionAdapter() {
	         public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button)event.getSource();
	            if (!btn.getSelection()) {
	               SettingDialog.this.filterReleasedDateCombo.setEnabled(false);
	               SettingDialog.this.fromDateTime.setEnabled(false);
	               SettingDialog.this.toDateTime.setEnabled(false);
	            } else {
	               SettingDialog.this.filterReleasedDateCombo.setEnabled(true);
	               if (SettingDialog.this.getOptionNumberForfilterReleasedDateCombo() == 3) {
	                  SettingDialog.this.fromDateTime.setEnabled(true);
	                  SettingDialog.this.toDateTime.setEnabled(true);
	               }
	            }

	            SettingDialog.this.setReleased(btn.getSelection());

	            try {
	               if (SettingDialog.this.validateSelectedPreferences()) {
	                  SettingDialog.this.enableApplyFilterButton(true);
	               }
	            } catch (ParseException var4) {
	               var4.printStackTrace();
	            }

	         }
	      });
	   }

	   private void addModifiableCheckBox(Composite composite) {
	      this.modifiable = new Button(composite, 32);
	      this.modifiable.setText("可修改");
	      this.modifiable.setSelection(this.preferenceStoreModel.isModifiable());
	      this.setModifiable(this.preferenceStoreModel.isModifiable());
	      GridData gdmodifiable = new GridData(4, 4, true, false, 4, 1);
	      this.modifiable.setLayoutData(gdmodifiable);
	      this.modifiable.addSelectionListener(new SelectionAdapter() {
	         public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button)event.getSource();
	            SettingDialog.this.setModifiable(btn.getSelection());

	            try {
	               if (SettingDialog.this.validateSelectedPreferences()) {
	                  SettingDialog.this.enableApplyFilterButton(true);
	               }
	            } catch (ParseException var4) {
	               var4.printStackTrace();
	            }

	         }
	      });
	   }

	   private void addWorkBenchRequestsCheckBox(Composite composite) {
	      this.workbenchRequests = new Button(composite, 32);
	      this.workbenchRequests.setText("工作台请求");
	      this.workbenchRequests.setSelection(this.preferenceStoreModel.isWorkbench());
	      this.setWorkbenchRequests(this.preferenceStoreModel.isWorkbench());
	      GridData gdworkbenchRequests = new GridData(4, 4, true, false, 4, 1);
	      this.workbenchRequests.setLayoutData(gdworkbenchRequests);
	      this.workbenchRequests.addSelectionListener(new SelectionAdapter() {
	         public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button)event.getSource();
	            SettingDialog.this.setWorkbenchRequests(btn.getSelection());

	            try {
	               if (SettingDialog.this.validateSelectedPreferences()) {
	                  SettingDialog.this.enableApplyFilterButton(true);
	               }
	            } catch (ParseException var4) {
	               var4.printStackTrace();
	            }

	         }
	      });
	   }

	   private void addTransportofCopiesCheckBox(Composite composite) {
	      this.transportofCopies = new Button(composite, 32);
	      this.transportofCopies.setText("副本请求");
	      this.transportofCopies.setSelection(this.preferenceStoreModel.isTransportofCopies());
	      this.setTransportOfCopies(this.preferenceStoreModel.isTransportofCopies());
	      GridData gdtocRequests = new GridData(4, 4, true, false, 4, 1);
//	      if (this.tmViewService.isTOCSupported(this.destinationId)) {
//	         gdtocRequests.exclude = false;
//	      } else {
//	         gdtocRequests.exclude = true;
//	      }

	      this.transportofCopies.setLayoutData(gdtocRequests);
	      this.transportofCopies.addSelectionListener(new SelectionAdapter() {
	         public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button)event.getSource();
	            SettingDialog.this.setTransportOfCopies(btn.getSelection());

	            try {
	               if (SettingDialog.this.validateSelectedPreferences()) {
	                  SettingDialog.this.enableApplyFilterButton(true);
	               }
	            } catch (ParseException var4) {
	               var4.printStackTrace();
	            }

	         }
	      });
	   }

	   private void addCustomizingRequestsCheckBox(Composite composite) {
	      this.customizingRequests = new Button(composite, 32);
	      this.customizingRequests.setText("定制请求");
	      this.customizingRequests.setSelection(this.preferenceStoreModel.isCustomizing());
	      this.setCustomizingRequests(this.preferenceStoreModel.isCustomizing());
	      GridData gdcustomizingRequests = new GridData(4, 4, true, false, 4, 1);
	      this.customizingRequests.setLayoutData(gdcustomizingRequests);
	      this.customizingRequests.addSelectionListener(new SelectionAdapter() {
	         public void widgetSelected(SelectionEvent event) {
	            Button btn = (Button)event.getSource();
	            SettingDialog.this.setCustomizingRequests(btn.getSelection());
	            SettingDialog.this.preferenceStoreModel.setCustomizing(btn.getSelection());

	            try {
	               if (SettingDialog.this.validateSelectedPreferences()) {
	                  SettingDialog.this.enableApplyFilterButton(true);
	               }
	            } catch (ParseException var4) {
	               var4.printStackTrace();
	            }

	         }
	      });
	   }

	   private void addUserNameBrowseButton(Composite composite) {
	      this.userNameBrowseButton = new Button(composite, 8);
	      this.userNameBrowseButton.setText("浏览...");
	      GridData gdUserNameBrowseButton = new GridData(4, 4, false, false);
	      this.userNameBrowseButton.setLayoutData(gdUserNameBrowseButton);
	      AdtSWTUtilFactory.getOrCreateSWTUtil().setButtonWidthHint(this.userNameBrowseButton);
	      this.userNameBrowseButton.addListener(13, new Listener() {
	         public void handleEvent(Event event) {
	            IAdtUserServiceUI adtUserServiceUI = AdtUserServiceUIFactory.createAdtUserServiceUI();
	            if (SettingDialog.this.project != null) {
	               String[] selectedUsers = adtUserServiceUI.openUserNameSelectionDialog(SettingDialog.this.getShell(), false, SettingDialog.this.project, SettingDialog.this.userNameTextField.getTextWidget().getText()); 
	               if (selectedUsers != null) {
	            	   SettingDialog.this.addUserNameToTextField(selectedUsers[0]);
	               } else {
	            	   SettingDialog.this.userNameBrowseButton.setFocus();
	               }
	            }

	         }
	      });
	   }

	   private void addUserNameTextField(Composite composite) {
	      this.userNameTextField = new TextViewer(composite, 2052);
	      AdtSWTUtilFactory.getOrCreateSWTUtil().addTextEditMenu(this.userNameTextField.getControl());
	      this.userNameTextField.getTextWidget().setFont(composite.getFont());
	      GridData gdUserNameTextField = new GridData(4, 16777216, true, false, 2, 1);
	      this.userNameTextField.getTextWidget().setLayoutData(gdUserNameTextField);
	      FieldHistoryProposalProvider.create(this.userNameTextField.getTextWidget(), SettingDialog.class.getName() + ".user", true);
	      this.assist = new UserContentAssistProcessor(this.userNameTextField);
	      this.assist.setProject(this.project);
	      this.userNameStyledText = this.userNameTextField.getTextWidget();
	      this.userNameStyledText.setTextLimit(12);
	      this.userNameStyledText.setText(this.getUserName());
	      this.userNameStyledText.addModifyListener(new ModifyListener() {
	         public void modifyText(ModifyEvent e) {
	            SettingDialog.this.setUserName(SettingDialog.this.userNameStyledText.getText().trim().toUpperCase(Locale.ENGLISH));

	            try {
	               if (SettingDialog.this.validateSelectedPreferences()) {
	            	   SettingDialog.this.enableApplyFilterButton(true);
	               }
	            } catch (ParseException var3) {
	               var3.printStackTrace();
	            }

	         }
	      });
	   }

	   private void addUserNameLabel(Composite composite) {
	      this.userNameLabel = new Label(composite, 0);
	      this.userNameLabel.setText("用户名");
	      this.userNameLabel.setFont(composite.getFont());
	      GridData gd = new GridData();
	      this.userNameLabel.setLayoutData(gd);
	   }

	   public void enableApplyFilterButton(boolean enable) {
	      this.getButton(0).setEnabled(enable);
	   }

	   public String getUserName() {
	      return this.userName;
	   }

	   public void setUserName(String userName) {
	      this.userName = userName;
	   }

	   public boolean isCustomizingRequests() {
	      return this.isCustomizingRequests;
	   }

	   public void setCustomizingRequests(boolean isCustomizingRequests) {
	      this.isCustomizingRequests = isCustomizingRequests;
	   }

	   public boolean isWorkbenchRequests() {
	      return this.isWorkbenchRequests;
	   }

	   public void setWorkbenchRequests(boolean isWorkbenchRequests) {
	      this.isWorkbenchRequests = isWorkbenchRequests;
	   }

	   public boolean isTransportOfCopies() {
	      return this.isTransportOfCopies;
	   }

	   public void setTransportOfCopies(boolean isTransportOfCopies) {
	      this.isTransportOfCopies = isTransportOfCopies;
	   }

	   public boolean isModifiable() {
	      return this.isModifiable;
	   }

	   public void setModifiable(boolean isModifiable) {
	      this.isModifiable = isModifiable;
	   }

	   public boolean isReleased() {
	      return this.isReleased;
	   }

	   public void setReleased(boolean isReleased) {
	      this.isReleased = isReleased;
	   }

	   public String getFromReleasedDate() {
	      return this.fromReleasedDate;
	   }

	   public void setFromReleasedDate(String fromReleasedDate) {
	      this.fromReleasedDate = fromReleasedDate;
	   }

	   public String getToReleasedDate() {
	      return this.toReleasedDate;
	   }

	   public void setToReleasedDate(String toReleasedDate) {
	      this.toReleasedDate = toReleasedDate;
	   }

	   public String getDestinationId() {
	      return this.destinationId;
	   }

	   public int getOptionNumberForfilterReleasedDateCombo() {
	      return this.optionNumberForfilterReleasedDateCombo;
	   }

	   public void setOptionNumberForFilterReleasedDateCombo(int optionNumberForfilterReleasedDateCombo) {
	      this.optionNumberForfilterReleasedDateCombo = optionNumberForfilterReleasedDateCombo;
	   }

	   protected void addUserNameToTextField(String user) {
	      this.userNameTextField.getTextWidget().setText(user);
	   }

	   protected void okPressed() { 
	      this.storeSelectedUIPreferences();
          super.okPressed();
	   }

	   private void storeSelectedUIPreferences() {
		   
		   this.preferenceStoreModel.setUserName(getUserName());
		   this.preferenceStoreModel.setCustomizing(isCustomizingRequests());
		   this.preferenceStoreModel.setWorkbench(isWorkbenchRequests());
		   this.preferenceStoreModel.setTransportofCopies(isTransportOfCopies());
		   this.preferenceStoreModel.setModifiable(isModifiable());
		   this.preferenceStoreModel.setReleased(isReleased());
		   this.preferenceStoreModel.setFilterReleasedComboSelection(getOptionNumberForfilterReleasedDateCombo());
		   this.preferenceStoreModel.setFromDateText(getFromReleasedDate());
		   this.preferenceStoreModel.setToDateText(getToReleasedDate());
		   
		   this.preferenceStoreModel.save();
	   }

//	   public Map<String, String> setSelectedPreferencesForConfigurationStoredInBackend() {
//	      this.propertiesStoredInBackend = new HashMap();
//	      String filterReleasedComboSelection = null;
//	      this.propertiesStoredInBackend.put("User", this.getUserName());
//	      if (this.isWorkbenchRequests()) {
//	         this.propertiesStoredInBackend.put("WorkbenchRequests", "true");
//	      } else {
//	         this.propertiesStoredInBackend.put("WorkbenchRequests", "false");
//	      }
//
//	      if (this.isCustomizingRequests()) {
//	         this.propertiesStoredInBackend.put("CustomizingRequests", "true");
//	      } else {
//	         this.propertiesStoredInBackend.put("CustomizingRequests", "false");
//	      }
//
//	      if (this.isTransportOfCopies()) {
//	         this.propertiesStoredInBackend.put("TransportOfCopies", "true");
//	      } else {
//	         this.propertiesStoredInBackend.put("TransportOfCopies", "false");
//	      }
//
//	      if (this.isModifiable()) {
//	         this.propertiesStoredInBackend.put("Modifiable", "true");
//	      } else {
//	         this.propertiesStoredInBackend.put("Modifiable", "false");
//	      }
//
//	      if (this.isReleased()) {
//	         this.propertiesStoredInBackend.put("Released", "true");
//	         filterReleasedComboSelection = Integer.toString(this.getOptionNumberForfilterReleasedDateCombo());
//	         this.propertiesStoredInBackend.put("DateFilter", filterReleasedComboSelection);
//	         if (filterReleasedComboSelection.equalsIgnoreCase(Integer.toString(3))) {
//	            this.propertiesStoredInBackend.put("FromDate", this.getFromReleasedDate());
//	            this.propertiesStoredInBackend.put("ToDate", this.getToReleasedDate());
//	         }
//	      } else {
//	         this.propertiesStoredInBackend.put("Released", "false");
//	         this.propertiesStoredInBackend.put("DateFilter", "-1");
//	      }
//
//	      if (this.treeHierarchyConfig != null) {
//	         this.propertiesStoredInBackend.put("com.sap.adt.tm.facets.order", this.treeHierarchyConfig.getSelectedTreeLevelsToStringValue()); 965
//	      }
//
//	      return this.propertiesStoredInBackend;
//	   }

	   private boolean validateSelectedPreferences() throws ParseException {
//	      if (this.getProjectName() != null && this.getProjectName().isEmpty()) {
//	         this.setMessage(Messages.TMUserDialog_New_Request_ABAP_Proj_Info_XMSG, 1);
//	         return false;
//	      } else if (this.getUserName() != null && this.getUserName().isEmpty()) {
//	         this.setErrorMessage(Messages.SettingDialog_Empty_User_Name_XMSG);
//	         this.enableApplyFilterButton(false);
//	         return false;
//	      } else if (!this.isWorkbenchRequests() && !this.isCustomizingRequests() && !this.isTransportOfCopies()) {
//	         this.setErrorMessage(Messages.SettingDialog_Request_Type_Error_message_XMSG);
//	         this.enableApplyFilterButton(false);
//	         return false;
//	      } else if (!this.isModifiable() && !this.isReleased()) {
//	         this.setErrorMessage(Messages.SettingDialog_Request_Status_Error_message_XMSG);
//	         this.enableApplyFilterButton(false);
//	         return false;
//	      } else {
//	         this.setErrorMessage((String)null);
//	         this.enableApplyFilterButton(true);
//	         return true;
//	      }
		   return true;
	   }

	   public boolean isFromDateAfterToDate(Date fromDate, Date toDate) {
	      if (fromDate.after(toDate)) {
	         Calendar fromDateCalendar = Calendar.getInstance();
	         Calendar toDateCalendar = Calendar.getInstance();
	         fromDateCalendar.setTime(fromDate);
	         toDateCalendar.setTime(toDate);
	         if (fromDateCalendar.get(1) != toDateCalendar.get(1) || fromDateCalendar.get(2) != toDateCalendar.get(2) || fromDateCalendar.get(6) != toDateCalendar.get(6)) { 
	            return true;
	         }
	      }

	      return false;
	   }

	   public IProject getIProject() {
//	      String projectName = this.getProjectName();
//	      if (projectName != null && !projectName.isEmpty()) {
//	         IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
//	         if (project.exists()) {
//	            return project;
//	         }
//	      }
//
//	      return null;
		  return MyAdtTools.getActiveProject();
	   }

	   public String getProjectName() {
	      return this.projectName;
	   }

	   public void setProjectName(String projectName) {
	      this.projectName = projectName;
	   }

	   public IProject getProject() {
	      return this.project;
	   }

	   public void setProject(IProject project) {
	      this.project = project;
	   }

	   public boolean isAbapProjectError() {
	      return this.abapProjectError;
	   }

	   public void setAbapProjectError(boolean abapProjectError) {
	      this.abapProjectError = abapProjectError;
	   }

//	   public Boolean isNewRequestCreationSupported(IProject iproject) {
//	      Boolean supported = false;
//
//	      try {
//	         IStatus status = AdtLogonServiceUIFactory.createLogonServiceUI().ensureLoggedOn(iproject);
//	         if (status.isOK()) {
//	            this.isSystemLoggedOn = true;
//	            this.setDestinationId(this.getDestinationIdfromIProject(iproject));
//	            supported = this.tmViewService.isCreateNewRequestSupported(this.getDestinationIdfromIProject(iproject));
//	         } else if (1 == status.getCode()) {
//	            this.isSystemLoggedOn = false;
//	         }
//	      } catch (LogonException var7) {
//	         String choosePreferencesDialogTitle = Messages.TMViewPreferences_dialogTitle_xgrp;
//	         String errorMessage = var7.getMessage();
//	         if (errorMessage.isEmpty()) {
//	            errorMessage = Messages.FilteredProjectGroupedView_backEndConnectionFailure_xmsg;
//	         }
//
//	         MessageDialog.openError(this.getShell(), choosePreferencesDialogTitle, errorMessage);
//	      }
//
//	      return supported;
//	   }

	   protected final String getDestinationIdfromIProject(IProject project) {
	      return MyAdtTools.getActiveAdtProject().getDestinationId();
	   }

	   public boolean isValidProject() {
	      return this.isValidProject;
	   }

	   public void setValidProject(boolean isValidProject) {
	      this.isValidProject = isValidProject;
	   }

//	   public boolean isValidUserName() {
//	      if (this.userName.equals("*")) {
//	         return true;
//	      } else {
//	         if (this.userName.isEmpty()) {
//	            this.setErrorMessage("用户名为空");
//	         } else {
//	            String error = this.validateUserName(this.userName);
//	            if (error == null || error.isEmpty()) {
//	               return true;
//	            }
//
//	            this.setErrorMessage(error);
//	            this.userNameStyledText.setSelection(0, this.userName.length());
//	         }
//
//	         this.userNameStyledText.setFocus();
//	         return false;
//	      }
//	   }

	   public boolean isCreationSupported() {
	      return this.isCreationSupportedInTheProject;
	   }

	   public void isCreationSupportedInTheProject(boolean isCreationSupported) {
	      this.isCreationSupportedInTheProject = isCreationSupported;
	   }

	   private boolean projectErrorExists() {
	      return this.isAbapProjectError() || this.abapProjectField.getTextWidget().getText().isEmpty();
	   }

//	   public String validateUserName(String userName) {
////	      final AtomicReference<String> error = new AtomicReference();
////	      final String userName1 = this.systemInfo.userFromString(userName).getId();
////	      if (userName != null && !userName.isEmpty()) {
////	         if (this.validatedUser != null && this.validatedUser.equals(userName1)) {
////	            return null;
////	         } else {
////	            IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
////
////	            try {
////	               progressService.busyCursorWhile(new IRunnableWithProgress() {
////	                  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
////	                     SettingDialog.this.systemInfo = AbapCore.getInstance().getAbapSystemInfo(SettingDialog.this.getDestinationId());
////	                     IUser user = SettingDialog.this.systemInfo.getUser(monitor, userName1);
////	                     if (user == null) {
////	                        error.set(NLS.bind(Messages.SettingDialog_User_Name_Invalid_XMIT, userName1));
////	                     }
////	                  } 1202
////	               });
////	            } catch (InvocationTargetException var6) {
////	               error.set(var6.getCause().getMessage());
////	            } catch (InterruptedException var7) {
////	               error.set(var7.getCause().getMessage());
////	            }
////
////	            if (error.get() != null && !((String)error.get()).isEmpty()) {
////	               return (String)error.get();
////	            } else {
////	               this.validatedUser = userName1;
////	               return null;
////	            }
////	         }
////	      } else {
////	         return Messages.SettingDialog_Empty_User_Name_XMSG;
////	      }
//	      
//	      this.validatedUser = userName;
//	   }

	   public void validateProjectField() {
	      if (this.projectErrorExists()) {
	         this.enableApplyFilterButton(false);
	      } else {
	         this.enableApplyFilterButton(true);
	      }

	   }

	   public SettingModel getPreferencesStoreModel() {
	      return this.preferenceStoreModel;
	   }

//	   public Map<String, String> getSelectedPreferencesForConfigurationStoredInBackend() {
//	      return this.propertiesStoredInBackend;
//	   }

//	   public Map<String, String> getSelectedPreferencesForUriInQueryParametersFormat() {
//	      return this.selectedPreferencesForQueryParameters;
//	   }

//	   public void setSelectedPreferencesForUriInQueryParametersFormat() {
//	      this.selectedPreferencesForQueryParameters = new HashMap();
//	      String requestCategory = "";
//	      String status = "";
//	      String releasedFromDate = "";
//	      String releasedToDate = "";
//	      this.selectedPreferencesForQueryParameters.put("destinationId", this.getProjectName());
//	      this.selectedPreferencesForQueryParameters.put("user", this.getUserName());
//	      if (this.isWorkbenchRequests()) {
//	         requestCategory = "K";
//	         this.selectedPreferencesForQueryParameters.put("requestType", requestCategory);
//	      }
//
//	      if (this.isCustomizingRequests()) {
//	         requestCategory = requestCategory + "W";
//	         this.selectedPreferencesForQueryParameters.put("requestType", requestCategory);
//	      }
//
//	      if (this.isTransportOfCopies()) {
//	         requestCategory = requestCategory + "T";
//	         this.selectedPreferencesForQueryParameters.put("requestType", requestCategory);
//	      }
//
//	      if (this.isModifiable()) {
//	         status = "D";
//	         this.selectedPreferencesForQueryParameters.put("requestStatus", status);
//	      }
//
//	      if (this.isReleased()) {
//	         status = status + "R";
//	         this.selectedPreferencesForQueryParameters.put("requestStatus", status);
//	         releasedFromDate = this.getFromReleasedDate();
//	         releasedToDate = this.getToReleasedDate();
//	         this.selectedPreferencesForQueryParameters.put("releasedFromDate", releasedFromDate);
//	         this.selectedPreferencesForQueryParameters.put("releasedToDate", releasedToDate);
//	      }
//
//	   }

	   public void setUserNameStyledText(String userName) {
	      this.userNameStyledText.setText(userName);
	   }
	}
