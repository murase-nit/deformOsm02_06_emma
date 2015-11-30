package mySrc.yahooAPI;

import java.io.*;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * YahooローカルサーチAPIのリクエストをし、受け取ったXMLデータから必要なデータを取り出す.
 * @author murase
 *
 */
public class LocalSearch {
	// yahooローカルサーチAPIのリクエスト.
	private static final String HOST_NAME = "http://search.olp.yahooapis.jp/OpenLocalPlatform/V1/localSearch?";
	private static final String PARAM_LNG = "lon=";			// 円形範囲検索で使用.
	private static final String PARAM_LAT = "&lat=";		// 円形範囲検索で使用.
	private static final String PARAM_GC = "&gc=";
	private static final String PARAM_DIST = "&dist=";		// 円形範囲検索で使用.
	private static final String PARAM_START = "&start=";
	private static final String PARAM_RESULT = "&results=";
	private static final String PARAM_SORT = "&sort=";
	private static final String PARAM_APPID = "&appid=";
	private static final String APPID = "dj0zaiZpPWFheWx0TmV3WGxvOSZkPVlXazllRlkzYzNka05XRW1jR285TUEtLSZzPWNvbnN1bWVyc2VjcmV0Jng9ODQ-";
	private static final String PARAM_BBOX_STRING ="&bbox=";		// 矩形範囲検索で使用.
	
	//private Point2D.Double latlngArray[] = new Point2D.Double[3001];	// 緯度経度(日本座標系で格納されている).
	private ArrayList<Point2D.Double> latlngArrayList = new ArrayList<Point2D.Double>();
	//private String nameArray[] = new String[3001];	// 名前の配列.
	private ArrayList<String> nameArrayList = new ArrayList<String>();
	private int infoNum = 0;	// 1回のリクエストで取得した建物ン数.
	private int totalInfoNum = 0;	// すべての建物の数.
	private int tmpTotalInfoNum = 0; // 取得したときの？.
	private String _groupCode;	// httpリクエストのために使うパラメータ　グループコード.
	private String _radius;		// httpリクエストのために使うパラメータ　指定範囲.
	
	/**
	 *　コンストラクタ　円形範囲の周辺建物を検索
	 * @param lng 日本測地系中心経度
	 * @param lat 日本測地系中心緯度
	 * @param aGroupCode グループコード
	 * @param aRadius 指定半径
	 */
	public LocalSearch(String lng, String lat, String aGroupCode, String aRadius){	// コンストラクタ.
//		latlngArray = new Point2D.Double[3001];
//		nameArray = new String[3001];
		infoNum = 0;
		_groupCode = aGroupCode;
		_radius = aRadius;
		
		//.
		insertXmlData(0, getRequestUrlCircle(lng, lat, 0));
		totalInfoNum = tmpTotalInfoNum;
		// 取得件数が100件以上のときは複数回リクエストを送る.
		if (totalInfoNum > 100) {
			int requestNum = (int)totalInfoNum/100;
			for (int i=0; i<requestNum && i<29; i++) {
				insertXmlData((i+1)*100, getRequestUrlCircle(lng, lat, (i+1)*100));
			}
		}
	}
	
	/**
	 * コンストラクタ　　矩形範囲の周辺建物を検索
	 * @param aLeftLowerLng
	 * @param aLeftLowerLat
	 * @param aRightUpperLng
	 * @param aRightUpperLat
	 * @param aGroupCode
	 */
	public LocalSearch(Point2D.Double aLeftLower, Point2D.Double aRightUpper, String aGroupCode){
		//latlngArray = new Point2D.Double[3001];
		//nameArray = new String[3001];
		infoNum = 0;
		_groupCode = aGroupCode;
		
		insertXmlData(0, getRequestUrlRectangle(aLeftLower, aRightUpper, 0));
		totalInfoNum = tmpTotalInfoNum;
		if (totalInfoNum > 100) {
			int requestNum = (int)totalInfoNum/100;
			for (int i=0; i<requestNum && i<29; i++) {
				insertXmlData((i+1)*100, getRequestUrlRectangle(aLeftLower, aRightUpper, (i+1)*100));
			}
		}
	}
	
