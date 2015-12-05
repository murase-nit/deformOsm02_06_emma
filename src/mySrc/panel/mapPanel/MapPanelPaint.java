package mySrc.panel.mapPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import mySrc.coordinate.ConvertLngLatXyCoordinate;
import mySrc.db.emma.*;

/**
 * 地図上に描画するクラス
 * @author murase
 *
 */
public class MapPanelPaint {
	private MapPanel _mapPanel;
	private Graphics _mapPanelGraphics;
	private Graphics2D _mapPanelGraphics2d;
	private int _markerSize;
	private ConvertLngLatXyCoordinate _convert;

	public MapPanelPaint(MapPanel aMapPanel) {
		_mapPanel = aMapPanel;
	}
	
	// 初期処理.
	public void init(int aMarkerSize, Graphics aG, ConvertLngLatXyCoordinate aConvert){
		_markerSize = aMarkerSize;
		_mapPanelGraphics = aG;
		_mapPanelGraphics2d = (Graphics2D)aG;
		_convert = aConvert;
	}
	
	// 地図の描画.
	public void paintMap(Image image, BufferedImage bufferedImage){
			//_mapPanelGraphics2d.drawImage(image,0,0,_mapPanel);	// 地図の再描画?.
			_mapPanelGraphics2d.drawImage(bufferedImage, 0, 0, _mapPanel);
	}
	// 中心座標の描画.
	public void paintCenterPoint(Point2D lnglat, ConvertLngLatXyCoordinate _convert){
		Point point = _convert.convertLngLatToXyCoordinate((Point2D.Double)lnglat);
		Rectangle2D.Double rectangle = new Rectangle2D.Double(
				point.x - _markerSize/2/2,
				MapPanel.WINDOW_HEIGHT - point.y - _markerSize/2/2,
				_markerSize/2,
				_markerSize/2);
		_mapPanelGraphics2d.setPaint(Color.green);
		_mapPanelGraphics2d.fill(rectangle);
		_mapPanelGraphics2d.setPaint(Color.black);
		_mapPanelGraphics2d.draw(rectangle);
	}
	// 道路データの描画.
	public void paintRoadData(boolean _roadDataFlg, ArrayList<Line2D> _link){
		if(_roadDataFlg){
			for(Line2D line2d : _link){
				paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(line2d),
					Color.black, (float)_markerSize/3);
				// 点の描画.
				paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(line2d.getP1()), Color.black, 6);
				paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(line2d.getP2()), Color.black, 6);
			}
		}
	}
	// 道路データの描画.
	public void paintRoadData2(boolean _roadData2Flg, ArrayList<ArrayList<Line2D>> _arc){
		if(_roadData2Flg){
			for(ArrayList<Line2D> arrArc : _arc){
				for(Line2D arc : arrArc){
					paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(arc),
						Color.black, (float)_markerSize/3);
					// 点の描画.
					paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(arc.getP1()), Color.black, 6);
					paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(arc.getP2()), Color.black, 6);
				}
			}
		}
	}

	
	// 施設データの描画.
	public void paintShopData(boolean _markFlg, ArrayList<Point2D> _lnglatDataArrayList){
		if(_markFlg){
			for(Point2D point2d: _lnglatDataArrayList){
				paint2dEllipse(_convert.convertLngLatToXyCoordinate((Point2D.Double)point2d), Color.red, _markerSize);
			}
		}
	}
	
	// SOAストロークの描画
	public void paintSOAStroke(
			boolean _strokeGeomFlg1, ArrayList<ArrayList<Line2D>> _strokeGeom, 
			ArrayList<Integer> _orderedStrokeIndexArrayList, int _soaThreshold){
		if(_strokeGeomFlg1){
			for(int i=0; i<_soaThreshold; i++){
				for(int j=0; j<_strokeGeom.get(_orderedStrokeIndexArrayList.get(i)).size(); j++){
					// 線の描画.
					paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(_strokeGeom.get(_orderedStrokeIndexArrayList.get(i)).get(j)), Color.black, (float)_markerSize/3);
					// 点の描画.
					paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(_strokeGeom.get(_orderedStrokeIndexArrayList.get(i)).get(j).getP1()), Color.black, (int)_markerSize/4);
					paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(_strokeGeom.get(_orderedStrokeIndexArrayList.get(i)).get(j).getP2()), Color.black, (int)5);
				}
			}
		}
	}

	// test.
	public void paintPolygon(ArrayList<ArrayList<ArrayList<Point2D>>> _testOsmLooproad, ArrayList<ArrayList<Line2D>> stroke, boolean _testFlg, int _soaThreshold, ArrayList<Integer> shopNum){
		if(_testFlg){
			System.out.println("shopNum;"+shopNum.get(_soaThreshold));
			for(int i=0; i<_testOsmLooproad.get(_soaThreshold).size(); i++){
				paintPolygon(_convert.convertLngLatToXyCoordinate(_testOsmLooproad.get(_soaThreshold).get(i)));
			}
			for(int i=0; i<stroke.get(_soaThreshold).size(); i++){
				paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(stroke.get(_soaThreshold).get(i)), Color.black, 5.0f);
			}
		}
	}
	
	// SOA3ストロークの描画
	public void paintSOA3Stroke(
			boolean _soa3Flg, ArrayList<ArrayList<Line2D>> _strokeGeom, 
			ArrayList<Integer> _orderedStrokeIndexArrayList, int _soaThreshold){
		if(_soa3Flg){
			for(int i=0; i<_soaThreshold; i++){
				for(int j=0; j<_strokeGeom.get(_orderedStrokeIndexArrayList.get(i)).size(); j++){
					// 線の描画.
					paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(_strokeGeom.get(_orderedStrokeIndexArrayList.get(i)).get(j)), Color.black, (float)5);
					// 点の描画.
//					paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(_strokeGeom.get(_orderedStrokeIndexArrayList.get(i)).get(j).getP1()), Color.black, (int)_markerSize/4);
//					paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(_strokeGeom.get(_orderedStrokeIndexArrayList.get(i)).get(j).getP2()), Color.black, (int)5);
				}
			}
		}
	}

	
	// 道路データの描画.
	public void paintRoadClass(boolean _roadDataFlg, ArrayList<ArrayList<Line2D>> _link){
		if(_roadDataFlg){
			for(ArrayList<Line2D> seg : _link){
				for(Line2D line2d : seg){
					paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(line2d),
						Color.black, (float)_markerSize/3);
					// 点の描画.
					paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(line2d.getP1()), Color.black, 6);
					paint2dEllipse((Point2D)_convert.convertLngLatToXyCoordinate(line2d.getP2()), Color.black, 6);
				}
			}
		}
	}
	
	// pinの画像を表示とその周辺のストローク表示.
	public void paintPin(ArrayList<Point> aPin, boolean aDrawPinFlg, ArrayList<ArrayList<ArrayList<Line2D>>> _concatStroke){
		if(aDrawPinFlg){
			BufferedImage bufferedImage = null;
			try{
				bufferedImage = ImageIO.read(new File("../img/pin.png"));
			}catch(Exception e){
				e.printStackTrace();
			}
			for(int i=0; i<aPin.size(); i++){
				_mapPanelGraphics2d.drawImage(bufferedImage, aPin.get(i).x-5, aPin.get(i).y-55, _mapPanel);
			}
		}
	}
