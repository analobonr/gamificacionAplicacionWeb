package pojos.configuracionEquipos;

public class ParametrosGE {
	
	private GrupoConf grupos;
	private EquipoConf equipos;

	public ParametrosGE(GrupoConf grupos) {
		
		this.grupos = grupos;
	}

	public GrupoConf getGrupos() {
		return grupos;
	}

	public void setGrupos(GrupoConf grupos) {
		this.grupos = grupos;
	}

	public EquipoConf getEquipos() {
		return equipos;
	}

	public void setEquipos(EquipoConf equipos) {
		this.equipos = equipos;
	}
	

}
