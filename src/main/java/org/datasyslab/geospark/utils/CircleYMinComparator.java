/*
 * 
 */
package org.datasyslab.geospark.utils;

import com.vividsolutions.jts.geom.Envelope;

import org.datasyslab.geospark.geometryObjects.Circle;

import java.io.Serializable;
import java.util.Comparator;

// TODO: Auto-generated Javadoc
/**
 * Created by GeoSpark Team on 9/29/15.
 */
public class CircleYMinComparator extends GeometryComparator implements Comparator<Circle>, Serializable {

	 /* (non-Javadoc)
 	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
 	 */
 	public int compare(Circle circle1, Circle circle2) {
		 Envelope envelope1=circle1.getMBR();
		 Envelope envelope2=circle1.getMBR();
	    if(envelope1.getMinY()>envelope2.getMinY())
	    {
	    	return 1;
	    }
	    else if (envelope1.getMinY()<envelope2.getMinY())
	    {
	    	return -1;
	    }
	    else return 0;
	    }
}