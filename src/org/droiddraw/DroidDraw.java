package org.droiddraw;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.UIManager;

import org.droiddraw.gui.DroidDrawPanel;
import org.droiddraw.gui.ImageResources;
import org.droiddraw.widget.Layout;

public class DroidDraw extends JApplet implements URLOpener {
	private static final long serialVersionUID = 1L;
	
	protected static final void switchToLookAndFeel(String clazz) {
		try {
			UIManager.setLookAndFeel(clazz);
		} catch (Exception ex) {
			AndroidEditor.instance().error(ex);
		}
	}
	
	public void openURL(String url) {
		try {
			getAppletContext().showDocument(new URL(url), "_blank");
		} 
		catch (MalformedURLException ex) {
			AndroidEditor.instance().error(ex);
		}
	}
	
	protected static final void setupRootLayout(Layout l) {
		l.setPosition(AndroidEditor.OFFSET_X,AndroidEditor.OFFSET_Y);
		l.setPropertyByAttName("android:layout_width", "fill_parent");
		l.setPropertyByAttName("android:layout_height", "fill_parent");
		l.apply();
	}
	
	protected void loadImage(String name, MediaTracker md, int id) {
		Image img = getImage(getCodeBase(), "ui/"+name+".png");
		md.addImage(img, id);
		ImageResources.instance().addImage(img, name);
		
	}
	
	@Override
	public void init() {
		super.init();
		AndroidEditor.instance().setURLOpener(this);
		// This is so that I can test out the Google examples...
		// START
		/*
		if ("true".equals(this.getParameter("load_strings"))) {
			try {
				URL url = new URL(getCodeBase(), "strings.xml");
				AndroidEditor.instance().setStrings(StringHandler.load(url.openStream()));
			} catch (Exception ex) {
				AndroidEditor.instance().error(ex);
			}
		}*/
		// END
		
		String screen = this.getParameter("Screen");
		if (screen == null) {
			screen="hvgap";
		}
		MediaTracker md = new MediaTracker(this);
		int ix = 0;
		loadImage("emu1", md, ix++);
		loadImage("emu2", md, ix++);
		loadImage("emu3", md, ix++);
		loadImage("emu4", md, ix++);
		loadImage("checkbox_off_background", md, ix++);
		loadImage("checkbox_on_background", md, ix++);
		loadImage("clock_dial", md, ix++);
		loadImage("clock_hand_hour", md, ix++);
		loadImage("clock_hand_minute", md, ix++);
		loadImage("radiobutton_off_background", md, ix++);
		loadImage("radiobutton_on_background", md, ix++);
		loadImage("button_background_normal.9", md, ix++);
		loadImage("editbox_background_normal.9", md, ix++);
		loadImage("progress_circular_background", md, ix++);
		loadImage("progress_particle", md, ix++);
		loadImage("progress_circular_indeterminate", md, ix++);
		loadImage("arrow_up_float", md, ix++);
		loadImage("arrow_down_float", md, ix++);
		loadImage("spinnerbox_background_focus_yellow.9", md, ix++);
		loadImage("spinnerbox_arrow_middle.9", md, ix++);
		loadImage("paint", md, ix++);
		loadImage("paypal", md, ix++);
		
		for (int i=0;i<ix;i++) {
			try {
				md.waitForID(i);
			} catch (InterruptedException ex) {}
		}
		setLayout(new BorderLayout());
		setSize(1100, 600);
		add(new DroidDrawPanel(screen, true), BorderLayout.CENTER);
	}
}
