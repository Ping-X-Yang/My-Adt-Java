package com.ping.adt.sapgui.quicklogin.gui;

//import com.sap.adt.communication.debug.AdtDebugDataProviderFactory;
//import com.sap.adt.communication.debug.DebugMode;
//import com.sap.adt.communication.debug.IDebugDataProvider;
//import com.sap.adt.communication.destinations.ApplicationServerConnectionAttributes;
//import com.sap.adt.communication.sapgui.JCoDestinationPasswordUtil;
import com.sap.adt.communication.util.FileUtils;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.destinations.model.ISystemConfiguration;
import com.sap.adt.logging.AdtLogging;
import com.sap.adt.logging.IAdtLogger;
import com.sap.adt.tools.core.IAdtObjectReference;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;

public class SapGuiStartupData {
   private static final String TRANSACTION_SADT_START_TCODE = "*SADT_START_TCODE";
   private static final String TRANSACTION_SADT_START_WB_URI = "*SADT_START_WB_URI";
   private static final String TRANSACTION_SESS = "SESS";
   private static final String TRANSACTION_PARAMETER_AIE_TCODE = "D_AIE_TCODE";
   private static final String TRANSACTION_PARAMETER_ECLIPSE_NAVIGATION = "D_ECLIPSE_NAVIGATION";
   private static final String TRANSACTION_PARAMETER_ECLIPSE_PROJECT = "D_ECLIPSE_PROJECT";
   private static final String TRANSACTION_PARAMETER_GUID = "D_GUID";
   private static final String TRANSACTION_PARAMETER_PARAMETERS = "D_PARAMETERS";
   private static final String TRANSACTION_PARAMETER_OBJECT_URI = "D_OBJECT_URI";
   private static final String TRANSACTION_PARAMETER_OBJECT_URI_EXT = "D_OBJECT_URI_EXT";
   private static final String TRANSACTION_PARAMETER_REQUEST_USER = "D_REQUEST_USER";
   private static final String TRANSACTION_PARAMETER_IDE_ID = "D_IDE_ID";
   private static final String TRANSACTION_PARAMETER_TID = "D_TID";
   private static final String TRANSACTION_PARAMETER_IDE_USER = "D_IDE_USER";
   private static final String TRANSACTION_PARAMETER_TEST_MODE = "D_TEST_MODE";
   private static final String TRANSACTION_PARAMETER_TRACE_ID = "D_TRACE_ID";
   private static final String TRANSACTION_PARAMETER_WB_ACTION = "D_WB_ACTION";
   private static final String HOST_PREFIX = "/H/";
   private static final IAdtLogger LOGGER = AdtLogging.getLogger(SapGuiStartupData.class);
   private final String transaction;
   private final Map<String, String> transactionParameters = new LinkedHashMap();
   protected String title;
   protected String toolTipText;
   private IDestinationDataWritable destinationData;
   private String guid = this.createGuid();
   private String client;
   private String user;
   private String password;
   private String language;
   private String sncName;
   private String sncQop;
   private boolean ssoEnabled;
   private String host;
   private int port;
   private String mshost;
   private String systemNumber;
   private String logonGroup;
   private String systemName;
   private boolean executesApplicationCode;
   private boolean notifyStartupListeners = true;
   private boolean enableElipseNavigation;
   private final Map<String, String> additionalParameters = new HashMap();
   private IAdtObjectReference navigationTarget;
   private String router;
   private boolean workplaceEnabled;

