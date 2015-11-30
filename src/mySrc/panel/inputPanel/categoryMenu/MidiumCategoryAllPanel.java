package mySrc.panel.inputPanel.categoryMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class MidiumCategoryAllPanel extends JPanel implements ActionListener{
	
	private CategoryWindow _categoryWindow;
	private JTabbedPane _tabblePane;
	private ArrayList<JCheckBox> _category2CheckBoxArray = new ArrayList<>();
	private ArrayList<Boolean> _category2CheckMode = new ArrayList<>();	// それぞれの項目がチェックされているかどうか.
	
	public MidiumCategoryAllPanel(CategoryWindow aCategoryWindow, JTabbedPane aTabbedPane){
		setPreferredSize(new Dimension(CategoryWindow.WINDOW_WIDTH, CategoryWindow.WINDOW_HEIGHT));
		setBackground(Color.pink);
		
		_categoryWindow = aCategoryWindow;
		_tabblePane = aTabbedPane;
		
		_category2CheckBoxArray.add(new JCheckBox("すべてのカテゴリ"));
		add(_category2CheckBoxArray.get(_category2CheckBoxArray.size() - 1));	// コンポーネントの追加.
		_category2CheckBoxArray.get(_category2CheckBoxArray.size() - 1).addActionListener(this);	// リスナ登録.
		_category2CheckMode.add(false);	// チェック状態の初期化.
		
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == _category2CheckBoxArray.get(0)){	// すべてが選択された.
			if(_category2CheckBoxArray.get(0).isSelected()){
				_category2CheckMode.set(0, true);
				_categoryWindow.setTabTitle(0, "All");
				// すべて以外のタブの無効.
				_tabblePane.setEnabledAt(1, false);
				_tabblePane.setEnabledAt(2, false);
				_tabblePane.setEnabledAt(3, false);
				_tabblePane.setEnabledAt(4, false);
				
			}else{
				_category2CheckMode.set(0, false);
				_categoryWindow.setTabTitle(0, "");
				// すべて以外のタブの有効.
				_tabblePane.setEnabledAt(1, true);
				_tabblePane.setEnabledAt(2, true);
				_tabblePane.setEnabledAt(3, true);
				_tabblePane.setEnabledAt(4, true);

			}
		}
	}
	
	public ArrayList<Boolean> getCategory2CheckMode(){
		return _category2CheckMode;
	}
}
