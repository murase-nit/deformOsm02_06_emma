package mySrc.db.emma;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.postgis.PGgeometry;


import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;
import mySrc.db.getData.OsmRoadDataGeom;
import mySrc.db.getData.OsmStrokeDataGeom;
import mySrc.panel.mapPanel.MapPanel;
import mySrc.coordinate.*;
import mySrc.elastic.*;

/**
 * focus glue contextのための道路データ
 * @author murase
 *
 */
public class FGC_road  extends HandleDbTemplateSuper {
	private static final String DBNAME = "osm_road_db";	// Database Name
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "usadasql";		// password for DB.
	private static final String URL = "rain2.elcom.nitech.ac.jp";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	/** リンクID */
	public ArrayList<Integer> _linkId;
	/** (sourcePoint, targetPoint)の組 */
	public ArrayList<Line2D> _link;
	public ArrayList<Integer> _sourceId;
	public ArrayList<Integer> _targetId;
	/** sourceノードの緯度経度 */
	public ArrayList<Point2D> _sourcePoint;
	/** targetノードの緯度経度 */
	public ArrayList<Point2D> _targetPoint;
	/** km */
	public ArrayList<Double> _length;
	/** cost */
	public ArrayList<Double> _cost;
	/** 道路のクラス */
	public ArrayList<Integer> _clazz;
	/** 道路の形状を表す */
	public ArrayList<ArrayList<Line2D>> _arc;
	/** ノードidと緯度経度の組(ハッシュ) */
	public HashMap<Integer, Point2D> _idLngLatHash;
	/** idとリンクのジオメトリの組(ハッシュ) */
	public HashMap<Integer, Line2D> _idLinkHash;
	
	
	/** ノードidとノードの位置(ｘｙ座標)の関係 */
	public HashMap<Integer, Point2D> _idXyHashMap;
	/** xy座標のリンク */
	public ArrayList<Line2D> _linkPoint = new ArrayList<>();
	
