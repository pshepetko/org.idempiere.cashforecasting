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
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for C_CF_Period
 *  @author iDempiere (generated) 
 *  @version Release 6.2 - $Id$ */
public class X_C_CF_Period extends PO implements I_C_CF_Period, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190606L;

    /** Standard Constructor */
    public X_C_CF_Period (Properties ctx, int C_CF_Period_ID, String trxName)
    {
      super (ctx, C_CF_Period_ID, trxName);
      /** if (C_CF_Period_ID == 0)
        {
			setC_CF_Period_ID (0);
        } */
    }

    /** Load Constructor */
    public X_C_CF_Period (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_C_CF_Period[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Cash Forecasting Period.
		@param C_CF_Period_ID Cash Forecasting Period	  */
	public void setC_CF_Period_ID (int C_CF_Period_ID)
	{
		if (C_CF_Period_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_CF_Period_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_CF_Period_ID, Integer.valueOf(C_CF_Period_ID));
	}

	/** Get Cash Forecasting Period.
		@return Cash Forecasting Period	  */
	public int getC_CF_Period_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_CF_Period_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set C_CF_Period_UU.
		@param C_CF_Period_UU C_CF_Period_UU	  */
	public void setC_CF_Period_UU (String C_CF_Period_UU)
	{
		set_ValueNoCheck (COLUMNNAME_C_CF_Period_UU, C_CF_Period_UU);
	}

	/** Get C_CF_Period_UU.
		@return C_CF_Period_UU	  */
	public String getC_CF_Period_UU () 
	{
		return (String)get_Value(COLUMNNAME_C_CF_Period_UU);
	}

	public I_C_CF_PeriodDefinition getC_CF_PeriodDefinition() throws RuntimeException
    {
		return (I_C_CF_PeriodDefinition)MTable.get(getCtx(), I_C_CF_PeriodDefinition.Table_Name)
			.getPO(getC_CF_PeriodDefinition_ID(), get_TrxName());	}

	/** Set Period Definition.
		@param C_CF_PeriodDefinition_ID Period Definition	  */
	public void setC_CF_PeriodDefinition_ID (int C_CF_PeriodDefinition_ID)
	{
		if (C_CF_PeriodDefinition_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_CF_PeriodDefinition_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_CF_PeriodDefinition_ID, Integer.valueOf(C_CF_PeriodDefinition_ID));
	}

	/** Get Period Definition.
		@return Period Definition	  */
	public int getC_CF_PeriodDefinition_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_CF_PeriodDefinition_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set End Date.
		@param EndDate 
		Last effective date (inclusive)
	  */
	public void setEndDate (Timestamp EndDate)
	{
		set_Value (COLUMNNAME_EndDate, EndDate);
	}

	/** Get End Date.
		@return Last effective date (inclusive)
	  */
	public Timestamp getEndDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_EndDate);
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

	/** Set Period No.
		@param PeriodNo 
		Unique Period Number
	  */
	public void setPeriodNo (int PeriodNo)
	{
		set_Value (COLUMNNAME_PeriodNo, Integer.valueOf(PeriodNo));
	}

	/** Get Period No.
		@return Unique Period Number
	  */
	public int getPeriodNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_PeriodNo);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Start Date.
		@param StartDate 
		First effective day (inclusive)
	  */
	public void setStartDate (Timestamp StartDate)
	{
		set_Value (COLUMNNAME_StartDate, StartDate);
	}

	/** Get Start Date.
		@return First effective day (inclusive)
	  */
	public Timestamp getStartDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StartDate);
	}
}