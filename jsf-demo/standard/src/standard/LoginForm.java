/*
 * $Id: LoginForm.java,v 1.1 2003/10/19 05:18:03 craigmcc Exp $
 */

/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package standard;


import javax.faces.application.Action;
import javax.faces.application.Message;
import javax.faces.application.MessageImpl;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;


/**
 * <p>Backing Bean for a username and password login form.</p>
 */

public class LoginForm {


    // -------------------------------------------------------------- Components


    private UIInput password = null;
    public UIInput getPassword() {
        return (this.password);
    }
    public void setPassword(UIInput password) {
        this.password = password;
    }


    private UIInput username = null;
    public UIInput getUsername() {
        return (this.username);
    }
    public void setUsername(UIInput username) {
        this.username = username;
    }


    // ---------------------------------------------------------------- Handlers


    // Validate the attempted login
    private String login() {

        String username = (String) this.username.getValue();
        String password = (String) this.password.getValue();
        if ("user".equals(username) && "pass".equals(password)) {
            return ("success");
        } else {
            FacesContext.getCurrentInstance().addMessage
                (this.username,
                 new MessageImpl(Message.SEVERITY_ERROR,
                                 "Invalid username or password, please retry",
                                 null));
            return (null);
        }

    }


    // ----------------------------------------------------------------- Actions


    public Action getLogin() {
	return new Action() {
		public String invoke() {
		    return (login());
		}
	    };
    }


}
