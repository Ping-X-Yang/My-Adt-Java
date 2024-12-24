package com.ping.adt.sapgui.quicklogin.gui;

//import com.sap.adt.logging.AdtTraceLevel;
//import com.sap.adt.sapgui.ui.ISapGui;
//import com.sap.adt.sapgui.ui.SapGuiPlugin;
//import com.sap.adt.sapgui.ui.internal.Activator;
//import com.sap.adt.sapgui.ui.internal.IEmbeddedSapGui;
//import com.sap.adt.sapgui.ui.internal.editors.AbstractSapGuiEditor;
//import com.sap.adt.sapgui.ui.internal.editors.SapGuiStartupData;
//import com.sap.adt.sapgui.ui.internal.views.AbstractSapGuiWorkbenchPart;
//import com.sap.adt.sapgui.ui.internal.win32.WinGuiFragment;
//import com.sap.adt.sapgui.ui.internal.win32.embedding.WinGuiServerProxy;
import com.sap.adt.util.WinRegistry;
import com.sap.adt.util.WinRegistryException;
import com.sap.adt.util.configuration.AdtConfiguration;
import com.sun.jna.platform.win32.WinNT;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;

public class WinGuiServerProxy {
	
	public static final int PIPE_CONNECTION_TIMEOUT = 30000;
	public static final String PIPE_CONNECTION_TIMEOUT_VM_PARAMETER = "com.sap.adt.sapgui.ui.win32.pipeConnectionTimeout";
	public static String injectedAdditionalShortcutParams;
	private static final String EVENT_PIPE_NAME_PREFIX = "\\\\.\\pipe\\AiEWinguiEventpipe-";
	private WinNT.HANDLE eventPipeHandle;
	private RandomAccessFile commandPipe;
	private ISapGuiServerProcess serverProcess;
	private String eventPipeName;
	protected WinGuiEventResponseQueue eventResponseQueue;
	protected WinGuiCommandQueue commandQueue;
	private final List<IGuiServerListener> serverListener = new CopyOnWriteArrayList();
	private final List<WinGuiEventResponseQueue.IEventPipeListener> eventPipeListener = new CopyOnWriteArrayList();
	private static WinGuiServerProxy proxy;
	
	protected WinGuiVersionInfo versionInfo = new WinGuiVersionInfo();

	private final IWorkbenchListener workbenchListener = new IWorkbenchListener(){
		public boolean preShutdown(IWorkbench workbench, boolean forced){
				WinGuiServerProxy proxy = WinGuiServerProxy.getProxy();
			    if (proxy != null) {
//			    	WinGuiFragment.this.trace(AdtTraceLevel.INFO, 
//			              "Eclipse workbench is about shutting down, therefore GUI server is shut down.", new Object[0]);
			    	proxy.shutdownServer();
			    } 
	    return true;
	    }

		@Override
		public void postShutdown(IWorkbench workbench) {
		}
	};
	
	private final JobChangeAdapter eventResponseQueueJobChangeListener = new JobChangeAdapter() {
		public void done(IJobChangeEvent event) {
//			WinGuiServerProxy.this.trace(
//					"Event response queue job has ended, therefore the whole GUI server is shut down.", new Object[0]);
			WinGuiServerProxy.this.shutdownServer();
		}
	};
	
	
	protected  WinGuiServerProxy() {
		this.startGuiServerProcess();
		PlatformUI.getWorkbench().addWorkbenchListener(this.workbenchListener);
	}
	
	public static WinGuiServerProxy getProxy() {
		if (proxy == null) {
			proxy = new WinGuiServerProxy();
		}else {
			proxy.ensureRunning();
		}
		return proxy;
	}
	
	
	protected ISapGuiServerProcess createGuiServerProcess() {
		return new SapGuiServerProcess();
	}

	protected String createEventPipeName() {
		return "\\\\.\\pipe\\AiEWinguiEventpipe-" + UUID.randomUUID().toString();
	}