   public SapGuiStartupData(IDestinationDataWritable destinationData, String transaction, boolean enableEclipseNavigation, Map<String, String> dynproFieldSettings, Map<String, String> additionalParameters, boolean executesApplicationCode) {
	   this.destinationData = destinationData;
	   this.executesApplicationCode = executesApplicationCode;
      if (additionalParameters != null) {
         this.additionalParameters.putAll(additionalParameters);
      }

      this.transaction = "*SADT_START_TCODE";
      this.transactionParameters.put("D_AIE_TCODE", transaction != null && !transaction.isEmpty() ? transaction : "SESS");
      this.enableElipseNavigation = enableEclipseNavigation;
      this.transactionParameters.put("D_ECLIPSE_NAVIGATION", "");
      this.transactionParameters.put("D_GUID", this.guid);
      this.transactionParameters.put("D_ECLIPSE_PROJECT", "");
//      this.setDebugData(destinationData, executesApplicationCode);
      this.setDynproFieldParameters(dynproFieldSettings);
      this.setLogonAndSystemData(destinationData);
      this.title = "标题";
      this.toolTipText = "标题";
   }
   protected SapGuiStartupData() {
      this.transaction = null;
   }
   public SapGuiStartupData(IDestinationDataWritable destinationData, IAdtObjectReference navigationTarget, boolean enableEclipseNavigation, String wbAction, Map<String, String> dynproFieldSettings, Map<String, String> additionalParameters, boolean executesApplicationCode) {
	   this.destinationData = destinationData;
      this.navigationTarget = navigationTarget;
      this.executesApplicationCode = executesApplicationCode;
      if (additionalParameters != null) {
         this.additionalParameters.putAll(additionalParameters);
      }

      String uriString = null;
      String uriPart2;
      try {
         URI uri = this.navigationTarget.getUri();
         uriPart2 = uri.getRawPath();
         StringBuilder sb = new StringBuilder(300);
         sb.append(uriPart2);
         String query = uri.getRawQuery();
         String fragment;
         if (query != null) {
            fragment = URLEncoder.encode(query, FileUtils.CHARSET_UTF8.name());
            sb.append('?').append(fragment);
         }

         fragment = uri.getRawFragment();
         if (fragment != null) {
            sb.append('#').append(fragment);
         }

         uriString = sb.toString();
      } catch (UnsupportedEncodingException var14) {
         throw new IllegalStateException(var14);
      }

      String uriPart1 = uriString;
      uriPart2 = null;
      if (uriString.length() > 255) {
         uriPart1 = uriString.substring(0, 255);
         uriPart2 = uriString.substring(255, uriString.length());
      }

      this.transaction = "*SADT_START_WB_URI";
      this.transactionParameters.put("D_OBJECT_URI", uriPart1);
      this.enableElipseNavigation = enableEclipseNavigation;
      this.transactionParameters.put("D_ECLIPSE_NAVIGATION", this.getBooleanAsAbapBool(enableEclipseNavigation));
      if (uriPart2 != null) {
         this.transactionParameters.put("D_OBJECT_URI_EXT", uriPart2);
      }

      if (wbAction != null) {
         this.transactionParameters.put("D_WB_ACTION", wbAction);
      }

      this.setDynproFieldParameters(dynproFieldSettings);
      this.transactionParameters.put("D_ECLIPSE_PROJECT", "");
//      this.setDebugData(destinationData, executesApplicationCode);
      this.transactionParameters.put("D_GUID", this.guid);
      this.setLogonAndSystemData(destinationData);
      this.title = "";
      this.toolTipText = "";
   }
   private String getBooleanAsAbapBool(boolean flag) {
      return flag ? "X" : " ";
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("SapGuiStartupData [title=");
      builder.append(this.title);
//      builder.append(", project=");
//      builder.append(this.project);
      builder.append(", toolTipText=");
      builder.append(this.toolTipText);
      builder.append(", guid=");
      builder.append(this.guid);
      builder.append(", host=");
      builder.append(this.host);
      builder.append(", port=");
      builder.append(this.port);
      builder.append(", mshost=");
      builder.append(this.mshost);
      builder.append(", systemName=");
      builder.append(this.systemName);
      builder.append(", systemNumber=");
      builder.append(this.systemNumber);
      builder.append(", router=");
      builder.append(this.router);
      builder.append(", client=");
      builder.append(this.client);
      builder.append(", user=");
      builder.append(this.user);
      builder.append(", password=");
      builder.append(this.password == null ? null : "***");
      builder.append(", language=");
      builder.append(this.language);
      builder.append(", logonGroup=");
      builder.append(this.logonGroup);
      builder.append(", sncName=");
      builder.append(this.sncName);
      builder.append(", sncQop=");
      builder.append(this.sncQop);
      builder.append(", ssoEnabled=");
      builder.append(this.ssoEnabled);
      builder.append(", navigationTarget=");
      builder.append(this.navigationTarget);
      builder.append(", transaction=");
      builder.append(this.transaction);
      builder.append(", transactionParameters=");
      builder.append(this.transactionParameters);
//      builder.append(", reentranceTicket=");
//      builder.append(this.reentranceTicket);
      builder.append(", executesApplicationCode=");
      builder.append(this.executesApplicationCode);
      builder.append(", notifyStartupListeners=");
      builder.append(this.notifyStartupListeners);
      builder.append(", additionalParameters=");
      builder.append(this.additionalParameters);
      builder.append("]");
      return builder.toString();
   }

