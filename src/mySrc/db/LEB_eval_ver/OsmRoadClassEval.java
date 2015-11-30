package mySrc.db.LEB_eval_ver;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.postgis.PGgeometry;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;

/**
 * 道路クラスごとに評価をする
 * @author murase
 *
 */
public class OsmRoadClassEval  extends HandleDbTemplateSuper{
	private static final String DBNAME = "osm_road_db";	// Database Name
	private static final String SCHEMA = "public";
	private static final String TBNAME = "";
	private static final String TBNAME2 = "";
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "usadasql";		// password for DB.
	private static final String URL = "rain2.elcom.nitech.ac.jp";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	
	public OsmRoadClassEval() {
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	
	/** リンクID */
	public ArrayList<Integer> _linkId;
	/** km */
	public ArrayList<Double> _length;
	/** 道路のクラス */
	public ArrayList<Integer> _clazz;
	/** 道路の形状を表す */
	public ArrayList<ArrayList<Line2D>> _arc;
	/**  */
	public ArrayList<String> _geomString;
	
	/**
	 * 矩形範囲のデータを取り出す
	 */
	public void insertOsmRoadData(Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat, int roadClass){
		_linkId = new ArrayList<>();
		_length = new ArrayList<>();
		_clazz = new ArrayList<>();
		_arc = new ArrayList<>();
		_geomString = new ArrayList<>();
		
		try{
			String statement;
			statement = "select " +
					" id, clazz, km, geom_way, st_asText(geom_way) as geomString " +
					" from osm_japan_car_2po_4pgr " +
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
					" clazz = " + roadClass+
					"";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_linkId.add(rs.getInt("id"));
				_length.add(rs.getDouble("km"));
				_clazz.add(rs.getInt("clazz"));
				_arc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("geom_way")));
				_geomString.add(rs.getString("geomString"));
			}
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
}

