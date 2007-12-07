package org.droiddraw;

import java.io.PrintWriter;
import java.util.Vector;

public interface Layout extends Widget {
	public void addWidget(Widget w);
	public Vector<Widget> getWidgets();
	public void removeWidget(Widget w);
	public void positionWidget(Widget w);
	public void repositionAllWidgets();
	public void printStartTag(PrintWriter pw);
	public void printEndTag(PrintWriter pw);
	public void addOutputProperties(Widget w, Vector<Property> properties);
	public void removeAllWidgets();
}
