package beans;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	
	
	
	//Peticiones GET al ms para obtener las listas de partidas
	public void listarPartidas() {
		
		//Obtenemos misPartidas

		//ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();
		
		//String uri = eContext.getRequestContextPath();
		//System.out.println(uri);		
		String url = URLs.LISTCONFPARTIDAS + login.getEmail();
		System.out.println("Petición de lista de conf partidas: "+url);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<ConfPartida>> response = restTemplate.exchange(
		  url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfPartida>>(){});
		this.misPartidas = response.getBody();
		
		System.out.println("Size misPartidas: "+misPartidas.size());
		
		//Obtenemos las partidas en curso
		url = URLs.LISTPARTIDAS + login.getEmail();
		System.out.println("Petición de lista de partidas en curso: "+url);
		ResponseEntity<List<Partida>> response2 = restTemplate.exchange(
		  url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<Partida>>(){});
		this.partidasEnCurso = response2.getBody();
           
		
		
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
	
	
