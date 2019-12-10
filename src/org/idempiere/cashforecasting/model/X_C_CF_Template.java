/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.idempiere.cashforecasting.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for C_CF_Template
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_C_CF_Template extends PO implements I_C_CF_Template, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190606L;

    /** Standard Constructor */
    public X_C_CF_Template (Properties ctx, int C_CF_Template_ID, String trxName)
    {
      super (ctx, C_CF_Template_ID, trxName);
      /** if (C_CF_Template_ID == 0)
        {
			setC_CF_Template_ID (0);
        } */
    }

    /** Load Constructor */
    public X_C_CF_Template (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_C_CF_Template[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Cash Forecasting Template.
		@param C_CF_Template_ID Cash Forecasting Template	  */
	public void setC_CF_Template_ID (int C_CF_Template_ID)
	{
		if (C_CF_Template_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_CF_Template_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_CF_Template_ID, Integer.valueOf(C_CF_Template_ID));
	}

	/** Get Cash Forecasting Template.
		@return Cash Forecasting Template	  */
	public int getC_CF_Template_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_CF_Template_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set C_CF_Template_UU.
		@param C_CF_Template_UU C_CF_Template_UU	  */
	public void setC_CF_Template_UU (String C_CF_Template_UU)
	{
		set_ValueNoCheck (COLUMNNAME_C_CF_Template_UU, C_CF_Template_UU);
	}

	/** Get C_CF_Template_UU.
		@return C_CF_Template_UU	  */
	public String getC_CF_Template_UU () 
	{
		return (String)get_Value(COLUMNNAME_C_CF_Template_UU);
	}

	public org.compiere.model.I_C_Currency getC_Currency() throws RuntimeException
    {
		return (org.compiere.model.I_C_Currency)MTable.get(getCtx(), org.compiere.model.I_C_Currency.Table_Name)
			.getPO(getC_Currency_ID(), get_TrxName());	}

	/** Set Currency.
		@param C_Currency_ID 
		The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1) 
			set_Value (COLUMNNAME_C_Currency_ID, null);
		else 
			set_Value (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
	}
}