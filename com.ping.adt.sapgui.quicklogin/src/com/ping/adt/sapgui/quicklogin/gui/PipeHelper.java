package com.ping.adt.sapgui.quicklogin.gui;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class PipeHelper {
	private static final int MAX_BUFFER_SIZE = 1024;

	public static WinNT.HANDLE createNamedPipe(String pipeName) throws Win32Exception {
		WinBase.SECURITY_ATTRIBUTES securityAttributes = createSecurityAttributesAllowingOnlyTheCurrentUser();

		WinNT.HANDLE pipeHandle = Kernel32.INSTANCE.CreateNamedPipe(pipeName, 3, 14, 1, 1024, 1024, -1,
				securityAttributes);

		if (pipeHandle == Kernel32.INVALID_HANDLE_VALUE) {
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
		}

		return pipeHandle;
	}

	public static void connectNamedPipe(WinNT.HANDLE pipeHandle) throws Win32Exception {
		checkSucceeded(Kernel32.INSTANCE.ConnectNamedPipe(pipeHandle, null));
	}

	public static String readNamedPipe(WinNT.HANDLE pipeHandle) throws Win32Exception {
		boolean hasMoreData;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		do {
			byte[] readBuffer = new byte[1024];
			IntByReference numberOfBytesRead = new IntByReference(0);
			hasMoreData = false;
			if (!Kernel32.INSTANCE.ReadFile(pipeHandle, readBuffer, readBuffer.length, numberOfBytesRead, null)) {
				int lastError = Kernel32.INSTANCE.GetLastError();
				if (lastError == 234) {
					hasMoreData = true;
				} else {
					throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
				}
			}
			outputStream.write(readBuffer, 0, numberOfBytesRead.getValue());
		} while (hasMoreData);
		return new String(outputStream.toByteArray(), StandardCharsets.UTF_16LE);
	}

	public static void writeNamedPipe(WinNT.HANDLE pipeHandle, String data) throws Win32Exception {
		byte[] buffer = data.getBytes(StandardCharsets.UTF_16LE);
		checkSucceeded(Kernel32.INSTANCE.WriteFile(pipeHandle, buffer, buffer.length, new IntByReference(0), null));
	}

	public static void disconnectNamedPipe(WinNT.HANDLE pipeHandle) throws Win32Exception {
		checkSucceeded(Kernel32.INSTANCE.DisconnectNamedPipe(pipeHandle));
	}

	public static void closeHandle(WinNT.HANDLE handle) throws Win32Exception {
		checkSucceeded(Kernel32.INSTANCE.CloseHandle(handle));
	}

	public static long getNamedPipeClientProcessId(WinNT.HANDLE pipeHandle) throws Win32Exception {
		WinDef.ULONGByReference processId = new WinDef.ULONGByReference();
		checkSucceeded(Kernel32.INSTANCE.GetNamedPipeClientProcessId(pipeHandle, processId));
		return processId.getValue().longValue();
	}

	private static WinBase.SECURITY_ATTRIBUTES createSecurityAttributesAllowingOnlyTheCurrentUser() {
		var currentUserAccount = getCurrentUserAccount();

		WinNT.PSID sid = new WinNT.PSID(currentUserAccount.sid);

		int aclSize = Native.getNativeSize(WinNT.ACL.class, null)
				+ Advapi32Util.getAceSize(Advapi32.INSTANCE.GetLengthSid(sid));
		WinNT.ACL acl = new WinNT.ACL(aclSize);
		checkSucceeded(Advapi32.INSTANCE.InitializeAcl(acl, aclSize, 2));
		checkSucceeded(Advapi32.INSTANCE.AddAccessAllowedAce(acl, 2, -1073741824, sid));

		WinNT.SECURITY_DESCRIPTOR securityDescriptor = new WinNT.SECURITY_DESCRIPTOR(65536);
		checkSucceeded(Advapi32.INSTANCE.InitializeSecurityDescriptor(securityDescriptor, 1));
		checkSucceeded(Advapi32.INSTANCE.SetSecurityDescriptorDacl(securityDescriptor, true, acl, false));

		WinBase.SECURITY_ATTRIBUTES securityAttributes = new WinBase.SECURITY_ATTRIBUTES();
		securityAttributes.dwLength = new WinDef.DWORD(securityDescriptor.size());
		securityAttributes.lpSecurityDescriptor = securityDescriptor.getPointer();

		return securityAttributes;
	}

	private static Advapi32Util.Account getCurrentUserAccount() {
		var tokenReference = new WinNT.HANDLEByReference();
		WinNT.HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
		Exception err = null;
		try {
			checkSucceeded(Advapi32.INSTANCE.OpenProcessToken(processHandle, 10, tokenReference));
			return Advapi32Util.getTokenAccount(tokenReference.getValue());
		} catch (Win32Exception e) {
			err = e;
			throw e;
		} finally {
			if (!WinBase.INVALID_HANDLE_VALUE.equals(tokenReference.getValue())) {
				try {
					Kernel32Util.closeHandle(tokenReference.getValue());
				} catch (Win32Exception e) {
					if (err == null) {
						err = e;
					} else {
						err.addSuppressed(e);
					}
				}
			}
		}
	}

	private static void checkSucceeded(boolean result) {
		if (!result)
			throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
	}
}