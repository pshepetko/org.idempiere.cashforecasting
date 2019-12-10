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

package org.idempiere.cashforecasting.util;

import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;

/**
 *  SysConfigUtil 
 */
public class SysConfigUtil {

	/**
	 * Gets SysConfig Value for Org_ID
	 * 
	 * @return Value
	 */
	public static String getSysConfigValuebyClient(int Client_ID , String parName) {
		try {
			String result= DB.getSQLValueString  (null, "SELECT TRIM(value) FROM AD_SysConfig WHERE ConfigurationLevel='C' AND AD_Client_ID="+Client_ID+" AND Name='"+parName+"';");
			 if (result == null) result=Msg.translate(Env.getCtx(), "ParameterNotExists");
			return result;
		} catch (Exception e) {
			return "";
		}
	}
}
