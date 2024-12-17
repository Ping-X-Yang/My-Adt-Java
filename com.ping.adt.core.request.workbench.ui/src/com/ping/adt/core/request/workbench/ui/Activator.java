package com.ping.adt.core.request.workbench.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

//public class Activator implements BundleActivator {
//
//	private static BundleContext context;
//
//	static BundleContext getContext() {
//		return context;
//	}
//
//	public void start(BundleContext bundleContext) throws Exception {
//		Activator.context = bundleContext;
//	}
//
//	public void stop(BundleContext bundleContext) throws Exception {
//		Activator.context = null;
//	}
//
//}



 import org.eclipse.core.runtime.Status;
 import org.eclipse.jface.resource.ImageDescriptor;
 import org.eclipse.jface.resource.ImageRegistry;
 import org.eclipse.jface.viewers.DecorationOverlayIcon;
 import org.eclipse.swt.graphics.Image;
 import org.eclipse.ui.plugin.AbstractUIPlugin;
 import org.osgi.framework.BundleContext;
 
 
 
 
 
 
 
 
 
 
 
 public class Activator
   extends AbstractUIPlugin
 {
   public static final String PLUGIN_ID = "com.sap.adt.tm.ui";
   private static final String IMAGE_PATH = "$nl$/icons/";
   public static final String IMAGE_TRANSPORT_REQUEST = "$nl$/icons/obj/transport_request.png";
   public static final String IMAGE_TRANSPORT_TASK = "$nl$/icons/obj/transport_task.png";
   public static final String IMAGE_RELEASED_TRANSPORT_TASK_OVERLAY = "$nl$/icons/taskoverlay_released.png";
   public static final String IMAGE_RELEASED_TRANSPORT_REQUEST_OVERLAY = "$nl$/icons/requestoverlay_released.png";
   public static final String IMAGE_MODIFIABLE_TRANSPORT_TASK_OVERLAY = "$nl$/icons/taskoverlay_modifiable.png";
   public static final String IMAGE_PROTECTED_TRANSPORT_REQUEST_LOCK_OVERLAY = "$nl$/icons/taskoverlay_lock.png";
   public static final String IMAGE_RELEASE_ACTION = "$nl$/icons/etool/transport.png";
   public static final String IMAGE_RELEASE_ACTION_DISABLED = "$nl$/icons/dtool/transport.png";
   public static final String IMAGE_ADD_USER = "$nl$/icons/obj/facet_user.png";
   public static final String IMAGE_LOCK = "$nl$/icons/etool/lock.png";
   public static final String IMAGE_OPEN_REQUEST_OR_TASK = "$nl$/icons/etool/Open_Request_Task.png";
   public static final String IMAGE_NEW_TRANSPORT_REQUEST = "$nl$/icons/etool/New_Transport_Request.png";
   public static final String IMAGE_CHANGE_OWNER = "$nl$/icons/obj/Change_Owner.png";
   public static final String IMAGE_REASSIGN_TASK = "$nl$/icons/obj/Reassign_Task.png";
   public static final String IMAGE_RUN_CONSISTENCY_CHECK = "$nl$/icons/etool/check.png";
   public static final String IMAGE_RUN_CONSISTENCY_CHECK_DISABLED = "$nl$/icons/dtool/check.png";
   public static final String IMAGE_SORT_AND_COMPRESS = "$nl$/icons/obj/Sort_And_Compress.png";
   public static final String IMAGE_WIZARD_BANNER = "$nl$/icons/wizban/new_req_wiz.png";
   public static final String IMAGE_OVERVIEW_PAGE = "$nl$/icons/etool/page_obj.png";
   public static final String IMAGE_EDIT = "$nl$/icons/etool/edit_textsymbol.png";
   public static final String IMAGE_ALL_OBJECTS = "$nl$/icons/obj/AbapDocument.png";
   public static final String IMAGE_OBJECT_KEYS_EDITOR = "$nl$/icons/obj/database_table_index.png";
   public static final String IMAGE_PREPARE_RELEASE = "$nl$/icons/etool/gotoobj_tsk.png";
   public static final String IMAGE_REPOSITORY = "$nl$/icons/obj/CheckinStatusInSync.png";
   public static final String IMAGE_RELEASED_TRANSPORT_REQUEST_DELETE_OBJECT_OVERLAY = "$nl$/icons/ovr/exemptionRejectedDec.png";
   public static final String IMAGE_PROPERTIES_VIEW = "$nl$/icons/etool/properties.png";
   public static final String IMAGE_SUCCESS_STATUS = "$nl$/icons/etool/success.png";
   public static final String IMAGE_PENDING_STATUS = "$nl$/icons/etool/waiting.png";
   public static final String IMAGE_RELEASE_STARTED_OVERLAY = "$nl$/icons/ovr/waiting_ovr.png";
   public static final String IMAGE_COLLAPSE_ALL = "$nl$/icons/etool/collapseall.png";
   public static final String IMAGE_EXPAND_ALL = "$nl$/icons/etool/expandall.png";
   public static final String IMAGE_GROUP_BY = "$nl$/icons/etool/type_mode.png";
   public static final String IMAGE_OBJECT_GROUP_BY_TYPE = "$nl$/icons/obj/facet_object_type.png";
   public static final String IMAGE_INFO = "$nl$/icons/obj/info_obj.png";
   public static final String CONTEXT_ID_TRANSPORT = "com.sap.adt.tm.ui.context.transportmanagement";
   private static Activator plugin;
   
   public void start(BundleContext context) {
     try {
       super.start(context);
     } catch (Exception e) {
//       getDefault().getLog().log(new Status(4, "com.sap.adt.tm.ui", "Error during start up of TMViewPlugin ", e));
     } 
     plugin = this;
   }
   
   public void stop(BundleContext context) {
     plugin = null;
     try {
       super.stop(context);
     } catch (Exception e) {
//       getDefault().getLog().log(new Status(4, "com.sap.adt.tm.ui", "Error during stop of TMViewPlugin ", e));
     } 
   }
 
   
   public static Activator getDefault() { return plugin; }
 
 
   
//   public TMFeatureManager getTMFeatureManager() { return new TMFeatureManager(); }
 
   
   protected void initializeImageRegistry(ImageRegistry imageRegistry) {
     registerImage(imageRegistry, "$nl$/icons/obj/transport_request.png", "$nl$/icons/obj/transport_request.png");
     registerImage(imageRegistry, "$nl$/icons/etool/Open_Request_Task.png", "$nl$/icons/etool/Open_Request_Task.png");
     registerImage(imageRegistry, "$nl$/icons/obj/transport_task.png", "$nl$/icons/obj/transport_task.png");
     registerImage(imageRegistry, "$nl$/icons/etool/transport.png", "$nl$/icons/etool/transport.png");
     registerImage(imageRegistry, "$nl$/icons/obj/facet_user.png", "$nl$/icons/obj/facet_user.png");
     registerImage(imageRegistry, "$nl$/icons/etool/lock.png", "$nl$/icons/etool/lock.png");
     registerImage(imageRegistry, "$nl$/icons/etool/New_Transport_Request.png", "$nl$/icons/etool/New_Transport_Request.png");
     registerImage(imageRegistry, "$nl$/icons/obj/Change_Owner.png", "$nl$/icons/obj/Change_Owner.png");
     registerImage(imageRegistry, "$nl$/icons/obj/Reassign_Task.png", "$nl$/icons/obj/Reassign_Task.png");
     registerImage(imageRegistry, "$nl$/icons/etool/check.png", "$nl$/icons/etool/check.png");
     registerImage(imageRegistry, "$nl$/icons/obj/Sort_And_Compress.png", "$nl$/icons/obj/Sort_And_Compress.png");
     registerImage(imageRegistry, "$nl$/icons/wizban/new_req_wiz.png", "$nl$/icons/wizban/new_req_wiz.png");
     registerImage(imageRegistry, "$nl$/icons/dtool/transport.png", "$nl$/icons/dtool/transport.png");
     registerImage(imageRegistry, "$nl$/icons/dtool/check.png", "$nl$/icons/dtool/check.png");
     registerImage(imageRegistry, "$nl$/icons/etool/page_obj.png", "$nl$/icons/etool/page_obj.png");
     registerImage(imageRegistry, "$nl$/icons/etool/edit_textsymbol.png", "$nl$/icons/etool/edit_textsymbol.png");
     registerImage(imageRegistry, "$nl$/icons/obj/AbapDocument.png", "$nl$/icons/obj/AbapDocument.png");
     registerImage(imageRegistry, "$nl$/icons/obj/database_table_index.png", "$nl$/icons/obj/database_table_index.png");
     registerImage(imageRegistry, "$nl$/icons/etool/gotoobj_tsk.png", "$nl$/icons/etool/gotoobj_tsk.png");
     registerImage(imageRegistry, "$nl$/icons/obj/CheckinStatusInSync.png", "$nl$/icons/obj/CheckinStatusInSync.png");
     registerImage(imageRegistry, "$nl$/icons/etool/properties.png", "$nl$/icons/etool/properties.png");
     registerImage(imageRegistry, "$nl$/icons/etool/success.png", "$nl$/icons/etool/success.png");
     registerImage(imageRegistry, "$nl$/icons/etool/waiting.png", "$nl$/icons/etool/waiting.png");
     registerImage(imageRegistry, "$nl$/icons/etool/collapseall.png", "$nl$/icons/etool/collapseall.png");
     registerImage(imageRegistry, "$nl$/icons/etool/expandall.png", "$nl$/icons/etool/expandall.png");
     registerImage(imageRegistry, "$nl$/icons/etool/type_mode.png", "$nl$/icons/etool/type_mode.png");
     registerImage(imageRegistry, "$nl$/icons/obj/facet_object_type.png", "$nl$/icons/obj/facet_object_type.png");
     registerImage(imageRegistry, "$nl$/icons/obj/info_obj.png", "$nl$/icons/obj/info_obj.png");
   }
   
   private void registerImage(ImageRegistry registry, String key, String fileName) {
     ImageDescriptor desc = imageDescriptorFromPlugin("com.sap.adt.tm.ui", fileName);
     if (registry.get(key) != null) {
       throw new IllegalStateException("duplicate imageId in image registry");
     }
     registry.put(key, desc);
   }
 
   
   public ImageDescriptor getImageDescriptor(String key) { return getImageRegistry().getDescriptor(key); }
 
   
   public Image decorateImage(Image baseImage, String decoratorKey) {
     Image decoratedImage = getImageRegistry().get(decoratorKey);
     if (decoratedImage == null) {
       DecorationOverlayIcon doi = new DecorationOverlayIcon(baseImage, imageDescriptorFromPlugin("com.sap.adt.tm.ui", decoratorKey), 
           2);
       decoratedImage = doi.createImage();
       getImageRegistry().put(decoratorKey, decoratedImage);
     } 
     return decoratedImage;
   }
 }

