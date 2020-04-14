package beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
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

/*Bean que maneja los datos de partidaForm para la configuración de una partida*/

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
	
	private boolean ficheroSubido;

	@PostConstruct
	public void init() {
		
		//Obtenemos la session del usuario
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpSession userSession = request.getSession(true);
		login = (Profesor) userSession.getAttribute("profesor");
		
		ExternalContext eContext = FacesContext.getCurrentInstance().getExternalContext();

		if((login == null) || (login.getRol() == rol_t.USER)) {
			try{
				
	            eContext.redirect( eContext.getRequestContextPath() + URLs.pathlogin );
			}catch(  Exception e ){
				System.out.println( "Me voy al carajo, no funciona esta redireccion" );
				
			}
			
		}else {
			this.juegoform = new Juego();
			this.juegosListadoCompleto = this.listarJuegos();
			this.juegosFiltrados = this.juegosListadoCompleto;
			this.verModificar = false;
			this.fieldLegend = "Nuevo Juego";
			this.ficheroSubido = false;
		}
	}

	public void add() {
	
		//Comprobamos si se ha subido el zip del juego
		
		if (this.ficheroSubido) {
		
			this.juegoform.setIdJuego(-1);
			this.juegoform.setEtapa(this.etapasToString());
			this.juegoform.setTipoRespuesta(Integer.parseInt(tipoRespuesta));	
			this.juegoform.setEstiloJuego(estilosJuego_t.valueOf(this.estiloJuego));
			
			Gson gson = new Gson();
			String juegoString = gson.toJson(this.juegoform);
			System.out.println("juegoString: "+juegoString);
		
			//Registramos el juego
			String url = URLs.GETJUEGO;
			System.out.println("Petición de añadir juego: " + url);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Estado> response = restTemplate.postForEntity(url, this.juegoform, Estado.class);
	
			//Si se registro OK
			if (response.getBody().isEstado()) {
				System.out.println("Juego añadido correctamente");
				
				//Descomprimimos el fichero
				this.descomprimirZip(this.juegoform.getRutaZip(), this.juegoform.getNombreZip());
				//Copiamos la portada y la captura en la carpeta del despligue
				this.despliegueImagenes(this.juegoform.getNombreZip(), this.juegoform.getRutaZip());
				
			//Si el registro no OK
			} else {
				System.out.println("Juego añadido fail");
				
				//Eliminamos el zip
				File zip = new File(this.juegoform.getRutaZip()+this.juegoform.getNombreZip());
				
				if(zip.delete()) {
					System.out.println("Archivo eliminado");
				}else {
					System.out.println("Error al eliminar el archivo");
				}
	
			}
			this.resetForm();
			this.juegosFiltrados = listarJuegos();
		}else {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,Mensajes.HEADERERROR, Mensajes.ERRORSUBIRZIP);
            FacesContext.getCurrentInstance().addMessage(null, message);
		}



		

	}
	
	public void descomprimirZip(String ruta,String nombre) {
		

		//crea un buffer temporal para el archivo que se va descomprimir
		ZipInputStream zis;
		try {
			zis = new ZipInputStream(new FileInputStream(ruta + nombre + ".zip"));
			
			ZipEntry salida;
			
			//recorre todo el buffer extrayendo uno a uno cada archivo del zup y 
			//creándolos de nuevo en su archivo original 
			try {
				while (null != (salida = zis.getNextEntry())) {
					
					
					if (salida.isDirectory()) {
						System.out.println("Nombre del directorio: "+salida.getName());	
			            File directorio = new File(ruta + salida.getName());
			            directorio.mkdir();
			        }else {
			        	System.out.println("Nombre del Archivo: "+salida.getName());	
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
	
	public void cambiarZip() {
		this.ficheroSubido = false;
		this.juegoform.setRutaZip("");
		this.juegoform.setNombreZip("");
		File zip = new File(this.juegoform.getRutaZip()+this.juegoform.getNombreZip());
		zip.delete();
	}
	
	public void despliegueImagenes(String nombreJuego, String rutaOrigen) {
		
	
		System.out.println(rutaOrigen+nombreJuego+"/"+Rutas.rutaOrigenImagenes);
		String ruta = rutaOrigen+nombreJuego+"/"+Rutas.rutaOrigenImagenes;
		String nombrePortada = nombreJuego.split("jug-")[1]+".png";
		String nombreCaptura = nombreJuego.split("jug-")[1]+"-cap.jpg";
		File portada = new File(ruta+nombrePortada);
		File captura = new File(ruta+nombreCaptura);
		
		if (portada.isFile()) {
			File portadaDestino = new File(Rutas.rutaDestinoImagenes+nombrePortada);
			
			this.copiarFicheros(portada, portadaDestino);
			System.out.println("Portada copiada a: "+Rutas.rutaDestinoImagenes+nombrePortada);
		}
		    
	    if (captura.isFile()) {
			File capturaDestino = new File(Rutas.rutaDestinoImagenes+nombreCaptura);
			this.copiarFicheros(captura, capturaDestino);
			System.out.println("Captura copiada a: "+Rutas.rutaDestinoImagenes+nombreCaptura);
		
	    } 
		    
		
	}
	
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
		
		String url = URLs.GETJUEGO + this.juegoform.getIdJuego() ;
		System.out.println("Petición de elimnar juego: " + url);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete(url);
		
		this.juegosFiltrados = listarJuegos();
		this.fieldLegend = this.juegoform.getNombre();

	}

	public List<Juego> listarJuegos() {
		String url = URLs.GETJUEGO;
		System.out.println("Petición de obtener el juego: " + url);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<Juego>> response = restTemplate.exchange(
				url ,HttpMethod.GET, null, new ParameterizedTypeReference<List<Juego>>(){});
		
		return response.getBody();
		
		
	}

	public void onSelectJuego(SelectEvent event) {

		this.juegoform = (Juego) event.getObject();		
		this.setEtapasSeleccionadas(this.etapasToArray(this.juegoform.getEtapa()));
		this.setTipoRespuesta(Integer.toString(this.juegoform.getTipoRespuesta()));
		this.verModificar = true;
		this.fieldLegend = this.juegoform.getNombre();
		this.ficheroSubido = true;
		
	}
	
	public void resetForm() {
        this.juegoform = new Juego();
        this.setEtapasSeleccionadas(null);
        this.setTipoRespuesta("0");
        this.verModificar = false;
        this.fieldLegend = "Nuevo Juego";
        this.ficheroSubido = false;
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
            if (id.equals(juego.getIdJuego())){
                return juego;
            }
        }
        return null;
	}

public void handleFileUpload(FileUploadEvent event) {
  
	UploadedFile fichero = event.getFile();
	this.juegoform.setNombreZip(fichero.getFileName().split(".zip")[0]);
	this.juegoform.setRutaZip(Rutas.rutaZip);
	
    try {
    	fichero.write(Rutas.rutaZip + fichero.getFileName());
    	this.ficheroSubido = true;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
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