	public void startGuiServerProcess() {
		this.serverProcess = createGuiServerProcess();
		this.eventPipeName = createEventPipeName();

		this.eventPipeHandle = PipeHelper.createNamedPipe(this.eventPipeName);
//     trace("CreateNamedPipe succeeded: {0} (Handle: {1})", new Object[] { this.eventPipeName, 
//           this.eventPipeHandle });

		final RuntimeException[] initializePipeJobException = new RuntimeException[1];
		Job initializePipeJob = new Job("WinGuiServer代理_初始化事件管道") {

			protected IStatus run(IProgressMonitor monitor) {
//           WinGuiServerProxy.this.trace("Connect job starting ...", new Object[0]);

				try {
					PipeHelper.connectNamedPipe(WinGuiServerProxy.this.eventPipeHandle);
//             WinGuiServerProxy.this.trace("ConnectNamedPipe succeeded: {0} (Handle: {1})", new Object[] { WinGuiServerProxy.this.eventPipeName, 
//                   WinGuiServerProxy.this.eventPipeHandle });

					long clientProcessId = PipeHelper
							.getNamedPipeClientProcessId(WinGuiServerProxy.this.eventPipeHandle);
//             WinGuiServerProxy.this.trace("GetNamedPipeClientProcessId: {0} (Handle: {1})", new Object[] { Long.valueOf(clientProcessId), WinGuiServerProxy.this.eventPipeHandle });

					String receivedXml = PipeHelper.readNamedPipe(WinGuiServerProxy.this.eventPipeHandle);
//             WinGuiServerProxy.this.trace("Event pipe read (length {0}):\n {1}", new Object[] { Integer.valueOf(receivedXml.length()), receivedXml });

					WinGuiPipeEventParser eventParser = new WinGuiPipeEventParser(receivedXml, createSaxParser());

					WinGuiServerProxy.this.versionInfo = WinGuiServerProxy.parseVersion(eventParser);

//					Activator.getDefault().putGuiVersionInfo(SapGuiPlugin.WINGUI_EDITOR_ID,
//							WinGuiServerProxy.this.versionInfo);

					String commandPipeName = eventParser.getProperty("CommandPipe");
					try {
						WinGuiServerProxy.this.commandPipe = new RandomAccessFile(commandPipeName, "rw");
					} catch (FileNotFoundException e) {
						IllegalStateException illegalStateException = new IllegalStateException(e);
//               WinGuiServerProxy.this.trace(AdtTraceLevel.ERROR, illegalStateException, "Command pipe not found: {0}", new Object[] { commandPipeName });
						throw illegalStateException;
					}

					WinGuiPipeXmlResponse response = new WinGuiPipeXmlResponse("UpAndRunning");
					response.setProperty("hr", "S_OK");
					String responseXml = response.serialize();
					PipeHelper.writeNamedPipe(WinGuiServerProxy.this.eventPipeHandle, responseXml);
//             WinGuiServerProxy.this.trace("Response to event pipe:\n{0}", new Object[] { responseXml });
				} catch (RuntimeException e) {
					initializePipeJobException[0] = e;
				}

				return Status.OK_STATUS;
			}
		};

		initializePipeJob.setSystem(true);
		initializePipeJob.schedule();

		this.serverProcess.start(this.eventPipeName);

		try {
			if (!initializePipeJob.join(getMaxConnectionTimeout(), null)) {
				if (initializePipeJob.getState() != 0) {
					RuntimeException exception = new IllegalStateException(
							String.format("connecting to event pipe %s timed out. Command line: %s",
									new Object[] { this.eventPipeName, this.serverProcess.getCommandLine() }));
//           trace(AdtTraceLevel.ERROR, exception, exception.getMessage(), new Object[0]);
					throw exception;
				}

			}
		} catch (InterruptedException | org.eclipse.core.runtime.OperationCanceledException e) {
			RuntimeException exception = new IllegalStateException(
					String.format("Connecting to event pipe %s was interrupted. Command line: %s",
							new Object[] { this.eventPipeName, this.serverProcess.getCommandLine() }),
					e);
//       trace(AdtTraceLevel.ERROR, exception, exception.getMessage(), new Object[0]);
			throw exception;
		}

		if (initializePipeJobException[0] != null) {
			RuntimeException exception = new IllegalStateException(
					String.format("Problems connecting to event pipe %s. Command line: %s",
							new Object[] { this.eventPipeName, this.serverProcess.getCommandLine() }),
					initializePipeJobException[0]);
//       trace(AdtTraceLevel.ERROR, exception, exception.getMessage(), new Object[0]);
			throw exception;
		}

		WinGuiPipeXmlCommand commandInit = new WinGuiPipeXmlCommand("Initialize");

		commandInit.setProperty("version", "29");
		commandInit.setProperty("productVersion", "300");
		commandInit.setProperty("LSAPIVersion", "200");
		commandInit.setProperty("LSAPIFeatures", "1");
		commandInit.setProperty("productName", "NWBC");
		commandInit.setProperty("theme", "default");
//     commandInit.setProperty("fontSize", 
//         Integer.toString(preferenceStore.getInt("winguiFontSize")));
		commandInit.setProperty("mergeToolbars", "0");

		commandInit.setProperty("RTLmode", "0");
//     commandInit.setProperty("menuInToolbar", 
//         WinGuiPipeXml.boolToXmlString(preferenceStore.getBoolean("menuInToolbar")));
		commandInit.setProperty("mergeToolbars", "0");

		this.commandQueue = new WinGuiCommandQueue("WinGuiServer代理_命令行队列", this.commandPipe, createSaxParser());

		this.commandQueue.schedule();

		sendCommand(commandInit);

		this.eventResponseQueue = new WinGuiEventResponseQueue("Event response queue", this.eventPipeHandle,
				createSaxParser());
		this.eventResponseQueue.addJobChangeListener(this.eventResponseQueueJobChangeListener);

		this.eventResponseQueue.schedule();

		notifyServerStartup();

//     this.eventResponseQueue.registerEventPipeListener(
//    		 event -> 
//         this.eventPipeListener.forEach({})
//     ); 
	}

