package mySrc.panel.mapPanel;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.swing.JComponent;

import mySrc.coordinate.ConvertLngLatXyCoordinate;
import mySrc.db.getData.OsmRoadDataGeom;

/**
 * 地図のイベントに関するクラス
 * @author murase
 *
 */
public class MapPanelEvent implements MouseListener, KeyListener{
	
	public MapPanel _mapPanel;
	
	// 経路探索用.
	public int _sourceId = -11;
	public int _targetId = -11;
	
	public MapPanelEvent(MapPanel aMapPanel) {
		_mapPanel = aMapPanel;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() >= 2 && MouseEvent.BUTTON1 == e.getButton()) {	// ダブルクリックしたときはその点を中心点にする.
			System.out.println("getpoint"+e.getPoint());
			Point2D.Double lnglat = _mapPanel._convert.convertXyCoordinateToLngLat(e.getPoint());
			_mapPanel._lngLat = lnglat;
//			_mapPanel._unsetFlg();
			_mapPanel.makeMap();
		} else if (e.getClickCount() == 1 && MouseEvent.BUTTON1 == e.getButton()) {	// クリックしたときはコンソールにデータを表示.
			System.out.println("$$$$$click$$$$$$$$$");
			System.out.println("getpoint"+e.getPoint());
			/////////////////////////////
			/////////施設データ表示//////////
			/////////////////////////////
			int foundShopNum=0;			// クリックした上にあるノードの数.
			ArrayList<Point> _pointDataArrayList = _mapPanel._convert.convertLngLatToXyCoordinate(_mapPanel._osmDataGeom._facilityLocation);	// 施設データのxy座標.
			//ArrayList<String> shopNameArrayList = new ArrayList<String>();	// クリックした上にあるお店の名前.
			ArrayList<Integer> foundIndex = new ArrayList<>();	// 見つかった施設のインデックス.
			for (int i=0; i < _pointDataArrayList.size(); i++) {	// 建物の数だけループ.
				if (e.getPoint().x > _pointDataArrayList.get(i).x - 5 &&
				e.getPoint().x < _pointDataArrayList.get(i).x + 5 &&
				e.getPoint().y > _pointDataArrayList.get(i).y - 5 &&
				e.getPoint().y < _pointDataArrayList.get(i).y + 5) {	// クリックした点が建物のマーカー上であるか.
					foundShopNum++;
					//shopNameArrayList.add(_mapPanel._pointDataNameArrayList.get(i));
					foundIndex.add(i);
				}
			}
			if(foundIndex.size() > 0){
				System.out.println("-------found Shop Num "+foundShopNum+"--------------");
				//for(String string: shopNameArrayList){
				for(int i=0; i<foundIndex.size(); i++){
					System.out.println(_mapPanel._osmDataGeom._facilityLocation.get(foundIndex.get(i)));
					if(_mapPanel._osmDataGeom._facilityId.size() == _pointDataArrayList.size()){	// 施設IDもあるなら表示.
						System.out.println("id:"+_mapPanel._osmDataGeom._facilityId.get(foundIndex.get(i)));
					}
					if(_mapPanel._osmDataGeom._facilityType.size() == _pointDataArrayList.size()){	// 施設タイプもあるなら表示.
						System.out.println("type "+_mapPanel._osmDataGeom._facilityType.get(foundIndex.get(i)));
					}
				}
				System.out.println("-----------------------");
			}
			//////////////////////////////
			//////////道路データ表示/////////
			//////////////////////////////
			if(_mapPanel._roadDataFlg){
				ArrayList<Line2D> _linkDataXyArrayList = _mapPanel._convert.convertLngLatToXyCoordinateLine2D(_mapPanel._osmRoadDataGeom._link);
				for(int i=0; i<_linkDataXyArrayList.size(); i++){
	//				System.out.println("p1: "+_linkDataXyArrayList.get(i).getP1());
	//				System.out.println("p2: "+_linkDataXyArrayList.get(i).getP2());
	//				System.out.println(""+_linkDataXyArrayList.get(i).ptLineDist(e.getPoint()));
					if(_linkDataXyArrayList.get(i).ptSegDist(e.getPoint()) < 3){
						System.out.println("\n----foundLink-----");
						System.out.println("linkId:"+_mapPanel._osmRoadDataGeom._linkId.get(i));
						System.out.println("linkLength:" + _mapPanel._osmRoadDataGeom._length.get(i));
						System.out.println("source Id:"+ _mapPanel._osmRoadDataGeom._sourceId.get(i));
						System.out.println("target Id:" + _mapPanel._osmRoadDataGeom._targetId.get(i));
						System.out.println("clazz:" + _mapPanel._osmRoadDataGeom._clazz.get(i));
						System.out.println("wkt"  );
						System.out.println("--------------------");
					}
				}
			}
			//////////////////////////////////////
			//////////////経路探索////////////////////////
			//////////////////////////////////////
		}else if (e.getClickCount() == 1 && MouseEvent.BUTTON3 == e.getButton()) {	// 右クリックは，経路探索
			
			int sourceId = -1;
			int targetId = -1;
			// FGCの時の経路探索.
			if(_mapPanel._fgcRoadFlg){
				for(Entry<Integer, Point2D> entry : _mapPanel._fgc_road._idXyHashMap.entrySet()){
					if(e.getPoint().distance(entry.getValue()) < 5){
						System.out.println("found");
						System.out.println("NodeId:"+entry.getKey());
						System.out.println("xy:"+entry.getValue());
						sourceId = entry.getKey();
						targetId = entry.getKey();
					}
				}
			// コンテキストのみの経路探索.
			}else if(_mapPanel._roadDataFlg){
				OsmRoadDataGeom osmRoadDataGeom = new OsmRoadDataGeom();
				osmRoadDataGeom.startConnection();
				osmRoadDataGeom.insertOsmRoadData(
						_mapPanel._convert.convertXyCoordinateToLngLat(new Point((int)e.getPoint().getX()-3, (int)e.getPoint().getY()-3)),
						_mapPanel._convert.convertXyCoordinateToLngLat(new Point((int)e.getPoint().getX()+3, (int)e.getPoint().getY()+3)));
				sourceId = osmRoadDataGeom._sourceId.size() > 0 ? osmRoadDataGeom._sourceId.get(0) : -1;
				targetId = osmRoadDataGeom._targetId.size() > 0 ? osmRoadDataGeom._targetId.get(0) : -1;
				osmRoadDataGeom.endConnection();
			}
			
			// 右クリックのみはその点を始点ノードに設定.
			if (sourceId != -1 &&(e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == 0 && MouseEvent.BUTTON3 == e.getButton()) {	// 始点ノードの設定.
				_sourceId = sourceId;
				System.out.println("set source Id : "+_sourceId);
			}
			// ctrl+右クリックは終点ノードに設定しダイクストラ法.
			else if (targetId != -1 && (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0  && MouseEvent.BUTTON3 == e.getButton()) {	// 終点ノードの設定をし最短経路の表示.
				System.out.println("set target id : "+targetId);
				_targetId = targetId;
				// FGCの時の経路探索.
				if(_mapPanel._fgcRoadFlg){
					_mapPanel.routing(_sourceId, _targetId, true);
				// コンテキストのみの経路探索.
				}else if(_mapPanel._roadDataFlg){
					_mapPanel.routing_context(_sourceId, _targetId, true);
				}
			}else{
				System.out.println("not found");
			}
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
//		System.out.println(e);
		// マウスの位置を取得.
		JComponent component = (JComponent)e.getSource();
		// キーを押したときのマウスの位置.
		Point mousePoint = component.getMousePosition();//new Point();// = MouseInfo.getPointerInfo().getLocation();
		if(mousePoint == null){
			return;
		}
		int key = e.getKeyCode();
		int mod = e.getModifiersEx();
		if(key == KeyEvent.VK_Q){	// ピンの表示，非表示(指定地点の重要度を高くする).
			System.out.println("press Q");
			if((mod & InputEvent.SHIFT_DOWN_MASK) == 0){	// shiftを押していないときはピンの追加.
					_mapPanel.pinDraw(mousePoint);
			}else{	// shiftを押しているときはピンの削除.
					_mapPanel.deletePin(mousePoint);
			}
		}
		
	}

	@Override
	// このコンポーネントにマウスが入ってきたとき.
	public void mouseEntered (MouseEvent e) {
		//System.out.println("entered mouse");
		_mapPanel.requestFocusInWindow();// このコンポーネントにフォーカスを充てる.
	}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}

}
