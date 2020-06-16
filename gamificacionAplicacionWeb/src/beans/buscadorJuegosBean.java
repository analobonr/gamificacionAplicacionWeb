package beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.event.SelectEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import pojos.Juego;
import pojos.Etapa;

@ManagedBean
public class buscadorJuegosBean implements Serializable{

	
	private static final long serialVersionUID = 2057658592045182866L;
	
	private List<Etapa> etapas;
	private String[] etapasSeleccionadas;
	
	private String ilimitadas;  //0 no select, 1 tick, 2 cruz
	private int tipoFich;
	private String[] respuestaSelect;
	private int minPreguntasMinimas=1;
	private int maxPreguntasMinimas=100;
	private int minPreguntasMaximas=1;
	private int maxPreguntasMaximas=100;
	private int minJugadores = 1;
	private int maxJugadores = 100;
	
	private List<Juego> juegos;
	private List<Juego> juegosFiltrados;

	private Juego juegoSeleccionado;
	
	
	@PostConstruct
    public void init() {
				
		this.listJuegos();
		this.setJuegosFiltrados(juegos);
		
		etapas = new ArrayList<Etapa>();
		etapas.add(new Etapa("GEN", "General"));
		etapas.add(new Etapa("INF", "Infantil"));
		etapas.add(new Etapa("PRI", "Primaria"));
		etapas.add(new Etapa("SEC", "Secundaria"));
		etapas.add(new Etapa("UNI", "Universidad"));
		
		

	}
	