	public WinGuiVersionInfo getVersionInfo() {
		return this.versionInfo;
	}

	private void notifyServerStartup() {
		for (IGuiServerListener listener : this.serverListener)
			listener.serverStarted();
	}

	private void notifyServerShutdown() {
		byte b;
		int i;
		IGuiServerListener[] arrayOfIGuiServerListener;
//		for (i = arrayOfIGuiServerListener = (IGuiServerListener[]) this.serverListener.toArray(new IGuiServerListener[0]).length, b = 0; b < i;) {
//			IGuiServerListener listener = arrayOfIGuiServerListener[b];
//			listener.serverStopped();
//			b++;
//		}

	}

	protected int getMaxConnectionTimeout() {
		String timoutString = AdtConfiguration.getSystemProperty("com.sap.adt.sapgui.ui.win32.pipeConnectionTimeout",
				null);
		if (timoutString != null) {
			try {
				return Integer.parseInt(timoutString);
			} catch (NumberFormatException numberFormatException) {
				numberFormatException.printStackTrace();
//				traceWithStack(AdtTraceLevel.WARNING, String
//						.format("Wrong number format for pipe connection timeout: %s", new Object[] { timoutString }),
//						new Object[0]);
			}
		}
		return 30000;
	}

	private static WinGuiVersionInfo parseVersion(WinGuiPipeEventParser eventParser) {
		WinGuiVersionInfo versionInfo = new WinGuiVersionInfo();

		String version = eventParser.getProperty("version");
		if (version != null) {
			versionInfo.setProtocolVersion(Integer.parseInt(version));
		}
		String productVersion = eventParser.getProperty("productVersion");
		if (productVersion != null) {
			versionInfo.setProductVersion(Integer.parseInt(productVersion));
		}

		return versionInfo;
	}

	public ISapGuiConnection openConnection(final SapGuiStartupData startupData, long windowHandle) {
		scheduleConnectionJob(startupData, windowHandle);

		return new ISapGuiConnection() {

			public void close() {
				WinGuiServerProxy.this.closeSession(startupData.getGuid());
			}
		};
	}

