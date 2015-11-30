package mySrc.panel.inputPanel.categoryMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * 選択されているデータの種類を表すパネル
 * @author murase
 *
 */
public class SelectedDataPanel extends JPanel implements ActionListener{
	private JTabbedPane _tabbedPane;
	
	private JCheckBox _yahooDataCheckBox = new JCheckBox("yahooData");	// yahooの建物データ.
	private JCheckBox _shopStreetCheckBox = new JCheckBox("ShopStreet");	// 研究室内の商店街データ.
	private JCheckBox _localDataCheckBox = new JCheckBox("localShopData");	// 研究室内の建物データ.

	
	public SelectedDataPanel(JTabbedPane aTabbledPane){
		_tabbedPane = aTabbledPane;

		_yahooDataCheckBox.setSelected(true);
		
		add(_yahooDataCheckBox);
		add(_shopStreetCheckBox);
		add(_localDataCheckBox);
		
		
		_yahooDataCheckBox.addActionListener(this);
		
		
	}
	
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == _yahooDataCheckBox){
			if(_yahooDataCheckBox.isSelected() == true){
				_tabbedPane.setEnabled(true);
			}else{
				_tabbedPane.setEnabled(false);
			}
		}
	}
	
	
	public boolean isYahooDataCheckBox(){
		return _yahooDataCheckBox.isSelected() ? true : false;
	}
	public boolean isShopStreetCheckBox(){
		return _shopStreetCheckBox.isSelected() ? true : false;
	}
	public boolean isLocalDataCheckBox(){
		return _localDataCheckBox.isSelected() ? true : false;
	}
	
}
