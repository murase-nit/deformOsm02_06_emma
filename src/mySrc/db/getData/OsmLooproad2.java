package mySrc.db.getData;

import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;

import mySrc.db.GeometryParsePostgres;

import org.postgis.PGgeometry;

/**
 * 周回道路処理関係(データを絞る)
 * @author murase
 *
 */
public class OsmLooproad2 extends OsmLooproad{

	public OsmLooproad2() {
		super();
	}
	
	/**
	 * 指定範囲を含む一時的な周回道路データベースの作成
	 */
	public void createTmpLooproadDb(Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
		try{
			String statement="";
			statement += "create temp table tb_temp_looproad(id integer, geom geometry);";
			statement += "create unique index tb_temp_looproad_id on tb_temp_looproad(id) ;";
			statement += "create index tb_temp_looproad_geom on tb_temp_looproad using gist (geom); ";
			statement += " insert into tb_temp_looproad(id, geom)" +
									" select id, geom" +
									" from " +
										""+SCHEMA+"."+TBNAME+"" +
									" where " +
										"st_intersects(" +
											"st_polygonFromText(" +
												"'Polygon(("+aUpperLeftLngLat.getX() +" "+aLowerRightLngLat.getY()  +","+
															 aLowerRightLngLat.getX()+" "+ aLowerRightLngLat.getY() +","+
															 aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()   +","+
															 aUpperLeftLngLat.getX() +" "+aUpperLeftLngLat.getY()   +","+
															 aUpperLeftLngLat.getX() +" "+aLowerRightLngLat.getY()  +"))'," +
												""+WGS84_EPSG_CODE+")," +
											"geom)" +
										" and " +
										" st_isValid(geom);";
										
//			System.out.println("create tmp table "+ statement);
			insertInto(statement);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/** 指定したストロークと接する周回道路WKT形式 */
	public ArrayList<String> touchedLooproadStrings = new ArrayList<>();
	/** 指定したストロークと線で接する周回道路ID. */
	public ArrayList<Integer> areaIdArrayList = new ArrayList<>();
	/** 接する周回道路のジオメトリ */
	public ArrayList<ArrayList<Point2D>> touchedLooproadGeom = new ArrayList<>();
	/*
	 * 一時テーブルからストロークと接する周回道路を取得する
	 * @param aStrokeString WKT形式のストローク
	 */
	public void calcNeighberLooproadFromTmpTableUsingStroke(String aStrokeString){
		touchedLooproadStrings = new ArrayList<>();
		areaIdArrayList = new ArrayList<>();
		touchedLooproadGeom = new ArrayList<>();
		try{
			String statement="";
			statement = " select " +
							" id ," +
							" geom, "+
							" st_asText(geom), "+
							" st_dimension(st_intersection(geom, st_geomFromText('"+aStrokeString+"',"+WGS84_EPSG_CODE+"))) as dimension "+
						" from " +
							" tb_temp_looproad " +
						" where " +
						" st_isvalid(geom) "+
						" and " +
						" st_intersects(geom, st_geomFromText('"+aStrokeString+"',"+WGS84_EPSG_CODE+")) ";
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				if(rs.getInt("dimension") == 1){
					areaIdArrayList.add(rs.getInt("id"));
					touchedLooproadStrings.add(rs.getString(3));
					touchedLooproadGeom.add(GeometryParsePostgres.pgGeometryPolygon((PGgeometry)rs.getObject("geom")));
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}

}