	protected Job scheduleConnectionJob(final SapGuiStartupData startupData, final long windowHandle) {
		final CountDownLatch latch = new CountDownLatch(1);
//		this.eventResponseQueue.registerPendingEditor(windowHandle, startupData.getGuid(),
//				() -> countDown());

		Job openConnection = new Job("WinGuiServer代理_创建GUI Session") {

			protected IStatus run(IProgressMonitor monitor) {
				WinGuiPipeXmlCommand command = WinGuiServerProxy.this.createLogonXml(startupData, windowHandle);
				WinGuiServerProxy.this.sendCommand(command);
				return Status.OK_STATUS;

//				int waited = 0;
//				int timeout = WinGuiServerProxy.this.getMaxConnectionTimeout();
//				do {
//					try {
//						if (latch.await(10L, TimeUnit.MILLISECONDS)) {
//							return Status.OK_STATUS;
//						}
//						waited += 10;
//					} catch (InterruptedException e) {
////						WinGuiServerProxy.this.trace(AdtTraceLevel.ERROR, e, e.getMessage(), new Object[0]);
//						throw new IllegalStateException(e);
//					}
//					if (monitor.isCanceled()) {
////						WinGuiServerProxy.this.traceWithStack(AdtTraceLevel.WARNING,
////								"Connection job was canceled, closing the editor.", new Object[0]);
////						WinGuiServerProxy.this.closeSapGuiEditorPart(startupData.getGuid());
//						return Status.CANCEL_STATUS;
//					}
//				} while (waited < timeout);
////				WinGuiServerProxy.this.traceWithStack(AdtTraceLevel.WARNING,
////						"Starting the connection job failed due to timeout {0}, closing the editor.",
////						new Object[] { Integer.valueOf(timeout) });
//				WinGuiServerProxy.this.closeSapGuiEditorPart(startupData.getGuid());
//				return Status.CANCEL_STATUS;
			}
		};

		openConnection.schedule();

		return openConnection;
	}

	protected void closeSapGuiEditorPart(String editorId) {
//		Set<ISapGui> guiEditorParts = ((SapGuiPlugin) SapGuiPlugin.getDefault())
//				.getAllGuiEditorParts(PlatformUI.getWorkbench(), null);
//		for (ISapGui guiEditor : guiEditorParts) {
//			final AbstractSapGuiEditor guiEditorPart = (AbstractSapGuiEditor) guiEditor;
//			if (guiEditorPart.getPartGuid().equals(editorId)) {
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						guiEditorPart.closePart();
//					}
//				});
//			}
//		}
	}

	public WinGuiPipeXmlCommand createLogonXml(SapGuiStartupData startupData, long windowHandle) {
		String shellId = String.valueOf(windowHandle);
		WinGuiPipeXmlCommand command = new WinGuiPipeXmlCommand("CreateSession");

		command.setProperty("client", startupData.getClient());
		command.setProperty("language", startupData.getLanguage());
		command.setProperty("parentalHWND", shellId);

		String transactionString = startupData.getTransactionString();
		command.setProperty("transaction", transactionString);
		command.setProperty("parameter", "");

		String shortcutParams = startupData.getConnectionString();

		if (startupData.getSncQop() != null) {
			shortcutParams = shortcutParams + " SNC_QOP=" + shortcutParams;
		}

		if (startupData.getSncName() != null) {
			shortcutParams = shortcutParams + " SNC_PARTNERNAME=\"" + shortcutParams + "\"";
		}

		if (!startupData.isSsoEnabled()) {
			shortcutParams = shortcutParams + " /SUPPORTBIT_ON=NEED_STDDYNPRO";
		}

		if (injectedAdditionalShortcutParams != null) {
			shortcutParams = shortcutParams + " " + shortcutParams;
		}

		if (startupData.getPort() != 0) {
			command.setProperty("service", String.valueOf(startupData.getPort()));
		}

		command.setProperty("systemID", startupData.getSystemName());
		command.setProperty("OKCodeFieldMode", "");
		command.setProperty("mergeToolbars", "0");
		command.setProperty("showCaption", "1");
		command.setProperty("showStatusBar", "");
		command.setProperty("noSplashScreen", "1");
		command.setProperty("reuseConnection", "0");

//		command.setProperty("animatedFocus",
//				WinGuiPipeXml.boolToXmlString(Activator.getDefault().getPreferenceStore().getBoolean("animatedFocus")));

//		if (startupData.getReentranceTicket() != null) {
//			command.setProperty("cookie", startupData.getReentranceTicket());
//		}

		if (startupData.getPassword() != null && !startupData.getPassword().isBlank()) {
			command.setProperty("password", startupData.getPassword());
		}

		if (startupData.getUser() != null) {
			command.setProperty("user", startupData.getUser());
		}

		if (!shortcutParams.isEmpty()) {
			command.setProperty("shortcutParams", shortcutParams);
		}

		return command;
	}

