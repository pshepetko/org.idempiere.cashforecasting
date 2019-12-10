package org.idempiere.cashforecasting.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MCFPeriodDefinition extends X_C_CF_PeriodDefinition{

	/**
	 * 
	 */
	private static final long serialVersionUID = -78512127568095470L;


	public MCFPeriodDefinition(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}


	public MCFPeriodDefinition(Properties ctx, int getID, String trxName) {
		super(ctx, getID, trxName);
		// TODO Auto-generated constructor stub
	}

}
