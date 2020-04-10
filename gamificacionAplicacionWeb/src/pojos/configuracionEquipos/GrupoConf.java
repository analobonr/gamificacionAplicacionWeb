package pojos.configuracionEquipos;


public class GrupoConf {
	
	private Configuracion nombresGrupos;
	private Configuracion numeroGrupos;
	private Configuracion valoresIniciales;
	
	
	
	public GrupoConf(Configuracion nombresGrupos, Configuracion numeroGrupos, Configuracion valoresIniciales) {
		super();
		this.nombresGrupos = nombresGrupos;
		this.numeroGrupos = numeroGrupos;
		this.valoresIniciales = valoresIniciales;
	}

	public Configuracion getNombresGrupos() {
		return nombresGrupos;
	}
	
	public void setNombresGrupos(Configuracion nombresGrupos) {
		this.nombresGrupos = nombresGrupos;
	}
	
	public Configuracion getNumeroGrupos() {
		return numeroGrupos;
	}
	
	public void setNumeroGrupos(Configuracion numeroGrupos) {
		this.numeroGrupos = numeroGrupos;
	}

	public Configuracion getValoresIniciales() {
		return valoresIniciales;
	}

	public void setValoresIniciales(Configuracion valoresIniciales) {
		this.valoresIniciales = valoresIniciales;
	}

}
