package pojos;



import constantes.valoresDesplegables.rol_t;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Profesor{
	
	private Integer id;
	private String email;
	private String nombre;
	private String apellidos;
	private String password;
	private String pais;
	private rol_t rol;
	private String fecha_registro;
	private String f_ultimo_acceso;
	
	

	public Profesor() {
		
	}
	
	public Profesor(Integer id,String email, String nombre, String apellidos, String password,
			String pais, rol_t rol, Date fecha_registro, Date f_ultimo_acceso) {
		
		this.setId(id);
		this.setEmail(email);
		this.setNombre(nombre);
		this.setApellidos(apellidos);
		this.setPassword(password);
		this.setPais(pais);
		this.setRol(rol);
		this.setFecha_registro(fecha_registro);
		this.setF_ultimo_acceso(f_ultimo_acceso);
		
	}
	
	

	public String getEmail() {
		return email;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}


	public void setEmail(String email) {
		this.email = email;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	public String getFecha_registro() {
		return fecha_registro;
	}

	public void setFecha_registro(Date fecha_registro) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			this.fecha_registro = formatter.format(fecha_registro);
		}catch(Exception e) {
			this.fecha_registro = null;
		}
		System.out.println(this.fecha_registro);
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public rol_t getRol() {
		return rol;
	}

	public void setRol(rol_t rol) {
		this.rol = rol;
	}

	public String getRolToString() {
		return rol.toString();
	}
	
	public void setRolToString(String rolToString) {
		this.rol = rol_t.valueOf(rolToString);
	}

	public String getF_ultimo_acceso() {
		return f_ultimo_acceso;
	}

	public void setF_ultimo_acceso(Date f_ultimo_acceso) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			this.f_ultimo_acceso = formatter.format(f_ultimo_acceso);
		}catch(Exception e) {
			this.f_ultimo_acceso = null;
		}
		System.out.println(this.fecha_registro);

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	/*public String getUltimoRegistroText() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return formatter.format(this.f_ultimo_acceso);
	}*/	
	
	
}