package mySrc.panel.inputPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mySrc.panel.inputPanel.categoryMenu.CategoryWindow;
import mySrc.panel.inputPanel.osmCategoryMenu.CategoryOsmWindow;
import mySrc.panel.mapPanel.MapPanel;
import mySrc.panel.outputPanel.OutputPanel;

/**
 * 上の画面(入力用)
 * @author murase
 *
 */
public class InputPanel  extends JPanel implements ActionListener ,ItemListener, ChangeListener{
	// パネルの縦横大きさ.
	private static final int INPUT_PANEL_WINDOW_WIDTH = MapPanel.WINDOW_WIDTH + MapPanel.RIGHT_AREA_WIDTH;
	private static final int INPUT_PANEL_WINDOW_HEIGHT = MapPanel.UPPER_AREA_HEIGHT;
	private MapPanel mapPanel;
	private OutputPanel outputPanel;
	public CategoryWindow _categoryWindow = new CategoryWindow();
	public CategoryOsmWindow _CategoryOsmWindow = new CategoryOsmWindow();
	
	// 入力インターフェース ---------------------.
	// 1番目のパネル.
	private JRadioButton selectedLnglatRadioButton;
	private JLabel lngLabel;
	private JTextField lngTextField;
	private JLabel latLabel;
	private JTextField latTextField;
	private JRadioButton selectedAddressRadioButton;
	private JRadioButton selectedLandmarkRadioButton;
	private JTextField locationTextField;
	private JLabel windowWidthLabel;
	private JTextField windowWidthTextField;	// 地図の横幅.
	private JLabel windowHeightLabel;
	private JTextField windowHeightTextField;	// 地図の縦幅.
	private JLabel scaleLabel;
	private static final String[] scaleList = {"5","6","7","8","9","10","11","12","13","14","15","16","17","18","19"};//{"10000", "25000", "40000", "55000", "70000"};
	private JComboBox<String> scaleBox;
	private JButton moveButton;
	// 2番目のパネル.
	private JButton categoryButton;	// yahooの施設データ検索.
	private JButton categoryOsmButton;	// OSMの施設データ検索
	private JButton displayButton;		// yahoo用施設データ表示.
	private JButton displayOsmButton;	// osm要施設データ表示.
	private JButton changeRoadDataModeButton;
	private JButton changeRoadDataModeButton2;
	private JButton putFocusButton;	// emma関係　focusを表示するボタン.
	private JButton displayFGCRoad; // fgc道路の描画.
	// 3番目のパネル.
	private JButton deformedButton1;	// デフォルメマップ2ボタン
	private JButton deformedButton2;	// デフォルメマップ2ボタン
	private JButton deformedButton3;	// デフォルメマップ2ボタン
	private JButton deformedButton4;	// デフォルメマップ4ボタン
	private JButton checkConnectivityButton;
	private JButton checkConnectivityButton2;
	private JSlider	  thresholdSlider;// 閾値関係.
	private JLabel	  thresholdValue;// 閾値関係.

	// 4番目のパネル.
		
