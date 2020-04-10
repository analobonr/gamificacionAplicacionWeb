package pojos.configuracionEquipos;

import java.util.List;

public class Equipo {
	
	private Variable nombreEquipo;
	private Configuracion numeroGrupos;
	private List<Variable> grupos;
	
	
	public Configuracion getNumeroGrupos() {
		return numeroGrupos;
	}
	public void setNumeroGrupos(Configuracion numeroGrupos) {
		this.numeroGrupos = numeroGrupos;
	}
	public List<Variable> getGrupos() {
		return grupos;
	}
	public void setGrupos(List<Variable> grupos) {
		this.grupos = grupos;
	}
	public Variable getNombreEquipo() {
		return nombreEquipo;
	}
	public void setNombreEquipo(Variable nombreEquipo) {
		this.nombreEquipo = nombreEquipo;
	}
	
	


}
