package org.idempiere.cashforecasting.form;

import java.io.IOException;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.panel.IFormController;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Iframe;


public class OLAPViewForm  implements IFormController,EventListener {

 	private CustomForm form = new CustomForm();
	private Iframe iframe = new Iframe();
	 
	private Button bRefresh = new Button();

	private Label lversion = new Label(" [v1.00]");
	
	private String OLAP_URL = null;

	public OLAPViewForm()
	{
		try
		{
			dynInit();
			zkInit();
			dynInit();
			Borderlayout contentLayout = new Borderlayout();
			ZKUpdateUtil.setWidth(contentLayout, "100%");
			ZKUpdateUtil.setHeight(contentLayout, "100%");
 			
			bRefresh.setLabel(Msg.translate(Env.getCtx(), "Refresh"));
			bRefresh.addActionListener(this);
			form.appendChild(bRefresh);
								
			form.appendChild(lversion);
			
			form.appendChild(iframe); 
		}
		catch(Exception e)
		{
			//log.log(Level.SEVERE, "", e);
		}
	}
	

	private void zkInit() throws Exception
	{
		int height = Double.valueOf(SessionManager.getAppDesktop().getClientInfo().desktopHeight * 0.8).intValue();
 
		height = height - 5;
		iframe.setHeight(height + "px");
		iframe.setWidth("100%");
		iframe.setAutohide(true);
		form.setWidth("100%");
		form.setHeight("100%");
		form.appendChild(iframe);
		
		if (!OLAP_URL.equals(null))
		{
		 	iframe.setSrc(OLAP_URL);		
			iframe.invalidate();
		}

	}
	
	/**
	 * 	Dynamic Init.
	 */
	private void dynInit()
	{
		OLAP_URL=getURL("OLAP_URL");
	}

		
	
	/**
	 * 	Dispose
	 */
	public void dispose()
	{
		SessionManager.getAppDesktop().closeActiveWindow();
	}	//	dispose
	
	public ADForm getForm() {
		return form;
	}
	
	/**************************************************************************
	 *  Action Listener
	 *  @param e event
	 * @throws IOException 
	 */
	public void onEvent(Event e) throws IOException
	{	
		if (e.getTarget().equals(bRefresh)) 
		{			
			dynInit();
			if (!OLAP_URL.equals(null))
			{
			 	iframe.setSrc(OLAP_URL);		
				iframe.invalidate();
			}
		}

	}   //  onEvent
	
	public String getURL(String name) 	{
		return DB.getSQLValueString  (null, "SELECT Value FROM AD_SysConfig WHERE name='"+name+"';");
	}
}
