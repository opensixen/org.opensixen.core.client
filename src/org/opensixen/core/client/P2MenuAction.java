package org.opensixen.core.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.compiere.apps.AEnv;
import org.compiere.util.Env;
import org.opensixen.osgi.AbstractMenuAction;
import org.opensixen.osgi.interfaces.IMenuAction;
import org.opensixen.swing.InstallForm;

public class P2MenuAction extends AbstractMenuAction implements
IMenuAction, ActionListener {

	public P2MenuAction() {
		
	}

	@Override
	public void addAction(JMenuBar menuBar) {
		JMenu menu = getMenu(menuBar, "Tools");

		if (menu == null) {
			return;
		}

		AEnv.addMenuItem("Plugins", "Plugins", null, menu, this);

	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		InstallForm form = new InstallForm(Env.getCtx());
		form.setVisible(true);		
	}

}
