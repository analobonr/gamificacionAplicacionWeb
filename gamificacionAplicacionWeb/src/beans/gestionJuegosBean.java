package beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import constantes.Mensajes;
import constantes.Rutas;
import constantes.URLs;
import pojos.Estado;
import pojos.Etapa;
import pojos.Juego;
import pojos.Profesor;
import pojos.configuracionEquipos.ParametrosGE;
import constantes.valoresDesplegables.estilosJuego_t;
import constantes.valoresDesplegables.rol_t;


/**
 * Bean que maneja los datos de la interfaz de gestión de juegos
 * @author Ana Lobón
 * @version 1.0 (14/06/2020)
 */
@ManagedBean
public class gestionJuegosBean implements Serializable {

	private static final long serialVersionUID = 3885381981170292304L;

	private Profesor login;
	
	//Propiedades para el formulario
	public Juego juegoform;
	private String tipoRespuesta;
	private String[] etapasSeleccionadas;
	private List<Etapa> listaEtapas = constantes.valoresDesplegables.etapasJuegos;
	private String estiloJuego;
	 
	
	//Propiedades para la lista de juegos
	private List<Juego> juegosFiltrados;
	private List<Juego> juegosListadoCompleto;
	
	//Propiedades para el filtrado
	private String[] etapasFiltro;
	private String ilimitadasFiltro;  //0 no select, 1 tick, 2 cruz
	private int tipoFichFiltro;
	private String[] respuestaSelectFiltro;
	private int minPreguntasMinimasFiltro = 1;
	private int maxPreguntasMinimasFiltro = 100;
	private int minPreguntasMaximasFiltro = 1;
	private int maxPreguntasMaximasFiltro = 100;
	private int minJugadoresFiltro = 1;
	private int maxJugadoresFiltro = 100;
	
	
	//Propiedad para el renderizado de los botones de modificar o añadir juego
	private boolean verModificar;
	private String fieldLegend;
	
	//Flag que indica si hay un fichero de juego cargado
	private boolean ficheroSubido;

	/**
	 * Método que se ejecuta automaticamente cuando carga la interfaz
	 */
	@PostConstruct
	public void init() {
		
		//Obtenemos la session del usuario
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession userSession = request.getSession(true);
		login = (Profesor) userSession.getAttribute("profesor");
		
		ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();
		
		//Si el usuario loggeado no tiene rol de superusuario
		if((login == null) || (login.getRol() == rol_t.USER)) {
			try{
				//Redireccionamos a la interfaz de login
	            eContext.redirect( eContext.getRequestContextPath() + URLs.pathlogin );
			}catch(  Exception e ){
				System.err.println(e);				
			}
			
		//Si no...
		}else {
			//Inicializamos el objeto Juego para el formulario
			this.juegoform = new Juego();
			this.juegoform.setNumFichMin(0);
			this.juegoform.setNumFichMax(0);
			this.juegoform.setPregMin(0);
			
			//Obtenemos el listado de juegos existentes
			this.juegosListadoCompleto = this.listarJuegos();
			this.juegosFiltrados = this.juegosListadoCompleto;
			
			//Inicializamos para vista inicial
			this.verModificar = false;
			this.fieldLegend = "Nuevo Juego";
			this.ficheroSubido = false;
		}
	}

