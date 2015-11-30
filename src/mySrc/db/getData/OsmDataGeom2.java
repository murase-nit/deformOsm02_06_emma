package mySrc.db.getData;

import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.postgis.PGgeometry;

import mySrc.db.GeometryParsePostgres;
import mySrc.db.HandleDbTemplateSuper;

/**
 * 施設データに関するクラス
 * @author murase
 *
 */
public class OsmDataGeom2 extends HandleDbTemplateSuper{
	protected static final String DBNAME = "osm_relation_db";	// Database Name
//	protected static final String SCHEMA = "stroke";
	protected static final String USER = "postgres";			// user name for DB.
	protected static final String PASS = "murase";		// password for DB.
	protected static final String URL = "localhost";
	protected static final int PORT = 5432;
	protected static final String DBURL = "jdbc:postgresql://"+URL+":"+PORT+"/" + DBNAME;
	
	// 施設データ
	/** 施設ID */
	public ArrayList<Long> _facilityId = new ArrayList<>();
	/** 施設位置 */
	public ArrayList<Point2D> _facilityLocation = new ArrayList<>();
	/** 施設名 */
	public ArrayList<String> _facilityName = new ArrayList<>();
	/** 施設種別 */
	public ArrayList<String> _facilityType = new ArrayList<>();
	
	public int shopNum = -1;
	
	public OsmDataGeom2() {
		super(DBNAME, USER, PASS, DBURL, HandleDbTemplateSuper.POSTGRESJDBCDRIVER_STRING);
	}
	
	/**
	 * 一時テーブルの作成(表示範囲内のお店テーブル)
	 */
	public void createTempTable(String category, Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat){
		try{
			String statement = "";
			
			statement = " create temp table tb_temp(id integer, name text, location geometry, looproad_id integer, category text); ";
			insertInto(statement);
			
			statement = " " +
					" insert into " +
						" tb_temp(id, name, location, looproad_id, category) " +
							" select " +
								" id, name, location, looproad_id, category" +
							" from " +
								" facility_table " +
							" where " +
								" st_contains(" +
									"st_polygonFromText(" +
										"'Polygon(("+
											aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
											aLowerRightLngLat.getX()+" "+aUpperLeftLngLat.getY()+","+
											aLowerRightLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
											aUpperLeftLngLat.getX()+" "+aLowerRightLngLat.getY()+","+
											aUpperLeftLngLat.getX()+" "+aUpperLeftLngLat.getY()+
										"))'," +
									"4326)," +
								"location)" +
								" and category = '"+category+"'" +";";
			System.out.println(statement);
//			System.exit(0);
			insertInto(statement);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 指定した複数の周回道路IDからお店の情報を取得する
	 */
	public int  searchShopInfoFromAreaIds(ArrayList<Integer> aAreaIds){
		String statement = "";
		_facilityLocation = new ArrayList<>();
		shopNum = 0;
		try{
			// 線で接する周回道路内のお店情報を取得する.
			// 1つのストロークにおけるお店の情報.
			ArrayList<Integer> shopId = new ArrayList<>();
			ArrayList<String> shopName = new ArrayList<>();
			ArrayList<Point2D.Double> shopLngLat = new ArrayList<>();
			if(aAreaIds.size() == 0){	// ストロークに接する周回道路がないときは飛ばす.
				return 0;
			}
			// 表示範囲内のお店だけを取り出して、そこから必要なお店を周回道路IDから求める.
			statement = "" +
					" select " +
						" id, name, st_asText(location) as lnglat" +
					" from " +
						"tb_temp"+
					"";
			statement += " where looproad_id = "+aAreaIds.get(0)+" ";
			for(int j=1; j<aAreaIds.size(); j++){
				statement += " or looproad_id = "+aAreaIds.get(j)+" ";
			}
			System.out.println(statement);
			ResultSet rs = execute(statement);
			while(rs.next()){
				shopId.add(rs.getInt("id"));
				shopName.add(rs.getString("name"));
				shopLngLat.add(GeometryParsePostgres.parsingPoint2d(rs.getString("lnglat")));
				shopNum++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return shopNum;
	}
	
}