	public FGC_road(){
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	/**
	 * FGCのルート探索のための一時的なテーブルの作成
	 */
	public void creatTmpRouteTable(){
		try{
			String statement = "";
			
			statement = " create temp table tb_temp(id integer, source integer, target integer, clazz integer, km double precision, cost double precision, " +
					"x1 double precision, y1 double precision, x2 double precision, y2 double precision, geom_way geometry); ";
			insertInto(statement);
			System.out.println("一時テーブル作成　"+statement);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 矩形範囲のデータを取り出す
	 */
	public void insertFgcRoadData(){
		_linkId = new ArrayList<>();
		_link = new ArrayList<>();
		_sourceId = new ArrayList<>();
		_sourcePoint = new ArrayList<>();
		_targetId = new ArrayList<>();
		_targetPoint = new ArrayList<>();
		_length = new ArrayList<>();
		_cost = new ArrayList<>();
		_clazz = new ArrayList<>();
		_arc = new ArrayList<>();
		_idLngLatHash = new HashMap<>();
		_idLinkHash = new HashMap<>();
		
		try{
			String statement;
			// SRID=4326.
			statement = "select " +
					" id, clazz, source, target, km, cost, x1, y1, x2, y2, geom_way " +
					" from tb_temp " +
					"";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
//				System.out.println(rs.getInt("id"));
				_linkId.add(rs.getInt("id"));
				_sourceId.add(rs.getInt("source"));
				_sourcePoint.add(new Point2D.Double(rs.getDouble("x1"), rs.getDouble("y1")));
				_targetId.add(rs.getInt("target"));
				_targetPoint.add(new Point2D.Double(rs.getDouble("x2"), rs.getDouble("y2")));
				_link.add((Line2D)new Line2D.Double(rs.getDouble("x1"), rs.getDouble("y1"), rs.getDouble("x2"), rs.getDouble("y2")));
				_length.add(rs.getDouble("km"));
				_cost.add(rs.getDouble("cost"));
				_clazz.add(rs.getInt("clazz"));
//				_arc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("geom_way")));
				if(!_idLngLatHash.containsKey(_sourceId.get(_sourceId.size()-1))){
					_idLngLatHash.put(_sourceId.get(_sourceId.size()-1), _sourcePoint.get(_sourcePoint.size()-1));
				}
				if(!_idLngLatHash.containsKey(_targetId.get(_targetId.size()-1))){
					_idLngLatHash.put(_targetId.get(_targetId.size()-1), _targetPoint.get(_targetPoint.size()-1));
				}
				_idLinkHash.put(_linkId.get(_linkId.size()-1), _link.get(_link.size()-1));
			}
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
//	/***
//	 * focus内のリンクを取り出しテーブルへ格納
//	 * @param aCenterLngLat
//	 * @param aRadius
//	 */
//	public void getFocusRoad(Point2D aCenterLngLat, double aRadius){
//		insertTmpTableFocus(aCenterLngLat, aRadius);
//	}
//	/**
//	 * contextのリンクを取り出しテーブルへ格納
//	 * @param aCenterLngLat
//	 * @param aRadius
//	 */
//	public void getContextRoad(Point2D aCenterLngLat, double aRadius, Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
//		insertTmpTableContext(aCenterLngLat, aRadius, aUpperLeftLngLat, aLowerRightLngLat);
//	}
//	// glueサーバからxml形式でとってくる.
//	/**
//	 * 
//	 */
	/**
	 * glue内の道路を取得
	 * @param aCenterLngLat
	 * @param aFoucsZoomLevel
	 * @param aContextZoomLevel
	 * @param aGlueInnerRadius
	 * @param aGlueOuterRadius
	 */
	public void getGlueRoad(Point2D aCenterLngLat, int aFoucsZoomLevel, int aContextZoomLevel, int aGlueInnerRadius, int aGlueOuterRadius, double aGlueOuterRadiusMeter,
			ConvertElasticPointGlue _convert, ConvertLngLatXyCoordinate _convert2){
		///////////////////////
		// glueのベクターデータを取得.
		///////////////////////
		GlueRoadXml glueRoadXml = new GlueRoadXml(aCenterLngLat, aFoucsZoomLevel, aContextZoomLevel, aGlueInnerRadius, aGlueOuterRadius);
		//////////////////////////
		// ストロークIDから道路IDを求める.
		//////////////////////////
		ArrayList<Integer> glueSelectedRoadId = new ArrayList<>();	// glueで選択された道路ID.
		OsmStrokeDataGeom osmStrokeDataGeom = new OsmStrokeDataGeom();
		osmStrokeDataGeom.startConnection();
		for(int i=0; i<glueRoadXml._strokeId.size(); i++){
			glueSelectedRoadId.addAll(osmStrokeDataGeom.getLinkIdFromStrokeId(glueRoadXml._strokeId.get(i)));
		}
		osmStrokeDataGeom.endConnection();
		/////////////////////////
		// 道路IDからその他の情報を取得.
		/////////////////////////
//		_arc = new ArrayList<>();
//		_idLngLatHash = new HashMap<>();
//		_idLinkHash = new HashMap<>();
		
		_linkId = new ArrayList<>();
		_sourceId = new ArrayList<>();
		_targetId = new ArrayList<>();
		_clazz = new ArrayList<>();
		_length = new ArrayList<>();
		_cost = new ArrayList<>();
		_link = new ArrayList<>();
		_idLngLatHash = new HashMap<>();
		_idXyHashMap = new HashMap<>();
		_idLinkHash = new HashMap<>();
		_linkPoint = new ArrayList<>();
		// glue範囲内にある道路データを取得.
		OsmRoadDataGeom osmRoadDataGeom = new OsmRoadDataGeom();
		osmRoadDataGeom.startConnection();
		osmRoadDataGeom.insertOsmRoadInCircle(aCenterLngLat, aGlueOuterRadiusMeter);
		osmRoadDataGeom.endConnection();
		for(int i=0; i<glueSelectedRoadId.size(); i++){
		//for(int i=0; i<300; i++){
			if(osmRoadDataGeom._linkId.contains(glueSelectedRoadId.get(i))){	// glue外側より中にあればその道路を追加.
				_linkId.add(osmRoadDataGeom._linkId.get(osmRoadDataGeom._idIndexHash.get(glueSelectedRoadId.get(i))));
				_sourceId.add(osmRoadDataGeom._sourceId.get(osmRoadDataGeom._idIndexHash.get(glueSelectedRoadId.get(i))));
				_targetId.add(osmRoadDataGeom._targetId.get(osmRoadDataGeom._idIndexHash.get(glueSelectedRoadId.get(i))));
				_clazz.add(osmRoadDataGeom._clazz.get(osmRoadDataGeom._idIndexHash.get(glueSelectedRoadId.get(i))));
				_length.add(osmRoadDataGeom._length.get(osmRoadDataGeom._idIndexHash.get(glueSelectedRoadId.get(i))));
				_link.add(osmRoadDataGeom._link.get(osmRoadDataGeom._idIndexHash.get(glueSelectedRoadId.get(i))));
				
				_idLngLatHash.put(_sourceId.get(_sourceId.size()-1), _link.get(_link.size()-1).getP1());
				_idXyHashMap.put(_sourceId.get(_sourceId.size()-1), (Point2D)_convert.convertLngLatGlueXy(_link.get(_link.size()-1).getP1()));
				_idLngLatHash.put(_targetId.get(_targetId.size()-1), _link.get(_link.size()-1).getP2());
				_idXyHashMap.put(_targetId.get(_targetId.size()-1), (Point2D)_convert.convertLngLatGlueXy(_link.get(_link.size()-1).getP2()));
				_idLinkHash.put(_linkId.get(_linkId.size()-1), _link.get(_link.size()-1));
				_linkPoint.add(new Line2D.Double(_convert.convertLngLatGlueXy(_link.get(_link.size()-1).getP1()), _convert.convertLngLatGlueXy(_link.get(_link.size()-1).getP2())));
				//_linkPoint.add(new Line2D.Double(_convert2.convertLngLatToXyCoordinate(_link.get(_link.size()-1).getP1()), _convert2.convertLngLatToXyCoordinate(_link.get(_link.size()-1).getP2())));
//				System.out.println("$$$$$$$$$$$$$$"+_convert2.convertLngLatToXyCoordinate(_link.get(_link.size()-1).getP1())+"  "+_convert2.convertLngLatToXyCoordinate(_link.get(_link.size()-1).getP2()));
//				System.out.println("##############"+_convert.convertLngLatGlueXy(_link.get(_link.size()-1).getP1())+"  "+_convert.convertLngLatGlueXy(_link.get(_link.size()-1).getP2()));
			}
		}
		///////////////////////////////
		//_strokePointは_linkPointに変換
		///////////////////////////////
//		_linkPoint = new ArrayList<>();
//		for(int i=0; i<glueRoadXml._strokePoint.size(); i++){
//			for(int j=0; j<glueRoadXml._strokePoint.get(i).size()-1; j++){
//				if(
//						(new Point2D.Double(aGlueOuterRadius, aGlueOuterRadius).distance(glueRoadXml._strokePoint.get(i).get(j))<aGlueOuterRadius || 
//						new Point2D.Double(aGlueOuterRadius, aGlueOuterRadius).distance(glueRoadXml._strokePoint.get(i).get(j+1))<aGlueOuterRadius)
//						&&
//						(new Point2D.Double(aGlueOuterRadius, aGlueOuterRadius).distance(glueRoadXml._strokePoint.get(i).get(j))>aGlueInnerRadius || 
//						new Point2D.Double(aGlueOuterRadius, aGlueOuterRadius).distance(glueRoadXml._strokePoint.get(i).get(j+1))>aGlueInnerRadius)
//				){
//					// focusの位置が中心に来るようにずらす.
//					_linkPoint.add(new Line2D.Double(
//							glueRoadXml._strokePoint.get(i).get(j).getX()+(MapPanel.WINDOW_WIDTH/2-aGlueOuterRadius), 
//							glueRoadXml._strokePoint.get(i).get(j).getY()+(MapPanel.WINDOW_WIDTH/2-aGlueOuterRadius), 
//							glueRoadXml._strokePoint.get(i).get(j+1).getX()+(MapPanel.WINDOW_WIDTH/2-aGlueOuterRadius), 
//							glueRoadXml._strokePoint.get(i).get(j+1).getY()+(MapPanel.WINDOW_WIDTH/2-aGlueOuterRadius)));
//				}
//			}
//		}
		
		// costの計算
		// 道路クラスと制限速度の関係 このリストにないものは10にする.
		HashMap<Integer, Integer> clazzSpeed = new HashMap<>();
		clazzSpeed.put(11, 120);
		clazzSpeed.put(12, 30);
		clazzSpeed.put(13, 90);
		clazzSpeed.put(14, 30);
		clazzSpeed.put(15, 70);
		clazzSpeed.put(16, 30);
		clazzSpeed.put(21, 60);
		clazzSpeed.put(22, 30);
		clazzSpeed.put(31, 40);
		clazzSpeed.put(32, 50);
		clazzSpeed.put(41, 30);
		clazzSpeed.put(42, 30);
		for(int i=0; i<_linkId.size(); i++){
			// コスト(距離/制限速度).
			double cost = clazzSpeed.containsKey(_clazz.get(i)) ? (double)_length.get(i)/clazzSpeed.get(_clazz.get(i)) : (double)_length.get(i)/10;
			_cost.add(cost);
		}
		
	}
	/**
	 * 一時テーブルへ格納
	 */
	public void insertTmpTable(ArrayList<Integer> linkId, ArrayList<Integer> sourceId, ArrayList<Integer> targetId, ArrayList<Integer> clazz, 
			ArrayList<Double> length, ArrayList<Double> cost, ArrayList<Line2D> link){
		for(int i=0; i<linkId.size(); i++){
			try{
				String stmt = "" +
						"insert into tb_temp(id, source, target, clazz, km, cost, x1, y1, x2, y2) " +
						" values("+linkId.get(i)+","+sourceId.get(i)+","+targetId.get(i)+","+clazz.get(i)+","+length.get(i)+","+cost.get(i)+","+
						link.get(i).getX1()+","+link.get(i).getY1()+","+link.get(i).getX2()+","+link.get(i).getY2()+")" +
						"";
				insertInto(stmt);
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * focus 指定のデータを取得
	 * @param aCenterLngLat
	 * @param aRadius
	 */
	public void getFoucsRoad(Point2D aCenterLngLat, double aRadius, ConvertLngLatXyCoordinate _convert){
		_linkId = new ArrayList<>();
		_link = new ArrayList<>();
		_sourceId = new ArrayList<>();
		_sourcePoint = new ArrayList<>();
		_targetId = new ArrayList<>();
		_targetPoint = new ArrayList<>();
		_length = new ArrayList<>();
		_cost = new ArrayList<>();
		_clazz = new ArrayList<>();
		_arc = new ArrayList<>();
		_linkPoint = new ArrayList<>();
		_idLngLatHash = new HashMap<>();
		_idLinkHash = new HashMap<>();
		_idXyHashMap = new HashMap<>();
		try{
			String statement = "";
			statement = " " +
						"select " +
						" id, source, target, clazz, km, cost, x1, y1, x2, y2, geom_way" +
						" from osm_japan_car_2po_4pgr " +
						" where" +
						" st_intersects(" +
							"st_transform(" +
								"ST_Buffer(" +
									"st_transform(" +
										"ST_SetSRID(ST_MakePoint("+aCenterLngLat.getX()+", "+aCenterLngLat.getY()+"),"+WGS84_EPSG_CODE+"), "+
										WGS84_UTM_EPGS_CODE+"" +
									"), "+aRadius+"" +
								"), "+WGS84_EPSG_CODE+"" +
							"), "+
							"geom_way) " +
						" and " +
						" clazz > 12" +
						";";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_linkId.add(rs.getInt("id"));
				_sourceId.add(rs.getInt("source"));
				_sourcePoint.add(new Point2D.Double(rs.getDouble("x1"), rs.getDouble("y1")));
				_targetId.add(rs.getInt("target"));
				_targetPoint.add(new Point2D.Double(rs.getDouble("x2"), rs.getDouble("y2")));
				_link.add((Line2D)new Line2D.Double(rs.getDouble("x1"), rs.getDouble("y1"), rs.getDouble("x2"), rs.getDouble("y2")));
				_length.add(rs.getDouble("km"));
				_cost.add(rs.getDouble("cost"));
				_clazz.add(rs.getInt("clazz"));
	//			_arc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("geom_way")));
				if(!_idLngLatHash.containsKey(_sourceId.get(_sourceId.size()-1))){
					_idLngLatHash.put(_sourceId.get(_sourceId.size()-1), _sourcePoint.get(_sourcePoint.size()-1));
					_idXyHashMap.put(_sourceId.get(_sourceId.size()-1), (Point2D)_convert.convertLngLatToXyCoordinate(_sourcePoint.get(_sourcePoint.size()-1)));
				}
				if(!_idLngLatHash.containsKey(_targetId.get(_targetId.size()-1))){
					_idLngLatHash.put(_targetId.get(_targetId.size()-1), _targetPoint.get(_targetPoint.size()-1));
					_idXyHashMap.put(_targetId.get(_targetId.size()-1), (Point2D)_convert.convertLngLatToXyCoordinate(_targetPoint.get(_targetPoint.size()-1)));
				}
				_idLinkHash.put(_linkId.get(_linkId.size()-1), _link.get(_link.size()-1));
				_linkPoint.add(_convert.convertLngLatToXyCoordinateLine2D(_link.get(_link.size()-1)));
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * context 指定のデータを取得
	 */
	public void getContextRoad(Point2D aCenterLngLat, double aRadius, Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat,
			ConvertLngLatXyCoordinate _convert){
		_linkId = new ArrayList<>();
		_link = new ArrayList<>();
		_sourceId = new ArrayList<>();
		_sourcePoint = new ArrayList<>();
		_targetId = new ArrayList<>();
		_targetPoint = new ArrayList<>();
		_length = new ArrayList<>();
		_cost = new ArrayList<>();
		_clazz = new ArrayList<>();
		_arc = new ArrayList<>();
		_linkPoint = new ArrayList<>();
		_idLngLatHash = new HashMap<>();
		_idLinkHash = new HashMap<>();
		_idXyHashMap = new HashMap<>();
		try{
			String statement = "";
			statement = " " +
					"select " +
					" id, clazz, source, target, km, cost, x1, y1, x2, y2, geom_way" +
					" from " +
						" (" +
							"select " +
								"id, clazz, source, target, km, cost, x1, y1, x2, y2, geom_way " +
							"from " +
								"osm_japan_car_2po_4pgr " +
							" where" +
								" st_intersects(" +
									"st_geomFromText(" +
										"'polygon(("+
											aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
											aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
											aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
											aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
											aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+
										"))',"+WGS84_EPSG_CODE+
									"), "+
								"geom_way) " +
						") as t1 " +
					" where" +
					" not st_contains(" +
						"st_transform(" +
							"ST_Buffer(" +
								"st_transform(" +
									"ST_SetSRID(ST_MakePoint("+aCenterLngLat.getX()+", "+aCenterLngLat.getY()+"),"+WGS84_EPSG_CODE+"), "+
									WGS84_UTM_EPGS_CODE+"" +
								"), "+aRadius+"" +
							"), "+WGS84_EPSG_CODE+"" +
						"), "+
						"geom_way) " +
					" and " +
					" clazz > 12" +
				";";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_linkId.add(rs.getInt("id"));
				_sourceId.add(rs.getInt("source"));
				_sourcePoint.add(new Point2D.Double(rs.getDouble("x1"), rs.getDouble("y1")));
				_targetId.add(rs.getInt("target"));
				_targetPoint.add(new Point2D.Double(rs.getDouble("x2"), rs.getDouble("y2")));
				_link.add((Line2D)new Line2D.Double(rs.getDouble("x1"), rs.getDouble("y1"), rs.getDouble("x2"), rs.getDouble("y2")));
				_length.add(rs.getDouble("km"));
				_cost.add(rs.getDouble("cost"));
				_clazz.add(rs.getInt("clazz"));
	//			_arc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("geom_way")));
				if(!_idLngLatHash.containsKey(_sourceId.get(_sourceId.size()-1))){
					_idLngLatHash.put(_sourceId.get(_sourceId.size()-1), _sourcePoint.get(_sourcePoint.size()-1));
					_idXyHashMap.put(_sourceId.get(_sourceId.size()-1), (Point2D)_convert.convertLngLatToXyCoordinate(_sourcePoint.get(_sourcePoint.size()-1)));
				}
				if(!_idLngLatHash.containsKey(_targetId.get(_targetId.size()-1))){
					_idLngLatHash.put(_targetId.get(_targetId.size()-1), _targetPoint.get(_targetPoint.size()-1));
					_idXyHashMap.put(_targetId.get(_targetId.size()-1), (Point2D)_convert.convertLngLatToXyCoordinate(_targetPoint.get(_targetPoint.size()-1)));
				}
				_idLinkHash.put(_linkId.get(_linkId.size()-1), _link.get(_link.size()-1));
				_linkPoint.add(_convert.convertLngLatToXyCoordinateLine2D(_link.get(_link.size()-1)));
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	
//	/**
//	 * 指定した円内の道路データを取得する
//	 */
//	public void getOsmRoadFromCircle(Point2D aCenterLngLat, double aRadius){
//		_linkId = new ArrayList<>();
//		_link = new ArrayList<>();
//		_sourceId = new ArrayList<>();
//		_targetId = new ArrayList<>();
//		_length = new ArrayList<>();
//		_length2 = new ArrayList<>();
//		_clazz = new ArrayList<>();
//		_arc = new ArrayList<>();
//		
//		try{
//			String statement;
//			// SRID=4326.
//			statement = "select " +
//					" id, osm_name,osm_source_id, osm_target_id, clazz, source, target, km, cost, x1, y1, x2, y2, geom_way" +
//					" from osm_japan_car_2po_4pgr " +
//					" where" +
//					" st_intersects(" +
//						"st_transform(" +
//							"ST_Buffer(" +
//								"st_transform(" +
//									"ST_SetSRID(ST_MakePoint("+aCenterLngLat.getX()+", "+aCenterLngLat.getY()+"),"+WGS84_EPSG_CODE+"), "+
//									WGS84_UTM_EPGS_CODE+"" +
//								"), "+aRadius+"" +
//							"), "+WGS84_EPSG_CODE+"" +
//						"), "+
//						"geom_way) " +
//					" and " +
//					" clazz > 12" +
//					"";
////			System.out.println(statement);
//			ResultSet rs = execute(statement);
//			while(rs.next()){
//				_linkId.add(rs.getInt("id"));
//				_sourceId.add(rs.getInt("source"));
//				_targetId.add(rs.getInt("target"));
//				_link.add((Line2D)new Line2D.Double(rs.getDouble("x1"), rs.getDouble("y1"), rs.getDouble("x2"), rs.getDouble("y2")));
//				_length.add(rs.getDouble("km"));
//				_length2.add(rs.getDouble("cost"));
//				_clazz.add(rs.getInt("clazz"));
//				_arc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("geom_way")));
//			}
//			rs.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	}
//	/**
//	 * 円の外のリンクを取り出す
//	 * @param aCenterLngLat
//	 * @param aRadius
//	 */
//	public void getOsmRoadFromOutCircle(Point2D aCenterLngLat, double aRadius, Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
//		_linkId = new ArrayList<>();
//		_link = new ArrayList<>();
//		_sourceId = new ArrayList<>();
//		_targetId = new ArrayList<>();
//		_length = new ArrayList<>();
//		_length2 = new ArrayList<>();
//		_clazz = new ArrayList<>();
//		_arc = new ArrayList<>();
//		
//		try{
//			String statement;
//			// SRID=4326.
//			statement = "select " +
//					" id, osm_name,osm_source_id, osm_target_id, clazz, source, target, km, cost, x1, y1, x2, y2, geom_way" +
//					" from (select from osm_japan_car_2po_4pgr where) " +
//					" where" +
//					" not st_contains(" +
//						"st_transform(" +
//							"ST_Buffer(" +
//								"st_transform(" +
//									"ST_SetSRID(ST_MakePoint("+aCenterLngLat.getX()+", "+aCenterLngLat.getY()+"),"+WGS84_EPSG_CODE+"), "+
//									WGS84_UTM_EPGS_CODE+"" +
//								"), "+aRadius+"" +
//							"), "+WGS84_EPSG_CODE+"" +
//						"), "+
//						"geom_way) " +
//					" and " +
//					" clazz > 12" +
//					"";
////			System.out.println(statement);
//			ResultSet rs = execute(statement);
//			while(rs.next()){
//				_linkId.add(rs.getInt("id"));
//				_sourceId.add(rs.getInt("source"));
//				_targetId.add(rs.getInt("target"));
//				_link.add((Line2D)new Line2D.Double(rs.getDouble("x1"), rs.getDouble("y1"), rs.getDouble("x2"), rs.getDouble("y2")));
//				_length.add(rs.getDouble("km"));
//				_length2.add(rs.getDouble("cost"));
//				_clazz.add(rs.getInt("clazz"));
//				_arc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("geom_way")));
//			}
//			rs.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	}
//	
	
	public ArrayList<Double> _routingCost = new ArrayList<>();
	/**
	 * 経路探索をする
	 * return .get(i).get(0): i番目の経路のsourceId .get(i).get(1):i番目の経路のエッジ(リンク) ID, .get(i).get(2): 通過コスト
	 */
	public ArrayList<ArrayList<Integer>> execRouting(int aSourceId, int aTargetId, Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
		ArrayList<ArrayList<Integer>> routingResult = new ArrayList<>();
		_routingCost = new ArrayList<>();
		try{
			String stmt = "select " +
						" seq, id1, id2, cost "+
					" from "+
						" pgr_dijkstra("+
//							SELECT id, source, target, cost [,reverse_cost] FROM edge_table
							" 'select"+
//								id:	エッジの識別子[ int4 ]
								" id::integer as id" +
//								source:	int4 型の始点ノードの識別子
								", source::integer as source" +
//								target:	int4 型の終点ノードの識別子
								", target::integer as target" +
//								cost:	float8 型のエッジにかかる重み。負の重みはエッジがグラフに挿入されることを防ぎます。
//								", cost::double precision as cost" +
								", cost::double precision as cost" +
//								reverse_cost:	(オプション) エッジの反対方向のコスト。この値は directed および``has_rcost`` パラメータが true の場合のみ使用されます。(負のコストについては前述の通りです)
//								", reverse_cost::double precision as reverse_cost"+
							" from"+
								" tb_temp"+
							"'"+
							","+aSourceId+""+//int4 始点ノードのID
							","+aTargetId+""+//int4 終点ノードのID
							",false"+//有向グラフの場合は true を指定
							",false" +//true の場合、SQLで生成される行セットの reverse_cost 列は、エッジの逆方向にかかる重みとして使用されます。
						");";
			System.out.println(stmt);
			ResultSet rSet = execute(stmt);
			while(rSet.next()){
				routingResult.add(new ArrayList<>(Arrays.asList(rSet.getInt("id1"), rSet.getInt("id2"))));
				_routingCost.add(rSet.getDouble("cost"));
				System.out.println(new ArrayList<>(Arrays.asList(rSet.getInt("id1"), rSet.getInt("id2"))));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return routingResult;
	}
	
	
	
}
