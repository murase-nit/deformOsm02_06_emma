package mySrc.panel.outputPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import mySrc.panel.mapPanel.MapPanel;

/**
 * 右の画面(出力用)
 * @author murase
 *
 */
public class OutputPanel  extends JPanel{
	// パネルの縦横大きさ.
		private static final int OUTPUT_PANEL_WINDOW_WIDTH = MapPanel.RIGHT_AREA_WIDTH;
		private static final int OUTPUT_PANEL_WINDOW_HEIGHT = MapPanel.WINDOW_HEIGHT;
	public OutputPanel() {
		this.setPreferredSize(new Dimension(OUTPUT_PANEL_WINDOW_WIDTH, OUTPUT_PANEL_WINDOW_HEIGHT));
	}
	
	
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 =(Graphics2D)g;
		
		g.drawString(" " , 10, 10);

	}
}
