package mySrc.panel.mapPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import mySrc.coordinate.ConvertMercatorXyCoordinate;
import mySrc.coordinate.LngLatMercatorUtility;

import mySrc.QuickSort2;
import mySrc.WriteDataToFile3;
import mySrc.coordinate.ConvertLngLatXyCoordinate;
import mySrc.coordinate.GetLngLatOsm;
import mySrc.db.LEB_eval_ver.OsmFatStrokeEvalVer;
import mySrc.db.LEB_eval_ver.OsmLR;
import mySrc.db.LEB_eval_ver.OsmRoadClassEval;
import mySrc.db.getData.OsmFatStroke;
import mySrc.db.getData.OsmDataGeom;
import mySrc.db.getData.OsmDataGeom2;
import mySrc.db.getData.OsmLooproad2;
import mySrc.db.getData.OsmRoadDataGeom;
import mySrc.db.getData.OsmStrokeDataGeom;
import mySrc.db.getData.OsmLooproad;
import mySrc.db.getData.HandleDbSpecificStroke;
import mySrc.db.emma.*;
import mySrc.panel.MyMap;
import mySrc.panel.inputPanel.InputPanel;
import mySrc.panel.outputPanel.OutputPanel;
import mySrc.yahooAPI.ContentsGeocorder;
import mySrc.yahooAPI.LocalSearch;
import mySrc.elastic.*;

/**
 * 左下の画面(地図表示)
 * @author murase
 *
 */
public class MapPanel extends JPanel{
	/** 地図パネルの横幅. */
	public static  int WINDOW_WIDTH = 1024;
	/** 地図パネルの高さ. */
	public static  int WINDOW_HEIGHT = 1024;
	/** 地図の大きさ */
	public static Point WINDOW_SIZE = new Point(WINDOW_WIDTH, WINDOW_HEIGHT);
	/** 右側のパネルの幅(出力用パネル). */
	public static final int RIGHT_AREA_WIDTH = 100;
	/** 上側のパネルの高さ(入力用パネル). */
	public static final int UPPER_AREA_HEIGHT = 150;
	
	/** 初期の経度. */
	private static final double DEFAULT_LNG = 136.9309671669116;	// 鶴舞公園.
	/** 初期の緯度. */
	private static final double DEFAULT_LAT = 35.15778942665804;	// 鶴舞公園.
	/** 初期のスケール. */
	private static final int DEFAULT_SCALE = 15;
	/** 地図スタイル */
	private static final String DEFAULT_MAPSTYLE = "mapnik";
	/** デフォルトのマーカーサイズ */
	public static final int DEFAULT_MARKER_SIZE = 10;
	/** 小さいマーカーサイズ */
	public static final int SMALL_MARKER_SIZE = 4;
	
	// 地図データのURL.
	public static final String HOSTNAME = "http://rain.elcom.nitech.ac.jp/OsmStaticMap/staticmap.php?";
	public static final String PARAM_CENTER = "center=";
	public static final String PARAM_ZOOM = "&zoom=";
	public static final String PARAM_SIZE = "&size=";
	public static final String PARAM_MAPTYPE = "&maptype=";
	
	// 各種インスタンス変数.
	public InputPanel _inputPanel;
	public MapPanelPaint _mapPanelPaint;
	public MapPanelEvent _mapPanelEvent;
	public ConvertLngLatXyCoordinate _convert;
	
	// 地図基本データ.
	/** 現在の緯度経度 */
	public Point2D _lngLat = new Point2D.Double(DEFAULT_LNG, DEFAULT_LAT);
	/** 現在のスケール*/
	public int _scale = DEFAULT_SCALE;
	/** 右上の緯度経度 */
	public Point2D _upperLeftLngLat = new Point2D.Double();
	/** 左下の緯度経度 */
	public Point2D _lowerRightLngLat = new Point2D.Double();
	
	/** 地図画像 */
	public BufferedImage _bufferedImage;
	public Image _image;
	
	// 道路基本データ.
	/** 道路データ関係 */
	public OsmRoadDataGeom _osmRoadDataGeom = new OsmRoadDataGeom();
	/** リンクを描画するフラグ */
	public boolean _roadDataFlg = false;
	/** arcを描画 */
	public boolean _roadData2Flg = false;
	
	// 施設データ関係.
	/** 施設データ関係 */
	public OsmDataGeom _osmDataGeom = new OsmDataGeom();
	/** 描画フラグ */
	public boolean _markFlg = false;
	
	// ストローク関係.
	/** ストローク関係 */
	public OsmStrokeDataGeom _osmStrokeDataGeom = new OsmStrokeDataGeom();	
	/** fat-stroke関係 */
	public OsmFatStroke _osmFatStroke = new OsmFatStroke();
	/** LBS評価用 */
	OsmFatStrokeEvalVer osmFatStrokeEvalVer = new OsmFatStrokeEvalVer();
	
	// SOA関係.
	/** SOA1描画フラグ */
	private boolean _strokeGeomFlg1 = false;
	/** SOA3描画フラグ */
	private boolean _soa3Flg = false;
	/** 順序付けたストロークID */
	public ArrayList<Integer> _orderedStrokeIndexArrayList = new ArrayList<>();
	/** 閾値 */
	private int _soaThreshold = 0;
	/** 各ストロークに接する周回道路内にあるお店の数 */
	ArrayList<Integer> _shopNum = new ArrayList<>();
	
	// 道路クラスでの総描.
	/** 道路の形状を表す */
	public ArrayList<ArrayList<Line2D>> _roadClassArc;
	/** フラグ */
	public boolean _roadClassFlg = false;
	
	// ピン表示関係.
	/** ピンの位置 */
	public ArrayList<Point> _pinPoint = new ArrayList<>();
	/** ピン描画フラグ */
	public boolean _drawPinFlg = false;
	/** 指定した場所の周回道路を通るストローク .get(i).get(j).get(k)...i:何番目のピンか,j:何番目のストロークか,k:何番目のリンクか */
	private ArrayList<ArrayList<ArrayList<Line2D>>> _concatStroke = new ArrayList<>();
	/** そのストロークID */
	private ArrayList<ArrayList<Integer>> _concatStrokeId = new ArrayList<>();
	
	// test.
	OsmLooproad _testOsmLooproad = new OsmLooproad();
	/** 各ストロークに接する周回道路のジオメトリ */
	ArrayList<ArrayList<ArrayList<Point2D>>> _testPolygon = new ArrayList<>();
	boolean _testFlg = false;
	
	// emma関係.
	/** focusの地図画像 */
	public BufferedImage _focusImage;
	/** contextの地図画像  */
	public BufferedImage _glueImage;
	/** 描画フラグ */
	public boolean _fgImageFlg = false;
	
	// 経路探索用データ.
	/** 経路探索の結果 */
	public ArrayList<Point2D> _routingResult = new ArrayList<>();
	/** 経路探索の描画フラグ */
	public boolean _routingFlg = false;
	
	// focus-glue-context道路の描画.
	/** 道路データ関係 */
	public FGC_road  _fgc_road;
	/** リンクを描画するフラグ */
	public boolean _fgcRoadFlg = false;
	
	// 汎用的な描画用変数.
	private ArrayList<ArrayList<Line2D>> _commonRoad = new ArrayList<>();
	private boolean _commonFlg = false;
	
	///////////////////////////////////////////////////////////////////////////
	//////////////////ここまで変数定義/////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	public MapPanel(OutputPanel aOutputPanel, MyMap aMyMap) {
		this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		_mapPanelPaint = new MapPanelPaint(this);
		_mapPanelEvent = new MapPanelEvent(this);
		
		// リス名の登録.
		addMouseListener(_mapPanelEvent);
		addKeyListener(_mapPanelEvent);
		
