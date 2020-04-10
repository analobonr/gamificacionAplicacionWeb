package pojos.configuracionEquipos;

public class Rango {
	
	private Integer valorMinimo;
	private Integer valorMaximo;
	private Integer valorDefecto;
	
	
	public Rango(Integer valorMinimo,Integer valorMaximo, Integer valorDefecto){
		this.setValorDefecto(valorDefecto);
		this.setValorMaximo(valorMaximo);
		this.setValorMinimo(valorMinimo);
	}
	
	public Integer getValorMinimo() {
		return valorMinimo;
	}
	public void setValorMinimo(Integer valorMinimo) {
		this.valorMinimo = new Integer(valorMinimo);
	}
	public Integer getValorMaximo() {
		return valorMaximo;
	}
	public void setValorMaximo(Integer valorMaximo) {
		this.valorMaximo = new Integer(valorMaximo);
	}
	public Integer getValorDefecto() {
		return valorDefecto;
	}
	public void setValorDefecto(Integer valorDefecto) {
		this.valorDefecto = new Integer(valorDefecto);
	}
	
	
	

}