   public String getTitle() {
      return this.title;
   }


   protected IDecoratorManager getDecoratorManager() {
      return PlatformUI.getWorkbench().getDecoratorManager();
   }

   public String getGuid() {
      return this.guid;
   }

   private void setDynproFieldParameters(Map<String, String> dynproFieldSettings) {
      if (dynproFieldSettings != null && !dynproFieldSettings.isEmpty()) {
         StringBuilder parameterString = new StringBuilder();
         Iterator var4 = dynproFieldSettings.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var4.next();
            parameterString.append(String.format("%s=%s;", entry.getKey(), entry.getValue()));
         }

         parameterString.deleteCharAt(parameterString.length() - 1);
         try {
            this.transactionParameters.put("D_PARAMETERS", URLEncoder.encode(parameterString.toString(), "UTF-8"));
         } catch (UnsupportedEncodingException var5) {
            throw new IllegalStateException(var5);
         }
      }

   }
   static String convertToSncQop(ISystemConfiguration.SNCType sncType) {
      if (sncType == null) {
         return null;
      } else {
         switch (sncType) {
            case SNC_AUTHENTICATION:
            case SNC_INTEGRITY:
            case SNC_ENCRYPTED:
            case SNC_DEFAULT:
            case SNC_HIGHEST_AVAILABLE:
               return String.valueOf(sncType.getCode());
            case SNC_INTEGRTY:
            default:
               return null;
         }
      }
   }

   protected final void setLogonAndSystemData(IDestinationDataWritable destinationData) {
      this.client = destinationData.getClient();
      this.user = destinationData.getUser();
      this.password = destinationData.getPassword();
      this.language = destinationData.getLanguage();
      if (destinationData.getSystemConfiguration().isSNCEnabled()) {
         this.sncName = destinationData.getSystemConfiguration().getPartnerName();
      }

      this.sncQop = convertToSncQop(destinationData.getSystemConfiguration().getSNCType());
      this.ssoEnabled = destinationData.getSystemConfiguration().isSSOEnabled();
      this.host = destinationData.getSystemConfiguration().getServer();
      this.systemNumber = destinationData.getSystemConfiguration().getSystemNumber();
      if (this.host != null && this.systemNumber != null) {
         String gatewayServer = destinationData.getSystemConfiguration().getGatewayServer();
         if (gatewayServer != null && !gatewayServer.isEmpty()) {
            this.host = gatewayServer;
         }

         String gatewayServerService = destinationData.getSystemConfiguration().getGatewayServerService();
         if (gatewayServerService != null && !gatewayServerService.isEmpty() && !gatewayServerService.equals("33" + this.systemNumber)) {
            throw new IllegalStateException("SAP GUI connections are not supported for destinations with custom RFC gateway server port.");
         }
      } else {
         this.host = null;
         this.systemNumber = null;
         this.mshost = destinationData.getSystemConfiguration().getMessageServer();
         this.logonGroup = destinationData.getSystemConfiguration().getGroup();
      }

      this.port = this.systemNumber == null ? 0 : 3200 + Integer.parseInt(this.systemNumber);
      this.systemName = destinationData.getSystemConfiguration().getSystemId();
      this.router = destinationData.getSystemConfiguration().getRouter();
   }
   public String getLogonParameters(boolean maskPassword) {
      String logonParameters = "";
      logonParameters = logonParameters + (this.client != null ? "&clnt=" + this.client : "") + (this.user != null ? "&user=" + this.user : "") + (this.language != null ? "&lang=" + this.language : "");// 482 483 484
      if (this.sncQop != null) {
         logonParameters = logonParameters + "&sncon=true&sncqop=" + this.sncQop;
      }

      if (this.sncName != null) {
         logonParameters = logonParameters + "&sncname=" + this.sncName;
      }

//      if (this.reentranceTicket != null) {
//         logonParameters = logonParameters + "&sso2=" + this.reentranceTicket;
//      }

      if (!this.ssoEnabled) {
         logonParameters = logonParameters + "&manualLogin=true";
      }

      if (this.password != null && !this.password.isEmpty()) {
         if (!this.password.contains("&")) {
//            logonParameters = logonParameters + (this.reentranceTicket == null ? "&pass=" + this.getPassword(maskPassword) : "");
         }
      } else {
         logonParameters = logonParameters + "&pass=!";
      }

      if (this.workplaceEnabled) {
         logonParameters = logonParameters + "&wp=true";
      }

      return logonParameters;
   }

   public String getConnectionString() {
      String connectionString = "";
      if (this.host != null && this.systemNumber != null) {
         connectionString = "/H/" + this.host + "/S/" + this.port;
      } else if (this.logonGroup != null && this.systemName != null) {
         connectionString = "/R/" + this.systemName + "/G/" + this.logonGroup;
      } else {
         LOGGER.log(new Status(4, "com.sap.adt.sapgui.ui", "Inconsistent JCo properties for system " + this.systemName));
      }

      String routerPrefix = this.router;
      if (routerPrefix != null) {
         if (routerPrefix.endsWith("/H/")) {
            routerPrefix = routerPrefix.substring(0, routerPrefix.length() - "/H/".length());
         }

         connectionString = routerPrefix + connectionString;
      }

      return connectionString;
   }

   public String getSystemNumber() {
      return this.systemNumber;
   }

   public int getPort() {
      return this.port;
   }

   public String getGroup() {
      return this.logonGroup;
   }

   private void setDebugData(IDestinationData destinationData, boolean executesApplicationCode) {
//      DebugMode debugMode = executesApplicationCode ? DebugMode.ON : DebugMode.AUTO;
//      IDebugDataProvider debugDataProvider = this.getDebugDataProvider(destinationData, debugMode);
//      String destinationId = destinationData.getId();
//      String ideUser = null;
//      String requestUser = null;
//      String terminalId = null;
//      String ideId = null;
//
//      try {
//         if (!debugDataProvider.isDebuggingEnabled(destinationId)) {
//            Activator.TRACER_DEBUG.trace(AdtTraceLevel.INFO, "SAP GUI does NOT use debug data since debugging is disabled for destination {0}", new Object[]{destinationId});
//            return;
//         }
//
//         ideUser = destinationData.getUser();
//         requestUser = debugDataProvider.getRequestUser(destinationId, debugMode);
//         terminalId = debugDataProvider.getTerminalId(destinationId, debugMode).getOrCreateAsString();
//         ideId = debugDataProvider.getIdeId(destinationId, debugMode).getOrCreateAsString();
//      } catch (Exception var11) {
//         AdtLogging.getLogger(this.getClass()).error(var11, "Error in debug data provider for destination {0}", new Object[]{destinationId});
//      }

//      this.transactionParameters.put("D_TID", terminalId != null ? terminalId : "");
//      this.transactionParameters.put("D_IDE_ID", ideId != null ? ideId : "");
//      this.transactionParameters.put("D_REQUEST_USER", requestUser != null ? requestUser : "");
//      this.transactionParameters.put("D_IDE_USER", ideUser != null ? ideUser : "");
//      Activator.TRACER_DEBUG.trace(AdtTraceLevel.INFO, "SAP GUI uses requestUser {0}, ideUser {1}, terminalId: {2}, ideId: {3}", new Object[]{String.valueOf(requestUser), ideUser, String.valueOf(terminalId), String.valueOf(ideId)});
   }
