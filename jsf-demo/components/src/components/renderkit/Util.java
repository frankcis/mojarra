/*
 * $Id: Util.java,v 1.1 2003/02/15 00:57:54 rkitain Exp $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// Util.java

package components.renderkit;

import org.mozilla.util.Assert;
import org.mozilla.util.ParameterCheck;

import javax.servlet.ServletContext;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;

import javax.faces.render.RenderKitFactory;
import javax.faces.render.RenderKit;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.tree.TreeFactory;
import javax.faces.context.FacesContextFactory;

import javax.faces.FactoryFinder;
import javax.faces.context.MessageResourcesFactory;
import javax.faces.context.MessageResources;
import javax.faces.context.Message;

import javax.faces.component.UISelectItem;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.SelectItem;

import javax.faces.context.FacesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.Locale;

/**
 *
 *  <B>Util</B> is a class which houses common functionality used by
 *     other classes.
 *
 * @version $Id: Util.java,v 1.1 2003/02/15 00:57:54 rkitain Exp $
 * 
 */

public class Util extends Object
{
//
// Protected Constants
//

//
// Class Variables
//

    /**

    * This array contains attributes that have a boolean value in JSP,
    * but have have no value in HTML.  For example "disabled" or
    * "readonly". <P>

    * @see renderBooleanPassthruAttributes

    */

    private static String booleanPassthruAttributes[] = {
	"disabled",
	"readonly",
        "ismap"
    };
	
    /**

    * This array contains attributes whose value is just rendered
    * straight to the content.  This array should only contain
    * attributes that require no interpretation by the Renderer.  If an
    * attribute requires interpretation by a Renderer, it should be
    * removed from this array.<P>

    * @see renderPassthruAttributes

    */
    private static String passthruAttributes[] = {
	"accesskey",
	"alt",
        "cols",
        "height",
	"lang",
	"longdesc",
	"maxlength",
	"onblur",
	"onchange",
	"onclick",
	"ondblclick",
	"onfocus",
	"onkeydown",
	"onkeypress",
	"onkeyup",
	"onload",
	"onmousedown",
	"onmousemove",
	"onmouseout",
	"onmouseover",
	"onmouseup",
	"onreset",
	"onselect",
	"onsubmit",
	"onunload",
        "rows",
	"size",
        "tabindex",
        "class",
        "title",
        "style",
        "width",
        "dir",
        "rules",
        "frame",
        "border",
        "cellspacing",
        "cellpadding",
        "summary",
        "bgcolor",
        "usemap",
        "enctype", 
        "accept-charset", 
        "accept", 
        "target", 
        "onsubmit", 
        "onreset"
    };

private static long id = 0;


//
// Instance Variables
//

// Attribute Instance Variables

// Relationship Instance Variables

//
// Constructors and Initializers    
//

private Util()
{
    throw new IllegalStateException();
}

//
// Class methods
//
    public static Class loadClass(String name) throws ClassNotFoundException {
	ClassLoader loader =
	    Thread.currentThread().getContextClassLoader();
	if (loader == null) {
	    return Class.forName(name);
	}
	else {
	    return loader.loadClass(name);
	}
    }

    /**
     * Generate a new identifier currently used to uniquely identify
     * components.
     */
    public static synchronized String generateId() {
        if (id == Long.MAX_VALUE) {
            id = 0;
        } else { 
            id++;
        }
        return Long.toHexString(id);
    }

    /**
     * Return a Locale instance using the following algorithm: <P>

     	<UL>

	<LI>

	If this component instance has an attribute named "bundle",
	interpret it as a model reference to a LocalizationContext
	instance accessible via FacesContext.getModelValue().

	</LI>

	<LI>

	If FacesContext.getModelValue() returns a LocalizationContext
	instance, return its Locale.

	</LI>

	<LI>

	If FacesContext.getModelValue() doesn't return a
	LocalizationContext, return the FacesContext's Locale.

	</LI>

	</UL>
     */

    public static Locale 
	getLocaleFromContextOrComponent(FacesContext context,
					UIComponent component) {
	Locale result = null;
	String bundleName = null, bundleAttr = "bundle";
	
	ParameterCheck.nonNull(context);
	ParameterCheck.nonNull(component);
	
	// verify our component has the proper attributes for bundle.
	if (null != (bundleName = (String)component.getAttribute(bundleAttr))){
	    // verify there is a Locale for this modelReference
	    javax.servlet.jsp.jstl.fmt.LocalizationContext locCtx = null;
	    if (null != (locCtx = 
			 (javax.servlet.jsp.jstl.fmt.LocalizationContext) 
			 context.getModelValue(bundleName))) {
		result = locCtx.getLocale();
		Assert.assert_it(null != result);
	    }
	}
	if (null == result) {
	    result = context.getLocale();
	}

	return result;
    }


    /**

    * Render any boolean "passthru" attributes.  
    * <P>

    * @see passthruAttributes

    */

    public static String renderBooleanPassthruAttributes(FacesContext context,
						       UIComponent component) {
	int i = 0, len = booleanPassthruAttributes.length;
	String value;
	boolean thisIsTheFirstAppend = true;
	StringBuffer renderedText = new StringBuffer();

	for (i = 0; i < len; i++) {
	    if (null != (value = (String) 
		      component.getAttribute(booleanPassthruAttributes[i]))) {
		if (thisIsTheFirstAppend) {
		    // prepend ' '
		    renderedText.append(' ');
		    thisIsTheFirstAppend = false;
		}
		if (Boolean.valueOf(value).booleanValue()) {
		    renderedText.append(booleanPassthruAttributes[i] + ' ');
		}
	    }
	}
	
	return renderedText.toString();
    }

    /**

    * Render any "passthru" attributes, where we simply just output the
    * raw name and value of the attribute.  This method is aware of the
    * set of HTML4 attributes that fall into this bucket.  Examples are
    * all the javascript attributes, alt, rows, cols, etc.  <P>

    * @return the rendererd attributes as specified in the component.
    * Padded with leading and trailing ' '.  If there are no passthru
    * attributes in the component, return the empty String.

    * @see passthruAttributes

    */

    public static String renderPassthruAttributes(FacesContext context,
						  UIComponent component) {
	int i = 0, len = passthruAttributes.length;
	String value;
	boolean thisIsTheFirstAppend = true;
	StringBuffer renderedText = new StringBuffer();

	for (i = 0; i < len; i++) {
	    if (null != (value = (String) 
			 component.getAttribute(passthruAttributes[i]))) {
		if (thisIsTheFirstAppend) {
		    // prepend ' '
		    renderedText.append(' ');
		    thisIsTheFirstAppend = false;
		}
		renderedText.append(passthruAttributes[i] + "=\"" + value + 
				    "\" ");
	    }
	}
	
	return renderedText.toString();
    }

//
// General Methods
//

} // end of class Util
