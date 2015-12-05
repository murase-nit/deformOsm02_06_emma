package mySrc.emma;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import mySrc.db.emma.FGC_road;

/**
 * glueの道路の密度を計測する
 * @author murase
 *
 */
public class GlueRoadDensity {
	
	// 入力.
	FGC_road _Fgc_road;
	public int _glueInnerRadius;
	public int _glueOuterRadius;
	
	/** 中心を原点とした座標. */
	ArrayList<Line2D> _xyLink = new ArrayList<>();
	
	
	//求める値.
	/** 各同心円状で交差するリンクの数 */
	public ArrayList<Integer> _intersectEachCircleNum = new ArrayList<>();
	/** その時の半径 */
	public ArrayList<Integer> _eachRadius = new ArrayList<>();
	/** 各方向での交差するリンクの数 */
	public ArrayList<Integer> _intersectEachAngleNum = new ArrayList<>();
	/** その時の角度 */
	public ArrayList<Double> _eachAngle = new ArrayList<>();
	
	
	public GlueRoadDensity(FGC_road aFgc_road, int aGlueInnerRadius, int aGlueOuterRadius){
		_Fgc_road = aFgc_road;
		_glueInnerRadius = aGlueInnerRadius;
		_glueOuterRadius = aGlueOuterRadius;
		
		// 中心を原点とした座標に変換.
		_xyLink = new ArrayList<>();
		for(int i=0; i<_Fgc_road._linkPoint.size(); i++){
			_xyLink.add(new Line2D.Double(
					_Fgc_road._linkPoint.get(i).getX1()-_glueOuterRadius,
					_Fgc_road._linkPoint.get(i).getY1()-_glueOuterRadius,
					_Fgc_road._linkPoint.get(i).getX2()-_glueOuterRadius,
					_Fgc_road._linkPoint.get(i).getY2()-_glueOuterRadius));
		}

	}
	
	/**
	 * 同心円方向の道路の密度を計測
	 */
	public void measureGlueRoadDensitySameCircle(){
		
		for(int radius=_glueInnerRadius; radius<=_glueOuterRadius; radius+=((_glueOuterRadius-_glueInnerRadius)/10)){
			_eachRadius.add(radius);
			int intersectInCircleNum = 0;	// 同心円上に交差するリンクの数.
			for(int i=0; i<_xyLink.size(); i++){
				if(isIntersectsCircle(_xyLink.get(i), radius)){
					intersectInCircleNum++;
				}
			}
			_intersectEachCircleNum.add(intersectInCircleNum);
		}
		System.out.println("同心円方向 :"+_intersectEachCircleNum);
		System.out.println("半径"+_eachRadius);
	}
	
	/**
	 * 放射方向の道路の密度を計測
	 */
	public void measureGlueRoadDensitySameAngle(){
		// 放射方向は水平方向から反時計回りに30度ごとに計測.
		ArrayList<Line2D> eachLine = new ArrayList<>();
		_eachAngle = new ArrayList<>();
		for(double i=0, angle=0; angle<2*Math.PI; i++, angle=(i*Math.PI/6)){
			eachLine.add(new Line2D.Double(0, 0, _glueOuterRadius*Math.cos(angle), _glueOuterRadius*Math.sin(angle)));
			_eachAngle.add(Math.toDegrees(angle));
		}
		
		for(int i=0; i<eachLine.size(); i++){
			int intersectOneLineNum = 0;
			for(int j=0; j<_xyLink.size(); j++){
				if(eachLine.get(i).intersectsLine(_xyLink.get(j))){
					intersectOneLineNum ++;
				}
			}
			_intersectEachAngleNum.add(intersectOneLineNum);
		}
		System.out.println("放射方向: "+_intersectEachAngleNum);
		System.out.println("角度"+_eachAngle);
	}
	
	/**
	 * 指定した線が円をまたぐか
	 * @param line
	 * @param radius
	 * @return
	 */
	public boolean isIntersectsCircle(Line2D line, int radius){
		boolean bool = false;
		if((isInCircle(line.getP1(), radius)==true && isInCircle(line.getP2(), radius) == false)||
				(isInCircle(line.getP1(), radius)==false && isInCircle(line.getP2(), radius) == true)){
			bool = true;
		}
		return bool;
	}
	/**
	 * 指定した点が円の中にあるか
	 * @param point
	 * @param radius
	 * @return
	 */
	public boolean isInCircle(Point2D point, int radius){
		boolean bool = false;
		if(point.distance(0.0, 0.0)<radius){
			bool = true;
		}
		return bool;
	}
//	/**
//	 * 2つの線分が交差するか
//	 * @return
//	 */
//	public boolean isIntersectsTwoLines(Line2D l1, Line2D l2){
//		boolean bool = false;
//		
//	}
	
}
