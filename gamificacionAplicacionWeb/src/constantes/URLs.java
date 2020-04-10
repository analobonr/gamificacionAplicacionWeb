package constantes;

public class URLs {
	//URL de la pagina de login
	public static final String URLlogin = "http://localhost:8081/gamificacionAplicacionWeb/login.xhtml";
	public static final String pathlogin = "/login.xhtml";
	public static final String URLGestUsuarios = "http://localhost:8081/gamificacionAplicacionWeb/indexUsuarios.xhtml";
	public static final String URLIndex = "http://localhost:8081/gamificacionAplicacionWeb/index.xhtml";
	
	//URL del servicio REST
	public static final String URL = "http://localhost:8080/";
	
    
    //Usuarios
    public static final String USUARIO = URL + "usuarios/";
    public static final String MODUSUARIO = URL + "usuarios/modificar";
    public static final String LISTUSUARIOS = URL + "usuarios/listar/";

    //Configuración de partidas
    public static final String LISTCONFPARTIDAS = URL + "confPartidas/listar/";
    public static final String NUEVACONFPARTIDA = URL + "confPartidas";
    public static final String GETCONFPARTIDA = URL + "confPartidas/";
    public static final String CONFPARTIDA = URL + "confPartidas/";
    public static final String MODCONFPARTIDA = URL + "confPartidas/modificar";
    
    //Partidas
    public static final String LISTPARTIDAS = URL + "partidas/listar/";
    
    //Juegos
    public static final String GETJUEGO = URL + "juegos/";
    public static final String MODJUEGO = URL + "juegos/modificar";
    
}
