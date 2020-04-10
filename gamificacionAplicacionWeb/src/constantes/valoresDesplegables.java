package constantes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pojos.Etapa;

public class valoresDesplegables {
	
	public enum rol_t {USER,ADMIN,SUPER};
	public enum estilosJuego_t {CLASIC,TV,SPORT,AJDA};
	
	public final static List<Etapa> etapasJuegos;
	public final static List<String> rolesList;
	public final static Map<rol_t,String> rolesMap;
	
	static {
		String [] rolesArray = new String[3];
		rolesArray[0] = rol_t.USER.toString();
		rolesArray[1] = rol_t.ADMIN.toString();
		rolesArray[2] = rol_t.SUPER.toString();
		rolesList = Arrays.asList(rolesArray); 
		
		
		etapasJuegos = new ArrayList<Etapa>();
		etapasJuegos.add(new Etapa("GEN", "General"));
		etapasJuegos.add(new Etapa("INF", "Infantil"));
		etapasJuegos.add(new Etapa("PRI", "Primaria"));
		etapasJuegos.add(new Etapa("SEC", "Secundaria"));
		etapasJuegos.add(new Etapa("UNI", "Universidad"));
		
		rolesMap = new HashMap<rol_t, String>();
		rolesMap.put(rol_t.USER, "Usuario");
		rolesMap.put(rol_t.ADMIN, "Administrador");
		rolesMap.put(rol_t.SUPER, "Superusuario");
		
		
	}

   
}
