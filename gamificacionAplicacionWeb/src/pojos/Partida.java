package pojos;

public class Partida {
	
	private int id_partida;
	private int id_profesor;
	private int id_juego;
	private int id_fpreguntas;
	private String titulo;
	private String etapa;
	private String curso;
	private String asignatura;
	private String tema;
	private int porcentaje_correccion;
	private int tiemporespuesta;
	private String parametros;
	private String equipos;
	private String fichero_continuacion;
	private String fecha_inicio;
	private String fecha_update;
	private boolean finalizada;
	
	
	public Partida() {
		
	}
	



	public Partida(int id_profesor, int id_juego, int id_fpreguntas, String titulo, String etapa, String curso,
			String asignatura, String tema, int porcentaje_correccion, int tiemporespuesta, String parametros,
			String equipos, boolean finalizada) {
		super();
		this.id_profesor = id_profesor;
		this.id_juego = id_juego;
		this.id_fpreguntas = id_fpreguntas;
		this.titulo = titulo;
		this.etapa = etapa;
		this.curso = curso;
		this.asignatura = asignatura;
		this.tema = tema;
		this.porcentaje_correccion = porcentaje_correccion;
		this.tiemporespuesta = tiemporespuesta;
		this.parametros = parametros;
		this.equipos = equipos;
		this.finalizada = finalizada;
	}




	public int getId_partida() {
		return id_partida;
	}



	public int getId_juego() {
		return id_juego;
	}


	public int getId_fpreguntas() {
		return id_fpreguntas;
	}


	public String getTitulo() {
		return titulo;
	}


	public int getPorcentaje_correccion() {
		return porcentaje_correccion;
	}


	public String getParametros() {
		return parametros;
	}


	public String getEquipos() {
		return equipos;
	}


	public void setId_partida(int id_partida) {
		this.id_partida = id_partida;
	}



	public void setId_juego(int id_juego) {
		this.id_juego = id_juego;
	}


	public void setId_fpreguntas(int id_fpreguntas) {
		this.id_fpreguntas = id_fpreguntas;
	}


	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}


	public void setPorcentaje_correccion(int porcentaje_correccion) {
		this.porcentaje_correccion = porcentaje_correccion;
	}


	public void setParametros(String parametros) {
		this.parametros = parametros;
	}


	public void setEquipos(String equipos) {
		this.equipos = equipos;
	}


	public boolean isFinalizada() {
		return finalizada;
	}


	public void setFinalizada(boolean finalizada) {
		this.finalizada = finalizada;
	}

	public int getId_profesor() {
		return id_profesor;
	}

	public void setId_profesor(int id_profesor) {
		this.id_profesor = id_profesor;
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



	public int getTiemporespuesta() {
		return tiemporespuesta;
	}



	public void setTiemporespuesta(int tiemporespuesta) {
		this.tiemporespuesta = tiemporespuesta;
	}



	public String getFichero_continuacion() {
		return fichero_continuacion;
	}



	public void setFichero_continuacion(String fichero_continuacion) {
		this.fichero_continuacion = fichero_continuacion;
	}



	public String getFecha_inicio() {
		return fecha_inicio;
	}



	public void setFecha_inicio(String fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}



	public String getFecha_update() {
		return fecha_update;
	}



	public void setFecha_update(String fecha_update) {
		this.fecha_update = fecha_update;
	}
	
	

}
