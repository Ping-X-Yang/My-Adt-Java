package com.ping.adt.sapgui.quicklogin.gui;

import com.sap.adt.logging.AdtTraceLevel;
// import com.sap.adt.sapgui.ui.internal.win32.Messages;
//import com.sap.adt.sapgui.ui.internal.win32.WinGuiFragment;
//import com.sap.adt.sapgui.ui.internal.win32.embedding.WinGuiNavigationEventHandler;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.SAXParser;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;

public class WinGuiEventResponseQueue extends Job {
	private static final String EXTERNAL_WINDOW_ID_SUFFIX = "-External";
	private final WinNT.HANDLE eventPipeHandle;
	private final ConcurrentMap<Long, PendingEditorInfo> pendingEditors = new ConcurrentHashMap();

	protected List<IEventPipeListener> listeners = new CopyOnWriteArrayList();

	protected IJobChangeListener testJobChangeListener;

	private final SAXParser saxParser;

	public WinGuiEventResponseQueue(String name, WinNT.HANDLE eventPipeHandle, SAXParser parser) {
		super(name);
		this.eventPipeHandle = eventPipeHandle;
		setSystem(true);
		this.saxParser = parser;
	}

	public void registerPendingEditor(long windowHandle, String editorId, Runnable callback) {
		this.pendingEditors.put(Long.valueOf(windowHandle), new PendingEditorInfo(editorId, callback));
	}

	protected IStatus run(IProgressMonitor monitor) {
		while (!monitor.isCanceled()) {

			try {
//				trace("Pipe queue: waiting for events", new Object[0]);

				String eventPipeResult = readEventPipe();

//				trace("Event Pipe: event received\n{0}", new Object[] { eventPipeResult });

				if (eventPipeResult.length() > 0) {

					WinGuiPipeEventParser event = new WinGuiPipeEventParser(eventPipeResult, this.saxParser);
					String eventName = event.getName();

					WinGuiPipeXmlResponse response = new WinGuiPipeXmlResponse(eventName);
					response.setProperty("hr", "S_OK");

					if (eventName.equals("SessionCreated")) {

						long handle = 0L;

						String guiHandleProperty = event.getProperty("hwndMainFrame");

						if (guiHandleProperty != null) {
							long guiHandle = Long.parseLong(guiHandleProperty);
							handle = getParentWindowHandle(guiHandle);
						}

						String editorId = null;
						PendingEditorInfo pendingEditorInfo = (PendingEditorInfo) this.pendingEditors
								.remove(Long.valueOf(handle));
						if (pendingEditorInfo != null) {
							editorId = pendingEditorInfo.editorId;
							pendingEditorInfo.callback.run();
						}

//						trace("Created new gui with main frame {0} in editor frame {1} with editor id {2}",
//								new Object[] { guiHandleProperty, Long.valueOf(handle), editorId });

						if (editorId == null) {

							editorId = createIdForExternalGuiWindow();
//							trace("Created new editor id {0}", new Object[] { editorId });
							registerEventPipeListener(new SimplePipeEventListenerForExternalWindows(editorId));
						}
						response.setProperty("id".toUpperCase(Locale.ENGLISH), editorId);
						response.setProperty("setForegroundWindow", "1");

						event.setProperty("id".toUpperCase(Locale.ENGLISH), editorId);
					}

					String responseXml = response.serialize();

					PipeHelper.writeNamedPipe(this.eventPipeHandle, responseXml);

//					trace("Response to event pipe:\n{0}", new Object[] { responseXml });

					notifyEventPipeListeners(event);
					continue;
				}
//				trace("Reading the event pipe resulted in an empty result.", new Object[0]);
				return Status.CANCEL_STATUS;
			} catch (Exception e) {
				String message = "Error accessing the SAP GUI event pipe, sapguiserver.exe might have crashed";
				if (e instanceof Win32Exception && ((Win32Exception) e).getErrorCode() == 109) {
//					traceError(
//							"{0}. Error is ignored, because sapguiserver.exe shutdown has been triggered / job was cancelled / SAP GUI 7.70 decided to shutdown sapguiserver.exe.",
//							e, new Object[] { message });
					throw new OperationCanceledException();
				}
//				traceError(message, e, new Object[0]);
				return new Status(4, "com.sap.adt.sapgui.ui", message, e);
			}
		}

		return Status.OK_STATUS;
	}

