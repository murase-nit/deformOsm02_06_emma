package mySrc.panel.inputPanel.osmCategoryMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import mySrc.panel.inputPanel.categoryMenu.CategoryWindow;

/**
 * カテゴリの選択パネル
 * @author murase
 *
 */
public class MidiumCategoryOsm extends JPanel{
	
	public CategoryOsmWindow _categoryOsmWindow;
	public ArrayList<JCheckBox> _category2CheckBoxArray = new ArrayList<>();
	//public ArrayList<JRadioButton> _category2CheckBoxArray = new ArrayList<>();

	
	public MidiumCategoryOsm(CategoryOsmWindow aCategoryOsmWindow) {
		setPreferredSize(new Dimension(CategoryWindow.WINDOW_WIDTH, CategoryWindow.WINDOW_HEIGHT));
		setBackground(Color.pink);
		
		_categoryOsmWindow = aCategoryOsmWindow;
		
		setLayout(new FlowLayout());
		JButton resetButton = new JButton("reset");
		add(resetButton);
		resetButton.addActionListener(new ActionListener() {
			@Override
			// チェックをすべて外す.
			public void actionPerformed(ActionEvent e) {
				for(JCheckBox iBox :_category2CheckBoxArray){
					iBox.setSelected(false);
				}
			}
		});
		ButtonGroup group = new ButtonGroup();
		for(ArrayList<String> categoryOne : CategoryListOsm.categoryAll){
			_category2CheckBoxArray.add(new JCheckBox(""+categoryOne.get(0)+":"+categoryOne.get(1)));
//			_category2CheckBoxArray.add(new JRadioButton(""+categoryOne.get(0)+":"+categoryOne.get(1)));
//			group.add(_category2CheckBoxArray.get(_category2CheckBoxArray.size() - 1));
			add(_category2CheckBoxArray.get(_category2CheckBoxArray.size() - 1));	// コンポーネントの追加.
		}
		// 初期のチェック状態.
		for(int i=0; i<CategoryListOsm.categoryAll.size(); i++){
			if(CategoryListOsm.categoryAll.get(i).get(1) == "parking"){
				_category2CheckBoxArray.get(i).setSelected(true);
			}
		}
	}

}
