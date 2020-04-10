package pojos;

import constantes.valoresDesplegables.estilosJuego_t;

public class Juego {

	private int idJuego;
	private String nombre;
	private String descripcion;
	private String etapa;
	private int tipoRespuesta;
	private estilosJuego_t estiloJuego;
	private boolean pregIlimitadas;
	private boolean cargaCompleta;
	private int tipoFich;
	private int pregMin;
	private int numFichMin;
	private int numFichMax;
	private String jsonRespuestas;
	private String jsonParametrosConf;
	private String jsonEquipos;
	private String jsonOtrosDatos;
	private String nombreZip;
	private String rutaZip;

	public Juego() {

	}

	public Juego(String nombre, String descripcion, String etapa, int tipoRespuesta, estilosJuego_t estiloJuego,
			boolean pregIlimitadas, boolean cargaCompleta, int tipoFich, int pregMin, int numFichMin, int numFichMax,
			String jsonRespuestas, String JSONparametrosConf, String JSONequipos, String JSONotrosDatos,
			String nombreZip, String rutaZip) {

		this.setIdJuego(-1);
		this.setNombre(nombre);
		this.setDescripcion(descripcion);
		this.setEtapa(etapa);
		this.setTipoRespuesta(tipoRespuesta);
		this.setEstiloJuego(estiloJuego);
		this.setJsonParametrosConf(JSONparametrosConf);
		this.setJsonEquipos(JSONequipos);
		this.setJsonRespuestas(jsonRespuestas);
		this.setJsonOtrosDatos(JSONotrosDatos);
		this.setTipoFich(tipoFich);
		this.setPregMin(pregMin);
		this.setPregIlimitadas(pregIlimitadas);
		this.setNumFichMin(numFichMin);
		this.setNumFichMax(numFichMax);
		this.setCargaCompleta(cargaCompleta);
		this.setNombreZip(nombreZip);
		this.setRutaZip(rutaZip);
		

	}

	public int getIdJuego() {
		return idJuego;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getEtapa() {
		return etapa;
	}

	public void setEtapa(String etapa) {
		this.etapa = etapa;
	}


	public int getTipoFich() {
		return tipoFich;
	}

	public int getPregMin() {
		return pregMin;
	}

	public boolean isPregIlimitadas() {
		return pregIlimitadas;
	}

	public int getNumFichMin() {
		return numFichMin;
	}

	public int getNumFichMax() {
		return numFichMax;
	}

	public boolean isCargaCompleta() {
		return cargaCompleta;
	}

	public void setIdJuego(int idJuego) {
		this.idJuego = idJuego;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	

	public void setTipoFich(int tipoFich) {
		this.tipoFich = tipoFich;
	}

	public void setPregMin(int pregMin) {
		this.pregMin = pregMin;
	}

	public void setPregIlimitadas(boolean pregIlimitadas) {
		this.pregIlimitadas = pregIlimitadas;
	}

	public void setNumFichMin(int numFichMin) {
		this.numFichMin = numFichMin;
	}

	public void setNumFichMax(int numFichMax) {
		this.numFichMax = numFichMax;
	}

	public void setCargaCompleta(boolean cargaCompleta) {
		this.cargaCompleta = cargaCompleta;
	}

	

	public int getTipoRespuesta() {
		return tipoRespuesta;
	}

	public void setTipoRespuesta(int tipoRespuesta) {
		this.tipoRespuesta = tipoRespuesta;
	}

	public estilosJuego_t getEstiloJuego() {
		return estiloJuego;
	}

	public void setEstiloJuego(estilosJuego_t estiloJuego) {
		this.estiloJuego = estiloJuego;
	}

	public String getNombreZip() {
		return nombreZip;
	}

	public void setNombreZip(String nombreZip) {
		this.nombreZip = nombreZip;
	}

	public String getRutaZip() {
		return rutaZip;
	}

	public void setRutaZip(String rutaZip) {
		this.rutaZip = rutaZip;
	}

	public String getJsonRespuestas() {
		return jsonRespuestas;
	}

	public void setJsonRespuestas(String jsonRespuestas) {
		this.jsonRespuestas = jsonRespuestas;
	}

	public String getJsonParametrosConf() {
		return jsonParametrosConf;
	}

	public void setJsonParametrosConf(String jsonParametrosConf) {
		this.jsonParametrosConf = jsonParametrosConf;
	}

	public String getJsonEquipos() {
		return jsonEquipos;
	}

	public void setJsonEquipos(String jsonEquipos) {
		this.jsonEquipos = jsonEquipos;
	}

	public String getJsonOtrosDatos() {
		return jsonOtrosDatos;
	}

	public void setJsonOtrosDatos(String jsonOtrosDatos) {
		this.jsonOtrosDatos = jsonOtrosDatos;
	}

}
