package es.gva.edu.iesjuandegaray.bicis;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class DatosJSon {
	private static String API_URL;
	private String datos = "";
	private String[] values;
	private int numEst;
	
	public DatosJSon(int nE) {
		numEst = nE;
		datos = "";
		API_URL = "https://geoportal.valencia.es/server/rest/services/OPENDATA/Trafico/MapServer/228/query"
				+"?where=1%3D1"
				+"&outFields=*"
				+"&returnGeometry=true"
				+"&f=json";
		
		values = new String[numEst];
		
		for(int i = 0; i < numEst; i++)
			values[i]="";
	}
	
	public void mostrarDatos(int nE) {
		numEst = nE;
		datos="";
		
		API_URL = "https://geoportal.valencia.es/server/rest/services/OPENDATA/Trafico/MapServer/228/query"
				+"?where=1%3D1"
				+"&outFields=*"
				+"&returnGeometry=true"
				+"&f=json";
		int number;
		String nombre;
		int bicis;
		int anclajes;
		double x,y;
		
		values = new String[numEst];
		for(int i = 0; i < numEst; i++)
			values[i] = "";
		
		if (API_URL.isEmpty()) {
			setDatos(getDatos().concat("La URL de la API no está especificada"));
			return;
		}
		
		try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(API_URL);
			HttpResponse response = httpClient.execute(request);
			
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
				String result = EntityUtils.toString(entity);
				
				try {
					JSONObject jsonObject = new JSONObject(result);
					JSONArray features = jsonObject.getJSONArray("features");
					
					// Añade aquí el Código para recorrer el vector de objetos JSON, con los datos de las
					//estaciones y preparar el vector de
					// valores (atributo values de esta clase).a
					// BUCLE SENCILLO
					
					for (int i = 0; i < getNumEst(); i++) {
						String coords ="";
						String[] partes;
						
	                	JSONObject feature = features.getJSONObject(i);
	                	JSONObject atributo = feature.getJSONObject("attributes");
	                	JSONObject geometria = feature.getJSONObject("geometry");
	                	
	                	number = atributo.getInt("number");
	                	nombre = atributo.getString("name");
	                	bicis = atributo.getInt("available");
	                    anclajes = atributo.getInt("free");
	                    String abierto = atributo.getString("open");
	                    
	                    x = geometria.getDouble("x");
	                    y = geometria.getDouble("y");
	                    
	                    coords = (String) ConversionGeoLongLat.conversion(x, y);
	                    partes = coords.split(",");
	                    
	                    String lat = partes[0].trim();
	                    String lon = partes[1].trim();
	                    
	                    
	                    values[i] = number+";"+nombre+";"+abierto+";"+bicis+";"+anclajes+";"+lat+";"+lon;
	                }
					
					setValues(values);
					
					
				} catch (org.json.JSONException e) {
					setDatos(getDatos().concat("Error al procesar los datos JSON:"+e.getMessage()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public String getDatos() {
		return datos;
	}

	public void setDatos(String datos) {
		this.datos = datos;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}

	public int getNumEst() {
		return numEst;
	}

	public void setNumEst(int numEst) {
		this.numEst = numEst;
	}
	
	
}
