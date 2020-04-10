package pojos;

public class Etapa {
	
	String id;
	String descripcion;
	
	
	
	public Etapa(String id, String descripcion) {
		super();
		this.id = id;
		this.descripcion = descripcion;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	

}
