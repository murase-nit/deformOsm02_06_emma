package mySrc.db.LEB_eval_ver;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.security.interfaces.RSAKey;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.postgis.PGgeometry;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;
/**
 * osm_relation_dbからファットストロークを取り出す
 * @author murase
 *
 */
public class OsmFatStrokeEvalVer extends HandleDbTemplateSuper{
	
	private static final String DBNAME = "osm_relation_db_v2";	// Database Name
	private static final String SCHEMA = "public";
	private static final String TBNAME = "";
	private static final String TBNAME2 = "";
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "murase";		// password for DB.
	private static final String URL = "localhost";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	public OsmFatStrokeEvalVer() {
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	// 切り出さずにそのままのストローク.
	/** ストロークID */
	public ArrayList<Integer> _strokeId = new ArrayList<>();
	/** データベースからそのまま取り出したストローク(arc形式) */
	public ArrayList<ArrayList<Line2D>> _strokeArc = new ArrayList<>();
	/** データベースからそのまま取り出したストローク(arc形式)(Point2D版) */
	public ArrayList<ArrayList<Point2D>> _strokeArcPoint = new ArrayList<>();
	/** ストロークのWKT形式 */
	public ArrayList<String> _strokeArcString = new ArrayList<>();
	/** ストロークの長さ */
	public ArrayList<Double> _strokeLength = new ArrayList<>();
	/** ストロークIDからインデックスを求めるハッシュ */
	public HashMap<Integer, Integer> _strokeIdToIndexHash = new HashMap<>();
	
	// fatstrokeの施設関係の変数.
	/** ストロークIDをキーとしたそのストロークのID */
	public HashMap<Integer, ArrayList<Integer>> _fatStrokeFacilityId = new HashMap<>();
	/** ストロークIDをキーとしたそのストロークのカテゴリ */
	public HashMap<Integer, ArrayList<String>> _fatStrokeCategory = new HashMap<>();
	/** ストロークIDをキーとしたそのストロークの施設位置 */
	public HashMap<Integer, ArrayList<Point2D>> _fatstrokeFacilityLocation = new HashMap<>();
	
	// 切り出したストローク.
	/** データベースから切り出したストローク　arc形式 */
	public ArrayList<ArrayList<Line2D>> _subStrokeArc = new ArrayList<>();
	/** データベースから切り出したストローク(WKT型) */
	public ArrayList<String> _subStrokeString = new ArrayList<>();
	/** データベースから切り出したストロークの長さ */
	public ArrayList<Double> _subStrokeLength = new ArrayList<>();

	/**
	 * カテゴリを指定して目的のFatStrokeを取り出す
	 * 
	 * 指定の範囲のストロークを切り出す.
	 * 切り出したストロークと切り出さないストロークの両方を求める.
	 * 
	 */
	public void cutOutStroke(Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat, String aCategory){
		// 切り出さない版.
		_strokeId = new ArrayList<>();
		_strokeLength = new ArrayList<>();
		_strokeArc = new ArrayList<>();
		_strokeArcPoint = new ArrayList<>();
		_strokeArcString = new ArrayList<>();
		_strokeIdToIndexHash = new HashMap<>();
		// 切り出している版.
		_subStrokeArc = new ArrayList<>();
		_subStrokeString = new ArrayList<>();
		_subStrokeLength = new ArrayList<>();
		// 施設データ関係
		_fatStrokeFacilityId = new HashMap<>();
		_fatStrokeCategory = new HashMap<>();
		_fatstrokeFacilityLocation = new HashMap<>();
		try{
			String statement;
//			statement = 
//					"select "+
//						" t1.stroke_id as strokeId, t1.facility_id as facilityId, t1.category as category, " +
//						" t2.stroke_length as strokeLength, t2.facility_num as facilityNum, t2.stroke as stroke, t3.location as location, "+
//						" st_asText(t2.stroke) as strokeString, " +
//						// 切り出したストロークのジオメトリとWKT形式.
//						"st_intersection(" +
//							"st_polyFromText(" +
//								"'Polygon(("+
//									aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//									aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//									aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//									aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//									aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
//								"))'," + HandleDbTemplateSuper.WGS84_EPSG_CODE+"" +
//							"), t2.stroke" +
//						") as cutoutStroke, " +
//						"st_asText(" +
//							"st_intersection(" +
//								"st_polyFromText(" +
//									"'Polygon(("+
//										aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//										aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//										aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//										aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//										aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
//									"))'," + HandleDbTemplateSuper.WGS84_EPSG_CODE+"" +
//								"), t2.stroke" +
//							")" +
//						") as cutoutStrokeString " +
//						////////////////////////////////////
//					" from " +
//						" stroke_and_facility_table as t1 "+
//						" inner join fatstroke_table as t2 on (t1.stroke_id = t2.stroke_id) "+
//						" inner join facility_table as t3 on (t1.facility_id = t3.id) "+
//					" where" +
//						" st_intersects(" +
//							"st_polygonFromText(" +
//								"'Polygon(("+
//									aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//									aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//									aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//									aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//									aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
//									"))'," +
//								""+HandleDbTemplateSuper.WGS84_EPSG_CODE+")," +
//							"t2.stroke)" +
//						" and" +
//						" t1.category = '"+aCategory+"';";
			
			statement = 
			"select "+
				" t1.facility_id as facilityId, t1.category as category, " +
				" t2.stroke_id as strokeId, t2.stroke_length as strokeLength, t2.facility_num as facilityNum, t2.stroke as stroke, " +
				" t3.location as location, "+
				" st_asText(t2.stroke) as strokeString, " +
				// 切り出したストロークのジオメトリとWKT形式.
				"st_intersection(" +
					"st_polyFromText(" +
						"'Polygon(("+
							aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
							aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
							aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
							aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
							aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
						"))'," + HandleDbTemplateSuper.WGS84_EPSG_CODE+"" +
					"), t2.stroke" +
				") as cutoutStroke, " +
				"st_asText(" +
					"st_intersection(" +
						"st_polyFromText(" +
							"'Polygon(("+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
							"))'," + HandleDbTemplateSuper.WGS84_EPSG_CODE+"" +
						"), t2.stroke" +
					")" +
				") as cutoutStrokeString " +
				////////////////////////////////////
			" from " +
				" (" +
				" select" +
				" stroke_id, stroke_length, facility_num, stroke, category"+
				" from"+
				" fatstroke_table"+
				" where"+
					" st_intersects(" +
						"st_polygonFromText(" +
							"'Polygon(("+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
								"))'," +
							""+HandleDbTemplateSuper.WGS84_EPSG_CODE+")," +
						"stroke)" +
				" and "+
				" category = '"+aCategory+"'"+
				" ) as t2 "+
				" left join stroke_and_facility_table as t1 on (t2.stroke_id = t1.stroke_id and t2.category = t1.category) "+
				" left join facility_table as t3 on (t1.facility_id = t3.id); ";
			
//			statement = 
//					"select "+
//						" t1.facility_id as facilityId, t1.category as category, " +
//						" t2.id as strokeId, t2.length as strokeLength, t2.flatted_arc_series as stroke, " +
//						" t3.location as location, "+
//						" st_asText(t2.flatted_arc_series) as strokeString, " +
//						// 切り出したストロークのジオメトリとWKT形式.
//						"st_intersection(" +
//							"st_polyFromText(" +
//								"'Polygon(("+
//									aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//									aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//									aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//									aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//									aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
//								"))'," + HandleDbTemplateSuper.WGS84_EPSG_CODE+"" +
//							"), t2.flatted_arc_series" +
//						") as cutoutStroke, " +
//						"st_asText(" +
//							"st_intersection(" +
//								"st_polyFromText(" +
//									"'Polygon(("+
//										aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//										aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//										aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//										aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//										aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
//									"))'," + HandleDbTemplateSuper.WGS84_EPSG_CODE+"" +
//								"), t2.flatted_arc_series" +
//							")" +
//						") as cutoutStrokeString " +
//					// flatted_arc_series(ストロークテーブル)にstroke_and_facility_tableを外部結合し，さらにfacility_tableを外部結合する.
//					" from " +
//						" dblink('host=rain2.elcom.nitech.ac.jp port=5432 dbname=osm_road_db user=postgres password=usadasql', " + 
//							" 'select " +
//								"id, length, clazz, flatted_arc_series "+
//							" from " +
//								"stroke.flatted_stroke_table "+
//							" where " +
//								"st_intersects(" +
//									"flatted_arc_series, " +
//									"st_geomFromText(" +
//										"''Polygon(("+
//												aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//												aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
//												aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//												aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
//												aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
//											"))''" +
//										", 4326))'" +
//						") as t2(id int, length double precision, clazz int, flatted_arc_series geometry) "+
//						" left join stroke_and_facility_table as t1 on (t1.stroke_id = t2.id) "+
//						" left join facility_table as t3 on (t1.facility_id = t3.id) "+
//					// 矩形範囲と，カテゴリでデータを絞る.
//					" where" +
//						" t1.category = '"+aCategory+"' or bit_length(t1.category) is null;";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
//				System.out.println("stroke id  "+rs.getInt("strokeId"));
				if(!_strokeIdToIndexHash.containsKey(rs.getInt("strokeId"))){// すでにストロークが入っている.
					// ストローク関係.
					_strokeId.add(rs.getInt("strokeId"));
					_strokeLength.add(rs.getDouble("strokeLength"));
					_strokeArc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("stroke")));
					_strokeArcPoint.add(GeometryParsePostgres.getLineStringMultiLine2((PGgeometry)rs.getObject("stroke")));
					_strokeArcString.add(rs.getString("strokeString"));
					_strokeIdToIndexHash.put(rs.getInt("strokeId"), _strokeId.size()-1);
					// 切り出したデータの取得.
					_subStrokeArc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("cutoutStroke")));
					_subStrokeString.add(rs.getString("cutOutStrokeString"));
					// 切り出した長さは後で求める.
					// 施設データ関係.
//					System.out.println(rs.getInt("facilityId"));
					if(rs.getInt("facilityId") != 0){
						_fatStrokeFacilityId.put(rs.getInt("strokeId"), new ArrayList<Integer>(Arrays.asList(rs.getInt("facilityId"))));
						_fatStrokeCategory.put(rs.getInt("strokeId"), new ArrayList<String>(Arrays.asList(rs.getString("category"))));
						_fatstrokeFacilityLocation.put(rs.getInt("strokeId"), new ArrayList<Point2D>(Arrays.asList(GeometryParsePostgres.pgGeometryToPoint2D((PGgeometry)rs.getObject("location")))));
					}else{
						_fatStrokeFacilityId.put(rs.getInt("strokeId"), new ArrayList<Integer>(Arrays.asList(0)));
						_fatStrokeCategory.put(rs.getInt("strokeId"), new ArrayList<String>(Arrays.asList("")));
						_fatstrokeFacilityLocation.put(rs.getInt("strokeId"), new ArrayList<Point2D>());
					}
				}else{	// このストロークはすでに格納したなら，施設データの情報のみを格納.
					_fatStrokeFacilityId.get(rs.getInt("strokeId")).add(rs.getInt("facilityId"));
					_fatStrokeCategory.get(rs.getInt("strokeId")).add(rs.getString("category"));
					_fatstrokeFacilityLocation.get(rs.getInt("strokeId")).add(GeometryParsePostgres.pgGeometryToPoint2D((PGgeometry)rs.getObject("location")));
				}
			}
