package loadbalancer;

public class PerformRequest {
	private String route;
	private int port;
	private String address;
	private String serverRepository;
	private boolean isValid;
	public PerformRequest(String _route, String _address, int _port, boolean _isValid) {
		// TODO Auto-generated constructor stub
		route = _route;
		port = _port;
		address = _address;
		isValid = _isValid;
	}
	public String getDisplay(){
		if(!isValid){
			return new String("Erreur de configuration, nombre de serveurs actifs supérieur au nombre de serveurs de la ferme");
		}
		return new String("");
	};
}
