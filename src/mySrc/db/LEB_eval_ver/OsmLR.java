package mySrc.db.LEB_eval_ver;

import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.postgis.PGgeometry;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;

public class OsmLR  extends HandleDbTemplateSuper{
	protected static final String DBNAME = "osm_road_db";	// Database Name
	protected static final String SCHEMA = "looproad";
	protected static final String TBNAME = "looproad_geom";
	protected static final String USER = "postgres";			// user name for DB.
	protected static final String PASS = "usadasql";		// password for DB.
	protected static final String URL = "rain2.elcom.nitech.ac.jp";
	protected static final int PORT = 5432;
	protected static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	/** 1つの周回道路 */
	public int _oneLooproadId = -1;
	public ArrayList<Point2D> _oneLooproad = new ArrayList<>();
	public String _oneLooproadString = "";
	
	
	/** 周回道路の集合 */
	ArrayList<ArrayList<Point2D>> _looproad = new ArrayList<>();
	/** wkt形式の周回道路 */
	ArrayList<String> _looproadWktString = new ArrayList<>();
	
//	/** 周回道路を束ねたジオメトリ */
//	ArrayList<ArrayList<Point2D>> _multiLooproad= new ArrayList<>();
	
	
	public OsmLR() {
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	
	/**
	 * 指定地点の周回道路を返す
	 */
	public void getLoopRoadFromPoint(Point2D aPoint2d){
		_oneLooproadId = -1;
		_oneLooproad = new ArrayList<>();
		_oneLooproadString = "";
		try{
			String stmt = "" +
					" select id, geom, st_asText(geom) as geomString " +
					" from "+SCHEMA+"."+TBNAME+" " +
					" where st_contains(geom, "+GeometryParsePostgres.point2dString(aPoint2d, WGS84_EPSG_CODE)+")";
//			System.out.println(stmt);
			ResultSet rs = execute(stmt);
			while(rs.next()){
				_oneLooproadId = rs.getInt("id");
				_oneLooproad = GeometryParsePostgres.pgGeometryPolygon((PGgeometry)rs.getObject("geom"));
				_oneLooproadString = rs.getString("geomString");
//				System.out.println("looproadId:"+_oneLooproadId);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