//			System.out.println("stroke size" + _strokeId.size());
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
//		System.exit(0);
		_subStrokeLength = getCutOutStrokeLength(_subStrokeString);	// 長さを求める.
	}
	
	/**
	 * 切り出したストロークの長さを求める.
	 * wkt形式のジオメトリ
	 */
	public ArrayList<Double> getCutOutStrokeLength(ArrayList<String> aStrokeString){
		ArrayList<Double> strokeLength = new ArrayList<>();
		try{
			ResultSet rs = null;
			for(int i=0; i<aStrokeString.size(); i++){
				String statement;
				statement = "select " +
						" st_length(st_transform(st_geomFromText('"+aStrokeString.get(i)+"',"+HandleDbTemplateSuper.WGS84_EPSG_CODE+"), "+WGS84_UTM_EPGS_CODE+")) as strokeLength" +
						";";
				rs = execute(statement);
				while(rs.next()){
					strokeLength.add(rs.getDouble("strokeLength"));
				}
			}
			System.out.println("lenght"+strokeLength);
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return strokeLength;
	}
	
	
	/**
	 * 指定した周回道路がどのストロークと接するか
	 */
	public ArrayList<Integer> _intersectStrokeId;
	public ArrayList<String> _intersectStrokeStrings;
	public void getTouchStrokeFromLooproad(String aLRString){
		_intersectStrokeId = new ArrayList<>();
		_intersectStrokeStrings = new ArrayList<>();
		try{
			String statement;
			statement = 
					" select id, stroke_id, st_asText(stroke) as strokeString " +
					" from fatstroke_table" +
					" where "+
						" st_intersects(st_geomFromText('"+aLRString+"', 4326),stroke)"+
						" and"+
						" category = 'parking' ";
			ResultSet rs = execute(statement);
			while(rs.next()){
				_intersectStrokeId.add(rs.getInt("stroke_id"));
				_intersectStrokeStrings.add(rs.getString("strokeString"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
