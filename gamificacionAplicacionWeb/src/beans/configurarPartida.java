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
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;


import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import constantes.Mensajes;
import constantes.URLs;
import pojos.ConfPartida;
import pojos.Estado;
import pojos.Juego;
import pojos.configuracionEquipos.Configuracion;
import pojos.configuracionEquipos.Equipo;
import pojos.configuracionEquipos.ParametrosGE;
import pojos.configuracionEquipos.Variable;

/*Bean que maneja los datos de partidaForm para la configuración de una partida*/

@ManagedBean
public class configurarPartida implements Serializable {

	private static final long serialVersionUID = 3885381981170292304L;

	private String configSeleccionada;

	private Juego juegoSeleccionado;
	private List<Juego> juegos;
	private String caratulaSrc;
	private String capturaSrc;

	private List<String> listaPrueba;

	private String titulo;
	private String etapa;
	private String curso;
	private String asignatura;
	private String tema;
	private Integer fichero;
	private Integer correctas;
	private Integer tiempoRespuesta;

	private String email;

	private String visible = "panelVisible";
	private String invisible = "panelInvisible";

	private String estiloPanelInfoJuego;
	private String estiloPanelSelectJuego;

	private ParametrosGE equipos;
	private List<Variable> nombresGrupos;
	private List<Equipo> listaEquipos;

	private Integer minimoGrupos;
	private Integer maximoGrupos;

	private Integer minimoEquipos;
	private Integer maximoEquipos;
	
	private Integer idProfesor;

	// Post Constructor. Se ejecuta al cargar la pagina
	@PostConstruct
	public void init() {

		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		this.email = params.get("email");
		this.configSeleccionada = "-1";
		this.estiloPanelInfoJuego = invisible;
		this.estiloPanelSelectJuego = visible;

		this.correctas = 50;
		this.tiempoRespuesta = 10;
		this.fichero = 1;

	}
	
