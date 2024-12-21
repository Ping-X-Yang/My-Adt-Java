package com.ping.adt.sapgui.quicklogin.gui;

import com.sap.adt.logging.AdtTraceLevel;
import com.sun.jna.platform.win32.Win32Exception;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.xml.parsers.SAXParser;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class WinGuiCommandQueue extends Job {
	public static final int COMMAND_QUEUE_CAPACITY = 1000;
	public static final String CANCEL_COMMAND = "<CancelQueue/>";
	private final RandomAccessFile commandPipe;
	private final BlockingQueue<String> queue = new LinkedBlockingQueue(1000);
	private final SAXParser saxParser;
	Consumer<WinGuiPipeXmlCommandResponse> responseConsumer;

	public WinGuiCommandQueue(String name, RandomAccessFile commandPipe, SAXParser parser) {
		super(name);
		this.commandPipe = commandPipe;
		setSystem(true);
		this.saxParser = parser;
	}

	protected IStatus run(IProgressMonitor monitor) {
		while (!monitor.isCanceled()) {
			try {
				String commandXml = (String) this.queue.take();

				if ("<CancelQueue/>".equals(commandXml)) {

					continue;
				}

				this.commandPipe.write(commandXml.getBytes("UTF-16LE"));
				//isTracing(AdtTraceLevel.INFO)
				if (true) {
					String commandWithMaskedPassword = hidePassword(commandXml);
				}

				String rawResponse = readResponse();

				WinGuiPipeXmlCommandResponse response = new WinGuiPipeXmlCommandResponse(rawResponse, this.saxParser);
				String hrProperty = response.getProperty("hr");
				if (hrProperty != null && !hrProperty.equals("0")) {
					String errorDescription = response.getProperty("errorDescription");
				}

				if (this.responseConsumer != null) {
					this.responseConsumer.accept(response);
				}
			} catch (Exception e) {
				String message = "Error accessing the SAP GUI command pipe, sapguiserver.exe might have crashed";
				if (e instanceof Win32Exception && ((Win32Exception) e).getErrorCode() == 109) {
					throw new OperationCanceledException();
				}
				return new Status(4, "com.sap.adt.sapgui.ui", message, e);
			}
		}

		return Status.OK_STATUS;
	}

	protected static String hidePassword(String commandXml) {
		String patternString = "(password\">)(.+)(</SAPGUI:param>)";

		return commandXml.replaceAll(patternString, "password\">*******</SAPGUI:param>");
	}

	public void shutdown() {
		cancel();

		this.queue.offer("<CancelQueue/>");
	}

	private String readResponse() throws IOException, UnsupportedEncodingException {
		byte[] buffer = new byte[65535];
		int read = this.commandPipe.read(buffer);
		return new String(buffer, 0, read, "UTF-16LE");
	}

	public void sendCommand(String command) {
		//isTracing(AdtTraceLevel.INFO)
		if (true) {
			String commandWithMaskedPassword = hidePassword(command);
		}
		boolean offered = this.queue.offer(command);
		if (!offered) {

			IllegalStateException exception = new IllegalStateException(
					"Command queue - Gui command queue overflow, command not sent");
			throw exception;
		}
	}
}
