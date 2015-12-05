package mySrc.db.emma;

import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * xmlのパース
 * http://133.68.13.112:8080/EmmaGlueMuraseOriginal/MainServlet?type=DrawGlue_v2&centerLngLat=136.9321346282959,35.15752496315493&focus_zoom_level=16&context_zoom_level=14&glue_inner_radius=125&glue_outer_radius=200&roadType=car&option=vector
 * @author murase
 *
 */
public class GlueRoadXml_link {
	
	/** ストロークのID */
	public ArrayList<Integer> _linkId = new ArrayList<>();
	/** ストロークの変形後の座標(xy座標(glueの左上が(0,0)) */
	public ArrayList<ArrayList<Point2D>> _linkPoint = new ArrayList<>();
	
	public GlueRoadXml_link(Point2D aCenterLngLat,int aFoucsZoomLevel, int aContextZoomLevel, int aGlueInnerRadius, int aGlueOuterRadius, String type){
		_linkId = new ArrayList<>();
		_linkPoint = new ArrayList<>();
		
		String uri = "http://133.68.13.112:8080/EmmaGlueMuraseOriginal/MainServlet?type="+ type +
				"&centerLngLat="+aCenterLngLat.getX()+","+aCenterLngLat.getY()+
				"&focus_zoom_level="+aFoucsZoomLevel+
				"&context_zoom_level="+ aContextZoomLevel +
				"&glue_inner_radius=" +aGlueInnerRadius+
				"&glue_outer_radius=" +aGlueOuterRadius+
				"&option=vector";
		System.out.println("glueの選択されたベクタ道路データ"+uri);
		try {
			// DOMを使用するために新しいインスタンスを生成する.
		     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		     //Documentインスタンス用factoryを生成する.
		     DocumentBuilder builder = factory.newDocumentBuilder();
		     // 解析対象ファイルをDocumentクラスのオブジェクトにする.
		     Document doc = builder.parse(uri);
		     // DocumentオブジェクトからXMLの最上位のタグであるルートタグを取得する.
		     Element root = doc.getDocumentElement();
		     
		     NodeList nodeList_1 = root.getElementsByTagName("oneLink");
		     for(int i=0; i<nodeList_1.getLength(); i++){
		    	Element element = (Element)nodeList_1.item(i);
		    	//System.out.println(getChildVal(element, "selectedStrokeId").trim());
		    	_linkId.add(Integer.parseInt(getChildVal(element, "selectedLinkId").trim()));	// ストロークID取得.
		    	NodeList nodeList_2 = element.getElementsByTagName("xy");
		    	ArrayList<Point2D> tmp = new ArrayList<>();
		    	for(int j=0;j<nodeList_2.getLength(); j++){
		    		Element element2 = (Element)nodeList_2.item(j);
//		    		System.out.println(element2.getTextContent());
		    		tmp.add(new Point2D.Double(
		    				Double.parseDouble(element2.getTextContent().split(",")[0]),
		    				Double.parseDouble(element2.getTextContent().split(",")[1])
		    				));
		    	}
		    	_linkPoint.add(tmp);	// 追加.
		     }

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
}
