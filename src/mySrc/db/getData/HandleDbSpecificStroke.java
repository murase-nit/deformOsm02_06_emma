package mySrc.db.getData;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.postgis.PGgeometry;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;

/**
 * データベースのストロークデータを扱う
 * @author murase
 *
 */
public class HandleDbSpecificStroke extends HandleDbTemplateSuper{
	private static final String DBNAME = "osm_road_db";	// Database Name
	private static final String SCHEMA = "stroke";
	private static final String TBNAME = "stroke_table";
	private static final String TBNAME2 = "flatted_stroke_table";
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "usadasql";		// password for DB.
	private static final String URL = "rain2.elcom.nitech.ac.jp";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	/** ピンの位置 */
	private Point2D.Double _pinPoint;
	
	/** 指定の周回道路 */
	public int _specificAreaId = -1;
	/** 指定した周回道路のwkt形式 */
	public String _specificAreaGeomWKT="";
	/** 指定した周回道路に接するストロークID */
	public ArrayList<Integer> _strokeId = new ArrayList<>();
	/** そのストロークのジオメトリ */
	public ArrayList<ArrayList<Line2D>> _strokeGeomArc = new ArrayList<>();
	
	
	public HandleDbSpecificStroke(){
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	/**
	 * 指定した点を含む周回道路を求める
	 */
	public void getSpecificLR(Point2D.Double aPoint){
		_pinPoint = aPoint;
		try{
			// dblinkで周回道路データベースへアクセスし，指定の座標を含む周回道路を取り出す.
			String statement;
			statement = "select " +
						" id, st_asText(geom) as geomText " +
					" from " +
					" looproad.looproad_geom "+ 
					" where "+
						" st_contains(geom, "+GeometryParsePostgres.point2dString(aPoint, 4326)+") " +
						"";
			
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_specificAreaGeomWKT = rs.getString("geomText");
			}
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * 指定した周回道路と接するストロークを求める(線で接する)
	 */
	public void getContactStroke(){
		_strokeId = new ArrayList<>();
		_strokeGeomArc = new ArrayList<>();
		try{			
			String statement;
			// 指定した周回道路とtouchesするストロークを取り出し，その中から，交差するジオメトリが1次元のものを取り出す.
			statement = "" +
					" select " +
					" id," +
					"flatted_arc_series," +
					"st_length(st_transform(flatted_arc_series, 3099)) as length," +
					"st_distance(flatted_arc_series, "+GeometryParsePostgres.point2dString(_pinPoint, 4326)+") as distanceFromPoint"+
				" from" +
					"(select " +
						" id, flatted_arc_series " +
					" from  " +
						"stroke.flatted_stroke_table" +
					" where st_touches(st_geomFromText('"+_specificAreaGeomWKT+"', 4326), flatted_arc_series)" +
					") as T1 " +
//				" where st_touches(st_geomFromText('"+_specificAreaGeomWKT+"', 4301), stroke_line) " +
				" where st_dimension(st_intersection(st_geomFromText('"+_specificAreaGeomWKT+"', 4326), flatted_arc_series)) = 1 " +
				" order by distanceFromPoint";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_strokeId.add(rs.getInt("id"));
				PGgeometry geom = (PGgeometry)rs.getObject(2);
				_strokeGeomArc.add(GeometryParsePostgres.getLineStringMultiLine(geom));
			}
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * コネクティビティがあるように並び替え
	 */
	public void reorderedContactStroke(){
		try{
			for(int i=1; i<_strokeId.size(); i++){
				String statement;
				// 現在順位が一番高いストロークと接触するストロークを取り出す.
				statement = "select st_intersects("+
						"ST_LineMerge("+GeometryParsePostgres.multiLineString(_strokeGeomArc.get(0), 4326)+"), "+
						"ST_LineMerge("+GeometryParsePostgres.multiLineString(_strokeGeomArc.get(i), 4326)+")) as bool";
				System.out.println(statement);
				ResultSet rs = execute(statement);
				if(rs.next()){
					System.out.println(rs.getBoolean(1));
					if(rs.getBoolean(1)){
						//入れかえ.
						int tmpId = _strokeId.get(1);
						ArrayList<Line2D> tmpLine2d = _strokeGeomArc.get(1);
						_strokeId.set(1, _strokeId.get(i));
						_strokeGeomArc.set(1, _strokeGeomArc.get(i));
						_strokeId.set(i, tmpId);
						_strokeGeomArc.set(i, tmpLine2d);
						break;
					}
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