	protected void sendCommand(WinGuiPipeXmlCommand command) {
		WinGuiCommandQueue commandQueue = this.commandQueue;
		if (commandQueue == null) {
			return;
		}
		commandQueue.sendCommand(command.serialize());
	}

	public void shutdownServer() {
//		traceWithStack(AdtTraceLevel.INFO, "Gui server shutdown called", new Object[0]);

		if (this.eventResponseQueue != null) {
//			trace("Stopping event response queue...", new Object[0]);
			this.eventResponseQueue.removeJobChangeListener(this.eventResponseQueueJobChangeListener);
			this.eventResponseQueue.cancel();
			this.eventResponseQueue = null;
		}

		if (this.serverProcess != null) {
			if (this.serverProcess.isRunning()) {
				boolean needsKilling = true;
				if (this.commandQueue != null && this.commandQueue.getState() == 4) {
//					trace("Sending shutdown command to server process...", new Object[0]);
					WinGuiPipeXmlCommand shutdownCommand = new WinGuiPipeXmlCommand("Shutdown");
					sendCommand(shutdownCommand);
					try {
//						trace("Waiting for server process to exit cleanly after shutdown command...", new Object[0]);
						if (this.serverProcess.waitFor(5L, TimeUnit.SECONDS)) {
							needsKilling = false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
//						trace(AdtTraceLevel.ERROR, e, "Got interrupted while waiting for server process to end...",
//								new Object[0]);
					}
				}
				if (needsKilling) {
//					trace("Server process is still running, killing it forcefully...", new Object[0]);
					this.serverProcess.shutdown();
					try {
//						trace("Waiting for server process to vanish after killing it...", new Object[0]);
						if (!this.serverProcess.waitFor(5L, TimeUnit.SECONDS)) {
//							trace("Server process not reacting to kill, giving up. This will most likely result in a deadlock ...",
//									new Object[0]);
						}
					} catch (InterruptedException e) {
//						trace(AdtTraceLevel.ERROR, e, "Got interrupted while waiting for server process to end...",
//								new Object[0]);
					}
				}
			}
			this.serverProcess = null;
		}

		if (this.eventPipeHandle != null) {
			try {
//				trace("Disconnecting event pipe...", new Object[0]);
				PipeHelper.disconnectNamedPipe(this.eventPipeHandle);
//				trace("Closing event pipe...", new Object[0]);
				PipeHelper.closeHandle(this.eventPipeHandle);
			} catch (RuntimeException e) {
//				trace(AdtTraceLevel.ERROR, e, e.getMessage(), new Object[0]);
			}
			this.eventPipeHandle = null;
			this.eventPipeName = null;
		}

		if (this.commandQueue != null) {
//			trace("Stopping command queue...", new Object[0]);
			this.commandQueue.shutdown();
			this.commandQueue = null;
		}

		if (this.commandPipe != null) {
			try {
//				trace("Closing command pipe...", new Object[0]);
				this.commandPipe.close();
			} catch (IOException e) {
//				trace(AdtTraceLevel.ERROR, e, e.getMessage(), new Object[0]);
			}
			this.commandPipe = null;
		}

//		trace("Notifying server shutdown...", new Object[0]);
		notifyServerShutdown();
	}

	public boolean isRunning() {
		boolean serverProcessIsRunning = (this.serverProcess != null && this.serverProcess.isRunning());
		boolean commandQueueIsRunning = (this.commandQueue != null && this.commandQueue.getState() == 4);
		boolean eventResponseQueueIsRunning = (this.eventResponseQueue != null
				&& this.eventResponseQueue.getState() == 4);
		boolean running = (serverProcessIsRunning && commandQueueIsRunning && eventResponseQueueIsRunning);

		if (!running) {
			traceRunningState(serverProcessIsRunning, commandQueueIsRunning, eventResponseQueueIsRunning);
		}
		return running;
	}

	public void ensureRunning() {
		if (!isRunning()) {
//			trace(AdtTraceLevel.INFO,
//					"GUI server is not properly running, therefore GUI server is shut down and restarted.",
//					new Object[0]);
			shutdownServer();
			startGuiServerProcess();
		}
	}

	public static interface ISapGuiConnection {
		void close();
	}

	public static interface ISapGuiServerProcess {
		void start(String param1String);

		boolean isRunning();

		Object getCommandLine();

		void shutdown();

		boolean waitFor(long param1Long, TimeUnit param1TimeUnit) throws InterruptedException;
	}

	class SapGuiServerProcess implements ISapGuiServerProcess {
		private Process process;

		private String commandLine;

		public void start(String eventPipeName) {
			String guiServerPath = getSapGuiServerPath();

			try {
				ProcessBuilder processBuilder = new ProcessBuilder(new String[] { guiServerPath, eventPipeName });

				processBuilder.environment().put("SAPGUI_INPLACE", "1");

				String traceLevel = AdtConfiguration.getSystemProperty("com.sap.adt.sapgui.winguiTraceLevel", null);
				if (traceLevel != null) {
					processBuilder.environment().put("DPTRACE", traceLevel);
				}

				this.commandLine = getCommandLine(processBuilder);
				this.process = processBuilder.start();
			} catch (IOException e) {

				IllegalStateException illegalStateException = new IllegalStateException(
						String.format("Could not start SAP GUI for Windows: %s", new Object[] { guiServerPath }), e);
//				WinGuiServerProxy.this.trace(AdtTraceLevel.ERROR, illegalStateException,
//						illegalStateException.getMessage(), new Object[0]);
				throw illegalStateException;
			}
		}

		private String getCommandLine(ProcessBuilder processBuilder) {
			List<String> command = processBuilder.command();
			StringBuilder result = new StringBuilder();
			for (String element : command) {
				result.append(element);
				result.append(" ");
			}
			return result.toString();
		}

		private String getSapGuiServerPath() {
			String guiServerPath = System.getenv("SAPGUI_PIPE_SERVER");
			if (guiServerPath != null) {
//				WinGuiServerProxy.this.trace(
//						"Computed SapGuiServer path: {0} (from environment variable SAPGUI_PIPE_SERVER)",
//						new Object[] { guiServerPath });
				return guiServerPath;
			}

			for (String registryKey : List.of("SOFTWARE\\WOW6432Node\\SAP\\SAP Shared", "SOFTWARE\\SAP\\SAP Shared")) {
				try {
					String installationDirectory = WinRegistry.getStringValue(WinRegistry.RegistryRootKey.HKLM,
							registryKey, "SAPsysdir");
					if (installationDirectory != null) {
						Path guiServerPath1 = Path.of(installationDirectory, new String[] { "SapGuiServer.exe" });
						if (Files.exists(guiServerPath1, new java.nio.file.LinkOption[0])) {
//							WinGuiServerProxy.this.trace("Computed SapGuiServer path: {0} (from registry key {1})",
//									new Object[] { guiServerPath, registryKey });
							return guiServerPath1.toString();
						}
					}
				} catch (WinRegistryException e) {
//					WinGuiServerProxy.this.trace(AdtTraceLevel.ERROR, e, e.getMessage(), new Object[0]);
					throw new IllegalStateException(e);
				}
			}

			for (String programFilesEnvVar : List.of("ProgramFiles(x86)", "ProgramFiles")) {
				String programFilesDirectory = System.getenv(programFilesEnvVar);
				if (programFilesDirectory != null && !programFilesDirectory.isEmpty()) {
					Path guiServerPath1 = Path.of(programFilesDirectory,
							new String[] { "SAP", "FrontEnd", "sapgui", "SapGuiServer.exe" });
					if (Files.exists(guiServerPath1, new java.nio.file.LinkOption[0])) {
//						WinGuiServerProxy.this.trace(
//								"Computed SapGuiServer path: {0} (fallback: default path based on environment variable %{1}%)",
//								new Object[] { guiServerPath, programFilesEnvVar });
						return guiServerPath1.toString();
					}
				}
			}

			IllegalStateException illegalStateException = new IllegalStateException(
					"The SapGuiServer path could not be determined");
//			WinGuiServerProxy.this.trace(AdtTraceLevel.ERROR, illegalStateException, illegalStateException.getMessage(),
//					new Object[0]);
			throw illegalStateException;
		}

		public String getCommandLine() {
			return this.commandLine;
		}

		public void shutdown() {
			if (this.process != null) {
				this.process.destroyForcibly();
			}
		}

		public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
			if (this.process == null) {
				return true;
			}
			return this.process.waitFor(timeout, unit);
		}

		public boolean isRunning() {
			if (this.process == null) {
				return false;
			}
			return this.process.isAlive();
		}
	}

