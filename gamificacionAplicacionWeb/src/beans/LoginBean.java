package beans;

import java.io.IOException;
import java.io.Serializable;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import constantes.Configuracion;
import constantes.Mensajes;
import constantes.URLs;
import pojos.Profesor;

/**
 * Bean para gestionar el acceso a la aplicación
 * 
 * @author: Ana Lobón
 * @version: 1.0 (03/05/2020)
 */

@ManagedBean
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 2057658592045182866L;

	// Propiedades para recoger los datos del formulario
	private String email;
	private String password;


	/**
	 * Método que procesa el login
	 */
	public void enviarDatos() {

		// Comprobamos que se ha introducido mail y password
		if ((this.email != "") && (this.password != "")) {

			// Peticion Rest para obtener el usuario
			String url = URLs.USUARIO + this.getEmail() + "/" + this.getPassword();
			
			try {
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<Profesor> response = restTemplate.exchange(url, HttpMethod.GET, null,
						new ParameterizedTypeReference<Profesor>() {
						});

				// Obtenemos la respuesta
				Profesor p = response.getBody();

				//Si hay error mostramos mensaje de error
				if ((!response.getStatusCode().is2xxSuccessful()) || (p == null) || (p.getEmail().length() == 0)) {
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage(Mensajes.HEADERUPS, Mensajes.ERRORACCESO));
				
				//Si no hay error...
				} else {
					
					// Iniciamos la sesion del usuario
					this.iniciarSesion(p);

					//Redirigimos al index (menu principal)
					FacesContext contex = FacesContext.getCurrentInstance();
					try {
						contex.getExternalContext().redirect(URLs.URLIndex);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						FacesContext contextEx = FacesContext.getCurrentInstance();
						contextEx.addMessage(null, new FacesMessage(Mensajes.HEADERUPS, Mensajes.ERRORREDIRECCION));
					}
				}
			} catch (HttpServerErrorException httpe) {
				httpe.printStackTrace();
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(Mensajes.HEADERUPS, Mensajes.ERRORACCESO));
			}
		}else {
			FacesContext contextEx = FacesContext.getCurrentInstance();
			contextEx.addMessage(null, new FacesMessage(Mensajes.HEADERERROR, Mensajes.ERRORVALIDACIONACCESO));
		}

	}

	/**
	 * Método que genera una nueva password y la envía al email registrado
	 * 
	 */
	public void enviarEmail() {

		//Si se ha establecido email del usuario
		if (email != "") {

			// Peticion GET para obtener una nueva password para el usuario
			String url = constantes.URLs.USUARIO + this.email;
			
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Profesor> response = restTemplate.exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<Profesor>() {
					});

			//Si hay error mostramos mensaje de error
			if (!response.getStatusCode().is2xxSuccessful()) {
				
				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR, Mensajes.ERRORNEWPASS));

			//Si no hay error...
			} else {
				
				//Obtenemos el obtenemos el objeto con la info del profesor
				Profesor p = response.getBody();
				
				//ENVIAMOS POR CORREO ELECTRONICO LA NUEVA PASS
				
				// Configuramos las propiedades
				Properties properties = new Properties();
				properties.put("mail.smtp.host", "smtp.gmail.com");
				properties.put("mail.smtp.starttls.enable", "true");
				properties.put("mail.smtp.port", "587");
				properties.put("mail.smtp.auth", "true");

				//Preparamos la sesion
				Session session = Session.getDefaultInstance(properties);

				// Preparamos el mensaje
				String correoRemitente = Configuracion.REM_EMAIL;
				String passwordRemitente = Configuracion.REM_PASSEMAIL;
				String correoReceptor = email;
				String asunto = Mensajes.ASUNTOMAIL;
				String mensaje = Mensajes.MENSAJEMAIL.replace("[NEWPASS]", p.getPassword());

				try {
					// Construimos el mensaje
					MimeMessage message = new MimeMessage(session);
					message.setFrom(new InternetAddress(correoRemitente));
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoReceptor));
					message.setSubject(asunto);
					message.setText(mensaje);

					// Enviamos el correo
					Transport t = session.getTransport("smtp");
					t.connect(correoRemitente, passwordRemitente);
					t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
					t.close();

					// Mensaje informativo 
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage(Mensajes.HEADERNEWPASS,
							Mensajes.NEWPASS.replace("[MAIL]", correoReceptor)));

				} catch (MessagingException me) {
					
					FacesContext context = FacesContext.getCurrentInstance();
					context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR, Mensajes.ERRORNEWPASS));
				}
			}
		} else {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage(Mensajes.HEADERERROR, Mensajes.ERRORNEWPASS2));
		}

	}

	/**
	 * Método que cierra la sesión del usuario activo
	 */
	public void cerrarSesion() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		HttpSession userSession = request.getSession(true);
		userSession.invalidate();
	}
	
	
	/**
	 * Método que inicia una sesión para el usuario
	 * @param p Profesor
	 */
	public void iniciarSesion(Profesor p) {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		HttpSession userSession = request.getSession(true);
		userSession.setAttribute("profesor", p);
	}

	// GETTERS & SETTERS

	/**
	 * Devuelve el email del usuario
	 * 
	 * @return Email del usuario
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Asigna el email del usuario
	 * 
	 * @param email Email del usuario
	 */

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Devuelve la contraseña del usuario
	 * 
	 * @return Contraseña del usuario
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Asigna la contraseña del usuario
	 * 
	 * @param password Contraseña del usuario
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
