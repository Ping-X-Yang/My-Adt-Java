package com.ping.adt.structure.browser.ui.parts;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

public class FieldNode {
	
	/**
	 * 字段名
	 */
	@JSONField(name = "fieldName")
	public String fieldName = "";
	
	
	/**
	 * 数据元素
	 */
	@JSONField(name = "rollName")
	public String rollName = "";
	
	
	/**
	 * 数据类型
	 */
	@JSONField(name = "dataType")
	public String dataType = "";
	
	
	/**
	 * 长度
	 */
	@JSONField(name = "length")
	public int length = 0;
	
	
	/**
	 * 小数位数
	 */
	@JSONField(name = "decimals")
	public int decimals = 0;
	
	
	/**
	 * 描述
	 */
	@JSONField(name = "text")
	public String text = "";
	
	
	/**
	 * 组件类型
	 */
	@JSONField(name = "componentType")
	public String componentType = "";
	
	
	/**
	 * 主键标识
	 */
	@JSONField(name = "keyFlag")
	public boolean keyFlag = false;
	
	
	/**
	 * 父节点
	 */
	public FieldNode parent;
	
	
	/**
	 * 子节点
	 */
	public List<FieldNode> children = new ArrayList<FieldNode>();
}