	public void setFocus(String partGuid) {
		if (isSetFocusOn()) {
			WinGuiPipeXmlCommand setFocusCommand = new WinGuiPipeXmlCommand("SetFocus");

			setFocusCommand.setProperty("id", partGuid);
			setFocusCommand.setProperty("focusAction", "2");

			sendCommand(setFocusCommand);
		}
	}

	private boolean isSetFocusOn() {
		String isTurnedOn = AdtConfiguration.getSystemProperty("com.sap.adt.sapgui.ui.win32.setFocus", "ON");
		if (isTurnedOn.equals("ON")) {
			return true;
		}
		return false;
	}

//	public void updateBreakpoints(IProject project) {
////		Set<String> editorIds = this.editorRegistry.getAllEditorIdsForProject(project);
//
////		for (String id : editorIds) {
////			updateBreakpoints(id);
////		}
//	}

//	public void resetBreakpoints(IProject project) {
////		Set<String> editorIds = this.editorRegistry.getAllEditorIdsForProject(project);
////
////		for (String id : editorIds) {
////			resetBreakpoints(id);
////		}
//	}

//	public void reactivateDebugger(IProject project) {
////		Set<String> editorIds = this.editorRegistry.getAllEditorIdsForProject(project);
////		String destinationId = AdtCoreProjectServiceFactory.createCoreProjectService().getDestinationId(project);
////
////		for (String id : editorIds) {
////			reactivateDebugger(destinationId, id);
////		}
//	}