	// 汎用インターフェース.
	public JCheckBox publicCheckBox;
	//入力インターフェース-----ここまで-----------------------------.
	private ButtonGroup selectedButtonGroup;
	private ButtonGroup selectedSearchTypeButtonGroup;

		
	public InputPanel(MapPanel aMapPanel, OutputPanel aOutputPanel) {
		this.setPreferredSize(new Dimension(INPUT_PANEL_WINDOW_WIDTH, INPUT_PANEL_WINDOW_HEIGHT));
		this.mapPanel = aMapPanel;
		this.outputPanel = aOutputPanel;
		// コンポーネントの初期化.
		// 1番目のパネル.
		selectedLnglatRadioButton = new JRadioButton("coodinate");
		lngLabel = new JLabel("lng");
		lngTextField = new JTextField(5);
		lngTextField.setEditable(false);
		latLabel = new JLabel("lat");
		latTextField = new JTextField(5);
		latTextField.setEditable(false);
		selectedAddressRadioButton = new JRadioButton("place name",true);
		selectedLandmarkRadioButton = new JRadioButton("landmark");
		locationTextField = new JTextField(7);
		windowWidthLabel = new JLabel("幅");
		windowWidthTextField = new JTextField(""+MapPanel.WINDOW_WIDTH,5);
		windowHeightLabel = new JLabel("高さ");
		windowHeightTextField = new JTextField(""+MapPanel.WINDOW_HEIGHT,5);
		scaleLabel = new JLabel("scale");
		scaleBox = new JComboBox<String>(scaleList);
		moveButton = new JButton("move");
		// 2番目のパネル.
		categoryButton = new JButton("カテゴリ");
		categoryOsmButton = new JButton("facility");
		displayButton = new JButton("表示");
		displayOsmButton = new JButton("dispaly");
		changeRoadDataModeButton = new JButton("link");
		changeRoadDataModeButton2 = new JButton("arc");
		putFocusButton = new JButton("focus");
		displayFGCRoad = new JButton("fgcRoad");
		// 3番目のパネル.
		deformedButton1 = new JButton("総描1");
		deformedButton2 = new JButton("総描2");
		deformedButton3 = new JButton("generalize");
		deformedButton4 = new JButton("roadClass");
		checkConnectivityButton = new JButton("connect");
		checkConnectivityButton2 = new JButton("connect2");
		
		thresholdSlider = new JSlider(JSlider.HORIZONTAL, 0, 4500, 0);
		thresholdValue = new JLabel("000");

		// 汎用インターフェース.
		publicCheckBox = new JCheckBox("", false);
		
		selectedButtonGroup = new ButtonGroup();
		selectedButtonGroup.add(selectedLnglatRadioButton);
		selectedButtonGroup.add(selectedAddressRadioButton);
		selectedButtonGroup.add(selectedLandmarkRadioButton);
		
		// デフォルトの選択状態.
		scaleBox.setSelectedItem("16");
		
		
		// コンポーネントをパネルに張る--------ここから----------.
		// 1.
		JPanel movePanel = new JPanel();
		JPanel move1Panel = new JPanel();
		JPanel move2Panel = new JPanel();
		JPanel move3Panel = new JPanel();
		JPanel move4Panel = new JPanel();
		JPanel moveButtonPanel = new JPanel();
		JPanel searchNodePanel = new JPanel();
		JPanel searchLinkPanel = new JPanel();
		// 2.
		JPanel displayPanel = new JPanel();
		JPanel display1Panel = new JPanel();
		JPanel display4Panel = new JPanel();
		JPanel display5Panel = new JPanel();
		JPanel display6Panel = new JPanel();
		JPanel display7Panel = new JPanel();
		JPanel display8Panel = new JPanel();
		// 3.
		JPanel secondPanel = new JPanel();
//		JPanel secondDisplay1Panel = new JPanel();
		JPanel secondDisplay2Panel = new JPanel();
		JPanel secondDisplay3Panel = new JPanel();
		JPanel secondDisplay4Panel = new JPanel();
		JPanel secondDisplay5Panel = new JPanel();
		JPanel secondDisplay6Panel = new JPanel();
		JPanel secondDisplay7Panel = new JPanel();
		// 4.
		JPanel forthPanel = new JPanel();
		JPanel forthPanel1 = new JPanel();
		JPanel forthPanel2 = new JPanel();
		JPanel forthPanel3 = new JPanel();
		JPanel forthPanel4 = new JPanel();
		JPanel forthPanel5 = new JPanel();
		JPanel forthPanel6 = new JPanel();
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		move1Panel.setLayout(layout);	// 左側の1行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		//gbc.ipadx = 5;
		layout.setConstraints(selectedLnglatRadioButton, gbc);
		gbc.gridx = 1;
		layout.setConstraints(lngLabel, gbc);
		gbc.gridx = 2;
		layout.setConstraints(lngTextField, gbc);
		gbc.gridx = 3;
		layout.setConstraints(latLabel, gbc);
		gbc.gridx = 4;
		layout.setConstraints(latTextField, gbc);
		move1Panel.add(selectedLnglatRadioButton);
		move1Panel.add(lngLabel);
		move1Panel.add(lngTextField);
		move1Panel.add(latLabel);
		move1Panel.add(latTextField);
		
		move2Panel.setLayout(layout);	// 左側の2行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(selectedAddressRadioButton, gbc);
		gbc.gridx = 1;
		layout.setConstraints(selectedLandmarkRadioButton, gbc);
		gbc.gridx = 2;
		layout.setConstraints(locationTextField, gbc);
		move2Panel.add(selectedAddressRadioButton);
		move2Panel.add(selectedLandmarkRadioButton);
		move2Panel.add(locationTextField);
		
		move3Panel.setLayout(layout);	// 左側の3行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(scaleLabel, gbc);
		gbc.gridx = 1;
		layout.setConstraints(scaleBox, gbc);
		gbc.gridx = 2;
		layout.setConstraints(moveButton, gbc);
		move3Panel.add(scaleLabel);
		move3Panel.add(scaleBox);
		move3Panel.add(moveButton);
		
		move4Panel.setLayout(layout);	// 左側の4行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(windowWidthLabel, gbc);
		gbc.gridx = 1;
		layout.setConstraints(windowWidthTextField, gbc);
		gbc.gridx = 2;
		layout.setConstraints(windowHeightLabel, gbc);
		gbc.gridx = 3;
		layout.setConstraints(windowHeightTextField, gbc);
//		move4Panel.add(windowWidthLabel);
//		move4Panel.add(windowWidthTextField);
//		move4Panel.add(windowHeightLabel);
//		move4Panel.add(windowHeightTextField);
		
		movePanel.setLayout(layout);	// 左側のコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		layout.setConstraints(move1Panel, gbc);
		gbc.gridy = 1;
		layout.setConstraints(move2Panel, gbc);
		gbc.gridy = 2;
		layout.setConstraints(move3Panel, gbc);
		gbc.gridy = 3;
		layout.setConstraints(move4Panel, gbc);
		movePanel.add(move1Panel);
		movePanel.add(move2Panel);
		movePanel.add(move3Panel);
		movePanel.add(move4Panel);
		
		display1Panel.setLayout(layout);	// 右側1行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(categoryButton, gbc);
		gbc.gridx = 1;
		layout.setConstraints(categoryOsmButton, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
//		display1Panel.add(categoryButton);
		display1Panel.add(categoryOsmButton);
		
		display4Panel.setLayout(layout);
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(displayFGCRoad, gbc);
		gbc.gridx = 1;
		layout.setConstraints(putFocusButton, gbc);
		display4Panel.add(displayFGCRoad);
		display4Panel.add(putFocusButton);
		
		display5Panel.setLayout(layout);	// 右側3行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(displayButton, gbc);
		gbc.gridx = 1;
		layout.setConstraints(displayOsmButton, gbc);
//		display5Panel.add(displayButton);
		display5Panel.add(displayOsmButton);
		
		display6Panel.setLayout(layout);	// 右側4行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(changeRoadDataModeButton, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		layout.setConstraints(changeRoadDataModeButton2, gbc);
		display6Panel.add(changeRoadDataModeButton);
		display6Panel.add(changeRoadDataModeButton2);


		
		
		displayPanel.setLayout(layout);	// 右側に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		layout.setConstraints(display1Panel, gbc);
		gbc.gridy = 1;
		layout.setConstraints(display4Panel, gbc);
		gbc.gridy = 2;
		layout.setConstraints(display5Panel, gbc);
		gbc.gridy = 3;
		layout.setConstraints(display6Panel, gbc);
		gbc.gridy = 4;
		layout.setConstraints(display7Panel, gbc);
		gbc.gridy = 5;
		layout.setConstraints(display8Panel, gbc);
		displayPanel.add(display1Panel);
		displayPanel.add(display4Panel);
		displayPanel.add(display5Panel);
		displayPanel.add(display6Panel);
//		displayPanel.add(display7Panel);
//		displayPanel.add(display8Panel);
		
		forthPanel1.setLayout(layout);	// 4番目の1行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(deformedButton1, gbc);
		gbc.gridx = 1;
		layout.setConstraints(deformedButton2, gbc);
		gbc.gridx = 2;
		layout.setConstraints(deformedButton3, gbc);
		gbc.gridx = 3;
		layout.setConstraints(deformedButton4, gbc);
		gbc.gridx = 4;
		layout.setConstraints(checkConnectivityButton, gbc);
		gbc.gridx = 5;
		layout.setConstraints(checkConnectivityButton2, gbc);
//		forthPanel1.add(deformedButton1);
//		forthPanel1.add(deformedButton2);
		forthPanel1.add(deformedButton3);
		forthPanel1.add(deformedButton4);
		forthPanel1.add(checkConnectivityButton);
		forthPanel1.add(checkConnectivityButton2);
		
		forthPanel2.setLayout(layout);	// 4番目の2行目に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(thresholdSlider, gbc);
		gbc.gridx = 1;
		forthPanel2.add(thresholdSlider);
		forthPanel2.add(thresholdValue);
		
		forthPanel.setLayout(layout);	// 4番目のコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(forthPanel1, gbc);
		gbc.gridy = 1;
		layout.setConstraints(forthPanel2, gbc);
		gbc.gridy = 2;
		layout.setConstraints(forthPanel3, gbc);
		gbc.gridy = 3;
		layout.setConstraints(forthPanel4, gbc);
		gbc.gridy = 4;
		layout.setConstraints(forthPanel5, gbc);
		forthPanel.add(forthPanel1);
		forthPanel.add(forthPanel2);
		forthPanel.add(forthPanel3);
		forthPanel.add(forthPanel4);
		forthPanel.add(forthPanel5);
	
		
		// 汎用インターフェース用コンポーネント　1行目.
		JPanel publicPanel1 = new JPanel();
		publicPanel1.setLayout(layout);
		gbc.gridx =0;
		gbc.gridy=0;
		layout.setConstraints(publicCheckBox, gbc);
		publicPanel1.add(publicCheckBox);
		
		
		// 汎用インターフェース用コンポーネント.
		JPanel publicPanel = new JPanel();
		publicPanel.setLayout(layout);
		gbc.gridx = 0;
		gbc.gridy = 0;
		layout.setConstraints(publicPanel1, gbc);
		publicPanel.add(publicPanel1);
		
		this.setLayout(layout);	// パネル全体に関するコンポーネント.
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(movePanel, gbc);
		gbc.gridx = 1;
		layout.setConstraints(displayPanel, gbc);
		gbc.gridx = 2;
		layout.setConstraints(secondPanel, gbc);
		gbc.gridx = 3;
		layout.setConstraints(forthPanel, gbc);
		gbc.gridx = 4;
		layout.setConstraints(publicPanel, gbc);
		this.add(movePanel);
		this.add(displayPanel);
		this.add(secondPanel);
		this.add(forthPanel);
		this.add(publicPanel);
		// コンポーネントをパネルに張る-------ここまで----------.
		
		// リスナに登録.
		// 1番目のパネルに関するリスナ登録.
		selectedLnglatRadioButton.addActionListener(this);
		selectedAddressRadioButton.addActionListener(this);
		selectedLandmarkRadioButton.addActionListener(this);
		//scaleBox.addActionListener(this);
		scaleBox.addItemListener(this);
		moveButton.addActionListener(this);
		// 2番目のパネルに関するリスナ登録.
		categoryButton.addActionListener(this);
		categoryOsmButton.addActionListener(this);
		displayButton.addActionListener(this);
		displayOsmButton.addActionListener(this);
		changeRoadDataModeButton.addActionListener(this);
		changeRoadDataModeButton2.addActionListener(this);
		putFocusButton.addActionListener(this);
		displayFGCRoad.addActionListener(this);
		// 3番目のパネルに関するリスナ登録.
		// 4番目のパネルに関するリスナ登録.
		deformedButton1.addActionListener(this);
		deformedButton2.addActionListener(this);
		deformedButton3.addActionListener(this);
		deformedButton4.addActionListener(this);
		checkConnectivityButton.addActionListener(this);
		checkConnectivityButton2.addActionListener(this);
		thresholdSlider.addChangeListener(this);
		// パブリックインターフェース.
		publicCheckBox.addChangeListener(this);
		
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == thresholdSlider){	// 閾値のスライダーを動かしているとき.
			DecimalFormat df = new DecimalFormat("000");
			thresholdValue.setText("" + df.format(thresholdSlider.getValue()));
		}
		// 閾値のスライダーを動かしてドロップしたとき.
		if(e.getSource() == thresholdSlider && !thresholdSlider.getValueIsAdjusting()){
//			// リンクが閾値以上なら描画.
			mapPanel.updateThreshold(thresholdSlider.getValue());
		}
	}
	

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO 自動生成されたメソッド・スタブ
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == selectedLnglatRadioButton) {	// 緯度経度のラジオボタン選択処理.
			lngTextField.setEditable(true);
			latTextField.setEditable(true);
			locationTextField.setEditable(false);
		} else if (e.getSource() == selectedAddressRadioButton ||
				e.getSource() == selectedLandmarkRadioButton
		) {	// アドレスまたは、ランドマークの　ラジオボタン選択処理.
			lngTextField.setEditable(false);
			latTextField.setEditable(false);
			locationTextField.setEditable(true);
		}else if (e.getSource() == changeRoadDataModeButton) {	// 道路データの表示非表示.
			 mapPanel.insertRoadData("link");
		}else if(e.getSource() == changeRoadDataModeButton2){
			mapPanel.insertRoadData("arc");
		}else if (e.getSource() == moveButton) {	// 移動ボタンが押されたときは指定の位置に移動する.
			String _selectedType = "";
			Point2D lnglat = new Point2D.Double();
			if (selectedLnglatRadioButton.isSelected()) {	// ラジオボタン"座標"が選択されているとき.
				_selectedType = "lnglat";
				lnglat = new Point2D.Double(Double.parseDouble(lngTextField.getText()), Double.parseDouble(latTextField.getText()));
			}else {
				if (selectedAddressRadioButton.isSelected()) {
					_selectedType = "address";
				} else {
					_selectedType = "landmark";
				}
			}
			MapPanel.WINDOW_WIDTH = Integer.parseInt(windowWidthTextField.getText());
			MapPanel.WINDOW_HEIGHT = Integer.parseInt(windowHeightTextField.getText());
			this.mapPanel.moveMap((Point2D.Double)lnglat, locationTextField.getText(), _selectedType,  Integer.parseInt((String)scaleBox.getSelectedItem()));	// 移動.
		}else if(e.getSource() == categoryButton){	// カテゴリボタンが押された.
			_categoryWindow.openCategoryWindow();
		}else if(e.getSource() == categoryOsmButton){	// osmカテゴリボタン.
			_CategoryOsmWindow.openCategoryWindow();
		} else if (e.getSource() == displayButton) {	// 表示ボタンが押されたときは地図上に指定された条件でマーカーを置く.
			mapPanel.insertShopData(_categoryWindow.getGroupCode(), "yahoo");
		}else if(e.getSource() == displayOsmButton){	// OSM.
			mapPanel.insertShopData(_CategoryOsmWindow.getCategory(), "OSM");
//			System.out.println(_CategoryOsmWindow.getCategory());
		}else if(e.getSource() == putFocusButton){	// EmmaのFoucs描画.
			mapPanel.drawFocusGlue();
		}else if(e.getSource() == displayFGCRoad){	// FGCの道路描画.
			mapPanel.drawFGC_link();
		}else if(e.getSource() == deformedButton1){	// ストローク関係.	// チェックがあればその場で表示，スライダーで数を指定.
			mapPanel.SOA1(thresholdSlider.getValue(), true, !publicCheckBox.isSelected());
		}else if(e.getSource() == deformedButton2){	// ストローク関係.　// チェックがあればその場で表示，スライダーで数を指定.
			mapPanel.SOA2(0);
		}else if(e.getSource() == deformedButton3){	// ストローク関係.　// チェックがあればその場で表示，スライダーで数を指定.
			if(publicCheckBox.isSelected() == false){
				mapPanel.SOA3(thresholdSlider.getValue(), true);
			}else{
				mapPanel.createResizableMapWindow(3);// ウインドウの変化で総描.
			}
		}else if(e.getSource() == deformedButton4){	// 道路クラスで総描.
			mapPanel.deformRoadClass();
		}else if(e.getSource() == checkConnectivityButton){	// コネクティビティーの評価.
			mapPanel.evalConnectivity("connect");
		}else if(e.getSource() == checkConnectivityButton2){	// コネクティビティーの評価2.
			mapPanel.evalConnectivity("connect2");
		}
		
		
	}
}
