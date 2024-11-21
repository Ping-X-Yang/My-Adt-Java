package com.ping.adt.core.tools;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.sap.adt.communication.message.IResponse;
import com.sap.adt.communication.resources.AdtRestResourceFactory;
import com.sap.adt.communication.resources.IQueryParameter;
import com.sap.adt.communication.resources.IRestResource;
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
		
		IAdtDiscovery discovery = AdtDiscoveryFactory.createDiscovery(this.project.getDestinationId(), URI.create("/sap/bc/adt/discovery"));
		this.collectionMember = discovery.getCollectionMember("http://ping.com/adt/plugins", serviceName, new NullProgressMonitor());
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
	
//	public IResponse get(IQueryParameter arg2) {
//		IRestResource resource = createResource();
//		return resource.get(null, IResponse.class);
//	}
	
	private IRestResource createResource() {
		IAdtTemplateLink link = collectionMember.getTemplateLink(this.resourceName);
		IAdtUriTemplate template = link.getUriTemplate();
		
		//填充参数
		for (Entry<String, Object> entry : params.entrySet()) {
			template.set(entry.getKey(), entry.getValue());
		}
		
		URI uri = URI.create(template.expand());
		
		IRestResource resource = AdtRestResourceFactory.createRestResourceFactory().createResourceWithStatelessSession(uri, project.getDestinationId());
		
		return resource;
	}
	
}
