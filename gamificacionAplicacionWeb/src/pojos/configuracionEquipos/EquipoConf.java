package pojos.configuracionEquipos;

import java.util.List;

public class EquipoConf {
	
	private Configuracion numeroEquipos;
	private List<Equipo> equipos;
	
	
	public EquipoConf(Configuracion numeroEquipos, List<Equipo> equipos) {
		
		this.numeroEquipos = numeroEquipos;
		this.setEquipos(equipos);
	}
	
	
	public Configuracion getNumeroEquipos() {
		return numeroEquipos;
	}
	public void setNumeroEquipos(Configuracion numeroEquipos) {
		this.numeroEquipos = numeroEquipos;
	}


	public List<Equipo> getEquipos() {
		return equipos;
	}


	public void setEquipos(List<Equipo> equipos) {
		this.equipos = equipos;
	}
	

}
