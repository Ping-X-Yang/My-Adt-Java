package com.ping.adt.core.request.workbench.ui.events;

import java.util.ArrayList;
import java.util.List;

import com.ping.adt.core.request.workbench.ui.model.Output.Data;

public class Message {
	public String topic = "";
	public List<Data> data = new ArrayList<Data>();
}
