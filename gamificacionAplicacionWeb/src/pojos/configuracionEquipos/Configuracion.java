package pojos.configuracionEquipos;

import java.util.List;

public class Configuracion {
	
	private String textoWeb;
	private Rango rango;
	private List<Variable> variablesJuego;
	
	
	
	public Configuracion(String textoWeb, Rango rango, List<Variable> variablesJuego) {
		
		this.textoWeb = textoWeb;
		this.rango = rango;
		this.variablesJuego = variablesJuego;
	}
	
	public String getTextoWeb() {
		return textoWeb;
	}
	public void setTextoWeb(String textoWeb) {
		this.textoWeb = textoWeb;
	}
	public Rango getRango() {
		return rango;
	}
	public void setRango(Rango rango) {
		this.rango = rango;
	}
	public List<Variable> getVariablesJuego() {
		return variablesJuego;
	}
	public void setVariablesJuego(List<Variable> variablesJuego) {
		this.variablesJuego = variablesJuego;
	}

}
