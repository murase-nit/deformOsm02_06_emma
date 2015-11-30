package mySrc.panel.inputPanel.categoryMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;


/**
 * カテゴリ選択ウインドウにおける"暮らし生活"のパネル.
 * @author murase
 *
 */
public class MidiumCategory4Panel extends JPanel implements ActionListener{
	
	CategoryWindow _categoryWindow;
	ArrayList<JCheckBox> _category2CheckBoxArray = new ArrayList<>();
	ArrayList<Boolean> _category2CheckMode = new ArrayList<>();	// それぞれの項目がチェックされているかどうか.
	
	public MidiumCategory4Panel(CategoryWindow aCategoryWindow){
		setPreferredSize(new Dimension(CategoryWindow.WINDOW_WIDTH, CategoryWindow.WINDOW_HEIGHT));
		setBackground(Color.pink);
		
		_categoryWindow = aCategoryWindow;
		
		setLayout(new FlowLayout());
		for(int i=0; i<CategoryList.category1d2.length; i++){
			_category2CheckBoxArray.add(new JCheckBox(CategoryList.category1d2[i]));
			add(_category2CheckBoxArray.get(_category2CheckBoxArray.size() - 1));	// コンポーネントの追加.
			_category2CheckBoxArray.get(_category2CheckBoxArray.size() - 1).addActionListener(this);	// リスナ登録.
			_category2CheckMode.add(false);	// チェック状態の初期化.
		}
		
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == _category2CheckBoxArray.get(0)){	// "すべて"の項目がチェックされた.
			boolean bool =_category2CheckBoxArray.get(0).isSelected();
			for(int i=1; i<_category2CheckBoxArray.size(); i++){
				_category2CheckBoxArray.get(i).setSelected(bool);
				_category2CheckMode.set(i, bool);
			}
			_category2CheckMode.set(0, bool);
			String checkedNum=bool?"All":""+0;// チェックされた項目数(0かAll).
			_categoryWindow.setTabTitle(4, checkedNum);
			return;
		}
		
		// "すべて"以外のそれぞれの項目がチェックされているか確かめる.
		for(int i=1; i<_category2CheckBoxArray.size(); i++){
			if(e.getSource() != _category2CheckBoxArray.get(i)){
				continue;
			}
			if(_category2CheckBoxArray.get(i).isSelected()){
				_category2CheckMode.set(i, true);
			}else{
				_category2CheckMode.set(i, false);
				
				// すべての項目をfalseにする.
				_category2CheckMode.set(0,false);
				_category2CheckBoxArray.get(0).setSelected(false);
			}
			
			int checkedNum=0;	// すべて以外のチェックされた項目数.
			for(int j=1; j<_category2CheckMode.size(); j++){
				if(_category2CheckMode.get(j).booleanValue() == true){
					checkedNum++;
				}
			}
			_categoryWindow.setTabTitle(4, ""+checkedNum);
			
			break;
		}
	}
	
	public ArrayList<Boolean> getCategory2CheckMode(){
		return _category2CheckMode;
	}
	
}
