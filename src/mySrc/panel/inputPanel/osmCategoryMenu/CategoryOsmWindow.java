package mySrc.panel.inputPanel.osmCategoryMenu;

import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;

/**
 * OSMのカテゴリウインドウ
 * @author murase
 *
 */
public class CategoryOsmWindow  extends JFrame{
	
	public static final int WINDOW_WIDTH = 400;
	public static final int WINDOW_HEIGHT = 400;
	
	/** コンテントペイン */
	private Container _contentPane = getContentPane();
	
	MidiumCategoryOsm _midiumCategoryPanel;
	
	public CategoryOsmWindow() {
		
		_midiumCategoryPanel = new MidiumCategoryOsm(this);
		add(_midiumCategoryPanel);
		
		_contentPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		pack();
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setVisible(false);
		
	}
	
	
	/**
	 * カテゴリウインドウの表示
	 */
	public void openCategoryWindow(){
		setVisible(true);
	}
	
	/**
	 * 選択されたカテゴリを取得
	 * カラム名:値
	 */
	public ArrayList<String> getCategory(){
		ArrayList<String> categorys = new ArrayList<>();
		for(JCheckBox c : _midiumCategoryPanel._category2CheckBoxArray){
			if(c.isSelected() == true){
				categorys.add(c.getText());
			}
		}
		return categorys;
	}
	
}
