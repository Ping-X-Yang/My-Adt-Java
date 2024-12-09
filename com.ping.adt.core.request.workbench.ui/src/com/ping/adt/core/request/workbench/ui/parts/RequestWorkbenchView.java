package com.ping.adt.core.request.workbench.ui.parts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.alibaba.fastjson2.JSON;
import com.ping.adt.core.request.workbench.ui.events.IRequestEventConstants;
import com.ping.adt.core.request.workbench.ui.events.Message;
import com.ping.adt.core.request.workbench.ui.model.Output;
import com.ping.adt.core.request.workbench.ui.model.Output.Data;
import com.ping.adt.core.request.workbench.ui.model.Output.RequestMessage;

import jakarta.inject.Inject;

public class RequestWorkbenchView {
	static RequestWorkbenchView requestWorkbenchView;
	
	Composite parent;

	
	SashForm parentSashForm;
	Composite mainContainer;
	Composite secondaryContainer;
	SourceViewer logViewer;
	Document document;
	
	
	RequestWorkbenchView(Composite parent){
		this.parent = parent;
		
	}
	
	public void createContents() {
		
		initContainers();
		
		createLogViewer(secondaryContainer);
		
	}
	
	
	
	
	
	private void initContainers() {
		parentSashForm = new SashForm(parent, SWT.VERTICAL);
		mainContainer = new Composite(parentSashForm, SWT.BORDER);
		secondaryContainer = new Composite(parentSashForm, SWT.BORDER);
		mainContainer.setLayout(new FillLayout());
		secondaryContainer.setLayout(new FillLayout());
		parentSashForm.setWeights(new int[] {68, 32});
	}

	private void createLogViewer(Composite parent) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		logViewer = new SourceViewer(parent, null, SWT.BORDER | SWT.V_SCROLL);
		logViewer.setEditable(false);
		document = new Document();
		logViewer.setDocument(document);
	}
	
	
	public void receiveLogMessages(Message message) {
		StyleRange range;
		TextPresentation textPresentation;
		
		if (message.data == null || message.data.size() == 0) {
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		
		try {
			String title = dateFormat.format(new Date())+"\n"+
						   message.topic+"\n";
			int offset = title.length();
			
			//时间戳
			document.replace(0, 0, title);
			for (Data data : message.data) {
				String line = "";
				line = String.format("    [%s]  %-12s  %s\n", data.request, data.username, data.text);
				
				//设置请求号样式
				textPresentation = new TextPresentation();
				if (data.message.size() == 0) {
					range = new StyleRange(offset + 5, data.request.length(), new Color(0,210,0),null,SWT.BOLD);
				}else {
					range = new StyleRange(offset + 5, data.request.length(), new Color(230,0,0),null,SWT.BOLD);
				}
				
				//添加错误消息
				for (RequestMessage msg : data.message) {
					line = line + String.format("        %s\n", msg.message);
				}
				
				//末尾空行
				line = line + "\n\n";
				
				
				document.replace(offset, 0, line);
				
				textPresentation.addStyleRange(range);
				logViewer.changeTextPresentation(textPresentation, true);
			}			
			

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	
	
	
//	public static RequestWorkbenchView getInstance(Composite parent){
//		if (requestWorkbenchView == null) {
//			requestWorkbenchView = new RequestWorkbenchView(parent);
//		}
//		return requestWorkbenchView;
//	}
}
