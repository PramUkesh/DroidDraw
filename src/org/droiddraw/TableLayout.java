package org.droiddraw;

import java.util.StringTokenizer;
import java.util.Vector;

public class TableLayout extends LinearLayout {
	Vector<Integer> max_widths;
	Vector<Integer> stretchColumns;
	StringProperty stretch;
	
	public TableLayout() {
		this.tagName = "TableLayout";
		this.max_widths = new Vector<Integer>();
	
		this.stretch = new StringProperty("Stretchable Column", "android:stretchColumns", "");
		props.add(stretch);
		
		this.stretchColumns = new Vector<Integer>();
		apply();
	}

	protected void calculateMaxWidths() {
		max_widths.clear();
		for (Widget wt : widgets) {
			if (wt instanceof TableRow) {
				int ix = 0;
				TableRow row = (TableRow)wt;
				for (Widget w : row.getWidgets()) {
					w.apply();
					int wd = w.getWidth();
					if (ix >= max_widths.size()) {
						max_widths.add(wd);
					}
					else if (max_widths.get(ix) < wd) {
						max_widths.set(ix, wd);
					}
					ix++;
				}
			}
		}
		int total = 0;
		for (int i : max_widths) {
			total += i;
		}
		int extra = getWidth()-total;
		if (extra > 0 && stretchColumns.size() > 0) {
			int share = extra/stretchColumns.size();
			for (int col : stretchColumns) {
				if (col < max_widths.size())
					max_widths.set(col, max_widths.get(col)+share);
			}
		}
	}
	
	public void apply() {
		if (stretch != null) {
			String cols = stretch.getStringValue();
			stretchColumns.clear();
			if (cols != null) {
				StringTokenizer toks = new StringTokenizer(cols, ",");
				while (toks.hasMoreTokens()) {
					stretchColumns.add(new Integer(toks.nextToken()));
				}
			}
		}
		super.apply();
	}
	
	public void resizeForRendering() {
		calculateMaxWidths();
		for (Widget w : widgets) {
			if (w instanceof TableRow) {
				((TableRow)w).setWidths(max_widths);
			}
			else {
				w.setSizeInternal(getWidth(), w.getHeight());
			}
		}
	}
}