	/**
	 * Realiza la petición get para obtener el listado de juegos
	 * @return Listado de juegos existentes
	 */
	public List<Juego> listarJuegos() {
		
		List<Juego> lista = new ArrayList<Juego>();
		
		try {
			String url = URLs.GETJUEGO;
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<List<Juego>> response = restTemplate.exchange(
					url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<Juego>>(){});
			
			if (response.getStatusCode().is2xxSuccessful()) {
				lista = response.getBody();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return lista;
		
	}
	
	/**
	 * Listener que gestiona la subida de un zip de juego
	 * @param event Contiene los datos del evento (incluye el archivo)
	 */
	public void handleFileUpload(FileUploadEvent event) {
	  
		//Obtenemos el archivo
		UploadedFile fichero = event.getFile();
		
		//Asignamos el nombre y la ruta donde se guardara el zip
		this.juegoform.setNombreZip(fichero.getFileName().split(".zip")[0]);
		this.juegoform.setRutaZip(Rutas.rutaZip);
		
	    try {
	    	//Guardamos el zip en la ruta correspondiente
	    	fichero.write(Rutas.rutaZip + fichero.getFileName());
	    	this.ficheroSubido = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,Mensajes.HEADERERROR, Mensajes.ERRORGUARDARZIP);
	        FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	/**
	 * Metodo que elimina el zip subido y prepara las variables para poder subir uno nuevo
	 */
	public void cambiarZip() {
		
		//Borramos el zip
		File zip = new File(this.juegoform.getRutaZip()+this.juegoform.getNombreZip()+".zip");
		zip.delete();
		
		//Reseteamos las variables
		this.ficheroSubido = false;
		this.juegoform.setRutaZip("");
		this.juegoform.setNombreZip("");

	}
	
	
	/**
	 * Valida los datos introducidos y añade un nuevo juego
	 */
	public void add() {
	
		//Comprobamos si se ha subido el zip del juego
		if (this.ficheroSubido) {
		
			//Preparamos el juego
			this.juegoform.setId_juego(-1);
			this.juegoform.setEtapa(this.etapasToString());
			this.juegoform.setTipoRespuesta(Integer.parseInt(tipoRespuesta));	
			this.juegoform.setEstiloJuego(estilosJuego_t.valueOf(this.estiloJuego));
			
			//Petición post para añadir el juego
			boolean error = this.postJuego(this.juegoform);
				
			//Si se produce error
			if (error) {
				
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,Mensajes.HEADERERROR, Mensajes.ERRORADDJUEGO);
	            FacesContext.getCurrentInstance().addMessage(null, message);
				
			//Si se añade correctamente
			} else {
				
				//Descomprimimos el fichero
				this.descomprimirZip(this.juegoform.getRutaZip(), this.juegoform.getNombreZip());
				//Copiamos la portada y la captura en la carpeta del despligue
				this.despliegueImagenes(this.juegoform.getNombreZip(), this.juegoform.getRutaZip());
				
				
				//Reseteamos el formulario y actualizamos el listado de juegos
				this.resetForm();
				this.juegosFiltrados = listarJuegos();
	
			}
			
			
			//Eliminamos el zip
			File zip = new File(this.juegoform.getRutaZip()+this.juegoform.getNombreZip()+".zip");
			zip.delete();
		
			
		}else {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,Mensajes.HEADERERROR, Mensajes.ERRORSUBIRZIP);
            FacesContext.getCurrentInstance().addMessage(null, message);
		}

	}
	
	/**
	 * Realiza la petición post para añadir un juego
	 * @param juego Juego a añadir
	 * @return Devuelve 1 si se produce error o 0 si el juego se añade correctamente
	 */
	private boolean postJuego(Juego juego) {
		
		boolean error = false;
		
		try {
			//Registramos el juego
			String url = URLs.GETJUEGO;
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Object> response = restTemplate.postForEntity(url, juego,null);
			
			if (!response.getStatusCode().is2xxSuccessful()) {
				error = true;
			}
		}catch(Exception e) {
			e.printStackTrace();
			error = true;
		}
		
		
		return error;
		
	}
	
	/**
	 * Metodo que reseta el formulario dejandolo vacio para poder añadir un nuevo juego
	 */
	public void resetForm() {
		
        this.juegoform = new Juego();
        this.setEtapasSeleccionadas(null);
        this.setTipoRespuesta("0");
        this.juegoform.setNumFichMin(0);
		this.juegoform.setNumFichMax(0);
		this.juegoform.setPregMin(0);
        this.verModificar = false;
        this.fieldLegend = "Nuevo Juego";
        this.ficheroSubido = false;
    }
	
	
	/**
	 * Método que descomprime el zip del juego
	 * @param ruta Ruta donde se encuentra el zip
	 * @param nombre Nombre del fichero .zip
	 */
	public void descomprimirZip(String ruta,String nombre) {
		

		//Se crea un buffer temporal para el archivo que se va descomprimir
		ZipInputStream zis;
		try {
			zis = new ZipInputStream(new FileInputStream(ruta + nombre + ".zip"));
			
			ZipEntry salida;
			
			//Se recorre todo el buffer extrayendo uno a uno cada archivo del zip y 
			//creándolos de nuevo en su archivo original 
			try {
				while (null != (salida = zis.getNextEntry())) { 
					
					//Si es un directorio...
					if (salida.isDirectory()) {
						//Se crea
			            File directorio = new File(ruta + salida.getName());
			            directorio.mkdir();
			            
			        //Si no.. es un fichero
			        }else {
			        	
			        	//Se guarda
						FileOutputStream fos = new FileOutputStream(ruta + salida.getName());
						int leer;
						byte[] buffer = new byte[1024];
						while (0 < (leer = zis.read(buffer))) {
							fos.write(buffer, 0, leer);
						}
						fos.close();
						zis.closeEntry();
			        }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	
	/**
	 * Método que despliega las imagenes en el directorio web content de la aplicacion
	 * @param nombreJuego Nombre del juego
	 * @param rutaOrigen Ubicación del juego
	 */
	public void despliegueImagenes(String nombreJuego, String rutaOrigen) {
		
		//Creamos los ficheros con las imagenes de caratura y capturas del juego
		String ruta = rutaOrigen+nombreJuego+"/"+Rutas.rutaOrigenImagenes;
		String nombrePortada = nombreJuego.split("jug-")[1]+".png";
		String nombreCaptura = nombreJuego.split("jug-")[1]+"-cap.jpg";
		File portada = new File(ruta+nombrePortada);
		File captura = new File(ruta+nombreCaptura);

		if (portada.isFile()) {
			
			//Fichero de destino de la portada
			File portadaDestino = new File(Rutas.rutaDestinoImagenes+nombrePortada);
			
			//Copiamos la portada en el destino
			this.copiarFicheros(portada, portadaDestino);
		}
		    
	    if (captura.isFile()) {
	    	
	    	//Fichero de destino de la captura
			File capturaDestino = new File(Rutas.rutaDestinoImagenes+nombreCaptura);
			
			//Copiamos la captura en el destino
			this.copiarFicheros(captura, capturaDestino);
		
	    } 
		    
		
	}
	
	
	/**
	 * Metodo auxiliar que copia un fichero en otro
	 * @param origen Fichero de origen
	 * @param destino Fichero de destino
	 */
	public void copiarFicheros(File origen, File destino) {
		try {
			InputStream in = new FileInputStream(origen);
			OutputStream out = new FileOutputStream(destino);
			
		    byte[] buf = new byte[1024];
		    int len;
		     
		    while ((len = in.read(buf)) > 0) {
		      out.write(buf, 0, len);
		    }
		    
		    in.close();
		    out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void modificar() {
		
		this.juegoform.setEtapa(this.etapasToString());
		this.juegoform.setTipoRespuesta(Integer.parseInt(tipoRespuesta));		

		
		String url = URLs.MODJUEGO;
		System.out.println("Petición de modificar juego: " + url);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Estado> response = restTemplate.postForEntity(url, this.juegoform, Estado.class);

		if (response.getBody().isEstado()) {
			System.out.println("Juego modificado correctamente");
		} else {
			System.out.println("Juego modificado fail");

		}
		
		this.juegosFiltrados = listarJuegos();
		this.fieldLegend = this.juegoform.getNombre();

	}
	
public void eliminar() {
		
		String url = URLs.GETJUEGO + this.juegoform.getId_juego() ;
		System.out.println("Petición de elimnar juego: " + url);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete(url);
		
		this.juegosFiltrados = listarJuegos();
		this.fieldLegend = this.juegoform.getNombre();

	}

	
	

	public void onSelectJuego(SelectEvent event) {

		this.juegoform = (Juego) event.getObject();		
		this.setEtapasSeleccionadas(this.etapasToArray(this.juegoform.getEtapa()));
		this.setTipoRespuesta(Integer.toString(this.juegoform.getTipoRespuesta()));
		this.verModificar = true;
		this.fieldLegend = this.juegoform.getNombre();
		this.ficheroSubido = true;
		
	}
	
	
	
public void aplicarFiltros() {
		
		System.out.println("Aplicar filtros");
		
		this.juegosFiltrados = this.listarJuegos();
		
		Boolean pregIlimitadas = this.setPregIlimitadas(this.ilimitadasFiltro);
		
		Iterator<Juego> iter = this.juegosFiltrados.iterator();
		Juego j;
		Boolean pasaFiltros = true;
		while (iter.hasNext()) {
			
			j = iter.next();
			if (j.getPregMin() < this.minPreguntasMinimasFiltro || j.getPregMin() > this.maxPreguntasMinimasFiltro) {
				
				pasaFiltros = false;
				System.out.println("Preguntas minimas no coincide: " + j.getNombre());
				
			}
			
			if (pregIlimitadas != null) {
				
				
				if(pregIlimitadas.compareTo(new Boolean(j.isPregIlimitadas())) != 0){
					pasaFiltros = false;
					System.out.println("Preguntas ilimitadas no coincide: " + j.getNombre());
					
				}
				
			}
			
			if (this.etapasSeleccionadas != null) {
				if(this.etapasSeleccionadas.length != 0){
					if(!existsEtapa(this.etapasSeleccionadas, j.getEtapa())){
						pasaFiltros = false;
						System.out.println("Etapas no coincide: " + j.getNombre());
						
					}
				}
			}
			
			if(this.respuestaSelectFiltro.length != 0){
				if(!existsFormaRespuesta(this.respuestaSelectFiltro, j.getTipoRespuesta())){
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

	private String etapasToString() {
		String e = "";
		for (String i : etapasSeleccionadas) {
			if (e == "") {

				e = i;
			} else {

				e = e + "-" + i;
			}
		}

		return e;
	}
	
	private String[] etapasToArray(String etapasString) {
		return etapasString.split("-");
	}
	
private Boolean setPregIlimitadas(String ilimitadas) {
		
		Boolean pregIlimitadas = null;
		if (ilimitadas != null) {
			if (ilimitadas.contains("1")) {
				pregIlimitadas = new Boolean(true);
	
			}else if (ilimitadas.contains("2"))
			{
				pregIlimitadas = new Boolean(false);
			}
		}
		
		return pregIlimitadas;
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
		
        for (Juego juego : this.juegosListadoCompleto){
            if (id.equals(juego.getId_juego())){
                return juego;
            }
        }
        return null;
	}



	// GETTERS & SETTERS

	public String getTipoRespuesta() {
		return tipoRespuesta;
	}

	public void setTipoRespuesta(String tipoRespuesta) {
		this.tipoRespuesta = tipoRespuesta;
	}


	public String[] getEtapasSeleccionadas() {
		return etapasSeleccionadas;
	}

	public void setEtapasSeleccionadas(String[] etapasSeleccionadas) {
		this.etapasSeleccionadas = etapasSeleccionadas;
	}

	public List<Etapa> getListaEtapas() {
		return listaEtapas;
	}

	public void setListaEtapas(List<Etapa> listaEtapas) {
		this.listaEtapas = listaEtapas;
	}

	public List<Juego> getJuegosListadoCompleto() {
		return juegosListadoCompleto;
	}

	public void setJuegosListadoCompleto(List<Juego> juegosListadoCompleto) {
		this.juegosListadoCompleto = juegosListadoCompleto;
	}

	public List<Juego> getJuegosFiltrados() {
		return juegosFiltrados;
	}

	public void setJuegosFiltrados(List<Juego> juegosFiltrados) {
		this.juegosFiltrados = juegosFiltrados;
	}

	public Juego getJuegoform() {
		return juegoform;
	}

	public void setJuegoform(Juego juegoform) {
		this.juegoform = juegoform;
	}

	public String[] getEtapasFiltro() {
		return etapasFiltro;
	}

	public void setEtapasFiltro(String[] etapasFiltro) {
		this.etapasFiltro = etapasFiltro;
	}

	public String getIlimitadasFiltro() {
		return ilimitadasFiltro;
	}

	public void setIlimitadasFiltro(String ilimitadasFiltro) {
		this.ilimitadasFiltro = ilimitadasFiltro;
	}

	public int getTipoFichFiltro() {
		return tipoFichFiltro;
	}

	public void setTipoFichFiltro(int tipoFichFiltro) {
		this.tipoFichFiltro = tipoFichFiltro;
	}

	public String[] getRespuestaSelectFiltro() {
		return respuestaSelectFiltro;
	}

	public void setRespuestaSelectFiltro(String[] respuestaSelectFiltro) {
		this.respuestaSelectFiltro = respuestaSelectFiltro;
	}

	public int getMinPreguntasMinimasFiltro() {
		return minPreguntasMinimasFiltro;
	}

	public void setMinPreguntasMinimasFiltro(int minPreguntasMinimasFiltro) {
		this.minPreguntasMinimasFiltro = minPreguntasMinimasFiltro;
	}

	public int getMaxPreguntasMinimasFiltro() {
		return maxPreguntasMinimasFiltro;
	}

	public void setMaxPreguntasMinimasFiltro(int maxPreguntasMinimasFiltro) {
		this.maxPreguntasMinimasFiltro = maxPreguntasMinimasFiltro;
	}

	public int getMinPreguntasMaximasFiltro() {
		return minPreguntasMaximasFiltro;
	}

	public void setMinPreguntasMaximasFiltro(int minPreguntasMaximasFiltro) {
		this.minPreguntasMaximasFiltro = minPreguntasMaximasFiltro;
	}

	public int getMaxPreguntasMaximasFiltro() {
		return maxPreguntasMaximasFiltro;
	}

	public void setMaxPreguntasMaximasFiltro(int maxPreguntasMaximasFiltro) {
		this.maxPreguntasMaximasFiltro = maxPreguntasMaximasFiltro;
	}

	public int getMinJugadoresFiltro() {
		return minJugadoresFiltro;
	}

	public void setMinJugadoresFiltro(int minJugadoresFiltro) {
		this.minJugadoresFiltro = minJugadoresFiltro;
	}

	public int getMaxJugadoresFiltro() {
		return maxJugadoresFiltro;
	}

	public void setMaxJugadoresFiltro(int maxJugadoresFiltro) {
		this.maxJugadoresFiltro = maxJugadoresFiltro;
	}

	public boolean isVerModificar() {
		return verModificar;
	}

	public void setVerModificar(boolean verModificar) {
		this.verModificar = verModificar;
	}

	public String getFieldLegend() {
		return fieldLegend;
	}

	public void setFieldLegend(String fieldLegend) {
		this.fieldLegend = fieldLegend;
	}

	public String getEstiloJuego() {
		return estiloJuego;
	}

	public void setEstiloJuego(String estiloJuego) {
		this.estiloJuego = estiloJuego;
	}

	public boolean isFicheroSubido() {
		return ficheroSubido;
	}

	public void setFicheroSubido(boolean ficheroSubido) {
		this.ficheroSubido = ficheroSubido;
	}


}