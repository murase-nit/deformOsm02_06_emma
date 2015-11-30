package mySrc.db.getData;

import java.awt.geom.Point2D;
import java.io.ObjectInputStream.GetField;
import java.nio.channels.SelectableChannel;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.postgis.PGgeometry;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;

/**
 * 周回道路に関するデータ処理
 * @author murase
 *
 */
public class OsmLooproad extends HandleDbTemplateSuper{
	protected static final String DBNAME = "osm_road_db";	// Database Name
	protected static final String SCHEMA = "looproad";
	protected static final String TBNAME = "looproad_geom";
	protected static final String USER = "postgres";			// user name for DB.
	protected static final String PASS = "usadasql";		// password for DB.
	protected static final String URL = "rain2.elcom.nitech.ac.jp";
	protected static final int PORT = 5432;
	protected static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	
	/** 1津の周回道路 */
	public int _oneLooproadId = -1;
	public ArrayList<Point2D> _oneLooproad = new ArrayList<>();
	
	
	/** 周回道路の集合 */
	ArrayList<ArrayList<Point2D>> _looproad = new ArrayList<>();
	/** wkt形式の周回道路 */
	ArrayList<String> _looproadWktString = new ArrayList<>();
	
//	/** 周回道路を束ねたジオメトリ */
//	ArrayList<ArrayList<Point2D>> _multiLooproad= new ArrayList<>();
	
	
	public OsmLooproad() {
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	
	/**
	 * 指定地点の周回道路を返す
	 */
	public void getLoopRoadFromPoint(Point2D aPoint2d){
		try{
			String stmt = "" +
					"select id, geom " +
					"from "+SCHEMA+"."+TBNAME+" " +
					"where st_contains(geom, "+GeometryParsePostgres.point2dString(aPoint2d, WGS84_EPSG_CODE)+")";
			System.out.println(stmt);
			ResultSet rs = execute(stmt);
			while(rs.next()){
				_oneLooproadId = rs.getInt("id");
				_oneLooproad = GeometryParsePostgres.pgGeometryPolygon((PGgeometry)rs.getObject("geom"));
				System.out.println("looproadId:"+_oneLooproadId);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


}