//   protected IDebugDataProvider getDebugDataProvider(IDestinationData destinationData, DebugMode debugMode) {
//      IDebugDataProvider debugDataProvider = AdtDebugDataProviderFactory.getDebugDataProvider();
//      return debugDataProvider;
//   }

//   public void enableTestMode() {
//      this.transactionParameters.put("D_TEST_MODE", this.getBooleanAsAbapBool(true));
//   }
   public void regenerateGuid() {
      this.guid = this.createGuid();
      this.transactionParameters.put("D_GUID", this.guid);
   }
//   public void setReentranceTicket(String ticket) {
//      this.reentranceTicket = ticket;
//   }
//   public String getReentranceTicket() {
//      return this.reentranceTicket;
//   }

//   protected static final IDestinationData getDestinationData(IProject project) {
//      IDestinationData destinationData = null;
//      IAdtCoreProject adtCoreProject = (IAdtCoreProject)project.getAdapter(IAdtCoreProject.class);
//      if (adtCoreProject != null) {
//         destinationData = adtCoreProject.getEffectiveDestinationData();
//      }
//
//      if (destinationData == null) {
//         throw new IllegalStateException(MessageFormat.format("Failed to retrieve destination data from project {0}", project.getName()));
//      } else {
//         return destinationData;
//      }
//   }