	protected long getParentWindowHandle(long windowHandle) {
		long window;
		try {
			try {
				Method method = org.eclipse.swt.internal.win32.OS.class.getMethod("GetParent",
						new Class[] { int.class });
				int windowHandle32 = (int) windowHandle;
				window = ((Integer) method.invoke(null, new Object[] { Integer.valueOf(windowHandle32) })).intValue();
			} catch (NoSuchMethodException noSuchMethodException) {
				Method method = org.eclipse.swt.internal.win32.OS.class.getMethod("GetParent",
						new Class[] { long.class });
				window = ((Long) method.invoke(null, new Object[] { Long.valueOf(windowHandle) })).longValue();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return window;
	}

	private String createIdForExternalGuiWindow() {
		return UUID.randomUUID().toString() + "-External";
	}

	private String readEventPipe() {
		StringBuilder readXml = new StringBuilder();
		String readChunk = null;
		while (!readXml.toString().contains("</SAPGUI:Event>") && !"".equals(readChunk)) {
			readChunk = PipeHelper.readNamedPipe(this.eventPipeHandle);
			readXml.append(readChunk);
		}

		return readXml.toString();
	}
	public void notifyEventPipeListeners(final WinGuiPipeEventParser event) {
		Job pipeEventNotification = new Job("事件响应队列_事件通知") {

			protected IStatus run(IProgressMonitor monitor) {
				for (WinGuiEventResponseQueue.IEventPipeListener listener : WinGuiEventResponseQueue.this.listeners) {
					try {
						listener.eventReceived(event);
					} catch (RuntimeException e) {
//						WinGuiEventResponseQueue.this.traceError("Exception {0} in listener {1}", e,
//								new Object[] { e, listener });
					}
				}

				return Status.OK_STATUS;
			}
		};

		if (this.testJobChangeListener != null) {
			pipeEventNotification.addJobChangeListener(this.testJobChangeListener);
		}

		pipeEventNotification.schedule();
	}

	public void registerEventPipeListener(IEventPipeListener listener) {
		this.listeners.add(listener);
	}

	public void unregisterEventPipeListener(IEventPipeListener listener) {
		this.listeners.remove(listener);
	}

	private static class PendingEditorInfo {
		public final String editorId;
		public final Runnable callback;

		public PendingEditorInfo(String editorId, Runnable callback) {
			this.editorId = editorId;
			this.callback = callback;
		}
	}

	public class SimplePipeEventListenerForExternalWindows implements IEventPipeListener {
		public SimplePipeEventListenerForExternalWindows(String guid) {
//			this.navigationEventHandler = new WinGuiNavigationEventHandler();

			this.guid = guid;
		}

//		private WinGuiNavigationEventHandler navigationEventHandler;

		public void eventReceived(WinGuiPipeEventParser event) {
			String id = event.getProperty("id".toUpperCase(Locale.ENGLISH));

			if (id.equals(this.guid)) {
				if ("InvokeLSAPI".equals(event.getName())) {

//					this.navigationEventHandler.sendClientEvent(event);
				} else if ("SessionDestroyed".equals(event.getName())) {

//					WinGuiEventResponseQueue.this.unregisterEventPipeListener(this);
				}
			}
		}

		private final String guid;

//		public void injectNavigationEventHandler(WinGuiPipeEventParser handler) {
//			this.navigationEventHandler = handler;
//		}
	}

	protected class ListenerReference<T> extends Object {
		private T target;
		private final T key;

		public ListenerReference(T target) {
			this.target = target;
			this.key = target;
		}

		public void clear() {
			this.target = null;
		}

		public T getKey() {
			return (T) this.key;
		}

		public T get() {
			return (T) this.target;
		}
	}

	public static interface IEventPipeListener {
		void eventReceived(WinGuiPipeEventParser param1WinGuiPipeEventParser);
	}
}
