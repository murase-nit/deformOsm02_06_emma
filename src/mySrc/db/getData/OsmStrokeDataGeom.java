package mySrc.db.getData;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.postgis.PGgeometry;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;
/**
 * ストロークテーブルを使ったデータ処理
 * @author murase
 *
 */
public class OsmStrokeDataGeom extends HandleDbTemplateSuper{
	
	private static final String DBNAME = "osm_road_db";	// Database Name
	private static final String SCHEMA = "stroke_v2";
	private static final String TBNAME = "stroke_table";
	private static final String TBNAME2 = "flatted_stroke_table";
	private static final String USER = "postgres";			// user name for DB.
	private static final String PASS = "usadasql";		// password for DB.
	private static final String URL = "rain2.elcom.nitech.ac.jp";
	private static final int PORT = 5432;
	private static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	/** ストロークID */
	public ArrayList<Integer> _strokeId = new ArrayList<>();
	
	public OsmStrokeDataGeom() {
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	// 切り出さずにそのままのストローク.
	/** データベースからそのまま取り出したストローク(arc形式) */
	public ArrayList<ArrayList<Line2D>> _strokeArc = new ArrayList<>();
	/** ストロークのWKT形式 */
	public ArrayList<String> _strokeArcString = new ArrayList<>();
	/** ストロークの長さ */
	public ArrayList<Double> _strokeLength = new ArrayList<>();
	/** ストロークIDからインデックスを求めるハッシュ */
	public HashMap<Integer, Integer> _strokeIdToIndexHash = new HashMap<>();
	/**
	 * 範囲内のストロークを取り出す
	 */
	public void insertStrokeData(Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
		_strokeId = new ArrayList<>();
		_strokeLength = new ArrayList<>();
		_strokeArc = new ArrayList<>();
		_strokeArcString = new ArrayList<>();
		_strokeIdToIndexHash = new HashMap<>();
		try{
			String statement;
			statement = "select "+
					" id, length,"+
					" flatted_arc_series, " +
					" st_asText(flatted_arc_series) as strokeString " +
					" from "+SCHEMA+"."+TBNAME2+" " +
					" where" +
					" st_intersects(" +
						"flatted_arc_series, "+
						"st_polygonFromText(" +
							"'Polygon(("+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
								"))'," +
							""+HandleDbTemplateSuper.WGS84_EPSG_CODE+")" +
						");";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_strokeId.add(rs.getInt("id"));
				_strokeLength.add(rs.getDouble("length"));
				_strokeArc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("flatted_arc_series")));
				_strokeArcString.add(rs.getString("strokeString"));
				_strokeIdToIndexHash.put(rs.getInt("id"), _strokeId.size()-1);
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	// 切り出さずにそのままのストローク.
//	/** データベースからそのまま取り出したストローク(arc形式) */
//	public ArrayList<ArrayList<Line2D>> _strokeArc = new ArrayList<>();
//	/** ストロークのWKT形式 */
//	public ArrayList<String> _strokeArcString = new ArrayList<>();
//	/** ストロークの長さ */
//	public ArrayList<Double> _strokeLength = new ArrayList<>();
//	/** ストロークIDからインデックスを求めるハッシュ */
//	public HashMap<Integer, Integer> _strokeIdToIndexHash = new HashMap<>();
	// 切り出したストローク.
	/** データベースから切り出したストローク　arc形式 */
	public ArrayList<ArrayList<Line2D>> _subStrokeArc = new ArrayList<>();
	/** データベースから切り出したストローク(WKT型) */
	public ArrayList<String> _subStrokeString = new ArrayList<>();
	/** データベースから切り出したストロークの長さ */
	public ArrayList<Double> _subStrokeLength = new ArrayList<>();
	/**
	 * 指定の範囲のストロークを切り出す.
	 * 切り出したストロークと切り出さないストロークの両方を求める.
	 */
	public void cutOutStroke(Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
		_subStrokeArc = new ArrayList<>();
		_subStrokeString = new ArrayList<>();
		_subStrokeLength = new ArrayList<>();
		
		_strokeId = new ArrayList<>();
		_strokeLength = new ArrayList<>();
		_strokeArc = new ArrayList<>();
		_strokeArcString = new ArrayList<>();
		_strokeIdToIndexHash = new HashMap<>();
		try{
			String statement;
			statement = "select "+
					" id, length,"+
					" flatted_arc_series, " +
					" st_asText(flatted_arc_series) as strokeString, " +
					"st_intersection(" +
						"st_polyFromText(" +
							"'Polygon(("+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
								aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
								aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
							"))'," + HandleDbTemplateSuper.WGS84_EPSG_CODE+"" +
						"), flatted_arc_series" +
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
							"), flatted_arc_series" +
						")" +
					") as cutoutStrokeString " +
					" from "+SCHEMA+"."+TBNAME2+" " +
					" where" +
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
						"flatted_arc_series);";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				_strokeId.add(rs.getInt("id"));
				_strokeLength.add(rs.getDouble("length"));
				_strokeArc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("flatted_arc_series")));
				_strokeArcString.add(rs.getString("strokeString"));
				_strokeIdToIndexHash.put(rs.getInt("id"), _strokeId.size()-1);
				
				_subStrokeArc.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rs.getObject("cutoutStroke")));
				_subStrokeString.add(rs.getString("cutOutStrokeString"));