	/**
	 * Método que eliminar una plantilla/configuración de partida
	 */
	public void eliminar() {

		// Obtenemos el ID de la configuracion seleccionada
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String idConfiguracion = params.get("partidaID");

		try {
			//Realizamos la peticion de delete para eliminar la partida
			String url = URLs.CONFPARTIDA + idConfiguracion;
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.delete(url);
	
			//Recargamos la pagina
			this.recargarPagina();
			
		}catch(Exception e) {
			e.printStackTrace();
			context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERRORDELETEPLANTILLA));
		}

	}

	// Metodo que establece el id de la configuracion de partida seleccionada
	public void seleccionarConf() {

		// Obtenemos el ID de la configuracion seleccionada
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		this.configSeleccionada = params.get("partidaID");

		// Obtenemos la configuracion
		ConfPartida cp = this.obtenerConfiguracion(params.get("partidaID"));

		// Asociamos la configuracion a las variables del formulario
		this.titulo = cp.getTitulo();
		this.etapa = cp.getEtapa();
		this.curso = cp.getCurso();
		this.asignatura = cp.getAsignatura();
		this.tema = cp.getTema();
		this.fichero = cp.getId_fpreguntas();
		this.correctas = cp.getPorcentaje_correccion();
		this.obtenerJuego(cp.getId_juego());

		this.estiloPanelInfoJuego = visible;
		this.estiloPanelSelectJuego = invisible;

	}

	public ConfPartida obtenerConfiguracion(String idConf) {

		// Llamada GET al servicio rest correspondiente
		String url = URLs.GETCONFPARTIDA + idConf;
		System.out.println("Petición de obtener conf partida: " + url);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ConfPartida> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<ConfPartida>() {
				});
		ConfPartida cp = response.getBody();

		return cp;

	}

	private void obtenerJuego(Integer idJuego) {

		// Llamada GET al servicio rest correspondiente
		String url = URLs.GETJUEGO + idJuego;
		System.out.println("Petición de obtener el juego: " + url);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Juego> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<Juego>() {
				});
		this.juegoSeleccionado = response.getBody();
		this.setCaratulaSrc(juegoSeleccionado.getNombreZip().split("jug-")[1] + ".png");
		this.setCapturaSrc(juegoSeleccionado.getNombreZip().split("jug-")[1] + "-cap.jpg");
		
		

	}

	public void cambiarJuego() {
		this.estiloPanelInfoJuego = invisible;
		this.estiloPanelSelectJuego = visible;
		this.juegoSeleccionado = null;
	}

	// Método para cambiar de pestana
	public String onFlowProcess(FlowEvent event) {

		String newStep = event.getNewStep();

		if (newStep.contains("juego")) {
			this.listJuegos();
		} else if (newStep.contains("equipos")) {
			this.renderizarEquipos();
		} else if (newStep.contains("configuracion")) {
			// Si no se ha seleccionado el juego se queda en la misma
			if (juegoSeleccionado == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Debe seleccionar un juego", ""));
				newStep = "juego";
			}
		}

		return newStep;

	}

	public void renderizarEquipos() {

		boolean cargarConfiguracion = false;
		// Obtenemos el objeto con la info base
		String configuracionEquipos = this.juegoSeleccionado.getJsonEquipos();
		if (this.configSeleccionada != "-1") {
			ConfPartida cp = this.obtenerConfiguracion(this.configSeleccionada);
			if (cp.getId_juego() == this.juegoSeleccionado.getId_juego()) {
				configuracionEquipos = cp.getEquipos();
				cargarConfiguracion = true;
			}
		}

		//Transformaos a objeto el JSON
		Gson gson = new Gson();
		this.equipos = gson.fromJson(configuracionEquipos, ParametrosGE.class);

		System.out.println(configuracionEquipos);
		
		//Si existe json con los equipos
		if ((this.equipos != null) && (this.equipos.getGrupos() != null)) {
			
			
			
			//Si se crea una nueva plantilla
			if (!cargarConfiguracion) {
				
				// Obtenemos el numero de grupos por defecto
				int numGruposDef = this.equipos.getGrupos().getNumeroGrupos().getRango().getValorDefecto();
				List<Variable> variables = this.equipos.getGrupos().getNumeroGrupos().getVariablesJuego();

				// Le asignamos el numero de grupos por defecto a la variable
				if ((variables.size() == 1) && (variables.get(0).getValor().isEmpty()) ) {
					variables.get(0).setValor(Integer.toString(numGruposDef));
				}

				this.equipos.getGrupos().getNumeroGrupos().setVariablesJuego(variables);
				
				// Insertamos tantos grupos  como indique el valor por defecto
				this.setNombresGrupos(
						this.equipos.getGrupos().getNombresGrupos().getVariablesJuego().subList(0, numGruposDef));
				
				
				// Inicializamos el minimo y maxmio de grupos
				this.minimoGrupos = new Integer(this.equipos.getGrupos().getNumeroGrupos().getRango().getValorMinimo());
				this.maximoGrupos = new Integer(this.equipos.getGrupos().getNumeroGrupos().getRango().getValorMaximo());
				
	
				//Si existen equipos
				/*if (this.equipos.getEquipos() != null) {
					// Obtenemos el numero de equipos por defecto
					int numEquiposDef = this.equipos.getEquipos().getNumeroEquipos().getRango().getValorDefecto();
					variables = this.equipos.getEquipos().getNumeroEquipos().getVariablesJuego();
	
					// Le asignamos el numero de grupos por defecto a la variable
					if (variables.size() == 1) {
						variables.get(0).setValor(Integer.toString(numEquiposDef));
					}
	
					this.equipos.getEquipos().getNumeroEquipos().setVariablesJuego(variables);
	
					//Insertamos a los equipos a la lista para mostrarlos
					this.setListaEquipos(this.equipos.getEquipos().getMiembrosEquipos().subList(0, numEquiposDef));
					List<Variable> vEquipos = this.equipos.getEquipos().getNombresEquipos().getVariablesJuego().subList(0,
							numEquiposDef);
					
					int contador = 0;
					for (Variable v : vEquipos) {
						this.listaEquipos.get(contador).setNombreEquipo(v);
						contador++;
					}
					
					// Inicializamos el minimo y maxmio de equipos
					this.minimoEquipos = new Integer(this.equipos.getEquipos().getNumeroEquipos().getRango().getValorMinimo());
					this.maximoEquipos = new Integer(this.equipos.getEquipos().getNumeroEquipos().getRango().getValorMaximo());
				
				}*/
				
			//Si estamos editando una plantilla
			} else {
				
				//Asignamos valores a las variables para el renderizado
				this.setNombresGrupos(this.equipos.getGrupos().getNombresGrupos().getVariablesJuego());
				
				this.minimoGrupos = new Integer(this.equipos.getGrupos().getNumeroGrupos().getRango().getValorMinimo());
				this.maximoGrupos = new Integer(this.equipos.getGrupos().getNumeroGrupos().getRango().getValorMaximo());

				// Falta la lista de equipos
			}
			
		}else if ((this.equipos != null) && (this.equipos.getEquipos() != null)) {
			
			// Inicializamos el minimo y maxmio de equipos
			this.minimoEquipos = new Integer(this.equipos.getEquipos().getNumeroEquipos().getRango().getValorMinimo());
			this.maximoEquipos = new Integer(this.equipos.getEquipos().getNumeroEquipos().getRango().getValorMaximo());
			
			//Asignamos el valor inicial al numero de equipos
			int numEquiposDef = this.equipos.getEquipos().getNumeroEquipos().getRango().getValorDefecto();
			List<Variable> variables = this.equipos.getEquipos().getNumeroEquipos().getVariablesJuego();

			// Le asignamos el numero de grupos por defecto a la variable
			if (variables.size() == 1) {
				variables.get(0).setValor(Integer.toString(numEquiposDef));
			}

			this.equipos.getEquipos().getNumeroEquipos().setVariablesJuego(variables);
			
			// Insertamos tantos equipos  como indique el valor por defecto
			this.setListaEquipos(this.equipos.getEquipos().getEquipos().subList(0, numEquiposDef));
			
			//Asignamos el valor inicial al numero de grupos de cada equipo
			for (Equipo e : this.equipos.getEquipos().getEquipos()) {
				int gruposDefecto = e.getNumeroGrupos().getRango().getValorDefecto();
				e.getNumeroGrupos().getVariablesJuego().get(0).setValor(Integer.toString(gruposDefecto));
				
			}
		}
	}

	public void numGruposChange(ValueChangeEvent e) {
			
		System.out.println("numGruposChange eventp");

		int nuevoValor = Integer.parseInt(e.getNewValue().toString());
		int antiguoValor = Integer.parseInt(e.getOldValue().toString());

		nuevoValor = (nuevoValor > this.maximoGrupos) ? this.maximoGrupos
				: ((nuevoValor < this.minimoGrupos) ? this.minimoGrupos : nuevoValor);
		antiguoValor = (antiguoValor > this.maximoGrupos) ? this.maximoGrupos
				: ((antiguoValor < this.minimoGrupos) ? this.minimoGrupos : antiguoValor);

		int diferencia = nuevoValor - antiguoValor;

		if (diferencia < 0) {

			if (nuevoValor != 0) {

				this.nombresGrupos = this.nombresGrupos.subList(0, nuevoValor);
			}
		} else {

			String configuracionEquipos = this.juegoSeleccionado.getJsonEquipos();
			Gson gson = new Gson();
			ParametrosGE eq = gson.fromJson(configuracionEquipos, ParametrosGE.class);
			for (int i = 0; i < diferencia; i++) {
				System.out.println(eq.getGrupos().getNombresGrupos().getVariablesJuego().size());
				Variable v = eq.getGrupos().getNombresGrupos().getVariablesJuego().get(antiguoValor + i);
				this.nombresGrupos.add(v);

			}
		}

	}

	public void numEquiposChange(ValueChangeEvent e) {

		int nuevoValor = Integer.parseInt(e.getNewValue().toString());
		int antiguoValor = Integer.parseInt(e.getOldValue().toString());

		nuevoValor = (nuevoValor > this.maximoEquipos) ? this.maximoEquipos
				: ((nuevoValor < this.minimoEquipos) ? this.minimoEquipos : nuevoValor);
		antiguoValor = (antiguoValor > this.maximoEquipos) ? this.maximoEquipos
				: ((antiguoValor < this.minimoEquipos) ? this.minimoEquipos : antiguoValor);

		int diferencia = nuevoValor - antiguoValor;

		if (diferencia < 0) {

			if (nuevoValor != 0) {

				this.setListaEquipos(this.listaEquipos.subList(0, nuevoValor));
			}
		} else {

			String configuracionEquipos = this.juegoSeleccionado.getJsonEquipos();
			Gson gson = new Gson();
			ParametrosGE eq = gson.fromJson(configuracionEquipos, ParametrosGE.class);
			for (int i = 0; i < diferencia; i++) {
				
				Equipo equipo = eq.getEquipos().getEquipos().get(antiguoValor+i);
				this.listaEquipos.add(equipo);

			}
		}
	}
	
	public void numGruposEquipoChange(ValueChangeEvent e) {

		//Hay que saber que equipo se esta modificando
		
		int nuevoValor = Integer.parseInt(e.getNewValue().toString());
		int antiguoValor = Integer.parseInt(e.getOldValue().toString());

		
		nuevoValor = (nuevoValor > this.maximoEquipos) ? this.maximoEquipos
				: ((nuevoValor < this.minimoEquipos) ? this.minimoEquipos : nuevoValor);
		antiguoValor = (antiguoValor > this.maximoEquipos) ? this.maximoEquipos
				: ((antiguoValor < this.minimoEquipos) ? this.minimoEquipos : antiguoValor);

		int diferencia = nuevoValor - antiguoValor;

		if (diferencia < 0) {

			if (nuevoValor != 0) {

				this.setListaEquipos(this.listaEquipos.subList(0, nuevoValor));
			}
		} else {

			String configuracionEquipos = this.juegoSeleccionado.getJsonEquipos();
			Gson gson = new Gson();
			ParametrosGE eq = gson.fromJson(configuracionEquipos, ParametrosGE.class);
			for (int i = 0; i < diferencia; i++) {
				
				Equipo equipo = eq.getEquipos().getEquipos().get(antiguoValor+i);
				this.listaEquipos.add(equipo);

			}
		}

	}

	// Metodo para guardar una configuracion en la base de datos
	public void guardar() {

		if (juegoSeleccionado != null) {

			// Obtenemos el email
			FacesContext context = FacesContext.getCurrentInstance();
			Map<String, String> params = context.getExternalContext().getRequestParameterMap();
			this.idProfesor = new Integer(params.get("id_profesor"));

			String configuracionGE = "";
			
			
			//Si tiene configuracion de equipos se obtiene
			if (this.equipos != null) {
				// Guardamos los nombres de los grupos
				this.equipos.getGrupos().getNombresGrupos().setVariablesJuego(this.nombresGrupos);
	
				// Convertimos los equipos en un json en string
				Gson gson = new Gson();
				configuracionGE = gson.toJson(this.equipos, ParametrosGE.class);
				System.out.println(configuracionGE);
			}

			/*
			 * ConfPartida configuracion = new
			 * ConfPartida(this.email,juegoSeleccionado.getIdJuego(),this.fichero,this.
			 * titulo, this.etapa,this.curso,this.asignatura,this.tema,this.correctas,"",
			 * configuracionGE);
			 */

			// Momentaneo
			ConfPartida configuracion = new ConfPartida(new Integer(this.configSeleccionada),this.idProfesor, juegoSeleccionado.getId_juego(), 1, this.titulo,
					this.etapa, this.curso, this.asignatura, this.tema, this.correctas, "", configuracionGE,this.tiempoRespuesta);

			Gson gson2 = new Gson();
			String confString = gson2.toJson(configuracion, ConfPartida.class);
			System.out.println(confString);

			String url;
			if (configuracion.getId_configuracion() == -1) {
				url = URLs.NUEVACONFPARTIDA;
				System.out.println("Petición nueva conf partida: " + url);

			} else {
				url = URLs.MODCONFPARTIDA;
				System.out.println("Petición MOD conf partida: " + url);
			}
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Estado> response = restTemplate.postForEntity(url, configuracion, Estado.class);

			Estado state = response.getBody();

			if (!response.getStatusCode().is2xxSuccessful() || !state.isEstado()) {
				System.out.println("No se ha podido añadir la configuracion de la partida");
			} else {
				System.out.println("Configuracion añadida correctamente");
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();

				try {
					ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			System.out.println("Juego seleccionado no disponible");
		}

	}

	// Método que obtiene todos los juegos disponibles
	public void listJuegos() {

		// Llamada GET al servicio rest correspondiente
		String url = "http://localhost:8080/juegos/";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<Juego>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Juego>>() {
				});
		this.juegos = response.getBody();

	}

	
	/**
	 * Método que realiza una copia de una plantilla
	 */
	public void duplicar() {

		// Obtenemos el ID de la configuracion seleccionada
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String idConfiguracion = params.get("partidaID");

		//Obtenemos la plantilla seleccionada (Petición GET)
		String url = URLs.GETCONFPARTIDA + idConfiguracion;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ConfPartida> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<ConfPartida>() {
				});
		ConfPartida plantilla = response.getBody();
		
		//Cambiamos el id y el nombre de la plantilla
		plantilla.setId_configuracion(-1);
		plantilla.setTitulo(plantilla.getTitulo() + " (copia)");
		
		
		//Realizamos la petición post para añadir la plantilla
		url = URLs.NUEVACONFPARTIDA;
		restTemplate.postForEntity(url, plantilla,null);

		//Recargamos la pagina
		this.recargarPagina();
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

	public void onSelectJuego(SelectEvent event) {

		this.juegoSeleccionado = (Juego) event.getObject();
		System.out.println("Juego seleccionado: " + juegoSeleccionado.getNombre());
		this.estiloPanelInfoJuego = visible;
		this.estiloPanelSelectJuego = invisible;

		this.setCaratulaSrc(juegoSeleccionado.getNombreZip().split("jug-")[1] + ".png");
		this.setCapturaSrc(juegoSeleccionado.getNombreZip().split("jug-")[1] + "-cap.jpg");

	}

	public void resetearConfig() {

		this.configSeleccionada = "-1";
		this.titulo = "";
		this.etapa = "";
		this.curso = "";
		this.asignatura = "";
		this.tema = "";
		this.fichero = 1;
		this.correctas = 50;
		this.estiloPanelInfoJuego = invisible;
		this.estiloPanelSelectJuego = visible;

	}

	// Getters & Setters
	public Juego getJuego(Integer id) {

		if (id == null) {
			throw new IllegalArgumentException("no id provided");
		}

		for (Juego juego : juegos) {
			if (id.equals(juego.getId_juego())) {
				return juego;
			}
		}
		return null;
	}

	public void setJuegoSeleccionado(Juego juegoSeleccionado) {
		this.juegoSeleccionado = juegoSeleccionado;
	}

	public Juego getJuegoSeleccionado() {
		return juegoSeleccionado;
	}

	public List<Juego> getJuegos() {
		return juegos;
	}

	public void setJuegos(List<Juego> juegos) {
		this.juegos = juegos;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getAsignatura() {
		return asignatura;
	}

	public void setAsignatura(String asignatura) {
		this.asignatura = asignatura;
	}

	public String getTema() {
		return tema;
	}

	public void setTema(String tema) {
		this.tema = tema;
	}

	public Integer getFichero() {
		return fichero;
	}

	public void setFichero(Integer fichero) {
		this.fichero = fichero;
	}

	public Integer getCorrectas() {
		return correctas;
	}

	public void setCorrectas(Integer correctas) {
		this.correctas = correctas;
	}

	public String getConfigSeleccionada() {
		return configSeleccionada;
	}

	public void setConfigSeleccionada(String configSeleccionada) {
		this.configSeleccionada = configSeleccionada;
	}

	public String getEstiloPanelInfoJuego() {
		return estiloPanelInfoJuego;
	}

	public void setEstiloPanelInfoJuego(String estiloPanelInfoJuego) {
		this.estiloPanelInfoJuego = estiloPanelInfoJuego;
	}

	public String getEstiloPanelSelectJuego() {
		return estiloPanelSelectJuego;
	}

	public void setEstiloPanelSelectJuego(String estiloPanelSelectJuego) {
		this.estiloPanelSelectJuego = estiloPanelSelectJuego;
	}

	public List<String> getListaPrueba() {
		return listaPrueba;
	}

	public void setListaPrueba(List<String> listaPrueba) {
		this.listaPrueba = listaPrueba;
	}

	public ParametrosGE getEquipos() {
		return equipos;
	}

	public void setEquipos(ParametrosGE equipos) {
		this.equipos = equipos;
	}

	public List<Variable> getNombresGrupos() {
		return nombresGrupos;
	}

	public void setNombresGrupos(List<Variable> nombresGrupos) {
		this.nombresGrupos = nombresGrupos;
	}

	public Integer getminimoGrupos() {
		return minimoGrupos;
	}

	public void setminimoGrupos(Integer minimoGrupos) {
		this.minimoGrupos = minimoGrupos;
	}

	public Integer getmaximoGrupos() {
		return maximoGrupos;
	}

	public void setmaximoGrupos(Integer maximoGrupos) {
		this.maximoGrupos = maximoGrupos;
	}

	public Integer getMinimoEquipos() {
		return minimoEquipos;
	}

	public void setMinimoEquipos(Integer minimoEquipos) {
		this.minimoEquipos = minimoEquipos;
	}

	public Integer getMaximoEquipos() {
		return maximoEquipos;
	}

	public void setMaximoEquipos(Integer maximoEquipos) {
		this.maximoEquipos = maximoEquipos;
	}

	public List<Equipo> getListaEquipos() {
		return listaEquipos;
	}

	public void setListaEquipos(List<Equipo> listaEquipos) {
		this.listaEquipos = listaEquipos;
	}

	public String getCaratulaSrc() {
		return caratulaSrc;
	}

	public void setCaratulaSrc(String caratulaSrc) {
		this.caratulaSrc = caratulaSrc;
	}

	public String getCapturaSrc() {
		return capturaSrc;
	}

	public void setCapturaSrc(String capturaSrc) {
		this.capturaSrc = capturaSrc;
	}

	public Integer getTiempoRespuesta() {
		return tiempoRespuesta;
	}

	public void setTiempoRespuesta(Integer tiempoRespuesta) {
		this.tiempoRespuesta = tiempoRespuesta;
	}

}