	public void updateBreakpoints(String partGuid) {
		sendOkCode(partGuid, "/H_REFRESH_EXT_BPS");
	}

	public void resetBreakpoints(String partGuid) {
		sendOkCode(partGuid, "/HRESET");
	}

	public void reactivateDebugger(String destinationId, String partGuid) {
//		sendOkCode(partGuid, IEmbeddedSapGui.buildOkCodeReactivateDebugger(destinationId));
	}

	public void detachDebugger(String partGuid) {
		sendOkCode(partGuid, "/HX");
	}

	public void sendOkCode(String partGuid, String okCode) {
		WinGuiPipeXmlCommand startTransactionCommand = new WinGuiPipeXmlCommand("StartOKCode");

		startTransactionCommand.setProperty("id", partGuid);
		startTransactionCommand.setProperty("okcode", okCode);

		sendCommand(startTransactionCommand);
	}

	public void stopTransaction(String partGuid) {
		WinGuiPipeXmlCommand startTransactionCommand = new WinGuiPipeXmlCommand("StopTransaction");

		startTransactionCommand.setProperty("id", partGuid);

		sendCommand(startTransactionCommand);
	}

	public void closeSession(String partGuid) {
		WinGuiPipeXmlCommand closeSessionCommand = new WinGuiPipeXmlCommand("DestroySession");

		closeSessionCommand.setProperty("id", partGuid);

		sendCommand(closeSessionCommand);

//		this.editorRegistry.unregisterEditor(partGuid);
	}

