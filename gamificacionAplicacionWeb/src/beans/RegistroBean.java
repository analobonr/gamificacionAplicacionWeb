package beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.primefaces.context.RequestContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import constantes.Mensajes;
import constantes.URLs;
import pojos.Estado;
import pojos.Profesor;
import constantes.valoresDesplegables.rol_t;

@ManagedBean
public class RegistroBean implements Serializable {
	
	private static final long serialVersionUID = 3885381981170292304L;
	
	//Propiedades para el formulario
	private String nombre;
	private String apellidos;
	private String pais;
	private String email;
	private String password;
	private rol_t rol = rol_t.USER;
	private String rolElegido;
	
	private List<String> rolesList;
	
	
	//Identifica si se esta gestionando usuarios como superusuario
	private boolean esSuperusuario = false;
	
	
	/**Registra un nuevo usuario**/
	public void registrar() {
		
		//Si el usuario lo esta creando el superusuario
		if (esSuperusuario) {
			
			//Creamos el usuario con una contraseña por defecto
			this.password = "defaultPass";
			
			//Asignamos el rol correspondiente
			switch(this.rolElegido) {
				case("ADMIN"):
					this.rol = rol_t.ADMIN;
				break;
				case("SUPER"):
					this.rol = rol_t.SUPER;
				break;
			}
			
		}
		
		//Creamos el usuario
		Profesor p = new Profesor(new Integer(-1),email,nombre,apellidos,password, pais,rol,new Date(),null);
		boolean nuevoUsuario = this.nuevoUsuario(p);
		
		
		//Si se ha creado el usuario correctamente y lo crea el superusuario
		if (nuevoUsuario && esSuperusuario) {
			
			 //Se autogenera la contraseña para el usuario y se le envia por correo
			 this.autogenerarPass(p.getEmail());
			 
			 //Se recarga la pagina de gestion de usuarios
			 try{
		            FacesContext contex = FacesContext.getCurrentInstance();
		            contex.getExternalContext().redirect( URLs.URLGestUsuarios );
			}catch(  Exception e ){
					System.err.println( "Error al redireccionar a: " + URLs.URLGestUsuarios );
					System.err.println(e.getMessage());
			}		 
		 }
	}
	
	public boolean nuevoUsuario(Profesor p) {
		
		boolean nuevoUsuario = true;
		
		//Peticion POST para insertar en la base de datos
		 RestTemplate restTemplate = new RestTemplate();
		 String url = constantes.URLs.USUARIO;
		 System.out.println(url);
		 ResponseEntity<Estado> response = restTemplate.postForEntity(url, p, Estado.class);
		 Estado state = response.getBody();
		 FacesContext fContext = FacesContext.getCurrentInstance();
		 
		 //Error
		 if(!response.getStatusCode().is2xxSuccessful() || !state.isEstado()) {
			 System.out.println("No se ha podido añadir el profesor");
			 
			fContext.addMessage(null, new FacesMessage("ERROR", "No se ha podido añadir el profesor"));
			nuevoUsuario = false;
			
		//Usuario añadido OK
		 }else {
			 System.out.println("Profesor añadido correctamente");
			 
			fContext.addMessage(null, new FacesMessage(Mensajes.HEADERNEWUSER, Mensajes.NEWUSER.replace("[MAIL]", email)));
			 this.resetear();
			 RequestContext context = RequestContext.getCurrentInstance();
			 context.execute("PF('registroDialog').hide();");
		 }
		 
		 return nuevoUsuario;
	}
	
	public void autogenerarPass(String email) {
		
		//Peticion para obtener una nueva password para el usuario
		String url = constantes.URLs.USUARIO + email;
		System.out.println("Petición nueva pass: "+url);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Profesor> response = restTemplate.exchange(
				url ,HttpMethod.GET, null, new ParameterizedTypeReference<Profesor>(){});
		
		
		//Obtenemos la respuesta
    	if(!response.getStatusCode().is2xxSuccessful()) {
			 System.out.println("La petición ha fallado");
			 FacesContext context = FacesContext.getCurrentInstance();
		     context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERRORNEWPASS));
			 
		//Enviamos el mail con la nueva password	 
		}else {
			Profesor p = response.getBody();
		
			//Configuramos las propiedades
			Properties properties = new Properties();
			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.port","587");
			properties.put("mail.smtp.auth", "true");
	 
			//Preparamos la sesion
			Session session = Session.getDefaultInstance(properties);
			
			String correoRemitente = "adjamail52@gmail.com";
			String passwordRemitente = "ju3g0sd1d4ct1c0s";
			String correoReceptor = email;
			String asunto = Mensajes.ASUNTOMAIL;
			String mensaje =  Mensajes.MENSAJEMAIL.replace("[NEWPASS]", p.getPassword());
			
			try{
				//Construimos el mensaje
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(correoRemitente));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoReceptor));
				message.setSubject(asunto);
				message.setText(mensaje);
				
				//Enviamos el correo
				Transport t = session.getTransport("smtp");
				t.connect(correoRemitente, passwordRemitente);
				t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
				t.close();
				
				System.out.println("Correo enviado correctamente");
				FacesContext context = FacesContext.getCurrentInstance();
		        context.addMessage(null, new FacesMessage(Mensajes.HEADERNEWPASS,  Mensajes.NEWPASS.replace("[MAIL]", correoReceptor)));
		        
		        
			}catch (MessagingException me){
	                
				System.err.println("Error al enviar email: "+ me);
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR,  Mensajes.ERRORNEWPASS));
			}
		 }
	}
	
	
	public void resetear() {
		nombre = "";
		apellidos = "";
		pais ="";
		email="";
		password="";
		rol = rol_t.USER;
	}


	//GETTERS & SETTERS
	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getApellidos() {
		return apellidos;
	}


	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}


	public String getPais() {
		return pais;
	}


	public void setPais(String pais) {
		this.pais = pais;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}



	public boolean isEsSuperusuario() {
		return esSuperusuario;
	}

	public void setEsSuperusuario(boolean esSuperusuario) {
		this.esSuperusuario = esSuperusuario;
	}


	public String getRol() {
		return rol.toString();
	}


	public void setRol(String rol) {
		this.rol = rol_t.valueOf(rol);
	}


	public String getRolElegido() {
		return rolElegido;
	}


	public void setRolElegido(String rolElegido) {
		this.rolElegido = rolElegido;
	}
	
}
