package com.guosen.android.system;

import java.util.Hashtable;

import com.eno.kjava.net.ENOCommServ;
import com.eno.kjava.system.ENODataEncoder;

public class SystemHUB2 extends SystemHUB {
	  public static void initialize()
	  {
/*	    if (paramContext == null)
	      return;
	    mCtx = paramContext;
	    m_param = new SysParameter(mCtx);
	    config = new SysConfig(mCtx);
	    if ("1".equals(m_param.getRecord(17)))
	      m_fSmallFont = true;*/
	    m_encoder = new ENODataEncoder();
	    m_ConnType = 0;
	    m_comServ = new ENOCommServ();
	    m_comServ.start();
	    m_activeMenu = new Hashtable();
	  }
}
