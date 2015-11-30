package mySrc.yahooAPI;

import java.awt.geom.Point2D;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


// YahooコンテンツジオコーダーAPIを使ってキーワードから緯度経度の座標を取得などに関するクラス.
// HTTPリクエストのレスポンスはXMLデータ.
public class ContentsGeocorder {
	
	// YahooコンテンツジオコーダーAPI　　HTTPリクエスト.
	private static final String HOST_NAME = "http://contents.search.olp.yahooapis.jp/OpenLocalPlatform/V1/contentsGeoCoder?";
	private static final String PARAM_QUERY ="&query=";
	private static final String PARAM_CATEGORY ="&category=";
	private static final String PARAM_APPID ="&appid=";
	private static final String APPID = "dj0zaiZpPWFheWx0TmV3WGxvOSZkPVlXazllRlkzYzNka05XRW1jR285TUEtLSZzPWNvbnN1bWVyc2VjcmV0Jng9ODQ-";
	
	private Point2D.Double _lnglat;	// 座標(日本測地系).
	private String _locationName;
	
	// 引数 : 入力文字　　検索対象カテゴリ(address:住所 landmark:ランドマーク).
	public ContentsGeocorder (String aLocationName, String aSelectedType) {
		
		String uri = HOST_NAME +
				PARAM_QUERY+ aLocationName +
				PARAM_CATEGORY+ aSelectedType +
				PARAM_APPID+APPID;
		
		System.out.println(uri);
		
		try {
			// DOMを使用するために新しいインスタンスを生成する.
		     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		     //Documentインスタンス用factoryを生成する.
		     DocumentBuilder builder = factory.newDocumentBuilder();
		     // 解析対象ファイルをDocumentクラスのオブジェクトにする.
		     Document doc = builder.parse(uri);
		     // DocumentオブジェクトからXMLの最上位のタグであるルートタグを取得する.
		     Element root = doc.getDocumentElement();
		     
		     // 名前の取得.
		     NodeList nodeList_1 = root.getElementsByTagName("Feature");
		     Element element_1 = (Element)nodeList_1.item(0);
		     this._locationName = getChildVal(element_1, "Name");
		     System.out.println(this._locationName);
		     
		     // 座標の取得.
		     nodeList_1 = root.getElementsByTagName("Geometry");
		     element_1 = (Element)nodeList_1.item(0);
		     String coordinates = getChildVal(element_1, "Coordinates");
		     String splitLnglat[] = coordinates.split(",");
		     //System.out.println("coord"+coordinates);
		     this._lnglat = new Point2D.Double(Double.parseDouble(splitLnglat[0]),Double.parseDouble(splitLnglat[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		     
// 	// 子ノードのエレメント取得.
// 	private Element getChildElement(Element element, String tagName){
// 		NodeList list = element.getElementsByTagName(tagName);
// 		Element cElement = (Element)list.item(0);
// 		return cElement;
// 	}
 	
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
	
	public Point2D.Double getLnglatValue() {
 		return this._lnglat;  
 	}
	public String getLocationNameValue(){
		return this._locationName;
	}
}
