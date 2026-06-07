package es.gva.edu.iesjuandegaray.bicis;

import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

public class ConversionGeoLongLat {
//a
	public static String conversion(double xGeom, double yGeom) {
		CRSFactory crsFactory = new CRSFactory();
		CoordinateReferenceSystem utm = crsFactory.createFromParameters(
				"ETRS89_UTM30",
				"+proj=utm +zone=30 +datum=WGS84 +units=m +no_defs"
		);
		
		CoordinateReferenceSystem wgs84 = crsFactory.createFromParameters(
				 "WGS84",
				 "+proj=longlat +datum=WGS84 +no_defs"
		);
		
		CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
		CoordinateTransform transform = ctFactory.createTransform(utm, wgs84);
		
		ProjCoordinate utmCoord = new ProjCoordinate(xGeom, yGeom);
		ProjCoordinate latLon = new ProjCoordinate();
		
		transform.transform(utmCoord, latLon);
		
		return latLon.y + "," + latLon.x;
	}
}
