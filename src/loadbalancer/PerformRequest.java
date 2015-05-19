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
			return new String("Erreur dans le fichier de config, un ou plusieurs serveurs de la liste workers n'existent pas");
		}
		return new String("");
	};
}
