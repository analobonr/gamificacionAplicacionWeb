package beans;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import constantes.Mensajes;
import constantes.URLs;
import pojos.ConfPartida;
import pojos.Partida;
import pojos.Profesor;

/**
 * Bean que maneja los datos para la gestión de partidas
 * 
 * @author Ana Lobón
 * @version v1.0 (06/07/2020)
 */
@ManagedBean
public class PartidasBean implements Serializable{

	
	private static final long serialVersionUID = 2057658592045182866L;
	
	private List<Partida> partidasEnCurso;
	private List<ConfPartida> misPartidas;
	private HttpSession userSession;
	private Profesor login;

	
	/**
	 * Post Constructor. Se ejecuta al cargar la pagina index.xhtml
	 */
	@PostConstruct
    public void init() {
		
		//Obtenemos la session del usuario
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		this.userSession = request.getSession(true);
		login = (Profesor) this.userSession.getAttribute("profesor");
		
		ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();
		
		//Si no hay usuario loggeado redirigimos a el login
		if(login == null) {
			try{
	            eContext.redirect( eContext.getRequestContextPath() + URLs.pathlogin );
			}catch(  Exception e ){
				e.printStackTrace();				
			}
		
		//Si hay usuario loggeado...
		}else {
			
			//Listamos las plantillas y las partidas en curso
			this.listarPartidas();
		}
		
	}
	
	
	/**
	 * Método que crea una partida en curso a partir de una configuración seleccionada
	 */
	public void iniciarPartida() {
		
		// Obtenemos el ID de la configuracion seleccionada
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String configSeleccionada = params.get("plantillaID");
		
		
		//Mapeamos la configuracion seleccionada en una nueva partida
		configurarPartida cpBean = new configurarPartida();
		ConfPartida configuracion = cpBean.obtenerConfiguracion(configSeleccionada);

		if (configuracion != null) {
			Partida partida = new Partida(configuracion.getId_profesor(),configuracion.getId_juego(),configuracion.getId_fpreguntas(),
										configuracion.getTitulo(),configuracion.getEtapa(),configuracion.getCurso(),configuracion.getAsignatura(),
										configuracion.getTema(),configuracion.getPorcentaje_correccion(),10,configuracion.getParametros(),
										configuracion.getEquipos(),false);
			
			//Creamos la partida en la base de datos
			this.postPartida(partida);
			
			//Recargamos la interfaz
			this.recargarPagina();
		}

	}
	
	/**
	 * Método que cambia el estado de una partida a finalizada
	 */
	public void finalizarPartida() {
		
		// Obtenemos el ID de la configuracion seleccionada
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String partidaID = params.get("partidaID");
		
		//Obtenemos la partida
		Partida partida = this.getPartida(partidaID);
		
		if (partida != null) {
			//Finalizamos la partida
			partida.setFinalizada(true);
			
			//Guardamos la partida
			this.putPartida(partida);
			
			this.recargarPagina();
		}
		
	}
	
	
	/**
	 * Método que realiza la llamada get para obtener una partida
	 * 
	 * @param partidaId Identificador de la partida
	 * @return La partida correspondiente o null en caso de
	 *         error.
	 */
	private Partida getPartida(String partidaId) {
		
		Partida partida = null;

		try {
			// Llamada GET al servicio rest correspondiente
			String url = URLs.NUEVAPARTIDA + partidaId;
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Partida> response = restTemplate.exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<Partida>() {
					});

			partida = response.getBody();

		// Capturamos errores
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR, Mensajes.ERRORGETPARTIDA));
		}

		return partida;
	}
	
	
	/**
	 * Método que realiza la llamada post para crear una nueva partida
	 * @param partida Partida
	 * @return true en caso de error false en caso contrario
	 */
	private boolean postPartida(Partida partida) {
		
		boolean error = false;
		try {
			// Realizamos la petición post para añadir la plantilla
			String url = URLs.NUEVAPARTIDA;
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.postForEntity(url, partida, null);
		} catch (Exception e) {
			error = true;
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR, Mensajes.ERRORPOSTPARTIDA));
		}
		
		return error;
	}
	
	
	/**
	 * Método que realiza la llamada put para modificar una partida
	 * @param partida Partida
	 * @return true en caso de error false en caso contrario
	 */
	private boolean putPartida(Partida partida) {
		
		boolean error = false;
		
		try {
			String url = URLs.NUEVAPARTIDA;
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.put(url, partida);
		} catch (Exception e) {
			error = true;
			e.printStackTrace();
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR, Mensajes.ERRORPUTPLANTILLA));
		}
		
		return error;
	}
	
	
	
	/**
	 * Método que obtiene el listado de plantillas/configuraciones de partidas y partidas en curso del usuario loggeado
	 */
	public void listarPartidas() {
		
		//Obtenemos el listado de plantillas del usuario
		String url = URLs.LISTCONFPARTIDAS + login.getEmail();
		RestTemplate restTemplate = new RestTemplate();
		try {
			
			ResponseEntity<List<ConfPartida>> response = restTemplate.exchange(
			  url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<ConfPartida>>(){});
			this.misPartidas = response.getBody();			
			
		}catch(Exception misPartidasEx) {
			System.err.println(misPartidasEx.getMessage());
			this.misPartidas = new ArrayList<ConfPartida>();
		}
		
		try {
			
			//Obtenemos las partidas en curso
			url = URLs.LISTPARTIDAS + login.getEmail();
			ResponseEntity<List<Partida>> response2 = restTemplate.exchange(
			  url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<Partida>>(){});
			this.partidasEnCurso = response2.getBody();
           
		
		}catch(Exception partidasEnCursoEx) {
			System.err.println(partidasEnCursoEx.getMessage());
			this.partidasEnCurso = new ArrayList<Partida>();
		}
		
	}

	/**
	 * Método que recarga la interfaz actual
	 */
	private void recargarPagina() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

		try {
			ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	
