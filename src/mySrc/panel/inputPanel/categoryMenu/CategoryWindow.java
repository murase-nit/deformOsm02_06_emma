package mySrc.panel.inputPanel.categoryMenu;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;


public class CategoryWindow extends JFrame{
	
	public static final int WINDOW_WIDTH = 550;
	public static final int WINDOW_HEIGHT = 500;
	
	/** コンテントペイン */
	private Container _contentPane = getContentPane();
	private JTabbedPane _tabbedPane = new JTabbedPane();	// タブペイン.
	
	private SelectedDataPanel _selectedDataPanel;
	
	private MidiumCategoryAllPanel _midiumCategoryAllPanel = new MidiumCategoryAllPanel(this,_tabbedPane);
	private MidiumCategory1Panel _midiumCategory1Panel = new MidiumCategory1Panel(this);
	private MidiumCategory2Panel _midiumCategory2Panel = new MidiumCategory2Panel(this);
	private MidiumCategory3Panel _midiumCategory3Panel = new MidiumCategory3Panel(this);
	private MidiumCategory4Panel _midiumCategory4Panel = new MidiumCategory4Panel(this);
	
	public CategoryWindow(){
		
		_tabbedPane.add(CategoryList.category1[0], _midiumCategoryAllPanel);
		_tabbedPane.add(CategoryList.category1[1], _midiumCategory1Panel);
		_tabbedPane.add(CategoryList.category1[2], _midiumCategory2Panel);
		_tabbedPane.add(CategoryList.category1[3], _midiumCategory3Panel);
		_tabbedPane.add(CategoryList.category1[4], _midiumCategory4Panel);
		
		_selectedDataPanel = new SelectedDataPanel(_tabbedPane);
		
		
		setLayout(new BorderLayout());
		
		_contentPane.add(_tabbedPane, BorderLayout.CENTER);
		_contentPane.add(_selectedDataPanel, BorderLayout.SOUTH);
		
		_contentPane.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		pack();
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setVisible(false);
		
		
		// 初期状態で"ショッピング"をすべて選択状態にする.
		_midiumCategory2Panel.initSelected();

	}
	
	/**
	 * 取得するデータの種類を取得(yahoo, 商店街, 研究室建物データ　の順番)
	 */
	public boolean[] isSelectedData(){
		boolean[] selectedData = {
				_selectedDataPanel.isYahooDataCheckBox(),
				_selectedDataPanel.isShopStreetCheckBox(),
				_selectedDataPanel.isLocalDataCheckBox()
				};
		
		return selectedData;
	}
	
	/**
	 * カテゴリウインドウの表示
	 */
	public void openCategoryWindow(){
		setVisible(true);
	}
	
	/**
	 * グループコードの取得
	 * @return
	 */
	public ArrayList<String> getGroupCode(){
		ArrayList<String> groupCodeArrayList = new ArrayList<>();
		
		// "すべて"のタブの"すべて"の項目にチェックがしてある.
		if(_midiumCategoryAllPanel.getCategory2CheckMode().get(0).booleanValue() == true){
			String[] strings = {"01","02","03","04"};	// 大カテゴリのすべてのグループコード.
			groupCodeArrayList.add(strings[0]);
			groupCodeArrayList.add(strings[1]);
			groupCodeArrayList.add(strings[2]);
			groupCodeArrayList.add(strings[3]);
			
			//System.out.println(groupCodeArrayList);
			return groupCodeArrayList;
		}
		
		DecimalFormat dFormat = new DecimalFormat("00");
		int firstGroupCode = 1; // 業種コード2の上2桁..
		int secondGroupCode = 1;	// 業種コード2の下2桁.
		for(int i=1; i<_midiumCategory1Panel.getCategory2CheckMode().size(); i++){
			if(_midiumCategory1Panel.getCategory2CheckMode().get(0).booleanValue() == true){
				groupCodeArrayList.add(dFormat.format(firstGroupCode));
				break;
			}
			if(_midiumCategory1Panel.getCategory2CheckMode().get(i).booleanValue() == true){
				groupCodeArrayList.add(dFormat.format(firstGroupCode) + dFormat.format(secondGroupCode));
			}
			secondGroupCode++;
		}
		firstGroupCode = 2; // 業種コード2の上2桁..
		secondGroupCode = 1;	// 業種コード2の下2桁.
		for(int i=1; i<_midiumCategory2Panel.getCategory2CheckMode().size(); i++){
			if(_midiumCategory2Panel.getCategory2CheckMode().get(0).booleanValue() == true){
				groupCodeArrayList.add(dFormat.format(firstGroupCode));
				break;
			}
			if(_midiumCategory2Panel.getCategory2CheckMode().get(i).booleanValue() == true){
				groupCodeArrayList.add(dFormat.format(firstGroupCode) + dFormat.format(secondGroupCode));
			}
			secondGroupCode++;
					
		}
		firstGroupCode = 3; // 業種コード2の上2桁..
		secondGroupCode = 1;	// 業種コード2の下2桁.
		for(int i=1; i<_midiumCategory3Panel.getCategory2CheckMode().size(); i++){
			if(_midiumCategory3Panel.getCategory2CheckMode().get(0).booleanValue() == true){
				groupCodeArrayList.add(dFormat.format(firstGroupCode));
				break;
			}
			if(_midiumCategory3Panel.getCategory2CheckMode().get(i).booleanValue() == true){
				groupCodeArrayList.add(dFormat.format(firstGroupCode) + dFormat.format(secondGroupCode));
			}
			secondGroupCode++;
			
		}
		firstGroupCode = 4; // 業種コード2の上2桁..
		secondGroupCode = 1;	// 業種コード2の下2桁.
		for(int i=1; i<_midiumCategory4Panel.getCategory2CheckMode().size(); i++){
			if(_midiumCategory4Panel.getCategory2CheckMode().get(0).booleanValue() == true){
				groupCodeArrayList.add(dFormat.format(firstGroupCode));
				break;
			}
			if(_midiumCategory4Panel.getCategory2CheckMode().get(i).booleanValue() == true){
				groupCodeArrayList.add(dFormat.format(firstGroupCode) + dFormat.format(secondGroupCode));
			}
			secondGroupCode++;
			
		}
		
		//System.out.println(groupCodeArrayList);
		return groupCodeArrayList;
	}
	
	/**
	 * タイトルの設定
	 * @param aCategoryIndex
	 * @param aCategoryNum
	 */
	public void setTabTitle(int aCategoryIndex, String aCategoryNum){
		_tabbedPane.setTitleAt(aCategoryIndex, CategoryList.category1[aCategoryIndex]+"("+aCategoryNum+")");
	}
	
//	public JTabbedPane getTabbedPane(){
//		return _tabbedPane;
//	}
	
	
}
