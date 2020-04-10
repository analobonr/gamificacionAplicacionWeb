package pojos.configuracionEquipos;

public class Variable {
	
	private String nombre;
	private String valor;
	private String textoWeb;
	
	public Variable(String nombre, String valor,String textoWeb) {
	
		this.nombre = nombre;
		this.valor = valor;
		this.setTextoWeb(textoWeb);
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getTextoWeb() {
		return textoWeb;
	}
	public void setTextoWeb(String textoWeb) {
		this.textoWeb = textoWeb;
	}
	
	

}
