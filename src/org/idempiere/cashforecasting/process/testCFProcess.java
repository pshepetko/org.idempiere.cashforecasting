/**
 * This file is part of iDempiere ERP <http://www.idempiere.org>.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Copyright (c) 2016 Saúl Piña <sauljabin@gmail.com>.
 */

package org.idempiere.cashforecasting.process;


import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.idempiere.cashforecasting.base.CustomProcess;

/**  
 * Process create Cash Forecasting
 */
public class testCFProcess extends CustomProcess  {

	private String		processVerNo = "[v.1.00] ";
	private static String 		result_msg="Cash Forecasting: ";
	private int 		p_C_BP_Group_ID=0;


	protected void prepare() {
			//	Parameter
			ProcessInfoParameter[] para = getParameter();
			for (int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				if (para[i].getParameter() == null && para[i].getParameter_To() == null)
					;
				else if (name.equals("C_BP_Group_ID"))
					p_C_BP_Group_ID =  para[i].getParameterAsInt();
				else
					log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
	}
	
	protected String doIt() throws Exception {
     
		return processVerNo+result_msg ;
	} 

}
