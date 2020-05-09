package beans;

import java.io.Serializable;
import java.util.Date;
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


/**
 * Bean para gestionar el registro en la aplicación
 * @author Ana Lobón
 * @version: 1.0 (03/05/2020)
 *
 */
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
	
	
	//Identifica si se esta gestionando usuarios como superusuario
	private boolean esSuperusuario = false;
	
	
	/**
	 * Método que recoge los datos y registra un nuevo usuario
	 */
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
	
	
	/**
	 * Método que realiza la petición POST para crear un nuevo usuario en el sistema
	 * @param p Datos del profesor a registrar
	 * @return Estado de la creación del usuario
	 */
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
	
	
	
	/**
	 * Método que autogenera una nueva contraseña para un usuario
	 * @param email Correo electrónico del usuario
	 */
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
	
	
	
	/**
	 * Método que resetea las variables usadas en el formulario de registro
	 */
	public void resetear() {
		nombre = "";
		apellidos = "";
		pais ="";
		email="";
		password="";
		rol = rol_t.USER;
	}


	//GETTERS & SETTERS
	
	/**
	 * Método get para el nombre
	 * @return Nombre del usuario
	 */
	public String getNombre() {
		return nombre;
	}


	/**
	 * Método set para el nombre del usuario
	 * @param nombre Nombre del usuario
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	/**
	 * Método get para los apellidos del usuario
	 * @return Apellidos del usuario
	 */
	public String getApellidos() {
		return apellidos;
	}


	/**
	 * Método set para los apellidos del usuario
	 * @param apellidos Apellidos del usuario
	 */
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}


	/**
	 * Método get para el país del usuario
	 * @return País del usuario
	 */
	public String getPais() {
		return pais;
	}

	/**
	 * Método set para el país del usuario
	 * @param pais País del usuario
	 */
	public void setPais(String pais) {
		this.pais = pais;
	}


	/**
	 * Método get para el correo electrónico del usuario
	 * @return Correo electrónico del usuario
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Método set para el correo electrónico del usuario
	 * @param email Correo electrónico del usuario
	 */
	public void setEmail(String email) {
		this.email = email;
	}


	/**
	 * Método get para la contraseña del usuario
	 * @return Contraseña del usuario
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * Método set para la contraseña del usuario
	 * @param password Contraseña del ususario
	 */
	public void setPassword(String password) {
		this.password = password;
	}



	/**
	 * Método que indica si el usuario loggeado es superusuario
	 * @return Usuario loggeado es superusuario o no
	 */
	public boolean isEsSuperusuario() {
		return esSuperusuario;
	}

	/**
	 * Método que establece si el usuario loggeado es superusuario
	 * @param esSuperusuario Usuario loggeado es superusuario o no
	 */
	public void setEsSuperusuario(boolean esSuperusuario) {
		this.esSuperusuario = esSuperusuario;
	}


	/**
	 * Método get para el rol del usuario
	 * @return Rol del usuario (Usuario, Administrador o Superusuario)
	 */
	public String getRol() {
		return rol.toString();
	}


	/**
	 * Método set para el rol del usuario
	 * @param rol Rol del usuario
	 */
	public void setRol(String rol) {
		this.rol = rol_t.valueOf(rol);
	}


	/**
	 * Método get para el rol seleccionado en el formulario de registro
	 * @return Rol seleccionado en el formulario
	 */
	public String getRolElegido() {
		return rolElegido;
	}


	/**
	 * Método set para el rol seleccionado en el formulario de registro
	 * @param rolElegido Rol seleccionado en el formulario
	 */
	public void setRolElegido(String rolElegido) {
		this.rolElegido = rolElegido;
	}
	
}
