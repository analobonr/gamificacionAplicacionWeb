package pojos;


public class ConfPartida {
	
	private int id_configuracion;
	private int id_profesor;
	private int id_juego;
	private int id_fpreguntas;
	private String titulo;
	private int porcentaje_correccion;
	private String parametros;
	private String equipos;
	
	
	private String fecha_creacion;
	private String etapa;
	private String curso;
	private String asignatura;
	private String tema;
	private int tiemporespuesta;
	
	
	public ConfPartida() {
		
	}
	
	public ConfPartida(int id_configuracion,int id_profesor, int id_juego, int id_fpreguntas, String titulo, String etapa,
				String curso, String asignatura, String tema, int porcentaje_correccion, 
				String parametros, String equipos,int tiemporespuesta) {
		
		this.setId_configuracion(id_configuracion);
		this.setId_profesor(id_profesor);
		this.setId_juego(id_juego);
		this.setId_fpreguntas(id_fpreguntas);
		this.setTitulo(titulo);
		this.setEtapa(etapa);
		this.setCurso(curso);
		this.setAsignatura(asignatura);
		this.setTema(tema);
		this.setPorcentaje_correccion(porcentaje_correccion);
		this.setParametros(parametros);
		this.setEquipos(equipos);
		this.setTiemporespuesta(tiemporespuesta);
		
		
			
	}

	public String toString() {
		
		return ""+this.getId_configuracion();
	}



	public int getId_configuracion() {
		return id_configuracion;
	}



	public void setId_configuracion(int id_configuracion) {
		this.id_configuracion = id_configuracion;
	}



	public int getId_profesor() {
		return id_profesor;
	}



	public void setId_profesor(int id_profesor) {
		this.id_profesor = id_profesor;
	}



	public int getId_juego() {
		return id_juego;
	}



	public void setId_juego(int id_juego) {
		this.id_juego = id_juego;
	}



	public int getId_fpreguntas() {
		return id_fpreguntas;
	}



	public void setId_fpreguntas(int id_fpreguntas) {
		this.id_fpreguntas = id_fpreguntas;
	}



	public String getTitulo() {
		return titulo;
	}



	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}



	public int getPorcentaje_correccion() {
		return porcentaje_correccion;
	}



	public void setPorcentaje_correccion(int porcentaje_correccion) {
		this.porcentaje_correccion = porcentaje_correccion;
	}



	public String getParametros() {
		return parametros;
	}



	public void setParametros(String parametros) {
		this.parametros = parametros;
	}



	public String getEquipos() {
		return equipos;
	}



	public void setEquipos(String equipos) {
		this.equipos = equipos;
	}



	/*public Date getFecha_creacion() {
		return fecha_creacion;
	}



	public void setFecha_creacion(Date fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}*/



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

	public String getFecha_creacion() {
		return fecha_creacion;
	}

	
	
	public void setFecha_creacion(String fecha_creacion) {
		this.fecha_creacion = fecha_creacion;
	}

	public int getTiemporespuesta() {
		return tiemporespuesta;
	}

	public void setTiemporespuesta(int tiemporespuesta) {
		this.tiemporespuesta = tiemporespuesta;
	}

	
	

}
