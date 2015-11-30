package mySrc.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JApplet;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;

import mySrc.panel.inputPanel.InputPanel;
import mySrc.panel.mapPanel.MapPanel;
import mySrc.panel.outputPanel.OutputPanel;


/**
 * メインのクラス
 * @author murase
 *
 */
public class MyMap  extends JApplet{
	
	/** ルートペイン */
	private JRootPane rootPane;
	/** レイヤードペイン */
	private JLayeredPane layeredPane;
	/** コンテントペイン */
	private Container contentPane;
	/** グラスペイン */
	private Component glassPane;
	/** 上側のパネル */
	private JPanel upperPanel;
	/** 下側のパネル */
	private JPanel downPanel;
	/** 右下のパネル */
	private JPanel downRightPanel;
	/** 左下のパネル */
	private JPanel downLeftPanel;

	
	/**
	 * @param args
	 */
	public MyMap(){
		contentPane = getContentPane();
		glassPane = getGlassPane();
		layeredPane = getLayeredPane();
		rootPane = getRootPane();
		
		upperPanel = new JPanel();		
		downPanel = new JPanel();
		downRightPanel = new JPanel();
		downLeftPanel = new JPanel();
		
		OutputPanel outputPanel = new OutputPanel();
		MapPanel mapPanel = new MapPanel(outputPanel,this);
		InputPanel inputPanel = new InputPanel(mapPanel, outputPanel);
		mapPanel.setInputPanel(inputPanel);
		
		upperPanel.add(inputPanel);
		downRightPanel.add(outputPanel);
		downLeftPanel.add(mapPanel);
		
		downPanel.setLayout(new BorderLayout());
		downPanel.add(downLeftPanel, BorderLayout.CENTER);
		downPanel.add(downRightPanel, BorderLayout.EAST);
		
		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BorderLayout());
		scrollPanel.add(upperPanel, BorderLayout.NORTH);
		scrollPanel.add(downPanel, BorderLayout.CENTER);
		JScrollPane jScrollPane = new JScrollPane(scrollPanel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		contentPane.setLayout(new BorderLayout());
		contentPane.add(jScrollPane, BorderLayout.CENTER);
		
//		contentPane.setLayout(new BorderLayout());
//		contentPane.add(upperPanel, BorderLayout.NORTH);
//		contentPane.add(downPanel, BorderLayout.CENTER);

	}

}