	// 格納される変数　latlngArray[] nameArray[] infoNum.
	// 日本測地系の座標.
	/**
	 * xmlファイルのデータをメンバ変数へ格納.
	 * @param lng	日本測地系緯度経度
	 * @param lat	日本測地系緯度経度
	 * @param start	検索位置.
	 */
	public void insertXmlData(int start, String aRequestUrl) {
		//String[] nameList = new String[100];// 一回のリクエストで取得した建物の名前.
		//Point2D.Double[] lnglatList = new Point2D.Double[100];	// 一回のリクエストで取得した建物の座標.
		ArrayList<String>nameList = new ArrayList<String>();// 一回のリクエストで取得した建物の名前.
		ArrayList<Point2D.Double> lnglatList = new ArrayList<Point2D.Double>();// 一回のリクエストで取得した建物の座標.
		
		// リクエストURLは世界測地系?.
		String uri = aRequestUrl;
		try {
			// DOMを使用するために新しいインスタンスを生成する.
		     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		     //Documentインスタンス用factoryを生成する.
		     DocumentBuilder builder = factory.newDocumentBuilder();
		     // 解析対象ファイルをDocumentクラスのオブジェクトにする.
		     Document doc = builder.parse(uri);
		     // DocumentオブジェクトからXMLの最上位のタグであるルートタグを取得する.
		     Element root = doc.getDocumentElement();
		     
		     // 取得したデータ数を求める.
		     NodeList nodeList_resultInfo = root.getElementsByTagName("ResultInfo");
		     Element element_resultInfo = (Element)nodeList_resultInfo.item(0);
		     infoNum = Integer.parseInt(getChildVal(element_resultInfo, "Count"));
		     tmpTotalInfoNum = Integer.parseInt(getChildVal(element_resultInfo, "Total"));
		     
		     NodeList nodeList_feature = root.getElementsByTagName("Feature");
		     for (int i=0; i<nodeList_feature.getLength(); i++) {
		    	 Element element_feature = (Element)nodeList_feature.item(i);
		    	 String name = getChildVal(element_feature, "Name");
		    	 Element element_geometry = getChildElement(element_feature, "Geometry");
		    	 String latlng = getChildVal(element_geometry, "Coordinates");
		    	 
		    	 //nameList[i] = name;	// 建物名の格納.
		    	 nameList.add(name);
		    	 String splitLatlng[] = latlng.split(",");
		    	 
		    	 lnglatList.add(new Point2D.Double(Double.parseDouble(splitLatlng[0]),Double.parseDouble(splitLatlng[1])));
		     }
		     
		     // 全体の配列を格納.
		     //System.arraycopy(nameList, 0, this.nameArray, start, nameList.length);
		     //System.arraycopy(lnglatList, 0, this.latlngArray, start, lnglatList.length);
		     this.nameArrayList.addAll(nameList);
		     this.latlngArrayList.addAll(lnglatList);
		      
		    } catch (ParserConfigurationException e) {
		      e.printStackTrace();
		    } catch (SAXException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }

	}
	
	// 子ノードのエレメント取得.
	private Element getChildElement(Element element, String tagName){
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element)list.item(0);
		return cElement;
	}
	
	 /**
	   * 指定されたエレメントから子要素の内容を取得。
	   * 
	   * @param   element 指定エレメント
	   * @param   tagName 指定タグ名
	   * @return  取得した内容
	   */
	  private String getChildVal(Element element, String tagName) {
	    NodeList list = element.getElementsByTagName(tagName);
	    Node cElement = list.item(0);
	    //System.out.println(cElement);
	    return cElement.getFirstChild().getNodeValue();
	  }
	  
	  /**
	   * リスエストURLの生成
	   * @param lng
	   * @param lat
	   * @param start
	   * @return
	   */
	  private String getRequestUrlCircle(String lng, String lat, int start){
		  //System.out.println(lng);
		  // 世界測地系にしてリクエストする.
		  String requestUrl = HOST_NAME +
				  PARAM_LNG + Double.parseDouble(lng) +
				  PARAM_LAT + Double.parseDouble(lat) +
				  PARAM_GC + _groupCode +
				  PARAM_DIST + _radius +
				  PARAM_START + start +
				  PARAM_RESULT + "100" +
				  PARAM_SORT + "score" +
				  PARAM_APPID + APPID;
		  System.out.println("resuest: "+requestUrl);
		  return requestUrl;
	  }
	  
	  /**
	   * リクエストURLの生成(矩形検索)
	   * @param aLeftLower
	   * @param aRightUpper
	   * @param start
	   * @return
	   */
	  private String getRequestUrlRectangle(Point2D.Double aLeftLower, Point2D.Double aRightUpper, int start){
		  // 世界測地系にしてリクエストする.
		  String requestUrl = HOST_NAME +
				  PARAM_BBOX_STRING +
				  aLeftLower.x + "," +
				  aLeftLower.y + "," + 
				  aRightUpper.x + "," +
				  aRightUpper.y +
				  PARAM_GC + _groupCode +
				  PARAM_START + start +
				  PARAM_RESULT + "100" +
				  PARAM_SORT + "score" +
				  PARAM_APPID + APPID;
		  System.out.println("resuest: "+requestUrl);
		  return requestUrl;
	  }
	  
//	public String[] getNameArrayValue(){
//		return this.nameArray;
//	}
//	public Point2D.Double[] getlatlngArrayValue(){
//		return this.latlngArray;
//	}
	  public ArrayList<String> getNameArrayList(){
		  return this.nameArrayList;
	  }
	  public ArrayList<Point2D.Double> getLatLngArrayList(){
		  return this.latlngArrayList;
	  }
	  public int getTotalInfoNumValue(){
		  //int totalNum = (totalInfoNum>3000)? 3000: totalInfoNum+1;	// totalInfoNum+1?? 最後がnullになるようにするため+1している.
		  int totalNum = (totalInfoNum>3000)? 3000: totalInfoNum;
		  System.out.println("totalNum "+ totalNum);
		  //System.exit(0);
		  return totalNum;
	  }
}
