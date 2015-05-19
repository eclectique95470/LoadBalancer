package loadbalancer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import loadbalancer.PerformRequest;

public class LoadBalancer {
	private Map<String,String> workermap = new HashMap<>();
	private Map<String,String> lbmap = new HashMap<>();
	public LoadBalancer() throws IOException {
		init();
		run();
	}
	private void init()
	{
		BufferedReader br = null;
		String sCurrentLine;
		try {
			br = new BufferedReader(new FileReader("config.ini"));
			String key = "";
			String value="";
			while ((sCurrentLine = br.readLine()) != null) {
				if(0 == sCurrentLine.indexOf("worker")){
					key = sCurrentLine.substring(0, sCurrentLine.indexOf('='));
					value = sCurrentLine.substring(sCurrentLine.indexOf('=')+1, sCurrentLine.length());
					workermap.put(key.substring(key.indexOf('.')+1, key.length()), value);
				}else if(sCurrentLine.contains("lb")){
					key = sCurrentLine.substring(0, sCurrentLine.indexOf('='));
					value = sCurrentLine.substring(sCurrentLine.indexOf('=')+1, sCurrentLine.length());
					lbmap.put(key.substring(key.indexOf('.')+1, key.length()), value);
				}
			}
			System.out.println(workermap);
			System.out.println(lbmap);
		}catch(Exception e){
			System.out.println("erreur"+e);
		}
	}
	private void run()throws IOException{
		int port = 8080;
		int callIndex = -1;
		int currentWorker = 0;
		boolean isConfigValid = true;
		ArrayList<String> declaredServers = new ArrayList<>();
		ArrayList<String> workersServers = new ArrayList<>();
		int nbServers;
		
		for(Entry<String, String> entry : workermap.entrySet()) {
		    String key = entry.getKey();
		    if(key.contains("ip")){
		    	declaredServers.add(key.substring(0,key.indexOf(".")));
		    }
		}
		String[] tempServers = lbmap.get("0.workers").split(",");
		for (String aServer : tempServers) {
			workersServers.add(aServer);
		}
		nbServers = workersServers.size();
		
		System.out.println(declaredServers);
		System.out.println(workersServers);
		boolean findIt = false;
		UNVALID: for (String aDeclaredServer : declaredServers) {
			for (String aWorkerServer : workersServers) {
				if(aDeclaredServer.equals(aWorkerServer))
					findIt = true;
			}
			if(!findIt){
				isConfigValid = false;
				break UNVALID;
			}
		}
		System.out.println(isConfigValid);
		
		ServerSocket serveur = new ServerSocket(port);
		try{
			while(true){
				Socket socket = serveur.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
				String inputLine = in.readLine();
				String route = inputLine.split(" ")[1];
				inputLine = in.readLine();
				String address = inputLine;
				try{
					PerformRequest pr = new PerformRequest(route,address,port,isConfigValid);
					String display = pr.getDisplay();
					OutputStreamWriter outWriter;
					PrintWriter out =
	                        new PrintWriter(socket.getOutputStream(), true);
	                out.println(display);
					if(!display.contains("Erreur")){
						if(!route.equals("/favicon.ico")){
							++callIndex;
							int workersIndex = callIndex % nbServers;
							currentWorker = Integer.parseInt(workersServers.get(workersIndex));
						}
						//Implémenter ici le serveur range a interoger
		                out.println("Ma ferme contient "+nbServers+" serveurs");
		                out.println("Requette numéro : "+callIndex);
		                out.println("Serveur a atteindre : "+currentWorker);
		                out.println("Ip du serveur a ateindre : "+workermap.get(""+currentWorker+".ip")+" sur le port "+workermap.get(""+currentWorker+".port"));
		                
					}

				}finally{
					socket.close();
				}
			}
		}finally{
			serveur.close();
		}
	}
}
