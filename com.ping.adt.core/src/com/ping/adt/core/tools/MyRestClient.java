package com.ping.adt.core.tools;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.alibaba.fastjson2.JSON;
import com.sap.adt.communication.message.IResponse;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IQueryParameter;
import com.sap.adt.communication.resources.IRestResource;
import com.sap.adt.communication.resources.QueryParameter;
import com.sap.adt.compatibility.discovery.AdtDiscoveryFactory;
import com.sap.adt.compatibility.discovery.IAdtDiscovery;
import com.sap.adt.compatibility.discovery.IAdtDiscoveryCollectionMember;
import com.sap.adt.compatibility.model.templatelink.IAdtTemplateLink;
import com.sap.adt.compatibility.uritemplate.IAdtUriTemplate;
import com.sap.adt.project.IAdtCoreProject;

public class MyRestClient {

	private IAdtDiscoveryCollectionMember collectionMember;
	private String resourceName;
	private Map<String, Object> params;
	private IAdtCoreProject project;

	public MyRestClient(String serviceName) {
		this.resourceName = "";
		this.params = new HashMap<String, Object>();
		this.project = MyAdtTools.getActiveAdtProject();

		IAdtDiscovery discovery = AdtDiscoveryFactory.createDiscovery(this.project.getDestinationId(),
				URI.create("/sap/bc/adt/discovery"));
		this.collectionMember = discovery.getCollectionMember("http://ping.com/adt/plugins", serviceName,
				new NullProgressMonitor());
	}

	public void setResource(String resourceName) {
		this.resourceName = resourceName;
	}

	public void addParam(String key, Object Value) {
		params.put(key, Value);
	}

	public void clearParams() {
		params.clear();
	}

	public IResponse get() {
		IRestResource resource = createResource();
		return resource.get(null, IResponse.class);
	}

	public <T> T get(Class<T> clazz) {
		IRestResource resource = createResource();
		IResponse res = resource.get(null, IResponse.class);
		return JSON.parseObject(res.getBody().toString(), clazz);
	}

	public <T> T get(Class<T> clazz, List<IQueryParameter> queries) {
		int index = 0;
		IQueryParameter[] queryArray = new IQueryParameter[100];
		for (IQueryParameter query : queries) {
			queryArray[index++] = query;
		}
		
		IRestResource resource = createResource();
		IResponse res = resource.get(null, IResponse.class, queryArray);
		return JSON.parseObject(res.getBody().toString(), clazz);
	}

	public IResponse post(Object object, Map<String, Object> queries) {
		// 转换查询参数
		List<IQueryParameter> queryArray = new ArrayList<IQueryParameter>();
		for (Entry<String, Object> entry : queries.entrySet()) {
			queryArray.add(new QueryParameter(entry.getKey(), entry.getValue().toString()));
		}

		// 请求资源
		IRestResource resource = createResource();
		return resource.post(null, IResponse.class, JSON.toJSONString(object),
				(IQueryParameter[]) queryArray.toArray());
	}

	public <T> T post(Object object, Class<T> clazz, Map<String, Object> queries) {
		int index = 0;
		// 转换查询参数
		IQueryParameter[] queryArray = new IQueryParameter[100];
//		List<IQueryParameter> queryArray = new ArrayList<IQueryParameter>();
		for (Entry<String, Object> entry : queries.entrySet()) {
//			queryArray.add(new QueryParameter(entry.getKey(), entry.getValue().toString()));
			queryArray[index++] = new QueryParameter(entry.getKey(), entry.getValue().toString());
		}

		// 请求资源
		IRestResource resource = createResource();
		IResponse res = resource.post(null, IResponse.class, JSON.toJSONString(object), queryArray);
		if (res.getStatus() != 200) {
			System.out.println(res.getErrorInfo().toString());
			return null;
		}

		return JSON.parseObject(res.getBody().toString(), clazz);
	}

//	public IResponse get(IQueryParameter arg2) {
//		IRestResource resource = createResource();
//		return resource.get(null, IResponse.class);
//	}

	private IRestResource createResource() {
		String uriString = "";

		try {
			IAdtTemplateLink link = collectionMember.getTemplateLink(this.resourceName);
			IAdtUriTemplate template = link.getUriTemplate();

			// 填充参数
			for (Entry<String, Object> entry : params.entrySet()) {
				template.set(entry.getKey(), entry.getValue());
			}
			uriString = template.expand();
		} catch (Exception e) {
		}

		URI uri = URI.create(uriString);

		IRestResource resource = AdtRestResourceFactory.createRestResourceFactory()
				.createResourceWithStatelessSession(uri, project.getDestinationId());

		return resource;
	}

}
