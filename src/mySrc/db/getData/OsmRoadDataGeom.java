package mySrc.db.getData;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.postgis.PGgeometry;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;
import mySrc.panel.inputPanel.InputPanel;
import mySrc.coordinate.*;
import mySrc.panel.mapPanel.*;

/**
 * OSM道路データを扱う
 * @author murase
 *
 */
public class OsmRoadDataGeom extends HandleDbTemplateSuper {
	private static final String DBNAME = "osm_road_db";	// Database Name
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "usadasql";		// password for DB.
	private static final String SCHEMA = "public";
	private static final String TABLE = "osm_japan_car_2po_4pgr";
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
	public ArrayList<Double> _length2;
	/** 道路のクラス */
	public ArrayList<Integer> _clazz;
	/** 道路の形状を表す */
	public ArrayList<ArrayList<Line2D>> _arc;
	/** ノードidと緯度経度の組(ハッシュ) */
	public HashMap<Integer, Point2D> _idLngLatHash;
	/**  */
	public HashMap<Integer, Point2D> _idXyHash;
	/** idとリンクのジオメトリの組(ハッシュ) */
	public HashMap<Integer, Line2D> _idLinkHash;
	/** idとインデックスのハッシュ */
	public HashMap<Integer, Integer> _idIndexHash;
	