	public void registerServerListener(IGuiServerListener listener) {
		this.serverListener.add(listener);
	}

	public void unregisterServerListener(IGuiServerListener listener) {
		this.serverListener.remove(listener);
	}

	public void registerEventPipeListener(WinGuiEventResponseQueue.IEventPipeListener listener) {
		this.eventPipeListener.add(listener);
	}

	public void unregisterEventPipeListener(WinGuiEventResponseQueue.IEventPipeListener listener) {
		this.eventPipeListener.remove(listener);
	}

//	private void trace(String message, Object... params) {
//		WinGuiFragment.getInstance().trace(AdtTraceLevel.INFO, message, params);
//	}

//	private void trace(AdtTraceLevel level, String message, Object... params) {
//		WinGuiFragment.getInstance().trace(level, message, params);
//	}

//	public void trace(AdtTraceLevel level, Throwable exception, String message, Object... params) {
//		WinGuiFragment.getInstance().trace(level, exception, message, params);
//	}

//	private void traceWithStack(AdtTraceLevel level, String message, Object... params) {
//		WinGuiFragment.getInstance().traceWithStack(level, message, params);
//	}

//	private boolean isTracing(AdtTraceLevel level) {
//		return WinGuiFragment.getInstance().isTracing(level);
//	}

//	public class WinGuiEditorRegistry implements WinGuiEventResponseQueue.IEventPipeListener {
//		private final Map<IProject, Set<String>> map = new HashMap();
//
//		public Set<String> getAllEditorIdsForProject(IProject project) {
//			Set<String> ids = (Set) this.map.get(project);
//			if (ids == null) {
//				return Collections.emptySet();
//			}
//
//			return Collections.unmodifiableSet(ids);
//		}
//
//		protected void registerEditor(IProject project, String id) {
//			Set<String> ids = (Set) this.map.get(project);
//			if (ids == null) {
//				ids = new HashSet<String>();
//				this.map.put(project, ids);
//			}
//			ids.add(id);
//
//			WinGuiServerProxy.this.traceWithStack(AdtTraceLevel.INFO, "New Editor added to registry: {0} {1}",
//					new Object[] { project.getName(), id });
//		}
//
//		protected String unregisterEditor(String id) {
//			for (Set<String> set : this.map.values()) {
//				if (set.remove(id)) {
//					WinGuiServerProxy.this.traceWithStack(AdtTraceLevel.INFO, "Editor removed from registry: {0}",
//							new Object[] { id });
//					return id;
//				}
//			}
//			return null;
//		}
//
//		public void eventReceived(WinGuiPipeEventParser event) {
//			if ("SessionDestroyed".equals(event.getName())) {
//
//				String id = event.getProperty("id".toUpperCase(Locale.ENGLISH));
//
//				unregisterEditor(id);
//			}
//		}
//	}

	private void traceRunningState(boolean serverProcessIsRunning, boolean commandQueueIsRunning,
			boolean eventResponseQueueIsRunning) {
//		StringBuilder builder = new StringBuilder("SAP GUI running state: ");
//		builder.append(serverProcessIsRunning ? "server process is running, " : "server process is not running, ");
//		builder.append(commandQueueIsRunning ? "command queue is running, " : "command queue is not running, ");
//		builder.append(eventResponseQueueIsRunning ? "event queue is running, " : "event queue is not running, ");
//
//		trace(AdtTraceLevel.INFO, builder.toString(), new Object[0]);
	}

	public SAXParser createSaxParser() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			return factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		} catch (SAXException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
