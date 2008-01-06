package org.droiddraw.widget;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JPanel;

import org.droiddraw.AndroidEditor;
import org.droiddraw.gui.PropertiesPanel;
import org.droiddraw.property.ColorProperty;
import org.droiddraw.property.Property;
import org.droiddraw.property.SelectProperty;
import org.droiddraw.property.StringProperty;
import org.droiddraw.util.DisplayMetrics;

public class TextView extends AbstractWidget {
	int fontSize = 14;
	
	StringProperty text;
	StringProperty fontSz;
	SelectProperty face;
	SelectProperty style;
	SelectProperty align;
	ColorProperty textColor;
	
	int pad_x = 6;
	int pad_y = 4;
	
	PropertiesPanel p;
	Font f;
	BufferedImage bg;
	Vector<String> texts;
	
	boolean osx;
	
	public static final String[] propertyNames = 
		new String[] {"android:textSize", "android:textStyle", "android:typeface", "android:textColor"};
	
	public TextView(String str) {
		super("TextView");
		
		text = new StringProperty("Text", "android:text", "");
		if (str != null) {
			text.setStringValue(str);
		}
		fontSz = new StringProperty("Font Size", "android:textSize", fontSize+"sp");
		face = new SelectProperty("Font Face", "android:typeface", new String[] {"normal","sans","serif","monospace"}, 0);
		style = new SelectProperty("Font Style", "android:textStyle", new String[] {"normal", "bold", "italic", "bold_italic"}, 0);
		textColor = new ColorProperty("Text Color", "android:textColor", Color.black);
		align = new SelectProperty("Text Alignment", "android:textAlign", new String[] {"end","center","start"}, 2);
		props.add(text);
		props.add(fontSz);
		props.add(face);
		props.add(style);
		props.add(textColor);
		props.add(align);
		
		osx = (System.getProperty("os.name").toLowerCase().contains("mac os x"));
		buildFont();
		
		texts = new Vector<String>();
		bg = new BufferedImage(1,1,BufferedImage.TYPE_BYTE_GRAY);
		apply();
	}

	protected void buildFont() {
		if (osx)
			f = new Font("Arial", Font.PLAIN, fontSize);
		else
			f = new Font(face.getStringValue(),Font.PLAIN,fontSize);
		if (style.getStringValue() != null && style.getStringValue().contains("bold")) {
			f = f.deriveFont(f.getStyle() | Font.BOLD);
		}
		if (style.getStringValue() != null && style.getStringValue().contains("italic")) {
			f = f.deriveFont(f.getStyle() | Font.ITALIC);
		}
	}

	public JPanel getEditorPanel() {
		if (p == null) {
			p = new PropertiesPanel(props, this);
		}
		return p;
	}

	public void apply() {
		if (fontSz.getStringValue() != null && fontSz.getStringValue().length() > 0) {
			fontSize = (DisplayMetrics.readSize(fontSz));
		}
		buildFont();
		buildLineBreaks();
		super.apply();
		this.baseline = fontSize+pad_y/2;
	}
	
	protected void buildLineBreaks() {
		String txt = text.getStringValue();
		texts.clear();
		int width = AndroidEditor.instance().getScreenX()-getX()-getPadding(LEFT)+getPadding(RIGHT);
		if (width < 0) {
			texts.add(txt);
			return;
		}
		
		int l = stringLength(txt);
		while (l > width) {
			int bk = 1;
			while (stringLength(txt.substring(0,bk)) < width) bk++;
			bk--;
			String sub = txt.substring(0, bk);
			texts.add(sub);
			txt = txt.substring(bk);
			l = stringLength(txt);
		}
		texts.add(txt);
	}
	
	protected int stringLength(String str) {
		if (str == null)
			return 0;
		return bg.getGraphics().getFontMetrics(f).stringWidth(str);
	}
	
	protected int getContentWidth() {
		int l = stringLength(text.getStringValue())+pad_x;
		if (l > AndroidEditor.instance().getScreenX())
			l = AndroidEditor.instance().getScreenX()-getX();
		return l;
	}
	
	protected int getContentHeight() {
		int h = texts.size()*(fontSize+1)+pad_y;
		return h;
	}
	
	public void paint(Graphics g) {
		drawBackground(g);
		if (text.getStringValue() != null) {
			Color c = textColor.getColorValue();
			if (c == null)
				c = Color.black;
			g.setColor(c);
			g.setFont(f);
			
			int h = fontSize+pad_y/2;
			String txt = text.getStringValue();
			int l = stringLength(txt)+pad_x;
			int x = getX()+pad_x/2;
			if (align.getStringValue().equals("end")) {
				x = getX()+getWidth()-l+pad_x/2;
			}
			if (align.getStringValue().equals("center")) {
				x = getX()+getWidth()/2-l/2;
			}
			for (String s : texts) {
				g.drawString(s, x, getY()+h);
				h += fontSize+1;
				if (h > getHeight())
					break;
			}
		}
	}
	
	public Vector<Property> getProperties() {
		return props;
	}
}
