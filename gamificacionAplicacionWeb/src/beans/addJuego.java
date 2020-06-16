package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;


import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import pojos.Juego;
import pojos.Estado;
import pojos.Etapa;

@ManagedBean
public class addJuego implements Serializable{

	
	private static final long serialVersionUID = 2057658592045182866L;
	
	private Juego nuevoJuego;
	private List<Etapa> etapas;
	private String[] etapasSeleccionadas;
	private String tipoFich;
	private String tipoRespuestas;
	
	//Metodo que inicializa las variables necesarias antes de cargar la página
	@PostConstruct
    public void init() {
		
		setNuevoJuego(new Juego());		
		nuevoJuego.setId_juego(-1);
		nuevoJuego.setPregMin(1);
		nuevoJuego.setNumFichMin(1);
		nuevoJuego.setNumFichMax(1);
		nuevoJuego.setTipoFich(0);
		
		etapas = new ArrayList<Etapa>();
		etapas.add(new Etapa("GEN", "General"));
		etapas.add(new Etapa("INF", "Infantil"));
		etapas.add(new Etapa("PRI", "Primaria"));
		etapas.add(new Etapa("SEC", "Secundaria"));
		etapas.add(new Etapa("UNI", "Universidad"));
		
		

	}
	
	
	//Metodo que valida los datos y los envía para insertarlos en la bbdd
	public void guardar() {
		
		
		//Guardamos la etapa con el formato adecuado
		String e ="";
		for (String i: etapasSeleccionadas) {
			if (e == "") {
				
				e = i;
			}else {
				
				e = e + "-" + i;
			}
		}
		
		nuevoJuego.setEtapa(e);
		
		
		//Guardamos el tipo de la respuesta
		nuevoJuego.setTipoRespuesta(Integer.parseInt(tipoRespuestas));
		
		//Guardamos el tipo de fichero
		nuevoJuego.setTipoFich(Integer.parseInt(tipoFich));
		
		
		//Peticion POST para insertar en la base de datos
		 RestTemplate restTemplate = new RestTemplate();
		 String url = "http://localhost:8080/juegos/";
		 
		 ResponseEntity<Estado> response = restTemplate.postForEntity(url, nuevoJuego,Estado.class);
		 Estado state = response.getBody();
		 
		 if(!response.getStatusCode().is2xxSuccessful() || !state.isEstado()) {
			 System.out.println("No se ha podido añadir el juego");
		 }else {
			 System.out.println("Juego añadido correctamente");
		 }
	
	}
	
	
	public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
	
	
	//GETTERS AND SETTERS
	public Juego getNuevoJuego() {
		return nuevoJuego;
	}

	public void setNuevoJuego(Juego nuevoJuego) {
		this.nuevoJuego = nuevoJuego;
	}



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




	public String getTipoFich() {
		return tipoFich;
	}


	public void setTipoFich(String tipoFich) {
		this.tipoFich = tipoFich;
	}


	public String getTipoRespuestas() {
		return tipoRespuestas;
	}


	public void setTipoRespuestas(String tipoRespuestas) {
		this.tipoRespuestas = tipoRespuestas;
	}


	
	
}
	
	