		makeMap();
		
	}
	
	/**
	 * 描画関係
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);	// アンチエイリアス処理.
		
		// 初期処理.
		_mapPanelPaint.init(DEFAULT_MARKER_SIZE, g, _convert);
		// 地図の描画.
		_mapPanelPaint.paintMap(_image, _bufferedImage);
		// 中心点の表示
		_mapPanelPaint.paintCenterPoint(_lngLat, _convert);
		// 道路データの描画.
		_mapPanelPaint.paintRoadData(_roadDataFlg, _osmRoadDataGeom._link);
		_mapPanelPaint.paintRoadData2(_roadData2Flg, _osmRoadDataGeom._arc);

		
		// ストロークの描画SOA1
		_mapPanelPaint.paintSOAStroke(_strokeGeomFlg1, _osmStrokeDataGeom._strokeArc, _orderedStrokeIndexArrayList, _soaThreshold);
		//fatstroke (soa3).
		_mapPanelPaint.paintSOA3Stroke(_soa3Flg, osmFatStrokeEvalVer._strokeArc, _orderedStrokeIndexArrayList, _soaThreshold);
		// roadclass
		_mapPanelPaint.paintRoadClass(_roadClassFlg, _roadClassArc);
		// test polygonの描画.
		_mapPanelPaint.paintPolygon(_testPolygon, _osmStrokeDataGeom._strokeArc ,_testFlg, _soaThreshold, _shopNum);
		// ピンの描画.
		_mapPanelPaint.paintPin(_pinPoint, _drawPinFlg, _concatStroke);
		// 確認用ピンの描画.
		//_mapPanelPaint.paintPinText(_concatStroke, _drawPinFlg);
		// 周辺の建物の座標を描画.
		_mapPanelPaint.paintShopData(_markFlg, _osmDataGeom._facilityLocation);
		// emmaのfocus,glueの描画.
		_mapPanelPaint.paintFoucsGlue(_focusImage, _glueImage, _fgImageFlg);
		// fgcの道路の描画
		_mapPanelPaint.paintFGCRoad(_fgc_road, _fgcRoadFlg);
		
		
		// 経路探索の結果の描画.
		_mapPanelPaint.paintRouting(_routingResult, _routingFlg);

		// 汎用的なArrayList<ArrayList<line2dの描画>>.
		_mapPanelPaint.paintCommonRoad(_commonFlg, _commonRoad);
	}
	
	/**
	 * 地図の描画
	 */
	public void makeMap(){
		URL url = null;
		try {
			url = new URL(HOSTNAME +
					PARAM_CENTER + _lngLat.getY() +","+_lngLat.getX() +
					PARAM_ZOOM + _scale +
					PARAM_SIZE + WINDOW_WIDTH +"x"+ WINDOW_HEIGHT +
					PARAM_MAPTYPE + DEFAULT_MAPSTYLE
					);
			System.out.println(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		revalidate();//遅延自動レイアウトをサポートします。  
		// bufferedimage を使った画像表示.
		try{
			_bufferedImage = ImageIO.read(url);
		}catch(Exception e){
			e.printStackTrace();
		}
//		_bufferedImage = reduce1(bufferedImage, (int)(WINDOW_WIDTH/_reduceRate), (int)(WINDOW_HEIGHT/_reduceRate));
		// imageを使った画像表示.
		_image = Toolkit.getDefaultToolkit().getImage(url);	// ? 画像ファイルのイメージオブジェクト.
		// 左上と右下の座標取得.
		GetLngLatOsm getLngLatOsm = new GetLngLatOsm(_lngLat, _scale, new Point(WINDOW_WIDTH, WINDOW_HEIGHT));
		_upperLeftLngLat = getLngLatOsm._upperLeftLngLat;
		_lowerRightLngLat = getLngLatOsm._lowerRightLngLat;
		System.out.println("upperLeftLnglat"+_upperLeftLngLat);
		System.out.println("lowerRightLngLat"+_lowerRightLngLat); 
		System.out.println("polygon(("+
				_upperLeftLngLat.getX()+" "+_upperLeftLngLat.getY()+","+
				_upperLeftLngLat.getX()+" "+_lowerRightLngLat.getY()+","+
				_lowerRightLngLat.getX()+" "+_lowerRightLngLat.getY()+","+
				_lowerRightLngLat.getX()+" "+_upperLeftLngLat.getY()+","+
				_upperLeftLngLat.getX()+" "+_upperLeftLngLat.getY()+""+
				"))");
		// 緯度経度とアプレット座標の変換用インスタンス.
		this._convert = new ConvertLngLatXyCoordinate((Point2D.Double)_upperLeftLngLat,
				(Point2D.Double)_lowerRightLngLat, new Point(WINDOW_WIDTH, WINDOW_HEIGHT));

//		_unsetFlg();
		
		repaint();
	}
	
	/**
	 * 入力した場所に移動する(移動ボタン移動したときに実行されるメソッド).
	 * @param aLnglat		緯度経度.
	 * @param aLocationName	地名に関するキーワード.
	 * @param aSelectedType	選択された地図移動タイプ:"lnglat" or "location" or "landmark".
	 * @param aScale		スケール.
	 */
	public void moveMap (Point2D.Double aLnglat, String aLocationName, String aSelectedType,int aScale) {
		// 値が入力されていないときは現在の座標.
		// 座標が選択されているときは緯度経度で指定地点へ移動.
		if (aSelectedType.equals("lnglat") && aLnglat.x != 0.0 && aLnglat.y != 0.0) {
			_lngLat = aLnglat;
		} else if ((aSelectedType.equals("address")||aSelectedType.equals("landmark"))&&
				!aLocationName.equals("")) {	// 地名からジオコーディングで指定地点へ移動.
			ContentsGeocorder contentsGeocorder = new ContentsGeocorder(aLocationName, aSelectedType);
			Point2D.Double lnglat = contentsGeocorder.getLnglatValue();
			_lngLat = lnglat;
		}
		this._scale = aScale;
		makeMap();
	}
	
	
	/**
	 * データベースからデータを取得し変数へ格納.
	 */
	public void insertRoadData(String type){
		if (_roadDataFlg == true && type=="link") {
			_roadDataFlg = false;
		}else if(_roadDataFlg != true && type=="link"){
			_roadDataFlg = true;
		}
		else if(_roadData2Flg == true && type=="arc"){
			_roadData2Flg = false;
		}else if(_roadData2Flg != true && type=="arc"){
				_roadData2Flg = true;
		}
		_osmRoadDataGeom = new OsmRoadDataGeom();
		_osmRoadDataGeom.startConnection();
		_osmRoadDataGeom.insertOsmRoadData(_upperLeftLngLat, _lowerRightLngLat);
		_osmRoadDataGeom.endConnection();
		repaint();
	}
	
	/**
	 * YahooローカルサーチAPIを使ったHTTPリクエストをしレスポンス(XMLデータ)をメンバ変数へ格納する.
	 * 格納される変数　_pointDataArray[] _pointInfoNum 周辺建物の緯度経度とその数.
	 * グループコードと範囲を指定する.
	 * InputPanelから呼び出される 1番目.
	 * @param aGroupCode	選択されたグループコード.
	 * @param aRadius		選択された半径(-1のときは矩形範囲選択　そうでないときは円形範囲選択).
	 */
	public void insertShopData(ArrayList<String> aGroupCode, String type){
		if(_markFlg == true){
			_markFlg = false;	// 点の描画消す.
			repaint();
			return;
		}
		_osmDataGeom = new OsmDataGeom();
		if(type == "yahoo"){	// yahooデータ.
			for (int i=0; i<aGroupCode.size(); i++) {
				LocalSearch localSearch;
				localSearch = new LocalSearch(new Point2D.Double(_upperLeftLngLat.getX(), _lowerRightLngLat.getY()),
						new Point2D.Double(_lowerRightLngLat.getX(), _upperLeftLngLat.getY()), aGroupCode.get(i));
				_osmDataGeom._facilityLocation.addAll(localSearch.getLatLngArrayList());
				_osmDataGeom._facilityName.addAll(localSearch.getNameArrayList());
			}
		}
		if(type == "OSM"){	// OSMデータ.
			_osmDataGeom = new OsmDataGeom();
			_osmDataGeom.startConnection();
			for(String code : aGroupCode){
				_osmDataGeom.insertFacilityFromCategory(code.split(":")[0], code.split(":")[1], _upperLeftLngLat, _lowerRightLngLat);
			}
			_osmDataGeom.endConnection();
		}
		_markFlg = true;
		repaint();
	}
	
	
	/**
	 * ストローク順序付けアルゴリズム 長さのみ
	 */
	public void SOA1(int aThresholdValue, boolean aPaintFlg, boolean aCutFlg){
		if(_strokeGeomFlg1 == true){// 表示していたら消す.
			_strokeGeomFlg1 = false;
			repaint();
			return;
		}else{
			_strokeGeomFlg1 = aPaintFlg;
		}
		// 画面内にあるストロークの取得.
		_osmStrokeDataGeom = new OsmStrokeDataGeom();
		_osmStrokeDataGeom.startConnection();
		if (aCutFlg){
			_osmStrokeDataGeom.cutOutStroke(_upperLeftLngLat, _lowerRightLngLat);
		}else{
			_osmStrokeDataGeom.insertStrokeData(_upperLeftLngLat, _lowerRightLngLat);
		}
		_osmStrokeDataGeom.endConnection();
		
		// 長さ順にストロークを並び替え.
		ArrayList<Integer> tmp = new ArrayList<>();
		for(int i=0; i<_osmStrokeDataGeom._strokeId.size(); i++){
			tmp.add(i);
		}
		QuickSort2<java.lang.Double, Integer> quickSort2 = new QuickSort2<>(_osmStrokeDataGeom._strokeLength, tmp, true);
		_orderedStrokeIndexArrayList = quickSort2.getArrayList2();
		
		
		
//		for(int i=0; i<_strokeGeomArc.size(); i++){
//			System.out.println("ストローク開始 "+i);
//			for(int j=0; j<_strokeGeomArc.get(_orderedStrokeIndexArrayList.get(i)).size(); j++){
//				System.out.println("link開始 "+j);
//				for(int k=0; k<_strokeGeomArc.get(_orderedStrokeIndexArrayList.get(i)).get(j).size(); k++){
//					System.out.println(
//							_strokeGeomArc.get(_orderedStrokeIndexArrayList.get(i)).get(j).get(k).getP1() +
//							" : "+
//							_strokeGeomArc.get(_orderedStrokeIndexArrayList.get(i)).get(j).get(k).getP2());
//				}
//				System.out.println("$$$$$$$$$$$$$$$$$$");
//			}
//			System.out.println("#########################");
//		}
		
		
		
		_soaThreshold = _osmStrokeDataGeom._strokeId.size() < aThresholdValue ? _osmStrokeDataGeom._strokeId.size()-1 : aThresholdValue;
		_soaThreshold = _osmStrokeDataGeom._strokeId.size();
		repaint();
	}
	
	/**
	 * 
	 * test 指定のストロークを描画し，接する周回道路，お店の数を表示する
	 */
	public void SOA2(int aNum){
		_testPolygon = new ArrayList<>();
		_shopNum = new ArrayList<>();
		
		// ストロークを取り出す.
		_osmStrokeDataGeom = new OsmStrokeDataGeom();
		_osmStrokeDataGeom.startConnection();
		_osmStrokeDataGeom.cutOutStroke(_upperLeftLngLat, _lowerRightLngLat);
		// 施設データを切り出す.
		OsmDataGeom2 osmDataGeom2 = new OsmDataGeom2();
		osmDataGeom2.startConnection();
		osmDataGeom2.createTempTable("parking", _upperLeftLngLat, _lowerRightLngLat);
		//周回道路を切り出す
		OsmLooproad2 osmLooproad2 = new OsmLooproad2();
		osmLooproad2.startConnection();
		osmLooproad2.createTmpLooproadDb(_upperLeftLngLat, _lowerRightLngLat);
		
		// 指定したストロークと接する周回道路を求める.
		for(int i=0; i<_osmStrokeDataGeom._strokeArcString.size(); i++){
			//ストロークと接する周回道路を取得する
			osmLooproad2.calcNeighberLooproadFromTmpTableUsingStroke(_osmStrokeDataGeom._strokeArcString.get(i));
			_testPolygon.add(osmLooproad2.touchedLooproadGeom);
			// 周回道路から施設データの数を取り出す.
			int shopNum = osmDataGeom2.searchShopInfoFromAreaIds(osmLooproad2.areaIdArrayList);
			_shopNum.add(shopNum);
			
		}
		osmDataGeom2.endConnection();
		osmLooproad2.endConnection();
		_osmStrokeDataGeom.endConnection();
		_testFlg = true;
		repaint();
	}
	
	public static final double FACILITY_PARAM = 0.2;
	public static final double LENGTH_PARAM = 1 - FACILITY_PARAM;
	/**
	 * ストローク順序付けアルゴリズム 長さとお店の数で決める(fatstrokeを使う)
	 * 
	 * fatstrokeのお店の数のはrelation_dbのデータから計算する
	 * 
	 */
	public void SOA3(int aThresholdValue, boolean aPaintFlg){
		if(aPaintFlg == true){
			if(_soa3Flg == true){// 表示していたら消す.
				_soa3Flg = false;
				repaint();
				return;
			}
		}
		if(_soa3Flg == false){
			_soa3Flg = true;
		}
//		// strokeを取り出す.
//		_osmStrokeDataGeom = new OsmStrokeDataGeom();
//		_osmStrokeDataGeom.startConnection();
//		_osmStrokeDataGeom.cutOutStroke(_upperLeftLngLat, _lowerRightLngLat);	// 切り出したストロークと切り出さないストロークの両方を求める.
//		_osmStrokeDataGeom.endConnection();
//		// fatstrokeを取り出す.
//		_osmFatStroke = new OsmFatStroke();
//		_osmFatStroke.startConnection();
//		_osmFatStroke.insertFatStrokeFromMBR(_upperLeftLngLat, _lowerRightLngLat, _inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[1]);
//		_osmFatStroke.endConnection();
		
		
		osmFatStrokeEvalVer = new OsmFatStrokeEvalVer();
		osmFatStrokeEvalVer.startConnection();
		osmFatStrokeEvalVer.cutOutStroke(_upperLeftLngLat, _lowerRightLngLat, _inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[1]);
		osmFatStrokeEvalVer.endConnection();
		
		// 各ストロークと紐づく施設データ(画面外も含む)を求める.
		_shopNum = new ArrayList<>();
		for(int i=0; i<osmFatStrokeEvalVer._strokeId.size(); i++){
			// 指定したストロークIDがfatstrokeにあるか.
			if(osmFatStrokeEvalVer._strokeIdToIndexHash.containsKey(osmFatStrokeEvalVer._strokeId.get(i))){
				// facility_idが0ならそのストロークには施設データが1つも紐づいていない.
				if(osmFatStrokeEvalVer._fatStrokeFacilityId.get(osmFatStrokeEvalVer._strokeId.get(i)).get(0) == 0 ){
					_shopNum.add(0);
				}else{
					_shopNum.add(osmFatStrokeEvalVer._fatStrokeFacilityId.get(osmFatStrokeEvalVer._strokeId.get(i)).size());
				}
			}else{
				_shopNum.add(0);
			}
		}
		
		// すべてのストロークの長さ(画面外も含む)を取り出す.
		double sumShopNum=0, sumStrokeLength=0;
		for(int i=0; i<_shopNum.size(); i++){
			sumStrokeLength += osmFatStrokeEvalVer._strokeLength.get(i);
			//sumStrokeLength += osmFatStrokeEvalVer._strokeLength.get(i);
		}
		// すべてのお店を取り出す(画面gaimo).
		OsmDataGeom osmDataGeom = new OsmDataGeom();
		osmDataGeom.startConnection();
		osmDataGeom.insertFacilityFromCategory(_inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[0],
				_inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[1], _upperLeftLngLat, _lowerRightLngLat);
		osmDataGeom.endConnection();
		sumShopNum = osmDataGeom._facilityId.size();
		// 長さとお店の数で並び替え.
		ArrayList<Double> strokeWeight = new ArrayList<>();	// ストロークの重要度(0~1).
		for(int i=0; i<_shopNum.size(); i++){
			// お店の重み.
			double shopWeight = (double)_shopNum.get(i)/sumShopNum*FACILITY_PARAM;
			System.out.println("shop num = "+_shopNum.get(i));
			System.out.println("shopWeight = "+shopWeight);
			// ストロークの長さの重み.
			double lengthWeight = (double)osmFatStrokeEvalVer._strokeLength.get(i)/sumStrokeLength*LENGTH_PARAM;
			System.out.println("length = "+osmFatStrokeEvalVer._strokeLength.get(i));
			System.out.println("lengthWeight = "+lengthWeight);
			double weight = (shopWeight + lengthWeight)/2;
			strokeWeight.add(weight);
		}
		
		System.out.println(strokeWeight);
		
		// インデックス用.
		ArrayList<Integer> tmp = new ArrayList<>();
		for(int i=0; i<osmFatStrokeEvalVer._strokeArc.size(); i++){
			tmp.add(i);
		}
		System.out.println(tmp);
		// ソート.
		QuickSort2<Double,Integer> quickSort2 = new QuickSort2<>(strokeWeight, tmp, true);
		_orderedStrokeIndexArrayList = quickSort2.getArrayList2();
		System.out.println(_orderedStrokeIndexArrayList);
		
		if (aPaintFlg){
			repaint();
		}
		
		evalForLBS();
		
	}
	
	/**
	 * LBS用の評価
	 * 上位nほんのストロークに対してそれらのストロークの合計長(画面内のみ)と隣接する施設の数(画面内のみ，重複しない)を数える
	 */
	public void evalForLBS(){
		
		// 上位Nこのストロークを取り出したときのストローク長総和(画面内のみ).
		ArrayList<Double> strokeLengthEvalArrayList = new ArrayList<>();
		// 上位ｎこのストロークを取り出したときの到達可能な施設の総和(画面内のみ).
		ArrayList<Integer> facilityNumEvalArrayList = new ArrayList<>();
		
		// 上位n番目のストロークの長さ(画面内のみ).
		for(int aTopN=0; aTopN<osmFatStrokeEvalVer._strokeId.size(); aTopN++){
			double topN_strokeLength = 0;
			for(int i=0; i<aTopN; i++){
				topN_strokeLength += osmFatStrokeEvalVer._subStrokeLength.get(_orderedStrokeIndexArrayList.get(i));
//				System.out.println("length in Window =  "+osmFatStrokeEvalVer._subStrokeLength.get(_orderedStrokeIndexArrayList.get(i)));
			}
			strokeLengthEvalArrayList.add(topN_strokeLength);
			
			// 上位n番目のストロークに隣接するお店の数(画面内のみ，重複しない).
			int topN_facilityNum = 0;
			// 施設IDのハッシュ.
			HashSet<Integer> facilityListHasSet = new HashSet<>();
			for(int i=0; i<aTopN; i++){
				ArrayList<Point2D> locationArray = osmFatStrokeEvalVer._fatstrokeFacilityLocation.get(osmFatStrokeEvalVer._strokeId.get(_orderedStrokeIndexArrayList.get(i)));
				for(int j=0; j<locationArray.size(); j++){
					// 画面内にあれば追加.
					if(
							_upperLeftLngLat.getX() < locationArray.get(j).getX() &
							locationArray.get(j).getX() < _lowerRightLngLat.getX() &
							_lowerRightLngLat.getY() < locationArray.get(j).getY() &
							locationArray.get(j).getY() < _upperLeftLngLat.getY()
					){
						facilityListHasSet.add(osmFatStrokeEvalVer._fatStrokeFacilityId.get(osmFatStrokeEvalVer._strokeId.get(_orderedStrokeIndexArrayList.get(i))).get(j));
					}
				}
//				System.out.println("shopNum in Window = " + facilityListHasSet.size());
			}
			topN_facilityNum = facilityListHasSet.size();
			facilityNumEvalArrayList.add(topN_facilityNum);
		}
		System.out.println(strokeLengthEvalArrayList);
		System.out.println(facilityNumEvalArrayList);
		WriteDataToFile3<Double, Integer> writeDataToFile3 = new WriteDataToFile3<>(strokeLengthEvalArrayList, facilityNumEvalArrayList);
		
	}
	
	
	/**
	 * 道路クラスで総描
	 */
	public void deformRoadClass(){
		// 道路データを取得.
		_roadClassArc = new ArrayList<>();
		// WKT.
		ArrayList<String> roadStrings = new ArrayList<>();
		// 道路の長さ.
		ArrayList<Double> length = new ArrayList<>();
		// 合計長.
		double sumLength = 0;
		OsmRoadClassEval osmRoadClassEval = new OsmRoadClassEval();
		osmRoadClassEval.startConnection();
		
		osmRoadClassEval.insertOsmRoadData(_upperLeftLngLat, _lowerRightLngLat, 13);	// 国道.
		_roadClassArc.addAll(osmRoadClassEval._arc);
		roadStrings.addAll(osmRoadClassEval._geomString);
		length.addAll(osmRoadClassEval._length);
		osmRoadClassEval.insertOsmRoadData(_upperLeftLngLat, _lowerRightLngLat, 14);
		_roadClassArc.addAll(osmRoadClassEval._arc);
		roadStrings.addAll(osmRoadClassEval._geomString);
		length.addAll(osmRoadClassEval._length);
		
		osmRoadClassEval.insertOsmRoadData(_upperLeftLngLat, _lowerRightLngLat, 15);	// 地方主要道.
		_roadClassArc.addAll(osmRoadClassEval._arc);
		roadStrings.addAll(osmRoadClassEval._geomString);
		length.addAll(osmRoadClassEval._length);
		osmRoadClassEval.insertOsmRoadData(_upperLeftLngLat, _lowerRightLngLat, 16);	// .
		_roadClassArc.addAll(osmRoadClassEval._arc);
		roadStrings.addAll(osmRoadClassEval._geomString);
		length.addAll(osmRoadClassEval._length);
		
		osmRoadClassEval.insertOsmRoadData(_upperLeftLngLat, _lowerRightLngLat, 21);	// 地方一般道.
		_roadClassArc.addAll(osmRoadClassEval._arc);
		roadStrings.addAll(osmRoadClassEval._geomString);
		length.addAll(osmRoadClassEval._length);
		osmRoadClassEval.insertOsmRoadData(_upperLeftLngLat, _lowerRightLngLat, 22);	// .
		_roadClassArc.addAll(osmRoadClassEval._arc);
		roadStrings.addAll(osmRoadClassEval._geomString);
		length.addAll(osmRoadClassEval._length);
		
		osmRoadClassEval.insertOsmRoadData(_upperLeftLngLat, _lowerRightLngLat, 31);	// 一般道2車線.
		_roadClassArc.addAll(osmRoadClassEval._arc);
		roadStrings.addAll(osmRoadClassEval._geomString);
		length.addAll(osmRoadClassEval._length);
		
		osmRoadClassEval.endConnection();
		// 合計長を求める.
		for(Double one: length){
			sumLength += one;
		}
		
		// 道路に接する周回道路を取り出す.
		// 周回道路ID.
		ArrayList<Integer> areaIdArrayList = new ArrayList<>();
		HashSet<Integer> tmp = new HashSet<>();
		OsmLooproad2 osmLooproad2 = new OsmLooproad2();
		osmLooproad2.startConnection();
		osmLooproad2.createTmpLooproadDb(_upperLeftLngLat, _lowerRightLngLat);
		for(int i=0; i<roadStrings.size(); i++){
			osmLooproad2.calcNeighberLooproadFromTmpTableUsingStroke(roadStrings.get(i));
			tmp.addAll(osmLooproad2.areaIdArrayList);
		}
		areaIdArrayList.addAll(tmp);
		osmLooproad2.endConnection();
		
		// 周回道路IDから目的のカテゴリの施設データを取得する.
		OsmDataGeom2 osmDataGeom2 = new OsmDataGeom2();
		osmDataGeom2.startConnection();
		osmDataGeom2.createTempTable(_inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[1], _upperLeftLngLat, _lowerRightLngLat);
		// 隣接する施設の数を求める.
		int shopNum = osmDataGeom2.searchShopInfoFromAreaIds(areaIdArrayList);
		osmDataGeom2.endConnection();

		System.out.println("sumLength = "+ sumLength + ",  sumShopNum = "+shopNum);
		
		
		_roadClassFlg = true;
		repaint();
	}
	
	/**
	 * コネクティビティーの評価
	 */
	public void evalConnectivity(String aType){
		if(_soa3Flg == true){// 表示していたら消す.
			_soa3Flg = false;
			repaint();
			return;
		}
		if(_soa3Flg == false){
			_soa3Flg = true;
		}
		osmFatStrokeEvalVer = new OsmFatStrokeEvalVer();
		osmFatStrokeEvalVer.startConnection();
		osmFatStrokeEvalVer.cutOutStroke(_upperLeftLngLat, _lowerRightLngLat, _inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[1]);
		osmFatStrokeEvalVer.endConnection();
		
		// 各ストロークと紐づく施設データ(画面外も含む)を求める.
		_shopNum = new ArrayList<>();
		for(int i=0; i<osmFatStrokeEvalVer._strokeId.size(); i++){
			// 指定したストロークIDがfatstrokeにあるか.
			if(osmFatStrokeEvalVer._strokeIdToIndexHash.containsKey(osmFatStrokeEvalVer._strokeId.get(i))){
				// facility_idが0ならそのストロークには施設データが1つも紐づいていない.
				if(osmFatStrokeEvalVer._fatStrokeFacilityId.get(osmFatStrokeEvalVer._strokeId.get(i)).get(0) == 0 ){
					_shopNum.add(0);
				}else{
					_shopNum.add(osmFatStrokeEvalVer._fatStrokeFacilityId.get(osmFatStrokeEvalVer._strokeId.get(i)).size());
				}
			}else{
				_shopNum.add(0);
			}
		}
		
		// すべてのストロークの長さ(画面外も含む)を取り出す.
		double sumShopNum=0, sumStrokeLength=0;
		for(int i=0; i<_shopNum.size(); i++){
			sumStrokeLength += osmFatStrokeEvalVer._strokeLength.get(i);
		}
		// すべてのお店を取り出す(画面外も).
		OsmDataGeom osmDataGeom = new OsmDataGeom();
		osmDataGeom.startConnection();
		osmDataGeom.insertFacilityFromCategory(_inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[0],
				_inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[1], _upperLeftLngLat, _lowerRightLngLat);
		osmDataGeom.endConnection();
		sumShopNum = osmDataGeom._facilityId.size();
		// 長さとお店の数で並び替え.
		ArrayList<Double> strokeWeight = new ArrayList<>();	// ストロークの重要度(0~1).
		for(int i=0; i<_shopNum.size(); i++){
			// お店の重み.
			double shopWeight = (double)_shopNum.get(i)/sumShopNum*FACILITY_PARAM;
			// ストロークの長さの重み.
			double lengthWeight = (double)osmFatStrokeEvalVer._strokeLength.get(i)/sumStrokeLength*LENGTH_PARAM;
			double weight = (shopWeight + lengthWeight)/2;
			strokeWeight.add(weight);
		}
		// インデックス用.
		ArrayList<Integer> tmp = new ArrayList<>();
		for(int i=0; i<osmFatStrokeEvalVer._strokeArc.size(); i++){
			tmp.add(i);
		}
		// ソート.
		QuickSort2<Double,Integer> quickSort2 = new QuickSort2<>(strokeWeight, tmp, true);
		_orderedStrokeIndexArrayList = quickSort2.getArrayList2();
		repaint();
		
		switch(aType){
			case "connect":
				evalIntersection();
				break;
			case "connect2":
				evalIntersection2();
				break;
			default:
				System.out.println("タイプが当てはまらないです");
				break;
		}
	}
	
	
	// ストロークを1つ取り出す.
	// そのストロークが他のストローク群と交差するならそのストローク群に追加する.
	// 複数のストローク群と交差するならそれらを1つのストローク群と数る.
	/**
	 * ストロークネットワークのうちストローク数最大となる連結グラフのストローク数を出力する
	 * ストロークを1つ取り出す.
	 * そのストロークが他のストローク群と交差するならそのストローク群に追加する.
	 * 複数のストローク群と交差するならそれらを1つのストローク群と数る.
	 */
	public void evalIntersection(){
		// 連結グラフのストローク数最大のグラフのストローク数.
		ArrayList<Integer> connectedGraphMaxStrokeNum = new ArrayList<>();
		// 連結グラフのストローク数最小のグラフのストローク総和長.
		ArrayList<Double> isolatedStrokeLengthsum = new ArrayList<>();
		// ストローク群ごとの集合(ストロークインデックス).
		ArrayList<ArrayList<Integer>> strokeIndexSet = new ArrayList<>();
		
		// 重要度の高いストロークから見ていく.
		for(int i=0; i<_orderedStrokeIndexArrayList.size(); i++){
			System.out.println("############");
			System.out.println("############");
			System.out.println("############");
			System.out.println("stroke set "+ strokeIndexSet);
			////////////////////////////////////////
			////////////////////////////////////////
			System.out.println("孤立しているストローク");
			for(int j=0; j<strokeIndexSet.size(); j++){
				if(i > 4000 && strokeIndexSet.get(j).size() < 1000){
					for(int k=0; k<strokeIndexSet.get(j).size(); k++){
						System.out.println(osmFatStrokeEvalVer._strokeArcString.get(strokeIndexSet.get(j).get(k)));
					}
				}
			}
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%");
//			if(i>900){
//				System.exit(0);
//			}
			////////////////////////////////////////
			////////////////////////////////////////
			// 1つ目はそれのストロークが新しいストローク群になる.
			if(i==0){
				strokeIndexSet.add(new ArrayList<>(Arrays.asList(_orderedStrokeIndexArrayList.get(i))));
			}else{
				
				// 取り出したストロークがどのストローク群と交差しているかを記憶する(strokeIndexSetのインデックスを記憶する).
				ArrayList<Integer> whichStrokeSetIntersects = new ArrayList<>();
				for(int j=0; j<strokeIndexSet.size(); j++){
					for(int k=0; k<strokeIndexSet.get(j).size(); k++){
						System.out.println("&&&&交差判定をする&&&&&&");
						System.out.println(osmFatStrokeEvalVer._strokeArcString.get(_orderedStrokeIndexArrayList.get(i)));
						System.out.println(osmFatStrokeEvalVer._strokeArcString.get(strokeIndexSet.get(j).get(k)));
	//					System.out.println(osmFatStrokeEvalVer._strokeArcPoint.get(_orderedStrokeIndexArrayList.get(i)));
	//					System.out.println(osmFatStrokeEvalVer._strokeArcPoint.get(strokeIndexSet.get(j).get(k)));
						if(
							//isIntersectTwoStroke(osmFatStrokeEvalVer._strokeArcPoint.get(_orderedStrokeIndexArrayList.get(i)), 
							//	osmFatStrokeEvalVer._strokeArcPoint.get(strokeIndexSet.get(j).get(k)))
								isIntersectTwoStrokeLine2D(osmFatStrokeEvalVer._strokeArc.get(_orderedStrokeIndexArrayList.get(i)), 
									osmFatStrokeEvalVer._strokeArc.get(strokeIndexSet.get(j).get(k)))
						){
							System.out.println("交差した");
							whichStrokeSetIntersects.add(j);
							break;
						}else{
//							if(i> 4410){
//							System.out.println("交差しない");
//							System.out.println(osmFatStrokeEvalVer._strokeArcString.get(_orderedStrokeIndexArrayList.get(i)));
//							System.out.println(osmFatStrokeEvalVer._strokeArcString.get(strokeIndexSet.get(j).get(k)));
//							}
						}
					}
				}
				
				if(i== 15){
					System.exit(0);
				}
//				System.out.println("whichStrokeSetIntersects"+whichStrokeSetIntersects);
				// どのストロークとも交差しないなら，新しいそれをストローク群とする.
				if(whichStrokeSetIntersects.size() == 0){
//					System.out.println("どのストローク群とも交差しない");
					strokeIndexSet.add(new ArrayList<>(Arrays.asList(_orderedStrokeIndexArrayList.get(i))));
				}
				// 1つのストローク群と交差しているならばそこに追加するだけ.
				else if(whichStrokeSetIntersects.size() == 1){
//					System.out.println("1つのストローク群とのみ交差");
//					System.out.println(""+strokeIndexSet.get(whichStrokeSetIntersects.get(0)));
					strokeIndexSet.get(whichStrokeSetIntersects.get(0)).add(_orderedStrokeIndexArrayList.get(i));
				}
				// 2つ以上のストローク群と交差するならそれらを1つのストローク群とする.
				else if(whichStrokeSetIntersects.size() > 1){
//					System.out.println("2つ以上のストローク群と交差する");
					// とりあえず自分を追加.
					strokeIndexSet.get(whichStrokeSetIntersects.get(0)).add(_orderedStrokeIndexArrayList.get(i));
					// 複数のストローク群を1つにまとめる.
					for(int j=1; j<whichStrokeSetIntersects.size(); j++){
						strokeIndexSet.get(whichStrokeSetIntersects.get(0)).addAll(strokeIndexSet.get(whichStrokeSetIntersects.get(j)));
					}
					// 使わなくなったストローク群の削除.
					for(int j=1; j<whichStrokeSetIntersects.size(); j++){
						strokeIndexSet.remove(whichStrokeSetIntersects.get(j)-(j-1));
					}
				}else{
					System.out.println("error");
					System.exit(0);
				}
			}	
				// 最大の連結ストロークネットワークを求める.
				int maxStrokeNum = 0;	// .
				for(int j=0; j<strokeIndexSet.size(); j++){
					if(strokeIndexSet.get(j).size() > maxStrokeNum){
						maxStrokeNum = strokeIndexSet.get(j).size();
					}
				}
				// 孤立するストローク集合を求める.
				// 連結グラフのストローク数最小のグラフのストローク総和長.
				double isolatedStrokeSumLength = 0;
				for(int j=0; j<strokeIndexSet.size(); j++){
					if(strokeIndexSet.get(j).size() < maxStrokeNum){
						for(int k=0; k<strokeIndexSet.get(j).size(); k++){
							isolatedStrokeSumLength += osmFatStrokeEvalVer._strokeLength.get(strokeIndexSet.get(j).get(k));
						}
					}
				}
				
//				// 孤立するストローク集合.
//				int isolateStrokeNum = 0;
//				// 連結グラフのストローク数最小のグラフのストローク総和長.
//				double isolatedStrokeSumLength = 0;
//				for(int j=0; j<strokeIndexSet.size(); j++){
//					if(strokeIndexSet.get(j).size() < maxStrokeNum){
//						for(int k=0; k<strokeIndexSet.get(j).size(); k++){
//							isolatedStrokeSumLength += osmFatStrokeEvalVer._strokeLength.get((strokeIndexSet.get(j).get(k)));
//							if(i> 4400){
//								System.out.println("%%%%%%%%%%%%%%"+osmFatStrokeEvalVer._strokeArcString.get(strokeIndexSet.get(j).get(k)));
//							}
//							isolateStrokeNum++;
//						}
//					}
//				}
				
				System.out.println("連結グラフの最大のストローク数  "+ maxStrokeNum);
				System.out.println("孤立しているストローク  "+(i+1-maxStrokeNum));
				System.out.println("孤立しているストローク長総和 "+isolatedStrokeSumLength);
				connectedGraphMaxStrokeNum.add(maxStrokeNum);
				isolatedStrokeLengthsum.add(isolatedStrokeSumLength);
//				if(i==550){
//					System.exit(0);
//				}
		}
		
		WriteDataToFile3<Integer, Double> writeDataToFile3 = new WriteDataToFile3<>(connectedGraphMaxStrokeNum, isolatedStrokeLengthsum);
		
	}
	
	// ストロークを1つ取り出す.
	// そのストロークが他のストローク群と交差するならそのストローク群に追加する.
	// 複数のストローク群と交差するならそれらを1つのストローク群と数る.
	/**
	 * ストロークネットワークのうちストローク数最大となる連結グラフのストローク数を出力する
	 * ストロークを1つ取り出す.
	 * そのストロークが他のストローク群と交差するならそのストローク群に追加する.
	 * 複数のストローク群と交差するならそれらを1つのストローク群と数る.
	 */
	/**
	 * 施設データを1つ取り出してその施設が選択されたストロークのいくつだけ到達可能か
	 * ストロークを1つ取り出してそのストロークが選択されたストロークの靴だけ到達可能か
	 */
	public void evalIntersection2(){
		// 連結グラフのストローク数最大のグラフのストローク数.
		ArrayList<Integer> connectedGraphMaxStrokeNum = new ArrayList<>();
		// 連結グラフのストローク数最小のグラフのストローク総和長.
		ArrayList<Double> isolatedStrokeLengthsum = new ArrayList<>();
		// ストローク群ごとの集合(ストロークインデックス).
		ArrayList<ArrayList<Integer>> strokeIndexSet = new ArrayList<>();
		// 各状態におけるストローク群集合.
//		ArrayList<ArrayList<ArrayList<Integer>>> strokeIndexSetEachCondition = new ArrayList<>();
		
		
		new WriteDataToFile3<String, String>("ストローク1本あたりの到達可能な道路長の割合((Σ(あるストロークから到達可能なストロークの長さ)/(選択されたストローク長))/(選択されたストローク数)*100.)", 
				" ストローク1本あたりの到達可能なストローク長の割合(Σ(あるストロークから到達可能なストロークの長さ)/(選択されたストローク長))/(選択されたストローク数)*100.");
		
		// 全ストローク長総和(画面内のみ).
//		double allStrokeLengthInWindow = 0;
//		for(int i=0; i<osmFatStrokeEvalVer._subStrokeLength.size(); i++){
//			allStrokeLengthInWindow += osmFatStrokeEvalVer._subStrokeLength.get(i);
//		}
//		System.out.println("allStrokeLengthInWindow"+allStrokeLengthInWindow);
		
		// 重要度の高いストロークから見ていく.
		for(int i=0; i<_orderedStrokeIndexArrayList.size(); i++){
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////ストロークの重要度が高いものがら順に選択し，ストロークのコネクティビティーがどうなっているか調べる//////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// 1つ目はそれのストロークが新しいストローク群になる.
			if(i==0){
				strokeIndexSet.add(new ArrayList<>(Arrays.asList(_orderedStrokeIndexArrayList.get(i))));
			}else{
				// 取り出したストロークがどのストローク群と交差しているかを記憶する(strokeIndexSetのインデックスを記憶する).
				ArrayList<Integer> whichStrokeSetIntersects = new ArrayList<>();
				for(int j=0; j<strokeIndexSet.size(); j++){
					for(int k=0; k<strokeIndexSet.get(j).size(); k++){
						if(
								isIntersectTwoStrokeLine2D(osmFatStrokeEvalVer._strokeArc.get(_orderedStrokeIndexArrayList.get(i)), 
									osmFatStrokeEvalVer._strokeArc.get(strokeIndexSet.get(j).get(k)))
						){
							whichStrokeSetIntersects.add(j);
							break;
						}else{
						}
					}
				}
				// どのストロークとも交差しないなら，新しいそれをストローク群とする.
				if(whichStrokeSetIntersects.size() == 0){
					strokeIndexSet.add(new ArrayList<>(Arrays.asList(_orderedStrokeIndexArrayList.get(i))));
				}
				// 1つのストローク群と交差しているならばそこに追加するだけ.
				else if(whichStrokeSetIntersects.size() == 1){
					strokeIndexSet.get(whichStrokeSetIntersects.get(0)).add(_orderedStrokeIndexArrayList.get(i));
				}
				// 2つ以上のストローク群と交差するならそれらを1つのストローク群とする.
				else if(whichStrokeSetIntersects.size() > 1){
					// とりあえず自分を追加.
					strokeIndexSet.get(whichStrokeSetIntersects.get(0)).add(_orderedStrokeIndexArrayList.get(i));
					// 複数のストローク群を1つにまとめる.
					for(int j=1; j<whichStrokeSetIntersects.size(); j++){
						strokeIndexSet.get(whichStrokeSetIntersects.get(0)).addAll(strokeIndexSet.get(whichStrokeSetIntersects.get(j)));
					}
					// 使わなくなったストローク群の削除.
					for(int j=1; j<whichStrokeSetIntersects.size(); j++){
						strokeIndexSet.remove(whichStrokeSetIntersects.get(j)-(j-1));
					}
				}else{
					System.out.println("error");
					System.exit(0);
				}
			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////ストローク1本あたりの到達可能なストローク長の平均を求める.///////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// 選択されているストローク長の総和.
			double selectedStrokeLengthSum = 0;
			for(int j=0; j<=i; j++){
				selectedStrokeLengthSum += osmFatStrokeEvalVer._subStrokeLength.get(_orderedStrokeIndexArrayList.get(j));
			}
//			System.out.println("selectedStrokeLengthSum"+selectedStrokeLengthSum);
			// 各ストローク群のストローク長総和(.get(j): j番目のストローク群)
			ArrayList<Double> eachStrokeSetLengthSum = new ArrayList<>();
			// 各ストローク群のストローク長総和を求める.
			for(int j=0; j<strokeIndexSet.size(); j++){
				eachStrokeSetLengthSum.add(0.0);
				for(int k=0; k<strokeIndexSet.get(j).size(); k++){
					eachStrokeSetLengthSum.set(j, eachStrokeSetLengthSum.get(j) + osmFatStrokeEvalVer._subStrokeLength.get(strokeIndexSet.get(j).get(k)));
				}
			}
//			System.out.println("eachStrokeSetLengthSum  "+eachStrokeSetLengthSum);
			// 各ストロークがどのストローク群に属しているか.(.get(j): j番目に重要度の高いストローク)
			ArrayList<Integer> strokeSetBelong = new ArrayList<>();
			for(int j=0; j<=i; j++){
				boolean breakFlg = false;
				for(int k=0; k<strokeIndexSet.size(); k++){
					for(int l=0; l<strokeIndexSet.get(k).size(); l++){
						if(_orderedStrokeIndexArrayList.get(j).intValue() == strokeIndexSet.get(k).get(l)){
							strokeSetBelong.add(k);
							breakFlg = true;
							break;
						}
					}
					if(breakFlg) break;
					if(k==strokeIndexSet.size()-1){
						System.out.println("おかしい");
						System.exit(0);
					}
				}
			}
			// ストローク1本あたりの到達可能なストローク長の平均を求める.
			// (Σ(あるストロークから到達可能なストロークの長さ)/(選択されたストローク長))/(選択されたストローク数)*100.
			double averageReachableStrokeLength = 0;
			for(int j=0; j<=i; j++){
				averageReachableStrokeLength += eachStrokeSetLengthSum.get(strokeSetBelong.get(j));
			}
			averageReachableStrokeLength = averageReachableStrokeLength/selectedStrokeLengthSum/(i+1)*100;
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////施設1か所あたりの到達可能なストローク長の割合を求める////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			// 施設1か所あたりの到達可能なストローク長の平均を求める.
			// (Σ(ある施設から到達可能なストロークの長さ)/(選択されたストローク長))/(選択されたストローク数)*100.
			
			// すべてのお店を取り出す(画面内のみ).
			// それぞれの施設がどの周回道路に属するか.
			// 周回道路からどのストロークと接するか.
			// ストロークから接続する道路長を求める.
			
			// すべてのお店を取り出す(画面内のみ).
			OsmDataGeom osmDataGeom = new OsmDataGeom();
			osmDataGeom.startConnection();
			osmDataGeom.insertFacilityFromCategory(_inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[0],
					_inputPanel._CategoryOsmWindow.getCategory().get(0).split(":")[1], _upperLeftLngLat, _lowerRightLngLat);
			osmDataGeom.endConnection();
			
			OsmLR osmLR = new OsmLR();
			osmLR.startConnection();
			osmFatStrokeEvalVer.startConnection();
			
			// それぞれの施設が接するストロークがどのストローク群にあるか.
			ArrayList<HashSet<Integer>> strokeSetBelongHashArrayList = new ArrayList<>();
			for(int j=0; j<osmDataGeom._facilityId.size(); j++){
				// それぞれの施設がどの周回道路に属するか.
				osmLR.getLoopRoadFromPoint(osmDataGeom._facilityLocation.get(j));
				// 周回道路からどのストロークと接するか.
				osmFatStrokeEvalVer.getTouchStrokeFromLooproad(osmLR._oneLooproadString);
				// 指定のストロークがどのストローク群に属するか.
				HashSet<Integer> oneShopstrokeSetBelongHash = new HashSet<>();// 1つの施設データから到達可能なストロークがどのストローク群にあるか.
				for(int sIdx=0; sIdx<osmFatStrokeEvalVer._intersectStrokeId.size(); sIdx++){
					boolean breakFlg = false;
					for(int k=0; k<strokeIndexSet.size(); k++){
						for(int l=0; l<strokeIndexSet.get(k).size(); l++){
//							System.out.println(osmFatStrokeEvalVer._intersectStrokeId.get(sIdx));
//							System.out.println(osmFatStrokeEvalVer._strokeIdToIndexHash.get(osmFatStrokeEvalVer._intersectStrokeId.get(sIdx)));
//							System.out.println();
//							System.out.println();
//							System.out.println();
//							System.out.println();
							if(osmFatStrokeEvalVer._strokeIdToIndexHash.containsKey(osmFatStrokeEvalVer._intersectStrokeId.get(sIdx)) && 
									osmFatStrokeEvalVer._strokeIdToIndexHash.get(osmFatStrokeEvalVer._intersectStrokeId.get(sIdx)).intValue() == strokeIndexSet.get(k).get(l)){
								oneShopstrokeSetBelongHash.add(k);
								breakFlg = true;
								break;
							}
						}
						if(breakFlg) break;
						if(k==strokeIndexSet.size()-1){	// 指定したストロークがどのストローク群にもなかった.
						}
					}
				}
				strokeSetBelongHashArrayList.add(oneShopstrokeSetBelongHash);
//				System.out.println("oneshop stroke set belong "+oneShopstrokeSetBelongHash);
			}
			System.out.println("各ストローク群のストローク長総和"+eachStrokeSetLengthSum);
			// ストロークから接続する道路長を求める.
			
			// 施設1か所あたりの到達可能なストローク長の平均を求める.
			// (Σ(ある施設から到達可能なストロークの長さ)/(選択されたストローク長))/(全施設数)*100.
			double averageShopReachableStrokeLength = 0;
			for(int j=0; j<osmDataGeom._facilityId.size(); j++){
				for(Integer item: strokeSetBelongHashArrayList.get(j)){
					averageShopReachableStrokeLength += eachStrokeSetLengthSum.get(item);
				}
			}
			averageShopReachableStrokeLength = averageShopReachableStrokeLength/selectedStrokeLengthSum/osmDataGeom._facilityId.size()*100;
			new WriteDataToFile3<Double, Double>(averageReachableStrokeLength, averageShopReachableStrokeLength);
			

			
			osmFatStrokeEvalVer.endConnection();
			osmLR.endConnection();
			
		}
		
		WriteDataToFile3<Integer, Double> writeDataToFile3 = new WriteDataToFile3<>(connectedGraphMaxStrokeNum, isolatedStrokeLengthsum);
		
	}
	
	/**
	 * 2つのストロークが交差するか
	 * @param s1 _strokeArcPointの形
	 * @param s2 _strokeArcPointの形
	 * @return
	 */
	public boolean isIntersectTwoStroke(ArrayList<Point2D> s1, ArrayList<Point2D> s2){
		
		// 端点が同じだったら消す
		if(s1.get(0).getX() == s1.get(s1.size()-1).getX() && s1.get(0).getY() == s1.get(s1.size()-1).getY()){
			s1.remove(s1.size()-1);
		}
		if(s2.get(0).getX() == s2.get(s2.size()-1).getX() && s2.get(0).getY() == s2.get(s2.size()-1).getY()){
			s2.remove(s2.size()-1);
		}
		
		// 端点で交差するか確かめる.
		if(
			(s1.get(0).getX() == s2.get(0).getX() && s1.get(0).getY() == s2.get(0).getY())||
			(s1.get(0).getX() == s2.get(s2.size()-1).getX() && s1.get(0).getY() == s2.get(s2.size()-1).getY())||
			(s1.get(s1.size()-1).getX() == s2.get(0).getX() && s1.get(s1.size()-1).getY() == s2.get(0).getY())||
			(s1.get(s1.size()-1).getX() == s2.get(s2.size()-1).getX() && s1.get(s1.size()-1).getY() == s2.get(s2.size()-1).getY())
		){
			return true;
		}
		
		// 中で交差するか確かめる.
		// 2つのストロークをそれぞれ偶数番目だけのセグメントを取り出して1つの集合とする
		ArrayList<Line2D> sList = new ArrayList<>();
		for(int i=0; i<s1.size()-1; i=i+2){
			sList.add(new Line2D.Double(s1.get(i), s1.get(i+1)));
		}
		for(int i=0; i<s2.size()-1; i=i+2){
			sList.add(new Line2D.Double(s2.get(i), s2.get(i+1)));
		}
		return isIntersectLines(sList);
	}
	/**
	 * 2つのストロークの交差判定遅い版? こっちの方が早い
	 * @param s1
	 * @param s2
	 * @return
	 */
	public boolean isIntersectTwoStrokeLine2D(ArrayList<Line2D> s1, ArrayList<Line2D> s2){
		for(int i=0; i<s1.size(); i++){
			for(int j=0; j<s2.size(); j++){
				if(s1.get(i).intersectsLine(s2.get(j))){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 複数の線分が交差しているか(端点でしか交差しないことを想定)
	 * @return
	 */
	public boolean isIntersectLines(ArrayList<Line2D> segmentList){
		
		HashSet<String> hashString = new HashSet<>();
		for(int i=0; i<segmentList.size(); i++){
			hashString.add(""+segmentList.get(i).getX1()+"_"+segmentList.get(i).getY1());
			hashString.add(""+segmentList.get(i).getX2()+"_"+segmentList.get(i).getY2());
		}
		if(hashString.size() == segmentList.size()*2){
//			System.out.println("交差しない");
			return false;
		}
		return true;// 交差する.
	}
	
	
	
	
	
	/**
	 * ウインドウの大きさを動的に変更できる地図ウインドウの作成
	 */
	public void createResizableMapWindow(int soaType){
		if(soaType == 1){
		}
		else if(soaType == 2){
		}
		else if(soaType == 3){
			SOA3(0, false);
		}
	}
	
	/**
	 * ピンを表示する
	 * @param aPinPoint
	 */
	public void pinDraw(Point aPinPoint){
		HandleDbSpecificStroke handleDbSpecificStroke = new HandleDbSpecificStroke();
		handleDbSpecificStroke.startConnection();
		handleDbSpecificStroke.getSpecificLR(_convert.convertXyCoordinateToLngLat(aPinPoint));
		handleDbSpecificStroke.getContactStroke();
		handleDbSpecificStroke.reorderedContactStroke();
		handleDbSpecificStroke.endConnection();
		_pinPoint.add(new Point(aPinPoint));
		_concatStrokeId.add(handleDbSpecificStroke._strokeId);
		_concatStroke.add(handleDbSpecificStroke._strokeGeomArc);
		_drawPinFlg = true;
		repaint();
	}
	
	/**
	 * ピンの削除をする
	 */
	public void deletePin(Point aPinPoint){
		for(int i=0; i<_pinPoint.size(); i++){
			if(_pinPoint.get(i).x-5 < aPinPoint.x &&
					aPinPoint.x < _pinPoint.get(i).x + 33-5 &&
					_pinPoint.get(i).y - 55 < aPinPoint.y &&
					aPinPoint.y < _pinPoint.get(i).y){
				_pinPoint.remove(i);
				_concatStrokeId.remove(i);
				_concatStroke.remove(i);
			}
		}
		repaint();
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////EMMA関係ここから///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 中心にfocusとglueの描画
	 */
	public void drawFocusGlue(){
		drawFocusGlue(_lngLat, 17, _scale, 200, 400);
	}
	public void drawFocusGlue(Point2D aCenterLngLat, int aFoucsZoomLevel, int aContextZoomLevel, int glueInnerRadius, int glueOuterRadius){
		if(_fgImageFlg == true){// 表示していたら消す.
			_fgImageFlg = false;
			repaint();
			return;
		}
		_fgImageFlg = true;
		
		try{
			URL urlGlue = new URL("http://133.68.13.112:8080/EmmaGlueMuraseOriginal/MainServlet?" +
	    		"type="+"DrawGlue_v2"+
	    		"&centerLngLat="+aCenterLngLat.getX()+","+aCenterLngLat.getY()+
	    		"&focus_zoom_level="+aFoucsZoomLevel+
	    		"&context_zoom_level="+aContextZoomLevel+
	    		"&glue_inner_radius="+glueInnerRadius+
	    		"&glue_outer_radius="+glueOuterRadius+
	    		"&roadType=" +"car");
			URL urlFoucs = new URL("http://rain2.elcom.nitech.ac.jp/OsmStaticMap/staticmap.php?" +
    		"center="+aCenterLngLat.getY()+","+aCenterLngLat.getX()+"" +
    		"&zoom=" +aFoucsZoomLevel+
    		"&size="+(glueInnerRadius*2)+"x"+(glueInnerRadius*2)+"" +
    		"&maptype=mapnik_local");
			System.out.println(urlFoucs);
			_focusImage = ImageIO.read(urlFoucs);
			_glueImage = ImageIO.read(urlGlue);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		repaint();
		
	}
	
	public int focusScale = 17;
	public int glueInnerRadius = 200;
	public int glueOuterRadius = 400;
	public Point focusGlueWindowSize = new Point(glueOuterRadius, glueOuterRadius);
	/***
	 * focus,glue,contextのリンクを描画
	 */
	public void drawFGC_link(){
		drawFGC_link(_lngLat, focusScale, _scale, glueInnerRadius, glueOuterRadius);
	}
	public void drawFGC_link(Point2D aCenterLngLat, int aFoucsZoomLevel, int aContextZoomLevel, int glueInnerRadius, int glueOuterRadius){
		if(_fgcRoadFlg == true){// 表示していたら消す.
			_fgcRoadFlg = false;
			repaint();
			return;
		}
		_fgcRoadFlg = true;
		
		//focus用の緯度経度xy変換
		GetLngLatOsm getLngLatOsmFocus = new GetLngLatOsm(aCenterLngLat, aFoucsZoomLevel, WINDOW_SIZE);
		ConvertLngLatXyCoordinate convertFocus = new ConvertLngLatXyCoordinate((Point2D.Double)getLngLatOsmFocus._upperLeftLngLat,
				(Point2D.Double)getLngLatOsmFocus._lowerRightLngLat, WINDOW_SIZE);
		//context用の緯度経度xy変換
		GetLngLatOsm getLngLatOsmContext = new GetLngLatOsm(aCenterLngLat, aContextZoomLevel, WINDOW_SIZE);
		ConvertLngLatXyCoordinate convertContext = new ConvertLngLatXyCoordinate((Point2D.Double)getLngLatOsmContext._upperLeftLngLat,
				(Point2D.Double)getLngLatOsmContext._lowerRightLngLat, WINDOW_SIZE);
		double glueInnerRadiusMeter = glueInnerRadius*convertFocus.meterPerPixel.getX();
		double glueOuterRadiusMeter = glueOuterRadius*convertContext.meterPerPixel.getX();
		
		
		// contextでのメルカトル座標系xy変換.
		ConvertMercatorXyCoordinate convertMercator = new ConvertMercatorXyCoordinate(
						LngLatMercatorUtility.ConvertLngLatToMercator((Point2D.Double)getLngLatOsmContext._upperLeftLngLat), 
						LngLatMercatorUtility.ConvertLngLatToMercator((Point2D.Double)getLngLatOsmContext._lowerRightLngLat), WINDOW_SIZE);
		// glueのxy変換.
		ConvertElasticPointGlue convertXyGlue = new ConvertElasticPointGlue(glueInnerRadius, glueOuterRadius, glueInnerRadiusMeter, glueOuterRadiusMeter
				, focusScale, _scale, _lngLat, convertFocus, convertContext, convertMercator);
		
		// focusのみ.
		FGC_road fgc_FocusRoad = new FGC_road();
		fgc_FocusRoad.startConnection();
		fgc_FocusRoad.getFoucsRoad(_lngLat, glueInnerRadiusMeter, convertFocus);
		fgc_FocusRoad.endConnection();
		// contextのみ.
		FGC_road fgc_ContextRoad = new FGC_road();
		fgc_ContextRoad.startConnection();
		fgc_ContextRoad.getContextRoad(_lngLat, glueOuterRadiusMeter, _upperLeftLngLat, _lowerRightLngLat, convertContext);
		fgc_ContextRoad.endConnection();
		// glue道路取得.
		FGC_road fgcGlueRoad = new FGC_road();
		fgcGlueRoad.startConnection();
		fgcGlueRoad.getGlueRoad(_lngLat, focusScale, _scale, glueInnerRadius, glueOuterRadius, glueOuterRadiusMeter, convertXyGlue, convertContext);
		fgcGlueRoad.endConnection();
		// focus,glue,context.
		_fgc_road = new FGC_road();
		_fgc_road.startConnection();
		_fgc_road.creatTmpRouteTable();
		_fgc_road.insertTmpTable(fgc_FocusRoad._linkId, fgc_FocusRoad._sourceId, fgc_FocusRoad._targetId, fgc_FocusRoad._clazz, fgc_FocusRoad._length, fgc_FocusRoad._cost,fgc_FocusRoad._link);
		_fgc_road.insertTmpTable(fgc_ContextRoad._linkId, fgc_ContextRoad._sourceId, fgc_ContextRoad._targetId, fgc_ContextRoad._clazz, fgc_ContextRoad._length, fgc_ContextRoad._cost,fgc_ContextRoad._link);
		_fgc_road.insertTmpTable(fgcGlueRoad._linkId, fgcGlueRoad._sourceId, fgcGlueRoad._targetId, fgcGlueRoad._clazz, fgcGlueRoad._length, fgcGlueRoad._cost,fgcGlueRoad._link);
		_fgc_road.insertFgcRoadData();
		_fgc_road._linkPoint = new ArrayList<>();	// xy座標の道路データ(linkのインデックスが使えない).
		_fgc_road._linkPoint.addAll(fgc_FocusRoad._linkPoint);
		_fgc_road._linkPoint.addAll(fgc_ContextRoad._linkPoint);
		_fgc_road._linkPoint.addAll(fgcGlueRoad._linkPoint);
		_fgc_road._idXyHashMap = new HashMap<>();	// ノードIDとノードの位置(ｘｙ)を紐付したデータ.
		_fgc_road._idXyHashMap.putAll(fgc_FocusRoad._idXyHashMap);
		_fgc_road._idXyHashMap.putAll(fgc_ContextRoad._idXyHashMap);
		_fgc_road._idXyHashMap.putAll(fgcGlueRoad._idXyHashMap);
		
		
		_fgcRoadFlg = true;
		repaint();

	}
	
	
	/**
	 * fgcのルーティング関係 mapPanelEventから呼び出される
	 */
	public void routing(int aSourceId, int aTargetId){
	ArrayList<ArrayList<Integer>> routingResult = _fgc_road.execRouting(aSourceId, aTargetId, _upperLeftLngLat, _lowerRightLngLat);
	_routingResult = new ArrayList<>();
	double cost = 0;
	for(int i=0; i<routingResult.size(); i++){
		System.out.println("node ID:"+routingResult.get(i).get(0));
		System.out.println("ノードの座標"+_fgc_road._idXyHashMap.get(routingResult.get(i).get(0)));
		System.out.println("リンクの座標"+_fgc_road._idLinkHash.get(routingResult.get(i).get(1)));
		System.out.println("コスト"+_fgc_road._routingCost.get(i));
		//_routingResult.add(_idLinkHash.get(Integer.valueOf(routingResult.get(i).get(1))));
		_routingResult.add(_fgc_road._idXyHashMap.get(Integer.valueOf(routingResult.get(i).get(0))));
		cost += _fgc_road._routingCost.get(i);
	}
	System.out.println("総コスト"+cost);
	_routingFlg = true;
	repaint();
}
/**
 * コンテキストのみのルーティング
 */
	public void routing_context(int aSourceId, int aTargetId){
		OsmRoadDataGeom osmRoadDataGeom  = new OsmRoadDataGeom();
		osmRoadDataGeom.startConnection();
		ArrayList<ArrayList<Integer>> routingResult = osmRoadDataGeom.execRouting(aSourceId, aTargetId, _upperLeftLngLat, _lowerRightLngLat);
		_routingResult = new ArrayList<>();
		for(int i=0; i<routingResult.size(); i++){
			System.out.println("node ID : "+routingResult.get(i).get(0));
			System.out.println("ノードの座標 "+_osmRoadDataGeom._idLngLatHash.get(routingResult.get(i).get(0)));
			System.out.println("リンクの座標"+_osmRoadDataGeom._idLinkHash.get(routingResult.get(i).get(1)));
			_routingResult.add(_osmRoadDataGeom._idXyHash.get(Integer.valueOf(routingResult.get(i).get(0))));
		}
		osmRoadDataGeom.endConnection();
		_routingFlg = true;
		repaint();
	}
	
	
	
	////////////////////////////////////
	//////////ここから上に新規メソッド追加//////////////////////////
	////////////////////////////////////
	
	
	
	/**
	 * 閾値の更新
	 */
	public void updateThreshold(int threshold){
		// ストロークを指定した数だけ描画.
		_soaThreshold = _orderedStrokeIndexArrayList.size() < threshold ? _orderedStrokeIndexArrayList.size()-1 : threshold;
		repaint();
	}
	
	// setter関数.
	public void setInputPanel(InputPanel aInputPanel){
		_inputPanel = aInputPanel;
	}
}