//	public void paintPinText(ArrayList<ArrayList<ArrayList<Line2D>>>_concatStroke, boolean _drawPinFlg){
//		if(_drawPinFlg){
//			for(int i=0; i<_concatStroke.size(); i++){
//				for(int j=0; j<_concatStroke.get(i).size() && j<2; j++){
//					for(int k=0; k<_concatStroke.get(i).get(j).size(); k++){
//						paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(_concatStroke.get(i).get(j).get(k)), Color.black, 5);
//					}
//				}
//			}
//		}
//	}
	
	
	public void paintCommonRoad(boolean _commonFlg, ArrayList<ArrayList<Line2D>> _commonRoad){
		if(_commonFlg){
			for(ArrayList<Line2D> oneRoad: _commonRoad){
				for(Line2D seg : oneRoad){
					paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(seg), Color.black, (float)_markerSize/3);
				}
			}
		}
	}
	
	// focus,glueの描画.
	public void paintFoucsGlue(BufferedImage _focusImage, BufferedImage _glueImage, boolean _fgImageFlg){
		if(_fgImageFlg){
			Shape oldClip = _mapPanelGraphics2d.getClip();
			_mapPanelGraphics2d.setClip(new Ellipse2D.Double(MapPanel.WINDOW_WIDTH/2-_glueImage.getWidth()/2, MapPanel.WINDOW_WIDTH/2-_glueImage.getWidth()/2, _glueImage.getWidth() ,_glueImage.getHeight()));
			_mapPanelGraphics2d.drawImage(_glueImage, MapPanel.WINDOW_WIDTH/2-_glueImage.getWidth()/2, MapPanel.WINDOW_WIDTH/2-_glueImage.getWidth()/2 ,_mapPanel);
			_mapPanelGraphics2d.setClip(new Ellipse2D.Double(MapPanel.WINDOW_WIDTH/2-_focusImage.getWidth()/2, MapPanel.WINDOW_WIDTH/2-_focusImage.getWidth()/2, _focusImage.getWidth() ,_focusImage.getHeight()));
			_mapPanelGraphics2d.drawImage(_focusImage, MapPanel.WINDOW_WIDTH/2-_focusImage.getWidth()/2, MapPanel.WINDOW_WIDTH/2-_focusImage.getWidth()/2 ,_mapPanel);
			_mapPanelGraphics.setClip(oldClip);
		}
	}
	
	// fgcの道路の描画
	public void paintFGCRoad(FGC_road _fgc_road,boolean _fgcRoadFlg){
		if(_fgcRoadFlg){
			for(int i=0; i<_fgc_road._linkPoint.size(); i++){
				paint2dLine(_fgc_road._linkPoint.get(i), Color.black, 3);
				paint2dEllipse(_fgc_road._linkPoint.get(i).getP1(), Color.black, 5);
				paint2dEllipse(_fgc_road._linkPoint.get(i).getP2(), Color.black, 5);
			}
		}
	}
	
	
	// 経路探索の結果の描画.
	public void paintRouting(ArrayList<Point2D>_routingResult, boolean _routingFlg){
		if(_routingFlg){
			for(int i=0; i<_routingResult.size()-1; i++){
				//paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(_routingResult.get(i)), Color.green, 5);
				paint2dLine(_routingResult.get(i), _routingResult.get(i+1), Color.green, 5);
			}
		}
	}
	// 複数　経路探索の結果の描画.
	public void paintMultiRouting(ArrayList<ArrayList<Point2D>>_routingResult, boolean _routingFlg){
		if(_routingFlg){
			for(int i=0; i<_routingResult.size(); i++){
				for(int j=0; j<_routingResult.get(i).size()-1; j++){
					//paint2dLine(_convert.convertLngLatToXyCoordinateLine2D(_routingResult.get(i)), Color.green, 5);
					if(_routingResult.get(i).get(j) == null || _routingResult.get(i).get(j+1) == null) continue;
					paint2dLine(_routingResult.get(i).get(j), _routingResult.get(i).get(j+1), Color.green, 5);
					if(j==0){
						paint2dEllipse(_routingResult.get(i).get(j), Color.pink, 5);
					}else if(j==_routingResult.get(i).size()-2){
						paint2dEllipse(_routingResult.get(i).get(j+1), Color.pink, 5);
					}
				}
			}
		}
	}

	
	/////////////////////////////////////////////////////
	/////////////////////////////////////////////////////
	// 直線.
	private void paint2dLine(Point2D aPoint1, Point2D aPoint2, Color aColor, float aLineWidth){
		Line2D.Double linkLine = new Line2D.Double(aPoint1, aPoint2);
		// 線の幅.
		BasicStroke wideStroke = new BasicStroke(aLineWidth);
		_mapPanelGraphics2d.setStroke(wideStroke);
		_mapPanelGraphics2d.setPaint(aColor);
		_mapPanelGraphics2d.draw(linkLine);

	}
	private void paint2dLine(Line2D aLine, Color aColor, float aLineWidth){
		Line2D linkLine = aLine;
		// 線の幅.
		BasicStroke wideStroke = new BasicStroke(aLineWidth);
		_mapPanelGraphics2d.setStroke(wideStroke);
		_mapPanelGraphics2d.setPaint(aColor);
//		_mapPanelGraphics2d.setPaint(new Color(0, 0, 0, 64));
		_mapPanelGraphics2d.draw(linkLine);
	}
	// 円.
	private void paint2dEllipse(Point2D aCenterPointDouble, Color aColor, int aMarkerSize){
		_mapPanelGraphics2d.setPaint(aColor);
		Ellipse2D.Double ellipse = new Ellipse2D.Double(aCenterPointDouble.getX() - aMarkerSize/2,
				aCenterPointDouble.getY() - aMarkerSize/2, aMarkerSize, aMarkerSize);
		_mapPanelGraphics2d.fill(ellipse);	// 内部塗りつぶし.
		BasicStroke wideStroke = new BasicStroke(1.0f);
		_mapPanelGraphics2d.setStroke(wideStroke);
		_mapPanelGraphics2d.setPaint(Color.black);
		_mapPanelGraphics2d.draw(ellipse);	// 輪郭の描画.

	}
	// 多角形.
	private void paintPolygon(ArrayList<Point> aPointArrayList){
		int[] xPoints = new int[aPointArrayList.size()];
		int[] yPoints = new int[aPointArrayList.size()];
		
		for(int i=0; i<aPointArrayList.size(); i++){
			xPoints[i] = aPointArrayList.get(i).x;
			yPoints[i] = aPointArrayList.get(i).y; 
		}
		
		Polygon polygon = new Polygon(xPoints, yPoints, xPoints.length);
		_mapPanelGraphics2d.setPaint(Color.black);
		_mapPanelGraphics2d.draw(polygon);
		_mapPanelGraphics2d.setPaint(new Color(0, 0, 0, 64));
		_mapPanelGraphics2d.fill(polygon);
	}
}
