package org.droiddraw;
import java.awt.BorderLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;


public class AndroidEditor {
	public static enum ScreenMode {QVGA_LANDSCAPE, QVGA_PORTRAIT, HVGA_LANDSCAPE, HVGA_PORTRAIT};

	Layout layout;
	Widget selected;
	Viewer viewer;
	ScreenMode screen;
	int sx, sy;
	PropertiesPanel pp;
	JFrame jf;
	
	public static int OFFSET_X = 0;
	public static int OFFSET_Y = 48;
	
	private static AndroidEditor inst;
	
	private AndroidEditor() {
		this(ScreenMode.QVGA_LANDSCAPE);
	}
	
	private AndroidEditor(ScreenMode mode) {
		layout = new AbsoluteLayout();
		setScreenMode(mode);
		this.pp = new PropertiesPanel();
	}

	public PropertiesPanel getPropertiesPanel() {
		return pp;
	}
	
	public static AndroidEditor instance() {
		if (inst == null)
			inst = new AndroidEditor();
		return inst;
	}
	
	public ScreenMode getScreenMode() {
		return screen;
	}
	
	public void setScreenMode(ScreenMode mode) {
		this.screen = mode;
		if (screen == ScreenMode.QVGA_LANDSCAPE) {
			sx = 320;
			sy = 240;
		}
		else if (screen == ScreenMode.QVGA_PORTRAIT) {
			sx = 240;
			sy = 320;
		}
		else if (screen == ScreenMode.HVGA_LANDSCAPE) {
			sx = 480;
			sy = 320;
		}
		else if (screen == ScreenMode.HVGA_PORTRAIT) {
			sx = 320;
			sy = 480;
		}
		for (Widget w : this.getLayout().getWidgets()) {
			w.apply();
		}
	}
	
	public int getScreenX() {
		return sx;
	}
	
	public int getScreenY() {
		return sy;
	}
	
	public void setViewer(Viewer v) {
		this.viewer = v;
		this.pp.setViewer(v);
	}
	
	public void setLayout(Layout l) {
		Vector<Widget> widgets = layout.getWidgets();
		this.layout = l;
		for (Widget w : widgets) {
			l.addWidget(w);
		}
	}
	
	public Layout getLayout() {
		return layout;
	}
	
	public void addWidget(Widget w) {
		layout.addWidget(w);
		layout.addEditableProperties(w, w.getProperties());
	}

	public Widget getSelected() {
		return selected;
	}

	public void select(Widget w) {
		selected = w;
		if (w != null) {
			pp.setProperties(w.getProperties(), w);
			pp.validate();
			pp.repaint();
			if (jf != null) {jf.validate(); jf.pack();}
		}
		else {
			pp.setProperties(new Vector<Property>(), null);
			pp.validate();
			pp.repaint();
			if (jf != null) { jf.validate(); jf.pack();}
		}
	}

	public void removeWidget(Widget w) {
		layout.removeWidget(w);
		if (selected == w) {
			selected = null;
		}
	}
	
	public void removeAllWidgets() {
		layout.removeAllWidgets();
		selected = null;
	}
	
	public void editSelected() {
		if (jf != null) {
			//jf.setVisible(false);
			jf.invalidate();
			jf.pack();
			jf.setVisible(true);
		}
		else {
			jf = new JFrame("Edit");
			jf.getContentPane().add(pp);
			jf.pack();
			jf.setVisible(true);
		}
	}

	public Widget findWidget(int x, int y) {
		Widget res = null;
		for (Widget w : layout.getWidgets()) {
			if (w.clickedOn(x, y)) {
				res = w;
				break;
			}
		}
		return res;
	}
	
	public void selectWidget(int x, int y) {
		Widget res = findWidget(x, y);
		if (res == selected) {
			selected = null;
		}
		else {
			this.select(res);
		}
	}

	@SuppressWarnings("unchecked")
	public void generate(PrintWriter pw) {
		pw.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		layout.printStartTag(pw);
		for (Widget w : layout.getWidgets()) {
			pw.println("<"+w.getTagName());
			Vector<Property> props = (Vector<Property>)w.getProperties().clone();
			layout.addOutputProperties(w, props);
			for (Property prop : props) {
				if (prop.getValue() != null) {
					pw.println(prop.getAtttributeName()+"=\""+prop.getValue()+"\"");
				}
			}
			pw.println(">");
			pw.println("</"+w.getTagName()+">");
		}
		layout.printEndTag(pw);
		pw.flush();
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame("Android Editor");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final AndroidEditor ae = new AndroidEditor();
		
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("src/emu1.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		final Viewer viewer = new Viewer(ae, img);
		ae.setViewer(viewer);
		
		MenuBar mb = new MenuBar();
		Menu menu = new Menu("File");
		MenuItem it;
		it = new MenuItem("Save");
		it.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) { 
				JFileChooser jfc = new JFileChooser();
				FileFilter ff = new FileFilter() {
					public boolean accept(File arg) {
						return arg.getName().endsWith(".xml") || arg.isDirectory();
					}

					@Override
					public String getDescription() {
						return "Android Layout file (.xml)";
					} 
				};
				jfc.setFileFilter(ff);
				int res = jfc.showSaveDialog(viewer);
				if (res == JFileChooser.APPROVE_OPTION) {
					try {
						ae.generate(new PrintWriter(new FileWriter(jfc.getSelectedFile())));
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		it.setShortcut(new MenuShortcut(KeyEvent.VK_S, false));
		menu.add(it);
		menu.addSeparator();
		it = new MenuItem("Quit");
		it.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		it.setShortcut(new MenuShortcut(KeyEvent.VK_Q, false));
		menu.add(it);

		mb.add(menu);
		jf.setMenuBar(mb);

		JButton butt;
		JToolBar tb = new JToolBar();

		butt = new JButton("Edit");
		butt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ae.editSelected();
			}
		});
		tb.addSeparator();
		tb.add(butt);

		tb.addSeparator();
		
		ButtonGroup bg = viewer.getListener().getInterfaceStateGroup();
		Enumeration<AbstractButton> buttons = bg.getElements();
		while (buttons.hasMoreElements()) {
			tb.add(buttons.nextElement());
			tb.addSeparator();
		}

		tb.add(viewer.getListener().getWidgetSelector());

		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(viewer, BorderLayout.CENTER);
		jf.getContentPane().add(tb, BorderLayout.SOUTH);
		jf.pack();
		jf.setVisible(true);
	}
}