	//Método que obtiene todos los juegos disponibles
		public void listJuegos() {
			
			//Llamada GET al servicio rest correspondiente
			URL url;
			try {
				url = new URL("http://localhost:8080/juegos/");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				
				BufferedReader in = new BufferedReader(new InputStreamReader(
		                conn.getInputStream()));
				
				//Parseamos la respuesta en una lista de Juegos
		        String json;
		        while ((json = in.readLine()) != null) {
		        	
		        	Gson gson = new Gson();
		        	Type type = (Type) new TypeToken<List<Juego>>() {}.getType();
		        	juegos  = gson.fromJson(json, type);
		    
		        
		        	
		        }
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	
	
	public void aplicarFiltros() {
		
		System.out.println("Aplicar filtros");
		
		this.setJuegosFiltrados(this.juegos);
		
		Boolean pregIlimitadas = this.setPregIlimitadas(this.ilimitadas);
		
		Iterator<Juego> iter = this.juegosFiltrados.iterator();
		Juego j;
		Boolean pasaFiltros = true;
		while (iter.hasNext()) {
			
			j = iter.next();
			if (j.getPregMin() < this.minPreguntasMinimas || j.getPregMin() > this.maxPreguntasMinimas) {
				
				pasaFiltros = false;
				System.out.println("Preguntas minimas no coincide: " + j.getNombre());
				
			}
			
			if (pregIlimitadas != null) {
				
				
				if(pregIlimitadas.compareTo(new Boolean(j.isPregIlimitadas())) != 0){
					pasaFiltros = false;
					System.out.println("Preguntas ilimitadas no coincide: " + j.getNombre());
					
				}
				
			}
			
			if(this.etapasSeleccionadas.length != 0){
				if(!existsEtapa(this.etapasSeleccionadas, j.getEtapa())){
					pasaFiltros = false;
					System.out.println("Etapas no coincide: " + j.getNombre());
					
				}
			}
			
			if(this.respuestaSelect.length != 0){
				if(!existsFormaRespuesta(this.respuestaSelect, j.getTipoRespuesta())){
					pasaFiltros = false;
					System.out.println("Forma respuesta no coincide: " + j.getNombre());
					
				}
			}
			
			/*if (tipoFich != j.getTipoFich()) {
				pasaFiltros = false;
				System.out.println("Tipo fichero no coincide: " + j.getNombre());
				System.out.println(tipoFich + " || "+ j.getTipoFich());
			}*/
			
			
			
			if (!pasaFiltros) {
				iter.remove();
				
				
			}
			
			pasaFiltros = true;
		
				
		}
	}
	
	
	private boolean existsEtapa(String[] etapasFiltro, String etapasJuego) {
		
		boolean existe = false;
	
		for (String etapa:etapasFiltro) {
			
			if (etapasJuego.contains(etapa)) {
				
				existe = true;
			}
			
				
		}
		
		return existe;
	}
	
private boolean existsFormaRespuesta(String[] respuestasFiltro, int tipoRespuesta) {
		
		boolean existe = false;
	
		for (String respuesta:respuestasFiltro) {
			
			if (Integer.parseInt(respuesta) == tipoRespuesta) {
				
				existe = true;
			}
			
				
		}
		
		return existe;
	}
	
	
	
	public Juego getJuego(Integer id) {
		
		if (id == null){
            throw new IllegalArgumentException("no id provided");
        }
		
        for (Juego juego : juegos){
            if (id.equals(juego.getId_juego())){
                return juego;
            }
        }
        return null;
	}

	private Boolean setPregIlimitadas(String ilimitadas) {
		
		Boolean pregIlimitadas = null;
		
		if (ilimitadas.contains("1")) {
			pregIlimitadas = new Boolean(true);

		}else if (ilimitadas.contains("2"))
		{
			pregIlimitadas = new Boolean(false);
		}
		
		return pregIlimitadas;
	}
	
	
	//Listener de juego seleccionado en la lista
	public void onSelectJuego(SelectEvent event) {
		
		this.juegoSeleccionado = (Juego) event.getObject();
		System.out.println("Juego seleccionado: "+juegoSeleccionado.getNombre());
	
     
    }
	
	
	
	//GETTERS AND SETTERS

	public List<Etapa> getEtapas() {
		return etapas;
	}


	public void setEtapas(List<Etapa> etapas) {
		this.etapas = etapas;
	}


	public String[] getEtapasSeleccionadas() {
		return etapasSeleccionadas;
	}


	public void setEtapasSeleccionadas(String[] etapasSeleccionadas) {
		this.etapasSeleccionadas = etapasSeleccionadas;
	}


	public String getIlimitadas() {
		return ilimitadas;
	}


	public void setIlimitadas(String ilimitadas) {
		this.ilimitadas = ilimitadas;
	}


	public String[] getRespuestaSelect() {
		return respuestaSelect;
	}


	public void setRespuestaSelect(String[] respuestaSelect) {
		this.respuestaSelect = respuestaSelect;
	}


	public int getMinPreguntasMinimas() {
		return minPreguntasMinimas;
	}


	public void setMinPreguntasMinimas(int minPreguntasMinimas) {
		this.minPreguntasMinimas = minPreguntasMinimas;
	}


	public int getMaxPreguntasMinimas() {
		return maxPreguntasMinimas;
	}


	public void setMaxPreguntasMinimas(int maxPreguntasMinimas) {
		this.maxPreguntasMinimas = maxPreguntasMinimas;
	}


	public int getMinPreguntasMaximas() {
		return minPreguntasMaximas;
	}


	public void setMinPreguntasMaximas(int minPreguntasMaximas) {
		this.minPreguntasMaximas = minPreguntasMaximas;
	}


	public int getMaxPreguntasMaximas() {
		return maxPreguntasMaximas;
	}


	public void setMaxPreguntasMaximas(int maxPreguntasMaximas) {
		this.maxPreguntasMaximas = maxPreguntasMaximas;
	}


	public int getMinJugadores() {
		return minJugadores;
	}


	public void setMinJugadores(int minJugadores) {
		this.minJugadores = minJugadores;
	}


	public int getMaxJugadores() {
		return maxJugadores;
	}


	public void setMaxJugadores(int maxJugadores) {
		this.maxJugadores = maxJugadores;
	}

	public List<Juego> getJuegos() {
		return juegos;
	}


	public void setJuegos(List<Juego> juegos) {
		this.juegos = juegos;
	}

	public List<Juego> getJuegosFiltrados() {
		return juegosFiltrados;
	}

	public void setJuegosFiltrados(List<Juego> juegosFiltrados) {
		this.juegosFiltrados = juegosFiltrados;
	}

	public int gettipoFich() {
		return tipoFich;
	}

	public void settipoFich(int tipoFich) {
		this.tipoFich = tipoFich;
	}

	public int getTipoFich() {
		return tipoFich;
	}

	public void setTipoFich(int tipoFich) {
		this.tipoFich = tipoFich;
	}

	public Juego getJuegoSeleccionado() {
		return juegoSeleccionado;
	}

	public void setJuegoSeleccionado(Juego juegoSeleccionado) {
		this.juegoSeleccionado = juegoSeleccionado;
	}

	
	
}
	
	