//				System.out.println(_strokeId.get(_strokeId.size()-1));
//				System.out.println(_subStrokeArc.get(_subStrokeArc.size()-1));
			}
			rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		_subStrokeLength = getCutOutStrokeLength(_subStrokeString);	// 長さを求める.
	}
	
	/**
	 * 上位ｎこのストロークを求める
	 * @param topN
	 * @return
	 */
	public ArrayList<ArrayList<Line2D>> getTopN(int topN){
		ArrayList<ArrayList<Line2D>> top10 = new ArrayList<>();
		try{
			String stmt = "select flatted_arc_series from "+SCHEMA+"."+TBNAME2 + " order by length desc limit "+topN+"";
			ResultSet rSet = execute(stmt);
			while(rSet.next()){
				top10.add(GeometryParsePostgres.getLineStringMultiLine((PGgeometry)rSet.getObject(1)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return top10;
	}
	
	/**
	 * 切り出したストロークの長さを求める.
	 * wkt形式のジオメトリ
	 */
	public ArrayList<Double> getCutOutStrokeLength(ArrayList<String> aStrokeString){
		ArrayList<Double> strokeLength = new ArrayList<>();
		try{
			ResultSet rs = null;
			for(int i=0; i<_strokeId.size(); i++){
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
	 * 指定したストロークIDのデータを取得する
	 */
	public String testStrokeString(int aStrokeId){
		String wktString = "";
		try{
			String stmt = "select st_asText(flatted_arc_series) from "+SCHEMA+"."+TBNAME2+" where id= "+aStrokeId+""+"";
			System.out.println(stmt);
			ResultSet rs = execute(stmt);
			while(rs.next()){
				wktString = rs.getString(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return wktString;
	}
	
	/**
	 * ストロークIDからリンクIDを求める
	 */
	public ArrayList<Integer> getLinkIdFromStrokeId(int aStrokeId){
		ArrayList<Integer> linkIdArray = new ArrayList<>();
		
		try{
			String stmt = "" +
					"select " +
						" T2.id, T2.link_ids as linkIds "+
					" from "+
						" (select stroke_id from stroke_v2.flatted_stroke_table where id = "+aStrokeId+") as T1 "+
						" , stroke_v2.stroke_table as T2 "+
					" where "+
						" T1.stroke_id = T2.id ";
//			System.out.println(stmt);
			ResultSet rs = execute(stmt);
			if(rs.next()){
				String[] linkIdString = rs.getString("linkIds").split(",");
				for(int i=0; i<linkIdString.length; i++){
					linkIdArray.add(Integer.parseInt(linkIdString[i]));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return linkIdArray;
	}
	
}
