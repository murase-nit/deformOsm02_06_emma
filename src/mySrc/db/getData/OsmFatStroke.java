package mySrc.db.getData;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;

import org.postgis.PGgeometry;

/**
 * fatstrokeを扱う
 * @author murase
 *
 */
public class OsmFatStroke extends HandleDbTemplateSuper{
	private static final String DBNAME = "osm_relation_db";	// Database Name
	private static final String TBNAME = "fatstroke_table";
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "murase";		// password for DB.
	private static final String URL = "localhost";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	public OsmFatStroke() {
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	/** stroke id */
	public ArrayList<Integer> _strokeId = new ArrayList<>();
	/** データベースからそのまま取り出したストローク(arc形式) */
	public ArrayList<ArrayList<Line2D>> _strokeArc = new ArrayList<>();
	/** ストロークのWKT形式 */
	public ArrayList<String> _strokeArcString = new ArrayList<>();
	/** ストロークの長さ */
	public ArrayList<Double> _strokeLength = new ArrayList<>();
	/** 紐づいている施設データの数 */
	public ArrayList<Integer> _strokeFacilityNum = new ArrayList<>();
	/** カテゴリ */
	public String categoryString = "";
	/** strokeIdをインデックスへ変換するハッシュ */
	public HashMap<Integer, Integer> _strokeIdToIndexHash = new HashMap<>();
	
	
	/**
	 * 指定範囲内のストロークを取り出す
	 */
	public void insertFatStrokeFromMBR(Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat, String aCategory){
		_strokeId = new ArrayList<>();
		_strokeArc = new ArrayList<>();
		_strokeArcString = new ArrayList<>();
		_strokeLength = new ArrayList<>();
		_strokeFacilityNum = new ArrayList<>();
		_strokeIdToIndexHash = new HashMap<>();
		try{
			String statement;
			statement = "select "+
					" id, stroke_id, stroke_length, facility_num, category, stroke, st_asText(stroke) as strokewkt " +
					" from "+TBNAME+" " +
					" where" +
					" st_intersects(" +
						"stroke, "+
						"st_polygonFromText(" +
							"'Polygon(("+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
								"))'," +
							""+HandleDbTemplateSuper.WGS84_EPSG_CODE+")" +
						")" +
						" and" +
						" category = '"+aCategory+"';";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_strokeId.add(rs.getInt("stroke_id"));
				_strokeArc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("stroke")));
				_strokeArcString.add(rs.getString("strokewkt"));
				_strokeLength.add(rs.getDouble("stroke_length"));
				_strokeFacilityNum.add(rs.getInt("facility_num"));
				_strokeIdToIndexHash.put(rs.getInt("stroke_id"), _strokeId.size()-1);
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
}
