package org.datasyslab.geospark.spatialOperator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datasyslab.geospark.spatialRDD.PointRDD;
import org.datasyslab.geospark.spatialRDD.PointRDDTest;
import org.datasyslab.geospark.spatialRDD.PolygonRDD;
import org.datasyslab.geospark.spatialRDD.RectangleRDD;
import org.datasyslab.geospark.spatialRDD.RectangleRDDTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;


public class RectangleKnnTest {
    public static JavaSparkContext sc;
    static Properties prop;
    static InputStream input;
    static String InputLocation;
    static Integer offset;
    static String splitter;
    static String indexType;
    static Integer numPartitions;
    static int loopTimes;
    static Point queryPoint;
    @BeforeClass
    public static void onceExecutedBeforeAll(){
        SparkConf conf = new SparkConf().setAppName("RectangleKnn").setMaster("local[2]");
        sc = new JavaSparkContext(conf);
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        prop = new Properties();
        input = RectangleKnnTest.class.getClassLoader().getResourceAsStream("rectangle.test.properties");

        //Hard code to a file in resource folder. But you can replace it later in the try-catch field in your hdfs system.
        InputLocation = "file://"+RectangleRDDTest.class.getClassLoader().getResource("primaryroads.csv").getPath();

        offset = 0;
        splitter = "";
        indexType = "";
        numPartitions = 0;
        GeometryFactory fact=new GeometryFactory();
        try {
            // load a properties file
            prop.load(input);
            // There is a field in the property file, you can edit your own file location there.
            // InputLocation = prop.getProperty("inputLocation");
            offset = Integer.parseInt(prop.getProperty("offset"));
            splitter = prop.getProperty("splitter");
            indexType = prop.getProperty("indexType");
            numPartitions = Integer.parseInt(prop.getProperty("numPartitions"));
            loopTimes=5;
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        queryPoint = fact.createPoint(new Coordinate(-84.01, 34.01));
    }

    @AfterClass
    public static void teardown(){
        sc.stop();
    }

    @Test
    public void testSpatialKnnQuery() throws Exception {
    	RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter);

    	for(int i=0;i<loopTimes;i++)
    	{
    		List<Envelope> result = KNNQuery.SpatialKnnQuery(rectangleRDD, queryPoint, 5);
    		assert result.size()>-1;
    	}

    }
    @Test
    public void testSpatialKnnQueryUsingIndex() throws Exception {
    	RectangleRDD rectangleRDD = new RectangleRDD(sc, InputLocation, offset, splitter);
    	rectangleRDD.buildIndex("rtree");
    	for(int i=0;i<loopTimes;i++)
    	{
    		List<Envelope> result = KNNQuery.SpatialKnnQueryUsingIndex(rectangleRDD, queryPoint, 5);
    		assert result.size()>-1;
    	}

    }

}