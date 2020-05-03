package beans;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import constantes.URLs;
import pojos.ConfPartida;
import pojos.Estado;
import pojos.Partida;
import pojos.Profesor;


@ManagedBean
public class PartidasBean implements Serializable{

	
	private static final long serialVersionUID = 2057658592045182866L;
	
	private List<Partida> partidasEnCurso;
	private List<ConfPartida> misPartidas;
	private HttpSession userSession;
	private Profesor login;

	@PostConstruct
    public void init() {
		//Obtenemos la session del usuario
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		this.userSession = request.getSession(true);
		login = (Profesor) this.userSession.getAttribute("profesor");
		
		ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();

		if(login == null) {
			try{
				
	            eContext.redirect( eContext.getRequestContextPath() + URLs.pathlogin );
			}catch(  Exception e ){
				System.out.println( "Me voy al carajo, no funciona esta redireccion" );
				
			}
			
		}else {
			System.out.println("Listamos las partidas");
			this.listarPartidas();
		}
		
	}
	
	public void iniciarPartida() {
		
		// Obtenemos el ID de la configuracion seleccionada
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String configSeleccionada = params.get("partidaID");
		
		
		//Mapeamos la configuracion seleccionada en una nueva partida
		configurarPartida cpBean = new configurarPartida();
		ConfPartida configuracion = cpBean.obtenerConfiguracion(configSeleccionada);

		Partida partida = new Partida(configuracion.getId_profesor(),configuracion.getId_juego(),configuracion.getId_fpreguntas(),
									configuracion.getTitulo(),configuracion.getEtapa(),configuracion.getCurso(),configuracion.getAsignatura(),
									configuracion.getTema(),configuracion.getPorcentaje_correccion(),10,configuracion.getParametros(),
									configuracion.getEquipos(),false);
		
		//Creamos la partida en la base de datos
		this.crearPartida(partida);
		
		
		//Listamos las partidas
		this.listarPartidas();
		

	}
	
	private void crearPartida(Partida p) {
		String url = URLs.NUEVAPARTIDA;
		System.out.println("Petición MOD conf partida: " + url);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Estado> response = restTemplate.postForEntity(url, p, Estado.class);

		Estado state = response.getBody();
		if (!response.getStatusCode().is2xxSuccessful() || !state.isEstado()) {
			System.out.println("No se ha podido añadir la partida");
			
		} else {
			
			System.out.println("Partida añadida correctamente");
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

			try {
				ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	//Peticiones GET al ms para obtener las listas de partidas
	public void listarPartidas() {
		
		//Obtenemos misPartidas

		//ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();
		
		//String uri = eContext.getRequestContextPath();
		//System.out.println(uri);		
		String url = URLs.LISTCONFPARTIDAS + login.getEmail();
		System.out.println("Petición de lista de conf partidas: "+url);
		RestTemplate restTemplate = new RestTemplate();
		try {
			
			ResponseEntity<List<ConfPartida>> response = restTemplate.exchange(
			  url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfPartida>>(){});
			System.out.println("response OK");
			this.misPartidas = response.getBody();
			
			System.out.println("Size misPartidas: "+misPartidas.size());
			
			
			
		}catch(Exception misPartidasEx) {
			System.err.println(misPartidasEx.getMessage());
			this.misPartidas = new ArrayList<ConfPartida>();
		}
		
		try {
			//Obtenemos las partidas en curso
			System.out.println("Petición de lista de partidas en curso: "+url);
			url = URLs.LISTPARTIDAS + login.getEmail();
			ResponseEntity<List<Partida>> response2 = restTemplate.exchange(
			  url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<Partida>>(){});
			this.partidasEnCurso = response2.getBody();
           
		
		}catch(Exception partidasEnCursoEx) {
			System.err.println(partidasEnCursoEx.getMessage());
			this.partidasEnCurso = new ArrayList<Partida>();
		}
		
	}

	
	//GETTERS & SETTERS
	public List<Partida> getPartidasEnCurso() {
		return partidasEnCurso;
	}



	public void setPartidasEnCurso(List<Partida> partidasEnCurso) {
		this.partidasEnCurso = partidasEnCurso;
	}



	public List<ConfPartida> getMisPartidas() {
		return misPartidas;
	}



	public void setMisPartidas(List<ConfPartida> misPartidas) {
		this.misPartidas = misPartidas;
	}
	
	public void setLogin(Profesor login) {
		this.login = login;
	}

	public Profesor getLogin() {
		return login;
	}

}
	
	
