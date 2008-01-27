package org.droiddraw.widget;

import java.awt.Graphics;
import java.awt.Image;

import org.droiddraw.gui.ImageResources;
import org.droiddraw.gui.NineWayImage;

public class ImageButton extends ImageView {
	NineWayImage img;
	
	public ImageButton() {
		Image i = ImageResources.instance().getImage("button_background_normal.9");
		if (i != null) {
			this.img = new NineWayImage(i, 10, 10);
		}
		this.tagName = "ImageButton";
		apply();
	}

	@Override
	protected int getContentHeight() {
		return 50;
	}

	@Override
	protected int getContentWidth() {
		return 50;
	}

	@Override
	public void paint(Graphics g) {
		if (img != null) {
			img.paint(g, getX(), getY(), getWidth(), getHeight());
		}
		if (super.img != null) {
			g.drawImage(super.img, getX()+10, getY()+10, getWidth()-20, getHeight()-20, null);
		}
		else if (paint != null) {
			g.drawImage(paint, getX()+10, getY()+10, getWidth()-20, getHeight()-20, null);
		}
	}
	
	
}