	public OsmRoadDataGeom(){
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	/**
	 * 矩形範囲のデータを取り出す
	 */
	public void insertOsmRoadData(Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
		_linkId = new ArrayList<>();
		_link = new ArrayList<>();
		_sourceId = new ArrayList<>();
		_targetId = new ArrayList<>();
		_length = new ArrayList<>();
		_length2 = new ArrayList<>();
		_clazz = new ArrayList<>();
		_arc = new ArrayList<>();
		_idLngLatHash = new HashMap<>();
		_idXyHash = new HashMap<>();
		_idLinkHash = new HashMap<>();
		_sourcePoint = new ArrayList<>();
		_targetPoint = new ArrayList<>();
		_idIndexHash = new HashMap<>();
		
		ConvertLngLatXyCoordinate convert = new ConvertLngLatXyCoordinate(aUpperLeftLngLat, aLowerRightLngLat, MapPanel.WINDOW_SIZE);
		
		try{
			String statement;
			// SRID=4326.
			statement = "select " +
					" id, osm_name,osm_source_id, osm_target_id, clazz, source, target, km, cost, x1, y1, x2, y2, geom_way " +
					" from "+TABLE+" " +
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
					" and " +
					" clazz > 12" +
					"";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_linkId.add(rs.getInt("id"));
				_sourceId.add(rs.getInt("source"));
				_targetId.add(rs.getInt("target"));
				_sourcePoint.add(new Point2D.Double(rs.getDouble("x1"), rs.getDouble("y1")));
				_targetPoint.add(new Point2D.Double(rs.getDouble("x2"), rs.getDouble("y2")));
				_link.add((Line2D)new Line2D.Double(rs.getDouble("x1"), rs.getDouble("y1"), rs.getDouble("x2"), rs.getDouble("y2")));
				_length.add(rs.getDouble("km"));
				_length2.add(rs.getDouble("cost"));
				_clazz.add(rs.getInt("clazz"));
//				System.out.println(GeometryParsePostgres.getLineStringMultiPoint((PGgeometry)rs.getObject("geom")));
				_arc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("geom_way")));
				if(!_idLngLatHash.containsKey(_sourceId.get(_sourceId.size()-1))){
					_idLngLatHash.put(_sourceId.get(_sourceId.size()-1), _sourcePoint.get(_sourcePoint.size()-1));
					_idXyHash.put(_sourceId.get(_sourceId.size()-1), convert.convertLngLatToXyCoordinate(_sourcePoint.get(_sourcePoint.size()-1)));
				}
				if(!_idLngLatHash.containsKey(_targetId.get(_targetId.size()-1))){
					_idLngLatHash.put(_targetId.get(_targetId.size()-1), _targetPoint.get(_targetPoint.size()-1));
					_idXyHash.put(_targetId.get(_targetId.size()-1), convert.convertLngLatToXyCoordinate(_targetPoint.get(_targetPoint.size()-1)));
				}
				_idLinkHash.put(_linkId.get(_linkId.size()-1), _link.get(_link.size()-1));
				_idIndexHash.put(_linkId.get(_linkId.size()-1), _linkId.size()-1);
			}
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	/***
	 * 円形範囲の道路データを取り出す
	 */
	public void insertOsmRoadInCircle(Point2D aCenterLngLat, double aRadiusMeter){
		_linkId = new ArrayList<>();
		_link = new ArrayList<>();
		_sourceId = new ArrayList<>();
		_targetId = new ArrayList<>();
		_length = new ArrayList<>();
		_length2 = new ArrayList<>();
		_clazz = new ArrayList<>();
		_arc = new ArrayList<>();
		_idLngLatHash = new HashMap<>();
		_idLinkHash = new HashMap<>();
		_sourcePoint = new ArrayList<>();
		_targetPoint = new ArrayList<>();
		_idIndexHash = new HashMap<>();
		
		try{
			String statement;
			// SRID=4326.
			statement = "" +
				"select "+
					" id, osm_name,osm_source_id, osm_target_id, clazz, source, target, km, cost, x1, y1, x2, y2, geom_way" +
				" from "+SCHEMA+"."+TABLE+" " +
				" where " +
					" st_intersects("+
						"st_transform("+
							" st_buffer(" +
								"st_transform("+
									"st_geomFromText(" +
										"'Point("+(aCenterLngLat.getX())+" "+
											(aCenterLngLat.getY())+")'" +
										", "+HandleDbTemplateSuper.WGS84_EPSG_CODE+"" +
									")," +
									WGS84_UTM_EPGS_CODE +
								")," +
								aRadiusMeter+
							"), " +
						""+HandleDbTemplateSuper.WGS84_EPSG_CODE+")" +
					",geom_way)" +
					"";
			System.out.println("円形範囲道路の取得"+statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_linkId.add(rs.getInt("id"));
				_sourceId.add(rs.getInt("source"));
				_targetId.add(rs.getInt("target"));
				_sourcePoint.add(new Point2D.Double(rs.getDouble("x1"), rs.getDouble("y1")));
				_targetPoint.add(new Point2D.Double(rs.getDouble("x2"), rs.getDouble("y2")));
				_link.add((Line2D)new Line2D.Double(rs.getDouble("x1"), rs.getDouble("y1"), rs.getDouble("x2"), rs.getDouble("y2")));
				_length.add(rs.getDouble("km"));
				_length2.add(rs.getDouble("cost"));
				_clazz.add(rs.getInt("clazz"));
//				System.out.println(GeometryParsePostgres.getLineStringMultiPoint((PGgeometry)rs.getObject("geom")));
				_arc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("geom_way")));
				if(!_idLngLatHash.containsKey(_sourceId.get(_sourceId.size()-1))){
					_idLngLatHash.put(_sourceId.get(_sourceId.size()-1), _sourcePoint.get(_sourcePoint.size()-1));
				}
				if(!_idLngLatHash.containsKey(_targetId.get(_targetId.size()-1))){
					_idLngLatHash.put(_targetId.get(_targetId.size()-1), _targetPoint.get(_targetPoint.size()-1));
				}
				_idLinkHash.put(_linkId.get(_linkId.size()-1), _link.get(_link.size()-1));
				_idIndexHash.put(_linkId.get(_linkId.size()-1), _linkId.size()-1);
			}
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * 経路探索をする
	 * return .get(i).get(0): i番目の経路のsourceId .get(i).get(1):i番目の経路のエッジID
	 */
	public ArrayList<ArrayList<Integer>> execRouting(int aSourceId, int aTargetId, Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
		ArrayList<ArrayList<Integer>> routingResult = new ArrayList<>();
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
								", cost::double precision as cost" +
//								reverse_cost:	(オプション) エッジの反対方向のコスト。この値は directed および``has_rcost`` パラメータが true の場合のみ使用されます。(負のコストについては前述の通りです)
								", reverse_cost::double precision as reverse_cost"+
							" from"+
								" osm_japan_car_2po_4pgr"+
							" where"+
								" st_intersects("+
									" geom_way,"+
									" st_geomFromText("+
										" ''polygon(("+
											aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
											aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
											aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
											aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
											aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+
										"))'',"+WGS84_EPSG_CODE+
									")"+
								")"+
							"'"+
							","+aSourceId+""+//int4 始点ノードのID
							","+aTargetId+""+//int4 終点ノードのID
							",false"+//有向グラフの場合は true を指定
							",true" +//true の場合、SQLで生成される行セットの reverse_cost 列は、エッジの逆方向にかかる重みとして使用されます。
						");";
			System.out.println(stmt);
			ResultSet rSet = execute(stmt);
			while(rSet.next()){
				routingResult.add(new ArrayList<>(Arrays.asList(rSet.getInt("id1"), rSet.getInt("id2"))));
				System.out.println(new ArrayList<>(Arrays.asList(rSet.getInt("id1"), rSet.getInt("id2"))));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return routingResult;
	}
}