//   protected static final ApplicationServerConnectionAttributes getApplicationServerConnectionAttributes(IProject project) {
//      ApplicationServerConnectionAttributes connectionAttributes = null;
//      IAdtCoreProject adtCoreProject = (IAdtCoreProject)project.getAdapter(IAdtCoreProject.class);
//      if (adtCoreProject != null) {
//         connectionAttributes = adtCoreProject.getApplicationServerConnectionAttributes();
//      }
//
//      if (connectionAttributes == null) {
//         throw new IllegalStateException(MessageFormat.format("Failed to retrieve application server connection attributes from project {0}", project.getName()));
//      } else {
//         return connectionAttributes;
//      }
//   }

   public IDestinationData getDestinationData() {
      return this.destinationData.getReadOnlyClone();
   }

   public String getParameters(boolean maskPassword) {
      StringBuilder result = new StringBuilder();
      result.append(this.getConnectionString());
      result.append("&tran=").append(this.getTransactionString());
      result.append(this.getLogonParameters(maskPassword));
      return result.toString();
   }

   public String getToolTipText() {
      return this.toolTipText;
   }

   public boolean executesApplicationCode() {
      return this.executesApplicationCode;
   }

   public String getTransactionString() {
      if (this.transactionParameters.isEmpty()) {
         return this.transaction;
      } else {
         String var10000 = this.transaction;
         return var10000 + " " + (String)this.transactionParameters.entrySet().stream().map((param) -> {// 704 705 706 707
            return String.format("%s=%s", param.getKey(), param.getValue());
         }).collect(Collectors.joining(";"));
      }
   }

   public String getClient() {
      return this.client;
   }

   public String getUser() {
      return this.user;
   }

   public String getPassword() {
      return this.getPassword(false);
   }

   private String getPassword(boolean mask) {
      return mask ? "***" : this.password;// 725 726 728
   }

   public String getLanguage() {
      return this.language;
   }

   public String getSystemName() {
      return this.systemName;
   }

   public String getHost() {
      return this.host;
   }

   public String getMsHost() {
      return this.mshost;
   }

   public String getSncName() {
      return this.sncName;
   }

   public String getSncQop() {
      return this.sncQop;
   }

   public boolean isSsoEnabled() {
      return this.ssoEnabled;
   }

   public boolean notifyStartupListeners() {
      return this.notifyStartupListeners;
   }

   public void setNotifyStartupListeners(boolean notify) {
      this.notifyStartupListeners = notify;
   }
//   public boolean navigationEnabled() {
//      return this.enableElipseNavigation;
//   }

//   public void setNavigationEnabled(boolean enabled) {
//      this.enableElipseNavigation = enabled;
//      this.transactionParameters.put("D_ECLIPSE_NAVIGATION", this.getBooleanAsAbapBool(enabled));
//   }
   public Map<String, String> getAdditionalParameters() {
      return Collections.unmodifiableMap(this.additionalParameters);
   }

   public IAdtObjectReference getNavigationTarget() {
      return this.navigationTarget;
   }

//   public void setTraceParameterId(String traceParameterId) {
//      if (traceParameterId != null) {
//         this.transactionParameters.put("D_TRACE_ID", traceParameterId);
//      } else {
//         this.transactionParameters.remove("D_TRACE_ID");
//      }
//
//   }
   public void setWorkplaceEnabled(boolean workplaceEnabled) {
      this.workplaceEnabled = workplaceEnabled;
   }
//   protected String getPasswordFromJcoDestinationRegistry(String destinationId) {
//      JCoDestinationPasswordUtil destinationPasswordUtil = JCoDestinationPasswordUtil.getInstance();
//      return destinationPasswordUtil != null ? destinationPasswordUtil.getPassword(destinationId) : null;// 816 817 819
//   }

   protected String createGuid() {
      return UUID.randomUUID().toString();
   }
}