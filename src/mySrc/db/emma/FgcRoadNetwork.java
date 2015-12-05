package mySrc.db.emma;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import mySrc.coordinate.ConvertLngLatXyCoordinate;
import mySrc.coordinate.ConvertMercatorXyCoordinate;
import mySrc.coordinate.GetLngLatOsm;
import mySrc.coordinate.LngLatMercatorUtility;
import mySrc.elastic.ConvertElasticPointGlue;

/**
 * F+G+Cの道路ネットワーク作成
 * @author murase
 *
 */
public class FgcRoadNetwork {
	
	Point2D _centerLngLat;
	Point2D _upperLeftLngLat;
	Point2D _lowerRightLngLat;
	int _foucsZoomLevel;
	int _contextZoomLevel;
	int _glueInnerRadius;
	int _glueOuterRadius;
	int _focusScale;
	int _contextScale;
	Point _windowSize;
	
	public FGC_road _fgcGlueRoad;
	public FGC_road _fgc_road;
	
	public FgcRoadNetwork(Point2D aCenterLngLat, Point2D aUpperLeftLngLat, Point2D aLowerRightLngLat, int aFocusZoomLevel, int aContextZoomLevel,
			int aGlueInnerRadius, int aGlueOuterRadius, int aFoucsScale, int aContextScale, Point aWindowSize){
		_centerLngLat = aCenterLngLat;
		_upperLeftLngLat = aUpperLeftLngLat ;
		_lowerRightLngLat = aLowerRightLngLat ;
		_foucsZoomLevel = aFocusZoomLevel ;
		_contextZoomLevel = aContextZoomLevel ;
		_glueInnerRadius = aGlueInnerRadius ;
		_glueOuterRadius = aGlueOuterRadius;
		_focusScale = aFoucsScale ;
		_contextScale = aContextScale ;
		_windowSize = aWindowSize ;
		 
	}
	
	
	public void createFgcRoadNetwork(String type){
		//focus用の緯度経度xy変換
		GetLngLatOsm getLngLatOsmFocus = new GetLngLatOsm(_centerLngLat, _foucsZoomLevel, _windowSize);
		ConvertLngLatXyCoordinate convertFocus = new ConvertLngLatXyCoordinate((Point2D.Double)getLngLatOsmFocus._upperLeftLngLat,
				(Point2D.Double)getLngLatOsmFocus._lowerRightLngLat, _windowSize);
		//context用の緯度経度xy変換
		GetLngLatOsm getLngLatOsmContext = new GetLngLatOsm(_centerLngLat, _contextZoomLevel, _windowSize);
		ConvertLngLatXyCoordinate convertContext = new ConvertLngLatXyCoordinate((Point2D.Double)getLngLatOsmContext._upperLeftLngLat,
				(Point2D.Double)getLngLatOsmContext._lowerRightLngLat, _windowSize);
		double glueInnerRadiusMeter = _glueInnerRadius*convertFocus.meterPerPixel.getX();
		double glueOuterRadiusMeter = _glueOuterRadius*convertContext.meterPerPixel.getX();
		// contextでのメルカトル座標系xy変換.
		ConvertMercatorXyCoordinate convertMercator = new ConvertMercatorXyCoordinate(
						LngLatMercatorUtility.ConvertLngLatToMercator((Point2D.Double)getLngLatOsmContext._upperLeftLngLat), 
						LngLatMercatorUtility.ConvertLngLatToMercator((Point2D.Double)getLngLatOsmContext._lowerRightLngLat), _windowSize);
		// glueのxy変換.
		ConvertElasticPointGlue convertXyGlue = new ConvertElasticPointGlue(_glueInnerRadius, _glueOuterRadius, glueInnerRadiusMeter, glueOuterRadiusMeter
				, _focusScale, _contextScale, _centerLngLat, convertFocus, convertContext, convertMercator);
		
		// focusのみ.
		FGC_road fgc_FocusRoad = new FGC_road();
		fgc_FocusRoad.startConnection();
		fgc_FocusRoad.getFoucsRoad(_centerLngLat, glueInnerRadiusMeter, convertFocus);
		fgc_FocusRoad.endConnection();
		// contextのみ.
		FGC_road fgc_ContextRoad = new FGC_road();
		fgc_ContextRoad.startConnection();
		fgc_ContextRoad.getContextRoad(_centerLngLat, glueOuterRadiusMeter, _upperLeftLngLat, _lowerRightLngLat, convertContext);
		fgc_ContextRoad.endConnection();
		// glue道路取得.
		_fgcGlueRoad = new FGC_road();
		_fgcGlueRoad.startConnection();
		if(type.equals("DrawGlue_v2")){
			_fgcGlueRoad.getGlueRoad(_centerLngLat, _focusScale, _contextScale, _glueInnerRadius, _glueOuterRadius, 
					glueOuterRadiusMeter, convertXyGlue, convertContext);
		}else if(type.equals("DrawMitinariSenbetuAlgorithm")){
			_fgcGlueRoad.getGlueRoad_link(_centerLngLat, _focusScale, _contextScale, _glueInnerRadius, _glueOuterRadius, 
					glueOuterRadiusMeter, convertXyGlue, convertContext, "DrawMitinariSenbetuAlgorithm");
		}else if(type.equals("DrawElasticRoad")){
			_fgcGlueRoad.getGlueRoad_link(_centerLngLat, _focusScale, _contextScale, _glueInnerRadius, _glueOuterRadius, 
					glueOuterRadiusMeter, convertXyGlue, convertContext, "DrawElasticRoad");
		}else{
			System.out.println("typeパラメータが正しくありません");
		}
		_fgcGlueRoad.endConnection();
		// focus,glue,context.
		_fgc_road = new FGC_road();
		_fgc_road.startConnection();
		_fgc_road.creatTmpRouteTable();
		_fgc_road.insertTmpTable(fgc_FocusRoad._linkId, fgc_FocusRoad._sourceId, fgc_FocusRoad._targetId, fgc_FocusRoad._clazz, fgc_FocusRoad._length, fgc_FocusRoad._cost,fgc_FocusRoad._link);
		_fgc_road.insertTmpTable(fgc_ContextRoad._linkId, fgc_ContextRoad._sourceId, fgc_ContextRoad._targetId, fgc_ContextRoad._clazz, fgc_ContextRoad._length, fgc_ContextRoad._cost,fgc_ContextRoad._link);
		_fgc_road.insertTmpTable(_fgcGlueRoad._linkId, _fgcGlueRoad._sourceId, _fgcGlueRoad._targetId, _fgcGlueRoad._clazz, _fgcGlueRoad._length, _fgcGlueRoad._cost,_fgcGlueRoad._link);
		_fgc_road.insertFgcRoadData();
		_fgc_road._linkPoint = new ArrayList<>();	// xy座標の道路データ(linkのインデックスが使えない).
		_fgc_road._linkPoint.addAll(fgc_FocusRoad._linkPoint);
		_fgc_road._linkPoint.addAll(fgc_ContextRoad._linkPoint);
		_fgc_road._linkPoint.addAll(_fgcGlueRoad._linkPoint);
		_fgc_road._idXyHashMap = new HashMap<>();	// ノードIDとノードの位置(ｘｙ)を紐付したデータ.
		_fgc_road._idXyHashMap.putAll(fgc_FocusRoad._idXyHashMap);
		_fgc_road._idXyHashMap.putAll(fgc_ContextRoad._idXyHashMap);
		_fgc_road._idXyHashMap.putAll(_fgcGlueRoad._idXyHashMap);
	}
	
}
